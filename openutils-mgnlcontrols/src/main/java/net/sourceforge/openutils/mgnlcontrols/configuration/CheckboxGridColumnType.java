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

import info.magnolia.cms.core.Content;
import info.magnolia.cms.i18n.Messages;
import info.magnolia.cms.security.AccessDeniedException;
import info.magnolia.context.MgnlContext;
import info.magnolia.module.ModuleRegistry;

import java.util.Map;

import javax.jcr.RepositoryException;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;


/**
 * A column of checkboxes.
 * @author dschivo
 * @version $Id$
 */
public class CheckboxGridColumnType implements GridColumnType
{

    /**
     * {@inheritDoc}
     */
    public String getHeadSnippet()
    {
        return "<script type=\"text/javascript\" src=\""
            + MgnlContext.getContextPath()
            + "/.resources/controls/"
            + ModuleRegistry.Factory.getInstance().getDefinition("controls").getVersion()
            + "/js/CheckColumn.js\"></script>";
    }

    /**
     * {@inheritDoc}
     */
    public String drawSupportHtml(String propertyName, int colIndex, Map colmap, Messages msgs)
    {
        return StringUtils.EMPTY;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public String drawColumnJs(String propertyName, int colIndex, Map colMap, Messages msgs)
    {
        String header = (String) colMap.get("header");
        return "(function(){\n"
            + "var cc = new Ext.grid.CheckColumn({"
            + "header: '"
            + StringEscapeUtils.escapeJavaScript(msgs.getWithDefault(header, header))
            + "',"
            + "dataIndex: '"
            + colIndex
            + "',"
            + "width: 40"
            + "});\n"
            + "plugins.push(cc);"
            + "return cc;\n"
            + "})()";
    }

    /**
     * {@inheritDoc}
     */
    public void processColumnOnLoad(String[] column, Content colConfig, String propertyName, Content storageNode)
    {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     */
    public void processColumnOnSave(String[] column, Content colConfig, String propertyName, Content parentNode)
        throws RepositoryException, AccessDeniedException
    {
    }
}
