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

package net.sourceforge.openutils.mgnlmedia.playlist.pages;

import java.util.List;


/**
 * @author dschivo
 */
public class PlaylistBean
{

    private String uuid;

    private String handle;

    private String title;

    private String description;

    private List<PlaylistEntryBean> entries;

    private boolean searchBased;

    /**
     * Returns the uuid.
     * @return the uuid
     */
    public String getUuid()
    {
        return uuid;
    }

    /**
     * Sets the uuid.
     * @param uuid the uuid to set
     */
    public void setUuid(String uuid)
    {
        this.uuid = uuid;
    }

    /**
     * Returns the handle.
     * @return the handle
     */
    public String getHandle()
    {
        return handle;
    }

    /**
     * Sets the handle.
     * @param handle the handle to set
     */
    public void setHandle(String handle)
    {
        this.handle = handle;
    }

    /**
     * Returns the title.
     * @return the title
     */
    public String getTitle()
    {
        return title;
    }

    /**
     * Sets the title.
     * @param title the title to set
     */
    public void setTitle(String title)
    {
        this.title = title;
    }

    /**
     * Returns the description.
     * @return the description
     */
    public String getDescription()
    {
        return description;
    }

    /**
     * Sets the description.
     * @param description the description to set
     */
    public void setDescription(String description)
    {
        this.description = description;
    }

    /**
     * Returns the entries.
     * @return the entries
     */
    public List<PlaylistEntryBean> getEntries()
    {
        return entries;
    }

    /**
     * Sets the entries.
     * @param entries the entries to set
     */
    public void setEntries(List<PlaylistEntryBean> entries)
    {
        this.entries = entries;
    }

    /**
     * Returns the searchBased.
     * @return the searchBased
     */
    public boolean isSearchBased()
    {
        return searchBased;
    }

    /**
     * Sets the searchBased.
     * @param searchBased the searchBased to set
     */
    public void setSearchBased(boolean searchBased)
    {
        this.searchBased = searchBased;
    }

}
