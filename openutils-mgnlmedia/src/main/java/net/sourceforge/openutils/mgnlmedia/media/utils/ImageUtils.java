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

import info.magnolia.cms.beans.runtime.FileProperties;
import info.magnolia.cms.core.MgnlNodeType;
import info.magnolia.context.Context;
import info.magnolia.context.MgnlContext;
import info.magnolia.context.SystemContext;
import info.magnolia.jcr.util.MetaDataUtil;
import info.magnolia.jcr.util.PropertyUtil;
import info.magnolia.objectfactory.Components;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.color.CMMException;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TimeZone;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.ImageOutputStream;
import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import net.sourceforge.openutils.mgnlmedia.media.configuration.ImageProcessorsManager;
import net.sourceforge.openutils.mgnlmedia.media.configuration.MediaConfigurationManager;
import net.sourceforge.openutils.mgnlmedia.media.configuration.MediaTypeConfiguration;
import net.sourceforge.openutils.mgnlmedia.media.lifecycle.MediaModule;
import net.sourceforge.openutils.mgnlmedia.media.processors.ImagePostProcessor;
import net.sourceforge.openutils.mgnlmedia.media.tags.el.MediaEl;
import net.sourceforge.openutils.mgnlmedia.media.types.impl.BaseTypeHandler;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.jackrabbit.value.BinaryValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Main utility class that works with images and media nodes
 * @author molaschi
 * @version $Id$
 */
public final class ImageUtils
{

    /**
     * Size for the "preview" resolution.
     */
    private static final String RESOLUTION_PREVIEW_SIZE = "l450x350";

    /**
     * Size for the "thumbnail" resolution.
     */
    private static final String RESOLUTION_THUMBNAIL_SIZE = "l100x100";

    /**
     * Name for the "preview" resolution.
     */
    private static final String RESOLUTION_PREVIEW = "preview";

    /**
     * Name for the "thumbnail" resolution.
     */
    private static final String RESOLUTION_THUMBNAIL = "thumbnail";

    /**
     * Logger.
     */
    private static Logger log = LoggerFactory.getLogger(ImageUtils.class);

    private static SimpleDateFormat sdf;

    /**
     * Nodedata name where resolution is saved
     */
    public static String RESOLUTION_PROPERTY = "resolution";

    private static final String[] extensions = new String[]{"jpg", "jpeg", "gif", "png", "ico" };

    private static int currentWorkingThreads = 0;

    static
    {
        sdf = new SimpleDateFormat();
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
    }

    private ImageUtils()
    {
    }

    private static int getType(ColorModel cm)
    {
        if (cm.getTransparency() == ColorModel.BITMASK)
        {
            if (cm.isAlphaPremultiplied())
            {
                return BufferedImage.TYPE_4BYTE_ABGR_PRE;
            }
            else
            {
                return BufferedImage.TYPE_4BYTE_ABGR;
            }
        }
        if (cm.getTransparency() == ColorModel.TRANSLUCENT)
        {
            if (cm.isAlphaPremultiplied())
            {
                return BufferedImage.TYPE_INT_ARGB_PRE;
            }
            else
            {
                return BufferedImage.TYPE_INT_ARGB;
            }
        }

        if (cm.getPixelSize() == 8)
        {
            return BufferedImage.TYPE_3BYTE_BGR;
        }
        return BufferedImage.TYPE_INT_RGB;
    }

    /**
     * Resize an image to x,y
     * @param original original image
     * @param x new width
     * @param y new height
     * @return resized image
     */
    public static BufferedImage resizeImage(BufferedImage original, int x, int y)
    {
        return resizeImage(original, x, y, false);
    }

    public static BufferedImage resizeImage(BufferedImage original, int x, int y, boolean skipRendering)
    {
        return resizeImage(original, x, y, x, y, null, skipRendering);
    }

    /**
     * Resize an image to x,y
     * @param original original image
     * @param x new width
     * @param y new height
     * @param canvasX canvas width
     * @param canvasY canvas height
     * @param background background color
     * @return resized image
     */
    public static BufferedImage resizeImage(BufferedImage original, int x, int y, int canvasX, int canvasY,
        Color background)
    {
        return resizeImage(original, x, y, canvasX, canvasY, background, false);
    }

