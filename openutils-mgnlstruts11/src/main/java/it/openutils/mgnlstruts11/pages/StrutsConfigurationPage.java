/**
 *
 * Struts 1.1 module for Magnolia CMS (http://www.openmindlab.com/lab/products/mgnlstruts.html)
 * Copyright(C) ${project.inceptionYear}-2012, Openmind S.r.l. http://www.openmindonline.it
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

package it.openutils.mgnlstruts11.pages;

import info.magnolia.cms.i18n.Messages;
import info.magnolia.cms.i18n.MessagesUtil;
import info.magnolia.module.admininterface.DialogHandlerManager;
import info.magnolia.module.admininterface.DialogMVCHandler;
import info.magnolia.module.admininterface.InvalidDialogHandlerException;
import info.magnolia.module.admininterface.TemplatedMVCHandler;
import it.openutils.mgnlstruts11.render.StrutsParagraph;
import it.openutils.mgnlstruts11.render.StrutsParagraphRegister;

import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;


/**
 * A magnolia page that lists all the Struts paragraphs.
 * @author fgiust
 * @version $Id$
 */
public class StrutsConfigurationPage extends TemplatedMVCHandler
{

    /**
     * Required constructor.
     * @param name page name
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     */
    public StrutsConfigurationPage(String name, HttpServletRequest request, HttpServletResponse response)
    {
        super(name, request, response);
    }

    public Set<StrutsParagraph> getStrutsParagraphs()
    {
        return StrutsParagraphRegister.getParagraphs();
    }

    public Messages getMessages()
    {
        // @todo review
        return MessagesUtil.chain(new String[]{
            "info.magnolia.module.admininterface.messages_templating_custom",
            "info.magnolia.module.admininterface.messages_templating" });
    }

    public ParagraphManager getParagraphManager()
    {
        return ParagraphManager.getInstance();
    }

    public boolean isDialogConfigured(String dialogName)
    {
        DialogMVCHandler handler = null;
        try
        {
            handler = DialogHandlerManager.getInstance().getDialogHandler(dialogName, request, response);
        }
        catch (InvalidDialogHandlerException e)
        {
            return false;
        }

        return handler != null;
    }

    public String getDialogPath(String dialogName)
    {
        DialogMVCHandler handler = null;
        try
        {
            handler = DialogHandlerManager.getInstance().getDialogHandler(dialogName, request, response);
        }
        catch (InvalidDialogHandlerException e)
        {
            return StringUtils.EMPTY;
        }

        return handler.getConfigNode().getHandle();
    }
}
