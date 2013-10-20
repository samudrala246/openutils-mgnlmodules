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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>
 * Java class for map complex type.
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="map">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="targetID" use="required" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *       &lt;attribute name="readSharedData" type="{http://www.w3.org/2001/XMLSchema}boolean" default="true" />
 *       &lt;attribute name="writeSharedData" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "map")
public class Map
{

    @XmlAttribute(required = true)
    @XmlSchemaType(name = "anyURI")
    protected String targetID;

    @XmlAttribute
    protected Boolean readSharedData;

    @XmlAttribute
    protected Boolean writeSharedData;

    /**
     * Gets the value of the targetID property.
     * @return possible object is {@link String }
     */
    public String getTargetID()
    {
        return targetID;
    }

    /**
     * Sets the value of the targetID property.
     * @param value allowed object is {@link String }
     */
    public void setTargetID(String value)
    {
        this.targetID = value;
    }

    /**
     * Gets the value of the readSharedData property.
     * @return possible object is {@link Boolean }
     */
    public boolean isReadSharedData()
    {
        if (readSharedData == null)
        {
            return true;
        }
        else
        {
            return readSharedData;
        }
    }

    /**
     * Sets the value of the readSharedData property.
     * @param value allowed object is {@link Boolean }
     */
    public void setReadSharedData(Boolean value)
    {
        this.readSharedData = value;
    }

    /**
     * Gets the value of the writeSharedData property.
     * @return possible object is {@link Boolean }
     */
    public boolean isWriteSharedData()
    {
        if (writeSharedData == null)
        {
            return true;
        }
        else
        {
            return writeSharedData;
        }
    }

    /**
     * Sets the value of the writeSharedData property.
     * @param value allowed object is {@link Boolean }
     */
    public void setWriteSharedData(Boolean value)
    {
        this.writeSharedData = value;
    }

}
