/**
 *
 * E-learning Module for Magnolia CMS (http://www.openmindlab.com/lab/products/lms.html)
 * Copyright(C) 2010-2011, Openmind S.r.l. http://www.openmindonline.it
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

package net.sourceforge.openutils.mgnllms.lms.configuration;

import net.sourceforge.openutils.mgnllms.lms.types.LmsTypeHandler;


/**
 * Lms type configuration
 * @author molaschi
 */
public class LmsTypeConfiguration
{

    private String name;

    private String label;

    private String menuIcon;

    private String dialog;

    private LmsTypeHandler handler;

    /**
     * Returns the label.
     * @return the label
     */
    public String getLabel()
    {
        return label;
    }

    /**
     * Sets the label.
     * @param label the label to set
     */
    public void setLabel(String label)
    {
        this.label = label;
    }

    /**
     * Returns the handler.
     * @return the handler
     */
    public LmsTypeHandler getHandler()
    {
        return handler;
    }

    /**
     * Sets the handler.
     * @param handler the handler to set
     */
    public void setHandler(LmsTypeHandler handler)
    {
        this.handler = handler;
    }

    /**
     * Returns the menuIcon.
     * @return the menuIcon
     */
    public String getMenuIcon()
    {
        return menuIcon;
    }

    /**
     * Sets the menuIcon.
     * @param menuIcon the menuIcon to set
     */
    public void setMenuIcon(String menuIcon)
    {
        this.menuIcon = menuIcon;
    }

    /**
     * Returns the dialog.
     * @return the dialog
     */
    public String getDialog()
    {
        return dialog;
    }

    /**
     * Sets the dialog.
     * @param dialog the dialog to set
     */
    public void setDialog(String dialog)
    {
        this.dialog = dialog;
    }

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

}
