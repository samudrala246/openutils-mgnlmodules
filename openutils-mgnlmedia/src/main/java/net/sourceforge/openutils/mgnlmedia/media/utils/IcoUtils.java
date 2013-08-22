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

package net.sourceforge.openutils.mgnlmedia.media.utils;

import info.magnolia.cms.core.NodeData;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import org.apache.commons.io.IOUtils;


/**
 * @author fgiust
 * @version $Id$
 */
public class IcoUtils
{

    public static BufferedImage createBufferedImage(NodeData image) throws IOException
    {
        // this should work for any image type, but better don't change the ImageIO.read() that worked till
        // now...
        InputStream is = image.getStream();
        try
        {
            nl.ikarus.nxt.priv.imageio.icoreader.lib.ICOReaderSpi.registerIcoReader();
            ImageInputStream in = ImageIO.createImageInputStream(is);
            Iterator<ImageReader> it = ImageIO.getImageReaders(in);
            ImageReader r = null;
            while (it.hasNext())
            {

                try
                {
                    r = it.next();
                    r.setInput(in);

                    BufferedImage read = r.read(-1);

                    return read;

                }
                catch (Throwable ex)
                {
                    IOUtils.closeQuietly(is);
                    is = image.getStream();
                    in = ImageIO.createImageInputStream(image.getStream());
                }
            }
        }
        finally
        {
            IOUtils.closeQuietly(is);
        }

        return null;
    }

}
