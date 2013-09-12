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
import info.magnolia.module.delta.TaskExecutionException;
import it.openutils.mgnlutils.api.NodeUtilsExt;

import javax.jcr.RepositoryException;
import javax.jcr.Session;


/**
 * Deletes a node (if existing).
 * @author fgiust
 * @version $Id$
 */
public class DeleteNodeTask extends AbstractRepositoryTask
{

    private final String workspaceName;

    private final String nodePath;

    /**
     * @param workspaceName workspace (for example "config")
     * @param nodePath node path
     */
    public DeleteNodeTask(String workspaceName, String nodePath)
    {
        super("Deleting the node " + nodePath, "Deleting the node " + nodePath);
        this.workspaceName = workspaceName;
        this.nodePath = nodePath;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doExecute(InstallContext installContext) throws RepositoryException, TaskExecutionException
    {

        Session hm = installContext.getJCRSession(workspaceName);

        NodeUtilsExt.deleteIfExisting(hm, nodePath);
    }

}
