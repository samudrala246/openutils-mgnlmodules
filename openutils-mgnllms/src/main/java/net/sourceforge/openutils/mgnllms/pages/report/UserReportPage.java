/**
 *
 * E-learning Module for Magnolia CMS (http://www.openmindlab.com/lab/products/lms.html)
 * Copyright(C) 2010-2013, Openmind S.r.l. http://www.openmindonline.it
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
package net.sourceforge.openutils.mgnllms.pages.report;

import info.magnolia.cms.core.Content;
import info.magnolia.cms.core.ItemType;
import info.magnolia.cms.util.AlertUtil;
import info.magnolia.context.MgnlContext;
import info.magnolia.module.admininterface.TemplatedMVCHandler;

import java.util.ArrayList;
import java.util.List;

import javax.jcr.RepositoryException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.openutils.mgnllms.lms.exceptions.CourseException;
import net.sourceforge.openutils.mgnllms.module.LMSModule;
import net.sourceforge.openutils.mgnllms.report.UserReport;


/**
 * @author carlo
 * @version $Id: $
 */
public class UserReportPage extends TemplatedMVCHandler
{

    private String handle;

    private List<UserReport> results;

    private List<String> users;

    private String courseName;

    /**
     * @param name
     * @param request
     * @param response
     */
    public UserReportPage(String name, HttpServletRequest request, HttpServletResponse response)
    {
        super(name, request, response);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String show()
    {
        Content course = null;
        try
        {
            course = MgnlContext.getHierarchyManager(LMSModule.REPO).getContent(handle);
        }
        catch (RepositoryException ex)
        {
            AlertUtil.setException(ex);
            return super.show();
        }
        this.courseName = course.getTitle();

        // TODO elenco utenti
        // ciclo utenti e creo gli userreport
        // li memorizzo

        try
        {
            users = new ArrayList<String>();
            results = new ArrayList<UserReport>();
            for (Content user : (List<Content>) course.getContent("users").getChildren(ItemType.CONTENTNODE))
            {
                users.add(user.getName());
                results.add(LMSModule.getInstance().getLearnerActivitiesManager().getLearnerReport(
                    course.getUUID(),
                    user.getName()));

            }
        }

        catch (RepositoryException ex)
        {
            AlertUtil.setException(ex);
            return super.show();
        }
        catch (CourseException ex)
        {
            AlertUtil.setException(ex);
            return super.show();
        }

// LMSConfigurationModuleManager.getLearnerActivitiesManager().getLearnerReport(courseId, leanerId);

        return super.show();
    }

    /**
     * Returns the courseName.
     * @return the courseName
     */
    public String getCourseName()
    {
        return courseName;
    }

    /**
     * Returns the handle.
     * @return the handle
     */
    public String getHandle()
    {
        return handle;
    }

    /**
     * Sets the handle.
     * @param handle the handle to set
     */
    public void setHandle(String handle)
    {
        this.handle = handle;
    }

    /**
     * Returns the results.
     * @return the results
     */
    public List<UserReport> getResults()
    {
        return results;
    }

    /**
     * Sets the results.
     * @param results the results to set
     */
    public void setResults(List<UserReport> results)
    {
        this.results = results;
    }

    /**
     * Sets the courseName.
     * @param courseName the courseName to set
     */
    public void setCourseName(String courseName)
    {
        this.courseName = courseName;
    }

    /**
     * Returns the users.
     * @return the users
     */
    public List<String> getUsers()
    {
        return users;
    }

    /**
     * Sets the users.
     * @param users the users to set
     */
    public void setUsers(List<String> users)
    {
        this.users = users;
    }

}
