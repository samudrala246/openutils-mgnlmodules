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

package net.sourceforge.openutils.mgnllms.scorm.model.nav;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each Java content interface and Java element interface generated in the
 * net.sourceforge.openutils.mgnllms.scorm.model.nav package.
 * <p>
 * An ObjectFactory allows you to programatically construct new instances of the Java representation for XML content.
 * The Java representation of XML content can consist of schema derived interfaces and classes representing the binding
 * of schema type definitions, element declarations and model groups. Factory methods for each of these are provided in
 * this class.
 */
@XmlRegistry
public class ObjectFactory
{

    private final static QName _HideLMSUI_QNAME = new QName("http://www.adlnet.org/xsd/adlnav_v1p3", "hideLMSUI");

    private final static QName _Presentation_QNAME = new QName("http://www.adlnet.org/xsd/adlnav_v1p3", "presentation");

    private final static QName _NavigationInterface_QNAME = new QName(
        "http://www.adlnet.org/xsd/adlnav_v1p3",
        "navigationInterface");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package:
     * net.sourceforge.openutils.mgnllms.scorm.model.nav
     */
    public ObjectFactory()
    {
    }

    /**
     * Create an instance of {@link Presentation }
     */
    public Presentation createPresentation()
    {
        return new Presentation();
    }

    /**
     * Create an instance of {@link NavigationInterface }
     */
    public NavigationInterface createNavigationInterface()
    {
        return new NavigationInterface();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link HideLMSUI }{@code >}
     */
    @XmlElementDecl(namespace = "http://www.adlnet.org/xsd/adlnav_v1p3", name = "hideLMSUI")
    public JAXBElement<HideLMSUI> createHideLMSUI(HideLMSUI value)
    {
        return new JAXBElement<HideLMSUI>(_HideLMSUI_QNAME, HideLMSUI.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Presentation }{@code >}
     */
    @XmlElementDecl(namespace = "http://www.adlnet.org/xsd/adlnav_v1p3", name = "presentation")
    public JAXBElement<Presentation> createPresentation(Presentation value)
    {
        return new JAXBElement<Presentation>(_Presentation_QNAME, Presentation.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link NavigationInterface }{@code >}
     */
    @XmlElementDecl(namespace = "http://www.adlnet.org/xsd/adlnav_v1p3", name = "navigationInterface")
    public JAXBElement<NavigationInterface> createNavigationInterface(NavigationInterface value)
    {
        return new JAXBElement<NavigationInterface>(_NavigationInterface_QNAME, NavigationInterface.class, null, value);
    }

}