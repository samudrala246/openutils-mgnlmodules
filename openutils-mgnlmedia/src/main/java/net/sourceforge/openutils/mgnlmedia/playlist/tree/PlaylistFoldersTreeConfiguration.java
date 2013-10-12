/**
 *
 * SimpleMedia Module for Magnolia CMS (http://www.openmindlab.com/lab/products/media.html)
 * Copyright(C) 2008-2013, Openmind S.r.l. http://www.openmindonline.it
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

package net.sourceforge.openutils.mgnlmedia.playlist.tree;

import info.magnolia.cms.gui.control.FunctionBarItem;
import info.magnolia.cms.gui.control.Tree;
import info.magnolia.cms.gui.control.TreeColumn;
import info.magnolia.cms.i18n.Messages;
import info.magnolia.module.admininterface.AbstractTreeConfiguration;

import javax.servlet.http.HttpServletRequest;

import net.sourceforge.openutils.mgnlmedia.playlist.PlaylistConstants;


/**
 * @author ADMIN
 * @version $Id: $
 */
public class PlaylistFoldersTreeConfiguration extends AbstractTreeConfiguration
{

    /**
     * {@inheritDoc}
     */
    public void prepareContextMenu(Tree tree, boolean browseMode, HttpServletRequest request)
    {
        tree.addSeparator();
    }

    /**
     * {@inheritDoc}
     */
    public void prepareFunctionBar(Tree tree, boolean browseMode, HttpServletRequest request)
    {
        tree.addFunctionBarItem(FunctionBarItem.getRefreshFunctionBarItem(tree, getMessages(), request));
    }

    /**
     * {@inheritDoc}
     */
    public void prepareTree(Tree tree, boolean browseMode, HttpServletRequest request)
    {
        final Messages msgs = getMessages();

        tree.addItemType(PlaylistConstants.NT_FOLDER, "/.resources/media/icons/ico16-folder.png");

        TreeColumn column0 = TreeColumn.createLabelColumn(tree, msgs.get("tree.playlists.column"), true);
        column0.setWidth(3);
        tree.addColumn(column0);
    }
}
