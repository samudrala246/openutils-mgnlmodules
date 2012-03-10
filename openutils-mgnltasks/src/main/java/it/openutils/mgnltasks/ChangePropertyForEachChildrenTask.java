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

import info.magnolia.cms.core.Content;
import info.magnolia.cms.core.HierarchyManager;
import info.magnolia.cms.core.Content.ContentFilter;
import info.magnolia.cms.util.ContentUtil;
import info.magnolia.cms.util.NodeDataUtil;
import info.magnolia.module.InstallContext;
import info.magnolia.module.delta.AbstractRepositoryTask;
import info.magnolia.module.delta.TaskExecutionException;

import java.util.Collection;

import javax.jcr.RepositoryException;

import org.apache.commons.lang.StringUtils;


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
        final HierarchyManager hm = ctx.getHierarchyManager(workspaceName);

        final Content parentnode = ContentUtil.createPath(hm, nodePath, false);

        Collection<Content> children = parentnode.getChildren(new ContentFilter()
        {

            public boolean accept(Content content)
            {
                return true;
            }
        });
        for (Content node : children)
        {
            if (node.hasNodeData(propertyName) || previousPropertyValue == null)
            {
                String currentvalue = node.getNodeData(propertyName).getString();
                if (previousPropertyValue != null
                    && StringUtils.equals(currentvalue, previousPropertyValue.toString())
                    || !StringUtils.equals(currentvalue, newPropertyValue.toString()))
                {
                    NodeDataUtil.getOrCreateAndSet(node, propertyName, newPropertyValue);
                }
            }
        }
    }

}
