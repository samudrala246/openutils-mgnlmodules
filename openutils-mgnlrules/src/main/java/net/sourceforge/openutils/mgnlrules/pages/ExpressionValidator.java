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

package net.sourceforge.openutils.mgnlrules.pages;

import info.magnolia.module.admininterface.TemplatedMVCHandler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.openutils.mgnlrules.el.ExpressionsElFunctions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author dschivo
 */
public class ExpressionValidator extends TemplatedMVCHandler
{

    private static Logger log = LoggerFactory.getLogger(ExpressionValidator.class);

    private String validationMessage;

    public ExpressionValidator(String name, HttpServletRequest request, HttpServletResponse response)
    {
        super(name, request, response);
    }

    /**
     * Returns the validationMessage.
     * @return the validationMessage
     */
    public String getValidationMessage()
    {
        return validationMessage;
    }

    @Override
    public String show()
    {
        return super.show();
    }

    public String validate()
    {
        String expression = request.getParameter("expression");
        try
        {
            validationMessage = ExpressionsElFunctions.evaluate(expression);
            String search = "\"${" + expression + "}\": ";
            int pos = validationMessage.indexOf(search);
            if (pos >= 0)
            {
                validationMessage = validationMessage.substring(pos + search.length());
            }
            else
            {
                validationMessage = "Validation OK";
            }
        }
        catch (ServletException e)
        {
            validationMessage = e.getMessage();
            log.error(e.getMessage(), e);
        }
        catch (IOException e)
        {
            validationMessage = e.getMessage();
            log.error(e.getMessage(), e);
        }
        return "validate";
    }
}
