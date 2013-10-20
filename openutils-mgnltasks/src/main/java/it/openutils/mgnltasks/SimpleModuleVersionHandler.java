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

import info.magnolia.init.MagnoliaConfigurationProperties;
import info.magnolia.module.DefaultModuleVersionHandler;
import info.magnolia.module.InstallContext;
import info.magnolia.module.delta.Delta;
import info.magnolia.module.delta.DeltaBuilder;
import info.magnolia.module.model.Version;
import info.magnolia.objectfactory.Components;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * <p>
 * A base module version handler that re-bootstrap its configuration each time the version number changes. Can be used
 * as is or subclasses (usually overriding getStartupTasks() for configuration tweaks).
 * </p>
 * <p>
 * All the bootstrap files placed in "/mgnl-bootstrap/(modulename)" will be reloaded every time the version number
 * changes, while xml files placed in "/mgnl-bootstrap/(modulename)-nooverwrite" will be bootstrapped only if the node
 * is not already existing in the repository.
 * </p>
 * @author fgiust
 * @version $Id$
 */
public class SimpleModuleVersionHandler extends DefaultModuleVersionHandler
{

    /**
     * Logger.
     */
    protected Logger log = LoggerFactory.getLogger(getClass());

    /**
     * Repositories to include in bootstrap.
     */
    protected Set<String> includedRepositoriesInBootstrap = new HashSet<String>();

    /**
     *
     */
    public SimpleModuleVersionHandler()
    {
        super();
        includedRepositoriesInBootstrap.add("config");
    }

    /**
     * Add a repository to bootstrap *inclusion* list
     * @param repository repository to include in bootstrap
     */
    public void addIncludedRepositoryInmBootstrap(String repository)
    {
        includedRepositoriesInBootstrap.add(repository);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Delta> getDeltas(InstallContext ctx, Version from)
    {
        if (from == null)
        {
            log.info("Actual version not set");
            List<Delta> deltas = super.getDeltas(ctx, from);

            // adding the (module)-nooverwrite in addiction to the standard bootstrap directory
            deltas
                .get(0)
                .getTasks()
                .add(new BootstrapMissingNodesTask(ctx.getCurrentModuleDefinition().getName() + "-nooverwrite"));

            // only for development, add the (module)-dev bootstrap directory
            if (Components.getComponent(MagnoliaConfigurationProperties.class).getBooleanProperty("magnolia.develop"))
            {
                if (StringUtils.isEmpty(Components.getComponent(MagnoliaConfigurationProperties.class).getProperty(
                    "magnolia.bootstrapdev"))
                    || StringUtils.contains(
                        Components.getComponent(MagnoliaConfigurationProperties.class).getProperty(
                            "magnolia.bootstrapdev"),
                        ctx.getCurrentModuleDefinition().getName()))
                {
                    deltas
                        .get(0)
                        .getTasks()
                        .add(new BootstrapMissingNodesTask(ctx.getCurrentModuleDefinition().getName() + "-dev"));
                }
            }

            return deltas;
        }

        String modulename = ctx.getCurrentModuleDefinition().getName();

        Version to = ctx.getCurrentModuleDefinition().getVersion();

        List<Delta> deltas = new ArrayList<Delta>();

        if (Components.getComponent(MagnoliaConfigurationProperties.class).getBooleanProperty(
            modulename + ".update.disabled"))
        {
            return deltas;
        }

        if (!to.isEquivalent(from)
            || "${project.version}".equals(ObjectUtils.toString(from))
            || "SNAPSHOT".equals(from.getClassifier()))
        {
            log.info("Updating from version {}", from);

            Delta delta = DeltaBuilder.update(to, "Update to current version");
            addModuleConfigBootstrapTasks(modulename, delta);
            delta.getTasks().add(new BootstrapMissingNodesTask(modulename + "-nooverwrite"));

            delta.getTasks().add(new UpdateModuleVersionTask());

            deltas.add(delta);
        }
        else
        {
            log.info("Version {} already installed, no update tasks to run", from);
        }

        return deltas;
    }

    /**
     * Deletes any existing module configuration node (templates, paragraphs, virtualURI and dialogs) before reimporting
     * all the content of the bootstrap folder, to avoid any leftover or problems with renamed items. Subclasses may
     * override this method to implement a more sophisticated logic for bootstrapping module configuration.
     * @param modulename
     * @param delta
     */
    protected void addModuleConfigBootstrapTasks(String modulename, Delta delta)
    {
        delta.getTasks().add(new ModuleConfigBootstrapTask(modulename, includedRepositoriesInBootstrap));
    }

    protected boolean samplesEnabled()
    {
        return Components.getComponent(MagnoliaConfigurationProperties.class).getBooleanProperty(
            "magnolia.bootstrap.samples");
    }
}
