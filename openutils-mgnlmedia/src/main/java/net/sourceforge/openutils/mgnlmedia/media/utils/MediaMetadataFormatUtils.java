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

import org.apache.commons.lang.time.DurationFormatUtils;


/**
 * @author fgiust
 * @version $Id$
 */
public class MediaMetadataFormatUtils
{

    /**
     * Format a size
     * @param size
     * @return
     */
    public static String formatSize(long size)
    {
        String unit = "bytes";
        String sizeStr;
        if (size >= 1000)
        {
            size = size / 1024;
            unit = "KB";
            if (size >= 1000)
            {
                size = size / 1024;
                unit = "MB";
            }
            sizeStr = Double.toString(size);
            sizeStr = sizeStr.substring(0, sizeStr.indexOf(".") + 2); //$NON-NLS-1$
        }
        else
        {
            sizeStr = Double.toString(size);
            sizeStr = sizeStr.substring(0, sizeStr.indexOf(".")); //$NON-NLS-1$
        }
        return sizeStr + " " + unit;
    }

    public static String formatDuration(long duration)
    {
        if (duration > 0)
        {
            return DurationFormatUtils.formatDuration(duration * 1000, "m:ss ");
        }
        return null;
    }

    public static String formatBitDepth(int bitDepth)
    {
        return String.valueOf(bitDepth);
    }
}
