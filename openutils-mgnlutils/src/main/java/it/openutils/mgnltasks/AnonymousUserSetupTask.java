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

import info.magnolia.cms.core.MgnlNodeType;
import info.magnolia.cms.security.AccessDeniedException;
import info.magnolia.cms.security.Permission;
import info.magnolia.cms.security.UserManager;
import info.magnolia.jcr.util.NodeUtil;
import info.magnolia.module.InstallContext;
import info.magnolia.module.delta.AbstractRepositoryTask;
import info.magnolia.module.delta.Task;
import info.magnolia.module.delta.TaskExecutionException;
import info.magnolia.repository.RepositoryConstants;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * A task that can be used to add or remove read only access to the anonymous user (for an easy admin/public switch).
 * @author fgiust
 * @version $Id$
 */
public class AnonymousUserSetupTask extends AbstractRepositoryTask implements Task
{

    private boolean allowAccess;

    private Logger log = LoggerFactory.getLogger(AnonymousUserSetupTask.class);

    /**
     * @param allowAccess true to add access to anonymous users, false to remove it
     */
    public AnonymousUserSetupTask(boolean allowAccess)
    {
        super("Setup anonymous user", allowAccess
            ? "Adding access to anonymous user"
            : "Removing access to anonymous user");
        this.allowAccess = allowAccess;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doExecute(InstallContext installContext) throws RepositoryException, TaskExecutionException
    {

        Session hm = installContext.getJCRSession(RepositoryConstants.USER_ROLES);

        Node role = hm.getNode("/" + UserManager.ANONYMOUS_USER);

        setupAcl(role, "website", "/*", this.allowAccess ? Permission.READ : Permission.NONE);
        setupAcl(role, "uri", "/*", this.allowAccess ? Permission.ALL : Permission.NONE);
        setupAcl(role, "uri", "/.magnolia*", Permission.NONE);
    }

    /**
     * @param role
     * @param repository
     * @param newpermissions
     * @throws RepositoryException
     * @throws AccessDeniedException
     */
    private void setupAcl(Node role, String repository, String path, long newpermissions) throws RepositoryException,
        AccessDeniedException
    {
        Node acls = NodeUtil.createPath(role, "acl_" + repository, MgnlNodeType.NT_CONTENTNODE);

        Iterable<Node> children = NodeUtil.getNodes(acls, MgnlNodeType.NT_CONTENTNODE);

        boolean found = false;
        for (Node acl : children)
        {
            if (!acl.hasProperty("path"))
            {
                continue;
            }

            String aclPath = acl.getProperty("path").getString();
            if (path.equals(aclPath))
            {
                found = true;

                long permissions = acl.getProperty("permissions").getLong();
                if (permissions != newpermissions)
                {
                    acl.setProperty("permissions", newpermissions);
                }
            }
        }

        if (!found && StringUtils.equals(path, "/*"))
        {
            // handle a change in URI ACLs, previously set to "/*", then to "*"
            setupAcl(role, repository, "*", newpermissions);
        }

        if (!found)
        {
            log.warn("Security not configured on anonymous user! No acl for {} found on {}", path, repository);
        }

    }

}
