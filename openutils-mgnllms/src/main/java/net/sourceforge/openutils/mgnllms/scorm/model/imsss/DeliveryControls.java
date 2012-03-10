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

//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2009.08.21 at 12:53:38 PM CEST 
//

package net.sourceforge.openutils.mgnllms.scorm.model.imsss;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * The type that describes any element which fullfills a delivery control semantic
 * <p>
 * Java class for deliveryControls complex type.
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="deliveryControls">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="tracked" type="{http://www.w3.org/2001/XMLSchema}boolean" default="true" />
 *       &lt;attribute name="completionSetByContent" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *       &lt;attribute name="objectiveSetByContent" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "deliveryControls")
public class DeliveryControls
{

    @XmlAttribute
    protected Boolean tracked;

    @XmlAttribute
    protected Boolean completionSetByContent;

    @XmlAttribute
    protected Boolean objectiveSetByContent;

    /**
     * Gets the value of the tracked property.
     * @return possible object is {@link Boolean }
     */
    public boolean isTracked()
    {
        if (tracked == null)
        {
            return true;
        }
        else
        {
            return tracked;
        }
    }

    /**
     * Sets the value of the tracked property.
     * @param value allowed object is {@link Boolean }
     */
    public void setTracked(Boolean value)
    {
        this.tracked = value;
    }

    /**
     * Gets the value of the completionSetByContent property.
     * @return possible object is {@link Boolean }
     */
    public boolean isCompletionSetByContent()
    {
        if (completionSetByContent == null)
        {
            return false;
        }
        else
        {
            return completionSetByContent;
        }
    }

    /**
     * Sets the value of the completionSetByContent property.
     * @param value allowed object is {@link Boolean }
     */
    public void setCompletionSetByContent(Boolean value)
    {
        this.completionSetByContent = value;
    }

    /**
     * Gets the value of the objectiveSetByContent property.
     * @return possible object is {@link Boolean }
     */
    public boolean isObjectiveSetByContent()
    {
        if (objectiveSetByContent == null)
        {
            return false;
        }
        else
        {
            return objectiveSetByContent;
        }
    }

    /**
     * Sets the value of the objectiveSetByContent property.
     * @param value allowed object is {@link Boolean }
     */
    public void setObjectiveSetByContent(Boolean value)
    {
        this.objectiveSetByContent = value;
    }

}
