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

package net.sourceforge.openutils.mgnlcontrols.configuration;

import info.magnolia.cms.core.Content;
import info.magnolia.cms.i18n.Messages;
import info.magnolia.cms.security.AccessDeniedException;
import info.magnolia.cms.util.ContentUtil;
import info.magnolia.cms.util.NodeDataUtil;
import info.magnolia.context.MgnlContext;
import info.magnolia.module.ModuleRegistry;
import info.magnolia.repository.RepositoryConstants;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.jcr.RepositoryException;

import org.apache.commons.lang.StringUtils;


/**
 * A column of links: UUIDs of jcr nodes are stored instead of handles.
 * @author dschivo
 * @version $Id$
 */
public class UUIDLinkGridColumnType extends AbstractGridColumnType
{

    /**
     * {@inheritDoc}
     */
    @Override
    public String getHeadSnippet()
    {
        return "<script type=\"text/javascript\" src=\""
            + MgnlContext.getContextPath()
            + "/.resources/controls/"
            + ModuleRegistry.Factory.getInstance().getDefinition("controls").getVersion()
            + "/js/UUIDLinkField.js\"></script>";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void addColumnData(Map<String, String> column, String propertyName, int colIndex, Map colMap,
        Messages msgs)
    {
        List<String> options = new ArrayList<String>();
        if (!StringUtils.isEmpty((String) colMap.get("tree")))
        {
            options.add("repository: '" + colMap.get("tree") + "'");
        }
        else if (!StringUtils.isEmpty((String) colMap.get("repository")))
        {
            options.add("repository: '" + colMap.get("repository") + "'");
        }
        if (!StringUtils.isEmpty((String) colMap.get("extension")))
        {
            options.add("extension: '" + colMap.get("extension") + "'");
        }
        column.put("editor", "new Ed(new UUIDLinkField({" + StringUtils.join(options, ",") + "}))");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void processColumnOnLoad(String[] column, Content colConfig, String propertyName, Content storageNode)
    {
        String repository = StringUtils.defaultIfEmpty(
            NodeDataUtil.getString(colConfig, "repository"),
            RepositoryConstants.WEBSITE);
        for (int index = 0; index < column.length; index++)
        {

            String nodeuuid = StringUtils.trim(column[index]);
            if (StringUtils.isNotBlank(nodeuuid))
            {
                Content node = ContentUtil.getContentByUUID(repository, nodeuuid);
                if (node != null)
                {
                    column[index] = node.getHandle();
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void processColumnOnSave(String[] column, Content colConfig, String propertyName, Content parentNode)
        throws RepositoryException, AccessDeniedException
    {
        String repository = StringUtils.defaultIfEmpty(
            NodeDataUtil.getString(colConfig, "repository"),
            RepositoryConstants.WEBSITE);
        for (int index = 0; index < column.length; index++)
        {
            if (!StringUtils.isEmpty(column[index]))
            {
                Content node = ContentUtil.getContent(repository, column[index]);
                if (node != null)
                {
                    column[index] = node.getUUID();
                }
            }
        }
    }
}
