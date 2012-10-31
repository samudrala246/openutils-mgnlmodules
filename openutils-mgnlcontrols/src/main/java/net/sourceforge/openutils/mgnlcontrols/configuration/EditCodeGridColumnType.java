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
import info.magnolia.cms.i18n.Messages;
import info.magnolia.cms.security.AccessDeniedException;
import info.magnolia.cms.util.ContentUtil;
import info.magnolia.cms.util.NodeDataUtil;
import info.magnolia.context.MgnlContext;
import info.magnolia.module.ModuleRegistry;
import info.magnolia.module.admininterface.SaveHandlerImpl;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import javax.jcr.RepositoryException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.safehaus.uuid.UUIDGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author diego
 * @version $Id: $
 */
public class EditCodeGridColumnType extends AbstractGridColumnType
{

    public static String escapeEditCodeGridColumnValue(String value)
    {
        return StringEscapeUtils.escapeJava(StringEscapeUtils.escapeXml(value));
    }

    public static String unescapeEditCodeGridColumnValue(String value)
    {
        return StringEscapeUtils.unescapeXml(StringEscapeUtils.unescapeJava(value));
    }

    private Logger log = LoggerFactory.getLogger(getClass());

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
            + "/js/EditCodeField.js\"></script>";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void processColumnOnLoad(String[] column, Content colConfig, String propertyName, Content storageNode)
    {
        if (NodeDataUtil.getBoolean(colConfig, "binary", false))
        {
            HierarchyManager hm = storageNode.getHierarchyManager();
            for (int index = 0; index < column.length; index++)
            {
                String value = StringUtils.EMPTY;
                String link = StringUtils.trim(column[index]);
                if (StringUtils.isNotBlank(link))
                {
                    NodeData nodeData = null;
                    try
                    {
                        nodeData = hm.getNodeData(storageNode.getHandle()
                            + "/"
                            + StringUtils.substringBeforeLast(link, "/"));
                    }
                    catch (RepositoryException e)
                    {
                        log.warn(e.getMessage(), e);
                    }
                    if (nodeData != null)
                    {
                        try
                        {
                            value = escapeEditCodeGridColumnValue(StringUtils.join(
                                IOUtils.readLines(nodeData.getStream(), "UTF-8"),
                                '\n'));
                        }
                        catch (Exception e)
                        {
                            log.warn(e.getMessage(), e);
                        }
                    }
                }
                column[index] = value;
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
        if (NodeDataUtil.getBoolean(colConfig, "binary", false))
        {
            String filename = NodeDataUtil.getString(colConfig, "binaryFilename", "file.txt");
            String type = NodeDataUtil.getString(colConfig, "binaryType", "text/plain");
            HierarchyManager hm = parentNode.getHierarchyManager();
            Content filesNode = ContentUtil.getOrCreateContent(parentNode, propertyName
                + "_"
                + colConfig.getName()
                + "_files", ItemType.CONTENTNODE);

            for (Iterator<NodeData> iter = filesNode.getNodeDataCollection().iterator(); iter.hasNext();)
            {
                NodeData fileNodeData = iter.next();
                fileNodeData.delete();
            }

            String uuid = UUIDGenerator.getInstance().generateTimeBasedUUID().toString();
            File tmpDir = new File(Path.getTempDirectoryPath() + "/fckeditor/" + uuid);
            for (int index = 0; index < column.length; index++)
            {
                if (StringUtils.isEmpty(column[index]))
                {
                    continue;
                }
                File tmpFile = new File(tmpDir, filename);
                try
                {
                    FileUtils.writeStringToFile(tmpFile, unescapeEditCodeGridColumnValue(column[index]), "UTF-8");
                }
                catch (IOException e)
                {
                    log.error("can't write to tmp file [" + tmpFile + "]");
                }
                Document doc = new Document(tmpFile, type);
                String fileNodeName = Path.getUniqueLabel(
                    hm,
                    filesNode.getHandle(),
                    String.valueOf(System.currentTimeMillis()));
                SaveHandlerImpl.saveDocument(filesNode, doc, fileNodeName, "", "");
                String link = filesNode.getName() + "/" + fileNodeName + "/" + doc.getFileNameWithExtension();
                column[index] = link;
                doc.delete();
            }
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

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("rawtypes")
    @Override
    protected void addColumnData(Map<String, String> column, String propertyName, int colIndex, Map colMap,
        Messages msgs)
    {
        column.put("editor", "new Ed(new EditCodeField())");
    }
}
