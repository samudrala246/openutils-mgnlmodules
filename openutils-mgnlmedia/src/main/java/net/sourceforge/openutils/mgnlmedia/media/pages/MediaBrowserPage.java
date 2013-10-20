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

package net.sourceforge.openutils.mgnlmedia.media.pages;

import info.magnolia.cms.beans.runtime.Document;
import info.magnolia.cms.core.MgnlNodeType;
import info.magnolia.cms.i18n.Messages;
import info.magnolia.cms.util.AlertUtil;
import info.magnolia.context.MgnlContext;
import info.magnolia.jcr.util.NodeUtil;
import info.magnolia.jcr.util.PropertyUtil;
import it.openutils.mgnlutils.util.NodeUtilsExt;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipFile;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.openutils.mgnlmedia.media.lifecycle.MediaModule;
import net.sourceforge.openutils.mgnlmedia.media.tags.el.MediaEl;
import net.sourceforge.openutils.mgnlmedia.media.zip.DefaultZipImporter;
import net.sourceforge.openutils.mgnlmedia.media.zip.ZipImporter;
import net.sourceforge.openutils.mgnlmedia.media.zip.ZipImporterException;
import net.sourceforge.openutils.mgnlmedia.playlist.PlaylistConstants;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Page that contains the tree and the folders view.
 * @author molaschi
 * @version $Id$
 */
public class MediaBrowserPage extends MessagesTemplatedMVCHandler
{

    /**
     * Logger.
     */
    private Logger log = LoggerFactory.getLogger(MediaBrowserPage.class);

    private String cacheKiller;

    private String actMedia;

    private boolean selectMedia;

    private String nodeid;

    private String openPath;

    private String actMediaHandle;

    private String mediaType;

    private String parentPath;

    private Document zipFile;

    private String playlistHandle;

    private String playlistSearch;

