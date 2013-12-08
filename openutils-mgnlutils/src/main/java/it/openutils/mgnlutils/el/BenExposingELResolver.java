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

import java.beans.FeatureDescriptor;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.el.ELContext;
import javax.el.ELException;
import javax.el.ELResolver;
import javax.el.PropertyNotWritableException;
import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.PageContext;

import org.apache.commons.lang.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * EL resolvers which exposes function classes to jsps without the need for tlds.
 * @author fgiust
 * @version $Id$
 */
public class BenExposingELResolver extends ELResolver
{

    private Logger log = LoggerFactory.getLogger(BenExposingELResolver.class);

    private Map<String, ? extends Object> beans = new HashMap<String, Object>();

    public void setBeans(Map<String, ? extends Object> beans)
    {
        this.beans = beans;
    }

    public BenExposingELResolver()
    {
    }

    public BenExposingELResolver(Map<String, ? extends Object> beans)
    {
        this.beans = beans;
    }

    @Override
    public Object getValue(ELContext elContext, Object base, Object property) throws ELException
    {
        if (base == null
            && ((PageContext) elContext.getContext(JspContext.class)).findAttribute(ObjectUtils.toString(property)) == null)
        {
            Object result = beans.get(ObjectUtils.toString(property));
            if (result != null)
            {
                elContext.setPropertyResolved(true);
                return result;
            }
        }
        return null;
    }

    @Override
    public Class< ? > getType(ELContext elContext, Object base, Object property) throws ELException
    {
        if (base == null
            && ((PageContext) elContext.getContext(JspContext.class)).findAttribute(ObjectUtils.toString(property)) == null)
        {
            Object result = beans.get(ObjectUtils.toString(property));
            if (result != null)
            {
                elContext.setPropertyResolved(true);
                return result.getClass();
            }
        }
        return null;
    }

    @Override
    public void setValue(ELContext elContext, Object base, Object property, Object value) throws ELException
    {
        if (base == null)
        {
            Object result = beans.get(ObjectUtils.toString(property));
            if (result != null)
            {
                elContext.setPropertyResolved(true);
                throw new PropertyNotWritableException("Variable '"
                    + property
                    + "' refers to a bean which by definition is not writable");
            }

        }
    }

    @Override
    public boolean isReadOnly(ELContext elContext, Object base, Object property) throws ELException
    {
        if (base == null)
        {
            if (beans.containsKey(ObjectUtils.toString(property)))
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public Iterator<FeatureDescriptor> getFeatureDescriptors(ELContext elContext, Object base)
    {
        return null;
    }

    @Override
    public Class< ? > getCommonPropertyType(ELContext elContext, Object base)
    {
        return Object.class;
    }

}
