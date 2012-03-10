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

import java.awt.image.BufferedImage;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Example post processor that logs required image size and resulting image size if the parameter "logsize" is specified
 * @author molaschi
 * @version $Id: $
 */
public class LogSizePostProcessor implements ImagePostProcessor
{

    /**
     * Logger.
     */
    private Logger log = LoggerFactory.getLogger(LogSizePostProcessor.class);

    /**
     * {@inheritDoc}
     */
    public BufferedImage processImage(BufferedImage image, int x, int y, Map<String, String> parameters)
    {
        if (parameters.containsKey("logsize"))
        {
            log.info("Image size [{},{}] - Required size [{},{}]", new Object[]{
                image.getWidth(),
                image.getHeight(),
                x,
                y });
        }
        return image;
    }

}
