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

package net.sourceforge.openutils.mgnlmedia.media.advancedsearch;

/**
 * @author cstrappazzon
 * @version $Id$
 */
public class Option
{

    private String label;

    private String value;

    private Boolean defaultValue;

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
     * Returns the value.
     * @return the value
     */
    public String getValue()
    {
        return value;
    }

    /**
     * Sets the value.
     * @param value the value to set
     */
    public void setValue(String value)
    {
        this.value = value;
    }

    /**
     * Returns the defaultValue.
     * @return the defaultValue
     */
    public Boolean getDefaultValue()
    {
        return defaultValue;
    }

    /**
     * Sets the defaultValue.
     * @param defaultValue the defaultValue to set
     */
    public void setDefaultValue(Boolean defaultValue)
    {
        this.defaultValue = defaultValue;
    }

}
