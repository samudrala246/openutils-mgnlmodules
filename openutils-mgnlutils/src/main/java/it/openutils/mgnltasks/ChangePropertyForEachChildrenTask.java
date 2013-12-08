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

import info.magnolia.jcr.util.NodeUtil;
import info.magnolia.module.InstallContext;
import info.magnolia.module.delta.AbstractRepositoryTask;
import info.magnolia.module.delta.TaskExecutionException;
import it.openutils.mgnlutils.util.NodeUtilsExt;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Change a nodedata value for all the children of a given node.
 * @author fgiust
 * @version $Id$
 */
public class ChangePropertyForEachChildrenTask extends AbstractRepositoryTask
{

    private final String workspaceName;

    private final String nodePath;

    private final String propertyName;

    private final Object newPropertyValue;

    private final Object previousPropertyValue;

    private Logger log = LoggerFactory.getLogger(ChangePropertyForEachChildrenTask.class);

    public ChangePropertyForEachChildrenTask(
        String workspaceName,
        String nodePath,
        String propertyName,
        Object previousPropertyValue,
        Object newPropertyValue)
    {
        super("Checking the value of " + nodePath + "/*/" + propertyName, "Creating property "
            + nodePath
            + "/*/"
            + propertyName
            + " and setting its value to "
            + newPropertyValue
            + " if the previous value is "
            + previousPropertyValue);
        this.workspaceName = workspaceName;
        this.nodePath = nodePath;
        this.propertyName = propertyName;
        this.previousPropertyValue = previousPropertyValue;
        this.newPropertyValue = newPropertyValue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doExecute(InstallContext ctx) throws RepositoryException, TaskExecutionException
    {
        Session hm = ctx.getJCRSession(workspaceName);

        Node parentnode = NodeUtilsExt.getNodeIfExists(hm, nodePath);
        if (parentnode == null)
        {
            log.info("Node {} not found, nothing to do", nodePath);
            return;
        }

        Iterable<Node> children = NodeUtil.getNodes(parentnode, NodeUtil.EXCLUDE_META_DATA_FILTER);

        for (Node node : children)
        {

            NodeUtilsExt.setPropertyIfDifferentFromValue(node, propertyName, newPropertyValue, previousPropertyValue);

        }
    }

}
