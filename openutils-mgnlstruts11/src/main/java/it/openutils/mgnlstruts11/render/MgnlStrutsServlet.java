/**
 *
 * Struts 1.1 module for Magnolia CMS (http://www.openmindlab.com/lab/products/mgnlstruts.html)
 * Copyright(C) ${project.inceptionYear}-2012, Openmind S.r.l. http://www.openmindonline.it
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

package it.openutils.mgnlstruts11.render;

import it.openutils.mgnlstruts11.process.MgnlRequestProcessor;
import it.openutils.mgnlstruts11.process.MgnlStrutsUtils;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.apache.struts.action.ActionServlet;
import org.apache.struts.action.RequestProcessor;
import org.apache.struts.config.ModuleConfig;


/**
 * An action servet extension, needed for action discovery.
 * @author fgiust
 * @version $Id$
 */
public class MgnlStrutsServlet extends ActionServlet
{

    /**
     * Stable serialVersionUID.
     */
    private static final long serialVersionUID = 42L;

    public static String DISPATCHER_KEY = MgnlStrutsServlet.class.getName();

    /**
     * {@inheritDoc}
     */
    @Override
    public void init(ServletConfig config) throws ServletException
    {
        super.init(config);
        config.getServletContext().setAttribute(DISPATCHER_KEY, this);
    }

    /**
     * Look up and return the {@link RequestProcessor} responsible for the specified module, creating a new one if
     * necessary.
     * @param config The module configuration for which to acquire and return a RequestProcessor.
     * @exception ServletException if we cannot instantiate a RequestProcessor instance
     * @since Struts 1.1
     */
    @Override
    protected synchronized RequestProcessor getRequestProcessor(ModuleConfig config) throws ServletException
    {
        return MgnlStrutsUtils.getRequestProcessor(config, this.getServletContext(), this, MgnlRequestProcessor.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected ModuleConfig initModuleConfig(String prefix, String paths) throws ServletException
    {
        ModuleConfig config = super.initModuleConfig(prefix, paths);
        StrutsParagraphRegister.parseParagraphs(config);
        return config;
    }

}
