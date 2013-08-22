/**
 *
 * SimpleMedia Module for Magnolia CMS (http://www.openmindlab.com/lab/products/media.html)
 * Copyright(C) 2008-2013, Openmind S.r.l. http://www.openmindonline.it
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

package net.sourceforge.openutils.mgnlmedia.media.pages;

import info.magnolia.cms.core.Content;
import info.magnolia.context.MgnlContext;
import info.magnolia.freemarker.FreemarkerUtil;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.jcr.RepositoryException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.openutils.mgnlcriteria.jcr.query.AdvancedResult;
import net.sourceforge.openutils.mgnlcriteria.jcr.query.AdvancedResultItem;
import net.sourceforge.openutils.mgnlmedia.media.advancedsearch.configuration.SearchMediaQueryConfiguration;
import net.sourceforge.openutils.mgnlmedia.media.configuration.MediaTypeConfiguration;
import net.sourceforge.openutils.mgnlmedia.media.lifecycle.MediaModule;
import net.sourceforge.openutils.mgnlmedia.media.tags.el.MediaEl;
import net.sourceforge.openutils.mgnlmedia.playlist.pages.PlaylistView;

import org.apache.commons.lang.StringUtils;

import com.google.common.base.Function;
import com.google.common.collect.Iterators;


/**
 * Page that renders the search results.
 * @author molaschi
 * @version $Id$
 */
public class MediaAdvancedSearchPage extends MediaFolderViewPage
{

    private static final String VIEW_RESULTS_XML = "-xml";

    private String format;

    private String selectTab;

    /**
     * @param name
     * @param request
     * @param response
     */
    public MediaAdvancedSearchPage(String name, HttpServletRequest request, HttpServletResponse response)
    {
        super(name, request, response);
    }

    @Override
    protected String getTemplateName(String viewName)
    {

        if (VIEW_SHOW.equals(viewName))
        {
            return FreemarkerUtil.createTemplateName(MediaFolderViewPage.class, "html");
        }
        else if (VIEW_RESULTS_XML.equals(viewName))
        {
            return FreemarkerUtil.createTemplateName(PlaylistView.class, "-xspf", "html");
        }
        return super.getTemplateName(viewName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected int countMediaItems(MediaTypeConfiguration type)
    {
        if ("xml".equals(format))
        {
            // no need to count media per type on xml requests
            return 0;
        }
        return MediaEl
            .module()
            .getSearch()
            .search(request, type.getName(), "/", true, SortMode.SCORE, 0, 1)
            .getTotalSize();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected AdvancedResult findMediaItems()
    {
        // @todo defaultBasePath handling only on xml requests?
        SearchMediaQueryConfiguration searchConfig = MediaEl.module().getSearch();
        if ("xml".equals(format))
        {
            return searchConfig.search(
                request,
                null,
                null,
                true,
                SortMode.SCORE,
                searchConfig.getXmlItemsPerPage(),
                page);
        }
        return searchConfig.search(request, type, "/", true, SortMode.valueOf(sorting), MediaEl
            .module()
            .getFolderViewItemsPerPage(), page);
    }

    @Override
    public String show()
    {
        super.show();

        selectTab = StringUtils.trimToEmpty(selectTab);
        if (StringUtils.isNotBlank(selectTab))
        {
            setType(selectTab);
        }

        if (!StringUtils.isNotBlank(selectTab))
        {
            selectTab = getType();
        }

        if ("xml".equals(format))
        {
            this.response.setContentType("text/xml");
            return VIEW_RESULTS_XML;
        }
        return VIEW_SHOW;
    }

    /**
     * Returns the format.
     * @return the format
     */
    public String getFormat()
    {
        return format;
    }

    /**
     * Sets the format.
     * @param format the format to set
     */
    public void setFormat(String format)
    {
        this.format = format;
    }

    /**
     * Returns the selectTab.
     * @return the selectTab
     */
    public String getSelectTab()
    {
        return selectTab;
    }

    /**
     * Sets the selectTab.
     * @param selectTab the selectTab to set
     */
    public void setSelectTab(String selectTab)
    {
        this.selectTab = selectTab;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected SortMode getDefaultSorting()
    {
        return SortMode.SCORE;
    }

    public String playlistTitle()
    {
        return null;
    }

    public String playlistLocation()
    {
        return "/media/search?" + currentQueryString();
    }

    public Map<String, Object> playlistMetas()
    {
        Map<String, Object> metas = new LinkedHashMap<String, Object>();
        metas.put("page", searchResult.getPage());
        metas.put("itemsperpage", searchResult.getItemsPerPage());
        metas.put("totalitems", searchResult.getTotalSize());
        return metas;
    }

    public Iterator<Content> getMediaContentList()
    {
        return Iterators.transform(searchResult.getItems(), new Function<AdvancedResultItem, Content>()
        {

            /**
             * {@inheritDoc}
             */
            public Content apply(AdvancedResultItem from)
            {
                try
                {
                    return MgnlContext.getHierarchyManager(MediaModule.REPO).getContentByUUID(from.getUUID());
                }
                catch (RepositoryException e)
                {
                    return null;
                }
            }
        });
    }

    public String saveAsPlaylist()
    {
        return show();
    }
}
