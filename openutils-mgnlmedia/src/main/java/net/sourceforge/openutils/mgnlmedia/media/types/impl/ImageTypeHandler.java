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

import info.magnolia.cms.beans.runtime.FileProperties;
import info.magnolia.cms.core.MgnlNodeType;
import info.magnolia.jcr.util.NodeUtil;
import info.magnolia.jcr.util.PropertyUtil;
import info.magnolia.objectfactory.Components;
import it.openutils.mgnlutils.util.NodeUtilsExt;

import java.awt.image.BufferedImage;
import java.io.InputStream;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import net.sourceforge.openutils.mgnlmedia.media.configuration.MediaConfigurationManager;
import net.sourceforge.openutils.mgnlmedia.media.utils.IcoUtils;
import net.sourceforge.openutils.mgnlmedia.media.utils.ImageUtils;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.devlib.schmidt.imageinfo.ImageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Media Type Handler that manages images (jpg, png, gif)
 * @author molaschi
 * @version $Id$
 */
public class ImageTypeHandler extends BaseTypeHandler
{

    /**
     * Logger.
     */
    private Logger log = LoggerFactory.getLogger(ImageTypeHandler.class);

    /**
     * {@inheritDoc}
     */
    public String getThumbnailUrl(Node media)
    {
        if (!ImageUtils.checkOrCreateResolution(media, "thumbnail", BaseTypeHandler.ORGINAL_NODEDATA_NAME))
        {
            return StringUtils.EMPTY;
        }

        try
        {
            return Components.getComponent(MediaConfigurationManager.class).getURIMappingPrefix()
                + NodeUtil.getPathIfPossible(media)
                + "/resolutions/thumbnail/"
                + media.getName()
                + "."
                + ImageUtils.getExtension(media, "thumbnail");
        }
        catch (RepositoryException e)
        {
            return StringUtils.EMPTY;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getPreviewUrl(Node media)
    {
        if (!ImageUtils.checkOrCreateResolution(media, "preview", BaseTypeHandler.ORGINAL_NODEDATA_NAME))
        {
            return StringUtils.EMPTY;
        }
        try
        {
            return Components.getComponent(MediaConfigurationManager.class).getURIMappingPrefix()
                + NodeUtil.getPathIfPossible(media)
                + "/resolutions/preview/"
                + media.getName()
                + "."
                + ImageUtils.getExtension(media, "preview");
        }
        catch (RepositoryException e)
        {
            throw new RuntimeException();
        }
    }

    @Override
    public boolean onPostSave(Node media)
    {
        InputStream stream = null;
        try
        {
            Node nodeData = getOriginalFileNodeData(media);
            stream = getOriginalFileNodeData(media)
                .getProperty(MgnlNodeType.JCR_DATA)
                .getValue()
                .getBinary()
                .getStream();
            ImageInfo ii = new ImageInfo();
            ii.setInput(stream);
            if (ii.check())
            {
                media.setProperty(METADATA_BITDEPTH, ii.getBitsPerPixel());
                media.setProperty(METADATA_WIDTH, ii.getWidth());
                media.setProperty(METADATA_HEIGHT, ii.getHeight());
                media.getSession().save();
            }
            else if (StringUtils.equals(PropertyUtil.getString(nodeData, FileProperties.EXTENSION), "ico"))
            {
                BufferedImage bi = IcoUtils.createBufferedImage(nodeData);
                if (bi != null)
                {

                    media.setProperty(METADATA_BITDEPTH, bi.getColorModel().getPixelSize());
                    media.setProperty(METADATA_WIDTH, bi.getWidth());
                    media.setProperty(METADATA_HEIGHT, bi.getHeight());
                    media.getSession().save();
                }
                bi.flush();
            }
        }
        catch (Throwable e)
        {
            try
            {
                log.warn("Error determining bit depth "
                    + getOriginalFileNodeData(media).getPath()
                    + " "
                    + e.getClass().getName()
                    + " "
                    + e.getMessage(), e);
            }
            catch (RepositoryException re)
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

}
