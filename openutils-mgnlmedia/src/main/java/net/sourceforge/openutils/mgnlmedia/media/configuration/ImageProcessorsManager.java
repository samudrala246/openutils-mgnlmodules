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

package net.sourceforge.openutils.mgnlmedia.media.configuration;

import info.magnolia.cms.beans.config.ObservedManager;
import info.magnolia.cms.core.Content;
import info.magnolia.cms.util.ContentUtil;
import info.magnolia.cms.util.FactoryUtil;
import info.magnolia.cms.util.NodeDataUtil;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.inject.Singleton;
import javax.jcr.RepositoryException;

import net.sourceforge.openutils.mgnlmedia.media.processors.FitInAndFillWithBandsImageResolutionProcessor;
import net.sourceforge.openutils.mgnlmedia.media.processors.FitInImageResolutionProcessor;
import net.sourceforge.openutils.mgnlmedia.media.processors.ImagePostProcessor;
import net.sourceforge.openutils.mgnlmedia.media.processors.ImageResolutionProcessor;
import net.sourceforge.openutils.mgnlmedia.media.processors.ResizeCropCenteredImageResolutionProcessor;
import net.sourceforge.openutils.mgnlmedia.media.processors.ResizeNoCropImageResolutionProcessor;

import org.apache.commons.lang.StringUtils;


/**
 * ObservedManager that manages the maps of processors that handle images resize and processors that do operation on
 * images after the resize. <br/>
 * default image resolution processors:
 * <table>
 * <tbody>
 * <tr>
 * <td>control chars</td>
 * <td>image resolution processor</td>
 * <td>description</td>
 * </tr>
 * <tr>
 * <td>b, o</td>
 * <td>{@link FitInAndFillWithBandsImageResolutionProcessor}</td>
 * <td>resize image to fit in required resolution and fills empty areas by color passed in parameters as "background"</td>
 * </tr>
 * <tr>
 * <td>l</td>
 * <td>{@link FitInImageResolutionProcessor}</td>
 * <td>resize image to fit in required resolution</td>
 * </tr>
 * <tr>
 * <td>x</td>
 * <td>{@link ResizeNoCropImageResolutionProcessor}</td>
 * <td>resize image to contain required resolution</td>
 * </tr>
 * <tr>
 * <td>no control char (default), c</td>
 * <td>{@link ResizeCropCenteredImageResolutionProcessor}</td>
 * <td>resize image to contain required resolution, and crop simmetric bands that outfits the required resolution rect</td>
 * </tr>
 * </tbody>
 * </table>
 * @author molaschi
 * @version $Id: $
 */
@Singleton
public class ImageProcessorsManager extends ObservedManager
{

    private static final String IMAGE_RESOLUTION_PROCESSORS_NAME = "image-resolution";

    private static final String IMAGE_POST_PROCESSORS_NAME = "image-post";

    private Map<String, ImageResolutionProcessor> imageResProcs = new HashMap<String, ImageResolutionProcessor>();

    private Map<String, ImagePostProcessor> imagePostProcs = new HashMap<String, ImagePostProcessor>();

