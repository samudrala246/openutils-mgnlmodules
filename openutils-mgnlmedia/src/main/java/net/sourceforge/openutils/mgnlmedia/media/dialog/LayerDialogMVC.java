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

package net.sourceforge.openutils.mgnlmedia.media.dialog;


import info.magnolia.cms.core.Content;
import info.magnolia.cms.core.HierarchyManager;
import info.magnolia.cms.core.Path;
import info.magnolia.cms.gui.dialog.Dialog;
import info.magnolia.cms.gui.misc.Sources;
import info.magnolia.cms.util.NodeDataUtil;
import info.magnolia.context.MgnlContext;
import info.magnolia.module.admininterface.SaveHandler;
import info.magnolia.module.admininterface.dialogs.ConfiguredDialog;

import java.io.IOException;
import java.io.PrintWriter;

import javax.jcr.RepositoryException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.openutils.mgnlmedia.media.configuration.MediaConfigurationManager;
import net.sourceforge.openutils.mgnlmedia.media.lifecycle.MediaModule;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * With {@link LayerDialog} allows to have a dialog in an Ext js layer.
 * @author molaschi
 * @version $Id$
 */
@SuppressWarnings("deprecation")
public class LayerDialogMVC extends ConfiguredDialog
{

    /**
     * Logger.
     */
    private Logger log = LoggerFactory.getLogger(LayerDialogMVC.class);

    /**
     * @param name
     * @param request
     * @param response
     * @param configNode
     */
    public LayerDialogMVC(String name, HttpServletRequest request, HttpServletResponse response, Content configNode)
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
    public void renderHtml(String view) throws IOException
    {
        PrintWriter out = this.getResponse().getWriter();

        // after saving
        if (VIEW_CLOSE_WINDOW.equals(view))
        {
            out.println("<html>"); //$NON-NLS-1$
            out.println(new Sources(this.getRequest().getContextPath()).getHtmlJs());
            out.println("<script type=\"text/javascript\">"); //$NON-NLS-1$
            String parentPath = this.path;
            if (parentPath.endsWith(this.nodeName))
            {
                parentPath = StringUtils.substringBefore(parentPath, "/" + this.nodeName);
            }
            out.println("var path = '" + parentPath + "';");
            out.println("var type = '" + this.getRequest().getParameter("type") + "';");

            /**
             * replaced
             */
            out.println("parent.reloadFolder(path, type);");
            out.println("parent.closeLayer();"); //$NON-NLS-1$
            /**
             * end replaced
             */
            out.println("</script></html>"); //$NON-NLS-1$
        }
        // show the created dialog
        else if (VIEW_SHOW_DIALOG.equals(view))
        {
            try
            {
                getDialog().drawHtml(out);
            }
            catch (IOException e)
            {
                log.error("Exception caught", e);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void configureSaveHandler(SaveHandler saveHandler)
    {
        super.configureSaveHandler(saveHandler);

        saveHandler.setCreationItemType(MediaConfigurationManager.NT_MEDIA);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean onPostSave(SaveHandler handler)
    {
        super.onPostSave(handler);
        Content node = this.getStorageNode();

        String type = NodeDataUtil.getString(node, "type");

        // TODO rename dei nodi in base al filename?

        return MediaConfigurationManager.getInstance().getTypes().get(type).getHandler().onPostSave(node.getJCRNode());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean onPreSave(SaveHandler control)
    {
        String type = this.request.getParameter("type");

        if (control.getNodeName().equals("mgnlNew"))
        {
            HierarchyManager hm = MgnlContext.getHierarchyManager(MediaModule.REPO);
            Content c = null;
            try
            {
                c = hm.getContent(control.getPath());
            }
            catch (RepositoryException e)
            {
                log.error("error getting {}", control.getPath(), e);
                return false;
            }
            control.setNodeName(Path.getUniqueLabel(
                c,
                Path.getValidatedLabel(MediaConfigurationManager
                    .getInstance()
                    .getTypes()
                    .get(type)
                    .getHandler()
                    .getNewNodeName(form, request))));
        }

        return super.onPreSave(control);
    }
}
