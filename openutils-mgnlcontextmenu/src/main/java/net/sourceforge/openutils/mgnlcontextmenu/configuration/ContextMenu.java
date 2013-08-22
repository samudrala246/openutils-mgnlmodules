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

package net.sourceforge.openutils.mgnlcontextmenu.configuration;

import java.util.ArrayList;
import java.util.List;


/**
 * JavaBean storing the definition of a context menu. A context menu can be defined by adding a child node to the
 * contextMenus node in the configuration workspace.
 * @author dschivo
 * @version $Id$
 */
public class ContextMenu
{

    private String name;

    private List<ContextMenuItem> items = new ArrayList<ContextMenuItem>();

    private String mouseoverClass;

    private String mouseoverIcon;

    /**
     * Returns the name.
     * @return the name
     */
    public String getName()
    {
        return name;
    }

    /**
     * Sets the name.
     * @param name the name to set
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Returns the items.
     * @return the items
     */
    public List<ContextMenuItem> getItems()
    {
        return items;
    }

    /**
     * Returns the mouseoverClass.
     * @return the mouseoverClass
     */
    public String getMouseoverClass()
    {
        return mouseoverClass;
    }

    /**
     * Sets the mouseoverClass.
     * @param mouseoverClass the mouseoverClass to set
     */
    public void setMouseoverClass(String mouseoverClass)
    {
        this.mouseoverClass = mouseoverClass;
    }

    /**
     * Returns the mouseoverIcon.
     * @return the mouseoverIcon
     */
    public String getMouseoverIcon()
    {
        return mouseoverIcon;
    }

    /**
     * Sets the mouseoverIcon.
     * @param mouseoverIcon the mouseoverIcon to set
     */
    public void setMouseoverIcon(String mouseoverIcon)
    {
        this.mouseoverIcon = mouseoverIcon;
    }

}
