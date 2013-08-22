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

package net.sourceforge.openutils.mgnlrules.pages;

import info.magnolia.module.admininterface.TemplatedMVCHandler;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.openutils.mgnlrules.configuration.ExpressionLibrary;
import net.sourceforge.openutils.mgnlrules.configuration.ExpressionLibraryManager;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author dschivo
 */
public class ExpressionPicker extends TemplatedMVCHandler
{

    private static Logger log = LoggerFactory.getLogger(ExpressionPicker.class);

    private List<ExpressionLibrary> expressionProviders;

    public ExpressionPicker(String name, HttpServletRequest request, HttpServletResponse response)
    {
        super(name, request, response);
    }

    /**
     * Returns the expressionProviders.
     * @return the expressionProviders
     */
    public List<ExpressionLibrary> getExpressionProviders()
    {
        return expressionProviders;
    }

    @Override
    public String show()
    {
        expressionProviders = ExpressionLibraryManager.getInstance().getLibraries();
        return super.show();
    }

    public String escapeApostrophe(String str)
    {
        return StringUtils.replace(str, "'", "\\'");
    }
}
