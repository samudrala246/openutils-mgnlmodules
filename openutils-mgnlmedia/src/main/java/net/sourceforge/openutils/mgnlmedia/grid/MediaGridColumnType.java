/**
 *
 * SimpleMedia Module for Magnolia CMS (http://www.openmindlab.com/lab/products/media.html)
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

package net.sourceforge.openutils.mgnlmedia.grid;

import info.magnolia.cms.core.Content;
import info.magnolia.cms.i18n.Messages;
import info.magnolia.cms.security.AccessDeniedException;
import info.magnolia.cms.util.ContentUtil;
import info.magnolia.cms.util.NodeDataUtil;
import info.magnolia.context.MgnlContext;

import java.io.StringWriter;
import java.util.Map;

import javax.jcr.RepositoryException;

import net.sourceforge.openutils.mgnlcontrols.configuration.AbstractGridColumnType;
import net.sourceforge.openutils.mgnlmedia.media.lifecycle.MediaModule;

import org.apache.commons.lang.StringUtils;


/**
 * @author dschivo
 * @version $Id$
 */
public class MediaGridColumnType extends AbstractGridColumnType
{

    /**
     * {@inheritDoc}
     */
    @Override
    public String getHeadSnippet()
    {
        return "<script type=\"text/javascript\" src=\""
            + MgnlContext.getContextPath()
            + "/.resources/media/js/MediaField.js\"></script>\n"
            + "<style type=\"text/css\">\n"
            + ".x-form-field-wrap .x-form-media-trigger {\n"
            + "background-image: url("
            + MgnlContext.getContextPath()
            + "/.resources/media/css/images/media-trigger.gif);\n"
            + "}\n"
            + "</style>";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void addColumnData(Map<String, String> column, String propertyName, int colIndex, Map colMap,
        Messages msgs)
    {
        column.put("editor", "new Ed(new MediaField({}))");

        StringWriter render = new StringWriter();
        render.append("function(v, p, record){ return v ? '<img border=\"0\" alt=\"\" src=\"");
        render.append(MgnlContext.getContextPath());
        render.append("/mediathumbnail/' + v + '\" ");
        if (colMap.containsKey("width"))
        {
            render.append("width=\"");
            render.append((String) colMap.get("width"));
            render.append("\" ");
        }
        if (colMap.containsKey("height"))
        {
            render.append("height=\"");
            render.append((String) colMap.get("height"));
            render.append("\" ");
        }
        render.append("/>' : v; }");

        column.put("renderer", render.toString());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void processColumnOnLoad(String[] column, Content colConfig)
    {
        String valueType = StringUtils.defaultIfEmpty(NodeDataUtil.getString(colConfig, "valueType"), "uuid");
        if ("uuid".equals(valueType))
        {
            for (int index = 0; index < column.length; index++)
            {
                if (StringUtils.startsWith(column[index], "/"))
                {
                    Content node = ContentUtil.getContentByUUID(MediaModule.REPO, column[index]);
                    if (node != null)
                    {
                        column[index] = node.getUUID();
                    }
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
        String mediaValueType = StringUtils.defaultIfEmpty(NodeDataUtil.getString(colConfig, "valueType"), "uuid");
        if ("uuid".equals(mediaValueType))
        {
            for (int index = 0; index < column.length; index++)
            {
                if (StringUtils.startsWith(column[index], "/"))
                {
                    Content node = ContentUtil.getContentByUUID(MediaModule.REPO, column[index]);
                    if (node != null)
                    {
                        column[index] = node.getUUID();
                    }
                }
            }
        }
    }
}
