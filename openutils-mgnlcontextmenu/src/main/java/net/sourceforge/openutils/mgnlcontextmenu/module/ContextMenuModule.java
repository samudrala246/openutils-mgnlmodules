/**
 *
 * ContextMenu Module for Magnolia CMS (http://www.openmindlab.com/lab/products/mgnlcontextmenu.html)
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

package net.sourceforge.openutils.mgnlcontextmenu.module;

import info.magnolia.module.ModuleLifecycle;
import info.magnolia.module.ModuleLifecycleContext;
import net.sourceforge.openutils.mgnlcontextmenu.configuration.ContextMenuManager;
import net.sourceforge.openutils.mgnlcontextmenu.configuration.GetGlobalEntriesNodeStrategy;
import net.sourceforge.openutils.mgnlcontextmenu.configuration.PersistenceStrategy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author dschivo
 */
public class ContextMenuModule implements ModuleLifecycle
{

    /**
     * Logger.
     */
    private Logger log = LoggerFactory.getLogger(ContextMenuModule.class);

    private GetGlobalEntriesNodeStrategy getGlobalEntriesNodeStrategy;

    private PersistenceStrategy persistenceStrategy;

    /**
     * Returns the getGlobalEntriesNodeStrategy.
     * @return the getGlobalEntriesNodeStrategy
     */
    public GetGlobalEntriesNodeStrategy getGetGlobalEntriesNodeStrategy()
    {
        return getGlobalEntriesNodeStrategy;
    }

    /**
     * Sets the getGlobalEntriesNodeStrategy.
     * @param getGlobalEntriesNodeStrategy the getGlobalEntriesNodeStrategy to set
     */
    public void setGetGlobalEntriesNodeStrategy(GetGlobalEntriesNodeStrategy getGlobalEntriesNodeStrategy)
    {
        this.getGlobalEntriesNodeStrategy = getGlobalEntriesNodeStrategy;
    }

    /**
     * Returns the persistenceStrategy.
     * @return the persistenceStrategy
     */
    public PersistenceStrategy getPersistenceStrategy()
    {
        return persistenceStrategy;
    }

    /**
     * Sets the persistenceStrategy.
     * @param persistenceStrategy the persistenceStrategy to set
     */
    public void setPersistenceStrategy(PersistenceStrategy persistenceStrategy)
    {
        this.persistenceStrategy = persistenceStrategy;
    }

    /**
     * {@inheritDoc}
     */
    public void start(ModuleLifecycleContext moduleLifecycleContext)
    {
        log.info("Starting module contextmenu");

        moduleLifecycleContext.registerModuleObservingComponent("contextMenus", ContextMenuManager.getInstance());
    }

    /**
     * {@inheritDoc}
     */
    public void stop(ModuleLifecycleContext moduleLifecycleContext)
    {
        log.info("Stopping module contextmenu");
    }

}
