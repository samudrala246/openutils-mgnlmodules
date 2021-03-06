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

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import net.sourceforge.openutils.mgnllms.scorm.model.seq.ConstrainChoiceConsiderations;
import net.sourceforge.openutils.mgnllms.scorm.model.seq.RollupConsiderations;


/**
 * The type associated with any top-level sequencing tag
 * <p>
 * Java class for sequencing complex type.
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="sequencing">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="controlMode" type="{http://www.imsglobal.org/xsd/imsss}controlMode" minOccurs="0"/>
 *         &lt;element name="sequencingRules" type="{http://www.imsglobal.org/xsd/imsss}sequencingRules" minOccurs="0"/>
 *         &lt;element name="limitConditions" type="{http://www.imsglobal.org/xsd/imsss}limitConditions" minOccurs="0"/>
 *         &lt;element name="auxiliaryResources" type="{http://www.imsglobal.org/xsd/imsss}auxiliaryResources" minOccurs="0"/>
 *         &lt;element name="rollupRules" type="{http://www.imsglobal.org/xsd/imsss}rollupRules" minOccurs="0"/>
 *         &lt;element name="objectives" type="{http://www.imsglobal.org/xsd/imsss}objectives" minOccurs="0"/>
 *         &lt;element name="randomizationControls" type="{http://www.imsglobal.org/xsd/imsss}randomization" minOccurs="0"/>
 *         &lt;element name="deliveryControls" type="{http://www.imsglobal.org/xsd/imsss}deliveryControls" minOccurs="0"/>
 *         &lt;element ref="{http://www.adlnet.org/xsd/adlseq_v1p3}constrainedChoiceConsiderations" minOccurs="0"/>
 *         &lt;element ref="{http://www.adlnet.org/xsd/adlseq_v1p3}rollupConsiderations" minOccurs="0"/>
 *         &lt;element ref="{http://www.adlnet.org/xsd/adlseq_v1p3}objectives" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="ID" type="{http://www.w3.org/2001/XMLSchema}ID" />
 *       &lt;attribute name="IDRef" type="{http://www.w3.org/2001/XMLSchema}IDREF" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "sequencing", propOrder = {
    "controlMode",
    "sequencingRules",
    "limitConditions",
    "auxiliaryResources",
    "rollupRules",
    "objectives",
    "randomizationControls",
    "deliveryControls",
    "constrainedChoiceConsiderations",
    "rollupConsiderations",
    "adlseqObjectives" })
public class Sequencing
{

    protected ControlMode controlMode;

    protected SequencingRules sequencingRules;

    protected LimitConditions limitConditions;

    @XmlElementWrapper
    protected List<AuxiliaryResource> auxiliaryResources;

    protected RollupRules rollupRules;

    protected net.sourceforge.openutils.mgnllms.scorm.model.imsss.Objectives objectives;

    protected Randomization randomizationControls;

    protected DeliveryControls deliveryControls;

    @XmlElement(namespace = "http://www.adlnet.org/xsd/adlseq_v1p3")
    protected ConstrainChoiceConsiderations constrainedChoiceConsiderations;

    @XmlElement(namespace = "http://www.adlnet.org/xsd/adlseq_v1p3")
    protected RollupConsiderations rollupConsiderations;

    @XmlElement(name = "objectives", namespace = "http://www.adlnet.org/xsd/adlseq_v1p3")
    protected net.sourceforge.openutils.mgnllms.scorm.model.seq.Objectives adlseqObjectives;

    @XmlAttribute(name = "ID")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    @XmlSchemaType(name = "ID")
    protected String id;

    @XmlAttribute(name = "IDRef")
    @XmlIDREF
    @XmlSchemaType(name = "IDREF")
    protected Sequencing idRef;

    /**
     * Gets the value of the controlMode property.
     * @return possible object is {@link ControlMode }
     */
    public ControlMode getControlMode()
    {
        if (controlMode == null && idRef != null)
        {
            return idRef.getControlMode();
        }
        return controlMode;
    }

    /**
     * Sets the value of the controlMode property.
     * @param value allowed object is {@link ControlMode }
     */
    public void setControlMode(ControlMode value)
    {
        this.controlMode = value;
    }

    /**
     * Gets the value of the sequencingRules property.
     * @return possible object is {@link SequencingRules }
     */
    public SequencingRules getSequencingRules()
    {
        if (sequencingRules == null && idRef != null)
        {
            return idRef.sequencingRules;
        }
        return sequencingRules;
    }

    /**
     * Sets the value of the sequencingRules property.
     * @param value allowed object is {@link SequencingRules }
     */
    public void setSequencingRules(SequencingRules value)
    {
        this.sequencingRules = value;
    }

    /**
     * Gets the value of the limitConditions property.
     * @return possible object is {@link LimitConditions }
     */
    public LimitConditions getLimitConditions()
    {
        if (limitConditions == null && idRef != null)
        {
            return idRef.limitConditions;
        }
        return limitConditions;
    }

    /**
     * Sets the value of the limitConditions property.
     * @param value allowed object is {@link LimitConditions }
     */
    public void setLimitConditions(LimitConditions value)
    {
        this.limitConditions = value;
    }

    /**
     * Gets the value of the auxiliaryResources property.
     * @return possible object is {@link AuxiliaryResources }
     */
    public List<AuxiliaryResource> getAuxiliaryResources()
    {
        if (auxiliaryResources == null && idRef != null)
        {
            return idRef.auxiliaryResources;
        }
        return auxiliaryResources;
    }

    /**
     * Sets the value of the auxiliaryResources property.
     * @param value allowed object is {@link AuxiliaryResources }
     */
    public void setAuxiliaryResources(List<AuxiliaryResource> value)
    {
        this.auxiliaryResources = value;
    }

    /**
     * Gets the value of the rollupRules property.
     * @return possible object is {@link RollupRules }
     */
    public RollupRules getRollupRules()
    {
        if (rollupRules == null && idRef != null)
        {
            return idRef.rollupRules;
        }
        return rollupRules;
    }

    /**
     * Sets the value of the rollupRules property.
     * @param value allowed object is {@link RollupRules }
     */
    public void setRollupRules(RollupRules value)
    {
        this.rollupRules = value;
    }

    /**
     * Gets the value of the objectives property.
     * @return possible object is {@link net.sourceforge.openutils.mgnllms.scorm.model.imsss.Objectives }
     */
    public net.sourceforge.openutils.mgnllms.scorm.model.imsss.Objectives getObjectives()
    {
        if (objectives == null && idRef != null)
        {
            return idRef.objectives;
        }
        return objectives;
    }

    /**
     * Sets the value of the objectives property.
     * @param value allowed object is {@link net.sourceforge.openutils.mgnllms.scorm.model.imsss.Objectives }
     */
    public void setObjectives(net.sourceforge.openutils.mgnllms.scorm.model.imsss.Objectives value)
    {
        this.objectives = value;
    }

    /**
     * Gets the value of the randomizationControls property.
     * @return possible object is {@link Randomization }
     */
    public Randomization getRandomizationControls()
    {
        if (randomizationControls == null && idRef != null)
        {
            return idRef.randomizationControls;
        }
        return randomizationControls;
    }

    /**
     * Sets the value of the randomizationControls property.
     * @param value allowed object is {@link Randomization }
     */
    public void setRandomizationControls(Randomization value)
    {
        this.randomizationControls = value;
    }

    /**
     * Gets the value of the deliveryControls property.
     * @return possible object is {@link DeliveryControls }
     */
    public DeliveryControls getDeliveryControls()
    {
        if (deliveryControls == null && idRef != null)
        {
            return idRef.deliveryControls;
        }
        return deliveryControls;
    }

    /**
     * Sets the value of the deliveryControls property.
     * @param value allowed object is {@link DeliveryControls }
     */
    public void setDeliveryControls(DeliveryControls value)
    {
        this.deliveryControls = value;
    }

    /**
     * Gets the value of the constrainedChoiceConsiderations property.
     * @return possible object is {@link ConstrainChoiceConsiderations }
     */
    public ConstrainChoiceConsiderations getConstrainedChoiceConsiderations()
    {
        if (constrainedChoiceConsiderations == null && idRef != null)
        {
            return idRef.constrainedChoiceConsiderations;
        }
        return constrainedChoiceConsiderations;
    }

    /**
     * Sets the value of the constrainedChoiceConsiderations property.
     * @param value allowed object is {@link ConstrainChoiceConsiderations }
     */
    public void setConstrainedChoiceConsiderations(ConstrainChoiceConsiderations value)
    {
        this.constrainedChoiceConsiderations = value;
    }

    /**
     * Gets the value of the rollupConsiderations property.
     * @return possible object is {@link RollupConsiderations }
     */
    public RollupConsiderations getRollupConsiderations()
    {
        if (rollupConsiderations == null && idRef != null)
        {
            return idRef.rollupConsiderations;
        }
        return rollupConsiderations;
    }

    /**
     * Sets the value of the rollupConsiderations property.
     * @param value allowed object is {@link RollupConsiderations }
     */
    public void setRollupConsiderations(RollupConsiderations value)
    {
        this.rollupConsiderations = value;
    }

    /**
     * Gets the value of the adlseqObjectives property.
     * @return possible object is {@link net.sourceforge.openutils.mgnllms.scorm.model.seq.Objectives }
     */
    public net.sourceforge.openutils.mgnllms.scorm.model.seq.Objectives getAdlseqObjectives()
    {
        if (adlseqObjectives == null && idRef != null)
        {
            return idRef.adlseqObjectives;
        }

        return adlseqObjectives;
    }

    /**
     * Sets the value of the adlseqObjectives property.
     * @param value allowed object is {@link net.sourceforge.openutils.mgnllms.scorm.model.seq.Objectives }
     */
    public void setAdlseqObjectives(net.sourceforge.openutils.mgnllms.scorm.model.seq.Objectives value)
    {
        this.adlseqObjectives = value;
    }

    /**
     * Gets the value of the id property.
     * @return possible object is {@link String }
     */
    public String getID()
    {
        return id;
    }

    /**
     * Sets the value of the id property.
     * @param value allowed object is {@link String }
     */
    public void setID(String value)
    {
        this.id = value;
    }

    /**
     * Gets the value of the idRef property.
     * @return possible object is {@link Object }
     */
    public Sequencing getIDRef()
    {
        return idRef;
    }

    /**
     * Sets the value of the idRef property.
     * @param value allowed object is {@link Object }
     */
    public void setIDRef(Sequencing value)
    {
        this.idRef = value;
    }

}
