/**
 *
 * Tagcloud module for Magnolia CMS (http://www.openmindlab.com/lab/products/mgnltagcloud.html)
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

package net.sourceforge.openutils.mgnltagcloud.dialog;

import info.magnolia.cms.beans.runtime.MultipartForm;
import info.magnolia.cms.core.Content;
import info.magnolia.cms.core.NodeData;
import info.magnolia.cms.security.AccessDeniedException;
import info.magnolia.module.admininterface.FieldSaveHandler;
import info.magnolia.module.admininterface.SaveHandlerImpl;

import java.util.ArrayList;
import java.util.List;

import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Value;

import org.apache.commons.lang.StringUtils;
import org.apache.jackrabbit.value.ValueFactoryImpl;


/**
 * Custom multi-value save handler that handles migration between single values - multi value properties.
 * @author fgiust
 * @version $Id$
 */
@SuppressWarnings("deprecation")
public class TagCloudDialogSafeMultivalueSaveHandler extends SaveHandlerImpl implements FieldSaveHandler
{

    /**
     * {@inheritDoc}
     */
    @Override
    protected void processMultiple(Content node, String name, int type, String[] values) throws RepositoryException,
        PathNotFoundException, AccessDeniedException
    {

        List<Value> valueList = new ArrayList<Value>();

        if (values != null && values.length != 0)
        {

            for (int j = 0; j < values.length; j++)
            {
                String valueStr = values[j];
                if (StringUtils.isNotEmpty(valueStr))
                {
                    Value value = getValue(valueStr, type);
                    if (value != null)
                    {

                        valueList.add(value);
                    }
                }
            }
            if (valueList.size() > 0)
            {
                getOrCreateAndSet(node, name, valueList.toArray(new Value[valueList.size()]));
            }
        }
        else
        {
            // Empty set of tags
            getOrCreateAndSet(node, name, new Value[]{ValueFactoryImpl.getInstance().createValue("") });
        }
    }

    /**
     * {@inheritDoc}
     */
    public void save(Content parentNode, Content configNode, String name, MultipartForm form, int type, int valueType,
        int isRichEditValue, int encoding) throws RepositoryException, AccessDeniedException
    {
        processMultiple(parentNode, name, type, form.getParameterValues(name));

    }

    private NodeData getOrCreateAndSet(Content node, String name, Value[] value) throws AccessDeniedException,
        RepositoryException
    {
        if (node.hasNodeData(name))
        {

            NodeData nodeData = node.getNodeData(name);
            if (nodeData.isMultiValue() == 0)
            {
                node.deleteNodeData(name);
                node.createNodeData(name, value);
            }
            else
            {
                node.setNodeData(name, value);
            }
            return node.getNodeData(name);
        }

        return node.createNodeData(name, value);

    }

}