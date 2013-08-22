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

package net.sourceforge.openutils.mgnlrules.el;

import info.magnolia.context.MgnlContext;
import info.magnolia.context.WebContext;

import java.io.IOException;
import java.io.StringWriter;

import javax.servlet.ServletException;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import net.sourceforge.openutils.mgnlrules.configuration.ExpressionFunctionManager;

import org.apache.taglibs.standard.lang.jstl.Evaluator;
import org.apache.taglibs.standard.lang.support.ExpressionEvaluatorManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author dschivo
 * @version $Id$
 */
public class ExpressionsElFunctions
{

    /**
     * 
     */
    public static final String EXPRESSION_KEY = ExpressionsElFunctions.class.getName() + ".expression";

    /**
     * 
     */
    public static final String EVALUATE_JSP = "/WEB-INF/jsps/expressions/evaluate.jsp";

    /**
     * Logger.
     */
    private static Logger log = LoggerFactory.getLogger(ExpressionsElFunctions.class);

    /**
     * Evaluates an expression in EL syntax (without dollar sign and curly braces delimiters). Use this method from
     * within a JSP page, where a pageContext is available.
     * @param expression
     * @param pageContext
     * @return
     * @throws JspException
     */
    public static Object evaluate(String expression, PageContext pageContext) throws JspException
    {
        Evaluator evaluator = (Evaluator) ExpressionEvaluatorManager
            .getEvaluatorByName(ExpressionEvaluatorManager.EVALUATOR_CLASS);
        return evaluator.evaluate(
            null,
            "${" + expression + "}",
            Object.class,
            null,
            pageContext,
            ExpressionFunctionManager.getInstance().getFunctions(),
            "mexpr");
    }

    /**
     * Evaluates an expression in EL syntax (without dollar sign and curly braces delimiters). Use this method if a
     * pageContext is not available.
     * @param expression
     * @return
     * @throws ServletException
     * @throws IOException
     */
    public static String evaluate(String expression) throws ServletException, IOException
    {
        WebContext wc = MgnlContext.getWebContext("Expressions can only be evaluated with a WebContext");
        wc.getRequest().setAttribute(EXPRESSION_KEY, expression);
        StringWriter sw = new StringWriter();
        wc.include(EVALUATE_JSP, sw);
        return sw.toString();
    }
}
