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

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;


/**
 * @author molaschi
 * @version $Id$
 */
public class SetNodeOrderTask extends AbstractRepositoryTask
{

    private String repository;

    private String parentNode;

    private String[] nodesOrder;

    /**
     * @param repository repository
     * @param parentNode parent node handle
     * @param nodesOrder ordered node names
     */
    public SetNodeOrderTask(String repository, String parentNode, String[] nodesOrder)
    {
        super("Set order in node " + parentNode, "Set order in node " + parentNode);
        this.repository = repository;
        this.parentNode = parentNode;
        this.nodesOrder = nodesOrder;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    protected void doExecute(InstallContext installContext) throws RepositoryException, TaskExecutionException
    {
        Session session = installContext.getJCRSession(repository);

        Node parent = session.getNode(parentNode);
        List<Node> children = NodeUtil.asList(NodeUtil.getNodes(parent, NodeUtil.EXCLUDE_META_DATA_FILTER));

        final List<String> orderedList = Arrays.asList(nodesOrder);

        Collections.sort(children, new Comparator<Node>()
        {

            public int compare(Node o1, Node o2)
            {
                Integer index1 = orderedList.indexOf(NodeUtil.getName(o1));
                Integer index2 = orderedList.indexOf(NodeUtil.getName(o2));
                if (index1 < 0)
                {
                    index1 = Integer.MAX_VALUE;
                }
                if (index2 < 0)
                {
                    index2 = Integer.MAX_VALUE;
                }

                return index2.compareTo(index1);
            }
        });

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
