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

import info.magnolia.importexport.BootstrapUtil;
import info.magnolia.module.InstallContext;
import info.magnolia.module.delta.BootstrapResourcesTask;
import info.magnolia.module.delta.TaskExecutionException;
import info.magnolia.repository.RepositoryConstants;
import it.openutils.mgnlutils.api.NodeUtilsExt;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.jcr.ImportUUIDBehavior;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * A task to bootstrap a module. Can be used to fully re-bootstrap a custom module after an update (useful to reload
 * templates, dialogs, etc). This task will always ignore bootstrap files for the <code>website</code> repository.
 * <strong> Please note that during an update this task will always delete the following nodes inside the affected
 * module configuration: "dialogs", "templates", "paragraphs", "virtualURIMapping" </strong>
 * @author fgiust
 * @version $Id$
 */
public class ModuleConfigBootstrapTask extends BootstrapResourcesTask
{

    /**
     * Logger.
     */
    private Logger log = LoggerFactory.getLogger(ModuleConfigBootstrapTask.class);

    protected String modulename;

    protected Set<String> includedRepositories = new HashSet<String>();

    protected String[] resourcesToBootstrap;

    public ModuleConfigBootstrapTask(String modulename)
    {
        super("Bootstrap", "Bootstraps module configuration for " + modulename + " (config repository only).");
        this.modulename = modulename;
        includedRepositories.add("config");
    }

    @Deprecated
    public ModuleConfigBootstrapTask(String modulename, List<String> excludeRepositories)
    {
        super("Bootstrap", "Bootstraps module configuration for " + modulename + " (will not overwrite website!).");
        this.modulename = modulename;

        log
            .warn("\n\n****************************\n"
                + "ModuleConfigBootstrapTask has changed in openutils-mgnltasks 4.1\n"
                + "By default only the config repository is bootstrapped, and you need to specificy *additional* repositories only if needed."
                + "The constructor with List<String> (excluded repositories) is now deprecated, you can use the default one or the one with the\n"
                + "Set<String> incleded repositories parameter\n"
                + "****************************\n");

    }

    public ModuleConfigBootstrapTask(String modulename, Set<String> includedRepositories)
    {
        super("Bootstrap", "Bootstraps module configuration for "
            + modulename
            + " (will bootstrap config and "
            + includedRepositories
            + " repositories).");
        this.modulename = modulename;

        this.includedRepositories.addAll(includedRepositories);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean acceptResource(InstallContext ctx, String name)
    {

        boolean accept = false;

        for (String repository : includedRepositories)
        {
            if (name.startsWith("/mgnl-bootstrap/" + modulename + "/" + repository + "."))
            {
                accept = true;
                break;
            }
        }

        if (accept)
        {
            log.debug("Importing file {}", name);
        }
        return accept;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(final InstallContext installContext) throws TaskExecutionException
    {
        try
        {
            resourcesToBootstrap = getResourcesToBootstrap(installContext);

            long millis = System.currentTimeMillis();
            log.info("{} bootstrap starting...", modulename);

            deleteNode(installContext, "/modules/" + modulename + "/dialogs");
            deleteNode(installContext, "/modules/" + modulename + "/templates");
            deleteNode(installContext, "/modules/" + modulename + "/paragraphs");
            deleteNode(installContext, "/modules/" + modulename + "/virtualURIMapping");

            List<String> filteredResources = new ArrayList<String>();
            for (String name : resourcesToBootstrap)
            {
                if (!skipResource(name))
                {
                    filteredResources.add(name);
                }
            }
            BootstrapUtil.bootstrap(
                filteredResources.toArray(new String[0]),
                ImportUUIDBehavior.IMPORT_UUID_COLLISION_REMOVE_EXISTING);
            log.info("{} bootstrap done in {} seconds", modulename, (System.currentTimeMillis() - millis) / 1000);
        }
        catch (IOException e)
        {
            throw new TaskExecutionException("Could not bootstrap: " + e.getMessage());
        }
        catch (RepositoryException e)
        {
            throw new TaskExecutionException("Could not bootstrap: " + e.getMessage());
        }
    }

    /**
     * Skips a resource. Subclasses may override this method to avoid the import of some resources.
     * @param name
     * @return
     */
    protected boolean skipResource(String name)
    {
        return false;
    }

    /**
     * Deletes a node.
     * @param installContext install context
     * @param nodePath node path
     * @throws RepositoryException for any exception wile operating on the repository
     */
    protected void deleteNode(InstallContext installContext, String nodePath) throws RepositoryException
    {

        Session hm = installContext.getJCRSession(RepositoryConstants.CONFIG);

        if (NodeUtilsExt.deleteIfExisting(hm, nodePath))
        {
            log.warn("Deleted node {}", nodePath);
        }
    }
}