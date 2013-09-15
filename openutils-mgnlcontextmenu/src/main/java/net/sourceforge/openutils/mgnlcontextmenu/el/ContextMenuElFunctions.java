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
import info.magnolia.jcr.util.NodeUtil;
import info.magnolia.jcr.wrapper.HTMLEscapingNodeWrapper;
import info.magnolia.objectfactory.Components;

import javax.jcr.Node;

import net.sourceforge.openutils.mgnlcontextmenu.configuration.PersistenceStrategy;
import net.sourceforge.openutils.mgnlcontextmenu.module.ContextMenuModule;
import net.sourceforge.openutils.mgnlcontextmenu.tags.MenuScripts;

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

    /**
     * Gets the entry value for the specified name, searching for it in the local and global entries corresponding to
     * the given node. Local entries take precedence on the global ones.
     * @param node
     * @param name
     * @return a local contents entry value if found, or a global contents one if found, otherwise null
     */
    public static String entryValue(Node node, String name)
    {
        ContextMenuModule module = Components.getComponent(ContextMenuModule.class);
        PersistenceStrategy strategy = module.getPersistenceStrategy();
        // LB crazy command! mgnl argsss
        Node nodeUnwrapped = NodeUtil.deepUnwrap(node, HTMLEscapingNodeWrapper.class);
        return strategy != null ? strategy.readEntry(nodeUnwrapped, name) : null;
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
                + "/.resources/contextmenu/css/contextmenu.css\" media=\"screen\" />\n");
            out.append("<script src=\"" + ctx + "/.resources/contextmenu/js/mgnladmin-custom.js\"></script>\n");
            out.append("<!-- end contextmenu:links -->\n");
        }

        return out.toString();
    }

}
