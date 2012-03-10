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

package net.sourceforge.openutils.mgnlrules.module;

import info.magnolia.module.ModuleLifecycle;
import info.magnolia.module.ModuleLifecycleContext;
import net.sourceforge.openutils.mgnlrules.configuration.ExpressionFunctionManager;
import net.sourceforge.openutils.mgnlrules.configuration.ExpressionLibraryManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author dschivo
 * @version $Id$
 */
public class RuleModule implements ModuleLifecycle
{

    /**
     * Logger.
     */
    private Logger log = LoggerFactory.getLogger(RuleModule.class);

    /**
     * {@inheritDoc}
     */
    public void start(ModuleLifecycleContext moduleLifecycleContext)
    {
        log.info("Starting module expressions");

        // Enable support for EL functions in expression editor
        System.setProperty("javax.servlet.jsp.functions.allowed", "true");

        moduleLifecycleContext.registerModuleObservingComponent("expression-libraries", ExpressionLibraryManager
            .getInstance());
        moduleLifecycleContext.registerModuleObservingComponent("expression-functions", ExpressionFunctionManager
            .getInstance());
    }

    /**
     * {@inheritDoc}
     */
    public void stop(ModuleLifecycleContext moduleLifecycleContext)
    {
        log.info("Stopping module expressions");
    }

}
