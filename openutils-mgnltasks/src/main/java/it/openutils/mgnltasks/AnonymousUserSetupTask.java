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

import info.magnolia.cms.beans.config.ContentRepository;
import info.magnolia.cms.core.Content;
import info.magnolia.cms.core.HierarchyManager;
import info.magnolia.cms.security.AccessDeniedException;
import info.magnolia.cms.security.Permission;
import info.magnolia.cms.security.UserManager;
import info.magnolia.cms.util.NodeDataUtil;
import info.magnolia.module.InstallContext;
import info.magnolia.module.delta.AbstractRepositoryTask;
import info.magnolia.module.delta.Task;
import info.magnolia.module.delta.TaskExecutionException;

import java.util.Collection;

import javax.jcr.RepositoryException;


/**
 * A task that can be used to add or remove read only access to the anonymous user (for an easy admin/public switch).
 * @author fgiust
 * @version $Id$
 */
public class AnonymousUserSetupTask extends AbstractRepositoryTask implements Task
{

    private boolean allowAccess;

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

        HierarchyManager hm = installContext.getHierarchyManager(ContentRepository.USER_ROLES);

        Content role = hm.getContent("/" + UserManager.ANONYMOUS_USER);

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
    private void setupAcl(Content role, String repository, String path, long newpermissions)
        throws RepositoryException, AccessDeniedException
    {
        Content acls = role.getChildByName("acl_" + repository);

        Collection<Content> children = acls.getChildren();

        boolean found = false;
        for (Content acl : children)
        {
            String aclPath = acl.getNodeData("path").getString();
            if (path.equals(aclPath))
            {
                found = true;

                long permissions = acl.getNodeData("permissions").getLong();
                if (permissions != newpermissions)
                {
                    NodeDataUtil.getOrCreate(acl, "permissions").setValue(newpermissions);
                }
            }
        }
        if (!found)
        {
            log.warn("Security not configured on anonymous user! No acl for {} found on {}", path, repository);
        }
    }

}
