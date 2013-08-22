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

import info.magnolia.cms.core.Content;
import info.magnolia.module.ModuleRegistry;
import net.sourceforge.openutils.mgnlcontextmenu.configuration.PersistenceStrategy;
import net.sourceforge.openutils.mgnlcontextmenu.module.ContextMenuModule;

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
    public static String entryValue(Content node, String name)
    {
        ContextMenuModule module = ModuleRegistry.Factory.getInstance().getModuleInstance(ContextMenuModule.class);
        PersistenceStrategy strategy = module.getPersistenceStrategy();
        return strategy != null ? strategy.readEntry(node, name) : null;
    }

}
