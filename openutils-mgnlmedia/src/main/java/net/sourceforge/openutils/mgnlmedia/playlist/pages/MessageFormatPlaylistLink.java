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

package net.sourceforge.openutils.mgnlmedia.playlist.pages;

import java.text.MessageFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author ADMIN
 * @version $Id: $
 */
public class MessageFormatPlaylistLink extends AbstractPlaylistLink
{

    private static final Logger log = LoggerFactory.getLogger(MessageFormatPlaylistLink.class);

    private String urlFormat;

    private MessageFormat urlMessageFormat;

    /**
     * Returns the urlFormat.
     * @return the urlFormat
     */
    public String getUrlFormat()
    {
        return urlFormat;
    }

    /**
     * Sets the urlFormat.
     * @param urlFormat the urlFormat to set
     */
    public void setUrlFormat(String urlFormat)
    {
        this.urlFormat = urlFormat;
        try
        {
            urlMessageFormat = new MessageFormat(this.urlFormat);
        }
        catch (IllegalArgumentException e)
        {
            log.error("Invalid \"sdpUrlPattern\" (check your magnolia.properties)", e);
            urlMessageFormat = null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String url(PlaylistBean playlist)
    {
        return urlMessageFormat.format(new Object[] { playlist.getHandle() });
    }

}
