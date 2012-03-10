/**
 *
 * SimpleMedia Module for Magnolia CMS (http://www.openmindlab.com/lab/products/media.html)
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

package net.sourceforge.openutils.mgnlmedia.freemarker;

import freemarker.ext.beans.BeansWrapper;
import freemarker.template.TemplateModelException;
import info.magnolia.cms.core.Content;
import info.magnolia.module.templating.RenderableDefinition;
import info.magnolia.module.templating.RenderingModel;
import info.magnolia.module.templatingkit.renderers.STKTemplateRenderer;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * deprecated after http://jira.openmindlab.com/browse/MEDIA-236
 * @author Ernst Bunders
 * @author fgiust
 */
@Deprecated
public class SktSimpleMediaTemplateRenderer extends STKTemplateRenderer
{

    /**
     * Logger.
     */
    private Logger log = LoggerFactory.getLogger(SktSimpleMediaTemplateRenderer.class);

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    protected void setupContext(Map ctx, Content content, RenderableDefinition definition, RenderingModel model,
        Object actionResult)
    {

        try
        {
            ctx.put(
                "media",
                BeansWrapper
                    .getDefaultInstance()
                    .getStaticModels()
                    .get(net.sourceforge.openutils.mgnlmedia.media.tags.el.MediaEl.class.getName()));
        }
        catch (TemplateModelException e)
        {
            log.error(e.getMessage(), e);
        }

        super.setupContext(ctx, content, definition, model, actionResult);
    }

}
