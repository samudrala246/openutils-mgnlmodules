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
package net.sourceforge.openutils.mgnllms.report;

/**
 * @author carlo
 * @version $Id: $
 */
public class TrackingModel
{

    private String user;

    private String course;

    private String title;

    private Integer level;

    private String identifier;

    private Boolean activityProgressStatus;

    private Double activityAbsoluteDuration;

    private Double activityExperiencedDuration;

    private Integer activityAttemptCount;

    private Boolean activityIsSuspended;

    /**
     * @param user
     * @param course
     * @param title
     * @param identifier
     */
    public TrackingModel(String user, String course, String title, String identifier)
    {
        super();
        this.user = user;
        this.course = course;
        this.title = title;
        this.identifier = identifier;
    }

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
     * Returns the title.
     * @return the title
     */
    public String getTitle()
    {
        return title;
    }

    /**
     * Sets the title.
     * @param title the title to set
     */
    public void setTitle(String title)
    {
        this.title = title;
    }

    /**
     * Returns the identifier.
     * @return the identifier
     */
    public String getIdentifier()
    {
        return identifier;
    }

    /**
     * Sets the identifier.
     * @param identifier the identifier to set
     */
    public void setIdentifier(String identifier)
    {
        this.identifier = identifier;
    }

    /**
     * Returns the activityProgressStatus.
     * @return the activityProgressStatus
     */
    public Boolean getActivityProgressStatus()
    {
        return activityProgressStatus;
    }

    /**
     * Sets the activityProgressStatus.
     * @param activityProgressStatus the activityProgressStatus to set
     */
    public void setActivityProgressStatus(Boolean activityProgressStatus)
    {
        this.activityProgressStatus = activityProgressStatus;
    }

    /**
     * Returns the activityAbsoluteDuration.
     * @return the activityAbsoluteDuration
     */
    public Double getActivityAbsoluteDuration()
    {
        return activityAbsoluteDuration;
    }

    /**
     * Sets the activityAbsoluteDuration.
     * @param activityAbsoluteDuration the activityAbsoluteDuration to set
     */
    public void setActivityAbsoluteDuration(Double activityAbsoluteDuration)
    {
        this.activityAbsoluteDuration = activityAbsoluteDuration;
    }

    /**
     * Returns the activityExperiencedDuration.
     * @return the activityExperiencedDuration
     */
    public Double getActivityExperiencedDuration()
    {
        return activityExperiencedDuration;
    }

    /**
     * Sets the activityExperiencedDuration.
     * @param activityExperiencedDuration the activityExperiencedDuration to set
     */
    public void setActivityExperiencedDuration(Double activityExperiencedDuration)
    {
        this.activityExperiencedDuration = activityExperiencedDuration;
    }

    /**
     * Returns the activityAttemptCount.
     * @return the activityAttemptCount
     */
    public Integer getActivityAttemptCount()
    {
        return activityAttemptCount;
    }

    /**
     * Sets the activityAttemptCount.
     * @param activityAttemptCount the activityAttemptCount to set
     */
    public void setActivityAttemptCount(Integer activityAttemptCount)
    {
        this.activityAttemptCount = activityAttemptCount;
    }

    /**
     * Returns the activityIsSuspended.
     * @return the activityIsSuspended
     */
    public Boolean getActivityIsSuspended()
    {
        return activityIsSuspended;
    }

    /**
     * Sets the activityIsSuspended.
     * @param activityIsSuspended the activityIsSuspended to set
     */
    public void setActivityIsSuspended(Boolean activityIsSuspended)
    {
        this.activityIsSuspended = activityIsSuspended;
    }

    /**
     * Returns the level.
     * @return the level
     */
    public Integer getLevel()
    {
        return level;
    }

    /**
     * Sets the level.
     * @param level the level to set
     */
    public void setLevel(Integer level)
    {
        this.level = level;
    }

}