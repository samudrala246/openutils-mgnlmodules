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
package net.sourceforge.openutils.mgnllms.managers.impl;

import info.magnolia.cms.core.Content;
import info.magnolia.cms.core.ItemType;
import info.magnolia.cms.security.AccessDeniedException;
import info.magnolia.cms.util.ContentUtil;
import info.magnolia.cms.util.NodeDataUtil;
import info.magnolia.context.MgnlContext;

import java.util.ArrayList;
import java.util.Set;

import javax.jcr.RepositoryException;

import net.sf.json.JSONObject;
import net.sourceforge.openutils.mgnllms.lms.exceptions.CourseException;
import net.sourceforge.openutils.mgnllms.managers.LearnerActivitiesManager;
import net.sourceforge.openutils.mgnllms.module.LMSModule;
import net.sourceforge.openutils.mgnllms.pages.lms.ScormCmiPage;
import net.sourceforge.openutils.mgnllms.report.Helper;
import net.sourceforge.openutils.mgnllms.report.Objective;
import net.sourceforge.openutils.mgnllms.report.TrackingModel;
import net.sourceforge.openutils.mgnllms.report.UserReport;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author carlo
 * @version $Id: $
 */
public class MagnoliaLearnerActivitiesManager implements LearnerActivitiesManager
{

    /**
     * Logger.
     */
    private Logger log = LoggerFactory.getLogger(ScormCmiPage.class);

