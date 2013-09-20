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

package net.sourceforge.openutils.mgnlmedia.playlist.dialog;


import info.magnolia.cms.core.Content;
import info.magnolia.cms.gui.dialog.Dialog;
import info.magnolia.cms.gui.misc.Sources;
import info.magnolia.module.admininterface.SaveHandler;
import info.magnolia.module.admininterface.dialogs.ConfiguredDialog;

import java.io.IOException;
import java.io.PrintWriter;

import javax.jcr.RepositoryException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.openutils.mgnlmedia.media.dialog.LayerDialog;
import net.sourceforge.openutils.mgnlmedia.playlist.PlaylistConstants;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author ADMIN
 * @version $Id: $
 */
public class SearchBasedPlaylistDialogMVC extends ConfiguredDialog
{

    /**
     * Logger.
     */
    private Logger log = LoggerFactory.getLogger(SearchBasedPlaylistDialogMVC.class);

    /**
     * 
     */
    public SearchBasedPlaylistDialogMVC(
        String name,
        HttpServletRequest request,
        HttpServletResponse response,
        Content configNode)
    {
        super(name, request, response, configNode);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Dialog createDialog(Content configNode, Content storageNode) throws RepositoryException
    {
        Dialog dialog = new LayerDialog();
        dialog.init(request, response, storageNode, configNode);
        return dialog;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void configureSaveHandler(SaveHandler saveHandler)
    {
        super.configureSaveHandler(saveHandler);
        if (!StringUtils.isEmpty(form.getParameter("parentFolder")))
        {
            saveHandler.setPath(form.getParameter("parentFolder"));
        }
        if (!StringUtils.isEmpty(form.getParameter("playlistName")))
        {
            saveHandler.setNodeName(form.getParameter("playlistName"));
        }
        saveHandler.setCreationItemType(PlaylistConstants.PLAYLIST);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void renderHtml(String view) throws IOException
    {
        if (VIEW_CLOSE_WINDOW.equals(view))
        {
            PrintWriter out = this.getResponse().getWriter();

            out.println("<html>"); //$NON-NLS-1$
            out.println(new Sources(this.getRequest().getContextPath()).getHtmlJs());
            out.println("<script type=\"text/javascript\">"); //$NON-NLS-1$
            String path = StringUtils.defaultIfEmpty(form.getParameter("mgnlPath"), form.getParameter("parentFolder")
                + "/"
                + form.getParameter("playlistName"));
            out
                .println("parent.location.href = '" + getRequest().getContextPath() + "/.magnolia/pages/playlistsBrowser.html?openPath=" + path + "'"); //$NON-NLS-1$
            out.println("</script></html>"); //$NON-NLS-1$
        }
        else
        {
            super.renderHtml(view);
        }
    }
}
