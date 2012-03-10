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

package net.sourceforge.openutils.mgnlmedia.media.advancedsearch.configuration;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sourceforge.openutils.mgnlcriteria.jcr.query.AdvancedResult;
import net.sourceforge.openutils.mgnlcriteria.jcr.query.Criteria;
import net.sourceforge.openutils.mgnlcriteria.jcr.query.JCRCriteriaFactory;
import net.sourceforge.openutils.mgnlcriteria.jcr.query.criterion.Criterion;
import net.sourceforge.openutils.mgnlcriteria.jcr.query.criterion.Order;
import net.sourceforge.openutils.mgnlcriteria.jcr.query.criterion.Restrictions;
import net.sourceforge.openutils.mgnlmedia.media.advancedsearch.SearchFilter;
import net.sourceforge.openutils.mgnlmedia.media.configuration.MediaConfigurationManager;
import net.sourceforge.openutils.mgnlmedia.media.lifecycle.MediaModule;
import net.sourceforge.openutils.mgnlmedia.media.pages.SortMode;
import net.sourceforge.openutils.mgnlmedia.media.types.MediaTypeHandler;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author cstrappazzon
 * @version $Id$
 */
public class SearchMediaQueryConfiguration
{

    /**
     * Logger.
     */
    private Logger log = LoggerFactory.getLogger(SearchMediaQueryConfiguration.class);

    private Map<String, SearchFilter> filters = new LinkedHashMap<String, SearchFilter>();

    private String defaultBasePath;

    private int xmlItemsPerPage;

    /**
     * Returns the filters.
     * @return the filters
     */
    public Map<String, SearchFilter> getFilters()
    {
        return filters;
    }

    public void addFilter(String name, SearchFilter filter)
    {
        filters.put(name, filter);
    }

    /**
     * Sets the defaultBasePath.
     * @param defaultBasePath the defaultBasePath to set
     */
    public void setDefaultBasePath(String defaultBasePath)
    {
        this.defaultBasePath = defaultBasePath;
    }

    /**
     * Returns the xmlItemsPerPage.
     * @return the xmlItemsPerPage
     */
    public int getXmlItemsPerPage()
    {
        return xmlItemsPerPage;
    }

    /**
     * Sets the xmlItemsPerPage.
     * @param xmlItemsPerPage the xmlItemsPerPage to set
     */
    public void setXmlItemsPerPage(int xmlItemsPerPage)
    {
        this.xmlItemsPerPage = xmlItemsPerPage;
    }

    public AdvancedResult search(HttpServletRequest request, String mediaType, String path, boolean descendants,
        SortMode sorting, int itemsPerPage, int pageNumberStartingFromOne)
    {
        Criteria c = JCRCriteriaFactory
            .createCriteria()
            .setWorkspace(MediaModule.REPO)
            .add(Restrictions.eq("@jcr:primaryType", MediaConfigurationManager.MEDIA.getSystemName()));

        // media type
        if (!StringUtils.isEmpty(mediaType))
        {
            c.add(Restrictions.eq("@type", mediaType));
        }

        path = StringUtils.defaultString(path, defaultBasePath);

        // filters
        for (String key : filters.keySet())
        {
            if ("type".equals(key) && !StringUtils.isEmpty(mediaType))
            {
                // restriction on type already set
                continue;
            }
            SearchFilter filter = filters.get(key);
            path = StringUtils.defaultIfEmpty(filter.getBasePath(key, request), path);
            for (Criterion criterion : filter.getCriterionList(key, request))
            {
                c.add(criterion);
            }
            SortMode s = filter.getSorting(key, request);
            if (s != null)
            {
                sorting = s;
            }
        }

        // base path
        String searchPath = StringUtils.removeEnd(path, "/") + "/" + (descendants ? "/" : StringUtils.EMPTY) + "*";
        c.setBasePath("/jcr:root/" + StringUtils.removeStart(searchPath, "/"));

        // sorting
        if (sorting == SortMode.SCORE)
        {
            c.addOrder(Order.desc("@jcr:score"));
        }
        else if (sorting == SortMode.CREATIONDATE_ASC)
        {
            c.addOrder(Order.asc("@jcr:created"));
        }
        else if (sorting == SortMode.CREATIONDATE_DESC)
        {
            c.addOrder(Order.desc("@jcr:created"));
        }
        else if (sorting == SortMode.FILENAME_ASC)
        {
            c.addOrder(Order.asc("@" + MediaTypeHandler.METADATA_NAME));
        }
        else if (sorting == SortMode.FILENAME_DESC)
        {
            c.addOrder(Order.desc("@" + MediaTypeHandler.METADATA_NAME));
        }

        // paging
        if (itemsPerPage > 0)
        {
            c.setPaging(itemsPerPage, pageNumberStartingFromOne);
        }

        AdvancedResult result = c.execute();
        if (log.isDebugEnabled())
        {
            log.debug("Executing {} -> {} results", c.toXpathExpression(), result.getTotalSize());
        }

        return result;
    }

}
