/**
 *
 * Messages Module for Magnolia CMS (http://www.openmindlab.com/lab/products/messages.html)
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

package net.sourceforge.openutils.mgnlmessages.lifecycle;

import info.magnolia.cms.i18n.MessagesManager;
import info.magnolia.module.ModuleLifecycle;
import info.magnolia.module.ModuleLifecycleContext;
import net.sourceforge.openutils.mgnlmessages.configuration.MessagesConfigurationManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author molaschi
 */
public class MessagesModuleLifecycle implements ModuleLifecycle
{

    /**
     *
     */
    public static final String REPO = "messages";

    private Logger log = LoggerFactory.getLogger(MessagesModuleLifecycle.class);

    /**
     * {@inheritDoc}
     */
    public void start(ModuleLifecycleContext ctx)
    {
        log.info("Starting module messages");
        ctx.registerModuleObservingComponent("locales", MessagesConfigurationManager.getInstance());

        MessagesManager.getInstance().init();
    }

    /**
     * {@inheritDoc}
     */
    public void stop(ModuleLifecycleContext ctx)
    {
        log.info("Stopping module messages");
    }

}
