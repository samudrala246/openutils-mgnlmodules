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

package net.sourceforge.openutils.mgnlmedia.media.configuration;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.inject.Singleton;

import net.sourceforge.openutils.mgnlmedia.media.processors.FitInAndFillWithBandsImageResolutionProcessor;
import net.sourceforge.openutils.mgnlmedia.media.processors.FitInImageResolutionProcessor;
import net.sourceforge.openutils.mgnlmedia.media.processors.ImagePostProcessor;
import net.sourceforge.openutils.mgnlmedia.media.processors.ImageResolutionProcessor;
import net.sourceforge.openutils.mgnlmedia.media.processors.ResizeCropCenteredImageResolutionProcessor;
import net.sourceforge.openutils.mgnlmedia.media.processors.ResizeNoCropImageResolutionProcessor;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


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
public class DefaultImageProcessorsManager implements ImageProcessorsManager
{

    private Map<String, ImageResolutionProcessor> resolutionprocessors = new LinkedHashMap<String, ImageResolutionProcessor>();

    private Map<String, ImagePostProcessor> postprocessors = new LinkedHashMap<String, ImagePostProcessor>();

    /**
     * Logger.
     */
    private Logger log = LoggerFactory.getLogger(DefaultImageProcessorsManager.class);

    /**
     * Get image resolution processor for a given control char
     * @param controlChar resolution control char
     * @return image resolution processor for a given control char
     */
    public ImageResolutionProcessor getImageResolutionProcessor(char controlChar)
    {
        String key = String.valueOf(controlChar);
        if (StringUtils.equals(key, "<"))
        {
            log.warn("Deprecated: please use \"l\" instead of \"<\" for FitInImageResolutionProcessor");
            key = "l";
        }

        return resolutionprocessors.get(key);
    }

    /**
     * Check if control char is registered
     * @param controlChar control char to check
     * @return true if control char is registered
     */
    public boolean isValidControlChar(char controlChar)
    {
        return resolutionprocessors.keySet().contains(String.valueOf(controlChar));
    }

    /**
     * Get the default image resolution processor (by default config is
     * {@link ResizeCropCenteredImageResolutionProcessor})
     * @return the default image resolution processor
     */
    public ImageResolutionProcessor getDefaultResolutionProcessor()
    {
        return resolutionprocessors.get("default");
    }

    /**
     * Get image post processor by its name
     * @param name image post processor name
     * @return image post processor
     */
    public ImagePostProcessor getImagePostProcessor(String name)
    {
        return postprocessors.get(name);
    }

    /**
     * Get the map of image post processors
     * @return image post processors map
     */
    public Map<String, ImagePostProcessor> getPostprocessors()
    {
        return postprocessors;
    }

    public void setPostprocessors(Map<String, ImagePostProcessor> postprocessors)
    {
        this.postprocessors = postprocessors;
    }

    /**
     * Returns the resolutionprocessors.
     * @return the resolutionprocessors
     */
    public Map<String, ImageResolutionProcessor> getResolutionprocessors()
    {
        return resolutionprocessors;
    }

    /**
     * Sets the resolutionprocessors.
     * @param resolutionprocessors the resolutionprocessors to set
     */
    public void setResolutionprocessors(Map<String, ImageResolutionProcessor> resolutionprocessors)
    {
        this.resolutionprocessors = resolutionprocessors;
    }
}
