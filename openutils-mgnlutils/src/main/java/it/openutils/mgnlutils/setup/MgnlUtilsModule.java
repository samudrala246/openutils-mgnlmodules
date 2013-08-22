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

package it.openutils.mgnlutils.setup;

import info.magnolia.module.ModuleLifecycle;
import info.magnolia.module.ModuleLifecycleContext;
import it.openutils.mgnlutils.el.NodeElResolver;

import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.servlet.jsp.JspFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author fgiust
 * @version $Id$
 */
public class MgnlUtilsModule implements ModuleLifecycle
{

    @Inject
    private ServletContext servletContext;

    private Logger log = LoggerFactory.getLogger(MgnlUtilsModule.class);

    /**
     * {@inheritDoc}
     */
    public void start(ModuleLifecycleContext moduleLifecycleContext)
    {
        try
        {
            JspFactory.getDefaultFactory().getJspApplicationContext(servletContext).addELResolver(new NodeElResolver());
            log.info("EL resolver for javax.jcr.Node added");
        }
        catch (IllegalStateException e)
        {
            // ignore, this means the module have been reloaded, but the webapp is already initialized
        }
    }

    /**
     * {@inheritDoc}
     */
    public void stop(ModuleLifecycleContext moduleLifecycleContext)
    {
        // nothing to do

    }

}
