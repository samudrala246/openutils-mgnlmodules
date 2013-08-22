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



/**
 * @author ADMIN
 * @version $Id: $
 */
public abstract class AbstractPlaylistLink implements PlaylistLink
{

    private String text;

    private String icon;

    private boolean external;

    /**
     * {@inheritDoc}
     */
    public String getText()
    {
        return text;
    }

    /**
     * Sets the text.
     * @param text the text to set
     */
    public void setText(String text)
    {
        this.text = text;
    }

    /**
     * {@inheritDoc}
     */
    public String getIcon()
    {
        return icon;
    }

    /**
     * Sets the icon.
     * @param icon the icon to set
     */
    public void setIcon(String icon)
    {
        this.icon = icon;
    }

    /**
     * {@inheritDoc}
     */
    public abstract String url(PlaylistBean playlist);

    /**
     * {@inheritDoc}
     */
    public boolean isExternal()
    {
        return external;
    }

    /**
     * Sets the external.
     * @param external the external to set
     */
    public void setExternal(boolean external)
    {
        this.external = external;
    }

}
