/**
 *
 * SimpleMedia Module for Magnolia CMS (http://www.openmindlab.com/lab/products/media.html)
 * Copyright(C) 2008-2013, Openmind S.r.l. http://www.openmindonline.it
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

package net.sourceforge.openutils.mgnlmedia.media.utils;

import info.magnolia.test.mock.jcr.MockNode;
import info.magnolia.test.mock.jcr.MockValue;

import java.io.IOException;
import java.io.InputStream;

import javax.jcr.Node;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.jackrabbit.value.BinaryImpl;


/**
 * @author fgiust
 * @version $Id: FileNodeData.java 4331 2013-09-20 12:38:39Z fgiust $
 */
public class FileNode extends MockNode implements Node
{

    /**
     * @param classpathLocation
     * @throws IOException
     */
    public FileNode(String classpathLocation) throws IOException
    {
        super(StringUtils.substringAfterLast(classpathLocation, "/"));
        InputStream is = getClass().getResourceAsStream(classpathLocation);

        if (is == null)
        {
            throw new IllegalArgumentException("Classpath resource " + classpathLocation + " cannot be found");
        }
        String extension = StringUtils.substringAfterLast(classpathLocation, ".");
        setProperty("extension", extension);

        try
        {
            byte[] byteArray = IOUtils.toByteArray(is);
            setProperty("jcr:data", new MockValue(new BinaryImpl(byteArray)));
        }
        finally
        {
            IOUtils.closeQuietly(is);
        }

    }

}
