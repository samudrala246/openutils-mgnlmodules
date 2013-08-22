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
import info.magnolia.cms.exchange.ExchangeException;
import info.magnolia.module.admininterface.AdminTreeMVCHandler;

import javax.jcr.RepositoryException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.openutils.mgnlmedia.media.tags.el.MediaEl;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author dschivo
 */
public class PlaylistsTreeMVCHandler extends AdminTreeMVCHandler
{

    /**
     * Log
     */
    private static Logger log = LoggerFactory.getLogger(PlaylistsTreeMVCHandler.class);

    /**
     * 
     */
    public PlaylistsTreeMVCHandler(String name, HttpServletRequest request, HttpServletResponse response)
    {
        super(name, request, response);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void renderHeaderIncludes(StringBuffer html)
    {
        super.renderHeaderIncludes(html);
        html.append("<script type=\"text/javascript\">\n");

        html.append("mgnlTree.prototype.openPlaylist = function(path){ parent.openPlaylist(path); };\n");

        html.append("_mgnlTreeSaveNodeDataCallback = mgnlTreeSaveNodeDataCallback;\n");
        html.append("mgnlTreeSaveNodeDataCallback = function(params, html) {\n");
        html.append("  var path = null;\n");
        html.append("  if (params.isLabel) {\n");
        html.append("    path = params.id.replace(/[^\\/]*$/, html);\n");
        html.append("    var img = document.getElementById(params.treeName + '_' + params.id + '_Icon');\n");
        html.append("    if (img.src.indexOf('ico16-playlist.png') == -1) path = null;\n");
        html.append("  }\n");
        html.append("  _mgnlTreeSaveNodeDataCallback.apply(this, arguments);\n");
        html.append("  if (path) parent.openPlaylist(path);\n");
        html.append("};\n");

        html.append("</script>\n");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTreeClass()
    {
        return PlaylistsTree.class.getName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String pasteNode(String pathOrigin, String pathSelected, int pasteType, int action)
        throws ExchangeException, RepositoryException
    {
        String movedHandle = super.pasteNode(pathOrigin, pathSelected, pasteType, action);
        if (!StringUtils.isEmpty(movedHandle))
        {
            try
            {
                Content moved = getHierarchyManager().getContent(movedHandle);
                moved.getParent().updateMetaData();
                if (MediaEl.module().isSingleinstance())
                {
                    try
                    {
                        moved.getParent().getMetaData().setActivated();
                    }
                    catch (RepositoryException e)
                    {
                        log.error("Error adding activated status to playlist entry node", e);
                    }
                }
                moved.getParent().save();
            }
            catch (RepositoryException re)
            {
                log.error("Problem when updating playlist activation status for "
                    + movedHandle
                    + " "
                    + org.apache.commons.lang.ClassUtils.getShortClassName(re.getClass())
                    + ": "
                    + re.getMessage(), re);
            }
        }
        return movedHandle;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteNode(String parentPath, String label) throws ExchangeException, RepositoryException
    {
        super.deleteNode(parentPath, label);
        Content parentNode = getHierarchyManager().getContent(parentPath);
        parentNode.updateMetaData();
        if (MediaEl.module().isSingleinstance())
        {
            parentNode.getMetaData().setActivated();
        }
        parentNode.save();
    }
}