    public static BufferedImage resizeImage(BufferedImage original, int x, int y, int canvasX, int canvasY,
        Color background, boolean skipRendering)
    {
        if (x <= 0)
        {
            x = 1;
            // throw new IllegalArgumentException("x=" + x + " (must be >0)");
        }
        if (y <= 0)
        {
            y = 1;
            // throw new IllegalArgumentException("y=" + y + " (must be >0)");
        }
        if (canvasX <= 0)
        {
            canvasX = 1;
            // throw new IllegalArgumentException("canvasX=" + canvasX + " (must be >0)");
        }
        if (canvasY <= 0)
        {
            canvasY = 1;
            // throw new IllegalArgumentException("canvasY=" + canvasY + " (must be >0)");
        }

        BufferedImage resizedImage;
        try
        {
            resizedImage = new BufferedImage(canvasX, canvasY, getType(original.getColorModel()));
        }
        catch (NegativeArraySizeException e)
        {
            throw new RuntimeException("NegativeArraySizeException caught when resizing image to ["
                + canvasX
                + ", "
                + canvasY
                + "]");
        }

        if (!skipRendering)
        {
            Graphics2D graphics2D = resizedImage.createGraphics();

            graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            graphics2D.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            if (canvasX > x || canvasY > y)
            {
                graphics2D.clearRect(0, 0, canvasX, canvasY);

                if (background == null && original.getColorModel().getTransparency() == Transparency.OPAQUE)
                {
                    background = Color.WHITE;
                }
                // fill bands
                if (background != null)
                {
                    graphics2D.setColor(background);

                    if (canvasX > x)
                    {
                        graphics2D.fillRect(0, 0, (canvasX - x) / 2, canvasY);
                        graphics2D.fillRect(x + (canvasX - x) / 2, 0, canvasX, canvasY);
                    }
                    if (canvasY > y)
                    {
                        graphics2D.fillRect(0, 0, canvasX, (canvasY - y) / 2);
                        graphics2D.fillRect(0, y + (canvasY - y) / 2, canvasX, canvasY);
                    }
                }
            }

            graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);

            AffineTransform at = new AffineTransform();
            double delta = ((double) x) / original.getWidth();
            if (x > original.getWidth())
            {
                at.scale(delta, delta);
                at.translate((canvasX - x) / (2 * delta), (canvasY - y) / (2 * delta));
            }
            else if (x < original.getWidth())
            {
                original = getScaledInstance(original, x, y, RenderingHints.VALUE_INTERPOLATION_BILINEAR, true, false);
                at.translate((canvasX - x) / 2.0, (canvasY - y) / 2.0);
            }

            graphics2D.drawImage(original, at, null);
            graphics2D.dispose();
        }

