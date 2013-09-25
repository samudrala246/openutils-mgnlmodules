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

import info.magnolia.test.ComponentsTestUtil;

import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.jcr.Node;

import net.sourceforge.openutils.mgnlmedia.media.configuration.ImageProcessorsManager;

import org.apache.commons.io.IOUtils;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;


/**
 * @author fgiust
 * @version $Id$
 */
public class ImageUtilsTest
{

    /**
     *
     */
    @BeforeMethod
    public void setup()
    {
        ComponentsTestUtil.setInstance(ImageProcessorsManager.class, new ImageProcessorsManagerMock());
    }

    /**
     * @throws Exception
     */
    @Test
    public void testYcck() throws Exception
    {

        Node cmyk = new FileNode("/images/ycck.jpg");
        BufferedImage bufferedImage = ImageUtils.createBufferedImage(cmyk);
        Assert.assertNotNull(bufferedImage);

        bufferedImage = ImageUtils.resizeImage(bufferedImage, 100, 100);

        File tempFile = File.createTempFile("image", ".jpg");
        OutputStream os = new BufferedOutputStream(new FileOutputStream(tempFile));

        ImageUtils.getStream(bufferedImage, "jpg", 1.0F, false, os);

        IOUtils.closeQuietly(os);

        tempFile.delete();

    }

    /**
     * Test for MEDIA-72
     * @throws Exception
     */
    @Test
    public void testInvalidJpeg() throws Exception
    {

        Node cmyk = new FileNode("/images/sample_invalid_colors.jpg");
        BufferedImage bufferedImage = ImageUtils.createBufferedImage(cmyk);
        Assert.assertNotNull(bufferedImage);

        bufferedImage = ImageUtils.resizeImage(bufferedImage, 100, 100);

        File tempFile = File.createTempFile("image", ".jpg");
        OutputStream os = new BufferedOutputStream(new FileOutputStream(tempFile));

        ImageUtils.getStream(bufferedImage, "jpg", 1.0F, false, os);

        IOUtils.closeQuietly(os);

        tempFile.delete();

    }

    /**
     * @throws Exception
     */
    @Test
    public void testBorders() throws Exception
    {

        Node cmyk = new FileNode("/images/openmind.png");
        BufferedImage bufferedImage = ImageUtils.createBufferedImage(cmyk);
        Assert.assertNotNull(bufferedImage);

        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("background", "FF0096");
        bufferedImage = ImageUtils.getImageForResolution(bufferedImage, "O300x300;background=FF0096", parameters);

        File tempFile = File.createTempFile("image", ".png");
        OutputStream os = new BufferedOutputStream(new FileOutputStream(tempFile));
        ImageUtils.getStream(bufferedImage, "png", 1.0F, false, os);

        IOUtils.closeQuietly(os);

        tempFile.delete();

    }

    /**
     * @throws Exception
     */
    @Test
    public void testIco() throws Exception
    {

        Node ico = new FileNode("/images/openmind.ico");
        BufferedImage bufferedImage = ImageUtils.createBufferedImage(ico);
        Assert.assertNotNull(bufferedImage);
    }

    /**
     * @throws Exception
     */
    @Test(expectedExceptions = BadImageFormatException.class)
    public void testBadImageTxt() throws Exception
    {

        Node cmyk = new FileNode("/images/badimage.txt");
        BufferedImage bufferedImage = ImageUtils.createBufferedImage(cmyk);
    }

    /**
     * @throws Exception
     */
    @Test(expectedExceptions = BadImageFormatException.class)
    public void testBadImageJpg() throws Exception
    {

        Node cmyk = new FileNode("/images/badimage.jpg");
        BufferedImage bufferedImage = ImageUtils.createBufferedImage(cmyk);
    }

    /**
     * test for MEDIA-33
     * @throws Exception
     */
    @Test
    public void testBadPaletteGif() throws Exception
    {

        Node badpalette = new FileNode("/images/badpalette.gif");
        BufferedImage bufferedImage = ImageUtils.createBufferedImage(badpalette);
        Assert.assertNotNull(bufferedImage);

        bufferedImage = ImageUtils.resizeImage(bufferedImage, 1, 1);

        File tempFile = File.createTempFile("image", ".gif");
        OutputStream os = new BufferedOutputStream(new FileOutputStream(tempFile));

        ImageUtils.getStream(bufferedImage, "gif", 0.8F, false, os);

        IOUtils.closeQuietly(os);

        tempFile.delete();
    }

    /**
     * transparent background
     * @throws Exception
     */
    @Test
    public void testTransparentGif() throws Exception
    {

        Node badpalette = new FileNode("/images/transparent.gif");
        BufferedImage bufferedImage = ImageUtils.createBufferedImage(badpalette);
        int transparency = bufferedImage.getColorModel().getTransparency();
        Assert.assertNotNull(bufferedImage);

        bufferedImage = ImageUtils.resizeImage(bufferedImage, 200, 200);

        File tempFile = File.createTempFile("image", ".gif");
        OutputStream os = new BufferedOutputStream(new FileOutputStream(tempFile));

        ImageUtils.getStream(bufferedImage, "gif", 0.8F, false, os);

        IOUtils.closeQuietly(os);

        tempFile.delete();
    }
}
