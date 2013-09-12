/**
 *
 * Tasks for for Magnolia CMS (http://www.openmindlab.com/lab/products/mgnltasks.html)
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

package it.openutils.mgnltasks;

import info.magnolia.module.InstallContext;
import info.magnolia.module.delta.BootstrapResourcesTask;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Bootstrap all the files in a single directory, only if nodes are not already existing in the repository. The folder
 * must be available in the classpath into the "mgnl-bootstrap" directory.
 * @author fgiust
 * @version $Id$
 */
public class BootstrapMissingNodesTask extends BootstrapResourcesTask
{

    private String folderName;

    private Logger log = LoggerFactory.getLogger(AnonymousUserSetupTask.class);

    /**
     * Bootstrap all the files included in the /mgnl-bootstrap/(folder) directory, only if the nodes are not already
     * existing.
     * @param folder
     */
    public BootstrapMissingNodesTask(String folder)
    {
        super("Loading new content", "Bootstrap of new configuration in " + folder);

        this.folderName = folder;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean acceptResource(InstallContext installContext, String name)
    {
        boolean accept = name.startsWith("/mgnl-bootstrap/" + folderName + "/") && name.endsWith(".xml");

        if (accept)
        {
            String handle = StringUtils.substringBetween(name, "/mgnl-bootstrap/" + folderName + "/", ".xml");

            String workspace = StringUtils.substringBefore(handle, ".");
            handle = StringUtils.replace(StringUtils.substringAfter(handle, "."), ".", "/");

            try
            {
                Session session = installContext.getJCRSession(workspace);

                // handle is a relative path
                boolean alreadyExisting = session.getRootNode().hasNode(handle);

                if (!alreadyExisting)
                {
                    log.info("Loading {} since no content at {}:{} has been found", new Object[]{
                        name,
                        workspace,
                        "/" + handle });
                }
                return !alreadyExisting;
            }
            catch (RepositoryException e)
            {
                log.debug(e.getMessage(), e);
            }

        }
        return false;
    }

}
