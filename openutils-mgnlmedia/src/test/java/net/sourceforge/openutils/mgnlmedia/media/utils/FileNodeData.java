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

import info.magnolia.cms.core.Content;
import info.magnolia.cms.core.HierarchyManager;
import info.magnolia.cms.core.NodeData;
import info.magnolia.cms.security.AccessDeniedException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Collection;

import javax.jcr.ItemNotFoundException;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Value;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;


/**
 * @author fgiust
 * @version $Id$
 */
public class FileNodeData implements NodeData
{

    private byte[] content;

    private String extension;

    /**
     * @param classpathLocation
     * @throws IOException
     */
    public FileNodeData(String classpathLocation) throws IOException
    {
        InputStream is = getClass().getResourceAsStream(classpathLocation);

        if (is == null)
        {
            throw new IllegalArgumentException("Classpath resource " + classpathLocation + " cannot be found");
        }
        try
        {
            extension = StringUtils.substringAfterLast(classpathLocation, ".");

            content = IOUtils.toByteArray(is);
        }
        finally
        {
            IOUtils.closeQuietly(is);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void delete() throws RepositoryException
    {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     */
    public String getAttribute(String key)
    {
        if (StringUtils.equals(key, "extension"))
        {
            return this.extension;
        }
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public Collection getAttributeNames() throws RepositoryException
    {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public boolean getBoolean()
    {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public long getContentLength()
    {
        // TODO Auto-generated method stub
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    public Calendar getDate()
    {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public double getDouble()
    {
        // TODO Auto-generated method stub
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    public String getHandle()
    {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public HierarchyManager getHierarchyManager()
    {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public Property getJCRProperty()
    {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public long getLong()
    {
        // TODO Auto-generated method stub
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    public String getName()
    {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public Content getParent() throws AccessDeniedException, ItemNotFoundException, javax.jcr.AccessDeniedException,
        RepositoryException
    {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public Content getReferencedContent() throws RepositoryException, PathNotFoundException, RepositoryException
    {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public Content getReferencedContent(String arg0) throws PathNotFoundException, RepositoryException
    {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public InputStream getStream()
    {
        return new ByteArrayInputStream(content);
    }

    /**
     * {@inheritDoc}
     */
    public String getString()
    {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public String getString(String arg0)
    {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public int getType()
    {
        // TODO Auto-generated method stub
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    public Value getValue()
    {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public Value[] getValues()
    {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isExist()
    {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isGranted(long arg0)
    {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public int isMultiValue()
    {
        // TODO Auto-generated method stub
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    public void refresh(boolean arg0) throws RepositoryException
    {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     */
    public void save() throws RepositoryException
    {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     */
    public void setAttribute(String arg0, String arg1) throws RepositoryException, AccessDeniedException,
        UnsupportedOperationException
    {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     */
    public void setAttribute(String arg0, Calendar arg1) throws RepositoryException, AccessDeniedException,
        UnsupportedOperationException
    {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     */
    public void setValue(String arg0) throws RepositoryException, AccessDeniedException
    {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     */
    public void setValue(int arg0) throws RepositoryException, AccessDeniedException
    {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     */
    public void setValue(long arg0) throws RepositoryException, AccessDeniedException
    {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     */
    public void setValue(InputStream arg0) throws RepositoryException, AccessDeniedException
    {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     */
    public void setValue(double arg0) throws RepositoryException, AccessDeniedException
    {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     */
    public void setValue(boolean arg0) throws RepositoryException, AccessDeniedException
    {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     */
    public void setValue(Calendar arg0) throws RepositoryException, AccessDeniedException
    {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     */
    public void setValue(Value arg0) throws RepositoryException, AccessDeniedException
    {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     */
    public void setValue(Value[] arg0) throws RepositoryException, AccessDeniedException
    {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     */
    public void setValue(Content value) throws RepositoryException, AccessDeniedException
    {
        // TODO Auto-generated method stub
    }

}
