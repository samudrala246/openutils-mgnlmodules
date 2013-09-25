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

package net.sourceforge.openutils.mgnlmedia.media.types.impl;

import info.magnolia.cms.beans.runtime.Document;
import info.magnolia.cms.beans.runtime.MultipartForm;
import info.magnolia.cms.util.ContentUtil;
import info.magnolia.jcr.util.PropertyUtil;
import info.magnolia.module.admininterface.SaveHandlerImpl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.jcr.AccessDeniedException;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.servlet.http.HttpServletRequest;

import net.sourceforge.openutils.mgnlmedia.media.types.externals.ExternalVideoSupport;
import net.sourceforge.openutils.mgnlmedia.media.utils.RtmpMedatadaFetcher;
import net.sourceforge.openutils.mgnlmedia.media.utils.VideoMedataUtils;
import net.sourceforge.openutils.mgnlmedia.media.utils.VideoMedataUtils.VideoMetaData;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * MediaType handler for external videos (youtube and similar)
 * @author fgiust
 * @version $Id$
 */
public class ExternalVideoTypeHandler extends BaseVideoTypeHandler
{

    /**
     * Name for the attribute holding the base path
     */
    private static final String BASEPATH_ATTRIBUTE = "basepath";

    /**
     * Logger.
     */
    private final Logger log = LoggerFactory.getLogger(ExternalVideoTypeHandler.class);

    /**
     * External video support, will be made configurable in future versions.
     */
    private List<ExternalVideoSupport> videoSupportHandlers = new ArrayList<ExternalVideoSupport>();

    /**
     * If true, the media module will try to connect to the remote server to analyze flv metadata.
     */
    private boolean parseremotefiles;

