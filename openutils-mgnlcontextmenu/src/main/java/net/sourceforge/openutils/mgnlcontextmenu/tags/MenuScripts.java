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

import info.magnolia.cms.security.Permission;
import info.magnolia.context.MgnlContext;
import info.magnolia.jcr.util.NodeUtil;

import java.util.List;
import java.util.Map;

import net.sourceforge.openutils.mgnlcontextmenu.configuration.ContextMenu;
import net.sourceforge.openutils.mgnlcontextmenu.configuration.ContextMenuItem;
import net.sourceforge.openutils.mgnlcontextmenu.configuration.ContextMenuManager;
import net.sourceforge.openutils.mgnlcontextmenu.el.ContextMenuElFunctions;

import org.apache.commons.lang.StringUtils;


/**
 * @author dschivo
 * @version $Id$
 */
public class MenuScripts
{

    public static String write()
    {
        if (canEdit())
        {
            StringBuilder out = new StringBuilder();

            out.append("<script type=\"text/javascript\">\n");
            out.append("var mgnlContextMenuInfo = {\n");
            out.append("  contextPath: '" + MgnlContext.getContextPath() + "',\n");
            out.append("  menus: " + menusJs() + ",\n");
            out.append("  elements: " + elementsJs() + "\n");
            out.append("};\n");
            out.append("</script>\n");

            out.append("<!-- start contextmenu:scripts -->\n");
            out.append("<script type=\"text/javascript\">\n");
            out.append("jQuery(document).ready(function() {\n");
            out.append("  jQuery.mgnlAddContextMenu("
                + ContextMenuElFunctions.editMessageInfosJs()
                + ", mgnlContextMenuInfo );\n");
            out.append("});\n");
            out.append("</script>\n");
            out.append("<!-- end contextmenu:scripts -->\n");

            out.append("<!-- start sortList script -->\n");
            out.append("<script type=\"text/javascript\">\n");
            out.append("jQuery(document).ready(function() {\n");
            List<Map<String, String>> items = (List<Map<String, String>>) MgnlContext
                .getWebContext()
                .getRequest()
                .getAttribute(ContextMenuElFunctions.SORT_LIST_KEY);
            if (items != null)
            {
                for (Map<String, String> item : items)
                {
                    out.append("jQuery('#" + item.get("containerId") + "').sortList({\n");
                    out.append(" url: '" + item.get("url") + "',\n");
                    out.append(" path: '" + item.get("path") + "',\n");
                    out.append(" name: '" + item.get("name") + "',\n");
                    out.append(" order: " + item.get("order") + "\n");
                    out.append("});\n");
                }
            }
            out.append("});\n");
            out.append("</script>\n");
            out.append("<!-- end sortList script -->\n");

            return out.toString();
        }
        return StringUtils.EMPTY;
    }

    private static String menusJs()
    {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (ContextMenu menu : ContextMenuManager.getInstance().getMenus())
        {
            if (i > 0)
            {
                sb.append(',');
            }
            sb.append("{");
            sb.append("'name':'").append(menu.getName()).append("',");
            sb.append("'items':[");
            int j = 0;
            for (ContextMenuItem item : menu.getItems())
            {
                if (j > 0)
                {
                    sb.append(',');
                }
                sb.append("{");
                sb.append("'name':'").append(item.getName()).append("'");
                sb.append(",'controlType':'").append(item.getControlType()).append("'");
                sb.append(",'icon':'").append(item.getIcon()).append("'");
                sb.append(",'text':'").append(item.getText()).append("'");
                sb.append(",'globalEnabled':").append(item.isGlobalEnabled());
                sb.append("}");
                j++;
            }
            sb.append("],");
            sb.append("'mouseoverClass':'").append(StringUtils.defaultString(menu.getMouseoverClass())).append("',");
            sb.append("'mouseoverIcon':'").append(StringUtils.defaultString(menu.getMouseoverIcon())).append("'");
            sb.append("}");
            i++;
        }
        return "[" + sb + "]";
    }

    @SuppressWarnings("unchecked")
    private static String elementsJs()
    {
        StringBuilder sb = new StringBuilder();
        List infos = (List) MgnlContext.getWebContext().getRequest().getAttribute(ElementTag.ELEMENT_INFOS_KEY);
        if (infos != null)
        {
            for (Object item : infos)
            {
                if (sb.length() > 0)
                {
                    sb.append(',');
                }
                ElementInfo info = (ElementInfo) item;
                sb.append("'").append(info.getElementId()).append("'");
                sb.append(":{");

                if (!StringUtils.isEmpty(info.getContextMenu()))
                {
                    sb.append("'contextMenu':'").append(info.getContextMenu()).append("',");
                }
                if (!StringUtils.isEmpty(info.getParentTrigger()))
                {
                    sb.append("'parentTrigger':'").append(info.getParentTrigger()).append("',");
                }
                if (!StringUtils.isEmpty(info.getEnterMode()))
                {
                    sb.append("'enterMode':'").append(info.getEnterMode()).append("',");
                }
                sb.append("'path':'").append(info.getPath()).append("'");
                sb.append('}');
            }
        }
        return "{" + sb + "}";
    }

    @SuppressWarnings("deprecation")
    private static boolean canEdit()
    {
        return NodeUtil.isGranted(MgnlContext.getAggregationState().getMainContent().getJCRNode(), Permission.SET);
    }

}
