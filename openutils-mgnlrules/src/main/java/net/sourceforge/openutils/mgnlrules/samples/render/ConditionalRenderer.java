/**
 *
 * Rules module for Magnolia CMS (http://www.openmindlab.com/lab/products/mgnlrules.html)
 * Copyright(C) 2010-2012, Openmind S.r.l. http://www.openmindonline.it
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

package net.sourceforge.openutils.mgnlrules.samples.render;

import info.magnolia.cms.core.Content;
import info.magnolia.cms.util.NodeDataUtil;
import info.magnolia.module.templating.RenderException;
import info.magnolia.module.templating.RenderableDefinition;
import info.magnolia.module.templating.paragraphs.JspParagraphRenderer;

import java.io.Writer;
import java.util.Map;

import net.sourceforge.openutils.mgnlrules.el.ExpressionsElFunctions;


/**
 * Custom paragraph renderer that evaluates an expression before rendering the jsp, when a pageContext is not yet
 * available. This sample uses the overload of the evaluate method that does not need a pageContext.
 * @author dschivo
 * @version $Id$
 */
public class ConditionalRenderer extends JspParagraphRenderer
{

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onRender(Content content, RenderableDefinition definition, Writer out, Map ctx, String templatePath)
        throws RenderException
    {
        String expression = NodeDataUtil.getString(content, "renderCondition");
        try
        {
            // expression evaluation without a pageContext
            String result = ExpressionsElFunctions.evaluate(expression);
            if ("true".equals(result))
            {
                super.onRender(content, definition, out, ctx, templatePath);
            }
        }
        catch (Exception e)
        {
            throw new RenderException("Can't render paragraph template " + templatePath, e);
        }
    }

}