        return resizedImage;
    }

    /**
     * Convenience method that returns a scaled instance of the provided {@code BufferedImage}.
     * @param img the original image to be scaled
     * @param targetWidth the desired width of the scaled instance, in pixels
     * @param targetHeight the desired height of the scaled instance, in pixels
     * @param hint one of the rendering hints that corresponds to {@code RenderingHints.KEY_INTERPOLATION} (e.g.
     * {@code RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR}, {@code RenderingHints.VALUE_INTERPOLATION_BILINEAR},
     * {@code RenderingHints.VALUE_INTERPOLATION_BICUBIC})
     * @param higherQuality if true, this method will use a multi-step scaling technique that provides higher quality
     * than the usual one-step technique (only useful in downscaling cases, where {@code targetWidth} or
     * {@code targetHeight} is smaller than the original dimensions, and generally only when the {@code BILINEAR} hint
     * is specified)
     * @return a scaled version of the original {@code BufferedImage}
     */
    public static BufferedImage getScaledInstance(BufferedImage img, int targetWidth, int targetHeight, Object hint,
        boolean higherQuality)
    {
        return getScaledInstance(img, targetWidth, targetHeight, hint, higherQuality, false);
    }

    public static BufferedImage getScaledInstance(BufferedImage img, int targetWidth, int targetHeight, Object hint,
        boolean higherQuality, boolean skipRendering)
    {
        int type = (img.getTransparency() == Transparency.OPAQUE)
            ? BufferedImage.TYPE_INT_RGB
            : BufferedImage.TYPE_INT_ARGB;
        BufferedImage ret = img;
        int w, h;
        if (higherQuality)
        {
            // Use multi-step technique: start with original size, then
            // scale down in multiple passes with drawImage()
            // until the target size is reached
            w = img.getWidth();
            h = img.getHeight();
        }
        else
        {
            // Use one-step technique: scale directly from original
            // size to target size with a single drawImage() call
            w = targetWidth;
            h = targetHeight;
        }

        do
        {
            if (higherQuality && w > targetWidth)
            {
                w /= 2;

                if (w < targetWidth)
                {
                    w = targetWidth;
                }
            }

            if (higherQuality && h > targetHeight)
            {
                h /= 2;
                if (h < targetHeight)
                {
                    h = targetHeight;
                }
            }

            BufferedImage tmp = new BufferedImage(w, h, type);
            if (!skipRendering)
            {
                Graphics2D g2 = tmp.createGraphics();
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, hint);
                g2.drawImage(ret, 0, 0, w, h, null);
                g2.dispose();
            }

            ret = tmp;
        }
        while (w != targetWidth || h != targetHeight);

        return ret;
    }

    /**
     * Crop an image from left, top for width, height
     * @param original original image
     * @param left start crop point left
     * @param top start crop point top
     * @param width crop width
     * @param height crop height
     * @return resized image
     */
    public static BufferedImage cropImage(BufferedImage original, int left, int top, int width, int height)
    {
        return cropImage(original, left, top, width, height, false);
    }

    public static BufferedImage cropImage(BufferedImage original, int left, int top, int width, int height,
        boolean skipRendering)
    {
        BufferedImage resizedImage = new BufferedImage(width, height, getType(original.getColorModel()));
        if (!skipRendering)
        {
            Graphics2D graphics2D = resizedImage.createGraphics();
            graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            graphics2D.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_DEFAULT);

            AffineTransform at = new AffineTransform();
            at.translate(-left, -top);
            graphics2D.drawImage(original, at, null);
            graphics2D.dispose();
        }

        return resizedImage;
    }

    /**
     * Create rounded corners on image
     * @param original original image
     * @param backgroundColor optional background color
     * @param radius corners radius
     * @return image with rounded corners
     */
    public static BufferedImage addRoundedCorners(BufferedImage original, Color backgroundColor, int radius)
    {
        return addRoundedCorners(original, backgroundColor, radius, false);
    }

    public static BufferedImage addRoundedCorners(BufferedImage original, Color backgroundColor, int radius,
        boolean skipRendering)
    {
        int originalImageType = getType(original.getColorModel());
        int roundedCornersImageType = BufferedImage.TYPE_4BYTE_ABGR;

        if (originalImageType != BufferedImage.TYPE_4BYTE_ABGR)
        {
            if (originalImageType != BufferedImage.TYPE_4BYTE_ABGR_PRE)
            {
                // the image has not alpha channel so fill background
                if (backgroundColor == null)
                {
                    backgroundColor = Color.WHITE;
                }
            }
            else
            {
                roundedCornersImageType = BufferedImage.TYPE_4BYTE_ABGR_PRE;
            }
        }

        // use a 4 byte image type to create antialiased rounded corners
        BufferedImage roundedImage;
        try
        {
            roundedImage = new BufferedImage(original.getWidth(), original.getHeight(), roundedCornersImageType);
        }
        catch (NegativeArraySizeException e)
        {
            throw new RuntimeException("NegativeArraySizeException caught when adding rounded corners");
        }

        if (!skipRendering)
        {
            Graphics2D roundedGraphics2D = roundedImage.createGraphics();
            roundedGraphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            roundedGraphics2D.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

            roundedGraphics2D.setColor(Color.WHITE);
            roundedGraphics2D.fillRoundRect(0, 0, original.getWidth(), original.getHeight(), radius, radius);
            roundedGraphics2D.setComposite(AlphaComposite.SrcIn);
            roundedGraphics2D.drawImage(original, 0, 0, original.getWidth(), original.getHeight(), null);

            if (backgroundColor != null)
            {

                BufferedImage destImage;
                try
                {
                    destImage = new BufferedImage(original.getWidth(), original.getHeight(), originalImageType);
                }
                catch (NegativeArraySizeException e)
                {
                    throw new RuntimeException("NegativeArraySizeException caught when resizing image]");
                }
                // draw new image
                Graphics2D destImageGraphics2D = destImage.createGraphics();
                destImageGraphics2D
                    .setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                destImageGraphics2D.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

                destImageGraphics2D.setBackground(backgroundColor);
                destImageGraphics2D.clearRect(0, 0, original.getWidth(), original.getHeight());
                // destImageGraphics2D.setComposite(AlphaComposite.DstIn);
                destImageGraphics2D.drawImage(roundedImage, 0, 0, original.getWidth(), original.getHeight(), null);
                return destImage;
            }
        }

        return roundedImage;
    }

    /**
     * Save a resolution for an image to a node (in resolutions/res-[width]x[height]/data.jpg)
     * @param image image to save
     * @param saveTo node to save to
     * @param extension extension
     * @param quality image quality
     * @param forceProgressive true to force progressive mode
     * @throws RepositoryException exception in jcr operations
     * @throws IOException exception converting image to jpg
     */
    public static Node saveResolution(BufferedImage image, Node saveTo, String extension, float quality,
        boolean forceProgressive) throws RepositoryException, IOException
    {
        return saveResolution(image, saveTo, null, extension, quality, forceProgressive);
    }

    /**
     * Save a resolution for an image to a node (in resolutions/[name]/data.jpg)
     * @param image image to save
     * @param saveTo node to save to
     * @param name name for this resolution
     * @param extension extension
     * @param quality image quality
     * @param forceProgressive true to force progressive mode
     * @throws RepositoryException exception in jcr operations
     * @throws IOException exception converting image to jpg
     */
    public static Node saveResolution(final BufferedImage image, final Node saveTo, final String name,
        final String extension, final float quality, final boolean forceProgressive) throws RepositoryException,
        IOException
    {

        Node resolutions = getResolutionsNode(saveTo);
        if (resolutions == null)
        {
            resolutions = saveTo.addNode("resolutions", MediaConfigurationManager.NT_RESOLUTIONS);
            MetaDataUtil.getMetaData(saveTo).setModificationDate();
            saveTo.getSession().save();
        }

        String resolution = name;
        if (resolution == null)
        {
            resolution = "res-" + image.getWidth() + "x" + image.getHeight();
        }

        final String resolutionNodeName = getResolutionPath(resolution);
        final Node resolutionsFinal = resolutions;

        Node resolutionsJcrNode = resolutions;

        Node ndtemp;
        boolean existing = false;
        if (resolutionsFinal.hasNode(resolutionNodeName))
        {
            ndtemp = resolutionsFinal.getNode(resolutionNodeName);
            existing = true;
        }
        else
        {
            // don't remove deprecated method call, needed for magnolia 4.0 compatibility
            ndtemp = resolutionsFinal.addNode(resolutionNodeName, MgnlNodeType.NT_RESOURCE); // NT_RESOURCE??????
        }
        log.debug("setting value to {}", ndtemp.getPath());

        final Node nd = ndtemp;
        final PipedInputStream stream = new PipedInputStream();
        PipedOutputStream outputstream = null;
        Thread t = null;
        long count = 0;

        try
        {
            outputstream = new PipedOutputStream(stream);
            t = new Thread(new Runnable()
            {

                /**
                 * {@inheritDoc}
                 */
                public void run()
                {
                    try
                    {
                        // getProperty(MgnlNodeType.JCR_DATA).getValue().getBinary().getStream();
                        nd.setProperty(MgnlNodeType.JCR_DATA, new BinaryValue(stream));
                    }
                    catch (RepositoryException e)
                    {
                        log.error(e.getMessage(), e);
                    }
                }

            });
            t.start();

            count = getStream(image, extension, quality, forceProgressive, outputstream);
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        IOUtils.closeQuietly(outputstream);
        try
        {
            t.join();
        }
        catch (InterruptedException e)
        {
            log.warn(e.getMessage(), e);
        }

        IOUtils.closeQuietly(stream);

        String mimetype = "image/" + extension;
        if ("jpg".equals(extension))
        {
            mimetype = "image/jpeg";
        }
        // NodeUtilsExt.setAttribute(nd, nd.getName(), ImageUtils.RESOLUTION_PROPERTY, resolutionNodeName);
        nd.setProperty(ImageUtils.RESOLUTION_PROPERTY, resolutionNodeName);
        nd.setProperty(FileProperties.PROPERTY_EXTENSION, extension);
        nd.setProperty(FileProperties.PROPERTY_FILENAME, saveTo.getName());
        nd.setProperty(FileProperties.PROPERTY_CONTENTTYPE, mimetype);
        nd.setProperty(FileProperties.PROPERTY_LASTMODIFIED, GregorianCalendar.getInstance(TimeZone.getDefault()));
        nd.setProperty(FileProperties.PROPERTY_WIDTH, "" + image.getWidth());
        nd.setProperty(FileProperties.PROPERTY_HEIGHT, "" + image.getHeight());

        nd.setProperty(FileProperties.PROPERTY_SIZE, "" + count);

        if (existing)
        {
            nd.getSession().save();
        }
        else
        {
            resolutionsFinal.getSession().save();
        }
        return nd;
    }

    /**
     * Get resolution nodedata name for a given resolution string
     * @param resolution resolution string
     * @return resolution nodedata name
     */
    public static String getResolutionPath(String resolution)
    {
        if (resolution.indexOf(';') > 0)
        {
            return StringUtils.substringBefore(resolution, ";")
                + "-p"
                + StringUtils.substringAfter(resolution, ";").hashCode();
        }
        return resolution;
    }

    /**
     * Get an inputstream for an image and the target file extension
     * @param image image to get the inputstream from
     * @param extension target file extension
     * @param quality image quality
     * @param forceProgressive true if image has to be saved as progressive mode
     * @return byte count
     * @throws IOException
     */
    public static long getStream(BufferedImage image, String extension, float quality, boolean forceProgressive,
        OutputStream outputstream) throws IOException
    {

        CountBytesBufferedOutputStream out = new CountBytesBufferedOutputStream(outputstream);

        try
        {
            if (extension.equals("jpg"))
            {
                JPEGImageWriteParam iwparam = new JPEGImageWriteParam(null);

                if (quality != 1.0f)
                {
                    iwparam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                    iwparam.setCompressionType("JPEG");
                    iwparam.setCompressionQuality(quality);
                }
                else
                {
                    iwparam.setCompressionMode(ImageWriteParam.MODE_COPY_FROM_METADATA);
                }

                ImageWriter iw = ImageIO.getImageWritersByFormatName("jpg").next();
                ImageOutputStream ios = ImageIO.createImageOutputStream(out);
                iw.setOutput(ios);
                iw.write(null, new IIOImage(image, null, null), iwparam);
                iw.dispose();
                ios.close();

                image.flush();
            }
            else
            {

                Iterator<ImageWriter> writers;
                ImageOutputStream imageOutputStream;
                ImageWriteParam params;
                ImageWriter imageWriter;

                String outputextension = extension;
                if (StringUtils.equals(extension, "ico") || StringUtils.equals(extension, "gif"))
                {
                    // gif and ico are remapped to png
                    outputextension = "png";
                }

                // don't use "ico" as output format
                writers = ImageIO.getImageWritersBySuffix(outputextension);

                if (writers != null && writers.hasNext())
                {
                    // Fetch the first writer in the list
                    imageWriter = writers.next();

                    // Specify the parameters according to those the output file will be written

                    // Get Default parameters
                    params = imageWriter.getDefaultWriteParam();

                    try
                    {

                        String[] compressionTypes = params.getCompressionTypes();

                        if (compressionTypes != null && compressionTypes.length > 0)
                        {
                            // Define compression mode
                            params.setCompressionMode(javax.imageio.ImageWriteParam.MODE_EXPLICIT);

                            params.setCompressionType(compressionTypes[0]);

                            // Define compression quality
                            params.setCompressionQuality(quality);
                        }
                        else
                        {
                            params.setCompressionMode(javax.imageio.ImageWriteParam.MODE_COPY_FROM_METADATA);
                        }

                        // Define progressive mode
                        if (forceProgressive)
                        {
                            params.setProgressiveMode(javax.imageio.ImageWriteParam.MODE_DEFAULT);
                        }
                        else
                        {
                            params.setProgressiveMode(javax.imageio.ImageWriteParam.MODE_DISABLED);
                        }
                    }
                    catch (UnsupportedOperationException e)
                    {
                        // go on
                    }

                    // Define destination type - used the ColorModel and SampleModel of the Input Image
                    params.setDestinationType(new ImageTypeSpecifier(image.getColorModel(), image.getSampleModel()));

                    imageOutputStream = ImageIO.createImageOutputStream(out);
                    imageWriter.setOutput(imageOutputStream);

                    IIOImage iioimage = new IIOImage(image, null, null);

                    try
                    {
                        // Write the changed Image
                        imageWriter.write(null, iioimage, params);
                    }
                    catch (NullPointerException e)
                    {
                        // http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6287936
                        // GIF writer is buggy for totally transparent gifs

                        // workaround: draw a point!
                        image.createGraphics().drawRect(0, 0, 1, 1);
                        imageWriter.write(null, iioimage, params);

                    }
                    finally
                    {
                        // Close the streams
                        imageOutputStream.close();
                        imageWriter.dispose();
                    }
                }
                else
                {
                    ImageIO.write(image, outputextension, out);
                }
            }
        }
        catch (IOException ex)
        {
            log.error("Error writing image to buffer", ex);
            throw ex;
        }

        return out.count;
    }

    /**
     * Check if the resolution for a media is already present. Otherwise create it
     * @param media media to check the resolutoin on
     * @param resolutionTarget target resolution
     * @return false if resolution doesn't exist and there is a problem in generate it; true otherwise
     */
    public static boolean checkOrCreateResolution(final Node media, final String resolutionTarget)
    {

        return checkOrCreateResolution(media, resolutionTarget, BaseTypeHandler.ORGINAL_NODEDATA_NAME);
    }

    /**
     * Check if the resolution for a media is already present. Otherwise create it
     * @param media media to check the resolution on
     * @param resolutionTarget target resolution
     * @param nodeDataName nodedata where the image to resize is stored
     * @return false if resolution doesn't exist and there is a problem in generate it; true otherwise
     */
    public static boolean checkOrCreateResolution(final Node media, final String resolutionTarget, String nodeDataName)
    {
        return checkOrCreateResolution(media, resolutionTarget, nodeDataName, false);
    }

    private static boolean checkResolution(final Node media, final String resolutionTarget, final boolean lazy)
    {
        Node resolutions = getResolutionsNode(media);

        String resolution = resolutionTarget;

        if (!RESOLUTION_THUMBNAIL.equals(resolutionTarget) && !RESOLUTION_PREVIEW.equals(resolutionTarget))
        {
            resolution = "res-" + resolutionTarget;
        }
        try
        {
            if (resolutions != null && resolutions.hasProperty(getResolutionPath(resolution)))
            {
                if (lazy)
                {
                    return true;
                }
                else if (!StringUtils.isEmpty(PropertyUtil.getString(
                    resolutions.getNode(getResolutionPath(resolution)),
                    "resolutionNotYetCreated")))
                {

                    // continue: replace the fake image with the actual one
                }
                else
                {
                    return true;
                }
            }
        }
        catch (RepositoryException e1)
        {
            // go on with res calculation
        }

        return false;
    }

    public static boolean checkOrCreateResolution(final Node media, final String resolutionTarget, String nodeDataName,
        final boolean lazy)
    {
        if (checkResolution(media, resolutionTarget, lazy))
        {
            return true;
        }

        if (nodeDataName == null)
        {

            MediaTypeConfiguration mtc = Components
                .getComponent(MediaConfigurationManager.class)
                .getMediaTypeConfigurationFromMedia(media);
            if (mtc == null)
            {
                nodeDataName = BaseTypeHandler.ORGINAL_NODEDATA_NAME;
            }
            else
            {
                nodeDataName = mtc.getHandler().getPreviewImageNodeDataName();
            }
        }

        try
        {
            // original is a node, not a property!
            if (!media.hasNode(nodeDataName) && !media.hasProperty(nodeDataName))
            {
                return false;
            }
        }
        catch (RepositoryException e2)
        {
            log.warn(e2.getMessage(), e2);
        }

        final String originalNodeDataName = nodeDataName;

        try
        {
            ImageUtils.doInSystemContext(new MgnlContext.VoidOp()
            {

                /**
                 * {@inheritDoc}
                 */
                @Override
                public void doExec()
                {
                    long timestart = System.currentTimeMillis();
                    Node node;
                    try
                    {
                        Session hm = MgnlContext.getJCRSession(MediaModule.REPO);
                        String resolutionstring = resolutionTarget;

                        if (RESOLUTION_THUMBNAIL.equals(resolutionstring))
                        {
                            resolutionstring = RESOLUTION_THUMBNAIL_SIZE;
                        }
                        if (RESOLUTION_PREVIEW.equals(resolutionstring))
                        {
                            resolutionstring = RESOLUTION_PREVIEW_SIZE;
                        }

                        String resolutioNodeName = "res-" + resolutionstring;
                        if (RESOLUTION_THUMBNAIL.equals(resolutionTarget)
                            || RESOLUTION_PREVIEW.equals(resolutionTarget))
                        {
                            resolutioNodeName = resolutionTarget;
                        }

                        node = hm.getNode(media.getPath());

                        Node image = node.getNode(originalNodeDataName);

                        Property jcrData = image.getProperty(MgnlNodeType.JCR_DATA);
                        if (image == null || jcrData.getLength() == 0)
                        {
                            throw new ZeroSizeImageException(image.getPath());
                        }

                        String outputextension = PropertyUtil.getString(image, FileProperties.PROPERTY_EXTENSION);
                        if (!Arrays.asList(extensions).contains(outputextension))
                        {
                            outputextension = "jpg";
                        }

                        BufferedImage original = null;
                        BufferedImage img = null;
                        Map<String, String> params = parseParameters(resolutionstring);

                        if (lazy)
                        {
                            params.put("skipRendering", "true");
                        }

                        synchronized (MediaEl.module().getLocks().nextLock())
                        {
                            // Check again for resolution, maybe it has been created by an other thread
                            if (checkResolution(node, resolutionTarget, lazy))
                            {
                                log.debug("Resolution {} for {} already generated", new Object[]{
                                    resolutioNodeName,
                                    node.getPath() });
                                return;
                            }
                            currentWorkingThreads++;
                            if (log.isDebugEnabled())
                            {
                                log.debug("Current working resizing thread: {}", currentWorkingThreads);
                            }
                            original = createBufferedImage(image);

                            try
                            {
                                img = ImageUtils.getImageForResolution(original, resolutionstring, params);
                            }
                            catch (IllegalArgumentException e)
                            {
                                throw new RuntimeException(e);
                            }
                            finally
                            {
                                currentWorkingThreads--;
                            }
                        }
                        float quality = 0.8F;
                        if (StringUtils.isNotEmpty(params.get("quality")))
                        {
                            try
                            {
                                quality = NumberUtils.toFloat(params.get("quality"));
                                if (quality > 1.0F)
                                {
                                    quality = 1.0F;
                                }
                            }
                            catch (NumberFormatException ex)
                            {
                                log.error("quality parameter must be a float number but was {}", params.get("quality"));
                            }
                        }
                        boolean forceProgressive = false;
                        if (StringUtils.isNotEmpty(params.get("progressive")))
                        {
                            forceProgressive = true;
                        }

                        Node nd = ImageUtils.saveResolution(
                            img,
                            node,
                            resolutioNodeName,
                            outputextension,
                            quality,
                            forceProgressive);
                        if (lazy)
                        {

                            nd.setProperty(
                                "resolutionNotYetCreated",
                                StringUtils.removeStart(resolutioNodeName, "res-"));
                            nd.getParent().getSession().save();
                        }
                        hm.save();
                        log.info(
                            "Generated {} for {} in {} milliseconds",
                            new Object[]{resolutioNodeName, node.getPath(), System.currentTimeMillis() - timestart });
                    }
                    catch (RepositoryException e)
                    {
                        throw new RuntimeException(e);
                    }
                    catch (IOException e)
                    {
                        throw new RuntimeException(e);
                    }

                }

            });
        }
        catch (BadImageFormatException e)
        {
            try
            {
                if (e.getCause() != null)
                {
                    log.warn(e.getMessage(), e.getCause());
                }
                else
                {
                    log.warn("Unable to extract a valid image from " + media.getPath() + " (no message)");
                }

                media.setProperty("bad_image_marker", media.getProperty("bad_image_marker").getLong() + 1);
                media.getSession().save();
            }
            catch (RepositoryException e1)
            {
                // ignore
            }
            return false;
        }
        catch (ZeroSizeImageException ex)
        {
            log.error(ex.getMessage());
            return false;
        }
        catch (RuntimeException ex)
        {
            try
            {
                log.error(ClassUtils.getShortClassName(ex.getClass())
                    + " checking resolution for "
                    + media.getPath()
                    + ": "
                    + ex.getMessage(), ex);
            }
            catch (RepositoryException e1)
            {
                // ignore
            }
            return false;
        }

        return true;
    }

    /**
     * Returns the "resolutions" node, checking for existence
     * @param media
     * @return
     */
    protected static Node getResolutionsNode(final Node media)
    {
        Node resolutions = null;

        try
        {
            if (media.hasNode("resolutions"))
            {
                resolutions = media.getNode("resolutions");
            }
        }
        catch (RepositoryException e)
        {
            // ignore, try to create it
        }
        return resolutions;
    }

    private static Map<String, String> parseParameters(String resolution)
    {

        Map<String, String> params = new HashMap<String, String>();
        if (StringUtils.contains(resolution, ";"))
        {
            String parameters = StringUtils.substringAfter(resolution, ";");
            resolution = StringUtils.substringBefore(resolution, ";");

            String tokens[] = StringUtils.split(parameters, ',');
            for (String token : tokens)
            {
                if (token.indexOf("=") > 0)
                {
                    String[] keyvalue = StringUtils.split(token, '=');
                    params.put(keyvalue[0], keyvalue[1]);
                }
                else
                {
                    params.put(token, "true");
                }
            }
        }
        return params;
    }

    /**
     * Get image for a resolution
     * @param original original image
     * @param resolution resolution
     * @param params parameters
     * @return new image
     */
    public static BufferedImage getImageForResolution(BufferedImage original, String resolution,
        Map<String, String> params)
    {

        if (original == null)
        {
            throw new IllegalArgumentException("input image is null");
        }
        if (resolution == null || resolution.length() < 1)
        {
            throw new IllegalArgumentException("Invalid resolution: " + resolution);
        }

        BufferedImage img = null;

        resolution = StringUtils.lowerCase(resolution);

        char controlChar = resolution.charAt(0);

        Point size = parseForSize(resolution);

        if (Components.getComponent(ImageProcessorsManager.class).isValidControlChar(controlChar))
        {
            img = Components
                .getComponent(ImageProcessorsManager.class)
                .getImageResolutionProcessor(controlChar)
                .getImageForResolution(original, size.x, size.y, params);
        }
        else if (controlChar < '0' || controlChar > '9')
        {
            throw new IllegalArgumentException("Invalid control char: " + controlChar);
        }
        else
        {
            img = Components
                .getComponent(ImageProcessorsManager.class)
                .getDefaultImageResolutionProcessor()
                .getImageForResolution(original, size.x, size.y, params);
        }

        for (ImagePostProcessor ipp : Components
            .getComponent(ImageProcessorsManager.class)
            .getImagePostProcessorsList())
        {
            img = ipp.processImage(img, size.x, size.y, params);
        }

        return img;
    }

    /**
     * Get file extension for a resolution stored in a media node
     * @param media media
     * @param resolution resolution
     * @return file extension for a resolution stored in a media node
     */
    public static String getExtension(Node media, String resolution)
    {
        try
        {
            Node resolutions = media.getNode("resolutions");
            Node res = resolutions.getNode(resolution);
            return PropertyUtil.getString(res, FileProperties.PROPERTY_EXTENSION);
        }
        catch (RepositoryException ex)
        {
            return "jpg";
        }
    }

    /**
     * Create a buffered image from the binary data stored in nodedata
     * @param image nodedata
     * @return buffered image from the binary data stored in nodedata
     */
    public static BufferedImage createBufferedImage(Node image)
    {
        InputStream is = null;

        try
        {
            is = image.getProperty(MgnlNodeType.JCR_DATA).getValue().getBinary().getStream();

            String ext = PropertyUtil.getString(image, FileProperties.EXTENSION);

            log.debug("processing {}, extension {}", image.getPath(), ext);

            if (StringUtils.equalsIgnoreCase(ext, "ico") || StringUtils.equalsIgnoreCase(ext, "bmp"))
            {
                BufferedImage buffered = IcoUtils.createBufferedImage(image);
                if (buffered != null)
                {
                    return buffered;
                }
            }

            BufferedImage result = ImageIO.read(is);

            // yes, ImageIO can return null without throwing any exception
            if (result == null)
            {
                try
                {
                    // throw the original exception back
                    throw new BadImageFormatException("Unable to handle " + image.getPath() + " (no message)");
                }
                catch (RepositoryException re)
                {
                    // do nothing
                }
            }

            return result;

        }
        catch (IOException e)
        {
            // CMYK jpeg can't be read be ImageIO
            // Caused by: javax.imageio.IIOException: Unsupported Image Type
            BufferedImage result = JpegUtils.processNonStandardImage(image);

            if (result == null)
            {
                try
                {
                    // throw the original exception back
                    throw new BadImageFormatException("Unable to handle " + image.getPath(), e);
                }
                catch (RepositoryException re)
                {
                    // do nothing
                }
            }
            return result;

        }
        catch (CMMException e)
        {
            // CMMException is thrown for non-standard jpegs?
            // Invalid image format
            // java.awt.color.CMMException: Invalid image format
            // at sun.awt.color.CMM.checkStatus(CMM.java:131)
            // at sun.awt.color.ICC_Transform.<init>(ICC_Transform.java:89)
            // at java.awt.image.ColorConvertOp.filter(ColorConvertOp.java:516)
            // at com.sun.imageio.plugins.jpeg.JPEGImageReader.acceptPixels(JPEGImageReader.java:1169)
            BufferedImage result = JpegUtils.processNonStandardImage(image);

            if (result == null)
            {

                try
                {
                    // throw the original exception back
                    throw new BadImageFormatException("Unable to handle " + image.getPath(), e);
                }
                catch (RepositoryException re)
                {
                    // do nothing
                }
            }
            return result;

        }
        catch (IllegalArgumentException e)
        {
            // java.lang.IllegalArgumentException: Numbers of source Raster bands and source color space components do
            // not match
            // at java.awt.image.ColorConvertOp.filter(ColorConvertOp.java:460)
            // at com.sun.imageio.plugins.jpeg.JPEGImageReader.acceptPixels(JPEGImageReader.java:1169)
            // at com.sun.imageio.plugins.jpeg.JPEGImageReader.readImage(Native Method)
            // at com.sun.imageio.plugins.jpeg.JPEGImageReader.readInternal(JPEGImageReader.java:1137)
            // at com.sun.imageio.plugins.jpeg.JPEGImageReader.read(JPEGImageReader.java:948)
            BufferedImage result = JpegUtils.processNonStandardImage(image);

            if (result == null)
            {
                try
                {
                    // throw the original exception back
                    throw new BadImageFormatException("Unable to handle " + image.getPath(), e);
                }
                catch (RepositoryException re)
                {
                    // do nothing
                }

            }
            return result;

        }
        catch (RepositoryException re)
        {
            BufferedImage result = JpegUtils.processNonStandardImage(image);

            if (result == null)
            {
                try
                {
                    // throw the original exception back
                    throw new BadImageFormatException("Unable to handle " + image.getPath(), re);
                }
                catch (RepositoryException e)
                {
                    // do nothing
                }

            }
            return result;
        }

        finally
        {
            IOUtils.closeQuietly(is);
        }
    }

    private static String normalizeResolutionString(String res)
    {

        String resolution = StringUtils.lowerCase(res);

        if (RESOLUTION_THUMBNAIL.equals(resolution))
        {
            return RESOLUTION_THUMBNAIL_SIZE;
        }
        if (RESOLUTION_PREVIEW.equals(resolution))
        {
            return RESOLUTION_PREVIEW_SIZE;
        }

        return resolution;
    }

    /**
     * Parse resolution string for required sizesuper.read(b);
     * @param res resolution string
     * @return required size parsed from given resolution string
     */
    public static java.awt.Point parseForSize(String res)
    {
        Point size = new Point();
        String resolution = normalizeResolutionString(res);

        if (StringUtils.contains(resolution, ";"))
        {
            resolution = StringUtils.substringBefore(resolution, ";");
        }

        if (Components.getComponent(ImageProcessorsManager.class).isValidControlChar(resolution.charAt(0))
            || resolution.charAt(0) < '0'
            || resolution.charAt(0) > '9')
        {
            resolution = resolution.substring(1);
        }

        String[] resXY = StringUtils.split(resolution, "x");

        size.x = NumberUtils.toInt(resXY[0]);
        size.y = NumberUtils.toInt(resXY[1]);
        return size;
    }

    private static void doInSystemContext(info.magnolia.context.MgnlContext.Op op)
    {
        final Context originalCtx = MgnlContext.hasInstance() ? MgnlContext.getInstance() : null;
        try
        {
            MgnlContext.setInstance(Components.getComponent(SystemContext.class));
            try
            {
                op.exec();
            }
            catch (Throwable e)
            {
                throw new RuntimeException(e);
            }
        }
        finally
        {
            MgnlContext.setInstance(originalCtx);
        }
    }

    static class CountBytesBufferedOutputStream extends BufferedOutputStream
    {

        private int count = 0;

        /**
         * @param in
         * @param size
         */
        public CountBytesBufferedOutputStream(OutputStream out, int size)
        {
            super(out, size);
            count = 0;
        }

        /**
         * @param in
         */
        public CountBytesBufferedOutputStream(OutputStream in)
        {
            super(in);
            count = 0;
        }

        /**
         * Returns the count.
         * @return the count
         */
        public int getCount()
        {
            return count;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public synchronized void write(byte[] b, int off, int len) throws IOException
        {
            count += len;
            super.write(b, off, len);
        }

    }
}