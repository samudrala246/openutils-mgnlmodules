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

import info.magnolia.cms.core.Content;
import info.magnolia.cms.core.HierarchyManager;
import info.magnolia.cms.core.ItemType;
import info.magnolia.cms.util.ContentUtil;
import info.magnolia.module.InstallContext;
import info.magnolia.module.delta.AbstractRepositoryTask;
import info.magnolia.module.delta.TaskExecutionException;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.jcr.RepositoryException;


/**
 * Sorts a list of nodes alphabetically.
 * @author fgiust
 * @version $Id$
 */
public class NodeSortTask extends AbstractRepositoryTask
{

    private String repository;

    private String node;

    private String property;

    public NodeSortTask(String repository, String node, String property)
    {
        super("Reorder " + node, "Reorder " + node);
        this.repository = repository;
        this.node = node;
        this.property = property;
    }

    public NodeSortTask(String repository, String node)
    {
        this(repository, node, null);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    protected void doExecute(InstallContext ctx) throws RepositoryException, TaskExecutionException
    {
        HierarchyManager hm = ctx.getHierarchyManager(repository);

        Content parent = hm.getContent(node);
        List<Content> children = (List<Content>) ContentUtil.getAllChildren(parent);

        if (children.isEmpty())
        {
            children = (List<Content>) parent.getChildren(ItemType.CONTENTNODE);
        }

        if (this.property == null)
        {
            Collections.sort(children, new Comparator<Content>()
            {

                public int compare(Content o1, Content o2)
                {
                    return o2.getName().compareTo(o1.getName());
                }
            });
        }
        else
        {
            Collections.sort(children, new Comparator<Content>()
            {

                public int compare(Content o1, Content o2)
                {
                    return o2.getNodeData(property).getString().compareTo(o1.getNodeData(property).getString());
                }
            });
        }

        Content previous = null;

        for (Content content : children)
        {
            if (previous != null)
            {
                parent.orderBefore(content.getName(), previous.getName());
            }
            previous = content;
        }
    }
}