    /**
     * Get singleton instance
     * @return singleton manager instance
     */
    public static ImageProcessorsManager getInstance()
    {
        return (ImageProcessorsManager) FactoryUtil.getSingleton(ImageProcessorsManager.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onClear()
    {
        imageResProcs.clear();
        imagePostProcs.clear();
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    protected void onRegister(Content parentNode)
    {
        for (Iterator iter = ContentUtil.getAllChildren(parentNode).iterator(); iter.hasNext();)
        {
            Content processorsNode = (Content) iter.next();

            for (Iterator iterProcessor = ContentUtil.getAllChildren(processorsNode).iterator(); iterProcessor
                .hasNext();)
            {
                Content node = (Content) iterProcessor.next();

                try
                {

                    if (IMAGE_POST_PROCESSORS_NAME.equals(processorsNode.getName()))
                    {
                        String classNameHandler = NodeDataUtil.getString(node, "class");
                        Class classHandler = Class.forName(classNameHandler);
                        if (!ImagePostProcessor.class.isAssignableFrom(classHandler))
                        {
                            log.error(
                                "Error getting post processor for {}: class {} not implements ImagePostProcessor",
                                node.getHandle(),
                                classHandler);
                            continue;
                        }

                        ImagePostProcessor imagePostProcessor = (ImagePostProcessor) classHandler.newInstance();
                        imagePostProcs.put(node.getName(), imagePostProcessor);
                    }

                    if (IMAGE_RESOLUTION_PROCESSORS_NAME.equals(processorsNode.getName()))
                    {
                        String controlChar = node.getName();

                        String classNameHandler = NodeDataUtil.getString(node, "class");
                        Class classHandler = Class.forName(classNameHandler);
                        if (!ImageResolutionProcessor.class.isAssignableFrom(classHandler))
                        {
                            log
                                .error(
                                    "Error getting resolution processor for {}: class {} not implements ImageResolutionProcessor",
                                    node.getHandle(),
                                    classHandler);
                            continue;
                        }

                        ImageResolutionProcessor imageResolutionProcessor = (ImageResolutionProcessor) classHandler
                            .newInstance();
                        if (!controlChar.equals("default"))
                        {
                            controlChar = controlChar.substring(0, 1);
                        }
                        imageResProcs.put(controlChar, imageResolutionProcessor);
                        try
                        {
                            if (node.hasNodeData("aliases"))
                            {
                                String[] aliases = StringUtils.split(NodeDataUtil.getString(node, "aliases"), ",");
                                for (String alias : aliases)
                                {
                                    imageResProcs.put(alias, imageResolutionProcessor);
                                }
                            }
                        }
                        catch (RepositoryException ex)
                        {
                            // go on
                        }
                    }
                }
                catch (InstantiationException ex)
                {
                    log.error("Error getting media type configuration for {}", node.getHandle(), ex);
                }
                catch (IllegalAccessException ex)
                {
                    log.error("Error getting media type configuration for {}", node.getHandle(), ex);
                }
                catch (ClassNotFoundException ex)
                {
                    log.error("Error getting media type configuration for {}", node.getHandle(), ex);
                }
                catch (RuntimeException ex)
                {
                    log.error("Error getting media type configuration for {}", node.getHandle(), ex);
                }
            }
        }
    }

    /**
     * Get image resolution processor for a given control char
     * @param controlChar resolution control char
     * @return image resolution processor for a given control char
     */
    public ImageResolutionProcessor getImageResolutionProcessor(char controlChar)
    {
        return imageResProcs.get(String.valueOf(controlChar));
    }

    /**
     * Check if control char is registered
     * @param controlChar control char to check
     * @return true if control char is registered
     */
    public boolean isValidControlChar(char controlChar)
    {
        return imageResProcs.keySet().contains(String.valueOf(controlChar));
    }

    /**
     * Get the default image resolution processor (by default config is
     * {@link ResizeCropCenteredImageResolutionProcessor})
     * @return the default image resolution processor
     */
    public ImageResolutionProcessor getDefaultImageResolutionProcessor()
    {
        return imageResProcs.get("default");
    }

    /**
     * Get image post processor by its name
     * @param name image post processor name
     * @return image post processor
     */
    public ImagePostProcessor getImagePostProcessor(String name)
    {
        return imagePostProcs.get(name);
    }

    /**
     * Get all image post processors
     * @return image post processors list
     */
    public Collection<ImagePostProcessor> getImagePostProcessorsList()
    {
        return imagePostProcs.values();
    }

    /**
     * Get the map of image post processors
     * @return image post processors map
     */
    public Map<String, ImagePostProcessor> getImagePostProcessorsMap()
    {
        return imagePostProcs;
    }
}
