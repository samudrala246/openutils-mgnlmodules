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
 * A column whose values are selectable from a combo box.
 * @author dschivo
 * @version $Id$
 */
public class ComboGridColumnType extends AbstractGridColumnType
{

    /**
     * {@inheritDoc}
     */
    @Override
    public String getHeadSnippet()
    {
        StringBuilder result = new StringBuilder(super.getHeadSnippet());
        result.append("<script type=\"text/javascript\" src=\"");
        result.append(MgnlContext.getContextPath());
            result.append("/.resources/controls/"
                + ModuleRegistry.Factory.getInstance().getDefinition("controls").getVersion()
                + "/js/PipeComboBox.js\"></script>");
        // Combobox must be patched because of a bug. For reference, see:
        // http://www.sencha.com/forum/showthread.php?17465-1.1.1-Local-ComboBox-data-store-filter-not-cleared-on-call-to-setValue%28%29
        result.append("<script type=\"text/javascript\">");
        result.append("var PatchedComboBox = Ext.extend(Ext.form.ComboBox, {");
        result.append("  setValue: function(v) {");
        result.append("    this.store.clearFilter();");
        result.append("    PatchedComboBox.superclass.setValue.call(this, v);");
        result.append("  }");
        result.append("});\n");
        result.append("</script>");
        return result.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String drawSupportHtml(String propertyName, int colIndex, Map colmap, Messages msgs)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("<select id=\"combo-"
            + propertyName
            + "\" name=\"combo-"
            + propertyName
            + "\" style=\"display: none;\">");
        Map optionMap = (Map) colmap.get("options");
        for (Object optionValue : optionMap.values())
        {
            Map option = (Map) optionValue;
            sb.append("<option value=\"" + option.get("value") + "\">" + option.get("label") + "</option>");
        }
        sb.append("</select>");
        return new String(sb);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void addColumnData(Map<String, String> column, String propertyName, int colIndex, Map colMap,
        Messages msgs)
    {
        List<String> options = new ArrayList<String>();
        options.add("typeAhead: true");
        options.add("triggerAction: 'all'");
        options.add("transform: 'combo-" + propertyName + "'");
        options.add("lazyRender: true");
        options.add("listClass: 'x-combo-list-small'");

        if ("true".equals(String.valueOf(colMap.get("showLabel"))))
        {
            column.put(
                "editor",
                "(function() { window.gridComboColumnTypeTmp = new "
                    + ("true".equals(String.valueOf(colMap.get("pipe"))) ? "PipeComboBox" : "PatchedComboBox")
                    + "({"
                    + StringUtils.join(options, ",")
                    + "}); return new Ed(gridComboColumnTypeTmp); })()");
            column.put("renderer", "(function(combo){"
                + "    return function(value){"
                + "        var record = combo.findRecord(combo.valueField, value);"
                + "        return record ? record.get(combo.displayField) : combo.valueNotFoundText;"
                + "    }"
                + "})(window.gridComboColumnTypeTmp)");
        }
        else
        {
            column.put("editor", "new Ed(new "
                + ("true".equals(String.valueOf(colMap.get("pipe"))) ? "PipeComboBox" : "PatchedComboBox")
                + "({"
                + StringUtils.join(options, ",")
                + "}))");
        }
    }
}
