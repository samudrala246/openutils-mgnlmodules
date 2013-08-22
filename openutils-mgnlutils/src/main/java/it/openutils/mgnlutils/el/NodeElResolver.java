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

package it.openutils.mgnlutils.el;

import info.magnolia.jcr.util.ContentMap;
import info.magnolia.jcr.util.NodeUtil;
import info.magnolia.jcr.util.PropertyUtil;

import java.beans.FeatureDescriptor;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.el.ELContext;
import javax.el.ELResolver;
import javax.el.PropertyNotWritableException;
import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.RepositoryException;

import org.apache.commons.lang.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * EL resolver for handing javax.jcr.Node, info.magnolia.jcr.util.ContentMap, info.magnolia.cms.core.Content in the same
 * way in jsps.
 * @author fgiust
 * @version $Id$
 */
public class NodeElResolver extends javax.el.ELResolver
{

    private Logger log = LoggerFactory.getLogger(NodeElResolver.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isReadOnly(ELContext context, Object base, Object property)
    {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setValue(ELContext context, Object base, Object property, Object value)
    {
        // readonly
        throw new PropertyNotWritableException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getValue(ELContext context, Object base, Object property)
    {
        if (base == null)
        {
            return null;
        }

        if (NodeElResolverUtils.toNode(base) != null)
        {
            context.setPropertyResolved(true);
            Object result = NodeElResolverUtils.get(base, ObjectUtils.toString(property));

            log.debug("getValue {} {} = {}", new Object[]{base, property, result });
            return result;
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class< ? > getType(ELContext context, Object base, Object property)
    {
        if (NodeElResolverUtils.toNode(base) != null)
        {
            context.setPropertyResolved(true);
            return Object.class;
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterator<FeatureDescriptor> getFeatureDescriptors(ELContext context, Object base)
    {
        if (base == null)
        {
            return null;
        }

        Node node = NodeElResolverUtils.toNode(base);
        if (node != null)
        {
            List<FeatureDescriptor> list = new ArrayList<FeatureDescriptor>();

            PropertyIterator iter = null;
            try
            {
                iter = node.getProperties();
            }
            catch (RepositoryException e)
            {
                log.warn("Unable to read properties from {}", NodeUtil.getPathIfPossible(node));
            }
            if (iter != null)
            {
                while (iter.hasNext())
                {
                    Property property = iter.nextProperty();

                    try
                    {
                        FeatureDescriptor descriptor = new FeatureDescriptor();
                        String name = property.getName();
                        descriptor.setName(name);
                        descriptor.setDisplayName(name);
                        descriptor.setShortDescription("");
                        descriptor.setExpert(false);
                        descriptor.setHidden(false);
                        descriptor.setPreferred(true);
                        descriptor.setValue("type", String.class);
                        descriptor.setValue(ELResolver.RESOLVABLE_AT_DESIGN_TIME, Boolean.TRUE);
                        list.add(descriptor);
                    }
                    catch (RepositoryException e)
                    {
                        // ignore (can't read a property name??)
                    }
                }
            }
            return list.iterator();
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class< ? > getCommonPropertyType(ELContext context, Object base)
    {
        if (NodeElResolverUtils.toNode(base) != null)
        {
            return Object.class;
        }
        return null;
    }

}
