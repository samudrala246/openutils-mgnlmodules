/**
 *
 * Stripes module for Magnolia CMS (http://www.openmindlab.com/lab/products/mgnlstripes.html)
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

package it.openutils.magnoliastripes;

import info.magnolia.cms.beans.runtime.Document;
import info.magnolia.cms.beans.runtime.MultipartForm;
import info.magnolia.context.Context;
import info.magnolia.context.MgnlContext;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;

import net.sourceforge.stripes.action.FileBean;
import net.sourceforge.stripes.controller.FileUploadLimitExceededException;
import net.sourceforge.stripes.controller.multipart.MultipartWrapper;

import org.apache.commons.io.IOUtils;


/**
 * An implementation of MultipartWrapper that delegates to the standard magnolia multipart form handling.
 * @author fgiust
 * @version $Id$
 */
public class MagnoliaMultipartWrapper implements MultipartWrapper
{

    /**
     * Nothing to do here {@inheritDoc}
     */
    public void build(HttpServletRequest request, File tempDir, long maxPostSize) throws IOException,
        FileUploadLimitExceededException
    {
        // nothing to do, already provided by magnolia

    }

    private MultipartForm getForm()
    {
        return (MultipartForm) MgnlContext.getAttribute(MultipartForm.REQUEST_ATTRIBUTE_NAME, Context.LOCAL_SCOPE);
    }

    /**
     * {@inheritDoc}
     */
    public Enumeration<String> getParameterNames()
    {
        // All params are already included in request
        return new Enumeration<String>()
        {

            public boolean hasMoreElements()
            {
                return false;
            }

            public String nextElement()
            {
                return null;
            }
        };
    }

    /**
     * {@inheritDoc}
     */
    public String[] getParameterValues(String name)
    {
        // All params are already included in request
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public Enumeration<String> getFileParameterNames()
    {
        return new IteratorEnumeration(getForm().getDocuments().keySet().iterator());
    }

    /**
     * {@inheritDoc}
     */
    public FileBean getFileParameterValue(String name)
    {
        final Document item = getForm().getDocument(name);
        if (item == null)
        {
            return null;
        }
        else
        {
            // Use an anonymous inner subclass of FileBean that overrides all the
            // methods that rely on having a File present, to use the FileItem
            // created by commons upload instead.
            return new FileBean(null, item.getType(), item.getFileNameWithExtension())
            {

                @Override
                public long getSize()
                {
                    return item.getLength();
                }

                @Override
                public InputStream getInputStream() throws IOException
                {
                    return item.getStream();
                }

                @Override
                public void save(File toFile) throws IOException
                {
                    OutputStream os = null;
                    InputStream is = null;
                    try
                    {
                        os = new BufferedOutputStream(new FileOutputStream(toFile));
                        is = item.getStream();
                        IOUtils.copyLarge(is, os);

                        delete();
                    }
                    catch (Exception e)
                    {
                        if (e instanceof IOException)
                        {
                            throw (IOException) e;
                        }
                        else
                        {
                            IOException ioe = new IOException("Problem saving uploaded file.");
                            ioe.initCause(e);
                            throw ioe;
                        }
                    }
                    finally
                    {
                        IOUtils.closeQuietly(is);
                        IOUtils.closeQuietly(os);
                    }
                }

                @Override
                public void delete() throws IOException
                {
                    item.delete();
                }
            };
        }
    }

    /**
     * Little helper class to create an enumeration as per the interface.
     */
    private static class IteratorEnumeration implements Enumeration<String>
    {

        Iterator<String> iterator;

        /**
         * Constructs an enumeration that consumes from the underlying iterator.
         */
        IteratorEnumeration(Iterator<String> iterator)
        {
            this.iterator = iterator;
        }

        /**
         * Returns true if more elements can be consumed, false otherwise.
         */
        public boolean hasMoreElements()
        {
            return this.iterator.hasNext();
        }

        /**
         * Gets the next element out of the iterator.
         */
        public String nextElement()
        {
            return this.iterator.next();
        }
    }
}
