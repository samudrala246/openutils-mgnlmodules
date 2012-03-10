/**
 *
 * Tasks for for Magnolia CMS (http://www.openmindlab.com/lab/products/mgnltasks.html)
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

package it.openutils.mgnltasks;

import info.magnolia.cms.core.HierarchyManager;
import info.magnolia.cms.util.NodeDataUtil;
import info.magnolia.module.InstallContext;
import info.magnolia.module.delta.AbstractTask;
import info.magnolia.module.delta.Task;
import info.magnolia.module.delta.TaskExecutionException;

import java.util.List;

import javax.jcr.RepositoryException;


/**
 * Base abstract task that executes a list of other tasks only if a specific node/nodedata doesn't already exists in the
 * repository. Subclasses must implement the <code>verifyCondition</code> method.
 * @author molaschi
 * @version $Id$
 */
public abstract class ConditionalTask extends AbstractTask
{

    /**
     * Workspace to check.
     */
    protected String workspace;

    /**
     * Handle to check.
     */
    protected String handle;

    /**
     * Subtasks that will be executed if the node doesn't exists.
     */
    protected List<Task> tasks;

    /**
     * @param taskName
     * @param taskDescription
     */
    public ConditionalTask(String workspace, String handle, List<Task> tasks)
    {
        super("Conditional task", "Execute task if exists " + handle + " in " + workspace);
        this.tasks = tasks;
        this.handle = handle;
        this.workspace = workspace;
    }

    /**
     * {@inheritDoc}
     */
    public void execute(InstallContext installContext) throws TaskExecutionException
    {
        HierarchyManager hm = installContext.getHierarchyManager(workspace);
        try
        {
            if (verifyCondition(hm, handle))
            {
                for (Task t : tasks)
                {
                    t.execute(installContext);
                }
            }
        }
        catch (RepositoryException e)
        {
            throw new TaskExecutionException(e.getMessage(), e);
        }
    }

    /**
     * Check if a node exists.
     * @param hm HieararchyManager
     * @param handle node handle
     * @return <code>true</code> if node exists
     */
    public boolean existsNode(HierarchyManager hm, String handle)
    {
        return hm.isExist(handle);
    }

    /**
     * Check if a nodedata exists.
     * @param hm HieararchyManager
     * @param handle node handle
     * @param nodedata nodedata name
     * @return <code>true</code> if nodedata exists
     * @throws RepositoryException exceptions while checking content
     */
    public boolean existsNodedata(HierarchyManager hm, String handle, String nodedata) throws RepositoryException
    {
        return hm.getContent(handle).hasNodeData(nodedata);
    }

    /**
     * Check if a nodedata exists with a specific value.
     * @param hm HieararchyManager
     * @param handle node handle
     * @param nodedata nodedata name
     * @param value expected value for nodeData
     * @return <code>true</code> if nodedata has the same value
     * @throws RepositoryException exceptions while checking content
     */
    public boolean nodeDataEquals(HierarchyManager hm, String handle, String nodedata, Object value)
        throws RepositoryException
    {
        return value.equals(NodeDataUtil.getValueObject(hm.getContent(handle).getNodeData(nodedata)));
    }

    /**
     * @param hm HieararchyManager
     * @param handle node handle
     * @return <code>true</code> if the task must be executed
     * @throws RepositoryException exceptions while checking content
     */
    public abstract boolean verifyCondition(HierarchyManager hm, String handle) throws RepositoryException;

}
