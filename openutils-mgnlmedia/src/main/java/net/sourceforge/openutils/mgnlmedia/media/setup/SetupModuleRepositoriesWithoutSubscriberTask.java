/**
 *
 * SimpleMedia Module for Magnolia CMS (http://www.openmindlab.com/lab/products/media.html)
 * Copyright(C) 2008-2012, Openmind S.r.l. http://www.openmindonline.it
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

package net.sourceforge.openutils.mgnlmedia.media.setup;

import info.magnolia.cms.beans.config.ContentRepository;
import info.magnolia.cms.security.Permission;
import info.magnolia.cms.security.Role;
import info.magnolia.cms.security.Security;
import info.magnolia.importexport.Bootstrapper;
import info.magnolia.module.InstallContext;
import info.magnolia.module.delta.AbstractTask;
import info.magnolia.module.delta.TaskExecutionException;
import info.magnolia.module.model.ModuleDefinition;
import info.magnolia.module.model.RepositoryDefinition;

import java.util.Collection;
import java.util.List;


/**
 * A copy of SetupModuleRepositoriesTask without the activation subscription (not needed in case of single-instance
 * configuration.
 * @author fgiust
 * @version $Id$
 */
public class SetupModuleRepositoriesWithoutSubscriberTask extends AbstractTask
{

    public SetupModuleRepositoriesWithoutSubscriberTask()
    {
        super("Setup module repositories", "Bootstrap empty repositories and grant them to superuser.");
    }

    /**
     * {@inheritDoc}
     */
    public void execute(InstallContext ctx) throws TaskExecutionException
    {
        try
        {
            final ModuleDefinition def = ctx.getCurrentModuleDefinition();
            // register repositories
            Collection<RepositoryDefinition> repositories = def.getRepositories();
            for (RepositoryDefinition repDef : repositories)
            {
                List<String> workspaces = repDef.getWorkspaces();
                for (final String workspace : workspaces)
                {
                    // bootstrap the workspace if empty
                    if (!ContentRepository.checkIfInitialized(workspace))
                    {
                        final String[] bootstrapDirs = Bootstrapper.getBootstrapDirs();
                        Bootstrapper.bootstrapRepository(bootstrapDirs, workspace, new Bootstrapper.BootstrapFilter()
                        {

                            public boolean accept(String filename)
                            {
                                return filename.startsWith(workspace + ".");
                            }
                        });
                    }

                    grantRepositoryToSuperuser(workspace);
                }
            }
        }
        catch (Throwable e)
        {
            throw new TaskExecutionException("Could not bootstrap workspace: " + e.getMessage(), e);
        }

    }

    private void grantRepositoryToSuperuser(String workspace)
    {
        final Role superuser = Security.getRoleManager().getRole("superuser");
        superuser.addPermission(workspace, "/*", Permission.ALL);
    }

}
