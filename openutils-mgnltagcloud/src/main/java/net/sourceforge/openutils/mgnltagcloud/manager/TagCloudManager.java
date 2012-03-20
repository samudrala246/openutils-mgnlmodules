/**
 *
 * Tagcloud module for Magnolia CMS (http://www.openmindlab.com/lab/products/mgnltagcloud.html)
 * Copyright(C) 2010-2012, Openmind S.r.l. http://www.openmindonline.it
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.sourceforge.openutils.mgnltagcloud.manager;

import info.magnolia.cms.beans.config.ObservedManager;
import info.magnolia.cms.core.Content;
import info.magnolia.cms.util.ContentUtil;
import info.magnolia.cms.util.FactoryUtil;
import info.magnolia.cms.util.ObservationUtil;
import info.magnolia.content2bean.Content2BeanException;
import info.magnolia.content2bean.Content2BeanUtil;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Singleton;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.observation.EventListener;

import net.sourceforge.openutils.mgnltagcloud.bean.TagCloud;
import net.sourceforge.openutils.mgnltagcloud.util.JackrabbitUtil;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;
import org.apache.jackrabbit.core.query.lucene.NamespaceMappings;
import org.apache.jackrabbit.core.query.lucene.SearchIndex;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.Query;

import com.browseengine.bobo.api.BoboBrowser;
import com.browseengine.bobo.api.BoboIndexReader;
import com.browseengine.bobo.api.BrowseException;
import com.browseengine.bobo.api.BrowseFacet;
import com.browseengine.bobo.api.BrowseRequest;
import com.browseengine.bobo.api.BrowseResult;
import com.browseengine.bobo.api.FacetAccessible;
import com.browseengine.bobo.api.FacetSpec;
import com.browseengine.bobo.api.FacetSpec.FacetSortSpec;
import com.browseengine.bobo.facets.FacetHandler;
import com.browseengine.bobo.facets.impl.MultiValueFacetHandler;


/**
 * @author molaschi
 * @version $Id$
 */
@Singleton
public class TagCloudManager extends ObservedManager
{

    /**
     * tag clouds configured in jcr config repository
     */
    private Map<String, TagCloud> tagClouds = new HashMap<String, TagCloud>();

    /**
     * tag clouds generated at runtime
     */
    private Map<String, TagCloud> runtimeTagClouds = new HashMap<String, TagCloud>();

    /**
     * Repository listeners
     */
    private Map<String, EventListener> repositoryListeners = new HashMap<String, EventListener>();

