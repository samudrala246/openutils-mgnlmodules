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

package net.sourceforge.openutils.mgnllms.report;

/**
 * @author carlo
 * @version $Id: $
 */
public class Objective
{

    private String id;

    private Boolean progressStatus;

    private Boolean measureStatus;

    private Boolean satisfiedStatus;

    private Double normalizedMeasure;

    /**
     * @param id
     */
    public Objective(String id)
    {
        super();
        this.id = id;
    }

    /**
     * Returns the id.
     * @return the id
     */
    public String getId()
    {
        return id;
    }

    /**
     * Sets the id.
     * @param id the id to set
     */
    public void setId(String id)
    {
        this.id = id;
    }

    /**
     * Returns the progressStatus.
     * @return the progressStatus
     */
    public Boolean getProgressStatus()
    {
        return progressStatus;
    }

    /**
     * Sets the progressStatus.
     * @param progressStatus the progressStatus to set
     */
    public void setProgressStatus(Boolean progressStatus)
    {
        this.progressStatus = progressStatus;
    }

    /**
     * Returns the measureStatus.
     * @return the measureStatus
     */
    public Boolean getMeasureStatus()
    {
        return measureStatus;
    }

    /**
     * Sets the measureStatus.
     * @param measureStatus the measureStatus to set
     */
    public void setMeasureStatus(Boolean measureStatus)
    {
        this.measureStatus = measureStatus;
    }

    /**
     * Returns the satisfiedStatus.
     * @return the satisfiedStatus
     */
    public Boolean getSatisfiedStatus()
    {
        return satisfiedStatus;
    }

    /**
     * Sets the satisfiedStatus.
     * @param satisfiedStatus the satisfiedStatus to set
     */
    public void setSatisfiedStatus(Boolean satisfiedStatus)
    {
        this.satisfiedStatus = satisfiedStatus;
    }

    /**
     * Returns the normalizedMeasure.
     * @return the normalizedMeasure
     */
    public Double getNormalizedMeasure()
    {
        return normalizedMeasure;
    }

    /**
     * Sets the normalizedMeasure.
     * @param normalizedMeasure the normalizedMeasure to set
     */
    public void setNormalizedMeasure(Double normalizedMeasure)
    {
        this.normalizedMeasure = normalizedMeasure;
    }
}