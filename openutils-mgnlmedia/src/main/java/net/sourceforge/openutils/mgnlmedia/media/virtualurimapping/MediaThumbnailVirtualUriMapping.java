/**
 *
 * SimpleMedia Module for Magnolia CMS (http://www.openmindlab.com/lab/products/media.html)
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

package net.sourceforge.openutils.mgnlmedia.media.virtualurimapping;

import info.magnolia.cms.beans.config.VirtualURIMapping;
import info.magnolia.cms.core.Content;
import net.sourceforge.openutils.mgnlmedia.media.tags.el.MediaEl;

import org.apache.commons.lang.StringUtils;


/**
 * @author cstrap
 */
public class MediaThumbnailVirtualUriMapping implements VirtualURIMapping
{

    private static String PREFIX_MAPPING = "/mediathumbnail/";

    /**
     * {@inheritDoc}
     */
    public MappingResult mapURI(String uri)
    {

        if (uri.startsWith(PREFIX_MAPPING))
        {
            String uuid = StringUtils.substringAfter(uri, PREFIX_MAPPING);
            Content media = MediaEl.node(uuid);
            if (media != null)
            {
                MappingResult mr = new MappingResult();
                mr.setToURI("redirect:" + MediaEl.thumbnail(media));
                mr.setLevel(1);
                return mr;
            }
        }

        return null;
    }

}