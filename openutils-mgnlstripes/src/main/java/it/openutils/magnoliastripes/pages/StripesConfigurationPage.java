/**
 *
 * Stripes module for Magnolia CMS (http://www.openmindlab.com/lab/products/mgnlstripes.html)
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

package it.openutils.magnoliastripes.pages;

import info.magnolia.cms.i18n.Messages;
import info.magnolia.cms.i18n.MessagesUtil;
import info.magnolia.module.admininterface.DialogHandlerManager;
import info.magnolia.module.admininterface.DialogMVCHandler;
import info.magnolia.module.admininterface.InvalidDialogHandlerException;
import info.magnolia.module.admininterface.TemplatedMVCHandler;
import info.magnolia.objectfactory.Components;
import info.magnolia.registry.RegistrationException;
import info.magnolia.rendering.template.TemplateDefinition;
import info.magnolia.rendering.template.registry.TemplateDefinitionRegistry;
import it.openutils.magnoliastripes.MgnlActionResolver;

import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;


/**
 * A magnolia page that lists all the Stripes paragraphs.
 * @author luca boati
 */
public class StripesConfigurationPage extends TemplatedMVCHandler
{

    /**
     * Required constructor.
     * @param name page name
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     */
    public StripesConfigurationPage(String name, HttpServletRequest request, HttpServletResponse response)
    {
        super(name, request, response);
    }

    public Set<TemplateDefinition> getStripesParagraphs()
    {
        return MgnlActionResolver.getParagraphs();
    }

    public Messages getMessages()
    {
        // @todo review
        return MessagesUtil.chain(new String[]{
            "info.magnolia.module.admininterface.messages_templating_custom",
            "info.magnolia.module.admininterface.messages_templating",
            "it.openutils.magnoliastripes" });
    }

    public TemplateDefinitionRegistry getTemplateDefinitionRegistry()
    {
        return Components.getComponent(TemplateDefinitionRegistry.class);
    }

    public boolean isDialogConfigured(String paragraphName)
    {
        TemplateDefinition paragraph = null;
        try
        {
            paragraph = getTemplateDefinitionRegistry().getTemplateDefinition(paragraphName);
        }
        catch (RegistrationException e)
        {
        }
        if (paragraph != null)
        {
            String dialogName = paragraph.getDialog();
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
        else
        {
            return false;
        }

    }

    public String getDialogPath(String paragraphName)
    {
        TemplateDefinition paragraph = null;
        try
        {
            paragraph = getTemplateDefinitionRegistry().getTemplateDefinition(paragraphName);
        }
        catch (RegistrationException e)
        {
        }
        if (paragraph != null)
        {
            String dialogName = paragraph.getDialog();
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
        else
        {
            return StringUtils.EMPTY;
        }
    }
}
