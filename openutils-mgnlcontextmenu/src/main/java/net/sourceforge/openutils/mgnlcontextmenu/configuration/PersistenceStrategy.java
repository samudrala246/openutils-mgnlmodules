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

package net.sourceforge.openutils.mgnlcontextmenu.configuration;

import info.magnolia.objectfactory.Components;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import net.sourceforge.openutils.mgnlcontextmenu.module.ContextMenuModule;

import org.apache.commons.lang.StringUtils;


/**
 * @author dschivo
 * @version $Id$
 */
public abstract class PersistenceStrategy
{

    /**
     * Reads the entry corresponding to the specified name, searching first in the local entries of the given node and
     * then in the global ones.
     * @param node
     * @param name
     * @return the entry value
     */
    public String readEntry(Node node, String name)
    {
        String value = readEntry(node, name, Scope.local);
        if (StringUtils.isEmpty(value))
        {
            value = readEntry(node, name, Scope.global);
        }
        return value;
    }

    /**
     * Reads an entry in the given scope.
     * @param node
     * @param name
     * @param scope
     * @return the entry value
     */
    public abstract String readEntry(Node node, String name, Scope scope);

    /**
     * Writes an entry in the given scope.
     * @param node
     * @param name
     * @param value
     * @param scope
     */
    public abstract void writeEntry(Node node, String name, String value, Scope scope) throws RepositoryException;

    /**
     * Gets the global entries node for the given node, using the strategy configured in the contextmenu module.
     * @param node
     * @return
     */
    protected Node getGlobalNode(Node node)
    {
        ContextMenuModule module = Components.getComponent(ContextMenuModule.class);
        GetGlobalEntriesNodeStrategy strategy = module.getGetGlobalEntriesNodeStrategy();
        return strategy != null ? strategy.getGlobalEntriesNode(node) : null;
    }

}
