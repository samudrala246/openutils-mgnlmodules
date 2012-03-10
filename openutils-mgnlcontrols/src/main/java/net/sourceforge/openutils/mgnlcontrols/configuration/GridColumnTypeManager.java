/**
 *
 * Controls module for Magnolia CMS (http://www.openmindlab.com/lab/products/controls.html)
 * Copyright(C) 2008-2012, Openmind S.r.l. http://www.openmindonline.it
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

package net.sourceforge.openutils.mgnlcontrols.configuration;

import info.magnolia.cms.beans.config.ObservedManager;
import info.magnolia.cms.core.Content;
import info.magnolia.cms.util.ContentUtil;
import info.magnolia.cms.util.NodeDataUtil;
import info.magnolia.content2bean.Content2BeanUtil;
import info.magnolia.objectfactory.Components;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Collects and exposes all the configured column types.
 * @author dschivo
 * @version $Id$
 */
public class GridColumnTypeManager extends ObservedManager
{

    public static GridColumnTypeManager getInstance()
    {
        return Components.getSingleton(GridColumnTypeManager.class);
    }

    private Logger log = LoggerFactory.getLogger(getClass());

    private final Map<String, GridColumnType> columnTypes = new HashMap<String, GridColumnType>();

    /**
     * Returns the types.
     * @return the types
     */
    public Map<String, GridColumnType> getColumnTypes()
    {
        return columnTypes;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onClear()
    {
        columnTypes.clear();
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    protected void onRegister(Content node)
    {
        for (Iterator iter = ContentUtil.getAllChildren(node).iterator(); iter.hasNext();)
        {
            Content columnTypeNode = (Content) iter.next();

            if (!NodeDataUtil.getBoolean(columnTypeNode, "enabled", true))
            {
                continue;
            }

            try
            {
                GridColumnType columnType = (GridColumnType) Content2BeanUtil.toBean(columnTypeNode);

                columnTypes.put(columnTypeNode.getName(), columnType);
            }
            catch (Throwable e)
            {
                log.error("Error getting column type {}", columnTypeNode.getHandle(), e);
            }
        }
    }

}
