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
import info.magnolia.cms.core.ItemType;
import info.magnolia.cms.core.Path;
import info.magnolia.cms.util.ContentUtil;
import info.magnolia.context.MgnlContext;
import info.magnolia.module.admininterface.SaveHandler;
import info.magnolia.module.admininterface.dialogs.ConfiguredDialog;

import java.io.IOException;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.openutils.mgnlmedia.media.tags.el.MediaEl;
import net.sourceforge.openutils.mgnlmedia.playlist.PlaylistConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author dschivo
 */
@SuppressWarnings("deprecation")
public class PlaylistEntryDialogMVC extends ConfiguredDialog
{

    /**
     * Logger.
     */
    private Logger log = LoggerFactory.getLogger(PlaylistEntryDialogMVC.class);

    /**
     * 
     */
    public PlaylistEntryDialogMVC(
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
    protected void configureSaveHandler(SaveHandler saveHandler)
    {
        super.configureSaveHandler(saveHandler);
        saveHandler.setCreationItemType(new ItemType(PlaylistConstants.NT_PLAYLIST_ENTRY));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean onPreSave(SaveHandler control)
    {
        try
        {
            Session hm = MgnlContext.getJCRSession(PlaylistConstants.REPO);
            Node c = hm.getNode(control.getPath());
            control.setNodeName(Path.getUniqueLabel(ContentUtil.asContent(c), "entry"));
        }
        catch (RepositoryException e)
        {
            log.error("error getting {}", control.getPath(), e);
            return false;
        }
        return super.onPreSave(control);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean onPostSave(SaveHandler control)
    {
        boolean result = super.onPostSave(control);
        if (result)
        {
            Content node = this.getStorageNode();
            if (node != null)
            {
                try
                {
                    node.updateMetaData();
                    node.save();
                }
                catch (RepositoryException e1)
                {
                    // ignore
                }

                if (MediaEl.module().isSingleinstance())
                {
                    try
                    {
                        node.getMetaData().setActivated();
                        node.save();
                    }
                    catch (RepositoryException e)
                    {
                        log.error("Error adding activated status to playlist entry node", e);
                    }
                }
            }

        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void renderHtml(String view) throws IOException
    {
    }
}
