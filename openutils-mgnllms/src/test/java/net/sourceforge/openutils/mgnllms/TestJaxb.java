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
package net.sourceforge.openutils.mgnllms;

import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import net.sourceforge.openutils.mgnllms.scorm.model.Manifest;

import org.testng.Assert;
import org.testng.annotations.Test;


/**
 * @author carlo
 * @version $Id: $
 */
public class TestJaxb
{

    @Test
    public void test() throws JAXBException
    {
        Manifest manifest = unmarshal(Manifest.class, this.getClass().getResourceAsStream("/testjaxb/imsmanifest2.xml"));
        Assert.assertNotNull(manifest);
    }

    public <T> T unmarshal(Class<T> docClass, InputStream inputStream) throws JAXBException
    {
        String packageName = docClass.getPackage().getName();
        JAXBContext jc = JAXBContext.newInstance(packageName);
        Unmarshaller u = jc.createUnmarshaller();
        JAXBElement<T> doc = (JAXBElement<T>) u.unmarshal(inputStream);
        return doc.getValue();
    }

}
