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

//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2009.08.21 at 12:53:38 PM CEST 
//

package net.sourceforge.openutils.mgnllms.scorm.model.imsss;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * The specification states: "Each activity must have one and only one objective that contributes to rollup". The
 * following type describes an unbounded set of elements all named "objective" that do not contribute to rollup, and one
 * element called "primaryObjective" that contributes to rollup.
 * <p>
 * Java class for objectives complex type.
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="objectives">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="primaryObjective">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;extension base="{http://www.imsglobal.org/xsd/imsss}objectiveBase">
 *                 &lt;attribute name="objectiveID" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *               &lt;/extension>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="objective" maxOccurs="unbounded" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;extension base="{http://www.imsglobal.org/xsd/imsss}objectiveBase">
 *                 &lt;attribute name="objectiveID" use="required" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *               &lt;/extension>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "objectives", propOrder = {"primaryObjective", "objective" })
public class Objectives
{

    @XmlElement(required = true)
    protected PrimaryObjective primaryObjective;

    protected List<Objective> objective;

    /**
     * Gets the value of the primaryObjective property.
     * @return possible object is {@link PrimaryObjective }
     */
    public PrimaryObjective getPrimaryObjective()
    {
        return primaryObjective;
    }

    /**
     * Sets the value of the primaryObjective property.
     * @param value allowed object is {@link PrimaryObjective }
     */
    public void setPrimaryObjective(PrimaryObjective value)
    {
        this.primaryObjective = value;
    }

    /**
     * Gets the value of the objective property.
     * <p>
     * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make to
     * the returned list will be present inside the JAXB object. This is why there is not a <CODE>set</CODE> method for
     * the objective property.
     * <p>
     * For example, to add a new item, do as follows:
     * 
     * <pre>
     *    getObjective().add(newItem);
     * </pre>
     * <p>
     * Objects of the following type(s) are allowed in the list {@link Objective }
     */
    public List<Objective> getObjective()
    {
        if (objective == null)
        {
            objective = new ArrayList<Objective>();
        }
        return this.objective;
    }

}
