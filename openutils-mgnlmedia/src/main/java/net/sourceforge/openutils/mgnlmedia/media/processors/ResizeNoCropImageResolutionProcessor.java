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

package net.sourceforge.openutils.mgnlmedia.media.processors;

import java.awt.image.BufferedImage;
import java.util.Map;


/**
 * {@link ImageResolutionProcessor} that resizes an image to contain the required resolution. The resulting image should
 * outfit required resolution
 * @author molaschi
 * @version $Id: $
 */
public class ResizeNoCropImageResolutionProcessor extends BaseImageResolutionProcessor
{

    /**
     * {@inheritDoc}
     */
    public BufferedImage getImageForResolution(BufferedImage original, int x, int y, Map<String, String> parameters)
    {
        return fill(original, x, y, false, "true".equals(parameters.get("skipRendering")));
    }

}
