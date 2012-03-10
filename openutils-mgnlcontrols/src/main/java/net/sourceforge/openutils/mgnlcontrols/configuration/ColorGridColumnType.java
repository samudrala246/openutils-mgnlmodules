/**
 *
 * Controls module for Magnolia CMS (http://www.openmindlab.com/lab/products/controls.html)
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

package net.sourceforge.openutils.mgnlcontrols.configuration;

import info.magnolia.cms.i18n.Messages;
import info.magnolia.context.MgnlContext;

import java.util.Map;


/**
 * A column of colors.
 * @author dschivo
 * @version $Id$
 */
public class ColorGridColumnType extends AbstractGridColumnType
{

    /**
     * {@inheritDoc}
     */
    @Override
    public String getHeadSnippet()
    {
        return "<script type=\"text/javascript\" src=\""
            + MgnlContext.getContextPath()
            + "/.resources/controls/js/ColorField.js\"></script>";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void addColumnData(Map<String, String> column, String propertyName, int colIndex, Map colMap,
        Messages msgs)
    {
        column.put("editor", "new Ed(new Ext.ux.ColorField({}))");
        column
            .put(
                "renderer",
                "function(v, p, record){ return v ? '<em style=\"display: block; float: left; border: 1px solid #ACA899;\">"
                    + "<span style=\"display: block; width: 10px; height: 10px; line-height: 10px; background-color: ' + v + ';\">"
                    + "</span></em>' : ''; }");
    }
}
