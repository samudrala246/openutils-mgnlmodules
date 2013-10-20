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
//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2009.08.21 at 12:53:38 PM CEST 
//

package net.sourceforge.openutils.mgnllms.scorm.model.seq;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>
 * Java class for rollupConsiderations complex type.
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="rollupConsiderations">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="requiredForSatisfied" type="{http://www.adlnet.org/xsd/adlseq_v1p3}rollupConsideration" default="always" />
 *       &lt;attribute name="requiredForNotSatisfied" type="{http://www.adlnet.org/xsd/adlseq_v1p3}rollupConsideration" default="always" />
 *       &lt;attribute name="requiredForCompleted" type="{http://www.adlnet.org/xsd/adlseq_v1p3}rollupConsideration" default="always" />
 *       &lt;attribute name="requiredForIncomplete" type="{http://www.adlnet.org/xsd/adlseq_v1p3}rollupConsideration" default="always" />
 *       &lt;attribute name="measureSatisfactionIfActive" type="{http://www.w3.org/2001/XMLSchema}boolean" default="true" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "rollupConsiderations")
public class RollupConsiderations
{

    @XmlAttribute
    protected RollupConsideration requiredForSatisfied;

    @XmlAttribute
    protected RollupConsideration requiredForNotSatisfied;

    @XmlAttribute
    protected RollupConsideration requiredForCompleted;

    @XmlAttribute
    protected RollupConsideration requiredForIncomplete;

    @XmlAttribute
    protected Boolean measureSatisfactionIfActive;

    /**
     * Gets the value of the requiredForSatisfied property.
     * @return possible object is {@link RollupConsideration }
     */
    public RollupConsideration getRequiredForSatisfied()
    {
        if (requiredForSatisfied == null)
        {
            return RollupConsideration.ALWAYS;
        }
        else
        {
            return requiredForSatisfied;
        }
    }

    /**
     * Sets the value of the requiredForSatisfied property.
     * @param value allowed object is {@link RollupConsideration }
     */
    public void setRequiredForSatisfied(RollupConsideration value)
    {
        this.requiredForSatisfied = value;
    }

    /**
     * Gets the value of the requiredForNotSatisfied property.
     * @return possible object is {@link RollupConsideration }
     */
    public RollupConsideration getRequiredForNotSatisfied()
    {
        if (requiredForNotSatisfied == null)
        {
            return RollupConsideration.ALWAYS;
        }
        else
        {
            return requiredForNotSatisfied;
        }
    }

    /**
     * Sets the value of the requiredForNotSatisfied property.
     * @param value allowed object is {@link RollupConsideration }
     */
    public void setRequiredForNotSatisfied(RollupConsideration value)
    {
        this.requiredForNotSatisfied = value;
    }

    /**
     * Gets the value of the requiredForCompleted property.
     * @return possible object is {@link RollupConsideration }
     */
    public RollupConsideration getRequiredForCompleted()
    {
        if (requiredForCompleted == null)
        {
            return RollupConsideration.ALWAYS;
        }
        else
        {
            return requiredForCompleted;
        }
    }

    /**
     * Sets the value of the requiredForCompleted property.
     * @param value allowed object is {@link RollupConsideration }
     */
    public void setRequiredForCompleted(RollupConsideration value)
    {
        this.requiredForCompleted = value;
    }

    /**
     * Gets the value of the requiredForIncomplete property.
     * @return possible object is {@link RollupConsideration }
     */
    public RollupConsideration getRequiredForIncomplete()
    {
        if (requiredForIncomplete == null)
        {
            return RollupConsideration.ALWAYS;
        }
        else
        {
            return requiredForIncomplete;
        }
    }

    /**
     * Sets the value of the requiredForIncomplete property.
     * @param value allowed object is {@link RollupConsideration }
     */
    public void setRequiredForIncomplete(RollupConsideration value)
    {
        this.requiredForIncomplete = value;
    }

    /**
     * Gets the value of the measureSatisfactionIfActive property.
     * @return possible object is {@link Boolean }
     */
    public boolean isMeasureSatisfactionIfActive()
    {
        if (measureSatisfactionIfActive == null)
        {
            return true;
        }
        else
        {
            return measureSatisfactionIfActive;
        }
    }

    /**
     * Sets the value of the measureSatisfactionIfActive property.
     * @param value allowed object is {@link Boolean }
     */
    public void setMeasureSatisfactionIfActive(Boolean value)
    {
        this.measureSatisfactionIfActive = value;
    }

}
