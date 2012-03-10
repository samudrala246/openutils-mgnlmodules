/**
 *
 * SimpleMedia Module for Magnolia CMS (http://www.openmindlab.com/lab/products/media.html)
 * Copyright(C) 2008-2012, Openmind S.r.l. http://www.openmindonline.it
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

package net.sourceforge.openutils.mgnlmedia.media.configuration;

import info.magnolia.cms.beans.config.ObservedManager;
import info.magnolia.cms.beans.config.URI2RepositoryManager;
import info.magnolia.cms.beans.config.URI2RepositoryMapping;
import info.magnolia.cms.core.Content;
import info.magnolia.cms.core.ItemType;
import info.magnolia.cms.core.search.Query;
import info.magnolia.cms.core.search.QueryManager;
import info.magnolia.cms.core.search.QueryResult;
import info.magnolia.cms.util.ContentUtil;
import info.magnolia.cms.util.FactoryUtil;
import info.magnolia.cms.util.NodeDataUtil;
import info.magnolia.content2bean.Content2BeanUtil;
import info.magnolia.context.MgnlContext;
import info.magnolia.repository.RepositoryConstants;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.RepositoryException;
import javax.jcr.query.InvalidQueryException;

import net.sourceforge.openutils.mgnlmedia.media.lifecycle.MediaModule;

import org.apache.commons.lang.StringUtils;
import org.apache.jackrabbit.util.ISO9075;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * ObservedManager that keeps that media types configuration.<br/>
 * Each media type is defined under the mediatypes contentnode in /modules/media.<br/>
 * I.e.<br/>
 * /modules/media/mediatypes/pdf<br/>
 * <ul>
 * <li>label = pdf file</li>
 * <li>handler = info.acme.PdfTypeHandler</li>
 * <li>menuIcon = .resources/pdf/icons/pdf16.gif</li>
 * <li>extensions = pdf</li>
 * </ul>
 * @author molaschi
 */
public class MediaConfigurationManager extends ObservedManager
{

    private static final String MGNL_MEDIA_TYPE = "mgnl:media";

    private static final String MGNL_RESOLUTION_TYPE = "mgnl:resolutions";

    /**
     * Folder type
     */
    public static final ItemType FOLDER = ItemType.CONTENT;

    /**
     * Media type
     */
    public static final ItemType MEDIA = new ItemType(MGNL_MEDIA_TYPE);

    /**
     * Resolutions node type
     */
    public static final ItemType RESOLUTIONS = new ItemType(MGNL_RESOLUTION_TYPE);

    private Logger log = LoggerFactory.getLogger(MediaConfigurationManager.class);

