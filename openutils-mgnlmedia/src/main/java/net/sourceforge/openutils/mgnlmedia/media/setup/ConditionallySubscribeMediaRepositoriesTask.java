/**
 *
 * SimpleMedia Module for Magnolia CMS (http://www.openmindlab.com/lab/products/media.html)
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

package net.sourceforge.openutils.mgnlmedia.media.setup;

import info.magnolia.cms.core.MgnlNodeType;
import info.magnolia.cms.exchange.ActivationManager;
import info.magnolia.cms.exchange.ActivationManagerFactory;
import info.magnolia.cms.exchange.Subscriber;
import info.magnolia.context.MgnlContext;
import info.magnolia.jcr.util.PropertyUtil;
import info.magnolia.module.InstallContext;
import info.magnolia.module.delta.AbstractTask;
import info.magnolia.module.delta.TaskExecutionException;
import info.magnolia.module.model.ModuleDefinition;
import info.magnolia.module.model.RepositoryDefinition;
import info.magnolia.repository.RepositoryConstants;

import java.util.Collection;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.RepositoryException;


/**
 * Adds a subscription for the media repositories only if the single-instance flag is not set.
 * @author fgiust
 * @version $Id$
 */
public class ConditionallySubscribeMediaRepositoriesTask extends AbstractTask
{

    public ConditionallySubscribeMediaRepositoriesTask()
    {
        super("Register workspaces for activation", "Register workspaces for activation.");
    }

    /**
     * {@inheritDoc}
     */
    public void execute(InstallContext ctx) throws TaskExecutionException
    {

        // check for the sigleinstance flag directly in jcr, the module is not started yet
        boolean singleinstance = false;
        try
        {
            Node moduleConfigNode = ctx.getConfigJCRSession().getNode("/modules/media/config");
            if (moduleConfigNode != null)
            {
                singleinstance = PropertyUtil.getBoolean(moduleConfigNode, "singleinstance", false);
            }
            if (singleinstance)
            {
                return;
            }

            final ModuleDefinition def = ctx.getCurrentModuleDefinition();

            Collection<RepositoryDefinition> repositories = def.getRepositories();
            for (RepositoryDefinition repDef : repositories)
            {
                List<String> workspaces = repDef.getWorkspaces();
                for (final String workspace : workspaces)
                {
                    subscribeRepository(workspace);
                }
            }
        }
        catch (RepositoryException re)
        {
            throw new TaskExecutionException("wasn't able to config singleinstance", re);
        }

    }

    /**
     * Register the repository to get used for activation.
     */
    private void subscribeRepository(String repository) throws TaskExecutionException
    {
        ActivationManager sManager = ActivationManagerFactory.getActivationManager();
        Collection<Subscriber> subscribers = sManager.getSubscribers();
        for (Subscriber subscriber : subscribers)
        {
            if (!subscriber.isSubscribed("/", repository))
            {
                try
                {
                    Node subscriptionsNode = MgnlContext.getJCRSession(RepositoryConstants.CONFIG).getNode(
                        "/server/activation/subscribers/" + subscriber.getName() + "/subscriptions");

                    Node newSubscription = subscriptionsNode.addNode(repository, MgnlNodeType.NT_CONTENTNODE);
                    newSubscription.setProperty("toURI", "/");
                    newSubscription.setProperty("repository", repository);
                    newSubscription.setProperty("fromURI", "/");
                }
                catch (RepositoryException re)
                {
                    throw new TaskExecutionException("wasn't able to subscribe repository [" + repository + "]", re);
                }
            }
        }
    }

}
