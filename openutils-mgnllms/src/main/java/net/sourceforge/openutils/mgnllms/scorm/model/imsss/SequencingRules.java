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

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>
 * Java class for sequencingRules complex type.
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="sequencingRules">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="preConditionRule" type="{http://www.imsglobal.org/xsd/imsss}preConditionRule" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="exitConditionRule" type="{http://www.imsglobal.org/xsd/imsss}exitConditionRule" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="postConditionRule" type="{http://www.imsglobal.org/xsd/imsss}postConditionRule" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "sequencingRules", propOrder = {"preConditionRule", "exitConditionRule", "postConditionRule" })
public class SequencingRules
{

    protected List<PreConditionRule> preConditionRule;

    protected List<ExitConditionRule> exitConditionRule;

    protected List<PostConditionRule> postConditionRule;

    /**
     * Gets the value of the preConditionRule property.
     * <p>
     * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make to
     * the returned list will be present inside the JAXB object. This is why there is not a <CODE>set</CODE> method for
     * the preConditionRule property.
     * <p>
     * For example, to add a new item, do as follows:
     * 
     * <pre>
     *    getPreConditionRule().add(newItem);
     * </pre>
     * <p>
     * Objects of the following type(s) are allowed in the list {@link PreConditionRule }
     */
    public List<PreConditionRule> getPreConditionRule()
    {
        if (preConditionRule == null)
        {
            preConditionRule = new ArrayList<PreConditionRule>();
        }
        return this.preConditionRule;
    }

    /**
     * Gets the value of the exitConditionRule property.
     * <p>
     * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make to
     * the returned list will be present inside the JAXB object. This is why there is not a <CODE>set</CODE> method for
     * the exitConditionRule property.
     * <p>
     * For example, to add a new item, do as follows:
     * 
     * <pre>
     *    getExitConditionRule().add(newItem);
     * </pre>
     * <p>
     * Objects of the following type(s) are allowed in the list {@link ExitConditionRule }
     */
    public List<ExitConditionRule> getExitConditionRule()
    {
        if (exitConditionRule == null)
        {
            exitConditionRule = new ArrayList<ExitConditionRule>();
        }
        return this.exitConditionRule;
    }

    /**
     * Gets the value of the postConditionRule property.
     * <p>
     * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make to
     * the returned list will be present inside the JAXB object. This is why there is not a <CODE>set</CODE> method for
     * the postConditionRule property.
     * <p>
     * For example, to add a new item, do as follows:
     * 
     * <pre>
     *    getPostConditionRule().add(newItem);
     * </pre>
     * <p>
     * Objects of the following type(s) are allowed in the list {@link PostConditionRule }
     */
    public List<PostConditionRule> getPostConditionRule()
    {
        if (postConditionRule == null)
        {
            postConditionRule = new ArrayList<PostConditionRule>();
        }
        return this.postConditionRule;
    }

}
