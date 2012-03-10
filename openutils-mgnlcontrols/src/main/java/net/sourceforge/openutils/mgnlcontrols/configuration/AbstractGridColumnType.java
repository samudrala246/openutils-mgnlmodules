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

import info.magnolia.cms.core.Content;
import info.magnolia.cms.i18n.Messages;
import info.magnolia.cms.security.AccessDeniedException;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.jcr.RepositoryException;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;


/**
 * A base class for column types: the javascript object for the column model is prepopulated with common data.
 * @author dschivo
 * @version $Id$
 */
public abstract class AbstractGridColumnType implements GridColumnType
{

    /**
     * {@inheritDoc}
     */
    public String getHeadSnippet()
    {
        return StringUtils.EMPTY;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public String drawSupportHtml(String propertyName, int colIndex, Map colmap, Messages msgs)
    {
        return StringUtils.EMPTY;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public String drawColumnJs(String propertyName, int colIndex, Map colMap, Messages msgs)
    {
        StringBuilder sb = new StringBuilder();
        Map<String, String> column = new HashMap<String, String>();
        String header = (String) colMap.get("header");
        column.put("header", "'" + StringEscapeUtils.escapeJavaScript(msgs.getWithDefault(header, header)) + "'");
        column.put("dataIndex", "'" + colIndex + "'");
        column.put("sortable", "false");
        addColumnData(column, propertyName, colIndex, colMap, msgs);
        for (Entry<String, String> entry : column.entrySet())
        {
            if (sb.length() > 0)
            {
                sb.append(",\n");
            }
            sb.append(entry.getKey()).append(": ").append(entry.getValue());
        }
        return "{\n" + sb + "\n}\n";
    }

    /**
     * {@inheritDoc}
     */
    public void processColumnOnLoad(String[] column, Content colConfig)
    {
    }

    /**
     * {@inheritDoc}
     */
    public void processColumnOnSave(String[] column, Content colConfig, String propertyName, Content parentNode)
        throws RepositoryException, AccessDeniedException
    {
    }

    /**
     * Adds custom data to the javascript object for the column model (e.g. editor, renderer).
     * @param column
     * @param propertyName
     * @param colIndex
     * @param colMap
     * @param msgs
     */
    @SuppressWarnings("unchecked")
    protected abstract void addColumnData(Map<String, String> column, String propertyName, int colIndex, Map colMap,
        Messages msgs);
}
