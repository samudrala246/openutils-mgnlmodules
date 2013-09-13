/**
 *
 * Generic utilities for Magnolia CMS (http://www.openmindlab.com/lab/products/mgnlutils.html)
 * Copyright(C) 2009-2012, Openmind S.r.l. http://www.openmindonline.it
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

package it.openutils.mgnlutils.api;

import info.magnolia.jcr.RuntimeRepositoryException;
import info.magnolia.jcr.util.PropertyUtil;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.commons.lang.StringUtils;


/**
 * @author fgiust
 * @version $Id: NodeUtilsExt.java 4292 2013-09-12 15:54:10Z fgiust $
 */
public class NodeUtilsExt
{

    public static boolean exists(Session session, String absolutepath)
    {
        try
        {
            return session.getRootNode().hasNode(StringUtils.stripStart(absolutepath, "/"));
        }
        catch (RepositoryException e)
        {
            throw new RuntimeRepositoryException(e);
        }
    }

    public static Node getNodeIfExists(Session session, String absolutepath)
    {
        if (exists(session, absolutepath))
        {
            try
            {
                return session.getNode(absolutepath);
            }
            catch (RepositoryException e)
            {
                throw new RuntimeRepositoryException(e);
            }
        }
        return null;
    }

    public static boolean deleteIfExisting(Session session, String absolutepath)
    {
        if (exists(session, absolutepath))
        {
            try
            {
                session.getNode(absolutepath).remove();
            }
            catch (RepositoryException e)
            {
                throw new RuntimeRepositoryException(e);
            }
            return true;
        }
        return false;
    }

    public static boolean setPropertyIfDifferent(Node node, String propertyname, Object propertyvalue)
    {
        return setPropertyIfDifferentFromValue(node, propertyname, propertyvalue, propertyvalue);
    }

    public static boolean setPropertyIfDifferentFromValue(Node node, String propertyname, Object propertyvalue,
        Object oldvalue)
    {
        try
        {
            if (!node.hasProperty(propertyname)
                || oldvalue == null
                || !StringUtils.equals(node.getProperty(propertyname).getString(), oldvalue.toString()))
            {
                PropertyUtil.setProperty(node, propertyname, propertyvalue);
                return true;
            }
        }
        catch (RepositoryException e)
        {
            throw new RuntimeRepositoryException(e);
        }
        return false;
    }

    public static void deletePropertyIfExist(Node node, String propertyname)
    {
        try
        {
            if (node != null && node.hasProperty(propertyname))
            {
                node.getProperty(propertyname).remove();
            }
        }
        catch (RepositoryException e)
        {
            throw new RuntimeRepositoryException(e);
        }
    }
}
