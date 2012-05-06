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

package net.sourceforge.openutils.mgnllms.scorm.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>
 * Java class for resource complex type.
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="resource">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.imsglobal.org/xsd/imscp_v1p1}metadata" minOccurs="0"/>
 *         &lt;element ref="{http://www.imsglobal.org/xsd/imscp_v1p1}file" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.imsglobal.org/xsd/imscp_v1p1}dependency" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attGroup ref="{http://www.imsglobal.org/xsd/imscp_v1p1}attr.href"/>
 *       &lt;attGroup ref="{http://www.imsglobal.org/xsd/imscp_v1p1}attr.resourcetype.req"/>
 *       &lt;attGroup ref="{http://www.imsglobal.org/xsd/imscp_v1p1}attr.base"/>
 *       &lt;attGroup ref="{http://www.imsglobal.org/xsd/imscp_v1p1}attr.identifier.req"/>
 *       &lt;attribute ref="{http://www.adlnet.org/xsd/adlcp_v1p3}scormType"/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "resource", propOrder = {"metadata", "file", "dependency" })
public class Resource
{

    protected Metadata metadata;

    protected List<File> file;

    protected List<Dependency> dependency;

    @XmlAttribute(namespace = "http://www.adlnet.org/xsd/adlcp_v1p3")
    protected String scormType;

    @XmlAttribute
    @XmlSchemaType(name = "anyURI")
    protected String href;

    @XmlAttribute(required = true)
    protected String type;

    @XmlAttribute(namespace = "http://www.w3.org/XML/1998/namespace")
    @XmlSchemaType(name = "anyURI")
    protected String base;

    @XmlAttribute(required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    @XmlSchemaType(name = "ID")
    protected String identifier;

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
     * Gets the value of the file property.
     * <p>
     * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make to
     * the returned list will be present inside the JAXB object. This is why there is not a <CODE>set</CODE> method for
     * the file property.
     * <p>
     * For example, to add a new item, do as follows:
     * 
     * <pre>
     *    getFile().add(newItem);
     * </pre>
     * <p>
     * Objects of the following type(s) are allowed in the list {@link File }
     */
    public List<File> getFile()
    {
        if (file == null)
        {
            file = new ArrayList<File>();
        }
        return this.file;
    }

    /**
     * Gets the value of the dependency property.
     * <p>
     * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make to
     * the returned list will be present inside the JAXB object. This is why there is not a <CODE>set</CODE> method for
     * the dependency property.
     * <p>
     * For example, to add a new item, do as follows:
     * 
     * <pre>
     *    getDependency().add(newItem);
     * </pre>
     * <p>
     * Objects of the following type(s) are allowed in the list {@link Dependency }
     */
    public List<Dependency> getDependency()
    {
        if (dependency == null)
        {
            dependency = new ArrayList<Dependency>();
        }
        return this.dependency;
    }

    /**
     * Gets the value of the scormType property.
     * @return possible object is {@link String }
     */
    public String getScormType()
    {
        return scormType;
    }

    /**
     * Sets the value of the scormType property.
     * @param value allowed object is {@link String }
     */
    public void setScormType(String value)
    {
        this.scormType = value;
    }

    /**
     * Gets the value of the href property.
     * @return possible object is {@link String }
     */
    public String getHref()
    {
        return href;
    }

    /**
     * Sets the value of the href property.
     * @param value allowed object is {@link String }
     */
    public void setHref(String value)
    {
        this.href = value;
    }

    /**
     * Gets the value of the type property.
     * @return possible object is {@link String }
     */
    public String getType()
    {
        return type;
    }

    /**
     * Sets the value of the type property.
     * @param value allowed object is {@link String }
     */
    public void setType(String value)
    {
        this.type = value;
    }

    /**
     * Gets the value of the base property.
     * @return possible object is {@link String }
     */
    public String getBase()
    {
        return base;
    }

    /**
     * Sets the value of the base property.
     * @param value allowed object is {@link String }
     */
    public void setBase(String value)
    {
        this.base = value;
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

}
