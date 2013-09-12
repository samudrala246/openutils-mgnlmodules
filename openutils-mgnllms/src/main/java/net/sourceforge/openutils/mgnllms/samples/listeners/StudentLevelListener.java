/**
 *
 * E-learning Module for Magnolia CMS (http://www.openmindlab.com/lab/products/lms.html)
 * Copyright(C) 2010-2011, Openmind S.r.l. http://www.openmindonline.it
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

package net.sourceforge.openutils.mgnllms.samples.listeners;

import info.magnolia.cms.beans.config.ContentRepository;
import info.magnolia.cms.core.Content;
import info.magnolia.cms.core.ItemType;
import info.magnolia.cms.security.ACLImpl;
import info.magnolia.cms.security.AccessDeniedException;
import info.magnolia.cms.security.Permission;
import info.magnolia.cms.security.PermissionImpl;
import info.magnolia.cms.security.User;
import info.magnolia.cms.security.auth.ACL;
import info.magnolia.cms.security.auth.PrincipalCollection;
import info.magnolia.cms.util.ContentUtil;
import info.magnolia.cms.util.NodeDataUtil;
import info.magnolia.cms.util.SimpleUrlPattern;
import info.magnolia.cms.util.UrlPattern;
import info.magnolia.context.MgnlContext;
import info.magnolia.context.MgnlContext.VoidOp;
import info.magnolia.repository.RepositoryConstants;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Iterator;

import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;

import net.sourceforge.openutils.mgnllms.listeners.EmptyCourseEventListener;
import net.sourceforge.openutils.mgnllms.module.LMSModule;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author carlo
 * @version $Id: $
 */
public class StudentLevelListener extends EmptyCourseEventListener
{

    /**
     * Logger.
     */
    private Logger log = LoggerFactory.getLogger(StudentLevelListener.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCourseSatisfied(String learnerId, String courseId)
    {
        Content course = ContentUtil.getContentByUUID(LMSModule.REPO, courseId);
        boolean levelComplete = true;
        try
        {
            for (Content siblingCourse : course.getParent().getChildren(course.getItemType()))
            {
                if (!course.getUUID().equals(siblingCourse.getUUID()))
                {
                    if (siblingCourse.hasContent(LMSModule.USERS_NODEDATA)
                        && siblingCourse.getContent(LMSModule.USERS_NODEDATA).hasContent(learnerId)
                        && siblingCourse.getContent(LMSModule.USERS_NODEDATA).getContent(learnerId).hasNodeData(
                            LMSModule.SATISFIED)
                        && NodeDataUtil.getBoolean(siblingCourse.getContent(LMSModule.USERS_NODEDATA).getContent(
                            learnerId), LMSModule.SATISFIED, false))
                    {
                        levelComplete = true;

                    }
                    else
                    {
                        levelComplete = false;
                        break;
                    }
                }
            }

            if (levelComplete)
            {
                course = MgnlContext.getSystemContext().getHierarchyManager(LMSModule.REPO).getContentByUUID(courseId);
                for (Content role : course.getParent().getChildren(new ExactContentFilter(ItemType.CONTENT)))
                {
                    try
                    {
                        addRole(role.getName());
                    }
                    catch (RepositoryException ex)
                    {
                        // go on
                    }
                }
            }

        }
        catch (AccessDeniedException e)
        {
            log.error("AccessDeniedException {}", e);
        }
        catch (PathNotFoundException e)
        {
            log.error("PathNotFoundException {}", e);
        }
        catch (RepositoryException e)
        {
            log.error("RepositoryException {}", e);
        }
    }

    @SuppressWarnings("unchecked")
    protected void addRole(String role) throws RepositoryException
    {
        PrincipalCollection principalList = null;

        for (Principal p : MgnlContext.getSubject().getPrincipals())
        {
            if (p instanceof PrincipalCollection)
            {
                principalList = (PrincipalCollection) p;
                break;
            }
        }

        Content node = MgnlContext
            .getSystemContext()
            .getHierarchyManager(RepositoryConstants.USER_ROLES)
            .getContent(role);

        Iterator it = node.getChildren(ItemType.CONTENTNODE.getSystemName(), "acl*").iterator();
        while (it.hasNext())
        {
            Content aclEntry = (Content) it.next();
            String name = StringUtils.substringAfter(aclEntry.getName(), "acl_");
            ACL acl;
            String repositoryName;
            String workspaceName;
            if (!StringUtils.contains(name, "_"))
            {
                repositoryName = name;
                workspaceName = ContentRepository.getDefaultWorkspace(name);
                name += ("_" + workspaceName); // default workspace must be added to the name
            }
            else
            {
                String[] tokens = StringUtils.split(name, "_");
                repositoryName = tokens[0];
                workspaceName = tokens[1];
            }
            // get the existing acl object if created before with some
            // other role
            if (!principalList.contains(name))
            {
                acl = new ACLImpl(name, new ArrayList<Permission>());
                principalList.add(acl);
            }
            else
            {
                acl = (ACL) principalList.get(name);
            }
            // acl.setRepository(repositoryName);
            // acl.setWorkspace(workspaceName);

            // add acl
            Iterator permissionIterator = aclEntry.getChildren().iterator();
            while (permissionIterator.hasNext())
            {
                Content map = (Content) permissionIterator.next();
                String path = map.getNodeData("path").getString();
                UrlPattern p = new SimpleUrlPattern(path);
                Permission permission = new PermissionImpl();
                permission.setPattern(p);
                permission.setPermissions(map.getNodeData("permissions").getLong());
                acl.getList().add(permission);
            }
        }

        final String finalRole = role;
        final User user = MgnlContext.getUser();
        MgnlContext.doInSystemContext(new VoidOp()
        {

            /**
             * {@inheritDoc}
             */
            @Override
            public void doExec()
            {
                user.addRole(finalRole);
            }

        });
    }

}