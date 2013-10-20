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
package net.sourceforge.openutils.mgnllms.module;

import info.magnolia.cms.beans.config.ObservedManager;
import info.magnolia.cms.core.Content;
import info.magnolia.cms.util.ContentUtil;
import info.magnolia.content2bean.Content2BeanException;
import info.magnolia.content2bean.Content2BeanUtil;
import info.magnolia.objectfactory.Components;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Singleton;
import javax.jcr.RepositoryException;

import net.sourceforge.openutils.mgnllms.listeners.CourseEventListener;


/**
 * @author molaschi
 * @version $Id: $
 */
@Singleton
public class LMSConfigurationModuleManager extends ObservedManager implements CourseEventListener
{

    public static LMSConfigurationModuleManager getInstance()
    {
        return Components.getSingleton(LMSConfigurationModuleManager.class);
    }

    private List<CourseEventListener> courseEventListeners = new ArrayList<CourseEventListener>();

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onClear()
    {
        courseEventListeners.clear();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onRegister(Content node)
    {
        try
        {
            if (node.hasContent("listeners"))
            {
                for (Content listenerNode : ContentUtil.getAllChildren(node.getContent("listeners")))
                {
                    try
                    {
                        CourseEventListener courseEventListener = (CourseEventListener) Content2BeanUtil
                            .toBean(listenerNode);
                        courseEventListeners.add(courseEventListener);
                    }
                    catch (Content2BeanException ex)
                    {
                        log.error("Cannot convert node {} to CourseEventListener", listenerNode.getHandle());
                    }
                }
            }
        }
        catch (RepositoryException ex)
        {
            log.error("Error initializing config", ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void onActivityView(String learnerId, String courseId, String activityId)
    {
        for (CourseEventListener cel : courseEventListeners)
        {
            cel.onActivityView(learnerId, courseId, activityId);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void onCourseCompletion(String learnerId, String courseId)
    {
        for (CourseEventListener cel : courseEventListeners)
        {
            cel.onCourseCompletion(learnerId, courseId);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void onObjectiveCompletion(String learnerId, String courseId, String objectiveId)
    {
        for (CourseEventListener cel : courseEventListeners)
        {
            cel.onObjectiveCompletion(learnerId, courseId, objectiveId);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void onCourseSatisfied(String learnerId, String courseId)
    {
        for (CourseEventListener cel : courseEventListeners)
        {
            cel.onCourseSatisfied(learnerId, courseId);
        }

    }

}
