/**
 *
 * Rules module for Magnolia CMS (http://www.openmindlab.com/lab/products/mgnlrules.html)
 * Copyright(C) 2010-2013, Openmind S.r.l. http://www.openmindonline.it
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

import info.magnolia.jcr.util.PropertyUtil;
import info.magnolia.rendering.context.RenderingContext;
import info.magnolia.rendering.engine.RenderException;
import info.magnolia.rendering.renderer.JspRenderer;
import info.magnolia.rendering.template.RenderableDefinition;

import java.util.Map;

import javax.jcr.Node;

import net.sourceforge.openutils.mgnlrules.el.ExpressionsElFunctions;


/**
 * Custom paragraph renderer that evaluates an expression before rendering the jsp, when a pageContext is not yet
 * available. This sample uses the overload of the evaluate method that does not need a pageContext.
 * @author dschivo
 * @version $Id$
 */
public class ConditionalRenderer extends JspRenderer
{

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onRender(Node content, RenderableDefinition definition, RenderingContext renderingCtx,
        Map<String, Object> ctx, String templateScript) throws RenderException
    {
        String expression = PropertyUtil.getString(content, "renderCondition");
        try
        {
            // expression evaluation without a pageContext
            String result = ExpressionsElFunctions.evaluate(expression);
            if ("true".equals(result))
            {
                super.onRender(content, definition, renderingCtx, ctx, templateScript);
            }
        }
        catch (Exception e)
        {
            throw new RenderException("Can't render paragraph template " + templateScript, e);
        }
    }

}
