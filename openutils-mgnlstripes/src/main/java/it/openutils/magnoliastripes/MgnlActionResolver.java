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

package it.openutils.magnoliastripes;

import info.magnolia.module.templating.Paragraph;
import info.magnolia.module.templating.ParagraphManager;
import it.openutils.magnoliastripes.annotations.Dialog;

import java.util.HashSet;
import java.util.Set;

import net.sourceforge.stripes.action.ActionBean;
import net.sourceforge.stripes.controller.NameBasedActionResolver;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * ActionResolver that extends <code>NameBasedActionResolver</code>, registering any found Stripe action as a Magnolia
 * paragraph.
 * @author fgiust
 * @version $Id: $
 */
public class MgnlActionResolver extends NameBasedActionResolver
{

    /**
     * Logger.
     */
    private static Logger log = LoggerFactory.getLogger(MgnlActionResolver.class);

    /**
     * Configured Stripes paragraphs.
     */
    private static Set<Paragraph> paragraphs = new HashSet<Paragraph>();

    /**
     * {@inheritDoc}
     */
    @Override
    protected void addActionBean(Class< ? extends ActionBean> clazz)
    {
        String binding = getUrlBinding(clazz);

        // Only process the class if it's properly annotated
        if (binding != null)
        {
            String paragraphName = actionNameToParagraphName(binding);
            String dialogName = annotationDialogToParagraphName(clazz);
            if (StringUtils.isBlank(dialogName))
            {
                dialogName = paragraphName;
            }
            collectStripesParagraphs(paragraphName, dialogName, binding);
            super.addActionBean(clazz);
        }
    }

    /**
     * Generate a paragraph name from "Dialog" Annotation.
     * @return paragraph name
     */
    protected String annotationDialogToParagraphName(Class< ? extends ActionBean> clazz)
    {
        return processDialogAnnotation(clazz);
    }

    protected String processDialogAnnotation(Class< ? extends ActionBean> clazz)
    {
        // check that class is annotated
        Dialog annotation = clazz.getAnnotation(Dialog.class);
        if (annotation == null)
        {
            return null;
        }
        // check that value is not null or empty
        String pattern = annotation.value();
        if (StringUtils.isBlank(pattern))
        {
            return null;
        }
        return pattern;
    }

    /**
     * Generate a paragraph name from a Stripes binding. This method will take the last token after "/", strip any
     * extension and convert everything to lowercase.
     * @param binding Stripe action binding
     * @return paragraph name
     */
    protected String actionNameToParagraphName(String binding)
    {
        String dialogName = StringUtils.lowerCase(StringUtils.substringBeforeLast(
            StringUtils.substringAfterLast(binding, "/"),
            "."));
        return dialogName;
    }

    /**
     * Registers a Magnolia paragraph which will delegate to a Stripe action.
     * @param name dialog name
     * @param binding Stripes action binding
     */
    private void collectStripesParagraphs(String paragraphName, String dialogName, String binding)
    {
        Paragraph paragraph = new Paragraph();

        paragraph.setName(paragraphName);
        paragraph.setTitle("paragraph." + paragraphName + ".title");
        paragraph.setDescription("paragraph." + paragraphName + ".description");
        paragraph.setDialog(dialogName);
        paragraph.setTemplatePath(binding);
        paragraph.setType("stripes");
        paragraph.setI18nBasename(StripesModule.getInstance().getI18nbasename());
        paragraphs.add(paragraph);

        log.info("Registering stripes paragraph {} with dialog {}", paragraph.getName(), paragraph.getDialog()); //$NON-NLS-1$ /$NON-NLS-2$
        ParagraphManager.getInstance().getParagraphs().put(paragraph.getName(), paragraph);
    }

    public static void registerParagraphs()
    {
        for (Paragraph paragraph : paragraphs)
        {
            log.info("Registering stripes paragraph {}", paragraph.getName()); //$NON-NLS-1$
            ParagraphManager.getInstance().getParagraphs().put(paragraph.getName(), paragraph);
        }
    }

    public static Set<Paragraph> getParagraphs()
    {
        return paragraphs;
    }

}
