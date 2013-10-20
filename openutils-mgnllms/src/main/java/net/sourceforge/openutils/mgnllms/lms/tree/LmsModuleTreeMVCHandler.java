/**
 *
 * E-learning Module for Magnolia CMS (http://www.openmindlab.com/lab/products/lms.html)
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
package net.sourceforge.openutils.mgnllms.lms.tree;

import info.magnolia.module.admininterface.AdminTreeMVCHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.chain.Command;


/**
 * @author luca boati
 */
public class LmsModuleTreeMVCHandler extends AdminTreeMVCHandler
{

    /**
     * @param name
     * @param request
     * @param response
     */
    public LmsModuleTreeMVCHandler(String name, HttpServletRequest request, HttpServletResponse response)
    {
        super(name, request, response);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void renderHeaderIncludes(StringBuffer html)
    {
        super.renderHeaderIncludes(html);
        html.append("<script type=\"text/javascript\" src=\""
            + this.getRequest().getContextPath()
            + "/.resources/mgnllms/js/lms.js\"><!-- --></script>");
        html.append("<script type=\"text/javascript\" src=\""
            + this.getRequest().getContextPath()
            + "/.resources/mgnllms/js/conditions.js\"><!-- --></script>");
        html.append("<link rel=\"stylesheet\" href=\""
            + this.getRequest().getContextPath()
            + "/.resources/mgnllms/css/override.css\"><!-- --></script>");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTreeClass()
    {
        return LmsModuleTree.class.getName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Command findCommand(String commandName)
    {
        String cmdName = commandName;
//        if ("activate".equals(cmdName))
//        {
//            cmdName = "activateLms";
//        }

        Command cmd = super.findCommand(cmdName);
        return cmd;
    }

}
