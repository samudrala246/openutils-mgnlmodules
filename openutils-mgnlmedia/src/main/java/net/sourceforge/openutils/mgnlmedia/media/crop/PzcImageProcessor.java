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

package net.sourceforge.openutils.mgnlmedia.media.crop;

import java.awt.image.BufferedImage;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import net.sourceforge.openutils.mgnlmedia.media.processors.ResizeCropCenteredImageResolutionProcessor;
import net.sourceforge.openutils.mgnlmedia.media.utils.ImageUtils;


/**
 * @author molaschi
 * @version $Id: $
 */
public class PzcImageProcessor extends ResizeCropCenteredImageResolutionProcessor
{

    /**
     * {@inheritDoc}
     */
    @Override
    public BufferedImage getImageForResolution(BufferedImage original, int x, int y, Map<String, String> parameters)
    {
        int oWidth = original.getWidth();
        int oHeight = original.getHeight();

        if (x == oWidth && y == oHeight)
        {
            // same size
            return original;
        }
        
        String pzcParametersString = parameters.get("pzc");

        if (StringUtils.isBlank(pzcParametersString))
        {
            return super.getImageForResolution(original, x, y, parameters);
        }
        
        String params[] = StringUtils.split(pzcParametersString, '|');
        
        float zoom = Float.parseFloat(params[0]) / 100.0f;
        float panX = Float.parseFloat(params[1]);
        float panY = Float.parseFloat(params[2]);
        float factor = 1.0f;
        if (params.length > 3)
        {
            factor = Float.parseFloat(params[3]);
        }

        float oX = oWidth * zoom * factor;
        float oY = oX * oHeight / oWidth;
        
        BufferedImage filled = ImageUtils.resizeImage(
            original,
            (int) oX,
            (int) oY,
            "true".equals(parameters.get("skipRendering")));
        return ImageUtils.cropImage(
            filled,
            (int) (panX * factor),
            (int) (panY * factor),
            x,
            y,
            "true".equals(parameters.get("skipRendering")));
    }

}
