/**
 *
 * E-learning Module for Magnolia CMS (http://www.openmindlab.com/lab/products/lms.html)
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
package net.sourceforge.openutils.mgnllms.filters;

import info.magnolia.cms.core.AggregationState;
import info.magnolia.cms.core.HierarchyManager;
import info.magnolia.cms.core.NodeData;
import info.magnolia.context.MgnlContext;
import info.magnolia.rendering.engine.RenderingEngine;
import info.magnolia.rendering.engine.RenderingFilter;
import info.magnolia.rendering.template.registry.TemplateDefinitionRegistry;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import javax.jcr.PathNotFoundException;
import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;
import javax.jcr.Value;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Allow serving content from zip files stored as jcr binary properties
 * @author molaschi
 * @version $Id: $
 */
public class RenderingServingZipFilter extends RenderingFilter
{

    /**
     * Logger.
     */
    private Logger log = LoggerFactory.getLogger(RenderingServingZipFilter.class);

    public RenderingServingZipFilter(
        RenderingEngine renderingEngine,
        TemplateDefinitionRegistry templateDefinitionRegistry)
    {
        super(renderingEngine, templateDefinitionRegistry);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void handleResourceRequest(AggregationState aggregationState, HttpServletRequest request,
        HttpServletResponse response) throws IOException
    {
        final String resourceHandle = aggregationState.getHandle();
        log.debug("handleResourceRequest, resourceHandle=\"{}\"", resourceHandle); //$NON-NLS-1$

        if (StringUtils.isNotEmpty(resourceHandle) && aggregationState.getFile() != null)
        {

            HierarchyManager hm = MgnlContext.getHierarchyManager(aggregationState.getRepository());

            String tempDir = System.getProperty("java.io.tmpdir");
            File parentFile = new File(tempDir);
            if (!parentFile.exists())
            {
                parentFile.mkdirs();
            }

            InputStream is = null;
            try
            {

                String zippedResourceURI = StringUtils.substringAfter(aggregationState.getCurrentURI(), resourceHandle);
                if (StringUtils.isBlank(zippedResourceURI))
                {
                    zippedResourceURI = "/";
                }
                zippedResourceURI = zippedResourceURI.substring(1);

                String fileName = aggregationState.getFile().getFileName()
                    + "."
                    + aggregationState.getFile().getExtension();

                File requestResource = new File(parentFile, fileName);
                if (!requestResource.exists())
                {
                    is = getNodedataAstream(resourceHandle, hm, response);
                }
                if (null != is || requestResource.exists())
                {

                    if ("zip".equals(aggregationState.getFile().getExtension()))
                    {

                        File temp = null;
                        try
                        {
                            if (StringUtils.isBlank(zippedResourceURI)
                                || StringUtils.equalsIgnoreCase(fileName, zippedResourceURI))
                            {
                                if (StringUtils.isBlank(zippedResourceURI))
                                {
                                    response.setContentType("application/zip"); // use
                                    // aggregationState.getFile().getContentType()
                                    // after
                                    // info.magnolia.cms.beans.runtime.File.java
                                    // fixing
                                    response.setContentLength(aggregationState.getFile().getSize());
                                    response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
                                }
                                sendUnCompressed(is, response);
                            }
                            else
                            {

                                temp = new File(parentFile, fileName);
                                if (!temp.exists())
                                {
                                    temp = new File(parentFile, fileName);

                                    FileOutputStream fos = new FileOutputStream(temp);
                                    IOUtils.copy(is, fos);

                                    unZipIt(fileName, tempDir);

                                    IOUtils.closeQuietly(fos);
                                    IOUtils.closeQuietly(is);

                                }

                                String newPath = temp.getAbsolutePath().substring(
                                    0,
                                    temp.getAbsolutePath().length() - 4);
                                File requestFile = new File(newPath, zippedResourceURI);
                                if (requestFile.exists())
                                {
                                    is = new ByteArrayInputStream(FileUtils.readFileToByteArray(requestFile));
                                    response.setContentLength(((int) requestFile.length()));
                                    sendUnCompressed(is, response);
                                    IOUtils.closeQuietly(is);
                                    return;
                                }

                            }

                            // ZipFile zip = new ZipFile(temp);
                            //
                            // Enumeration< ? extends ZipEntry> entries = zip.entries();
                            // while (entries.hasMoreElements())
                            // {
                            // ZipEntry entry = entries.nextElement();
                            //
                            // String path = entry.getName();
                            //
                            // if (zippedResourceURI.equals(path))
                            // {
                            //
                            // InputStream inputStream = zip.getInputStream(entry);
                            // response.setContentLength(((int) entry.getSize()));
                            // sendUnCompressed(inputStream, response);
                            // IOUtils.closeQuietly(inputStream);
                            //
                            // return;
                            //
                            // }
                            // }
                            log.debug(
                                "Resource not found, redirecting request for [{}] to 404 URI",
                                request.getRequestURI());

                            if (!response.isCommitted())
                            {
                                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                            }
                            else
                            {
                                log.info(
                                    "Unable to redirect to 404 page, response is already committed. URI was {}",
                                    request.getRequestURI());
                            }
                            // stop the chain
                            return;
                        }
                        catch (IOException e)
                        {
                            log.error(e.getMessage(), e);
                        }
                        finally
                        {
                            IOUtils.closeQuietly(is);
                            if (temp != null)
                            {
                                // temp.delete();
                            }
                        }
                    }
                    else
                    {
                        // don't reset any existing status code, see MAGNOLIA-2005
                        // response.setStatus(HttpServletResponse.SC_OK);
                        sendUnCompressed(is, response);
                        IOUtils.closeQuietly(is);
                    }
                    return;
                }
            }
            catch (IOException e)
            {
                // don't log at error level since tomcat tipically throws a
                // org.apache.catalina.connector.ClientAbortException if the user stops loading the page
                log.debug("Exception while dispatching resource " + e.getClass().getName() + ": " + e.getMessage(), e); //$NON-NLS-1$ //$NON-NLS-2$
                return;
            }
            catch (Exception e)
            {
                log.error("Exception while dispatching resource  " + e.getClass().getName() + ": " + e.getMessage(), e); //$NON-NLS-1$ //$NON-NLS-2$
                return;
            }
            finally
            {
                IOUtils.closeQuietly(is);
            }
        }
        log.debug("Resource not found, redirecting request for [{}] to 404 URI", request.getRequestURI()); //$NON-NLS-1$

        if (!response.isCommitted())
        {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
        else
        {
            log.info("Unable to redirect to 404 page for {}, response is already committed", request.getRequestURI()); //$NON-NLS-1$
        }
    }

    private void unZipIt(String zipFile, String tempDir)
    {

        zipFile = tempDir + File.separator + zipFile;
        log.debug(zipFile);
        int BUFFER = 2048;
        File file = new File(zipFile);

        ZipFile zip;
        try
        {
            zip = new ZipFile(file);

            String newPath = zipFile.substring(0, zipFile.length() - 4);

            new File(newPath).mkdir();
            Enumeration zipFileEntries = zip.entries();

            // Process each entry
            while (zipFileEntries.hasMoreElements())
            {
                // grab a zip file entry
                ZipEntry entry = (ZipEntry) zipFileEntries.nextElement();
                String currentEntry = entry.getName();
                File destFile = new File(newPath, currentEntry);
                // destFile = new File(newPath, destFile.getName());
                File destinationParent = destFile.getParentFile();

                // create the parent directory structure if needed
                destinationParent.mkdirs();

                if (!entry.isDirectory())
                {
                    BufferedInputStream is = new BufferedInputStream(zip.getInputStream(entry));
                    int currentByte;
                    // establish buffer for writing file
                    byte data[] = new byte[BUFFER];

                    // write the current file to disk
                    FileOutputStream fos = new FileOutputStream(destFile);
                    BufferedOutputStream dest = new BufferedOutputStream(fos, BUFFER);

                    // read and write until last byte is encountered
                    while ((currentByte = is.read(data, 0, BUFFER)) != -1)
                    {
                        dest.write(data, 0, currentByte);
                    }
                    dest.flush();
                    dest.close();
                    is.close();
                }

// if (currentEntry.endsWith(".zip"))
// {
// // found a zip file, try to open
// extractFolder(destFile.getAbsolutePath());
// }
            }
        }
        catch (ZipException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Send data as is.
     * @param is Input stream for the resource
     * @param response HttpServletResponse as received by the service method
     * @throws IOException standard servlet exception
     */
    private void sendUnCompressed(InputStream is, HttpServletResponse response) throws IOException
    {
        ServletOutputStream os = response.getOutputStream();
        byte[] buffer = new byte[8192];
        int read;
        while ((read = is.read(buffer)) > 0)
        {
            os.write(buffer, 0, read);
        }
        os.flush();
        IOUtils.closeQuietly(os);
    }

    /**
     * @param path path for nodedata in jcr repository
     * @param hm Hierarchy manager
     * @param res HttpServletResponse
     * @return InputStream or <code>null</code> if nodeData is not found
     */
    private InputStream getNodedataAstream(String path, HierarchyManager hm, HttpServletResponse res)
    {

        log.debug("getNodedataAstream for path \"{}\"", path); //$NON-NLS-1$

        try
        {
            NodeData atom = hm.getNodeData(path);
            if (atom != null)
            {
                if (atom.getType() == PropertyType.BINARY)
                {

                    String sizeString = atom.getAttribute("size"); //$NON-NLS-1$
                    if (NumberUtils.isNumber(sizeString))
                    {
                        res.setContentLength(Integer.parseInt(sizeString));
                    }
                }

                Value value = atom.getValue();
                if (value != null)
                {
                    return value.getStream();
                }
            }

            log.warn("Resource not found: [{}]", path); //$NON-NLS-1$

        }
        catch (PathNotFoundException e)
        {
            log.warn("Resource not found: [{}]", path); //$NON-NLS-1$
        }
        catch (RepositoryException e)
        {
            log.error("RepositoryException while reading Resource [" + path + "]", e); //$NON-NLS-1$ //$NON-NLS-2$
        }
        return null;
    }
}
