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
package net.sourceforge.openutils.mgnllms.pages.lms;

import info.magnolia.cms.core.Content;
import info.magnolia.cms.util.NodeDataUtil;
import info.magnolia.context.MgnlContext;
import info.magnolia.module.admininterface.PageMVCHandler;

import java.io.IOException;

import javax.jcr.RepositoryException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.openutils.mgnllms.lms.exceptions.CourseException;
import net.sourceforge.openutils.mgnllms.module.LMSConfigurationModuleManager;
import net.sourceforge.openutils.mgnllms.module.LMSModule;
import net.sourceforge.openutils.mgnllms.report.UserReport;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author carlo
 * @version $Id: $
 */
public class ScormCmiPage extends PageMVCHandler
{

    /**
     * Logger.
     */
    private Logger log = LoggerFactory.getLogger(ScormCmiPage.class);

    private String mgnlPath;

    private String repository;

    private String values;

    private String activityId;

    private String courseStatus;

    private String adldata;

    /**
     * @param name
     * @param request
     * @param response
     */
    public ScormCmiPage(String name, HttpServletRequest request, HttpServletResponse response)
    {
        super(name, request, response);
    }

    public String initialize()
    {
        Content course = null;
        try
        {
            course = MgnlContext.getHierarchyManager(LMSModule.REPO).getContent(this.mgnlPath);
        }
        catch (RepositoryException e)
        {
            throw new RuntimeException(e);
        }

        try
        {
            return LMSModule.getInstance().getScormCMIManager().initialize(
                course.getUUID(),
                this.activityId,
                LMSModule.getInstance().getLearnerManager().getCurrentLearner());
        }
        catch (CourseException e)
        {
            return getError(e);
        }
    }

    protected String getError(Throwable t)
    {
        return new StringBuffer()
            .append("{")
            .append("error:'")
            .append(t.getMessage())
            .append("'")
            .append("}")
            .toString();
    }

    public String commit()
    {
        Content course = null;
        try
        {
            course = MgnlContext.getHierarchyManager(LMSModule.REPO).getContent(this.mgnlPath);
        }
        catch (RepositoryException e)
        {
            throw new RuntimeException(e);
        }

        try
        {
            LMSModule.getInstance().getScormCMIManager().commit(
                course.getUUID(),
                this.activityId,
                LMSModule.getInstance().getLearnerManager().getCurrentLearner(),
                values,
                adldata);
            response.setStatus(200);
            return StringUtils.EMPTY;
        }
        catch (CourseException e)
        {
            response.setStatus(500);
            return getError(e);
        }
    }

    public String terminate()
    {
        Content course = null;
        try
        {
            course = MgnlContext.getHierarchyManager(LMSModule.REPO).getContent(this.mgnlPath);
        }
        catch (RepositoryException e)
        {
            throw new RuntimeException(e);
        }

        try
        {
            LMSModule.getInstance().getScormCMIManager().terminate(
                course.getUUID(),
                this.activityId,
                LMSModule.getInstance().getLearnerManager().getCurrentLearner(),
                values,
                adldata);
            response.setStatus(200);
            return StringUtils.EMPTY;
        }
        catch (CourseException e)
        {
            response.setStatus(500);
            log.error("Course Exception: {}", e);
            return getError(e);
        }
    }

    public String status()
    {
        Content course = null;
        try
        {
            course = MgnlContext.getHierarchyManager(LMSModule.REPO).getContent(this.mgnlPath);
        }
        catch (RepositoryException e)
        {
            throw new RuntimeException(e);
        }
        try
        {
            return LMSModule.getInstance().getLearnerActivitiesManager().getLearnerStatus(
                course.getUUID(),
                LMSModule.getInstance().getLearnerManager().getCurrentLearner());
        }
        catch (CourseException e)
        {
            return getError(e);
        }
    }

