/**
 *
 * SimpleMedia Module for Magnolia CMS (http://www.openmindlab.com/lab/products/media.html)
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

package net.sourceforge.openutils.mgnlmedia.media.tree;

import info.magnolia.cms.beans.config.ServerConfiguration;
import info.magnolia.cms.exchange.ActivationManagerFactory;
import info.magnolia.cms.gui.control.ContextMenuItem;
import info.magnolia.cms.gui.control.FunctionBarItem;
import info.magnolia.cms.gui.control.Tree;
import info.magnolia.cms.gui.control.TreeColumn;
import info.magnolia.cms.i18n.Messages;
import info.magnolia.module.admininterface.AbstractTreeConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sourceforge.openutils.mgnlmedia.media.configuration.MediaConfigurationManager;
import net.sourceforge.openutils.mgnlmedia.media.configuration.MediaTypeConfiguration;
import net.sourceforge.openutils.mgnlmedia.media.tags.el.MediaEl;

import org.apache.commons.lang.StringUtils;


/**
 * Custom Tree configuration for media folders browsing
 * @author molaschi
 * @version $Id$
 */
public class MediaModuleTreeConfiguration extends AbstractTreeConfiguration
{

    /**
     * {@inheritDoc}
     */
    public void prepareContextMenu(Tree tree, boolean browseMode, HttpServletRequest request)
    {
        final Messages msgs = getMessages();

        ContextMenuItem menuNewPage = new ContextMenuItem("newFolder");
        menuNewPage.setLabel(msgs.get("tree.config.menu.newFolder")); //$NON-NLS-1$
        menuNewPage.setIcon(request.getContextPath() + "/.resources/media/icons/ico16-folder.png"); //$NON-NLS-1$
        menuNewPage.setOnclick(tree.getJavascriptTree()
            + ".createNode('" + MediaConfigurationManager.FOLDER.getSystemName() + "');"); //$NON-NLS-1$ //$NON-NLS-2$
        menuNewPage.addJavascriptCondition("new mgnlTreeMenuItemConditionSelectedNotNodeData(" //$NON-NLS-1$
            + tree.getJavascriptTree()
            + ")"); //$NON-NLS-1$
        menuNewPage.addJavascriptCondition("new mgnlTreeMenuItemConditionSelectedNotContentNode(" //$NON-NLS-1$
            + tree.getJavascriptTree()
            + ")"); //$NON-NLS-1$
        menuNewPage.addJavascriptCondition("new mgnlTreeMenuItemConditionPermissionWrite("
            + tree.getJavascriptTree()
            + ")");

        List<ContextMenuItem> menusNewMedia = new ArrayList<ContextMenuItem>();

        for (Map.Entry<String, MediaTypeConfiguration> mtc : MediaConfigurationManager
            .getInstance()
            .getTypes()
            .entrySet())
        {
            ContextMenuItem menuItem = new ContextMenuItem("newMedia-" + mtc.getKey());
            menuItem.setLabel(msgs.get("media.types." + mtc.getValue().getName() + ".load"));
            menuItem.setIcon(request.getContextPath() + mtc.getValue().getMenuIcon());
            menuItem.setOnclick("mgnl.media.createNew("
                + tree.getJavascriptTree()
                + ".selectedNode.id, '"
                + mtc.getValue().getDialog()
                + "');");
            menuItem.addJavascriptCondition("new mgnlTreeMenuItemConditionSelectedNotContentNode("
                + tree.getJavascriptTree()
                + ")");
            menuItem.addJavascriptCondition("new mgnlTreeMenuItemConditionSelectedNotRoot("
                + tree.getJavascriptTree()
                + ")");
            menuItem.addJavascriptCondition("new mgnlTreeMenuItemConditionPermissionWrite(" //$NON-NLS-1$
                + tree.getJavascriptTree()
                + ")"); //$NON-NLS-1$
            menusNewMedia.add(menuItem);
        }

        ContextMenuItem menuDelete = new ContextMenuItem("delete");
        menuDelete.setLabel(msgs.get("tree.config.menu.delete")); //$NON-NLS-1$
        menuDelete.setIcon(request.getContextPath() + "/.resources/icons/16/delete2.gif"); //$NON-NLS-1$
        menuDelete.setOnclick(tree.getJavascriptTree() + ".deleteNode();"); //$NON-NLS-1$
        menuDelete.addJavascriptCondition("new mgnlTreeMenuItemConditionSelectedNotRoot(" //$NON-NLS-1$
            + tree.getJavascriptTree()
            + ")"); //$NON-NLS-1$
        menuDelete.addJavascriptCondition("new mgnlTreeMenuItemConditionPermissionWrite("
            + tree.getJavascriptTree()
            + ")");

        ContextMenuItem menuCopy = new ContextMenuItem("copy");
        menuCopy.setLabel(msgs.get("tree.config.menu.copy")); //$NON-NLS-1$
        menuCopy.setIcon(request.getContextPath() + "/.resources/icons/16/copy.gif"); //$NON-NLS-1$
        menuCopy.addJavascriptCondition("new mgnlTreeMenuItemConditionSelectedNotRoot(" //$NON-NLS-1$
            + tree.getJavascriptTree()
            + ")"); //$NON-NLS-1$
        menuCopy.addJavascriptCondition("new mgnlTreeMenuItemConditionSelectedNotNodeData(" //$NON-NLS-1$
            + tree.getJavascriptTree()
            + ")"); //$NON-NLS-1$
        menuCopy.setOnclick(tree.getJavascriptTree() + ".copyNode();"); //$NON-NLS-1$

        ContextMenuItem menuCut = new ContextMenuItem("move");
        menuCut.setLabel(msgs.get("tree.config.menu.move")); //$NON-NLS-1$
        menuCut.setIcon(request.getContextPath() + "/.resources/icons/16/up_down.gif"); //$NON-NLS-1$
        menuCut
            .addJavascriptCondition("new mgnlTreeMenuItemConditionSelectedNotRoot(" + tree.getJavascriptTree() + ")"); //$NON-NLS-1$ //$NON-NLS-2$
        menuCut.addJavascriptCondition("new mgnlTreeMenuItemConditionSelectedNotNodeData(" //$NON-NLS-1$
            + tree.getJavascriptTree()
            + ")"); //$NON-NLS-1$
        menuCut.setOnclick(tree.getJavascriptTree() + ".cutNode();"); //$NON-NLS-1$
        menuCut
            .addJavascriptCondition("new mgnlTreeMenuItemConditionPermissionWrite(" + tree.getJavascriptTree() + ")");

        ContextMenuItem menuActivateExcl = new ContextMenuItem("activate");
        menuActivateExcl.setLabel(msgs.get("tree.config.menu.activate")); //$NON-NLS-1$
        menuActivateExcl.setIcon(request.getContextPath() + "/.resources/icons/16/arrow_right_green.gif"); //$NON-NLS-1$
        menuActivateExcl.setOnclick(tree.getJavascriptTree() + ".activateNode(" + Tree.ACTION_ACTIVATE + ",false);"); //$NON-NLS-1$ //$NON-NLS-2$
        menuActivateExcl.addJavascriptCondition("new mgnlTreeMenuItemConditionSelectedNotRoot(" //$NON-NLS-1$
            + tree.getJavascriptTree()
            + ")"); //$NON-NLS-1$
        menuActivateExcl.addJavascriptCondition("new mgnlTreeMenuItemConditionSelectedNotNodeData(" //$NON-NLS-1$
            + tree.getJavascriptTree()
            + ")"); //$NON-NLS-1$
        menuActivateExcl.addJavascriptCondition("new mgnlTreeMenuItemConditionPermissionWrite("
            + tree.getJavascriptTree()
            + ")");

        ContextMenuItem menuActivate = new ContextMenuItem("activateInclSubs");
        menuActivate.setLabel(msgs.get("tree.config.menu.activateInclSubs")); //$NON-NLS-1$
        menuActivate.setIcon(request.getContextPath() + "/.resources/icons/16/arrow_right_green.gif"); //$NON-NLS-1$
        menuActivate.setOnclick(tree.getJavascriptTree() + ".activateNode(" + Tree.ACTION_ACTIVATE + ",true);"); //$NON-NLS-1$ //$NON-NLS-2$
        menuActivate.addJavascriptCondition("new mgnlTreeMenuItemConditionSelectedNotRoot(" //$NON-NLS-1$
            + tree.getJavascriptTree()
            + ")"); //$NON-NLS-1$
        menuActivate.addJavascriptCondition("new mgnlTreeMenuItemConditionSelectedNotNodeData(" //$NON-NLS-1$
            + tree.getJavascriptTree()
            + ")"); //$NON-NLS-1$
        menuActivate.addJavascriptCondition("new mgnlTreeMenuItemConditionPermissionWrite("
            + tree.getJavascriptTree()
            + ")");

        ContextMenuItem menuDeactivate = new ContextMenuItem("deactivate");
        menuDeactivate.setLabel(msgs.get("tree.config.menu.deactivate")); //$NON-NLS-1$
        menuDeactivate.setIcon(request.getContextPath() + "/.resources/icons/16/arrow_left_red.gif"); //$NON-NLS-1$
        menuDeactivate.setOnclick(tree.getJavascriptTree() + ".deactivateNode(" + Tree.ACTION_DEACTIVATE + ");"); //$NON-NLS-1$ //$NON-NLS-2$
        menuDeactivate.addJavascriptCondition("new mgnlTreeMenuItemConditionSelectedNotRoot(" //$NON-NLS-1$
            + tree.getJavascriptTree()
            + ")"); //$NON-NLS-1$
        menuDeactivate.addJavascriptCondition("new mgnlTreeMenuItemConditionSelectedNotNodeData(" //$NON-NLS-1$
            + tree.getJavascriptTree()
            + ")"); //$NON-NLS-1$
        menuDeactivate.addJavascriptCondition("new mgnlTreeMenuItemConditionPermissionWrite("
            + tree.getJavascriptTree()
            + ")");

        ContextMenuItem menuLoadZip = new ContextMenuItem("loadzip");
        menuLoadZip.setLabel(msgs.get("media.loadzip")); //$NON-NLS-1$
        menuLoadZip.setIcon(request.getContextPath() + "/.resources/media/icons/ico16-zip.png"); //$NON-NLS-1$
        menuLoadZip
            .addJavascriptCondition("new mgnlTreeMenuItemConditionSelectedNotRoot(" + tree.getJavascriptTree() + ")"); //$NON-NLS-1$ //$NON-NLS-2$
        menuLoadZip.addJavascriptCondition("new mgnlTreeMenuItemConditionSelectedNotNodeData(" //$NON-NLS-1$
            + tree.getJavascriptTree()
            + ")"); //$NON-NLS-1$
        menuLoadZip.setOnclick("parent.uploadzip(" + tree.getJavascriptTree() + ");"); //$NON-NLS-1$
        menuLoadZip.addJavascriptCondition("new mgnlTreeMenuItemConditionPermissionWrite("
            + tree.getJavascriptTree()
            + ")");

        // is it possible to activate?
        if (!ActivationManagerFactory.getActivationManager().hasAnyActiveSubscriber())
        {
            menuActivate.addJavascriptCondition("new mgnlTreeMenuItemConditionBoolean(false)"); //$NON-NLS-1$
            menuActivateExcl.addJavascriptCondition("new mgnlTreeMenuItemConditionBoolean(false)"); //$NON-NLS-1$
            menuDeactivate.addJavascriptCondition("new mgnlTreeMenuItemConditionBoolean(false)"); //$NON-NLS-1$
        }

        if (!browseMode)
        {
            tree.addMenuItem(menuNewPage);

            for (ContextMenuItem it : menusNewMedia)
            {
                tree.addMenuItem(it);
            }

            tree.addSeparator();
            tree.addMenuItem(menuLoadZip);

            tree.addSeparator();
            tree.addMenuItem(menuDelete);

            tree.addSeparator();
            tree.addMenuItem(menuCut);
            tree.addMenuItem(menuCopy);

            if (!MediaEl.module().isSingleinstance())
            {
                tree.addSeparator();
                tree.addMenuItem(menuActivateExcl);
                tree.addMenuItem(menuActivate);
                tree.addMenuItem(menuDeactivate);
            }
        }
        else
        {
            tree.addMenuItem(ContextMenuItem.getRefreshMenuItem(tree, msgs, request));
        }
    }

