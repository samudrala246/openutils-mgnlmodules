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

package net.sourceforge.openutils.mgnllms.scorm.model.cp;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;


/**
 * <p>
 * Java class for completionThreshold complex type.
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="completionThreshold">
 *   &lt;simpleContent>
 *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
 *       &lt;attribute name="completedByMeasure" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *       &lt;attribute name="minProgressMeasure" type="{http://www.adlnet.org/xsd/adlcp_v1p3}minProgressMeasure" default="1.0" />
 *       &lt;attribute name="progressWeight" type="{http://www.adlnet.org/xsd/adlcp_v1p3}progressWeight" default="1.0" />
 *     &lt;/extension>
 *   &lt;/simpleContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "completionThreshold", propOrder = {"value" })
public class CompletionThreshold
{

    @XmlValue
    protected String value;

    @XmlAttribute
    protected Boolean completedByMeasure;

    @XmlAttribute
    protected BigDecimal minProgressMeasure;

    @XmlAttribute
    protected BigDecimal progressWeight;

    /**
     * Gets the value of the value property.
     * @return possible object is {@link String }
     */
    public String getValue()
    {
        return value;
    }

    /**
     * Sets the value of the value property.
     * @param value allowed object is {@link String }
     */
    public void setValue(String value)
    {
        this.value = value;
    }

    /**
     * Gets the value of the completedByMeasure property.
     * @return possible object is {@link Boolean }
     */
    public boolean isCompletedByMeasure()
    {
        if (completedByMeasure == null)
        {
            return false;
        }
        else
        {
            return completedByMeasure;
        }
    }

    /**
     * Sets the value of the completedByMeasure property.
     * @param value allowed object is {@link Boolean }
     */
    public void setCompletedByMeasure(Boolean value)
    {
        this.completedByMeasure = value;
    }

    /**
     * Gets the value of the minProgressMeasure property.
     * @return possible object is {@link BigDecimal }
     */
    public BigDecimal getMinProgressMeasure()
    {
        if (minProgressMeasure == null)
        {
            return new BigDecimal("1.0");
        }
        else
        {
            return minProgressMeasure;
        }
    }

    /**
     * Sets the value of the minProgressMeasure property.
     * @param value allowed object is {@link BigDecimal }
     */
    public void setMinProgressMeasure(BigDecimal value)
    {
        this.minProgressMeasure = value;
    }

    /**
     * Gets the value of the progressWeight property.
     * @return possible object is {@link BigDecimal }
     */
    public BigDecimal getProgressWeight()
    {
        if (progressWeight == null)
        {
            return new BigDecimal("1.0");
        }
        else
        {
            return progressWeight;
        }
    }

    /**
     * Sets the value of the progressWeight property.
     * @param value allowed object is {@link BigDecimal }
     */
    public void setProgressWeight(BigDecimal value)
    {
        this.progressWeight = value;
    }

}