    public String setStatus()
    {
        Content course = null;
        try
        {
            course = MgnlContext.getHierarchyManager(LMSModule.REPO).getContent(this.mgnlPath);
        }
        catch (RepositoryException e)
        {
            throw new RuntimeException(e);
        }
        try
        {
            LMSModule.getInstance().getLearnerActivitiesManager().storeLearnerStatus(
                course.getUUID(),
                LMSModule.getInstance().getLearnerManager().getCurrentLearner(),
                courseStatus);

            if (!courseStatus.equals(StringUtils.EMPTY))
            {
                // switch in base alla modalità di verifica del completamento del corso:
                int completionMode = Integer.parseInt(NodeDataUtil
                    .getString(course, LMSModule.SATISFACTION_RULE, "100"));
                UserReport ur = LMSModule.getInstance().getLearnerActivitiesManager().getLearnerReport(
                    course.getUUID(),
                    LMSModule.getInstance().getLearnerManager().getCurrentLearner());
                switch (completionMode)
                {
                    // per attività viste
                    case 0 :
                        if (ur.getOrganizationPrimaryObjective().getProgressStatus()
                            && ur.getOrganizationPrimaryObjective().getSatisfiedStatus())
                        {
                            // Chiama il manager per "salvare" l'informazione che il corso è completato
                            LMSModule.getInstance().getLearnerActivitiesManager().onCourseSatisfied(
                                LMSModule.getInstance().getLearnerManager().getCurrentLearner(),
                                course.getUUID());
                            // Chiama i listener per notificare che il corso è completato
                            LMSConfigurationModuleManager.getInstance().onCourseSatisfied(
                                LMSModule.getInstance().getLearnerManager().getCurrentLearner(),
                                course.getUUID());
                        }
                        break;
                }
            }
            return StringUtils.EMPTY;
        }
        catch (CourseException e)
        {
            return getError(e);
        }

// case 1 :
//
// List<String> activitiesToVerify = new ArrayList<String>(); // mi arriva dalla configurazione
//
// for (TrackingModel tm : ur.getTrackingModel())
// {
// if (tm.getActivityAttemptCount() > 0
// && !tm.getActivityIsSuspended()
// && activitiesToVerify.contains(tm.getIdentifier()))
// {
// activitiesToVerify.remove(tm.getIdentifier());
// }
// }
//
// if (activitiesToVerify.size() == 0)
// {
// LMSConfigurationModuleManager.getLearnerActivitiesManager().onCourseCompletion(
// LMSConfigurationModuleManager.getLearnerManager().getCurrentLearner(),
// course.getUUID());
// // Chiama i listener per notificare che il corso è completato
// LMSConfigurationModuleManager.getInstance().onCourseCompletion(
// LMSConfigurationModuleManager.getLearnerManager().getCurrentLearner(),
// course.getUUID());
// }
//
// break;
// // per obbiettivi raggiunti
// case 2 :
//
// break;
// // per corso completato (completionstatus?)
    }

    /**
     * {@inheritDoc}
     */
    public void renderHtml(String view) throws IOException
    {
        super.getResponse().setContentType("application/json");
        super.getResponse().getWriter().write(view);
    }

    /**
     * Returns the mgnlPath.
     * @return the mgnlPath
     */
    public String getMgnlPath()
    {
        return mgnlPath;
    }

    /**
     * Sets the mgnlPath.
     * @param mgnlPath the mgnlPath to set
     */
    public void setMgnlPath(String mgnlPath)
    {
        this.mgnlPath = mgnlPath;
    }

    /**
     * Returns the repository.
     * @return the repository
     */
    public String getRepository()
    {
        return repository;
    }

    /**
     * Sets the repository.
     * @param repository the repository to set
     */
    public void setRepository(String repository)
    {
        this.repository = repository;
    }

    /**
     * Returns the values.
     * @return the values
     */
    public String getValues()
    {
        return values;
    }

    /**
     * Sets the values.
     * @param values the values to set
     */
    public void setValues(String values)
    {
        this.values = values;
    }

    /**
     * Returns the activityId.
     * @return the activityId
     */
    public String getActivityId()
    {
        return activityId;
    }

    /**
     * Sets the activityId.
     * @param activityId the activityId to set
     */
    public void setActivityId(String activityId)
    {
        this.activityId = activityId;
    }

    /**
     * Returns the courseStatus.
     * @return the courseStatus
     */
    public String getCourseStatus()
    {
        return courseStatus;
    }

    /**
     * Sets the courseStatus.
     * @param courseStatus the courseStatus to set
     */
    public void setCourseStatus(String courseStatus)
    {
        this.courseStatus = courseStatus;
    }

    /**
     * Returns the adldata.
     * @return the adldata
     */
    public String getAdldata()
    {
        return adldata;
    }

    /**
     * Sets the adldata.
     * @param adldata the adldata to set
     */
    public void setAdldata(String adldata)
    {
        this.adldata = adldata;
    }

}
