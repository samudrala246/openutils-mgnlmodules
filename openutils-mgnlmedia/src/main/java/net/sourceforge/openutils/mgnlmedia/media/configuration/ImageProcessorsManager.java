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

import java.util.Collection;
import java.util.Map;

import net.sourceforge.openutils.mgnlmedia.media.processors.ImagePostProcessor;
import net.sourceforge.openutils.mgnlmedia.media.processors.ImageResolutionProcessor;


/**
 * @author fgiust
 * @version $Id: $
 */
public interface ImageProcessorsManager
{

    ImageResolutionProcessor getImageResolutionProcessor(char controlChar);

    boolean isValidControlChar(char controlChar);

    ImageResolutionProcessor getDefaultImageResolutionProcessor();

    ImagePostProcessor getImagePostProcessor(String name);

    Collection<ImagePostProcessor> getImagePostProcessorsList();

    Map<String, ImagePostProcessor> getImagePostProcessorsMap();

}
