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

import info.magnolia.cms.core.MgnlNodeType;

import java.awt.Dimension;
import java.io.InputStream;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.apache.commons.io.IOUtils;
import org.devlib.schmidt.imageinfo.ImageInfo;
import org.freehep.graphicsio.swf.SWFHeader;
import org.freehep.graphicsio.swf.SWFInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Type handler for Flash SWF files.
 * @author fgiust
 * @version $Id$
 */
public class SwfTypeHandler extends MediaWithPreviewImageTypeHandler
{

    /**
     * Logger.
     */
    private Logger log = LoggerFactory.getLogger(SwfTypeHandler.class);

    @Override
    public boolean onPostSave(Node media)
    {
        InputStream stream = null;
        try
        {
            stream = getOriginalFileNodeData(media)
                .getProperty(MgnlNodeType.JCR_DATA)
                .getValue()
                .getBinary()
                .getStream();

            Dimension dimension = null;
            Integer flashversion = null;
            try
            {
                SWFInputStream swfinput = new SWFInputStream(stream);
                SWFHeader header = new SWFHeader(swfinput);
                dimension = header.getSize();
                flashversion = header.getVersion();
            }
            catch (Throwable e)
            {
                log.warn("Unable to parse swf header: " + e.getClass().getName() + " " + e.getMessage());
            }

            if (dimension != null)
            {
                media.setProperty(METADATA_WIDTH, dimension.getWidth());
                media.setProperty(METADATA_HEIGHT, dimension.getHeight());
                media.setProperty("media_flashversion", flashversion);
                media.getSession().save();
            }
            else if (hasPreview(media))
            {
                IOUtils.closeQuietly(stream);
                stream = media.getProperty(getPreviewImageNodeDataName()).getBinary().getStream();

                ImageInfo ii = new ImageInfo();
                ii.setInput(stream);
                if (ii.check())
                {
                    media.setProperty(METADATA_WIDTH, ii.getWidth());
                    media.setProperty(METADATA_HEIGHT, ii.getHeight());
                    media.getSession().save();
                }
            }

        }
        catch (Throwable e)
        {
            try
            {
                log.warn("Error extracting metadata "
                    + getOriginalFileNodeData(media).getPath()
                    + " "
                    + e.getClass().getName()
                    + " "
                    + e.getMessage(), e);
            }
            catch (RepositoryException e1)
            {
                // do nothing
            }
        }
        finally
        {
            IOUtils.closeQuietly(stream);
        }

        return super.onPostSave(media);
    }

    @Override
    public String getReplacementThumbnail()
    {
        return "/.resources/media/icons/thumb-swf.png";
    }
}
