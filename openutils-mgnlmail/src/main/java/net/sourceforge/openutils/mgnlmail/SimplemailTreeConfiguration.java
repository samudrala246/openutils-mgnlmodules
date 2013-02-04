/**
 *
 * simplemail module for Magnolia CMS (http://www.openmindlab.com/lab/products/mgnlmail.html)
 * Copyright(C) 2011-2011, Openmind S.r.l. http://www.openmindonline.it
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

package net.sourceforge.openutils.mgnlmail;

import info.magnolia.cms.core.ItemType;
import info.magnolia.cms.gui.control.ContextMenuItem;
import info.magnolia.cms.gui.control.Tree;
import info.magnolia.module.admininterface.trees.WebsiteTreeConfiguration;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author fgiust
 * @version $Id$
 */
public class SimplemailTreeConfiguration extends WebsiteTreeConfiguration
{

    private String urlprefix = "email";

    /**
     * Logger.
     */
    private Logger log = LoggerFactory.getLogger(SimplemailTreeConfiguration.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public void prepareTree(Tree tree, boolean browseMode, HttpServletRequest request)
    {
        super.prepareTree(tree, browseMode, request);

        tree.addIcon(ItemType.CONTENT.getSystemName(), "/.resources/simplemail/ico16-mail.png");
        tree.addItemType(ItemType.NT_FOLDER, Tree.ICONDOCROOT + "folder.gif");
        tree.setDrawShifter(true); // for folders
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void prepareContextMenu(Tree tree, boolean browseMode, HttpServletRequest request)
    {
        super.prepareContextMenu(tree, browseMode, request);

        ContextMenuItem menuNewFolder = new ContextMenuItem("newFolder");
        menuNewFolder.setLabel("New Folder"); // @todo translate
        menuNewFolder.setIcon(request.getContextPath() + "/.resources/icons/16/folder_add.gif"); //$NON-NLS-1$
        menuNewFolder.setOnclick(tree.getJavascriptTree() + ".createNode('" + ItemType.NT_FOLDER + "');"); //$NON-NLS-1$
        menuNewFolder.addJavascriptCondition("new mgnlTreeMenuItemConditionSelectedNotItemType(" //$NON-NLS-1$
            + tree.getJavascriptTree()
            + ", '" + ItemType.CONTENT + "')"); //$NON-NLS-1$

        // check needed for browse dialog in uuid links
        if (!browseMode)
        {

            List<ContextMenuItem> menuItems = tree.getMenu().getMenuItems();
            menuItems.add(2, menuNewFolder); // add after "new page"

            String action = "var w=window.open(mgnlEncodeURL(contextPath + '"
                + (urlprefix != null ? "/" + urlprefix : "")
                + "' + "
                + tree.getJavascriptTree()
                + ".selectedNode.path + '.html'),'mgnlInline','');if (w) w.focus();";

            ContextMenuItem menuOpen = tree.getMenu().getMenuItemByName("open");
            menuOpen.setOnclick(action);
            menuOpen.setLabel("Open email");
            menuOpen.addJavascriptCondition("new mgnlTreeMenuItemConditionSelectedNotItemType(" //$NON-NLS-1$
                + tree.getJavascriptTree()
                + ", '" + ItemType.FOLDER + "')"); //$NON-NLS-1$
            tree.setIconOndblclick(action);

            ContextMenuItem menuNew = tree.getMenu().getMenuItemByName("new");
            menuNew.setLabel("New email");
            menuNew.addJavascriptCondition("new mgnlTreeMenuItemConditionSelectedNotItemType(" //$NON-NLS-1$
                + tree.getJavascriptTree()
                + ", '" + ItemType.FOLDER + "')"); //$NON-NLS-1$

        }

        // todo: add menu item "send test mail"?
    }

    /**
     * Sets the urlprefix.
     * @param urlprefix the urlprefix to set
     */
    public void setUrlprefix(String urlprefix)
    {
        this.urlprefix = urlprefix;
    }
}
