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

package net.sourceforge.openutils.mgnlcontextmenu.configuration;

/**
 * JavaBean storing the definition of an item in a context menu.
 * @author dschivo
 * @version $Id$
 */
public class ContextMenuItem
{

    private String name;

    private String controlType;

    private String icon;

    private String text;

    private boolean globalEnabled;

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
     * Returns the controlType.
     * @return the controlType
     */
    public String getControlType()
    {
        return controlType;
    }

    /**
     * Sets the controlType.
     * @param controlType the controlType to set
     */
    public void setControlType(String controlType)
    {
        this.controlType = controlType;
    }

    /**
     * Returns the icon.
     * @return the icon
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
     * Returns the text.
     * @return the text
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
     * Returns the globalEnabled.
     * @return the globalEnabled
     */
    public boolean isGlobalEnabled()
    {
        return globalEnabled;
    }

    /**
     * Sets the globalEnabled.
     * @param globalEnabled the globalEnabled to set
     */
    public void setGlobalEnabled(boolean globalEnabled)
    {
        this.globalEnabled = globalEnabled;
    }
}
