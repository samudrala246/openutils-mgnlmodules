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
import info.magnolia.cms.core.ItemType;
import info.magnolia.cms.core.MgnlNodeType;
import info.magnolia.cms.util.ContentUtil;
import info.magnolia.module.InstallContext;
import info.magnolia.module.delta.AbstractRepositoryTask;
import info.magnolia.module.delta.TaskExecutionException;
import info.magnolia.repository.RepositoryConstants;

import java.util.Collection;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Adds a default repository permissions for all the configured users, only if there is no acl set for the given
 * repository.
 * @author fgiust
 * @version $Id$
 */
public class CreateDefaultRepositoryAclForAllUsersTask extends AbstractRepositoryTask
{

    private static Logger log = LoggerFactory.getLogger(CreateDefaultRepositoryAclForAllUsersTask.class);

    private String repository;

    private long permissions;

    public CreateDefaultRepositoryAclForAllUsersTask(String repository, long permissions)
    {
        super("Adding permissions on " + repository + " repository", "Adding permissions on "
            + repository
            + " repository");
        this.repository = repository;
        this.permissions = permissions;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    protected void doExecute(InstallContext ctx) throws RepositoryException, TaskExecutionException
    {
        Session hm = ctx.getJCRSession(RepositoryConstants.USER_ROLES);
        final Node parentNode = hm.getNode("/");

        final Collection<Content> childNodes = ContentUtil.collectAllChildren(parentNode, ItemType.ROLE);

        for (Content content : childNodes)
        {
            operateOnChildNode(content, ctx);
        }
    }

    /**
     * @param node Node
     * @param ctx Context
     * @throws RepositoryException for any exception wile operating on the repository
     */
    protected void operateOnChildNode(Content node, InstallContext ctx) throws RepositoryException
    {

        String aclpath = "acl_" + repository;

        if (!node.hasContent(aclpath))
        {
            log.info("adding permissions on {} to role {}", repository, node.getName());

            Content aclnode = node.createContent(aclpath, MgnlNodeType.NT_CONTENTNODE);
            Content permNode = aclnode.createContent("0", MgnlNodeType.NT_CONTENTNODE);
            permNode.createNodeData("path", "/*");
            permNode.createNodeData("permissions", new Long(permissions));
        }
    }

}
