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

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.openutils.mgnlmedia.media.advancedsearch.SearchFilter;
import net.sourceforge.openutils.mgnlmedia.media.tags.el.MediaEl;


/**
 * Page that renders the search form.
 * @author molaschi
 * @version $Id$
 */
public class MediaAdvancedSearchFormPage extends MessagesTemplatedMVCHandler
{

    private boolean selectMedia;

    private Map<String, SearchFilter> filters;

    private String playlistHandle;

    /**
     * Returns the selectMedia.
     * @return the selectMedia
     */
    public boolean isSelectMedia()
    {
        return selectMedia;
    }

    /**
     * Sets the selectMedia.
     * @param selectMedia the selectMedia to set
     */
    public void setSelectMedia(boolean selectMedia)
    {
        this.selectMedia = selectMedia;
    }

    /**
     * @param name
     * @param request
     * @param response
     */
    public MediaAdvancedSearchFormPage(String name, HttpServletRequest request, HttpServletResponse response)
    {
        super(name, request, response);
        filters = MediaEl.module().getSearch().getFilters();
    }

    /**
     * Returns the filters.
     * @return the filters
     */
    public Map<String, SearchFilter> getFilters()
    {
        return filters;
    }

    /**
     * Returns the playlistHandle.
     * @return the playlistHandle
     */
    public String getPlaylistHandle()
    {
        return playlistHandle;
    }

    /**
     * Sets the playlistHandle.
     * @param playlistHandle the playlistHandle to set
     */
    public void setPlaylistHandle(String playlistHandle)
    {
        this.playlistHandle = playlistHandle;
    }
}