    /**
     * If true, the media module will try to connect to the remote server to analyze flv metadata.
     * @param parseremotefiles true to enable the parsing of remote flv files.
     */
    public void setParseremotefiles(boolean parseremotefiles)
    {
        this.parseremotefiles = parseremotefiles;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void init(Node typeDefinitionNode)
    {
        super.init(typeDefinitionNode);
    }

    /**
     * Returns the list of configured videoSupportHandlers.
     * @return the list of configured videoSupportHandlers.
     */
    public List<ExternalVideoSupport> getVideoSupportHandlers()
    {
        return videoSupportHandlers;
    }

    /**
     * Sets the list of configured videoSupportHandlers.
     * @param videoSupportHandlers the list of configured videoSupportHandlers.
     */
    public void setVideoSupportHandlers(List<ExternalVideoSupport> videoSupportHandlers)
    {
        this.videoSupportHandlers = videoSupportHandlers;
    }

    /**
     * Adds a video support handler.
     * @param videoSupportHandler ExternalVideoSupport implementation
     */
    // method required by contentToBean
    public void addVideoSupportHandlers(ExternalVideoSupport videoSupportHandler)
    {
        videoSupportHandlers.add(videoSupportHandler);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getFilename(Node media)
    {
        return getUrl(media);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getExtension(Node media)
    {
        return null;
    }

    @Override
    public void saveFromZipFile(Node media, File f, String cleanFileName, String extension)
        throws AccessDeniedException, RepositoryException
    {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUrl(Node media, Map<String, String> options)
    {

        String url = PropertyUtil.getString(media, "videoUrl");

        if (!StringUtils.startsWith(url, "http") && !StringUtils.startsWith(url, "rtmpt"))
        {
            String basepath = PropertyUtil.getString(media, "BASEPATH_ATTRIBUTE");
            if (StringUtils.isNotBlank(basepath))
            {
                return basepath + url;
            }
        }

        return url;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getNewNodeName(MultipartForm form, HttpServletRequest request)
    {
        String videoUrl = StringUtils.lowerCase(request.getParameter("videoUrl"));
        String nodeName = null;

        for (ExternalVideoSupport external : videoSupportHandlers)
        {
            if (external.isEnabled() && external.canHandle(videoUrl))
            {
                nodeName = external.getMediaName(videoUrl);
                break;
            }
        }

        if (StringUtils.isEmpty(nodeName))
        {
            if (StringUtils.contains(videoUrl, ".flv"))
            {
                nodeName = StringUtils.substringBefore(videoUrl, ".flv");
                if (StringUtils.contains(nodeName, "/"))
                {
                    nodeName = StringUtils.substringBeforeLast(nodeName, "/");
                }
                if (StringUtils.contains(nodeName, "="))
                {
                    nodeName = StringUtils.substringBeforeLast(nodeName, "=");
                }
            }
            else
            {
                nodeName = StringUtils.substringBefore(StringUtils.substringAfterLast(videoUrl, "/"), "?");

            }
        }

        if (StringUtils.isEmpty(nodeName))
        {
            nodeName = UUID.randomUUID().toString();
        }

        return nodeName;
    }

    @Override
    protected VideoMetaData parseFLVMetaData(Node media) throws Exception
    {
        if (!parseremotefiles)
        {
            return null;
        }

        String downloadUrl = getUrl(media);
        return parseFLVMetaData(downloadUrl);
    }

    /**
     * @param downloadUrl
     * @return
     */
    private VideoMetaData parseFLVMetaData(String downloadUrl)
    {
        for (ExternalVideoSupport external : videoSupportHandlers)
        {
            if (external.isEnabled() && external.canHandle(downloadUrl))
            {
                downloadUrl = external.getFlvUrl(downloadUrl);
                break;
            }
        }

        try
        {
            if (StringUtils.startsWith(downloadUrl, "http"))
            {
                // handle spaces: note we can't encode the full url here
                downloadUrl = StringUtils.replace(downloadUrl, " ", "%20");
                URL url = new URL(downloadUrl);
                String extension = StringUtils.contains(downloadUrl, ".flv") ? "flv" : "mp4";

                URLConnection connection = url.openConnection();
                connection.setConnectTimeout(5000);

                InputStream fis = connection.getInputStream();
                try
                {
                    return VideoMedataUtils.parsefromStream(extension, fis);
                }
                finally
                {
                    IOUtils.closeQuietly(fis);
                }

            }
            else if (StringUtils.startsWith(downloadUrl, "rtmp"))
            {
                return RtmpMedatadaFetcher.fetchMetadata(downloadUrl, 10000);
            }

        }
        catch (IOException e)
        {
            log.warn("Got a "
                + ClassUtils.getShortClassName(e.getClass())
                + " ("
                + e.getMessage()
                + ") while parsing URL "
                + downloadUrl);
        }
        catch (Throwable e)
        {
            log.warn("Got a "
                + ClassUtils.getShortClassName(e.getClass())
                + " ("
                + e.getMessage()
                + ") while parsing URL "
                + downloadUrl, e);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUrl(Node media)
    {
        return getUrl(media, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onPostSave(Node media)
    {

        if (parseremotefiles)
        {
            try
            {
                if (!media.hasProperty(PREVIEW_NODEDATA_NAME))
                {
                    String downloadUrl = getUrl(media);
                    for (ExternalVideoSupport external : videoSupportHandlers)
                    {
                        if (external.isEnabled() && external.canHandle(downloadUrl))
                        {
                            String previewUrl = external.getPreviewUrl(downloadUrl);
                            if (StringUtils.isNotBlank(previewUrl))
                            {
                                copyPreviewImageToRepository(media, previewUrl);
                            }
                            break;
                        }
                    }
                }
            }
            catch (RepositoryException e)
            {
                log.error(e.getMessage(), e);
            }
        }

        return super.onPostSave(media);
    }

    /**
     * @param media
     * @param previewUrl
     */
    private void copyPreviewImageToRepository(Node media, String previewUrl)
    {
        InputStream is = null;
        try
        {
            URL url = new URL(previewUrl);
            HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
            httpConn.setConnectTimeout(5000);
            if (httpConn.getResponseCode() == HttpURLConnection.HTTP_OK)
            {
                is = httpConn.getInputStream();
                File file = File.createTempFile("external-video", "preview");
                FileOutputStream fout = null;
                try
                {
                    fout = new FileOutputStream(file);
                    IOUtils.copy(is, fout);
                }
                finally
                {
                    IOUtils.closeQuietly(fout);
                }
                String contentType = httpConn.getContentType();
                Document doc = new Document(file, contentType);
                try
                {
                    SaveHandlerImpl.saveDocument(
                        ContentUtil.asContent(media),
                        doc,
                        PREVIEW_NODEDATA_NAME,
                        "preview",
                        null);
                }
                catch (RepositoryException e)
                {
                    log.error(e.getMessage(), e);
                }
                finally
                {
                    doc.delete();
                }
            }
            else
            {
                log.warn("Problem establishing connection with {}: {}", url, httpConn.getResponseCode());
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
            IOUtils.closeQuietly(is);
        }
    }
}
