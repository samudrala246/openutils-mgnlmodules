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
import info.magnolia.cms.core.Path;
import info.magnolia.cms.security.AccessDeniedException;
import info.magnolia.jcr.util.NodeUtil;
import info.magnolia.module.InstallContext;
import info.magnolia.module.delta.AbstractRepositoryTask;
import info.magnolia.module.delta.Task;
import info.magnolia.module.delta.TaskExecutionException;
import info.magnolia.repository.RepositoryConstants;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


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

    private Logger log = LoggerFactory.getLogger(AddPermissionTask.class);

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

        Session hm = installContext.getJCRSession(RepositoryConstants.USER_ROLES);

        Node roleNode = hm.getNode("/" + role);

        setupAcl(roleNode, repo, path, permission);
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
                    setPermission(acl, path, newpermissions);
                }
            }
        }
        if (!found)
        {
            Node acl = NodeUtil.createPath(
                acls,
                Path.getUniqueLabel(acls.getSession(), NodeUtil.getPathIfPossible(acls), "0"),
                MgnlNodeType.NT_CONTENTNODE);
            setPermission(acl, path, newpermissions);
        }
    }

    /**
     * @param acl
     * @param newpermissions
     * @throws RepositoryException
     * @throws AccessDeniedException
     */
    private void setPermission(Node acl, String path, long newpermissions) throws RepositoryException,
        AccessDeniedException
    {
        log.info("Setting permissions for {} to {}", this.repo + ":" + path, this.role);
        acl.setProperty("path", path);
        acl.setProperty("permissions", newpermissions);
    }
}
