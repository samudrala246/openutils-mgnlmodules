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
import info.magnolia.cms.core.Content;

import java.io.InputStream;

import net.sourceforge.openutils.mgnlmedia.media.utils.VideoMedataUtils;
import net.sourceforge.openutils.mgnlmedia.media.utils.VideoMedataUtils.VideoMetaData;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Type handler for flv videos
 * @author molaschi
 * @version $Id$
 */
public class VideoTypeHandler extends BaseVideoTypeHandler
{

    /**
     * Logger.
     */
    private Logger log = LoggerFactory.getLogger(VideoTypeHandler.class);

    protected final String DURATION_ATTRIBUTE = "duration";

    @Override
    protected VideoMetaData parseFLVMetaData(Content media) throws Exception
    {
        InputStream stream = getOriginalFileNodeData(media).getStream();
        try
        {
            VideoMetaData metaData = VideoMedataUtils.parsefromStream(getOriginalFileNodeData(media).getAttribute(
                FileProperties.PROPERTY_EXTENSION), stream);
            if (metaData != null && metaData.getFileSize() == 0)
            {
                metaData.setFileSize(Long.parseLong(getOriginalFileNodeData(media).getAttribute(
                    FileProperties.PROPERTY_SIZE)));
            }
            return metaData;
        }
        finally
        {
            IOUtils.closeQuietly(stream);
        }
    }

}
