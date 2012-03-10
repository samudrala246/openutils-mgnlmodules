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

package net.sourceforge.openutils.mgnlmedia.playlist.pages;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.openutils.mgnlmedia.media.pages.MessagesTemplatedMVCHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author dschivo
 */
public class PlaylistsBrowserPage extends MessagesTemplatedMVCHandler
{

    /**
     * Logger.
     */
    private Logger log = LoggerFactory.getLogger(PlaylistsBrowserPage.class);

    private String openPath;

    /**
     * 
     */
    public PlaylistsBrowserPage(String name, HttpServletRequest request, HttpServletResponse response)
    {
        super(name, request, response);
    }

    /**
     * Returns the openPath.
     * @return the openPath
     */
    public String getOpenPath()
    {
        return openPath;
    }

    /**
     * Sets the openPath.
     * @param openPath the openPath to set
     */
    public void setOpenPath(String openPath)
    {
        this.openPath = openPath;
    }
}
