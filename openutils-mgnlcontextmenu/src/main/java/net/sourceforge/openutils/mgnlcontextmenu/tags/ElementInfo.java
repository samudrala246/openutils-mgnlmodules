/**
 *
 * ContextMenu Module for Magnolia CMS (http://www.openmindlab.com/lab/products/mgnlcontextmenu.html)
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

package net.sourceforge.openutils.mgnlcontextmenu.tags;

/**
 * JavaBean storing informations on an element having a context menu. Instances are created by the "element" tag, and
 * finally collected by the "script" tag.
 * @author dschivo
 * @version $Id$
 */
public class ElementInfo
{

    /**
     * Paragraph's handle
     */
    private final String path;

    /**
     * HTML identifier of the element containing the content
     */
    private final String elementId;

    /**
     * Entry name
     */
    private String entryName;

    /**
     * Context menu name
     */
    private String contextMenu;

    /**
     * Wrapper HTML tag to which attach context menu
     */
    private String parentTrigger;

    /**
     * Enter mode for FCK editor
     */
    private String enterMode;

    /**
     * JavaScript function to call when context menu opens
     */
    private String showCallback;

    public ElementInfo(String path, String elementId)
    {
        this.path = path;
        this.elementId = elementId;
    }

    /**
     * Returns the path.
     * @return the path
     */
    public String getPath()
    {
        return path;
    }

    /**
     * Returns the elementId.
     * @return the elementId
     */
    public String getElementId()
    {
        return elementId;
    }

    /**
     * Returns the entryName.
     * @return the entryName
     */
    public String getEntryName()
    {
        return entryName;
    }

    /**
     * Sets the entryName.
     * @param entryName the entryName to set
     */
    public void setEntryName(String entryName)
    {
        this.entryName = entryName;
    }

    /**
     * Returns the contextMenu.
     * @return the contextMenu
     */
    public String getContextMenu()
    {
        return contextMenu;
    }

    /**
     * Sets the contextMenu.
     * @param contextMenu the contextMenu to set
     */
    public void setContextMenu(String contextMenu)
    {
        this.contextMenu = contextMenu;
    }

    /**
     * Returns the parentTrigger.
     * @return the parentTrigger
     */
    public String getParentTrigger()
    {
        return parentTrigger;
    }

    /**
     * Sets the parentTrigger.
     * @param parentTrigger the parentTrigger to set
     */
    public void setParentTrigger(String parentTrigger)
    {
        this.parentTrigger = parentTrigger;
    }

    /**
     * Returns the enterMode.
     * @return the enterMode
     */
    public String getEnterMode()
    {
        return enterMode;
    }

    /**
     * Sets the enterMode.
     * @param enterMode the enterMode to set
     */
    public void setEnterMode(String enterMode)
    {
        this.enterMode = enterMode;
    }

    /**
     * Returns the showCallback.
     * @return the showCallback
     */
    public String getShowCallback()
    {
        return showCallback;
    }

    /**
     * Sets the showCallback.
     * @param showCallback the showCallback to set
     */
    public void setShowCallback(String showCallback)
    {
        this.showCallback = showCallback;
    }
}
