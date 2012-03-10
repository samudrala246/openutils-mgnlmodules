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

package net.sourceforge.openutils.mgnlcontrols.dialog;

import info.magnolia.cms.beans.runtime.MultipartForm;
import info.magnolia.cms.core.Content;
import info.magnolia.cms.core.ItemType;
import info.magnolia.cms.security.AccessDeniedException;
import info.magnolia.cms.util.NodeDataUtil;
import info.magnolia.module.admininterface.FieldSaveHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.jcr.RepositoryException;

import net.sourceforge.openutils.mgnlcontrols.configuration.GridColumnType;
import net.sourceforge.openutils.mgnlcontrols.configuration.GridColumnTypeManager;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Needed by file upload. Setup: assign this class name as a "saveHandler" property of the grid field's configuration
 * node.
 * @author dschivo
 */
public class DialogGridSaveHandler implements FieldSaveHandler
{

    public static List<String[]> valueToColumns(String value)
    {
        String[] allRows = StringUtils.splitPreserveAllTokens(value, '\n');
        List<String[]> columns = new ArrayList<String[]>();
        int rowIndex = 0;
        for (String row : allRows)
        {
            row = StringUtils.removeEnd(row, "\r");
            int colIndex = 0;
            for (String token : StringUtils.splitPreserveAllTokens(row, '\t'))
            {
                if (columns.size() < colIndex + 1)
                {
                    columns.add(new String[allRows.length]);
                }
                columns.get(colIndex)[rowIndex] = token;
                colIndex++;
            }
            rowIndex++;
        }
        return columns;
    }

    public static String columnsToValue(List<String[]> columns)
    {
        int rowCount = 0;
        for (String[] column : columns)
        {
            rowCount = Math.max(rowCount, column.length);
        }

        StringBuilder sbValue = new StringBuilder();
        StringBuilder sbLine = new StringBuilder();
        for (int rowIndex = 0; rowIndex < rowCount; rowIndex++)
        {
            int colIndex = 0;
            for (String[] column : columns)
            {
                if (colIndex > 0)
                {
                    sbLine.append('\t');
                }
                sbLine.append(StringUtils.defaultString(column[rowIndex]));
                colIndex++;
            }
            if (sbValue.length() > 0)
            {
                sbValue.append('\n');
            }
            sbValue.append(sbLine);
            sbLine.setLength(0);
        }
        return new String(sbValue);
    }

    private static Logger log = LoggerFactory.getLogger(DialogGridSaveHandler.class);

    /**
     * {@inheritDoc}
     */
    public void save(Content parentNode, Content configNode, String name, MultipartForm form, int type, int valueType,
        int isRichEditValue, int encoding) throws RepositoryException, AccessDeniedException
    {
        String value = form.getParameter(name);

        List<Content> colConfigs = new ArrayList<Content>();
        if (configNode != null && configNode.hasContent("columns"))
        {
            colConfigs.addAll(configNode.getContent("columns").getChildren(ItemType.CONTENTNODE));
        }

        Map<String, GridColumnType> colTypeMap = GridColumnTypeManager.getInstance().getColumnTypes();

        List<String[]> columns = valueToColumns(value);

        int colIndex = 0;
        for (Content colConfig : colConfigs)
        {
            String colTypeName = NodeDataUtil.getString(colConfig, "type");
            GridColumnType colType = colTypeMap.get(colTypeName);
            if (colType != null && colIndex < columns.size())
            {
                colType.processColumnOnSave(columns.get(colIndex), colConfig, name, parentNode);
            }
            colIndex++;
        }

        NodeDataUtil.getOrCreateAndSet(parentNode, name, columnsToValue(columns));
    }

}
