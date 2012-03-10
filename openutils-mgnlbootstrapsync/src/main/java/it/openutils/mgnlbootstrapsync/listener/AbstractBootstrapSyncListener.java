/**
 *
 * BootstrapSync for Magnolia CMS (http://www.openmindlab.com/lab/products/bootstrapsync.html)
 * Copyright(C) ${project.inceptionYear}-2012, Openmind S.r.l. http://www.openmindonline.it
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

package it.openutils.mgnlbootstrapsync.listener;

import info.magnolia.cms.core.Content;
import info.magnolia.cms.core.HierarchyManager;
import info.magnolia.cms.core.Path;
import info.magnolia.cms.core.ie.DataTransporter;
import info.magnolia.cms.security.AccessDeniedException;
import info.magnolia.cms.util.ContentUtil;
import info.magnolia.context.MgnlContext;
import info.magnolia.importexport.filters.VersionFilter;
import it.openutils.mgnlbootstrapsync.BootstrapAtomicFilter;
import it.openutils.mgnlbootstrapsync.watch.BootstrapSyncRepositoryWatch;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.zip.DeflaterOutputStream;

import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.observation.EventIterator;
import javax.jcr.observation.EventListener;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;


/**
 * @author mmolaschi
 * @version $Id: $
 */
public abstract class AbstractBootstrapSyncListener implements EventListener
{

    private static Logger log = LoggerFactory.getLogger(AbstractBootstrapSyncListener.class);

    private BootstrapSyncRepositoryWatch watch;

    private Object synchronizationObject = new Object();

    /**
     * @param watch watch for this listener
     */
    public AbstractBootstrapSyncListener(BootstrapSyncRepositoryWatch watch)
    {
        this.watch = watch;
    }

    /**
     * {@inheritDoc}
     */
    public abstract void onEvent(EventIterator events);

    public BootstrapSyncRepositoryWatch getWatch()
    {
        return watch;
    }

    public void setWatch(BootstrapSyncRepositoryWatch watch)
    {
        this.watch = watch;
    }

    /**
     * Change event on node
     * @param nodePath path to node
     */
    public void exportNode(String nodePath)
    {
        synchronized (synchronizationObject)
        {
            String path = nodePath;

            // get repository manager
            HierarchyManager hm = MgnlContext.getSystemContext().getHierarchyManager(watch.getRepository());

            // check if path can exported
            boolean isEnablePath = this.watch.getEnableRoots().isEnable(path);
            if (isEnablePath)
            {
                try
                {
                    // check if it is a property
                    if (hm.isNodeData(path))
                    {
                        // move to parent node
                        path = StringUtils.substringBeforeLast(path, "/");
                    }
                }
                catch (AccessDeniedException e)
                {
                    log.error(e.getMessage(), e);
                    return;
                }

                Content exported = null;

                try
                {

                    if (watch.getNodeType() != null)
                    {
                        // get exported content
                        exported = getRightContent(hm.getContent(path), watch.getNodeType());
                        if (exported == null)
                        {
                            return;
                        }

                    }
                    else
                    {
                        exported = hm.getContent(path);
                    }
                }
                catch (RepositoryException e)
                {
                    if (log.isDebugEnabled() && e instanceof PathNotFoundException)
                    {
                        log.debug("Path already deleted:" + path);
                    }
                    if (log.isErrorEnabled() && !(e instanceof PathNotFoundException))
                    {
                        log.error("Error hierarchy manager for path " + path, e);
                    }
                    return;
                }

                // get handle
                String handle = exported.getHandle();

                // check if path is under a "compressed" node
                String pathToExport = this.watch.getExportRoots().getRootPath(handle);

                if (pathToExport == null)
                {
                    // not "compressed"
                    exportFileIterative(hm, exported);
                }
                else
                {
                    exportFile(hm, pathToExport, false);
                }
            }
        }
    }

    /**
     * Remove event on node
     * @param path path to node
     */
    public void removeNode(String path)
    {
        // synchornization
        synchronized (synchronizationObject)
        {
            // get hierarchy manager
            HierarchyManager hm = MgnlContext.getSystemContext().getHierarchyManager(watch.getRepository());

            // check if path can exported
            boolean isEnablePath = this.watch.getEnableRoots().isEnable(path);
            if (isEnablePath)
            {
                // check if path is under a "compressed" node
                String pathToExport = this.watch.getExportRoots().getRootPath(path);

                if (pathToExport == null || pathToExport.equals(path))
                {
                    // remove every file that starts with path
                    cleanFileSystem(path);

                    try
                    {
                        // get parent node
                        String parentHandle = StringUtils.substringBeforeLast(path, "/");
                        String parentXmlName = watch.getRepository()
                            + StringUtils.replace(parentHandle, "/", ".")
                            + ".xml";
                        if (parentHandle.length() == 0)
                        {
                            parentHandle = "/";
                        }
                        // check if path is under a "compressed" node
                        String pathParentToExport = this.watch.getExportRoots().getRootPath(parentHandle);

                        if (pathParentToExport == null)
                        {
                            // xml reader for filtering 2 levels
                            BootstrapAtomicFilter xmlReader = new BootstrapAtomicFilter(XMLReaderFactory
                                .createXMLReader(org.apache.xerces.parsers.SAXParser.class.getName()));

                            // export parent node
                            exportFile(parentXmlName, xmlReader, hm, parentHandle);
                        }
                        else
                        {
                            exportFile(hm, pathParentToExport, false);
                        }
                    }
                    catch (SAXException ex)
                    {
                        throw new RuntimeException(ex);
                    }
                }
                else
                {
                    exportFile(hm, pathToExport, false);
                }
            }
        }
    }

