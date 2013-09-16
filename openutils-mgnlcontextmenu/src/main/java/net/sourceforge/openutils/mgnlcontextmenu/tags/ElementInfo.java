/**
 *
 * ContextMenu Module for Magnolia CMS (http://www.openmindlab.com/lab/products/mgnlcontextmenu.html)
 * Copyright(C) 2010-2013, Openmind S.r.l. http://www.openmindonline.it
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
     * Message key
     */
    private final String key;

    /**
     * Entry name
     */
    private String entryName;

    /**
     * Paragraph's handle
     */
    private final String path;

    /**
     * HTML identifier of the element containing the message
     */
    private final String elementId;

    /**
     * Wrapper HTML tag to which attach context menu
     */
    private String parentTrigger;

    /**
     * Context menu type
     */
    private String contextMenu;

    /**
     * Enter mode for FCK editor
     */
    private String enterMode;

    /**
     * JavaScript function to call when context menu opens
     */
    private String showCallback;

    public ElementInfo(String key, String path, String elementId)
    {
        this.key = key;
        this.path = path;
        this.elementId = elementId;
    }

    public String getKey()
    {
        return key;
    }

    public String getPath()
    {
        return path;
    }

    public String getElementId()
    {
        return elementId;
    }

    public String getParentTrigger()
    {
        return parentTrigger;
    }

    public void setParentTrigger(String parentTrigger)
    {
        this.parentTrigger = parentTrigger;
    }

    public String getContextMenu()
    {
        return contextMenu;
    }

    public void setContextMenu(String contextMenu)
    {
        this.contextMenu = contextMenu;
    }

    public String getEnterMode()
    {
        return enterMode;
    }

    public void setEnterMode(String enterMode)
    {
        this.enterMode = enterMode;
    }

    public String getEntryName()
    {
        return entryName;
    }

    public void setEntryName(String entryName)
    {
        this.entryName = entryName;
    }

    public String getShowCallback()
    {
        return showCallback;
    }

    public void setShowCallback(String showCallback)
    {
        this.showCallback = showCallback;
    }
}
