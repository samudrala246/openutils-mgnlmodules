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
import info.magnolia.cms.core.NodeData;
import info.magnolia.cms.util.ContentUtil;
import info.magnolia.module.InstallContext;
import info.magnolia.module.delta.AbstractRepositoryTask;
import info.magnolia.module.delta.TaskExecutionException;
import info.magnolia.repository.RepositoryConstants;

import java.util.List;

import javax.jcr.RepositoryException;


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

        HierarchyManager hm = installContext.getHierarchyManager(RepositoryConstants.CONFIG);

        if (hm.isExist(subscribersPath))
        {

            Content subscribersNode = hm.getContent(subscribersPath);
            List<Content> subscribers = ContentUtil.collectAllChildren(subscribersNode);

            for (Content content : subscribers)
            {
                NodeData nd = content.getNodeData("active");
                if (nd.getBoolean())
                {
                    nd.setValue(false);
                }

            }
        }
    }

}
