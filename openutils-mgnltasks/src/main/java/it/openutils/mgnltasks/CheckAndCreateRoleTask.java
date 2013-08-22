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

import info.magnolia.cms.beans.config.ContentRepository;
import info.magnolia.cms.core.HierarchyManager;
import info.magnolia.module.InstallContext;
import info.magnolia.module.delta.AbstractRepositoryTask;
import info.magnolia.module.delta.BootstrapSingleResource;
import info.magnolia.module.delta.Task;
import info.magnolia.module.delta.TaskExecutionException;

import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;


/**
 * Creates a role if not already existing.
 * @author fgiust
 * @version $Id$
 */
public class CheckAndCreateRoleTask extends AbstractRepositoryTask implements Task
{

    private String role;

    private String bootstrapFile;

    /**
     * @param role role name
     * @param bootstrapFile bootstrap file used to create the role
     */
    public CheckAndCreateRoleTask(String role, String bootstrapFile)
    {
        super("Checking " + role, "Checking " + role);
        this.role = role;
        this.bootstrapFile = bootstrapFile;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doExecute(InstallContext installContext) throws RepositoryException, TaskExecutionException
    {

        HierarchyManager hm = installContext.getHierarchyManager(ContentRepository.USER_ROLES);

        try
        {
            hm.getContent(role);
        }
        catch (PathNotFoundException e)
        {

            BootstrapSingleResource bsr = new BootstrapSingleResource(
                "creating role " + role,
                "creating role " + role,
                bootstrapFile);
            bsr.execute(installContext);
        }
    }
}
