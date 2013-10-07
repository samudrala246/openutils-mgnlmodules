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

package net.sourceforge.openutils.mgnlcontextmenu.el;

import info.magnolia.cms.security.Permission;
import info.magnolia.context.MgnlContext;
import info.magnolia.context.WebContext;
import info.magnolia.jcr.util.NodeUtil;
import info.magnolia.jcr.util.PropertyUtil;
import info.magnolia.jcr.wrapper.HTMLEscapingNodeWrapper;
import info.magnolia.objectfactory.Components;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import javax.jcr.Node;
import javax.servlet.http.HttpServletRequest;

import net.sourceforge.openutils.mgnlcontextmenu.configuration.PersistenceStrategy;
import net.sourceforge.openutils.mgnlcontextmenu.module.ContextMenuModule;
import net.sourceforge.openutils.mgnlcontextmenu.tags.ElementInfo;
import net.sourceforge.openutils.mgnlcontextmenu.tags.MenuScripts;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author dschivo
 */
public class ContextMenuElFunctions
{

    /**
     * Logger.
     */
    private static Logger log = LoggerFactory.getLogger(ContextMenuElFunctions.class);

    public static final String SORT_LIST_KEY = "mgnlSortLists";

    private static final String EDIT_MESSAGE_INFOS_KEY = "mgnlEditMessageInfos";

    /**
     * A shortcut to get the current request.
     * @return The request.
     */
    private static HttpServletRequest getRequest()
    {
        return ((WebContext) MgnlContext.getInstance()).getRequest();
    }

    /**
     * Gets the entry value for the specified name, searching for it in the local and global entries corresponding to
     * the given node. Local entries take precedence on the global ones.
     * @param node
     * @param name
     * @return a local contents entry value if found, or a global contents one if found, otherwise null
     */
    public static String entryValue(Node node, String name)
    {
        if (node == null)
        {
            return null;
        }

        ContextMenuModule module = Components.getComponent(ContextMenuModule.class);
        PersistenceStrategy strategy = module.getPersistenceStrategy();
        Node nodeUnwrapped = NodeUtil.deepUnwrap(node, HTMLEscapingNodeWrapper.class);

        String result = strategy != null ? strategy.readEntry(nodeUnwrapped, name) : null;

        return result;
    }

    public static String scripts()
    {
        return MenuScripts.write();
    }

    public static String links()
    {
        String ctx = MgnlContext.getContextPath();
        boolean canEdit = NodeUtil.isGranted(
            MgnlContext.getAggregationState().getMainContent().getJCRNode(),
            Permission.SET);

        StringBuilder out = new StringBuilder();

        if (canEdit)
        {
            out.append("<!-- start contextmenu:links -->\n");
            out.append("<link rel=\"stylesheet\" type=\"text/css\" href=\""
                + ctx
                + "/.resources/contextmenu/css/jquery.contextMenu.css\" media=\"screen\" />\n");
            // out.append("<link rel=\"stylesheet\" type=\"text/css\" href=\""
            // + ctx
            // + "/.resources/contextmenu/css/contextmenu.css\" media=\"screen\" />\n");
            // out.append("<script src=\"" + ctx + "/.resources/contextmenu/js/contextmenu-jquery.js\"></script>\n");
            out.append("<script src=\"" + ctx + "/.resources/contextmenu/js/mgnladmin-custom.js\"></script>\n");
            out.append("<script src=\"" + ctx + "/.resources/contextmenu/js/jquery.contextMenu.js\"></script>\n");
            out.append("<script src=\"" + ctx + "/.resources/contextmenu/js/contextmenu-addMenu.js\"></script>\n");
            // if(mgnlSortLists != null){
            out.append("<script src=\"" + ctx + "/.resources/contextmenu/js/contextmenu-sortList.js\"></script>\n");
            // }
            out.append("<!-- end contextmenu:links -->\n");
        }

        return out.toString();
    }

