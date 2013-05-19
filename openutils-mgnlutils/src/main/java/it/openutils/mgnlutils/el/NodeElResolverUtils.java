package it.openutils.mgnlutils.el;

import java.lang.reflect.InvocationTargetException;

import info.magnolia.cms.beans.runtime.FileProperties;
import info.magnolia.jcr.util.NodeUtil;
import info.magnolia.link.LinkException;
import info.magnolia.link.LinkTransformerManager;

import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;
import javax.jcr.Value;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author fgiust
 * @version $Id$
 */
public class NodeElResolverUtils
{

    private static Logger log = LoggerFactory.getLogger(NodeElResolverUtils.class);

    public static Object get(Node node, String key)
    {
        if (node == null || key == null)
        {
            return null;
        }

        if (StringUtils.equalsIgnoreCase(key, "uuid")
            || StringUtils.equalsIgnoreCase(key, "@uuid")
            || StringUtils.equalsIgnoreCase(key, "@id"))
        {
            return NodeUtil.getNodeIdentifierIfPossible(node);
        }
        else if (StringUtils.equals(key, "handle")
            || StringUtils.equals(key, "@handle")
            || StringUtils.equals(key, "@path"))
        {
            return NodeUtil.getPathIfPossible(node);
        }
        else if (StringUtils.equals(key, "@level") || StringUtils.equals(key, "@depth"))
        {
            try
            {
                return node.getDepth();
            }
            catch (RepositoryException e)
            {
                // ignore
            }
        }

        try
        {
            Object property = BeanUtils.getProperty(node, key);

            // the Node class has a property with this name, stop here
            return property;
        }
        catch (IllegalAccessException e)
        {
            // ignore, we tried and failed
        }
        catch (NoSuchMethodException e)
        {
            // ignore, we tried and failed
        }
        catch (InvocationTargetException e)
        {
            // ignore, we tried and failed
        }

        Object nodeprop = getNodeProperty(node, key);

        return nodeprop;
    }

    public static Object getNodeProperty(Node node, String keyStr)
    {
        try
        {
            if (node.hasProperty(keyStr))
            {
                Property prop = node.getProperty(keyStr);
                int type = prop.getType();
                if (type == PropertyType.DATE)
                {
                    return prop.getDate();
                }
                else if (type == PropertyType.BINARY)
                {
                    // what to do?
                }
                else if (type == PropertyType.BOOLEAN)
                {
                    return prop.getBoolean();
                }
                else if (type == PropertyType.LONG)
                {
                    return prop.getLong();
                }
                else if (type == PropertyType.DOUBLE)
                {
                    return prop.getDouble();
                }
                else if (type == PropertyType.DECIMAL)
                {
                    return prop.getDecimal();
                }
                else if (prop.isMultiple())
                {

                    Value[] values = prop.getValues();

                    String[] valueStrings = new String[values.length];

                    for (int j = 0; j < values.length; j++)
                    {
                        try
                        {
                            valueStrings[j] = values[j].getString();
                        }
                        catch (RepositoryException e)
                        {
                            log.debug(e.getMessage());
                        }
                    }

                    return valueStrings;
                }
                else
                {
                    try
                    {
                        return info.magnolia.link.LinkUtil.convertLinksFromUUIDPattern(
                            prop.getString(),
                            LinkTransformerManager.getInstance().getBrowserLink(node.getPath()));
                    }
                    catch (LinkException e)
                    {
                        log.warn("Failed to parse links with from " + prop.getName(), e);
                    }
                }

                return prop.getString();
            }

            if (node.hasNode(keyStr))
            {
                return node.getNode(keyStr);
            }

        }
        catch (PathNotFoundException e)
        {
            // ignore, property doesn't exist
        }
        catch (RepositoryException e)
        {
            log.warn("Failed to retrieve {} on {} with {}", new Object[]{keyStr, node, e.getMessage() });
        }

        return null;
    }

}
