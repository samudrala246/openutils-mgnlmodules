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

import info.magnolia.cms.core.Content;


/**
 * Abstracts the logic for mapping a node to the one storing the global contents. Tipically the global contents node is
 * the homepage of the website containing the specified paragraph/page. An implementation of this interface should be
 * set in the configuration of the contextmenu module.
 * @author dschivo
 * @version $Id$
 */
public interface GetGlobalEntriesNodeStrategy
{

    /**
     * Gets the node storing the global contents for the given node. Tipically the node to be returned is the homepage
     * of the website containing the specified page or paragraph.
     * @param node
     * @return
     */
    Content getGlobalEntriesNode(Content node);
}
