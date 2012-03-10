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

package net.sourceforge.openutils.mgnlmedia.media.types.impl;

import info.magnolia.cms.core.Content;

import javax.jcr.RepositoryException;

import net.sourceforge.openutils.mgnlmedia.media.configuration.MediaConfigurationManager;
import net.sourceforge.openutils.mgnlmedia.media.utils.ImageUtils;


/**
 * Base class for medias that has a dynamic image as thumbnail
 * @author molaschi
 * @version $Id: $
 */
public abstract class MediaWithPreviewImageTypeHandler extends BaseTypeHandler
{

    /**
     * Nodedata name where preview media content is saved
     */
    public static final String PREVIEW_NODEDATA_NAME = "image";

    /**
     * {@inheritDoc}
     */
    public String getThumbnailUrl(Content media)
    {

        if (hasPreview(media))
        {
            if (!ImageUtils.checkOrCreateResolution(media, "thumbnail", PREVIEW_NODEDATA_NAME))
            {
                return "";
            }
            return MediaConfigurationManager.getInstance().getURIMappingPrefix()
                + media.getHandle()
                + "/resolutions/thumbnail/"
                + media.getName()
                + "."
                + ImageUtils.getExtension(media, "thumbnail");
        }

        return getReplacementThumbnail();

    }

    public abstract String getReplacementThumbnail();

    protected boolean hasPreview(Content media)
    {

        if (media != null)
        {
            try
            {
                return media.hasNodeData(PREVIEW_NODEDATA_NAME);
            }
            catch (RepositoryException e)
            {
                // ignore
            }
        }

        return false;
    }

    @Override
    public String getPreviewImageNodeDataName()
    {
        return PREVIEW_NODEDATA_NAME;
    }
}
