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

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.Map;


/**
 * @author molaschi
 * @version $Id: $
 */
public class BlackAndWhitePostProcessor implements ImagePostProcessor
{

    /**
     * {@inheritDoc}
     */
    public BufferedImage processImage(BufferedImage image, int x, int y, Map<String, String> parameters)
    {
        if (parameters.get("bw") != null)
        {
            BufferedImage bwImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
            Graphics g = bwImage.getGraphics();
            g.drawImage(image, 0, 0, null);
            g.dispose();
            return bwImage;
        }
        return image;
    }

}
