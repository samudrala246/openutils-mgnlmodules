/**
 *
 * SimpleMedia Module for Magnolia CMS (http://www.openmindlab.com/lab/products/media.html)
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

package net.sourceforge.openutils.mgnlmedia.media.types.impl;

import info.magnolia.cms.beans.runtime.Document;
import info.magnolia.cms.beans.runtime.FileProperties;
import info.magnolia.cms.core.NodeData;
import info.magnolia.cms.util.NodeDataUtil;
import info.magnolia.module.admininterface.SaveHandlerImpl;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

import javax.imageio.ImageIO;
import javax.jcr.Node;
import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Type handler for generic "document" handling (pdf files).
 * @author dschivo
 * @version $Id$
 */
public class DocumentTypeHandler extends MediaWithPreviewImageTypeHandler
{

    /**
     * metadata containing the number of pages
     */
    private static final String METADATA_PAGES = "media_pages";

    /**
     * Logger.
     */
    private Logger log = LoggerFactory.getLogger(DocumentTypeHandler.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUrl(Node media)
    {
        return getUrl(media, Collections.<String, String> emptyMap());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onPostSave(Node media)
    {

        NodeData data = getOriginalFileNodeData(media);
        if (data.getType() == PropertyType.BINARY)
        {

            if (StringUtils.equalsIgnoreCase(data.getAttribute(FileProperties.EXTENSION), "pdf"))
            {

                try
                {
                    String filename = data.getAttribute(FileProperties.PROPERTY_FILENAME) + ".png";

                    InputStream stream = data.getStream();
                    try
                    {
                        createPdfPreview(media, stream, filename);
                    }
                    finally
                    {
                        IOUtils.closeQuietly(stream);
                    }
                }
                catch (Throwable e)
                {
                    log.warn("Unable to generate a preview for {} due to a {}: {}", new Object[]{
                        media.getHandle(),
                        e.getClass().getName(),
                        e.getMessage() });
                }
            }

        }

        return super.onPostSave(media);
    }

    /**
     * Automatically create a thumbnail from the pdf.
     * @param media main media node
     * @param stream inputStream for the original pdf
     * @param filename original filename
     */
    protected void createPdfPreview(Node media, InputStream stream, String filename)
    {

        PDDocument document = null;
        try
        {

            document = PDDocument.load(stream);

            List<PDPage> pages = document.getDocumentCatalog().getAllPages();

            NodeDataUtil.getOrCreateAndSet(media, METADATA_PAGES, document.getNumberOfPages());

            if (!pages.isEmpty())
            {

                PDPage page = pages.get(0);

                java.awt.image.BufferedImage image = page.convertToImage(BufferedImage.TYPE_INT_ARGB, 72);

                File file = File.createTempFile(filename, ".png");
                ImageIO.write(image, "png", file);

                copyPreviewImageToRepository(media, file, filename);

                media.save();

                file.delete();
            }
        }
        catch (NoClassDefFoundError e)
        {
            log.warn("Apache pdfbox 1.0.0 not available, not generating preview for pdf document");
        }
        catch (IOException e)
        {
            if (e.getCause() instanceof ClassCastException)
            {
                log
                    .warn("Two conflicting versions of pdfbox are loaded, only one pdfbox jar version 1.x must be loaded in order to make thumbnail generation work. "
                        + "Not generating preview for pdf document");
            }
            else
            {
                log.error("Error creating preview for " + media.getHandle(), e);
            }
        }
        catch (Throwable e)
        {
            log.error("Error creating preview for " + media.getHandle(), e);
        }
        finally
        {
            if (document != null)
            {
                try
                {
                    document.close();
                }
                catch (IOException e)
                {
                    // ignore
                }
            }
        }
    }

    /**
     * Copy a preview image to a nodedata.
     * @param media main media node
     * @param file File to be copied
     * @param filename filename
     */
    protected void copyPreviewImageToRepository(Node media, File file, String filename)
    {

        Document doc = new Document(file, "image/png");
        try
        {
            SaveHandlerImpl.saveDocument(media, doc, DocumentTypeHandler.PREVIEW_NODEDATA_NAME, filename, null);
        }
        catch (RepositoryException e)
        {
            log.error(e.getMessage(), e);
        }
        finally
        {
            doc.delete();
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getReplacementThumbnail()
    {
        return "/.resources/media/icons/thumb-document.png";
    }
}
