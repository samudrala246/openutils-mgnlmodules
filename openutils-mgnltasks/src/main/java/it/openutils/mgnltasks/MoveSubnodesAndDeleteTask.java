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

import info.magnolia.cms.core.MgnlNodeType;
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
 * Move children of a node and deletes it(if existing).
 * @author fgiust
 * @version $Id$
 */
public class MoveSubnodesAndDeleteTask extends AbstractRepositoryTask
{

    private String workspaceName;

    private String from;

    private String warning;

    private Logger log = LoggerFactory.getLogger(MoveSubnodesAndDeleteTask.class);

    private String to;

    /**
     * @param workspaceName workspace (for example "config")
     * @param from source node path
     * @param from destination node path
     * @param warning optional warning message which will be shown if the node to move actually exists
     */
    public MoveSubnodesAndDeleteTask(String workspaceName, String from, String to, String warning)
    {
        super("Moving configuration from " + from + " to " + to, "Moving configuration from " + from + " to " + to);
        this.workspaceName = workspaceName;
        this.from = from;
        this.to = to;
        this.warning = warning;
    }

    @Override
    protected void doExecute(InstallContext installContext) throws RepositoryException, TaskExecutionException
    {

        Session session = installContext.getJCRSession(workspaceName);

        if (NodeUtilsExt.exists(session, from))
        {
            if (warning != null)
            {
                installContext.warn(warning);
            }

            Node fromnode = session.getNode(from);

            Node toNode = null;

            if (!NodeUtilsExt.exists(session, to))
            {
                toNode = NodeUtil.createPath(session.getRootNode(), to, MgnlNodeType.NT_CONTENT);
            }
            else
            {
                toNode = session.getNode(to);
            }

            Iterable<Node> nodes = NodeUtil.getNodes(fromnode, NodeUtil.EXCLUDE_META_DATA_FILTER);

            for (Node node : nodes)
            {
                if (!toNode.hasNode(node.getName()))
                {
                    node.getSession().move(node.getPath(), toNode.getPath() + "/" + node.getName());
                }
            }

            NodeUtilsExt.deleteIfExisting(session, from);
        }
    }

}
