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

package net.sourceforge.openutils.mgnlmedia.media.types.externals;

/**
 * Interface for external video handling. Users can implement a custom parsing logic to extract a flv/image url from a
 * specific sharing URL.
 * @author fgiust
 * @version $Id$
 */
public interface ExternalVideoSupport
{

    /**
     * Can this VideoSupport class handle this kind of urls? Usually done by checking the domain.
     * @param shareUrl share URL
     * @return true if this class can handle video with the given URL
     */
    boolean canHandle(String shareUrl);

    /**
     * Is this VideoSupport class enabled?
     * @return true if this video support class should be used when an external video is loaded
     */
    boolean isEnabled();

    /**
     * Return the FLV url from the share url. This method is guaranteed to be called only if canHandle() returned true
     * for the same URL.
     * @param shareUrl share URL
     * @return flv direct URL
     */
    String getFlvUrl(String shareUrl);

    /**
     * Return the preview url from the share url. This method is guaranteed to be called only if canHandle() returned
     * true for the same URL.
     * @param shareUrl share URL
     * @return preview URL (may be null)
     */
    String getPreviewUrl(String shareUrl);

    /**
     * Return the video name from the share url. This method is guaranteed to be called only if canHandle() returned
     * true for the same URL.
     * @param shareUrl share URL
     * @return video name (name of the node that will be created in the media repository)
     */
    String getMediaName(String shareUrl);
}
