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

package net.sourceforge.openutils.mgnlmedia.media.pages;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * @author molaschi
 * @version $Id$
 */
public class MediaFolderSelectionPage extends MessagesTemplatedMVCHandler
{

    private String cacheKiller = String.valueOf((new Date()).getTime());

    private String parentFrame;

    private String sourceNode;

    private String action;

    /**
     * @param name
     * @param request
     * @param response
     */
    public MediaFolderSelectionPage(String name, HttpServletRequest request, HttpServletResponse response)
    {
        super(name, request, response);
    }

    /**
     * Returns the cacheKiller.
     * @return the cacheKiller
     */
    public String getCacheKiller()
    {
        return cacheKiller;
    }

    /**
     * Sets the cacheKiller.
     * @param cacheKiller the cacheKiller to set
     */
    public void setCacheKiller(String cacheKiller)
    {
        this.cacheKiller = cacheKiller;
    }

    /**
     * Returns the parentFrame.
     * @return the parentFrame
     */
    public String getParentFrame()
    {
        return parentFrame;
    }

    /**
     * Sets the parentFrame.
     * @param parentFrame the parentFrame to set
     */
    public void setParentFrame(String parentFrame)
    {
        this.parentFrame = parentFrame;
    }

    /**
     * Returns the sourceNode.
     * @return the sourceNode
     */
    public String getSourceNode()
    {
        return sourceNode;
    }

    /**
     * Sets the sourceNode.
     * @param sourceNode the sourceNode to set
     */
    public void setSourceNode(String sourceNode)
    {
        this.sourceNode = sourceNode;
    }

    /**
     * Returns the action.
     * @return the action
     */
    public String getAction()
    {
        return action;
    }

    /**
     * Sets the action.
     * @param action the action to set
     */
    public void setAction(String action)
    {
        this.action = action;
    }

}
