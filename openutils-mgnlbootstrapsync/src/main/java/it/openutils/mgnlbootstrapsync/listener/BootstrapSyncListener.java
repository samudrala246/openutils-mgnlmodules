/**
 *
 * BootstrapSync for Magnolia CMS (http://www.openmindlab.com/lab/products/bootstrapsync.html)
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

package it.openutils.mgnlbootstrapsync.listener;

import info.magnolia.cms.beans.config.ContentRepository;
import info.magnolia.cms.security.AccessDeniedException;
import it.openutils.mgnlbootstrapsync.watch.BootstrapSyncRepositoryWatch;

import java.util.ArrayList;
import java.util.List;

import javax.jcr.RepositoryException;
import javax.jcr.observation.Event;
import javax.jcr.observation.EventIterator;

import org.apache.commons.lang.StringUtils;
import org.apache.jackrabbit.core.observation.EventImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author luca boati mmolaschi
 * @version $Id: $
 */
public class BootstrapSyncListener extends AbstractBootstrapSyncListener
{

    /**
     * Store operation
     * @author mmolaschi
     * @version $Id: $
     */
    public class JcrOperation
    {

        private String path;

        private boolean remove;

        /**
         * @param path path of operation
         * @param remove is a remove op?
         */
        public JcrOperation(String path, boolean remove)
        {
            this.path = path;
            this.remove = remove;
        }
    }

    private static Logger log = LoggerFactory.getLogger(BootstrapSyncListener.class);

    /**
     * Configure listener on watch
     * @param watch watch
     */
    public BootstrapSyncListener(BootstrapSyncRepositoryWatch watch)
    {
        super(watch);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onEvent(EventIterator events)
    {

        // @todo Do we need it yet?
        try
        {
            if (!ContentRepository.checkIfInitialized())
            {
                return;
            }
        }
        catch (AccessDeniedException e)
        {
            return;
        }
        catch (RepositoryException e)
        {
            return;
        }

        // if (!ConfigLoader.isConfigured() || ConfigLoader.isBootstrapping())
        // {
        // return;
        // }

        List<JcrOperation> operations = new ArrayList<JcrOperation>();
        while (events.hasNext())
        {
            // Event event
            EventImpl event = (EventImpl) events.nextEvent();
            String eventPath = null;
            try
            {
                eventPath = event.getPath();
            }
            catch (RepositoryException ex)
            {
                log.error(ex.getMessage(), ex);
                continue;
            }
            if (eventPath.startsWith("/jcr:"))
            {
                return;
            }

            switch (event.getType())
            {
                case Event.NODE_ADDED :
                    log.debug(eventPath + " added");
                    break;
                case Event.NODE_REMOVED :
                    log.debug(eventPath + " removed");
                    break;
                case Event.PROPERTY_ADDED :
                    log.debug(eventPath + " added");
                    break;
                case Event.PROPERTY_CHANGED :
                    log.debug(eventPath + " changed");
                    break;
                case Event.PROPERTY_REMOVED :
                    log.debug(eventPath + " removed");
                    break;
                default :
                    break;
            }

            if (event.getType() == Event.NODE_REMOVED)
            {
                operations.add(new JcrOperation(eventPath, true));
            }
            else
            {
                operations.add(new JcrOperation(eventPath, false));
            }
        }

        // calculate minimum path and remove operation
        String basePath = null;
        JcrOperation remove = null;
        for (JcrOperation operation : operations)
        {
            if (operation.remove)
            {
                remove = operation;
                continue;
            }

            int basePathSl = StringUtils.countMatches(basePath, "/");
            int pathSl = StringUtils.countMatches(operation.path, "/");

            if (basePath == null || pathSl < basePathSl)
            {
                basePath = operation.path;
            }
        }

        if (remove != null)
        {
            if (log.isDebugEnabled())
            {
                log.debug("Removed PATH: " + remove.path);
            }

            removeNode(remove.path);
        }

        if (basePath != null)
        {
            // remove metadata if present
            basePath = StringUtils.substringBefore(basePath, "/MetaData");

            if (log.isDebugEnabled())
            {
                log.debug("Changed PATH: " + basePath);
            }

            exportNode(basePath);
        }
    }

}
