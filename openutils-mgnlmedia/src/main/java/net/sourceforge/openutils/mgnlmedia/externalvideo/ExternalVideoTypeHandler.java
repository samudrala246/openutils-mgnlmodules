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
package net.sourceforge.openutils.mgnlmedia.externalvideo;

import info.magnolia.cms.beans.runtime.MultipartForm;
import info.magnolia.jcr.util.PropertyUtil;
import info.magnolia.objectfactory.Components;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.jcr.AccessDeniedException;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.servlet.http.HttpServletRequest;

import net.sourceforge.openutils.mgnlmedia.media.configuration.MediaConfigurationManager;
import net.sourceforge.openutils.mgnlmedia.media.types.MediaTypeHandler;


/*
 * 
 */
public class ExternalVideoTypeHandler extends BaseVideoTypeHandler
{

    public static final String ND_PROVIDER = "provider";

    public static ExternalVideoProvider getExternalVideoProvider(Node media)
    {
        MediaTypeHandler mth = Components
            .getComponent(MediaConfigurationManager.class)
            .getMediaTypeConfigurationFromMedia(media)
            .getHandler();
        if (mth.getClass().isAssignableFrom(ExternalVideoTypeHandler.class))
        {
            return ((ExternalVideoTypeHandler) mth).getVideoProvider(media);
        }
        return null;
    }

    /**
     * External video support, will be made configurable in future versions.
     */
    private Map<String, ExternalVideoProvider> videoProviders = new HashMap<String, ExternalVideoProvider>();

    /**
     * {@inheritDoc}
     */
    @Override
    public void init(Node typeDefinitionNode)
    {
        super.init(typeDefinitionNode);
    }

    /**
     * Returns the videoProviders.
     * @return the videoProviders
     */
    public Map<String, ExternalVideoProvider> getVideoProviders()
    {
        return videoProviders;
    }

    /**
     * Sets the videoProviders.
     * @param videoProviders the videoProviders to set
     */
    public void setVideoProviders(Map<String, ExternalVideoProvider> videoProviders)
    {
        this.videoProviders = videoProviders;
    }

    /**
     * Adds a video support handler.
     * @param videoSupportHandler ExternalVideoSupport implementation
     */
    public void addVideoProvider(String name, ExternalVideoProvider videoProvider)
    {
        videoProviders.put(name, videoProvider);
    }

    public ExternalVideoProvider getVideoProvider(Node media)
    {
        return videoProviders.get(PropertyUtil.getString(media, ND_PROVIDER));
    }

    @Override
    public void saveFromZipFile(Node media, File f, String cleanFileName, String extension)
        throws AccessDeniedException, RepositoryException
    {
        media.setProperty("provider", "weebo");
        ExternalVideoUtil.setStatus(media.getIdentifier(), AsyncUploadExternalVideoProvider.STATUS_TO_UPLOAD);
        super.saveFromZipFile(media, f, cleanFileName, extension);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUrl(Node media, Map<String, String> options)
    {
        ExternalVideoProvider provider = getVideoProvider(media);
        if (provider == null)
        {
            return null;
        }
        return provider.getUrl(media, options);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getNewNodeName(MultipartForm form, HttpServletRequest request)
    {
        return videoProviders.get(request.getParameter(ND_PROVIDER)).getNewNodeName(form, request);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUrl(Node media)
    {
        ExternalVideoProvider provider = getVideoProvider(media);
        if (provider == null)
        {
            return null;
        }
        return provider.getUrl(media, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onPostSave(Node media)
    {
        ExternalVideoProvider provider = getVideoProvider(media);
        if (provider != null)
        {
            provider.processVideo(media);
        }

        return super.onPostSave(media);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getFilename(Node media)
    {
        ExternalVideoProvider provider = getVideoProvider(media);
        if (provider != null)
        {
            return provider.getFilename(media);
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getThumbnailUrl(Node media)
    {
        ExternalVideoProvider provider = getVideoProvider(media);
        if (provider == null)
        {
            return null;
        }
        return provider.getThumbnailUrl(media);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getPreviewUrl(Node media)
    {
        ExternalVideoProvider provider = getVideoProvider(media);
        if (provider == null)
        {
            return null;
        }
        return provider.getPreviewUrl(media);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stop()
    {
        Map<String, ExternalVideoProvider> providers = this.getVideoProviders();
        if (providers != null)
        {
            for (ExternalVideoProvider provider : providers.values())
            {
                provider.stop();
            }
        }
        super.stop();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getReplacementThumbnail()
    {
        return "/.resources/media/icons/thumb-video.png";
    }
}
