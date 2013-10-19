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

import net.sourceforge.openutils.mgnlmedia.media.configuration.DefaultImageProcessorsManager;
import net.sourceforge.openutils.mgnlmedia.media.processors.FitInAndFillWithBandsImageResolutionProcessor;
import net.sourceforge.openutils.mgnlmedia.media.processors.FitInImageResolutionProcessor;
import net.sourceforge.openutils.mgnlmedia.media.processors.ImageResolutionProcessor;
import net.sourceforge.openutils.mgnlmedia.media.processors.ResizeCropCenteredImageResolutionProcessor;
import net.sourceforge.openutils.mgnlmedia.media.processors.ResizeNoCropImageResolutionProcessor;

import org.apache.commons.lang.ArrayUtils;


/**
 * @author molaschi
 * @version $Id: $
 */
public class ImageProcessorsManagerMock extends DefaultImageProcessorsManager
{

    private final char[] controlChars = new char[]{'o', 'b', 'l', '<', 'x' };

    /**
     * {@inheritDoc}
     */
    @Override
    public ImageResolutionProcessor getDefaultResolutionProcessor()
    {
        return new ResizeCropCenteredImageResolutionProcessor();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ImageResolutionProcessor getImageResolutionProcessor(char controlChar)
    {
        switch (controlChar)
        {
            case 'o' :
            case 'b' :
                return new FitInAndFillWithBandsImageResolutionProcessor();
            case 'l' :
            case '<' :
                return new FitInImageResolutionProcessor();
            case 'x' :
                return new ResizeNoCropImageResolutionProcessor();
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isValidControlChar(char controlChar)
    {
        return ArrayUtils.contains(controlChars, controlChar);
    }

}