    /**
     * Go up in tree until the current node type equals passed item type
     * @param c current node
     * @param itemType item type to check
     * @return right node or null if not found
     * @throws RepositoryException repository exception
     */
    @SuppressWarnings("unchecked")
    private Content getRightContent(Content c, String itemType) throws RepositoryException
    {
        if (c.getItemType().getSystemName().equals(itemType))
        {
            return c;
        }
        if (c.getParent() != null)
        {
            return getRightContent(c.getParent(), itemType);
        }
        return null;
    }

    /**
     * Export node and its subnodes
     * @param hm hierarchy manager
     * @param node node
     */
    @SuppressWarnings("unchecked")
    protected void exportFileIterative(HierarchyManager hm, Content node)
    {
        // write itself
        exportFile(hm, node.getHandle(), true);

        // cycle on children
        Collection<Content> children = node.getChildren(ContentUtil.EXCLUDE_META_DATA_CONTENT_FILTER);
        if (children != null)
        {
            for (Content child : children)
            {
                exportFileIterative(hm, child);
            }
        }
    }

    /**
     * Export node to file
     * @param hm hierarchy manager
     * @param handle path to node
     * @param singleNode export single node in file
     */
    protected void exportFile(HierarchyManager hm, String handle, boolean singleNode)
    {
        try
        {
            if (singleNode)
            {
                String parentHandle = StringUtils.substringBeforeLast(handle, "/");
                String parentXmlName = watch.getRepository() + StringUtils.replace(parentHandle, "/", ".") + ".xml";
                if (parentHandle.length() == 0)
                {
                    parentHandle = "/";
                }
                BootstrapAtomicFilter xmlReader = new BootstrapAtomicFilter(XMLReaderFactory
                    .createXMLReader(org.apache.xerces.parsers.SAXParser.class.getName()));

                exportFile(parentXmlName, xmlReader, hm, parentHandle);
            }
            String xmlName = watch.getRepository() + StringUtils.replace(handle, "/", ".") + ".xml";

            XMLReader xmlReader = null;

            if (singleNode)
            {
                xmlReader = new BootstrapAtomicFilter(XMLReaderFactory
                    .createXMLReader(org.apache.xerces.parsers.SAXParser.class.getName()));
            }
            exportFile(xmlName, xmlReader, hm, handle);
        }
        catch (SAXException ex)
        {
            throw new RuntimeException(ex);
        }
    }

    /**
     * @param fileName file name
     * @param reader xml filter
     * @param hm hierarchy manager
     * @param handle path to node
     */
    protected void exportFile(String fileName, XMLReader reader, HierarchyManager hm, String handle)
    {
        // do not export root (ex. website.xml)
        if (StringUtils.isNotBlank(handle) && !StringUtils.equals(handle, "/"))
        {
            // create necessary parent directories
            File folder = new File(Path.getAbsoluteFileSystemPath(watch.getExportPath()));
            folder.mkdirs();

            File xmlFile = new File(folder.getAbsoluteFile(), fileName);
            FileOutputStream fos = null;
            try
            {
                fos = new FileOutputStream(xmlFile);
            }
            catch (FileNotFoundException e)
            {
                log.error(e.getMessage(), e);
                return;
            }

            try
            {
                executeExport(fos, reader, hm.getWorkspace().getSession(), handle, watch.getRepository());
            }
            catch (IOException e)
            {
                log.error(e.getMessage(), e);
            }
            finally
            {
                IOUtils.closeQuietly(fos);
            }
        }

    }

    /**
     * Clean files for node (path)
     * @param path path to node
     */
    protected void cleanFileSystem(String path)
    {

        String baseName = watch.getRepository() + StringUtils.replace(path, "/", ".");

        // create necessary parent directories
        File folder = new File(Path.getAbsoluteFileSystemPath(watch.getExportPath()));

        if (folder.exists())
        {
            String[] files = folder.list();
            for (String file : files)
            {
                File f = new File(folder, file);
                if (f.exists() && f.getName().startsWith(baseName))
                {
                    f.delete();
                    log.debug("Removed File: " + f.getName());
                }
            }
        }
    }

    /**
     * Execute export
     * @param baseOutputStream file output stream
     * @param reader xml filter
     * @param session jcr session
     * @param basepath path to node
     * @param repository repository
     * @throws IOException exception writing file
     */
    protected static void executeExport(OutputStream baseOutputStream, XMLReader reader, Session session,
        String basepath, String repository) throws IOException
    {
        OutputStream outputStream = baseOutputStream;

        try
        {
            // use XMLSerializer and a SAXFilter in order to rewrite the
            // file

            XMLReader xmlReader = reader;
            if (reader == null)
            {
                xmlReader = new VersionFilter(XMLReaderFactory
                    .createXMLReader(org.apache.xerces.parsers.SAXParser.class.getName()));
            }
            else
            {
                xmlReader = new VersionFilter(reader);
            }

            DataTransporter.parseAndFormat(outputStream, xmlReader, repository, basepath, session, false);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
        catch (SAXException e)
        {
            throw new RuntimeException(e);
        }
        catch (RepositoryException e)
        {
            throw new RuntimeException(e);
        }

        // finish the stream properly if zip stream
        // this is not done by the IOUtils
        if (outputStream instanceof DeflaterOutputStream)
        {
            ((DeflaterOutputStream) outputStream).finish();
        }

        baseOutputStream.flush();
        IOUtils.closeQuietly(baseOutputStream);
    }

}
