/**
 *
 * Repository tools for Magnolia CMS (http://www.openmindlab.com/lab/products/repotools.html)
 * Copyright(C) 2009-2013, Openmind S.r.l. http://www.openmindonline.it
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

package net.sourceforge.openutils.mgnlrepoutils;

import info.magnolia.cms.beans.config.ContentRepository;
import info.magnolia.cms.util.AlertUtil;
import info.magnolia.context.MgnlContext;
import info.magnolia.module.admininterface.TemplatedMVCHandler;
import info.magnolia.objectfactory.Components;
import info.magnolia.repository.RepositoryManager;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;

import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.jackrabbit.core.WorkspaceImpl;
import org.apache.jackrabbit.core.persistence.PersistenceManager;
import org.apache.jackrabbit.core.query.QueryManagerImpl;
import org.apache.jackrabbit.core.query.lucene.IndexUtils;
import org.apache.jackrabbit.core.query.lucene.SearchIndex;


/**
 * @author fgiust
 * @version $Id$
 */
public class RepositoryUtilsPage extends TemplatedMVCHandler
{

    private String[] repos;

    /**
     * @param name
     * @param request
     * @param response
     */
    public RepositoryUtilsPage(String name, HttpServletRequest request, HttpServletResponse response)
    {
        super(name, request, response);
    }

    /**
     * Returns the repos.
     * @return the repos
     */
    public String[] getRepos()
    {
        return repos;
    }

    /**
     * Sets the repos.
     * @param repos the repos to set
     */
    public void setRepos(String[] repos)
    {
        this.repos = repos;
    }

    public Iterator<String> getRepositories()
    {
        return Components.getComponent(RepositoryManager.class).getWorkspaceNames().iterator();
    }

    public String doIndexConsistencyFix() throws Exception
    {

        if (repos == null)
        {
            AlertUtil.setMessage("Please select one or more workspaces");
            return this.show();
        }

        for (String repo : repos)
        {
            IndexUtils iu = new IndexUtils(getSearchIndex(repo));
            iu.runConsistencyCheck(true);
        }
        AlertUtil.setMessage("Index consistency check done on " + ArrayUtils.toString(repos));
        return this.show();
    }

    public String doPmCheck() throws Exception
    {
        if (repos == null)
        {
            AlertUtil.setMessage("Please select one or more workspaces");
            return this.show();
        }
        for (String repo : repos)
        {
            PersistenceManager pm = getPersistenceManager(repo);
            pm.checkConsistency(null, true, true);
        }
        AlertUtil.setMessage("Persistence manager check done on " + ArrayUtils.toString(repos));
        return this.show();
    }

    private Object getPropertyUsingReflection(Object object, String property) throws NoSuchMethodException,
        IllegalAccessException, InvocationTargetException
    {
        Method method = object
            .getClass()
            .getDeclaredMethod("get" + StringUtils.capitalize(property), new Class< ? >[0]);
        method.setAccessible(true);
        return method.invoke(object, new Object[0]);
    }

    private SearchIndex getSearchIndex(String repo) throws RepositoryException, NoSuchMethodException,
        IllegalAccessException, InvocationTargetException
    {
        WorkspaceImpl workspace = (WorkspaceImpl) MgnlContext.getHierarchyManager(repo).getWorkspace();

        QueryManagerImpl queryManager = (QueryManagerImpl) workspace.getQueryManager();

        SearchIndex searchIndex = (SearchIndex) getPropertyUsingReflection(queryManager, "queryHandler");
        return searchIndex;
    }

    private PersistenceManager getPersistenceManager(String repo) throws Exception
    {
        WorkspaceImpl workspace = (WorkspaceImpl) MgnlContext.getHierarchyManager(repo).getWorkspace();
        Repository repository = workspace.getSession().getRepository();

        String workspaceName = ContentRepository.getMappedWorkspaceName(repo);
        return getPM(repository, workspaceName);
    }

    private PersistenceManager getPM(Repository repository, String workspaceName) throws Exception
    {
        if (workspaceName == null || workspaceName.equals("version"))
        {
            Object versionManager = findMethodByName(repository, "getVersionManager", null);

            Field f = versionManager.getClass().getDeclaredField("pMgr");
            f.setAccessible(true);
            return (PersistenceManager) f.get(versionManager);
        }

        Object workspaceInfo = findMethodByName(repository, "getWorkspaceInfo", new Object[]{workspaceName });
        return (PersistenceManager) findMethodByName(workspaceInfo, "getPersistenceManager", null);
    }

    private Object findMethodByName(Object obj, String name, Object[] parameters) throws Exception
    {
        Method m = null;
        Method[] ms = obj.getClass().getDeclaredMethods();
        for (int i = 0; i < ms.length; i++)
        {
            final Method x = ms[i];
            if (x.getName().equals(name))
            {
                m = x;
                break;
            }
        }
        m.setAccessible(true);
        return m.invoke(obj, parameters);
    }

}
