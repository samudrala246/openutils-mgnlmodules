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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>
 * Java class for rollupRules complex type.
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="rollupRules">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="rollupRule" type="{http://www.imsglobal.org/xsd/imsss}rollupRule" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="rollupObjectiveSatisfied" type="{http://www.w3.org/2001/XMLSchema}boolean" default="true" />
 *       &lt;attribute name="rollupProgressCompletion" type="{http://www.w3.org/2001/XMLSchema}boolean" default="true" />
 *       &lt;attribute name="objectiveMeasureWeight" type="{http://www.imsglobal.org/xsd/imsss}weight" default="1.0000" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "rollupRules", propOrder = {"rollupRule" })
public class RollupRules
{

    protected List<RollupRule> rollupRule;

    @XmlAttribute
    protected Boolean rollupObjectiveSatisfied;

    @XmlAttribute
    protected Boolean rollupProgressCompletion;

    @XmlAttribute
    protected BigDecimal objectiveMeasureWeight;

    /**
     * Gets the value of the rollupRule property.
     * <p>
     * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make to
     * the returned list will be present inside the JAXB object. This is why there is not a <CODE>set</CODE> method for
     * the rollupRule property.
     * <p>
     * For example, to add a new item, do as follows:
     * 
     * <pre>
     *    getRollupRule().add(newItem);
     * </pre>
     * <p>
     * Objects of the following type(s) are allowed in the list {@link RollupRule }
     */
    public List<RollupRule> getRollupRule()
    {
        if (rollupRule == null)
        {
            rollupRule = new ArrayList<RollupRule>();
        }
        return this.rollupRule;
    }

    /**
     * Gets the value of the rollupObjectiveSatisfied property.
     * @return possible object is {@link Boolean }
     */
    public boolean isRollupObjectiveSatisfied()
    {
        if (rollupObjectiveSatisfied == null)
        {
            return true;
        }
        else
        {
            return rollupObjectiveSatisfied;
        }
    }

    /**
     * Sets the value of the rollupObjectiveSatisfied property.
     * @param value allowed object is {@link Boolean }
     */
    public void setRollupObjectiveSatisfied(Boolean value)
    {
        this.rollupObjectiveSatisfied = value;
    }

    /**
     * Gets the value of the rollupProgressCompletion property.
     * @return possible object is {@link Boolean }
     */
    public boolean isRollupProgressCompletion()
    {
        if (rollupProgressCompletion == null)
        {
            return true;
        }
        else
        {
            return rollupProgressCompletion;
        }
    }

    /**
     * Sets the value of the rollupProgressCompletion property.
     * @param value allowed object is {@link Boolean }
     */
    public void setRollupProgressCompletion(Boolean value)
    {
        this.rollupProgressCompletion = value;
    }

    /**
     * Gets the value of the objectiveMeasureWeight property.
     * @return possible object is {@link BigDecimal }
     */
    public BigDecimal getObjectiveMeasureWeight()
    {
        if (objectiveMeasureWeight == null)
        {
            return new BigDecimal("1.0000");
        }
        else
        {
            return objectiveMeasureWeight;
        }
    }

    /**
     * Sets the value of the objectiveMeasureWeight property.
     * @param value allowed object is {@link BigDecimal }
     */
    public void setObjectiveMeasureWeight(BigDecimal value)
    {
        this.objectiveMeasureWeight = value;
    }

}
