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
import info.magnolia.jcr.util.PropertyUtil;
import info.magnolia.module.InstallContext;
import info.magnolia.module.delta.AbstractRepositoryTask;
import info.magnolia.module.delta.TaskExecutionException;
import info.magnolia.repository.RepositoryConstants;
import it.openutils.mgnlutils.util.NodeUtilsExt;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;


/**
 * Disable any configured subscriber.
 * @author fgiust
 * @version $Id$
 */
public class DisableSubscribersTask extends AbstractRepositoryTask
{

    private String subscribersPath = "/server/activation/subscribers";

    public DisableSubscribersTask()
    {
        super("Disabling subscribers", "Disabling subscribers");
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    protected void doExecute(InstallContext installContext) throws RepositoryException, TaskExecutionException
    {

        Session session = installContext.getJCRSession(RepositoryConstants.CONFIG);

        if (NodeUtilsExt.exists(session, subscribersPath))
        {

            Node subscribersNode = session.getNode(subscribersPath);

            Iterable<Node> subscribers = NodeUtil.getNodes(subscribersNode, NodeUtil.EXCLUDE_META_DATA_FILTER);

            for (Node node : subscribers)
            {
                if (PropertyUtil.getBoolean(node, "active", true))
                {
                    node.setProperty("active", false);
                }
            }
        }
    }

}
