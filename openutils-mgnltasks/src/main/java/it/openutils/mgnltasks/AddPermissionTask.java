/**
 *
 * Tasks for for Magnolia CMS (http://www.openmindlab.com/lab/products/mgnltasks.html)
 * Copyright(C) 2008-2012, Openmind S.r.l. http://www.openmindonline.it
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
import info.magnolia.cms.core.Path;
import info.magnolia.cms.security.AccessDeniedException;
import info.magnolia.cms.util.NodeDataUtil;
import info.magnolia.module.InstallContext;
import info.magnolia.module.delta.AbstractRepositoryTask;
import info.magnolia.module.delta.Task;
import info.magnolia.module.delta.TaskExecutionException;

import java.util.Collection;

import javax.jcr.RepositoryException;


/**
 * Sets or add permissions on a repository:path for a given role.
 * @author fgiust
 * @version $Id$
 */
public class AddPermissionTask extends AbstractRepositoryTask implements Task
{

    private final String role;

    private final String repo;

    private final String path;

    private final long permission;

    public AddPermissionTask(String role, String repo, String path, long permission)
    {
        super("Setup permissions on " + repo + ":" + path + " for " + role, "Setup permissions on "
            + repo
            + ":"
            + path
            + " for "
            + role);
        this.role = role;
        this.repo = repo;
        this.path = path;
        this.permission = permission;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doExecute(InstallContext installContext) throws RepositoryException, TaskExecutionException
    {

        HierarchyManager hm = installContext.getHierarchyManager(ContentRepository.USER_ROLES);

        Content roleNode = hm.getContent("/" + role);

        setupAcl(roleNode, repo, path, permission);
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

        if (acls == null)
        {
            acls = role.createContent("acl_" + repository, "mgnl:contentNode");
        }

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
                    setPermission(acl, path, newpermissions);
                }
            }
        }
        if (!found)
        {
            Content acl = acls.createContent(Path.getUniqueLabel(acls, "0"), "mgnl:contentNode");
            setPermission(acl, path, newpermissions);

        }
    }

    /**
     * @param acl
     * @param newpermissions
     * @throws RepositoryException
     * @throws AccessDeniedException
     */
    private void setPermission(Content acl, String path, long newpermissions) throws RepositoryException,
        AccessDeniedException
    {
        log.info("Setting permissions for {} to {}", this.repo + ":" + path, this.role);
        NodeDataUtil.getOrCreate(acl, "path").setValue(path);
        NodeDataUtil.getOrCreate(acl, "permissions").setValue(newpermissions);
    }
}
