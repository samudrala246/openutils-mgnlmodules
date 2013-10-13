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

import info.magnolia.cms.core.MgnlNodeType;
import info.magnolia.cms.gui.control.Tree;
import info.magnolia.cms.gui.control.TreeColumn;
import info.magnolia.cms.gui.control.TreeColumnHtmlRenderer;
import info.magnolia.module.admininterface.trees.JcrBrowserTreeConfiguration;

import javax.servlet.http.HttpServletRequest;

import net.sourceforge.openutils.mgnlmedia.media.configuration.MediaConfigurationManager;


/**
 * Tree Configuration that shows folders and media but no resolutions
 * @author molaschi
 * @version $Id$
 */
public class MediaBrowserConfiguration extends JcrBrowserTreeConfiguration
{

    /**
     * {@inheritDoc}
     */
    @Override
    public void prepareTree(Tree tree, boolean browseMode, HttpServletRequest request)
    {
        super.prepareTree(tree, browseMode, request);

        tree.getItemTypes().clear();

        tree.addItemType(MgnlNodeType.NT_CONTENT, null);
        tree.addItemType(MediaConfigurationManager.NT_MEDIA, null);

        TreeColumn tc = TreeColumn.createColumn(tree, "Media type", new TreeColumnHtmlRenderer()
        {

            /**
             * {@inheritDoc}
             */
            @SuppressWarnings("deprecation")
            public String renderHtml(TreeColumn treeColumn, info.magnolia.cms.core.Content content)
            {
                return content.getNodeData("type").getString();
            }

        });

        tree.addColumn(tc);
    }

}