    /**
     * {@inheritDoc}
     */
    public String getLearnerStatus(String courseId, String learnerId) throws CourseException
    {
        Content course = null;
        try
        {
            course = MgnlContext.getHierarchyManager(LMSModule.REPO).getContentByUUID(courseId);

            if (course.hasContent(LMSModule.USERS_NODEDATA)
                && course.getContent(LMSModule.USERS_NODEDATA).hasContent(learnerId)
                && course.getContent(LMSModule.USERS_NODEDATA).getContent(learnerId).hasNodeData(
                    LMSModule.STATUS_NODEDATA))
            {
                return NodeDataUtil.getString(
                    course.getContent(LMSModule.USERS_NODEDATA).getContent(learnerId),
                    LMSModule.STATUS_NODEDATA);
            }
            else
            {
                return StringUtils.EMPTY;
            }
        }
        catch (RepositoryException e)
        {
            log.error("Course Exception: {}", e);
            throw new CourseException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void storeLearnerStatus(String courseId, String learnerId, String status) throws CourseException
    {
        Content systemCourse = null;

        try
        {
            systemCourse = MgnlContext
                .getSystemContext()
                .getHierarchyManager(LMSModule.REPO)
                .getContentByUUID(courseId);
            if (!systemCourse.hasContent(LMSModule.USERS_NODEDATA))
            {
                systemCourse.createContent(LMSModule.USERS_NODEDATA, ItemType.CONTENTNODE);
            }

            if (!systemCourse.getContent(LMSModule.USERS_NODEDATA).hasContent(learnerId))
            {
                systemCourse.getContent(LMSModule.USERS_NODEDATA).createContent(learnerId, ItemType.CONTENTNODE);
            }

            NodeDataUtil.getOrCreateAndSet(
                systemCourse.getContent(LMSModule.USERS_NODEDATA).getContent(learnerId),
                LMSModule.STATUS_NODEDATA,
                status);
            systemCourse.save();
        }
        catch (RepositoryException e)
        {
            log.error("Course Exception: {}", e);
            throw new CourseException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public UserReport getLearnerReport(String courseId, String leanerId) throws CourseException
    {
        UserReport userReport = new UserReport();
        userReport.setObjectives(new ArrayList<Objective>());
        userReport.setTrackingModel(new ArrayList<TrackingModel>());

        userReport.setCourse(courseId);
        userReport.setUser(leanerId);

        Content courseNode = null;
        Content userNode = null;
        try
        {
            courseNode = MgnlContext.getHierarchyManager(LMSModule.REPO).getContentByUUID(courseId);
            userNode = MgnlContext.getHierarchyManager(LMSModule.REPO).getContent(
                courseNode.getHandle() + "/users/" + leanerId);
        }
        catch (RepositoryException e)
        {
            log.error("Course Exception: {}", e);
            throw new CourseException(e);
        }
        String status = NodeDataUtil.getString(userNode, "status");

        if (!status.equals(StringUtils.EMPTY))
        {
            JSONObject statusJson = JSONObject.fromObject(status);

            JSONObject objectivesJson = (JSONObject) statusJson.get("objectives");
            for (String id : (Set<String>) objectivesJson.keySet())
            {
                Objective obj = new Objective(id);
                JSONObject objectiveJson = (JSONObject) objectivesJson.get(id);
                obj.setMeasureStatus(Helper.getBoolean(objectiveJson.get("_objectiveMeasureStatus")));
                obj.setSatisfiedStatus(Helper.getBoolean(objectiveJson.get("_objectiveSatisfiedStatus")));
                obj.setNormalizedMeasure(Helper.getDouble(objectiveJson.get("_objectiveNormalizedMeasure")));
                obj.setProgressStatus(Helper.getBoolean(objectiveJson.get("_objectiveProgressStatus")));

                if (objectiveJson.getBoolean("primaryObj") && objectiveJson.getBoolean("organizationObjective"))
                {
                    userReport.setOrganizationPrimaryObjective(obj);
                }
                userReport.getObjectives().add(obj);
            }
            JSONObject trackJson = (JSONObject) statusJson.get("tracks");
            for (String id : (Set<String>) trackJson.keySet())
            {
                TrackingModel tm = new TrackingModel(leanerId, courseId, ((JSONObject) trackJson.get(id))
                    .getString("title"), id);
                tm.setLevel(Helper.getInt(((JSONObject) trackJson.get(id)).get("level")));
                tm.setActivityAbsoluteDuration(Helper.getDouble(((JSONObject) trackJson.get(id))
                    .get("activityAbsoluteDuration")));
                tm.setActivityAttemptCount(Helper.getInt(((JSONObject) trackJson.get(id)).get("activityAttemptCount")));
                tm.setActivityExperiencedDuration(Helper.getDouble(((JSONObject) trackJson.get(id))
                    .get("activityExperiencedDuration")));
                tm.setActivityProgressStatus(Helper.getBoolean(((JSONObject) trackJson.get(id))
                    .getBoolean("activityProgressStatus")));

                tm.setActivityIsSuspended(Helper.getBoolean(((JSONObject) trackJson.get(id))
                    .get("_activityIsSuspended")));

                userReport.getTrackingModel().add(tm);
            }
        }
        return userReport;
    }

    /**
     * {@inheritDoc}
     */
    public void onActivityView(String learnerId, String courseId, String activityId)
    {

    }

    /**
     * {@inheritDoc}
     */
    public void onCourseCompletion(String learnerId, String courseId)
    {

    }

    /**
     * {@inheritDoc}
     */
    public void onObjectiveCompletion(String learnerId, String courseId, String objectiveId)
    {

    }

    /**
     * {@inheritDoc}
     */
    public void onCourseSatisfied(String learnerId, String courseId)
    {
        try
        {
            Content course = MgnlContext.getSystemContext().getHierarchyManager(LMSModule.REPO).getContentByUUID(
                courseId);
            Content users = ContentUtil
                .getOrCreateContent(course, LMSModule.USERS_NODEDATA, ItemType.CONTENTNODE, true);
            Content user = ContentUtil.getContent(users, learnerId);

            NodeDataUtil.getOrCreateAndSet(user, LMSModule.SATISFIED, true);
            course.save();
        }
        catch (AccessDeniedException e)
        {
            log.error("Course Exception: {}", e);
        }
        catch (RepositoryException e)
        {
            log.error("Course Exception: {}", e);
        }
    }

}