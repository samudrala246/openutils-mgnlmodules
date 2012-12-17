/**
 *
 * Generic utilities for Magnolia CMS (http://www.openmindlab.com/lab/products/mgnlutils.html)
 * Copyright(C) 2009-2012, Openmind S.r.l. http://www.openmindonline.it
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

package it.openutils.mgnlutils.templating;

import info.magnolia.cms.core.Content;
import info.magnolia.cms.core.ItemType;
import info.magnolia.cms.util.ContentUtil;
import info.magnolia.context.MgnlContext;
import info.magnolia.rendering.model.RenderingModel;
import info.magnolia.rendering.model.RenderingModelImpl;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Model class for auto-generating paragraphs. See {@link ExtendedTemplate} for details.
 * @author fgiust
 * @version $Id$
 */
public class AutoGenerateTemplateModel extends RenderingModelImpl<ExtendedTemplate>
{

    private static Logger log = LoggerFactory.getLogger(AutoGenerateTemplateModel.class);

    /**
     * @param content
     * @param definition
     * @param parent
     */
    public AutoGenerateTemplateModel(Node content, ExtendedTemplate definition, RenderingModel parent)
    {
        super(content, definition, parent);
    }

    public String execute()
    {
        final ExtendedTemplate templateDef = this.getDefinition();
        MgnlContext.doInSystemContext(new MgnlContext.VoidOp()
        {

            public void doExec()
            {
                createMainArea(templateDef);
            }
        });
        return super.execute();
    }

    private void createMainArea(ExtendedTemplate templateDef)
    {
        String autogenerate = templateDef.getAutogenerate();

        if (StringUtils.isEmpty(autogenerate))
        {
            return;
        }

        String[] autogeneratelist = StringUtils.split(autogenerate, ",");

        for (String autogeneratepar : autogeneratelist)
        {
            String[] parDef = StringUtils.split(autogeneratepar, "=");
            if (parDef != null && parDef.length == 2)
            {
                Content paragraph;
                try
                {
                    paragraph = ContentUtil.createPath(ContentUtil.asContent(content), parDef[0], ItemType.CONTENTNODE, true);
                    if (StringUtils.isEmpty(paragraph.getTemplate()))
                    {
                        paragraph.getMetaData().setTemplate(parDef[1]);
                        paragraph.save();
                    }
                }

                catch (RepositoryException e)
                {
                    log.error("Cannot create auto-generated paragraph " + autogeneratepar, e);
                }
            }
        }
    }
}
