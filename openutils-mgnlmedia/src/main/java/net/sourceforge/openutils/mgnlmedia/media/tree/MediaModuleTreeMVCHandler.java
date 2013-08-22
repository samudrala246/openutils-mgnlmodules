/**
 *
 * SimpleMedia Module for Magnolia CMS (http://www.openmindlab.com/lab/products/media.html)
 * Copyright(C) 2008-2013, Openmind S.r.l. http://www.openmindonline.it
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

package net.sourceforge.openutils.mgnlmedia.media.tree;

import info.magnolia.module.admininterface.AdminTreeMVCHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * Extends {@link AdminTreeMVCHandler} to include custom js and to route activate command to activateMedia command
 * @author molaschi
 * @version $Id$
 */
public class MediaModuleTreeMVCHandler extends AdminTreeMVCHandler
{

    /**
     * @param name
     * @param request
     * @param response
     */
    public MediaModuleTreeMVCHandler(String name, HttpServletRequest request, HttpServletResponse response)
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
            + "/.resources/media/js/media.js\"><!-- --></script>");
        html.append(" <!--[if lte IE 6]><script type=\"text/javascript\" src=\""
            + this.getRequest().getContextPath()
            + "/.resources/media/js/tree-ie6.js\"></script><![endif]-->");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTreeClass()
    {
        return MediaModuleTree.class.getName();
    }

}
