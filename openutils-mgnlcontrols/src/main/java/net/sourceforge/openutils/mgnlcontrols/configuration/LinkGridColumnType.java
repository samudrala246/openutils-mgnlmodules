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
import info.magnolia.module.ModuleRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;


/**
 * A column of links: values can be edited selecting an internal link from standard magnolia dialog.
 * @author dschivo
 * @version $Id$
 */
public class LinkGridColumnType extends AbstractGridColumnType
{

    /**
     * {@inheritDoc}
     */
    @Override
    public String getHeadSnippet()
    {
        return "<script type=\"text/javascript\" src=\""
            + MgnlContext.getContextPath()
            + "/.resources/controls/"
            + ModuleRegistry.Factory.getInstance().getDefinition("controls").getVersion()
            + "/js/LinkField.js\"></script>";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void addColumnData(Map<String, String> column, String propertyName, int colIndex, Map colMap,
        Messages msgs)
    {
        List<String> options = new ArrayList<String>();
        if (!StringUtils.isEmpty((String) colMap.get("repository")))
        {
            options.add("repository: '" + colMap.get("repository") + "'");
        }
        if (!StringUtils.isEmpty((String) colMap.get("extension")))
        {
            options.add("extension: '" + colMap.get("extension") + "'");
        }
        column.put("editor", "new Ed(new LinkField({" + StringUtils.join(options, ",") + "}))");
    }
}