    /**
     * {@inheritDoc}
     */
    public void prepareFunctionBar(Tree tree, boolean browseMode, HttpServletRequest request)
    {
        tree.addFunctionBarItem(FunctionBarItem.getRefreshFunctionBarItem(tree, getMessages(), request));
        // ContextMenuItem search = new ContextMenuItem("search");
        // search.setLabel(getMessages().get("media.menu.search"));
        // search.setIcon(request.getContextPath() + "/.resources/icons/24/view.gif");
        // search.setOnclick("parent.search()");
        // tree.addFunctionBarItem(new FunctionBarItem(search));
    }

    /**
     * {@inheritDoc}
     */
    public void prepareTree(Tree tree, boolean browseMode, HttpServletRequest request)
    {
        final Messages msgs = getMessages();

        tree.addItemType(MediaConfigurationManager.FOLDER.getSystemName(), "/.resources/media/icons/ico16-folder.png");

        TreeColumn column0 = TreeColumn.createLabelColumn(tree, msgs.get("tree.media.folders"), true);
        column0.setWidth(3);

        tree.addColumn(column0);

        if (!browseMode && !MediaEl.module().isSingleinstance())
        {

            if (ServerConfiguration.getInstance().isAdmin()
                || ActivationManagerFactory.getActivationManager().hasAnyActiveSubscriber())
            {
                TreeColumn columnIcons = TreeColumn.createIconColumn(tree, msgs.get("tree.config.status"), null);
                columnIcons.setCssClass(StringUtils.EMPTY);
                columnIcons.setWidth(1);
                columnIcons.setIconsActivation(true);
                columnIcons.setIconsPermission(true);

                tree.addColumn(columnIcons);
            }
        }
    }
}