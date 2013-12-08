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

package it.openutils.mgnltasks;

import info.magnolia.module.InstallContext;
import info.magnolia.module.delta.AbstractRepositoryTask;
import info.magnolia.module.delta.BootstrapSingleResource;
import info.magnolia.module.delta.Task;
import info.magnolia.module.delta.TaskExecutionException;
import info.magnolia.repository.RepositoryConstants;

import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;


/**
 * Creates a user if not already existing.
 * @author fgiust
 * @version $Id$
 */
public class CheckAndCreateUserTask extends AbstractRepositoryTask implements Task
{

    private String user;

    private String bootstrapFile;

    /**
     * @param user User path (e.g. /system/superuser)
     * @param bootstrapFile bootstrap file used to create the user
     */
    public CheckAndCreateUserTask(String user, String bootstrapFile)
    {
        super("Checking " + user, "Checking " + user);
        this.user = user;
        this.bootstrapFile = bootstrapFile;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doExecute(InstallContext installContext) throws RepositoryException, TaskExecutionException
    {

        Session hm = installContext.getJCRSession(RepositoryConstants.USERS);

        try
        {
            hm.getNode(user);
        }
        catch (PathNotFoundException e)
        {

            BootstrapSingleResource bsr = new BootstrapSingleResource(
                "creating user " + user,
                "creating user " + user,
                bootstrapFile);
            bsr.execute(installContext);
        }
    }
}
