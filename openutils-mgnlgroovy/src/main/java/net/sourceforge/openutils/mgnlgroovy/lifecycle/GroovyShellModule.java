/**
 *
 * Groovy Shell for Magnolia CMS (http://www.openmindlab.com/lab/products/groovy.html)
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

package net.sourceforge.openutils.mgnlgroovy.lifecycle;

import groovy.lang.GroovySystem;
import info.magnolia.module.ModuleLifecycle;
import info.magnolia.module.ModuleLifecycleContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author fgrilli
 * @version $Id: GroovyShellModule.java 5020 2008-10-17 10:22:55Z federico.grilli $
 */
public class GroovyShellModule implements ModuleLifecycle
{

    private static Logger log = LoggerFactory.getLogger(GroovyShellModule.class);

    public void start(ModuleLifecycleContext moduleLifecycleContext)
    {
        log.info("starting GroovyShellModule. Groovy version is " + GroovySystem.getVersion());

    }

    public void stop(ModuleLifecycleContext moduleLifecycleContext)
    {
        log.info("stopping GroovyShellModule...");
    }
}
