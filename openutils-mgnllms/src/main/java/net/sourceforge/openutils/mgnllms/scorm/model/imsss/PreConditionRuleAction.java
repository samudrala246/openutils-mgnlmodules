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

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>
 * Java class for preConditionRuleAction.
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 * <p>
 * 
 * <pre>
 * &lt;simpleType name="preConditionRuleAction">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token">
 *     &lt;enumeration value="skip"/>
 *     &lt;enumeration value="disabled"/>
 *     &lt;enumeration value="hiddenFromChoice"/>
 *     &lt;enumeration value="stopForwardTraversal"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 */
@XmlType(name = "preConditionRuleAction")
@XmlEnum
public enum PreConditionRuleAction {

    @XmlEnumValue("skip")
    SKIP("skip"), @XmlEnumValue("disabled")
    DISABLED("disabled"), @XmlEnumValue("hiddenFromChoice")
    HIDDENFROMCHOICE("hiddenFromChoice"), @XmlEnumValue("stopForwardTraversal")
    STOPFORWARDTRAVERSAL("stopForwardTraversal");

    private final String value;

    PreConditionRuleAction(String v)
    {
        value = v;
    }

    public String value()
    {
        return value;
    }

    public static PreConditionRuleAction fromValue(String v)
    {
        for (PreConditionRuleAction c : PreConditionRuleAction.values())
        {
            if (c.value.equals(v))
            {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
