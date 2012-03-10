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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * ExternalVideoSupport specific for Youtube URLs.
 * @author fgiust
 * @version $Id$
 */
public class YoutubeSupport implements ExternalVideoSupport
{

    /**
     * Logger.
     */
    private Logger log = LoggerFactory.getLogger(YoutubeSupport.class);

    /**
     * Enabled.
     */
    private boolean enabled = true;

    /**
     * {@inheritDoc}
     */
    public boolean isEnabled()
    {
        return enabled;
    }

    /**
     * Sets the enabled.
     * @param enabled the enabled to set
     */
    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }

    /**
     * {@inheritDoc}
     */
    public boolean canHandle(String url)
    {
        return StringUtils.startsWith(url, "http://www.youtube") || StringUtils.startsWith(url, "http://youtu.be/");
    }

    /**
     * {@inheritDoc}
     */
    public String getFlvUrl(String shareUrl)
    {
        String flvUrl = null;
        BufferedReader in = null;
        try
        {
            String videoId = getVideoId(shareUrl);
            String videoInfoUrl = "http://www.youtube.com/get_video_info?video_id=" + videoId;
            URL url = new URL(videoInfoUrl);
            HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
            httpConn.setConnectTimeout(5000);
            if (httpConn.getResponseCode() == HttpURLConnection.HTTP_OK)
            {
                in = new BufferedReader(new InputStreamReader(httpConn.getInputStream()));
                String line;
                String search = "&token=";
                while ((line = in.readLine()) != null)
                {
                    int p = line.indexOf(search);
                    if (p != -1)
                    {
                        String token = StringUtils.substringBefore(line.substring(p + search.length()), "&");
                        flvUrl = "http://www.youtube.com/get_video?fmt=5&video_id=" + videoId + "&t=" + token;
                        break;
                    }
                }
            }
        }
        catch (MalformedURLException e)
        {
            log.error(e.getMessage(), e);
        }
        catch (IOException e)
        {
            log.error(e.getMessage(), e);
        }
        finally
        {
            IOUtils.closeQuietly(in);
        }
        return flvUrl;
    }

    /**
     * {@inheritDoc}
     */
    public String getPreviewUrl(String shareUrl)
    {
        return "http://img.youtube.com/vi/" + getVideoId(shareUrl) + "/0.jpg";
    }

    /**
     * {@inheritDoc}
     */
    public String getMediaName(String shareUrl)
    {
        return getVideoId(shareUrl);
    }

    private String getVideoId(String shareUrl)
    {

        if (StringUtils.startsWith(shareUrl, "http://youtu.be/"))
        {

            return StringUtils.substringAfter(shareUrl, "http://youtu.be/");
        }

        for (String att : StringUtils.split(StringUtils.substringAfter(shareUrl, "?"), "&"))
        {
            if (att.startsWith("v="))
            {
                return StringUtils.substringAfter(att, "v=");
            }
        }
        return null;
    }

}
