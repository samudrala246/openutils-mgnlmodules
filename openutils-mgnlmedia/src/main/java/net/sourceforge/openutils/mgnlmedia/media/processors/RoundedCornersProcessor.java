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

package net.sourceforge.openutils.mgnlmedia.media.processors;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.Map;

import net.sourceforge.openutils.mgnlmedia.media.utils.ImageUtils;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Generate rounded corners on images.<br/>
 * Activated by declaring the parameter <strong>roundcorners</strong>; its value can be:
 * <ul>
 * <li>no value or true: processor draws roundcorners with 5px radius</li>
 * <li>int value: processor draws roundcorners with passed radius</li>
 * </ul>
 * If the "background" parameter is specified (hex color), it will be applied when rounding corners.<br/>
 * If no "background" parameter is found, if the image format is png a transparent background is applied else a white
 * background is applied.
 * @author molaschi
 * @version $Id: $
 */
public class RoundedCornersProcessor implements ImagePostProcessor
{

    /**
     * Logger.
     */
    private Logger log = LoggerFactory.getLogger(RoundedCornersProcessor.class);

    /**
     * {@inheritDoc}
     */
    public BufferedImage processImage(BufferedImage image, int x, int y, Map<String, String> parameters)
    {
        String roundCorners = parameters.get("roundcorners");
        if (StringUtils.isNotBlank(roundCorners))
        {
            int radius = 5;
            if (!"true".equals(roundCorners))
            {
                radius = NumberUtils.toInt(roundCorners);
            }

            Color backgroundColor = null;
            String hexColor = parameters.get("background");
            if (StringUtils.isNotBlank(hexColor))
            {
                try
                {
                    hexColor = "0x" + (hexColor.startsWith("#") ? hexColor.substring(1) : hexColor);
                    backgroundColor = Color.decode(hexColor);
                }
                catch (NumberFormatException e)
                {
                    log.error("Invalid color code: " + hexColor, e);
                }
            }
            return ImageUtils.addRoundedCorners(image, backgroundColor, radius, "true".equals(parameters.get("skipRendering")));
        }
        return image;
    }

}
