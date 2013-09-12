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

import info.magnolia.jcr.util.NodeUtil;
import info.magnolia.jcr.util.PropertyUtil;
import info.magnolia.module.InstallContext;
import info.magnolia.module.delta.AbstractRepositoryTask;
import info.magnolia.module.delta.TaskExecutionException;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.commons.lang.StringUtils;


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

        Session session = ctx.getJCRSession(repository);

        Node parent = session.getNode(node);
        List<Node> children = NodeUtil.asList(NodeUtil.getNodes(parent, NodeUtil.EXCLUDE_META_DATA_FILTER));

        if (this.property == null)
        {
            Collections.sort(children, new Comparator<Node>()
            {

                public int compare(Node o1, Node o2)
                {
                    return NodeUtil.getName(o2).compareTo(NodeUtil.getName(o1));
                }
            });
        }
        else
        {
            Collections.sort(children, new Comparator<Node>()
            {

                public int compare(Node o1, Node o2)
                {
                    return StringUtils.defaultString(PropertyUtil.getString(o2, property)).compareTo(
                        StringUtils.defaultString(PropertyUtil.getString(o1, property)));
                }
            });
        }

        Node previous = null;

        for (Node content : children)
        {
            if (previous != null)
            {
                parent.orderBefore(content.getName(), previous.getName());
            }
            previous = content;
        }
    }
}