    private Map<String, MediaTypeConfiguration> types = new LinkedHashMap<String, MediaTypeConfiguration>();

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onClear()
    {
        types.clear();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    protected void onRegister(Content node)
    {
        for (Iterator iter = ContentUtil.getAllChildren(node).iterator(); iter.hasNext();)
        {
            Content typeNode = (Content) iter.next();

            if (!NodeDataUtil.getBoolean(typeNode, "enabled", true))
            {
                continue;
            }

            try
            {
                MediaTypeConfiguration conf = (MediaTypeConfiguration) Content2BeanUtil.toBean(
                    typeNode,
                    true,
                    MediaTypeConfiguration.class);

                if (conf.getHandler() != null)
                {
                    conf.getHandler().init(typeNode);
                }
                else
                {
                    log.error("Missing handler for media type {}", typeNode.getName());
                    continue;
                }

                types.put(typeNode.getName(), conf);
            }
            catch (Throwable e)
            {
                log.error("Error getting media type configuration for {}", typeNode.getHandle(), e);
            }
        }
    }

    /**
     * Get singleton instance
     * @return singleton instance
     */
    public static MediaConfigurationManager getInstance()
    {
        return (MediaConfigurationManager) FactoryUtil.getSingleton(MediaConfigurationManager.class);
    }

    /**
     * Get the media type from a file extension
     * @param extension file extension
     * @return media type
     */
    public static MediaTypeConfiguration getMediaHandlerFromExtension(String extension)
    {
        if (extension == null)
        {
            return null;
        }

        String lowerCasedExtension = extension.toLowerCase();
        for (Map.Entry<String, MediaTypeConfiguration> entry : MediaConfigurationManager
            .getInstance()
            .getTypes()
            .entrySet())
        {

            MediaTypeConfiguration value = entry.getValue();

            List<String> extensionsList = value.getExtensionsList();

            if (extensionsList != null && extensionsList.contains(lowerCasedExtension))
            {
                return entry.getValue();
            }
        }

        return null;
    }

    /**
     * Get all media type map
     * @return media type map
     */
    public Map<String, MediaTypeConfiguration> getTypes()
    {
        return types;
    }

    /**
     * Get the list of web pages where a media is used
     * @param uuid uuid of media
     * @return list of web pages where a media is used
     * @throws InvalidQueryException invalid query
     * @throws RepositoryException repository exception
     */
    public List<String> getUsedInWebPages(String uuid) throws InvalidQueryException, RepositoryException
    {
        return MediaUsedInManager.getInstance().getUsedInWorkspacePaths(uuid, RepositoryConstants.WEBSITE);
    }

    /**
     * Search media
     * @param text text to search
     * @param type if specified restricts the search to the type
     * @return found medias
     * @throws RepositoryException exception working on repository
     * @deprecated use SearchMediaQueryConfiguration.search(...)
     */
    @Deprecated
    public Collection<Content> search(String text, final String type) throws RepositoryException
    {
        return find(null, type, text, true);
    }

    /**
     * @param path
     * @param type
     * @param search
     * @param recursive
     * @return
     * @throws RepositoryException
     * @deprecated use the find method overload based on Magnolia Criteria API
     */
    @Deprecated
    public Collection<Content> find(String path, String type, String search, boolean recursive)
        throws RepositoryException
    {
        QueryManager qm = MgnlContext.getQueryManager(MediaModule.REPO);
        StringBuffer sbQuery = new StringBuffer("/jcr:root/");
        path = StringUtils.removeEnd(StringUtils.removeStart(StringUtils.trimToEmpty(path), "/"), "/");
        if (StringUtils.isNotEmpty(path))
        {
            sbQuery.append(ISO9075.encodePath(path)).append('/');
        }
        if (recursive)
        {
            sbQuery.append('/');
        }
        sbQuery.append("element(*," + MediaConfigurationManager.MEDIA.getSystemName() + ")");
        List<String> clauses = new ArrayList<String>();
        if (StringUtils.isNotBlank(search))
        {
            clauses.add("jcr:contains(.,'" + StringUtils.replace(search, "'", "''") + "')");
        }
        if (StringUtils.isNotBlank(type))
        {
            clauses.add("@type='" + type + "'");
        }
        if (!clauses.isEmpty())
        {
            sbQuery.append('[').append(StringUtils.join(clauses, " and ")).append(']');
        }
        if (StringUtils.isNotBlank(search))
        {
            sbQuery.append(" order by @jcr:score descending");
        }
        Query q = qm.createQuery(new String(sbQuery), Query.XPATH);
        QueryResult qr = q.execute();
        return qr.getContent(MediaConfigurationManager.MGNL_MEDIA_TYPE);
    }

    /**
     * Get the type configuration for a media
     * @param media media
     * @return type configuration
     */
    public MediaTypeConfiguration getMediaTypeConfigurationFromMedia(Content media)
    {
        try
        {
            if (!media.getItemType().equals(MEDIA))
            {
                return null;
            }
        }
        catch (RepositoryException e)
        {
            log.error("Error getting item type on node {} module media", media.getHandle(), e);
            return null;
        }

        return types.get(NodeDataUtil.getString(media, "type"));
    }

    /**
     * Get uri mapping for repo
     * @return uri mapping for repo
     */
    public String getURIMappingPrefix()
    {
        Collection<URI2RepositoryMapping> mappings = URI2RepositoryManager.getInstance().getMappings();
        for (URI2RepositoryMapping mapping : mappings)
        {
            if (mapping.getRepository().equals(MediaModule.REPO))
            {
                return mapping.getURIPrefix();
            }
        }
        return StringUtils.EMPTY;
    }
}
