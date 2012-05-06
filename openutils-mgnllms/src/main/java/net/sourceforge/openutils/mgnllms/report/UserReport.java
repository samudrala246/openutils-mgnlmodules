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

package net.sourceforge.openutils.mgnllms.report;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.BooleanUtils;


/**
 * @author carlo
 * @version $Id: $
 */
public class UserReport
{

    private String user;

    private String course;

    private Objective organizationPrimaryObjective;

    private List<Objective> objectives;

    private List<TrackingModel> trackingModel = new ArrayList<TrackingModel>();

    /**
     * Returns the user.
     * @return the user
     */
    public String getUser()
    {
        return user;
    }

    /**
     * Sets the user.
     * @param user the user to set
     */
    public void setUser(String user)
    {
        this.user = user;
    }

    /**
     * Returns the course.
     * @return the course
     */
    public String getCourse()
    {
        return course;
    }

    /**
     * Sets the course.
     * @param course the course to set
     */
    public void setCourse(String course)
    {
        this.course = course;
    }

    /**
     * Returns the objectives.
     * @return the objectives
     */
    public List<Objective> getObjectives()
    {
        return objectives;
    }

    /**
     * Sets the objectives.
     * @param objectives the objectives to set
     */
    public void setObjectives(List<Objective> objectives)
    {
        this.objectives = objectives;
    }

    /**
     * Returns the trackingModel.
     * @return the trackingModel
     */
    public List<TrackingModel> getTrackingModel()
    {
        return trackingModel;
    }

    /**
     * Sets the trackingModel.
     * @param trackingModel the trackingModel to set
     */
    public void setTrackingModel(List<TrackingModel> trackingModel)
    {
        this.trackingModel = trackingModel;
    }

    /**
     * Returns the objectiveProgressStatus.
     * @return the objectiveProgressStatus
     */
    public int getObjectiveProgressStatus()
    {
        int temp = 0;
        for (Objective obj : this.objectives)
        {
            if (BooleanUtils.isTrue(obj.getProgressStatus()))
            {
                temp++;
            }
        }
        return temp;
    }

    /**
     * Returns the objectiveSatisfiedStatus.
     * @return the objectiveSatisfiedStatus
     */
    public int getObjectiveSatisfiedStatus()
    {
        int temp = 0;
        for (Objective obj : this.objectives)
        {

            if (BooleanUtils.isTrue(obj.getSatisfiedStatus()))
            {
                temp++;
            }
        }
        return temp;
    }

    /**
     * Sets the organizationPrimaryObjective.
     * @param organizationPrimaryObjective the organizationPrimaryObjective to set
     */
    public void setOrganizationPrimaryObjective(Objective organizationPrimaryObjective)
    {
        this.organizationPrimaryObjective = organizationPrimaryObjective;
    }

    /**
     * Returns the organizationPrimaryObjective.
     * @return the organizationPrimaryObjective
     */
    public Objective getOrganizationPrimaryObjective()
    {
        return organizationPrimaryObjective;
    }
}
