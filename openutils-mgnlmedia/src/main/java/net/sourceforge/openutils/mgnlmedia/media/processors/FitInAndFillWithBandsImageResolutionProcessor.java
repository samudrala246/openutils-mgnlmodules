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

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Implementation of {@link ImageResolutionProcessor} that render image to fully fit the required resolution, eventually
 * adding colored bands.
 * @author molaschi
 * @version $Id: $
 */
public class FitInAndFillWithBandsImageResolutionProcessor extends BaseImageResolutionProcessor
{

    /**
     * Logger.
     */
    private Logger log = LoggerFactory.getLogger(FitInAndFillWithBandsImageResolutionProcessor.class);

    /**
     * {@inheritDoc}
     */
    public BufferedImage getImageForResolution(BufferedImage original, int x, int y, Map<String, String> parameters)
    {
        Color color = null;
        String hexColor = parameters.get("background");

        if (StringUtils.isNotBlank(hexColor))
        {
            try
            {
                hexColor = "0x" + (hexColor.startsWith("#") ? hexColor.substring(1) : hexColor);
                color = Color.decode(hexColor);
            }
            catch (NumberFormatException e)
            {
                log.error("Invalid color code: " + hexColor, e);
            }
        }
        return fitIn(original, x, y, color, "true".equals(parameters.get("skipRendering")));
    }
}
