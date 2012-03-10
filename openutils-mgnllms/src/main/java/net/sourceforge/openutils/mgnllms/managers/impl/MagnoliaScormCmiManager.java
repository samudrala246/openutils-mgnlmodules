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

package net.sourceforge.openutils.mgnllms.managers.impl;

import info.magnolia.cms.core.Content;
import info.magnolia.cms.core.ItemType;
import info.magnolia.cms.core.Path;
import info.magnolia.cms.util.ContentUtil;
import info.magnolia.cms.util.NodeDataUtil;
import info.magnolia.context.MgnlContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.RepositoryException;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import net.sourceforge.openutils.mgnllms.lms.exceptions.CourseException;
import net.sourceforge.openutils.mgnllms.managers.ScormCMIManager;
import net.sourceforge.openutils.mgnllms.module.LMSModule;

import org.apache.commons.lang.StringUtils;


/**
 * @author carlo
 * @version $Id: $
 */
public class MagnoliaScormCmiManager implements ScormCMIManager
{

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public void commit(String courseId, String activityId, String learnerId, String cmiValues, String adldata)
        throws CourseException
    {
        Content course = null;
        try
        {
            course = MgnlContext.getSystemContext().getHierarchyManager(LMSModule.REPO).getContentByUUID(courseId);
            Content activity = ContentUtil.getOrCreateContent(course.getContent(LMSModule.ACTIVITIES_NODEDATA), Path
                .getValidatedLabel(activityId), ItemType.CONTENTNODE, true);
            Content user = ContentUtil.getOrCreateContent(activity, learnerId, ItemType.CONTENTNODE, true);
            NodeDataUtil.getOrCreateAndSet(user, LMSModule.DATAMODEL_NODEDATA, cmiValues);
            user.save();

            if (StringUtils.isNotBlank(adldata))
            {
                JSONArray adllist = JSONArray.fromObject(adldata);
                ContentUtil.getOrCreateContent(course, LMSModule.USERS_NODEDATA, ItemType.CONTENTNODE, true);

                Content userContent = ContentUtil.getOrCreateContent(
                    course.getContent(LMSModule.USERS_NODEDATA),
                    learnerId,
                    ItemType.CONTENTNODE,
                    true);
                for (JSONObject map : (List<JSONObject>) adllist)
                {
                    String id = map.getString("id");
                    if (NodeDataUtil.getBoolean(
                        activity.getContent(LMSModule.ADLDATA_NODEDATA).getContent(id),
                        "writeSharedData",
                        true))
                    {
                        Content adlcontent = ContentUtil.getOrCreateContent(
                            userContent,
                            LMSModule.ADLDATA_NODEDATA,
                            ItemType.CONTENTNODE,
                            true);
                        NodeDataUtil.getOrCreateAndSet(adlcontent, id, map.getString("store"));
                        adlcontent.save();
                    }
                }
            }
        }
        catch (RepositoryException e)
        {
            throw new CourseException(e);
        }

    }

    /**
     * {@inheritDoc}
     */
    public String initialize(String courseId, String activityId, String learnerId) throws CourseException
    {
        Content course = null;
        try
        {
            course = MgnlContext.getSystemContext().getHierarchyManager(LMSModule.REPO).getContentByUUID(courseId);
            Content activities = ContentUtil.getOrCreateContent(
                course,
                LMSModule.ACTIVITIES_NODEDATA,
                ItemType.CONTENTNODE,
                true);
            Content activity = ContentUtil.getOrCreateContent(
                activities,
                Path.getValidatedLabel(activityId),
                ItemType.CONTENTNODE,
                true);

            String activityCmi;
            activityCmi = NodeDataUtil.getString(activity, LMSModule.DATAMODEL_NODEDATA);

            String userActivityCmi;

            if (!activity.hasContent(learnerId)
                || !activity.getContent(learnerId).hasNodeData(LMSModule.DATAMODEL_NODEDATA)
                || NodeDataUtil.getString(activity.getContent(learnerId), LMSModule.DATAMODEL_NODEDATA) == "")
            {
                if (!activity.hasContent(learnerId))
                {
                    activity.createContent(learnerId, ItemType.CONTENTNODE);
                }
                Map<String, String> tempObject = new HashMap<String, String>();

                tempObject.put("learner_id", learnerId);
                tempObject.put("credit", "credit");
                tempObject.put("entry", "ab-initio"); // se creo il data model è un nuovo attempt
                tempObject.put("exit", "");
                tempObject.put("learner_name", learnerId);
                tempObject.put("mode", "normal");
                tempObject.put("completion_status", "unknown");
                tempObject.put("success_status", "unknown");
                tempObject.put("total_time", "PT0S");

                userActivityCmi = JSONSerializer.toJSON(tempObject).toString();
                userActivityCmi = userActivityCmi.substring(1, userActivityCmi.length() - 1);

                NodeDataUtil.getOrCreateAndSet(
                    activity.getContent(learnerId),
                    LMSModule.DATAMODEL_NODEDATA,
                    userActivityCmi);
            }
            else
            {
                userActivityCmi = NodeDataUtil.getString(activity.getContent(learnerId), LMSModule.DATAMODEL_NODEDATA);
            }
            activity.save();
            List<Map<String, String>> maps = new ArrayList<Map<String, String>>();
            if (activity.hasContent(LMSModule.ADLDATA_NODEDATA))
            {
                for (Content data : activity.getContent(LMSModule.ADLDATA_NODEDATA).getChildren(ItemType.CONTENTNODE))
                {
                    Content user = ContentUtil.getOrCreateContent(
                        course.getContent(LMSModule.USERS_NODEDATA),
                        learnerId,
                        ItemType.CONTENTNODE);

                    Content adldata = ContentUtil.getOrCreateContent(
                        user,
                        LMSModule.ADLDATA_NODEDATA,
                        ItemType.CONTENTNODE);

                    Map<String, String> temp = new HashMap<String, String>(2);

                    temp.put("id", data.getName());

                    temp.put("store", adldata.hasNodeData(data.getName()) ? NodeDataUtil.getString(adldata, data
                        .getName()) : null);

                    temp
                        .put("readSharedData", NodeDataUtil.getBoolean(data, "readSharedData", true) ? "true" : "false");
                    temp.put("writeSharedData", NodeDataUtil.getBoolean(data, "writeSharedData", true)
                        ? "true"
                        : "false");

                    maps.add(temp);
                }
            }

            MgnlContext.getHierarchyManager(LMSModule.REPO).save();

            String cmiString = new StringBuffer().append("{").append(activityCmi).append(
                StringUtils.isBlank(activityCmi) ? StringUtils.EMPTY : (StringUtils.isBlank(userActivityCmi)
                    ? StringUtils.EMPTY
                    : ",")).append(userActivityCmi).append("}").toString();

            String adlString = JSONSerializer.toJSON(maps).toString();

            return new StringBuffer()
                .append("{ cmi:")
                .append(cmiString)
                .append(",adl:")
                .append(adlString)
                .append("}")
                .toString();
        }
        catch (RepositoryException e)
        {
            throw new CourseException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void terminate(String courseId, String activityId, String learnerId, String cmiValues, String adldata)
        throws CourseException
    {
        commit(courseId, activityId, learnerId, cmiValues, adldata);
    }
}