    @SuppressWarnings({"rawtypes" })
    private static Stack getSortListStack()
    {
        HttpServletRequest request = getRequest();
        Stack stack = (Stack) request.getAttribute("SORT_LIST_STACK");
        if (stack == null)
        {
            stack = new Stack();
            request.setAttribute("SORT_LIST_STACK", stack);
        }
        return stack;
    }

    @SuppressWarnings({"unchecked", "rawtypes" })
    public static void beginSortList()
    {
        getSortListStack().push(new ArrayList());
    }

    @SuppressWarnings({"unchecked", "rawtypes" })
    public static void addSortListItem(String html)
    {
        ((List) getSortListStack().peek()).add(html);
    }

    @SuppressWarnings({"unchecked", "rawtypes" })
    public static List endSortList(String order)
    {
        String[] tokens = StringUtils.splitPreserveAllTokens(order, ',');

        List input = (List) getSortListStack().pop();
        List output = new ArrayList(input.size());

        int i = 0;
        Iterator iter = input.iterator();
        while (iter.hasNext())
        {
            String html = (String) iter.next();
            int index = i < tokens.length ? NumberUtils.toInt(tokens[i], -1) : -1;
            output.add(index >= 0 && index < input.size() ? input.get(index) : html);
            i++;
        }
        return output;
    }

    @SuppressWarnings({"rawtypes" })
    public static void init()
    {
        HttpServletRequest request = getRequest();
        List list = (List) request.getAttribute(EDIT_MESSAGE_INFOS_KEY);
        if (list == null)
        {
            list = new ArrayList();
            request.setAttribute(EDIT_MESSAGE_INFOS_KEY, list);
        }
        List items = (List) request.getAttribute(SORT_LIST_KEY);
        if (items == null)
        {
            items = new ArrayList();
            request.setAttribute(SORT_LIST_KEY, items);
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes" })
    public static ElementInfo addEditMessageInfo(String key, String path, String elementId)
    {
        HttpServletRequest request = getRequest();
        List list = (List) request.getAttribute(EDIT_MESSAGE_INFOS_KEY);
        if (list == null)
        {
            list = new ArrayList();
            request.setAttribute(EDIT_MESSAGE_INFOS_KEY, list);
        }
        ElementInfo item = new ElementInfo(key, path, elementId);
        list.add(item);

        return item;
    }

    @SuppressWarnings({"rawtypes" })
    public static List editMessageInfos()
    {
        List list = (List) getRequest().getAttribute(EDIT_MESSAGE_INFOS_KEY);
        return list != null ? list : Collections.EMPTY_LIST;
    }

    public static String editMessageInfosJs()
    {
        StringBuilder sb = new StringBuilder();
        for (Object item : editMessageInfos())
        {
            if (sb.length() > 0)
            {
                sb.append(',');
            }
            ElementInfo info = (ElementInfo) item;
            sb.append("'").append(info.getElementId()).append("'");
            sb.append(":{");
            if (!StringUtils.isEmpty(info.getParentTrigger()))
            {
                sb.append("'parentTrigger':'").append(info.getParentTrigger()).append("',");
            }
            if (!StringUtils.isEmpty(info.getEntryName()))
            {
                sb.append("'entryName':'").append(info.getEntryName()).append("',");
            }
            if (!StringUtils.isEmpty(info.getContextMenu()))
            {
                sb.append("'contextMenu':'").append(info.getContextMenu()).append("',");
            }
            if (!StringUtils.isEmpty(info.getEnterMode()))
            {
                sb.append("'enterMode':'").append(info.getEnterMode()).append("',");
            }
            if (!StringUtils.isEmpty(info.getShowCallback()))
            {
                sb.append("'showCallback':").append(info.getShowCallback()).append(',');
            }
            sb.append("'key':'").append(info.getKey()).append("',");
            sb.append("'path':'").append(info.getPath()).append("'");
            sb.append('}');
        }
        return "{" + sb + "}";
    }

}
