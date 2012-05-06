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

package net.sourceforge.openutils.mgnllms.scorm.utils;

import info.magnolia.cms.core.Content;
import info.magnolia.cms.core.HierarchyManager;
import info.magnolia.cms.util.NodeDataUtil;
import info.magnolia.context.MgnlContext;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.jcr.RepositoryException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import net.sourceforge.openutils.mgnllms.scorm.model.Manifest;

import org.apache.commons.io.IOUtils;


/**
 * @author luca boati
 * @version $Id: $
 */
public class JaxbUtils
{

    public static Manifest getManifest(Content course) throws RepositoryException, JAXBException
    {
        InputStream is = null;
        try
        {
            String manifestStr = NodeDataUtil.getString(course, "manifestStr");
            is = new ByteArrayInputStream(manifestStr.getBytes());

            Manifest manifest = unmarshal(Manifest.class, is);

            return manifest;
        }
        finally
        {
            if (is != null)
            {
                IOUtils.closeQuietly(is);
            }
        }
    }

    public static Manifest getManifest(String path, String repository) throws RepositoryException, JAXBException
    {
        HierarchyManager hm = MgnlContext.getInstance().getHierarchyManager(repository);
        Content c = hm.getContent(path);
        return getManifest(c);
    }

    @SuppressWarnings("unchecked")
    public static <T> T unmarshal(Class<T> docClass, InputStream inputStream) throws JAXBException
    {
        String packageName = docClass.getPackage().getName();
        JAXBContext jc = JAXBContext.newInstance(packageName);
        Unmarshaller u = jc.createUnmarshaller();
        JAXBElement<T> doc = (JAXBElement<T>) u.unmarshal(inputStream);
        return doc.getValue();
    }
}
