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

/**
 * Copyright Openmind http://www.openmindonline.it
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package it.openutils.mgnltasks;

import info.magnolia.cms.core.MgnlNodeType;
import info.magnolia.jcr.util.NodeUtil;
import info.magnolia.module.InstallContext;
import info.magnolia.module.delta.AbstractRepositoryTask;
import info.magnolia.module.delta.TaskExecutionException;
import info.magnolia.repository.RepositoryConstants;

import javax.jcr.Node;
import javax.jcr.RepositoryException;


/**
 * A task that updates the current version of a module stored in the config repository.
 * @author fgiust
 * @version $Id$
 */
public class UpdateModuleVersionTask extends AbstractRepositoryTask
{

    public UpdateModuleVersionTask()
    {
        super("Version number", "Sets installed module version number");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doExecute(InstallContext ctx) throws RepositoryException, TaskExecutionException
    {
        Node modules = NodeUtil.createPath(
            ctx.getJCRSession(RepositoryConstants.CONFIG).getRootNode(),
            "modules",
            MgnlNodeType.NT_CONTENT);

        Node moduleNode = NodeUtil.createPath(
            modules,
            ctx.getCurrentModuleDefinition().getName(),
            MgnlNodeType.NT_CONTENT);

        moduleNode.setProperty("version", ctx.getCurrentModuleDefinition().getVersion().toString());
    }
}