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

import info.magnolia.cms.core.Path;
import info.magnolia.content2bean.Content2BeanException;
import info.magnolia.content2bean.Content2BeanUtil;
import info.magnolia.context.MgnlContext;
import info.magnolia.jcr.RuntimeRepositoryException;
import info.magnolia.jcr.util.NodeUtil;
import info.magnolia.jcr.util.PropertyUtil;

import java.util.regex.Pattern;

import javax.jcr.ItemNotFoundException;
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

    private static Pattern UUID_PATTERN = Pattern
        .compile("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$");

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

    public static Node getNodeByIdOrPath(String workspace, String uuidOrPath)
    {
        if (uuidOrPath == null)
        {
            return null;
        }

        if (isUUID(uuidOrPath))
        {
            try
            {
                return NodeUtil.getNodeByIdentifier(workspace, uuidOrPath);
            }
            catch (ItemNotFoundException e)
            {
                // ignore
            }
            catch (RepositoryException e)
            {
                throw new RuntimeRepositoryException(e);
            }
        }
        else
        {
            try
            {
                return getNodeIfExists(MgnlContext.getJCRSession(workspace), uuidOrPath);
            }
            catch (RepositoryException e)
            {
                throw new RuntimeRepositoryException(e);
            }
        }

        return null;
    }

    public static boolean isUUID(String string)
    {
        // 97ed692a-31a9-4670-9c36-4d8ee8f6128d
        if (StringUtils.length(string) != 36)
        {
            return false;
        }

        return UUID_PATTERN.matcher(string).find();
    }

    public static Object toBean(Node node) throws Content2BeanException
    {
        return toBean(node, null);
    }

    public static Object toBean(Node node, Class outclass) throws Content2BeanException
    {
        return toBean(node, false, outclass);
    }

    @SuppressWarnings("deprecation")
    public static Object toBean(Node node, boolean recursive, Class outclass) throws Content2BeanException
    {
        return Content2BeanUtil.toBean(info.magnolia.cms.util.ContentUtil.asContent(node), recursive, outclass);
    }

    @SuppressWarnings("deprecation")
    public static String getUniqueLabel(Node parent, String label)
    {
        return Path.getUniqueLabel(info.magnolia.cms.util.ContentUtil.asContent(parent), label);
    }

    public static Node wrap(Node node)
    {
        if (node == null)
        {
            return null;
        }
        // TODO introduce configured wrapping
        return node;
    }

    public static String getBinaryPath(Node node)
    {
        if (node == null)
        {
            return null;
        }
        return NodeUtil.getPathIfPossible(node) + "/" + PropertyUtil.getString(node, "fileName");
    }
}
