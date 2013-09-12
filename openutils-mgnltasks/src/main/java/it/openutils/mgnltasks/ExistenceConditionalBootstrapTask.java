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

import info.magnolia.jcr.RuntimeRepositoryException;
import info.magnolia.module.InstallContext;
import info.magnolia.module.delta.BootstrapResourcesTask;
import it.openutils.mgnlutils.api.NodeUtilsExt;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Bootstraps a file only if a specific node doesn't already exists in the repository.
 * @author molaschi
 * @version $Id$
 */
public class ExistenceConditionalBootstrapTask extends BootstrapResourcesTask
{

    private String workspace;

    private String folderName;

    private Logger log = LoggerFactory.getLogger(AnonymousUserSetupTask.class);

    /**
     * @param name
     * @param description
     * @param importUUIDBehavior
     */
    public ExistenceConditionalBootstrapTask(String workspace, String folderName, int importUUIDBehavior)
    {
        super("ExistenceConditionalBootstrap", "ExistenceConditionalBootstrap", importUUIDBehavior);

        this.workspace = workspace;
        this.folderName = folderName;
    }

    /**
     * @param name
     * @param description
     */
    public ExistenceConditionalBootstrapTask(String workspace, String folderName)
    {
        super("Loading new content", "Bootstrap of new configuration in " + folderName);

        this.workspace = workspace;
        this.folderName = folderName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean acceptResource(InstallContext installContext, String name)
    {
        boolean accept = name.startsWith("/mgnl-bootstrap/" + folderName + "/" + workspace) && name.endsWith(".xml");

        if (accept)
        {
            String handle = StringUtils.substringAfter(name, "/mgnl-bootstrap/" + folderName + "/" + workspace + ".");
            handle = StringUtils.substringBeforeLast(handle, ".xml");
            handle = "/" + StringUtils.replace(handle, ".", "/");
            Session session;

            try
            {
                session = installContext.getJCRSession(workspace);
            }
            catch (RepositoryException e)
            {
                throw new RuntimeRepositoryException(e);
            }

            boolean alreadyExisting = NodeUtilsExt.exists(session, handle);

            if (!alreadyExisting)
            {
                log.info("Loading {} since no content at {}:{} has been found", new Object[]{name, workspace, handle });
            }
            return !alreadyExisting;
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String[] getResourcesToBootstrap(InstallContext installContext)
    {
        return super.getResourcesToBootstrap(installContext);
    }

}
