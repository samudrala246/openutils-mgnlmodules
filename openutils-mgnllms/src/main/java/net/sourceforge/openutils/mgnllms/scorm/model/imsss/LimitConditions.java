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

package net.sourceforge.openutils.mgnllms.scorm.model.imsss;

import java.math.BigInteger;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>
 * Java class for limitConditions complex type.
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="limitConditions">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="attemptLimit" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" />
 *       &lt;attribute name="attemptAbsoluteDurationLimit" type="{http://www.w3.org/2001/XMLSchema}String" />
 *       &lt;attribute name="attemptExperiencedDurationLimit" type="{http://www.w3.org/2001/XMLSchema}String" />
 *       &lt;attribute name="activityAbsoluteDurationLimit" type="{http://www.w3.org/2001/XMLSchema}String" />
 *       &lt;attribute name="activityExperiencedDurationLimit" type="{http://www.w3.org/2001/XMLSchema}String" />
 *       &lt;attribute name="beginTimeLimit" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
 *       &lt;attribute name="endTimeLimit" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "limitConditions")
public class LimitConditions
{

    @XmlAttribute
    @XmlSchemaType(name = "nonNegativeInteger")
    protected BigInteger attemptLimit;

    @XmlAttribute
    protected String attemptAbsoluteDurationLimit;

    @XmlAttribute
    protected String attemptExperiencedDurationLimit;

    @XmlAttribute
    protected String activityAbsoluteDurationLimit;

    @XmlAttribute
    protected String activityExperiencedDurationLimit;

    @XmlAttribute
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar beginTimeLimit;

    @XmlAttribute
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar endTimeLimit;

    /**
     * Gets the value of the attemptLimit property.
     * @return possible object is {@link BigInteger }
     */
    public BigInteger getAttemptLimit()
    {
        return attemptLimit;
    }

    /**
     * Sets the value of the attemptLimit property.
     * @param value allowed object is {@link BigInteger }
     */
    public void setAttemptLimit(BigInteger value)
    {
        this.attemptLimit = value;
    }

    /**
     * Gets the value of the attemptAbsoluteDurationLimit property.
     * @return possible object is {@link String }
     */
    public String getAttemptAbsoluteDurationLimit()
    {
        return attemptAbsoluteDurationLimit;
    }

    /**
     * Sets the value of the attemptAbsoluteDurationLimit property.
     * @param value allowed object is {@link String }
     */
    public void setAttemptAbsoluteDurationLimit(String value)
    {
        this.attemptAbsoluteDurationLimit = value;
    }

    /**
     * Gets the value of the attemptExperiencedDurationLimit property.
     * @return possible object is {@link String }
     */
    public String getAttemptExperiencedDurationLimit()
    {
        return attemptExperiencedDurationLimit;
    }

    /**
     * Sets the value of the attemptExperiencedDurationLimit property.
     * @param value allowed object is {@link String }
     */
    public void setAttemptExperiencedDurationLimit(String value)
    {
        this.attemptExperiencedDurationLimit = value;
    }

    /**
     * Gets the value of the activityAbsoluteDurationLimit property.
     * @return possible object is {@link String }
     */
    public String getActivityAbsoluteDurationLimit()
    {
        return activityAbsoluteDurationLimit;
    }

    /**
     * Sets the value of the activityAbsoluteDurationLimit property.
     * @param value allowed object is {@link String }
     */
    public void setActivityAbsoluteDurationLimit(String value)
    {
        this.activityAbsoluteDurationLimit = value;
    }

    /**
     * Gets the value of the activityExperiencedDurationLimit property.
     * @return possible object is {@link String }
     */
    public String getActivityExperiencedDurationLimit()
    {
        return activityExperiencedDurationLimit;
    }

    /**
     * Sets the value of the activityExperiencedDurationLimit property.
     * @param value allowed object is {@link String }
     */
    public void setActivityExperiencedDurationLimit(String value)
    {
        this.activityExperiencedDurationLimit = value;
    }

    /**
     * Gets the value of the beginTimeLimit property.
     * @return possible object is {@link XMLGregorianCalendar }
     */
    public XMLGregorianCalendar getBeginTimeLimit()
    {
        return beginTimeLimit;
    }

    /**
     * Sets the value of the beginTimeLimit property.
     * @param value allowed object is {@link XMLGregorianCalendar }
     */
    public void setBeginTimeLimit(XMLGregorianCalendar value)
    {
        this.beginTimeLimit = value;
    }

    /**
     * Gets the value of the endTimeLimit property.
     * @return possible object is {@link XMLGregorianCalendar }
     */
    public XMLGregorianCalendar getEndTimeLimit()
    {
        return endTimeLimit;
    }

    /**
     * Sets the value of the endTimeLimit property.
     * @param value allowed object is {@link XMLGregorianCalendar }
     */
    public void setEndTimeLimit(XMLGregorianCalendar value)
    {
        this.endTimeLimit = value;
    }

}
