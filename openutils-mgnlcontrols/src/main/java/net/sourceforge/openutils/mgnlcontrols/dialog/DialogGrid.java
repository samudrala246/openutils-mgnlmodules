/**
 *
 * Controls module for Magnolia CMS (http://www.openmindlab.com/lab/products/controls.html)
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

package net.sourceforge.openutils.mgnlcontrols.dialog;

import info.magnolia.cms.core.Content;
import info.magnolia.cms.core.ItemType;
import info.magnolia.cms.util.NodeDataUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.jcr.RepositoryException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.openutils.mgnlcontrols.configuration.GridColumnType;
import net.sourceforge.openutils.mgnlcontrols.configuration.GridColumnTypeManager;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;


/**
 * @author fgiust
 * @version $Id$
 */
public class DialogGrid extends ConfigurableFreemarkerDialog
{

    private List<Content> colConfigs = new ArrayList<Content>();

    /**
     * {@inheritDoc}
     */
    @Override
    public void init(HttpServletRequest request, HttpServletResponse response, Content websiteNode, Content configNode)
        throws RepositoryException
    {
        super.init(request, response, websiteNode, configNode);
        if (StringUtils.isEmpty(getConfigValue("saveHandler")))
        {
            setConfig("saveHandler", "net.sourceforge.openutils.mgnlcontrols.dialog.DialogGridSaveHandler");
        }
        if (configNode != null && configNode.hasContent("columns"))
        {
            colConfigs.addAll(configNode.getContent("columns").getChildren(ItemType.CONTENTNODE));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getPath()
    {
        return "dialogs/grid.ftl";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void addToParameters(Map<String, Object> parameters)
    {
        super.addToParameters(parameters);

        Map<String, GridColumnType> colTypeMap = GridColumnTypeManager.getInstance().getColumnTypes();
        parameters.put("gridColumnTypes", colTypeMap);

        String value = getValue();
        if (value != null)
        {
            int rows = NumberUtils.toInt(String.valueOf(getConfigValue("rows")), -1);
            if (rows > 0)
            {
                value = adjustRowCount(value, rows);
            }
            List<String[]> columns = DialogGridSaveHandler.valueToColumns(value);
            int colIndex = 0;
            for (String[] column : columns)
            {
                if (colIndex < colConfigs.size())
                {
                    Content colConfig = colConfigs.get(colIndex);
                    String colTypeName = NodeDataUtil.getString(colConfig, "type");
                    GridColumnType colType = colTypeMap.get(colTypeName);
                    if (colType != null)
                    {
                        colType.processColumnOnLoad(column, colConfig, getName(), getStorageNode());
                    }
                }
                colIndex++;
            }
            value = DialogGridSaveHandler.columnsToValue(columns);
        }
        parameters.put("gridValue", value);
    }

    protected String adjustRowCount(String value, int count)
    {
        List<String> rows = new ArrayList<String>(Arrays.asList(StringUtils.splitPreserveAllTokens(value, '\n')));
        while (rows.size() < count)
        {
            rows.add(StringUtils.EMPTY);
        }
        while (rows.size() > count)
        {
            String row = StringUtils.removeEnd(rows.get(rows.size() - 1), "\r");
            if (!StringUtils.isEmpty(StringUtils.remove(row, '\t')))
            {
                // non-empty line: do not remove it
                break;
            }
            rows.remove(rows.size() - 1);
        }
        return StringUtils.join(rows, '\n');
    }
}
