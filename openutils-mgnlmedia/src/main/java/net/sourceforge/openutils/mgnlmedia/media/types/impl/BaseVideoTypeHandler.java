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

import java.util.Map;

import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.ValueFormatException;

import net.sourceforge.openutils.mgnlmedia.media.utils.MediaMetadataFormatUtils;
import net.sourceforge.openutils.mgnlmedia.media.utils.VideoMedataUtils.VideoMetaData;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author dschivo
 */
public abstract class BaseVideoTypeHandler extends MediaWithPreviewImageTypeHandler
{

    /**
     * Logger.
     */
    private Logger log = LoggerFactory.getLogger(BaseVideoTypeHandler.class);

    protected abstract VideoMetaData parseFLVMetaData(Node media) throws Exception;

    @Override
    public boolean onPostSave(Node media)
    {
        try
        {
            VideoMetaData flvMetaData = null;
            try
            {
                flvMetaData = parseFLVMetaData(media);
            }
            catch (Throwable e)
            {
                log.warn("Error parsing video file "
                    + getOriginalFileNodeData(media).getPath()
                    + " "
                    + e.getClass().getName()
                    + " "
                    + e.getMessage(), e);
            }

            if (flvMetaData != null)
            {

                media.setProperty(METADATA_EXTENSION, getExtension(media));
                media.setProperty(METADATA_WIDTH, flvMetaData.getWidth());
                media.setProperty(METADATA_HEIGHT, flvMetaData.getHeight());
                media.setProperty(METADATA_DURATION, flvMetaData.getDuration());
                media.setProperty(METADATA_FRAMERATE, flvMetaData.getFrameRate());

                media.save();
            }
        }
        catch (Exception e)
        {
            log.error(e.getMessage(), e);
        }
        return super.onPostSave(media);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, String> getMediaInfo(Node media)
    {

        Map<String, String> info = super.getMediaInfo(media);

        long duration;
        try
        {
            duration = media.getProperty(METADATA_DURATION).getLong();

            if (duration > 0)
            {
                info.put(METADATA_DURATION, MediaMetadataFormatUtils.formatDuration(duration));
            }

            long framerate = media.getProperty(METADATA_FRAMERATE).getLong();
            if (framerate > 0)
            {
                info.put(METADATA_FRAMERATE, Long.toString(framerate));
            }
        }
        catch (ValueFormatException e)
        {
            // do nothing
        }
        catch (PathNotFoundException e)
        {
            // do nothing
        }
        catch (RepositoryException e)
        {
            // do nothing

        }

        return info;
    }

    @Override
    public String getReplacementThumbnail()
    {
        return "/.resources/media/icons/thumb-video.png";
    }

}
