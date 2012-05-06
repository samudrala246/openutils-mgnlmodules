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

package net.sourceforge.openutils.mgnlmedia.media.utils;

import info.magnolia.cms.core.NodeData;

import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.imaging.jpeg.JpegSegmentReader;


/**
 * Utility class for handling of CMYK/YCCK jpegs.
 * @author fgiust
 * @version $Id$
 */
public class JpegUtils
{

    /**
     * Logger.
     */
    private static Logger log = LoggerFactory.getLogger(JpegUtils.class);

    /**
     * Java's ImageIO can't process 4-component images and Java2D can't apply AffineTransformOp either, so convert
     * raster data to RGB. Technique due to MArk Stephens. Free for any use. See
     * http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4799903 or
     * http://www.mail-archive.com/java2d-interest@capra.eng.sun.com/msg03247.html
     * @param raster raster image
     * @param ycckProfile true for YCCK color profile (else CMYK)
     * @return image
     */
    public static BufferedImage createJPEG4(Raster raster, boolean ycckProfile)
    {
        int w = raster.getWidth();
        int h = raster.getHeight();
        byte[] rgb = new byte[w * h * 3];

        // if (Adobe_APP14 and transform==2) then YCCK else CMYK
        if (ycckProfile)
        { // YCCK -- Adobe

            float[] Y = raster.getSamples(0, 0, w, h, 0, (float[]) null);
            float[] Cb = raster.getSamples(0, 0, w, h, 1, (float[]) null);
            float[] Cr = raster.getSamples(0, 0, w, h, 2, (float[]) null);
            float[] K = raster.getSamples(0, 0, w, h, 3, (float[]) null);

            for (int i = 0, imax = Y.length, base = 0; i < imax; i++, base += 3)
            {
                float k = 220 - K[i], y = 255 - Y[i], cb = 255 - Cb[i], cr = 255 - Cr[i];

                double val = y + 1.402 * (cr - 128) - k;
                val = (val - 128) * .65f + 128;
                rgb[base] = val < 0.0 ? (byte) 0 : val > 255.0 ? (byte) 0xff : (byte) (val + 0.5);

                val = y - 0.34414 * (cb - 128) - 0.71414 * (cr - 128) - k;
                val = (val - 128) * .65f + 128;
                rgb[base + 1] = val < 0.0 ? (byte) 0 : val > 255.0 ? (byte) 0xff : (byte) (val + 0.5);

                val = y + 1.772 * (cb - 128) - k;
                val = (val - 128) * .65f + 128;
                rgb[base + 2] = val < 0.0 ? (byte) 0 : val > 255.0 ? (byte) 0xff : (byte) (val + 0.5);
            }

        }
        else
        {
            // assert xform==0: xform; // CMYK

            int[] C = raster.getSamples(0, 0, w, h, 0, (int[]) null);
            int[] M = raster.getSamples(0, 0, w, h, 1, (int[]) null);
            int[] Y = raster.getSamples(0, 0, w, h, 2, (int[]) null);
            int[] K = raster.getSamples(0, 0, w, h, 3, (int[]) null);

            for (int i = 0, imax = C.length, base = 0; i < imax; i++, base += 3)
            {
                int c = 255 - C[i];
                int m = 255 - M[i];
                int y = 255 - Y[i];
                int k = 255 - K[i];
                float kk = k / 255f;

                rgb[base] = (byte) (255 - Math.min(255f, c * kk + k));
                rgb[base + 1] = (byte) (255 - Math.min(255f, m * kk + k));
                rgb[base + 2] = (byte) (255 - Math.min(255f, y * kk + k));
            }
        }

        // from other image types we know InterleavedRaster's can be
        // manipulated by AffineTransformOp, so create one of those.
        raster = Raster.createInterleavedRaster(
            new DataBufferByte(rgb, rgb.length),
            w,
            h,
            w * 3,
            3,
            new int[]{0, 1, 2 },
            null);

        ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_sRGB);
        ColorModel cm = new ComponentColorModel(cs, false, true, Transparency.OPAQUE, DataBuffer.TYPE_BYTE);
        return new BufferedImage(cm, (WritableRaster) raster, true, null);
    }

    /**
     * process image
     * @param image
     * @return image
     */
    public static BufferedImage processNonStandardImage(NodeData image)
    {
        log.debug("Processing {}", image.getHandle());
        InputStream is2 = image.getStream();
        try
        {
            // Get an ImageReader.
            ImageInputStream input = ImageIO.createImageInputStream(is2);
            Iterator<ImageReader> readers = ImageIO.getImageReaders(input);
            if (readers == null || !readers.hasNext())
            {
                throw new RuntimeException("No ImageReaders found");
            }

            ImageReader reader = readers.next();
            reader.setInput(input);
            String format = reader.getFormatName();

            if ("JPEG".equalsIgnoreCase(format) || "JPG".equalsIgnoreCase(format))
            {
                Raster raster = reader.readRaster(0, reader.getDefaultReadParam());
                boolean ycckProfile = false;

                // yes, we need to read it once again to extract metadata
                InputStream is3 = image.getStream();
                try
                {

                    JpegSegmentReader segmentReader = new JpegSegmentReader(is3);
                    byte[] exifSegment = segmentReader.readSegment(JpegSegmentReader.SEGMENT_APPE);

                    if (exifSegment != null) // see MEDIA-72, rgb images have a null exif segment here
                    {
                        switch (exifSegment[11])
                        {
                            case 2 :
                                ycckProfile = true;
                                break;
                            case 1 :
                                // "YCbCr"
                                break;
                            case 0 :
                            default :
                                // Unknown (RGB or CMYK)
                                break;
                        }
                    }
                }
                catch (JpegProcessingException e1)
                {
                    log.warn("Unable to read color space");
                }
                finally
                {
                    IOUtils.closeQuietly(is3);
                }

                if (input != null)
                {
                    input.close();
                }
                reader.dispose();

                if (ycckProfile)
                {
                    // CMYK/YCCK image, need to process it manually
                    return createJPEG4(raster, ycckProfile);
                }
                else
                {
                    // may be either an RGB or CMYK image, try and see if it's RGB first
                    InputStream is4 = image.getStream();
                    try
                    {
                        // see MEDIA-72, we need the sun codec to make this work properly

                        Class< ? > sunjpegcodec = Class.forName("com.sun.image.codec.jpeg.JPEGCodec");
                        Method createJPEGDecoderMethod = sunjpegcodec.getMethod("createJPEGDecoder", InputStream.class);
                        Object jpegDecoder = createJPEGDecoderMethod.invoke(null, is4);
                        Method decodeAsBufferedImageMethod = jpegDecoder.getClass().getMethod(
                            "decodeAsBufferedImage",
                            new Class[0]);
                        BufferedImage result = (BufferedImage) decodeAsBufferedImageMethod.invoke(jpegDecoder, null);

                        return result;
                    }
                    catch (ClassNotFoundException e)
                    {
                        log.info("com.sun.image.codec.jpeg.JPEGCodec not available, can't process non-standard jpeg");
                        return createJPEG4(raster, ycckProfile);
                    }
                    catch (NoSuchMethodException e)
                    {
                        log.info("com.sun.image.codec.jpeg.JPEGCodec not available, can't process non-standard jpeg");
                        return createJPEG4(raster, ycckProfile);
                    }
                    catch (IllegalAccessException e)
                    {
                        // should never happen
                        log.error(e.getMessage(), e);
                        return createJPEG4(raster, ycckProfile);
                    }
                    catch (InvocationTargetException e)
                    {
                        log.debug(e.getTargetException().getMessage(), e.getTargetException());
                        // Icom.sun.image.codec,jpeg.ImageFormatException: Can't construct a BufferedImage for given
                        // COLOR_ID try also with CMYK?
                        return createJPEG4(raster, ycckProfile);
                    }
                    finally
                    {
                        IOUtils.closeQuietly(is4);
                    }
                }

            }
            throw new BadImageFormatException("No ImageReaders found for " + image.getHandle());

        }
        catch (IOException e1)
        {
            log.error("Unable to handle " + image.getHandle() + ": " + e1.getMessage(), e1);
            throw new BadImageFormatException(image.getHandle(), e1);
        }
        finally
        {
            IOUtils.closeQuietly(is2);
        }
    }
}
