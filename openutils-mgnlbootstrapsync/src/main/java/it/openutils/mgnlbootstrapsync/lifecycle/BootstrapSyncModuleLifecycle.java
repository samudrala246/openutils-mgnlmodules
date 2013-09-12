/**
 *
 * BootstrapSync for Magnolia CMS (http://www.openmindlab.com/lab/products/bootstrapsync.html)
 * Copyright(C) ${project.inceptionYear}-2013, Openmind S.r.l. http://www.openmindonline.it
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

package it.openutils.mgnlbootstrapsync.lifecycle;

import info.magnolia.cms.core.HierarchyManager;
import info.magnolia.cms.core.ItemType;
import info.magnolia.cms.core.SystemProperty;
import info.magnolia.context.MgnlContext;
import info.magnolia.module.ModuleLifecycle;
import info.magnolia.module.ModuleLifecycleContext;
import info.magnolia.repository.RepositoryConstants;
import it.openutils.mgnlbootstrapsync.listener.AbstractBootstrapSyncListener;
import it.openutils.mgnlbootstrapsync.listener.BootstrapSyncListener;
import it.openutils.mgnlbootstrapsync.watch.BootstrapSyncRepositoryWatch;

import java.util.HashMap;
import java.util.Map;

import javax.jcr.RepositoryException;
import javax.jcr.observation.Event;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author molaschi
 * @version $Id: $
 */
public class BootstrapSyncModuleLifecycle implements ModuleLifecycle
{

    /**
     * Logger.
     */
    private Logger log = LoggerFactory.getLogger(BootstrapSyncModuleLifecycle.class);

    private Map<String, AbstractBootstrapSyncListener> listeners = new HashMap<String, AbstractBootstrapSyncListener>();

    /**
     * {@inheritDoc}
     */
    public void start(ModuleLifecycleContext moduleLifecycleContext)
    {
        // is develop?
        boolean develop = SystemProperty.getBooleanProperty("magnolia.develop");
        // bootstrap sync enabled?
        boolean bootstrapSync = SystemProperty.getBooleanProperty("magnolia.bootstrapSync");

        if (develop && bootstrapSync)
        {
            // get all repositories on which activate bootstrap sync
            String[] repositories = StringUtils.split(
                SystemProperty.getProperty("magnolia.bootstrapSync.repositories"),
                ",");

            // cycle
            for (String repository : repositories)
            {
                // get synchronization property for each repository

                // get path to export to
                String exportPath = SystemProperty.getProperty("magnolia.bootstrapSync." + repository + ".exportPath");

                // get roots to export (node with all children)
                String exportRoots = SystemProperty
                    .getProperty("magnolia.bootstrapSync." + repository + ".exportRoots");

                // get roots from which to export (one file for each node (ordering problem))
                String enableRoots = SystemProperty
                    .getProperty("magnolia.bootstrapSync." + repository + ".enablePaths");

                // get node type to export
                String nodeType = null;// SystemProperty.getProperty("magnolia.bootstrapSync." + repository +
                // ".nodeType");
                if (nodeType == null)
                {
                    // set default nodeType
                    nodeType = ItemType.CONTENT.getSystemName();
                    if (RepositoryConstants.USERS.equalsIgnoreCase(repository))
                    {
                        nodeType = ItemType.USER.getSystemName();
                    }
                    else if (RepositoryConstants.USER_ROLES.equalsIgnoreCase(repository))
                    {
                        nodeType = ItemType.ROLE.getSystemName();
                    }
                    else if (RepositoryConstants.USER_GROUPS.equalsIgnoreCase(repository))
                    {
                        nodeType = ItemType.GROUP.getSystemName();
                    }
                }

                // create watch
                BootstrapSyncRepositoryWatch watch = new BootstrapSyncRepositoryWatch(
                    repository,
                    exportPath,
                    exportRoots,
                    enableRoots,
                    nodeType);

                // create listener
                BootstrapSyncListener listener = new BootstrapSyncListener(watch);

                try
                {
                    HierarchyManager hm = MgnlContext.getSystemContext().getHierarchyManager(repository);

                    log.debug("Starting listener on repository {}", repository);
                    hm.getWorkspace().getObservationManager().addEventListener(
                        listener,
                        Event.NODE_ADDED
                            | Event.PROPERTY_CHANGED
                            | Event.PROPERTY_ADDED
                            | Event.PROPERTY_REMOVED
                            | Event.NODE_REMOVED,
                        "/",
                        true,
                        null,
                        null,
                        false);

                    // store
                    listeners.put(watch.getRepository(), listener);
                }
                catch (RepositoryException ex)
                {
                    log.error(ex.getMessage(), ex);
                    log.error("Stopping BootstrapSync Listeners");
                    stop(moduleLifecycleContext);
                    break;
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void stop(ModuleLifecycleContext moduleLifecycleContext)
    {
        for (Map.Entry<String, AbstractBootstrapSyncListener> entry : listeners.entrySet())
        {
            HierarchyManager hm = MgnlContext.getSystemContext().getHierarchyManager(entry.getKey());
            try
            {
                // stop listener
                log.debug("Stopping listener on repository {}", entry.getKey());
                hm.getWorkspace().getObservationManager().removeEventListener(entry.getValue());
            }
            catch (RepositoryException ex)
            {
                log.error(ex.getMessage(), ex);
            }
        }
    }

}
