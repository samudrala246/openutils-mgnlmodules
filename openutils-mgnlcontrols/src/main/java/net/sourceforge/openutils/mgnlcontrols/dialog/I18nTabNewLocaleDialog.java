/**
 *
 * Controls module for Magnolia CMS (http://www.openmindlab.com/lab/products/controls.html)
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

package net.sourceforge.openutils.mgnlcontrols.dialog;

import info.magnolia.cms.core.Content;
import info.magnolia.cms.gui.dialog.Dialog;
import info.magnolia.cms.gui.dialog.DialogControlImpl;
import info.magnolia.module.admininterface.DialogHandlerManager;
import info.magnolia.module.admininterface.dialogs.ConfiguredDialog;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;

import javax.jcr.RepositoryException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author dschivo
 * @version $Id$
 */
public class I18nTabNewLocaleDialog extends ConfiguredDialog
{

    private static Logger log = LoggerFactory.getLogger(I18nTabNewLocaleDialog.class);

    private String dialogName = StringUtils.EMPTY;

    private String tabName = StringUtils.EMPTY;

    private String tabId = StringUtils.EMPTY;

    public I18nTabNewLocaleDialog(
        String name,
        HttpServletRequest request,
        HttpServletResponse response,
        Content configNode)
    {
        super(name, request, response, configNode);
        dialogName = params.getParameter("mgnlI18nDialog");
        tabName = params.getParameter("mgnlI18nTab");
        tabId = params.getParameter("mgnlI18nId");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Dialog createDialog(Content configNode, Content storageNode) throws RepositoryException
    {
        final Content dialogConfigNode = DialogHandlerManager.getInstance().getDialogConfigNode(dialogName);
        return super.createDialog(dialogConfigNode, storageNode);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void renderHtml(String view) throws IOException
    {
        PrintWriter out = this.getResponse().getWriter();
        if (VIEW_SHOW_DIALOG.equals(view))
        {
            try
            {
                DialogControlImpl tab = getDialog().getSub(tabName);
                Field idField = DialogControlImpl.class.getDeclaredField("id");
                idField.setAccessible(true);
                idField.set(tab, tabId);
                tab.drawHtml(out);
            }
            catch (Exception e)
            {
                log.error("Exception caught", e);
            }
        }
    }
}
