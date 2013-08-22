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

import java.awt.Color;
import java.awt.image.BufferedImage;

import net.sourceforge.openutils.mgnlmedia.media.utils.ImageUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Base implementation of {@link ImageResolutionProcessor}.
 * @author molaschi
 * @version $Id: $
 */
public abstract class BaseImageResolutionProcessor implements ImageResolutionProcessor
{

    /**
     * Logger.
     */
    private Logger log = LoggerFactory.getLogger(BaseImageResolutionProcessor.class);

    /**
     * Resize an image to fit into x,y not changing image original side proportion
     * @param original original image
     * @param x max width
     * @param y max height
     * @return resized image
     */
    public BufferedImage fitIn(BufferedImage original, int x, int y)
    {
        return fitIn(original, x, y, false);
    }

    public BufferedImage fitIn(BufferedImage original, int x, int y, boolean skipRendering)
    {
        return resizeInOut(original, x, y, false, null, skipRendering);
    }

    /**
     * Resize an image to fit into the rect x, y and fill empty spaces with given background color
     * @param original original image
     * @param x max width
     * @param y max height
     * @param background background to color empty spaces
     * @return resized image
     */
    public BufferedImage fitIn(BufferedImage original, int x, int y, Color background)
    {
        return fitIn(original, x, y, background, false);
    }

    public BufferedImage fitIn(BufferedImage original, int x, int y, Color background, boolean skipRendering)
    {
        return resizeInOut(original, x, y, true, background, skipRendering);
    }

    /**
     * Resize image to fit in x,y. If fit is true the resulting image is x,y and if there are two empty bands they are
     * filled with backgroung color.
     * @param original original image
     * @param x max width
     * @param y max height
     * @param fit fully fit x,y?
     * @param background color to fill empty bands if fit is true
     * @return image
     */
    protected BufferedImage resizeInOut(BufferedImage original, int x, int y, boolean fit, Color background)
    {
        return resizeInOut(original, x, y, fit, background, false);
    }

    protected BufferedImage resizeInOut(BufferedImage original, int x, int y, boolean fit, Color background, boolean skipRendering)
    {
        if (original == null)
        {
            throw new IllegalArgumentException("input image is null");
        }

        float oX = original.getWidth();
        float oY = original.getHeight();

        if (oX < 1 || oY < 1)
        {
            throw new IllegalArgumentException("Broken input image (width=" + oX + ",height=" + oY + ")");
        }

        double xRatio = (double) x / oX;
        double yRatio = (double) y / oY;

        if (xRatio * yRatio == 1)
        {
            log.debug("Nothing to resize, return original");
            return original;
        }

        double ratio = Math.min(xRatio, yRatio);
        int newX = (int) Math.round(oX * ratio);
        int newY = (int) Math.round(oY * ratio);

        return ImageUtils.resizeImage(original, newX, newY, fit ? x : newX, fit ? y : newY, background, skipRendering);
    }

    /**
     * Resize an image to fill x or y and (if set) center and if cropCentered is true crop what outfit
     * @param original original image
     * @param x min width
     * @param y min height
     * @param cropCentered crop image?
     * @return resized image
     */
    public BufferedImage fill(BufferedImage original, int x, int y, boolean cropCentered)
    {
        return fill(original, x, y, cropCentered, false);
    }

    public BufferedImage fill(BufferedImage original, int x, int y, boolean cropCentered, boolean skipRendering)
    {
        int oWidth = original.getWidth();
        int oHeight = original.getHeight();
        if (x == oWidth && y == oHeight)
        {
            // same size
            return original;
        }

        float oX = oWidth;
        float oY = oHeight;
        float oDelta = oX / oY;
        float delta = ((float) x) / ((float) y);

        if (oDelta >= delta)
        {
            int newX = (int) (y * oX / oY);
            BufferedImage filled = ImageUtils.resizeImage(original, newX, y, skipRendering);
            return cropCentered ? ImageUtils.cropImage(filled, (newX - x) / 2, 0, x, y, skipRendering) : filled;
        }
        else
        // if (oDelta < delta)
        {
            int newY = (int) (x * oY / oX);
            BufferedImage filled = ImageUtils.resizeImage(original, x, newY, skipRendering);
            return cropCentered ? ImageUtils.cropImage(filled, 0, (newY - y) / 2, x, y, skipRendering) : filled;
        }
    }
}
