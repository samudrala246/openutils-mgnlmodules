/**
 *
 * E-learning Module for Magnolia CMS (http://www.openmindlab.com/lab/products/lms.html)
 * Copyright(C) 2010-2012, Openmind S.r.l. http://www.openmindonline.it
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

package net.sourceforge.openutils.mgnllms.lms.el;

import info.magnolia.cms.core.Content;
import info.magnolia.cms.core.search.Query;
import info.magnolia.cms.core.search.QueryManager;
import info.magnolia.cms.core.search.QueryResult;
import info.magnolia.cms.security.Security;
import info.magnolia.cms.security.User;
import info.magnolia.cms.util.NodeDataUtil;
import info.magnolia.context.MgnlContext;

import java.util.ArrayList;
import java.util.Collection;

import javax.jcr.RepositoryException;
import javax.jcr.query.InvalidQueryException;

import net.sf.json.JSONObject;
import net.sourceforge.openutils.mgnllms.module.LMSModule;
import net.sourceforge.openutils.mgnllms.report.Helper;

import org.apache.commons.lang.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author luca boati
 */
public class LmsElFunctions
{

    /**
     * Logger.
     */
    private static Logger log = LoggerFactory.getLogger(LmsElFunctions.class);

    /**
     * @return
     */
    public static User currentUser()
    {
        return Security.getUserManager().getUser(LMSModule.getInstance().getLearnerManager().getCurrentLearner());
    }

    public static Collection<Content> courseList()
    {
        Collection<Content> course = new ArrayList<Content>();

        QueryManager qm = MgnlContext.getQueryManager(LMSModule.REPO);

        Query q;
        try
        {
            q = qm.createQuery("//*", Query.XPATH);
            QueryResult qres = q.execute();
            course = qres.getContent("mgnl:course");
        }
        catch (InvalidQueryException e)
        {
            log.error("Exception: {}", e);
        }
        catch (RepositoryException e)
        {
            log.error("Exception: {}", e);
        }

        return course;
    }

    public static boolean activityAttempted(String courseId, String activityId)
    {
        Integer attempt = Helper.getInt(activityInformation(courseId, activityId, "activityAttemptCount"));
        if (attempt != null && attempt > 0)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public static boolean activityIsSuspended(String courseId, String activityId)
    {
        Boolean isSuspended = Helper.getBoolean(activityInformation(courseId, activityId, "_activityIsSuspended"));
        if (BooleanUtils.isTrue(isSuspended))
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public static String activityInformation(String courseId, String activityId, String information)
    {
        JSONObject activities = (JSONObject) getUserTrackingModel(courseId).get(LMSModule.JSON_KEY_ACTIVITIES_TM);
        return ((JSONObject) activities.get(activityId)).optString(information);
    }

    public static String suspendedActivity(String courseId)
    {
        return getUserTrackingModel(courseId).optString("suspendedActivity");
    }

    public static boolean hasSuspendedActivity(String courseId)
    {
        return getUserTrackingModel(courseId).optString("suspendedActivity").length() > 0;
    }

    private static JSONObject getUserTrackingModel(String courseId)
    {
        Content course = null;
        Content statusNode = null;
        String status = "{}";
        try
        {
            course = MgnlContext.getHierarchyManager(LMSModule.REPO).getContentByUUID(courseId);
            if (course.hasContent(LMSModule.USERS_NODEDATA))
            {
                Content users = MgnlContext.getHierarchyManager(LMSModule.REPO).getContent(
                    course.getHandle() + "/" + LMSModule.USERS_NODEDATA);
                if (users != null)
                {
                    statusNode = MgnlContext.getHierarchyManager(LMSModule.REPO).getContent(
                        course.getHandle() + "/" + LMSModule.USERS_NODEDATA + "/" + MgnlContext.getUser().getName());
                }
                if (statusNode != null && statusNode.hasNodeData(LMSModule.STATUS_NODEDATA))
                {
                    status = NodeDataUtil.getString(statusNode, LMSModule.STATUS_NODEDATA);
                }
            }

        }
        catch (RepositoryException e)
        {
            log.error("Exception: {}", e);
        }
        return JSONObject.fromObject(status);
    }

    public static boolean isCourseSatisfied(String courseId)
    {
        String user = MgnlContext.getUser().getName();
        try
        {
            Content course = MgnlContext.getHierarchyManager(LMSModule.REPO).getContentByUUID(courseId);
            if (course.hasContent(LMSModule.USERS_NODEDATA)
                && course.getContent(LMSModule.USERS_NODEDATA).hasContent(user)
                && course.getContent(LMSModule.USERS_NODEDATA).getContent(user).hasNodeData(LMSModule.SATISFIED))
            {
                return NodeDataUtil.getBoolean(
                    course.getContent(LMSModule.USERS_NODEDATA).getContent(user),
                    LMSModule.SATISFIED,
                    false);
            }
            else
            {
                return false;
            }
        }
        catch (RepositoryException e)
        {
            log.error("Exception: {}", e);
            return false;
        }

    }
}
