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

package it.openutils.mgnlstruts11.render;

import info.magnolia.objectfactory.Components;
import info.magnolia.registry.RegistrationException;
import info.magnolia.rendering.template.TemplateDefinition;
import info.magnolia.rendering.template.registry.TemplateDefinitionProvider;
import info.magnolia.rendering.template.registry.TemplateDefinitionRegistry;

import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.config.ActionConfig;
import org.apache.struts.config.ForwardConfig;
import org.apache.struts.config.ModuleConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author fgiust
 * @version $Id$
 */
public class StrutsParagraphRegister
{

    /**
     * Configured Struts paragraphs.
     */
    private static Set<StrutsParagraph> paragraphs = new LinkedHashSet<StrutsParagraph>();

    /**
     * Logger.
     */
    private static Logger log = LoggerFactory.getLogger(StrutsParagraphRegister.class);

    /**
     * {@inheritDoc}
     */
    public static void parseParagraphs(ModuleConfig config)
    {

        ActionConfig[] acs = config.findActionConfigs();
        for (ActionConfig actionConfig : acs)
        {
            String path = actionConfig.getPath();
            String name = actionNameToParagraphName(path);
            collectParagraphs(name, path, StrutsParagraph.PARAGRAPHTYPE_ACTION);
        }

        ForwardConfig[] forwardConfigs = config.findForwardConfigs();
        for (ForwardConfig forwardConfig : forwardConfigs)
        {
            String path = forwardConfig.getPath();
            String name = "forward-" + forwardConfig.getName();
            collectParagraphs(name, path, StrutsParagraph.PARAGRAPHTYPE_FORWARD);
        }

    }

    /**
     * Generate a paragraph name from a Struts mapping. This method will take the last token after "/".
     * @param binding Strings action mapping
     * @return paragraph name
     */
    protected static String actionNameToParagraphName(String binding)
    {
        String dialogName = binding;
        if (dialogName.startsWith("/"))
        {
            dialogName = StringUtils.substringAfter(dialogName, "/");
        }

        dialogName = StringUtils.replace(dialogName, ".", "-");
        dialogName = StringUtils.replace(dialogName, "/", "-");
        return dialogName;
    }

    /**
     * Registers a Magnolia paragraph which will delegate to a struts action.
     * @param name dialog name
     * @param binding struts action mapping
     */
    @SuppressWarnings("unchecked")
    private static void collectParagraphs(String name, String path, String strutsType)
    {
        final StrutsParagraph paragraph = new StrutsParagraph();

        paragraph.setId("struts11:" + name);
        paragraph.setName(name);
        paragraph.setTitle("paragraph." + name + ".title");
        paragraph.setDescription("paragraph." + name + ".description");
        paragraph.setDialog(name);
        paragraph.setTemplateScript(path);
        paragraph.setRenderType("struts");
        paragraph.setStrutsType(strutsType);
        paragraph.setI18nBasename("it.openutils.mgnlstruts11.messages");
        paragraphs.add(paragraph);

        log.info("Registering struts paragraph " + paragraph.getName());
        // ParagraphManager.getInstance().getParagraphs().put(paragraph.getName(), paragraph);
        Components.getComponent(TemplateDefinitionRegistry.class).register(new TemplateDefinitionProvider()
        {
            
            public TemplateDefinition getTemplateDefinition() throws RegistrationException
            {
                return paragraph;
            }
            
            public String getId()
            {
                return paragraph.getId();
            }
        });
    }

    @SuppressWarnings("unchecked")
    public static void registerParagraphs()
    {
        for (final TemplateDefinition paragraph : paragraphs)
        {
            log.info("Registering struts paragraph " + paragraph.getName());
            // ParagraphManager.getInstance().getParagraphs().put(paragraph.getName(), paragraph);
            Components.getComponent(TemplateDefinitionRegistry.class).register(new TemplateDefinitionProvider()
            {
                
                public TemplateDefinition getTemplateDefinition() throws RegistrationException
                {
                    return paragraph;
                }
                
                public String getId()
                {
                    return paragraph.getName();
                }
            });
        }
    }

    public static Set<StrutsParagraph> getParagraphs()
    {
        return paragraphs;
    }

}
