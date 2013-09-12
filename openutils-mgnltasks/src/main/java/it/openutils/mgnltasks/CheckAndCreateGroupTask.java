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
import info.magnolia.module.delta.AbstractRepositoryTask;
import info.magnolia.module.delta.BootstrapSingleResource;
import info.magnolia.module.delta.Task;
import info.magnolia.module.delta.TaskExecutionException;
import info.magnolia.repository.RepositoryConstants;

import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;


/**
 * Creates a group if not already existing.
 * @author fgiust
 * @version $Id$
 */
public class CheckAndCreateGroupTask extends AbstractRepositoryTask implements Task
{

    private String group;

    private String bootstrapFile;

    /**
     * @param group group name
     * @param bootstrapFile bootstrap file used to create the group
     */
    public CheckAndCreateGroupTask(String group, String bootstrapFile)
    {
        super("Checking " + group, "Checking " + group);
        this.group = group;
        this.bootstrapFile = bootstrapFile;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doExecute(InstallContext installContext) throws RepositoryException, TaskExecutionException
    {

        Session session = installContext.getJCRSession(RepositoryConstants.USER_GROUPS);

        try
        {
            session.getRootNode().getNode(group);
        }
        catch (PathNotFoundException e)
        {

            BootstrapSingleResource bsr = new BootstrapSingleResource("creating group " + group, "creating group "
                + group, bootstrapFile);
            bsr.execute(installContext);
        }
    }
}
