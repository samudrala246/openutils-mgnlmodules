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

package net.sourceforge.openutils.mgnlmedia.media.tree;


import info.magnolia.cms.core.Content;
import info.magnolia.cms.core.ItemType;
import info.magnolia.cms.gui.control.Tree;
import info.magnolia.cms.gui.control.TreeColumn;
import info.magnolia.cms.gui.control.TreeColumnHtmlRenderer;
import info.magnolia.module.admininterface.trees.JcrBrowserTreeConfiguration;

import javax.jcr.RepositoryException;
import javax.servlet.http.HttpServletRequest;

import net.sourceforge.openutils.mgnlmedia.media.configuration.MediaConfigurationManager;


/**
 * Custom JCR tree browser configuration that adds management of media custom nodetypes (mgnl:media mgnl:resolutions)
 * and adds a column showing node type
 * @author molaschi
 * @version $Id$
 */
@SuppressWarnings("deprecation")
public class JcrBrowserWithNodeTypeTreeConfiguration extends JcrBrowserTreeConfiguration
{

    /**
     * {@inheritDoc}
     */
    @Override
    public void prepareTree(Tree tree, boolean browseMode, HttpServletRequest request)
    {
        // TODO Auto-generated method stub
        super.prepareTree(tree, browseMode, request);

        tree.getItemTypes().clear();

        tree.addItemType(ItemType.CONTENT);
        tree.addItemType(MediaConfigurationManager.NT_MEDIA);
        tree.addItemType(MediaConfigurationManager.NT_RESOLUTIONS);
        tree.addItemType(Tree.ITEM_TYPE_NODEDATA);

        TreeColumn tc = TreeColumn.createColumn(tree, "Node type", new TreeColumnHtmlRenderer()
        {

            /**
             * {@inheritDoc}
             */
            public String renderHtml(TreeColumn treeColumn, Content content)
            {
                try
                {
                    return content.getNodeType().getName();
                }
                catch (RepositoryException e)
                {
                    return "error";
                }
            }

        });

        tree.addColumn(tc);
    }

}
