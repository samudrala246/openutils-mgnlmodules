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

package net.sourceforge.openutils.mgnllms.scorm.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.namespace.QName;

import net.sourceforge.openutils.mgnllms.scorm.model.cp.CompletionThreshold;
import net.sourceforge.openutils.mgnllms.scorm.model.cp.Data;
import net.sourceforge.openutils.mgnllms.scorm.model.cp.TimeLimitAction;
import net.sourceforge.openutils.mgnllms.scorm.model.imsss.Sequencing;
import net.sourceforge.openutils.mgnllms.scorm.model.nav.Presentation;


/**
 * <p>
 * Java class for item complex type.
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="item">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.imsglobal.org/xsd/imscp_v1p1}title" minOccurs="0"/>
 *         &lt;element ref="{http://www.imsglobal.org/xsd/imscp_v1p1}item" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.imsglobal.org/xsd/imscp_v1p1}metadata" minOccurs="0"/>
 *         &lt;element ref="{http://www.imsglobal.org/xsd/imsss}sequencing" minOccurs="0"/>
 *         &lt;element ref="{http://www.adlnet.org/xsd/adlnav_v1p3}presentation" minOccurs="0"/>
 *         &lt;element ref="{http://www.adlnet.org/xsd/adlcp_v1p3}timeLimitAction" minOccurs="0"/>
 *         &lt;element ref="{http://www.adlnet.org/xsd/adlcp_v1p3}dataFromLMS" minOccurs="0"/>
 *         &lt;element ref="{http://www.adlnet.org/xsd/adlcp_v1p3}completionThreshold" minOccurs="0"/>
 *         &lt;element ref="{http://www.adlnet.org/xsd/adlcp_v1p3}data" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attGroup ref="{http://www.imsglobal.org/xsd/imscp_v1p1}attr.isvisible"/>
 *       &lt;attGroup ref="{http://www.imsglobal.org/xsd/imscp_v1p1}attr.identifierref"/>
 *       &lt;attGroup ref="{http://www.imsglobal.org/xsd/imscp_v1p1}attr.parameters"/>
 *       &lt;attGroup ref="{http://www.imsglobal.org/xsd/imscp_v1p1}attr.identifier.req"/>
 *       &lt;anyAttribute processContents='lax' namespace='##other'/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "item", propOrder = {
    "title",
    "item",
    "metadata",
    "sequencing",
    "presentation",
    "timeLimitAction",
    "dataFromLMS",
    "completionThreshold",
    "data" })
public class Item implements CommonDataItemOrganization
{

    protected String title;

    protected List<Item> item;

    protected Metadata metadata;

    @XmlElement(namespace = "http://www.imsglobal.org/xsd/imsss")
    protected Sequencing sequencing;

    @XmlElement(namespace = "http://www.adlnet.org/xsd/adlnav_v1p3")
    protected Presentation presentation;

    @XmlElement(namespace = "http://www.adlnet.org/xsd/adlcp_v1p3")
    protected TimeLimitAction timeLimitAction;

    @XmlElement(namespace = "http://www.adlnet.org/xsd/adlcp_v1p3")
    protected String dataFromLMS;

    @XmlElement(namespace = "http://www.adlnet.org/xsd/adlcp_v1p3")
    protected CompletionThreshold completionThreshold;

    @XmlElement(namespace = "http://www.adlnet.org/xsd/adlcp_v1p3")
    protected Data data;

    @XmlAttribute
    protected Boolean isvisible = true; // default value

    @XmlAttribute
    protected String identifierref;

    @XmlAttribute
    protected String parameters;

    @XmlAttribute(required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    @XmlSchemaType(name = "ID")
    protected String identifier;

    @XmlAnyAttribute
    private Map<QName, String> otherAttributes = new HashMap<QName, String>();

    /**
     * Gets the value of the title property.
     * @return possible object is {@link String }
     */
    public String getTitle()
    {
        return title;
    }

    /**
     * Sets the value of the title property.
     * @param value allowed object is {@link String }
     */
    public void setTitle(String value)
    {
        this.title = value;
    }

    /**
     * Gets the value of the item property.
     * <p>
     * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make to
     * the returned list will be present inside the JAXB object. This is why there is not a <CODE>set</CODE> method for
     * the item property.
     * <p>
     * For example, to add a new item, do as follows:
     * 
     * <pre>
     *    getItem().add(newItem);
     * </pre>
     * <p>
     * Objects of the following type(s) are allowed in the list {@link Item }
     */
    public List<Item> getItem()
    {
        if (item == null)
        {
            item = new ArrayList<Item>();
        }
        return this.item;
    }

    /**
     * Gets the value of the metadata property.
     * @return possible object is {@link Metadata }
     */
    public Metadata getMetadata()
    {
        return metadata;
    }

    /**
     * Sets the value of the metadata property.
     * @param value allowed object is {@link Metadata }
     */
    public void setMetadata(Metadata value)
    {
        this.metadata = value;
    }

    /**
     * Gets the value of the sequencing property.
     * @return possible object is {@link Sequencing }
     */
    public Sequencing getSequencing()
    {
        return sequencing;
    }

    /**
     * Sets the value of the sequencing property.
     * @param value allowed object is {@link Sequencing }
     */
    public void setSequencing(Sequencing value)
    {
        this.sequencing = value;
    }

    /**
     * Gets the value of the presentation property.
     * @return possible object is {@link Presentation }
     */
    public Presentation getPresentation()
    {
        return presentation;
    }

    /**
     * Sets the value of the presentation property.
     * @param value allowed object is {@link Presentation }
     */
    public void setPresentation(Presentation value)
    {
        this.presentation = value;
    }

    /**
     * Gets the value of the timeLimitAction property.
     * @return possible object is {@link TimeLimitAction }
     */
    public TimeLimitAction getTimeLimitAction()
    {
        return timeLimitAction;
    }

    /**
     * Sets the value of the timeLimitAction property.
     * @param value allowed object is {@link TimeLimitAction }
     */
    public void setTimeLimitAction(TimeLimitAction value)
    {
        this.timeLimitAction = value;
    }

    /**
     * Gets the value of the dataFromLMS property.
     * @return possible object is {@link String }
     */
    public String getDataFromLMS()
    {
        return dataFromLMS;
    }

    /**
     * Sets the value of the dataFromLMS property.
     * @param value allowed object is {@link String }
     */
    public void setDataFromLMS(String value)
    {
        this.dataFromLMS = value;
    }

    /**
     * Gets the value of the completionThreshold property.
     * @return possible object is {@link CompletionThreshold }
     */
    public CompletionThreshold getCompletionThreshold()
    {
        return completionThreshold;
    }

    /**
     * Sets the value of the completionThreshold property.
     * @param value allowed object is {@link CompletionThreshold }
     */
    public void setCompletionThreshold(CompletionThreshold value)
    {
        this.completionThreshold = value;
    }

    /**
     * Gets the value of the data property.
     * @return possible object is {@link Data }
     */
    public Data getData()
    {
        return data;
    }

    /**
     * Sets the value of the data property.
     * @param value allowed object is {@link Data }
     */
    public void setData(Data value)
    {
        this.data = value;
    }

    /**
     * Gets the value of the isvisible property.
     * @return possible object is {@link Boolean }
     */
    public Boolean getIsvisible()
    {
        return isvisible;
    }

    /**
     * Sets the value of the isvisible property.
     * @param value allowed object is {@link Boolean }
     */
    public void setIsvisible(Boolean value)
    {
        this.isvisible = value;
    }

    /**
     * Gets the value of the identifierref property.
     * @return possible object is {@link String }
     */
    public String getIdentifierref()
    {
        return identifierref;
    }

    /**
     * Sets the value of the identifierref property.
     * @param value allowed object is {@link String }
     */
    public void setIdentifierref(String value)
    {
        this.identifierref = value;
    }

    /**
     * Gets the value of the parameters property.
     * @return possible object is {@link String }
     */
    public String getParameters()
    {
        return parameters;
    }

    /**
     * Sets the value of the parameters property.
     * @param value allowed object is {@link String }
     */
    public void setParameters(String value)
    {
        this.parameters = value;
    }

    /**
     * Gets the value of the identifier property.
     * @return possible object is {@link String }
     */
    public String getIdentifier()
    {
        return identifier;
    }

    /**
     * Sets the value of the identifier property.
     * @param value allowed object is {@link String }
     */
    public void setIdentifier(String value)
    {
        this.identifier = value;
    }

    /**
     * Gets a map that contains attributes that aren't bound to any typed property on this class.
     * <p>
     * the map is keyed by the name of the attribute and the value is the string value of the attribute. the map
     * returned by this method is live, and you can add new attribute by updating the map directly. Because of this
     * design, there's no setter.
     * @return always non-null
     */
    public Map<QName, String> getOtherAttributes()
    {
        return otherAttributes;
    }

}