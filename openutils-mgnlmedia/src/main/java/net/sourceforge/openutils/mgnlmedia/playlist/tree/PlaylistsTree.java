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


import info.magnolia.cms.core.Content;
import info.magnolia.cms.core.NodeData;
import info.magnolia.cms.gui.control.Tree;

import javax.jcr.RepositoryException;

import net.sourceforge.openutils.mgnlmedia.playlist.PlaylistConstants;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author dschivo
 */
public class PlaylistsTree extends Tree
{

    /**
     * Logger.
     */
    private Logger log = LoggerFactory.getLogger(PlaylistsTree.class);

    /**
     * 
     */
    public PlaylistsTree(String name, String repository)
    {
        super(name, repository);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void getHtmlOfSingleItem(StringBuffer html, Content parentNode, String itemType, Object item)
        throws RepositoryException
    {
        StringBuffer sb = new StringBuffer();
        super.getHtmlOfSingleItem(sb, parentNode, itemType, item);
        if (item instanceof Content)
        {
            Content node = (Content) item;
            String s = new String(sb);
            sb.insert(
                StringUtils.indexOf(s, '>', s.indexOf("mgnlTreeControl.nodeHighlight")),
                "onclick=\"mgnlTreeControl.openPlaylist('"
                    + (PlaylistConstants.PLAYLIST.getSystemName().equals(itemType) ? node.getHandle() : "")
                    + "');\"");
        }
        html.append(sb);
    }

    /**
     * {@inheritDoc}
     */
    protected String getIcon(Content node, NodeData nodedata, String itemType)
    {
        try
        {
            // handle dynamic playlist icon
            if (node != null && PlaylistConstants.PLAYLIST.equals(node.getItemType()) && node.hasContent("search"))
            {
                return "/.resources/media/icons/ico16-playlist-dynamic.png";
            }
        }
        catch (RepositoryException e)
        {
            log.error("Failed to read content of " + node.getHandle());
        }

        return super.getIcon(node, nodedata, itemType);
    }

}
