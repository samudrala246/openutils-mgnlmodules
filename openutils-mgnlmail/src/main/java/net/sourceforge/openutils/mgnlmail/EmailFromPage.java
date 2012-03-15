/**
 *
 * simplemail module for Magnolia CMS (http://www.openmindlab.com/lab/products/mgnlmail.html)
 * Copyright(C) 2011-2011, Openmind S.r.l. http://www.openmindonline.it
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

package net.sourceforge.openutils.mgnlmail;

import freemarker.template.Template;
import info.magnolia.context.MgnlContext;
import info.magnolia.module.mail.MailTemplate;
import info.magnolia.module.mail.templates.impl.FreemarkerEmail;
import info.magnolia.objectfactory.Components;
import info.magnolia.rendering.engine.AppendableOnlyOutputProvider;
import info.magnolia.rendering.engine.RenderingEngine;

import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;

import javax.jcr.Node;

import org.apache.commons.lang.StringUtils;


/**
 * @author fgiust
 * @version $Id$
 */
public class EmailFromPage extends FreemarkerEmail
{

    public EmailFromPage(MailTemplate template)
    {
        super(template);
    }

    @Override
    public void setBodyFromResourceFile() throws Exception
    {

        final Writer writer = new StringWriter();

        final String pageUrl = this.getTemplate().getTemplateFile();

        Node mailNode = MgnlContext.getJCRSession("email").getNode(pageUrl);
        Components.getSingleton(RenderingEngine.class).render(mailNode, new AppendableOnlyOutputProvider(writer));

        String pageContent = writer.toString();

        super.setBody(pageContent);

        String body = proccesFreemarkerString(pageContent);

        setContent(body, getContentType());

        if (StringUtils.contains(body, "<title>"))
        {
            String title = StringUtils.substringBetween(body, "<title>", "</title>");
            if (StringUtils.isNotBlank(title))
            {
                setSubject(title);
            }
        }

    }

    public void setBodyFromTemplate(Template template, Map _map) throws Exception
    {
        final StringWriter writer = new StringWriter();
        template.process(_map, writer);
        writer.flush();
        setBody(writer.toString());
    }

}
