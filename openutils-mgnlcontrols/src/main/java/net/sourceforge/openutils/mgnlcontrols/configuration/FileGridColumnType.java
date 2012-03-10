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

import info.magnolia.cms.beans.runtime.Document;
import info.magnolia.cms.core.Content;
import info.magnolia.cms.core.HierarchyManager;
import info.magnolia.cms.core.ItemType;
import info.magnolia.cms.core.NodeData;
import info.magnolia.cms.core.Path;
import info.magnolia.cms.gui.fckeditor.FCKEditorTmpFiles;
import info.magnolia.cms.i18n.Messages;
import info.magnolia.cms.security.AccessDeniedException;
import info.magnolia.cms.util.ContentUtil;
import info.magnolia.context.MgnlContext;
import info.magnolia.module.admininterface.SaveHandlerImpl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.jcr.RepositoryException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * A column of files.
 * @author dschivo
 * @version $Id$
 */
public class FileGridColumnType extends AbstractGridColumnType
{

    private Logger log = LoggerFactory.getLogger(getClass());

    /**
     * {@inheritDoc}
     */
    @Override
    public String getHeadSnippet()
    {
        return "<script type=\"text/javascript\" src=\""
            + MgnlContext.getContextPath()
            + "/.resources/controls/js/FileField.js\"></script>";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void addColumnData(Map<String, String> column, String propertyName, int colIndex, Map colMap,
        Messages msgs)
    {
        column.put("editor", "new Ed(new FileField({}))");
        column.put("renderer", "function(v, p, record){ return v ? v.replace(/^.*\\//, '') : v;}");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void processColumnOnSave(String[] column, Content colConfig, String propertyName, Content parentNode)
        throws RepositoryException, AccessDeniedException
    {
        String ctx = MgnlContext.getContextPath();
        HierarchyManager hm = parentNode.getHierarchyManager();
        Content filesNode = ContentUtil.getOrCreateContent(parentNode, propertyName + "_files", ItemType.CONTENTNODE);
        String tmpPath = "/tmp/fckeditor/";
        List<String> usedFiles = new ArrayList<String>();

        for (int index = 0; index < column.length; index++)
        {
            if (StringUtils.isEmpty(column[index]))
            {
                continue;
            }
            if (column[index].startsWith(ctx))
            {
                String link = StringUtils.removeStart(column[index], ctx);
                if (StringUtils.startsWith(link, tmpPath))
                {
                    String uuid = StringUtils.substringBetween(link, tmpPath, "/");
                    Document doc = FCKEditorTmpFiles.getDocument(uuid);
                    String fileNodeName = Path.getUniqueLabel(hm, filesNode.getHandle(), "file");
                    SaveHandlerImpl.saveDocument(filesNode, doc, fileNodeName, "", "");
                    link = filesNode.getHandle() + "/" + fileNodeName + "/" + doc.getFileNameWithExtension();
                    column[index] = link;
                    doc.delete();
                    try
                    {
                        FileUtils.deleteDirectory(new java.io.File(Path.getTempDirectory() + "/fckeditor/" + uuid));
                    }
                    catch (IOException e)
                    {
                        log.error("can't delete tmp file [" + Path.getTempDirectory() + "/fckeditor/" + uuid + "]");
                    }
                }
            }
            if (column[index].startsWith(filesNode.getHandle()))
            {
                String fileNodeName = StringUtils.removeStart(column[index], filesNode.getHandle() + "/");
                fileNodeName = StringUtils.substringBefore(fileNodeName, "/");
                usedFiles.add(fileNodeName);
            }
        }

        for (Iterator<NodeData> iter = filesNode.getNodeDataCollection().iterator(); iter.hasNext();)
        {
            NodeData fileNodeData = iter.next();
            if (!usedFiles.contains(fileNodeData.getName()))
            {
                fileNodeData.delete();
            }
        }
    }

}
