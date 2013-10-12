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

import info.magnolia.cms.core.MetaData;
import info.magnolia.cms.core.MgnlNodeType;
import info.magnolia.context.MgnlContext;
import info.magnolia.jcr.util.MetaDataUtil;
import info.magnolia.jcr.util.NodeUtil;
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

    @Override
    public String execute()
    {
        final ExtendedTemplate templateDef = this.getDefinition();
        MgnlContext.doInSystemContext(new MgnlContext.VoidOp()
        {

            @Override
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
                Node paragraph;
                try
                {
                    paragraph = NodeUtil.createPath(content, parDef[0], MgnlNodeType.NT_COMPONENT, true);
                    MetaData metaData = MetaDataUtil.getMetaData(paragraph);
                    if (StringUtils.isEmpty(metaData.getTemplate()))
                    {
                        metaData.setTemplate(parDef[1]);
                        paragraph.getSession().save();
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