    /**
     * @param name command name
     * @param request
     * @param response
     */
    public MediaBrowserPage(String name, HttpServletRequest request, HttpServletResponse response)
    {
        super(name, request, response);
        cacheKiller = String.valueOf(new Date().getTime());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void init()
    {
        super.init();
        if (StringUtils.isNotBlank(actMedia))
        {
            try
            {
                Session mgr = MgnlContext.getJCRSession(MediaModule.REPO);
                Node media = mgr.getNodeByIdentifier(actMedia);
                openPath = media.getParent().getPath();
                actMediaHandle = media.getPath();
            }
            catch (RepositoryException ex)
            {
                log.warn("Error retrieving media {}", actMedia);
            }
        }
        if (!StringUtils.isEmpty(playlistHandle))
        {
            Node playlistContent = NodeUtilsExt.getNodeByIdOrPath(PlaylistConstants.REPO, playlistHandle);
            try
            {
                if (playlistContent.hasNode("search"))
                {
                    List<String> params = new ArrayList<String>();
                    Node search = playlistContent.getNode("search");
                    Iterable<Node> nodes = NodeUtil.getNodes(search, MgnlNodeType.NT_CONTENTNODE);
                    for (Node content : nodes)
                    {
                        String paramName = PropertyUtil.getString(content, "name");
                        String paramValue = PropertyUtil.getString(content, "value");
                        params.add(paramName + "=" + paramValue);
                    }
                    playlistSearch = StringUtils.join(params, '&');
                }
            }
            catch (RepositoryException e)
            {
                // should never happen
            }
        }
        for (Cookie cookie : request.getCookies())
        {
            if ("mediafolderpath".equals(cookie.getName()))
            {
                log.debug("Found mediafolderpath cookie with value {}", cookie.getValue());
                if (StringUtils.isEmpty(openPath))
                {
                    try
                    {
                        openPath = URLDecoder.decode(cookie.getValue(), "UTF-8");
                    }
                    catch (UnsupportedEncodingException e)
                    {
                        // should never happen
                    }
                }
                log.debug("openPath={}", openPath);
            }
        }
    }

    /**
     * Save Zip file command
     * @return view
     */
    public String saveZip()
    {
        InputStream zipStream = null;
        File temp = null;
        try
        {
            temp = File.createTempFile("zipmedia", ".zip");
            FileOutputStream fos = new FileOutputStream(temp);

            zipStream = zipFile.getStream();
            IOUtils.copy(zipStream, fos);
            IOUtils.closeQuietly(fos);
            ZipFile zip = new ZipFile(temp);
            ZipImporter importer = MediaEl.module().getZipimporter();
            if (importer == null)
            { // Fallback to the default if nothing is set.
                importer = new DefaultZipImporter();
            }
            importer.importFromZip(zip, parentPath);
            zip.close();
            AlertUtil.setMessage(getMsgs().get("media.loadzip.success"));
        }
        catch (IOException e)
        {
            log.error(e.getMessage(), e);
            AlertUtil.setMessage(getMessage("media.loadzip.failure", e.getMessage()));
        }
        catch (ZipImporterException e)
        {
            log.error(e.getMessage(), e);
            AlertUtil.setMessage(getMessage("media.loadzip.failure", e.getMessage()));
        }
        finally
        {
            IOUtils.closeQuietly(zipStream);
            FileUtils.deleteQuietly(temp);
        }

        this.openPath = parentPath;

        return this.show();
    }

    /**
     * Returns the cacheKiller.
     * @return the cacheKiller
     */
    public String getCacheKiller()
    {
        return cacheKiller;
    }

    /**
     * Sets the cacheKiller.
     * @param cacheKiller the cacheKiller to set
     */
    public void setCacheKiller(String cacheKiller)
    {
        this.cacheKiller = cacheKiller;
    }

    /**
     * Returns the actMedia.
     * @return the actMedia
     */
    public String getActMedia()
    {
        return actMedia;
    }

    /**
     * Sets the actMedia.
     * @param actMedia the actMedia to set
     */
    public void setActMedia(String actMedia)
    {
        this.actMedia = actMedia;
    }

    /**
     * Returns the selectMedia.
     * @return the selectMedia
     */
    public boolean isSelectMedia()
    {
        return selectMedia;
    }

    /**
     * Sets the selectMedia.
     * @param selectMedia the selectMedia to set
     */
    public void setSelectMedia(boolean selectMedia)
    {
        this.selectMedia = selectMedia;
    }

    /**
     * Returns the nodeid.
     * @return the nodeid
     */
    public String getNodeid()
    {
        return nodeid;
    }

    /**
     * Sets the nodeid.
     * @param nodeid the nodeid to set
     */
    public void setNodeid(String nodeid)
    {
        this.nodeid = nodeid;
    }

    /**
     * Returns the openPath.
     * @return the openPath
     */
    public String getOpenPath()
    {
        return openPath;
    }

    /**
     * Sets the openPath.
     * @param openPath the openPath to set
     */
    public void setOpenPath(String openPath)
    {
        this.openPath = openPath;
    }

    /**
     * Returns the actMediaHandle.
     * @return the actMediaHandle
     */
    public String getActMediaHandle()
    {
        return actMediaHandle;
    }

    /**
     * Sets the actMediaHandle.
     * @param actMediaHandle the actMediaHandle to set
     */
    public void setActMediaHandle(String actMediaHandle)
    {
        this.actMediaHandle = actMediaHandle;
    }

    /**
     * Returns the mediaType.
     * @return the mediaType
     */
    public String getMediaType()
    {
        return mediaType;
    }

    /**
     * Sets the mediaType.
     * @param mediaType the mediaType to set
     */
    public void setMediaType(String mediaType)
    {
        this.mediaType = mediaType;
    }

    /**
     * Returns the parentPath.
     * @return the parentPath
     */
    public String getParentPath()
    {
        return parentPath;
    }

    /**
     * Sets the parentPath.
     * @param parentPath the parentPath to set
     */
    public void setParentPath(String parentPath)
    {
        this.parentPath = parentPath;
    }

    /**
     * Returns the file.
     * @return the file
     */
    public Document getZipFile()
    {
        return zipFile;
    }

    /**
     * Sets the file.
     * @param file the file to set
     */
    public void setZipFile(Document file)
    {
        this.zipFile = file;
    }

    /**
     * Returns the playlistHandle.
     * @return the playlistHandle
     */
    public String getPlaylistHandle()
    {
        return playlistHandle;
    }

    /**
     * Sets the playlistHandle.
     * @param playlistHandle the playlistHandle to set
     */
    public void setPlaylistHandle(String playlistHandle)
    {
        this.playlistHandle = playlistHandle;
    }

    /**
     * Returns the playlistSearch.
     * @return the playlistSearch
     */
    public String getPlaylistSearch()
    {
        return playlistSearch;
    }

    /**
     * Sets the playlistSearch.
     * @param playlistSearch the playlistSearch to set
     */
    public void setPlaylistSearch(String playlistSearch)
    {
        this.playlistSearch = playlistSearch;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Messages getMsgs()
    {
        return super.getMsgs();
    }

    public String getMessage(String key, String arg)
    {
        return super.getMsgs().get(key, new String[]{arg });
    }

    /**
     * Returns the installed media module version.
     * @return version as string.
     */
    public String getModuleVersion()
    {
        return MediaEl.module().getVersion();
    }

}
