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
package net.sourceforge.openutils.mgnlmedia.externalvideo;

import java.util.Map;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import net.sourceforge.openutils.mgnlmedia.media.types.impl.MediaWithPreviewImageTypeHandler;
import net.sourceforge.openutils.mgnlmedia.media.utils.MediaMetadataFormatUtils;

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

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, String> getMediaInfo(Node media)
    {

        Map<String, String> info = super.getMediaInfo(media);

        try
        {
            long duration = media.getProperty(METADATA_DURATION).getLong();
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
        catch (RepositoryException re)
        {
            log.error("Error getting external video media infos", re);
        }

        return info;
    }

}
