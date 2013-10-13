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

import info.magnolia.jcr.util.NodeUtil;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import net.sourceforge.openutils.mgnlmedia.media.configuration.MediaConfigurationManager;
import net.sourceforge.openutils.mgnlmedia.media.utils.ImageUtils;

import org.apache.commons.lang.StringUtils;


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
    public String getThumbnailUrl(Node media)
    {

        if (hasPreview(media))
        {
            if (!ImageUtils.checkOrCreateResolution(media, "thumbnail", PREVIEW_NODEDATA_NAME))
            {
                return StringUtils.EMPTY;
            }

            try
            {
                return MediaConfigurationManager.getInstance().getURIMappingPrefix()
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

        return getReplacementThumbnail();

    }

    public abstract String getReplacementThumbnail();

    protected boolean hasPreview(Node media)
    {

        if (media != null)
        {
            try
            {
                return media.hasNode(PREVIEW_NODEDATA_NAME);
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