    /**
     * @return tagcloud manager singleton instance
     */
    public static TagCloudManager getInstance()
    {
        return (TagCloudManager) FactoryUtil.getSingleton(TagCloudManager.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onClear()
    {
        tagClouds = new HashMap<String, TagCloud>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onRegister(Content node)
    {
        List<Content> allChildren = ContentUtil.collectAllChildren(node);
        for (Content tagCloudNode : allChildren)
        {
            try
            {
                TagCloud tagCloud = (TagCloud) Content2BeanUtil.toBean(tagCloudNode, TagCloud.class);
                if (!tagCloud.isEnabled())
                {
                    continue;
                }
                tagClouds.put(tagCloudNode.getName(), tagCloud);
                calculateTagCloud(tagCloud);
                if (tagCloud.isCacheAndObserve() && !repositoryListeners.containsKey(tagCloud.getRepository()))
                {
                    EventListener el = new TagCloudRepositoryObserver(tagCloud.getRepository());
                    ObservationUtil.registerChangeListener(tagCloud.getRepository(), "/", el);
                    repositoryListeners.put(tagCloud.getRepository(), el);
                }
            }
            catch (Content2BeanException e)
            {
                log.warn("Error converting node {} to TagCloud class", tagCloudNode.getHandle(), e);
            }
        }
    }

    /**
     * Unregister all jcr event listeners
     */
    public void stopObserving()
    {
        for (Map.Entry<String, EventListener> entry : repositoryListeners.entrySet())
        {
            ObservationUtil.unregisterChangeListener(entry.getKey(), entry.getValue());
        }
    }

    /**
     * Get tag clouds for a repository
     * @param repository repository
     * @return tag clouds map for a repository
     */
    @SuppressWarnings("unchecked")
    public Map<String, TagCloud> getTagClouds(final String repository)
    {
        Map<String, TagCloud> allTagClouds = new HashMap<String, TagCloud>(tagClouds);
        allTagClouds.putAll(runtimeTagClouds);
        return MapUtils.predicatedMap(allTagClouds, null, new Predicate()
        {

            public boolean evaluate(Object object)
            {
                return ((TagCloud) object).isCacheAndObserve()
                    && repository.equals(((TagCloud) object).getRepository());
            }
        });
    }

    /**
     * calculate tag cloud and store it in tagCloud.tags
     * @param tagCloud tag cloud to calculate
     */
    public void calculateTagCloud(TagCloud tagCloud)
    {
        IndexReader ir = null;
        try
        {
            // get index reader
            Session session = JackrabbitUtil.getSession(tagCloud.getRepository());
            SearchIndex si = JackrabbitUtil.getSearchIndex(tagCloud.getRepository(), session);
            ir = si.getIndexReader();

            NamespaceMappings namespaceMappings = si.getNamespaceMappings();
            String propertyName = namespaceMappings.getPrefix(StringUtils.EMPTY)
                + ":FULL:"
                + tagCloud.getPropertyName();

            // configure bobo for faceted search
            MultiValueFacetHandler tagsHandler = new MultiValueFacetHandler(propertyName);
            List<FacetHandler> handlerList = Arrays.asList(new FacetHandler[]{tagsHandler });
            BoboIndexReader boboReader = HierarchyBoboIndexReader.getInstance(ir, handlerList);

            // get query
            Query q = boboReader.getFastMatchAllDocsQuery();
            if (StringUtils.isNotBlank(tagCloud.getPath()) && !"/".equals(tagCloud.getPath()))
            {
                q = JackrabbitUtil.getQuery(tagCloud.getPath(), session, si);
            }

            // build request for bobo
            BrowseRequest br = new BrowseRequest();
            br.setCount(0); // we are not interested in lucene hits
            br.setOffset(0);
            br.setQuery(q);

            // add the facet output specs
            FacetSpec tagsSpec = new FacetSpec();
            tagsSpec.setOrderBy(FacetSortSpec.OrderHitsDesc);
            tagsSpec.setMaxCount(tagCloud.getCount());

            br.setFacetSpec(propertyName, tagsSpec);

            BoboBrowser browser = new BoboBrowser(boboReader);

            // perform browse
            BrowseResult result = browser.browse(br);

            // get tags
            Map<String, FacetAccessible> facetMap = result.getFacetMap();

            FacetAccessible tagsFacets = facetMap.get(propertyName);
            List<BrowseFacet> tagsVals = tagsFacets.getFacets();

            // store them in tagCloud itself
            tagCloud.setTags(new HashMap<String, Integer>());
            for (BrowseFacet bf : tagsVals)
            {
                tagCloud.getTags().put(bf.getValue(), bf.getHitCount());
            }

        }
        catch (RepositoryException ex)
        {
            log.error("Error retriving SearchIndex", ex);
        }
        catch (BrowseException ex)
        {
            log.error("Error making query", ex);
        }
        catch (IOException ex)
        {
            log.error("Error wrapping indexes", ex);
        }
        finally
        {
            if (ir != null)
            {
                try
                {
                    ir.close();
                }
                catch (IOException e)
                {
                    log.error("Error closing index reader", e);
                }
            }
        }
    }

    public TagCloud getTagCloud(String name)
    {
        return tagClouds.get(name);
    }

    public TagCloud checkForTagCloud(final TagCloud tagCloud)
    {
        TagCloud found = (TagCloud) CollectionUtils.find(tagClouds.values(), new Predicate()
        {

            public boolean evaluate(Object object)
            {
                return tagCloud.equals(object);
            }
        });
        if (found == null)
        {
            found = runtimeTagClouds.get(tagCloud.getName());
            if (found == null)
            {
                calculateTagCloud(tagCloud);
                runtimeTagClouds.put(tagCloud.getName(), tagCloud);
                found = tagCloud;
                if (!repositoryListeners.containsKey(tagCloud.getRepository()))
                {
                    EventListener el = new TagCloudRepositoryObserver(tagCloud.getRepository());
                    ObservationUtil.registerChangeListener(tagCloud.getRepository(), "/", el);
                    repositoryListeners.put(tagCloud.getRepository(), el);
                }
            }
        }
        return found;
    }

}
