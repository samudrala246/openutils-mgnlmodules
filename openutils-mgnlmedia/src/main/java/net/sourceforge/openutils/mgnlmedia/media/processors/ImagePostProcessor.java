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


/**
 * Interface for processors that are called after the image resize has happened. Maybe, you could choose to apply or
 * skip the processor checking for a parameter in parameters map.
 * @author molaschi
 * @version $Id: $
 */
public interface ImagePostProcessor
{

    /**
     * Process image
     * @param image image to process
     * @param x width
     * @param y height
     * @param parameters parameters map
     * @return processed image
     */
    BufferedImage processImage(BufferedImage image, int x, int y, Map<String, String> parameters);
}
