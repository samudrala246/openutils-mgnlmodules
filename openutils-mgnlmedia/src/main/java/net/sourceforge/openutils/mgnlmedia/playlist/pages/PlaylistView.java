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

package net.sourceforge.openutils.mgnlmedia.playlist.pages;

import info.magnolia.cms.core.Content;
import info.magnolia.cms.i18n.I18nContentSupportFactory;
import info.magnolia.cms.util.ContentUtil;
import info.magnolia.cms.util.NodeDataUtil;
import info.magnolia.context.MgnlContext;
import info.magnolia.jcr.util.NodeUtil;
import info.magnolia.jcr.util.PropertyUtil;
import info.magnolia.module.ModuleRegistry;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import javax.jcr.RepositoryException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.openutils.mgnlcriteria.jcr.query.AdvancedResultItem;
import net.sourceforge.openutils.mgnlmedia.media.configuration.MediaConfigurationManager;
import net.sourceforge.openutils.mgnlmedia.media.configuration.MediaTypeConfiguration;
import net.sourceforge.openutils.mgnlmedia.media.lifecycle.MediaModule;
import net.sourceforge.openutils.mgnlmedia.media.pages.MediaBean;
import net.sourceforge.openutils.mgnlmedia.media.pages.MediaBeanBuilder;
import net.sourceforge.openutils.mgnlmedia.media.pages.MessagesTemplatedMVCHandler;
import net.sourceforge.openutils.mgnlmedia.media.tags.el.MediaEl;
import net.sourceforge.openutils.mgnlmedia.playlist.PlaylistConstants;
import net.sourceforge.openutils.mgnlmedia.playlist.utils.PlaylistIterateUtils;
import net.sourceforge.openutils.mgnlmedia.playlist.utils.PlaylistIterateUtils.MediaNodeAndEntryPath;

import org.apache.commons.lang.LocaleUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author dschivo
 */
public class PlaylistView extends MessagesTemplatedMVCHandler
{

    /**
     * Logger.
     */
    private Logger log = LoggerFactory.getLogger(PlaylistView.class);

    private String path;

    private PlaylistBean playlist;

    private String title;

    private String description;

    private String mediaHandle;

    private boolean success;

    private static final String VIEW_XSPF = "-xspf";

    /**
     * JSON response to ajax calls (eg: saving playlist title and description)
     */
    private static final String VIEW_EXTJS = "-extjs";

    private String locale;

    private boolean xml;

    private List<Content> mediaContentList;

    private List<MediaBean> mediaBeans;

    /**
     *
     */
    public PlaylistView(String name, HttpServletRequest request, HttpServletResponse response)
    {
        super(name, request, response);
    }

    /**
     * Sets the path.
     * @param path the path to set
     */
    public void setPath(String path)
    {
        this.path = path;
    }

    /**
     * Returns the playlist.
     * @return the playlist
     */
    public PlaylistBean getPlaylist()
    {
        return playlist;
    }

    /**
     * Sets the title.
     * @param title the title to set
     */
    public void setTitle(String title)
    {
        this.title = title;
    }

    /**
     * Sets the description.
     * @param description the description to set
     */
    public void setDescription(String description)
    {
        this.description = description;
    }

    /**
     * Returns the mediaHandle.
     * @return the mediaHandle
     */
    public String getMediaHandle()
    {
        return mediaHandle;
    }

    /**
     * Sets the mediaHandle.
     * @param mediaHandle the mediaHandle to set
     */
    public void setMediaHandle(String mediaHandle)
    {
        this.mediaHandle = mediaHandle;
    }

    /**
     * Returns the success.
     * @return the success
     */
    public boolean isSuccess()
    {
        return success;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String show()
    {
        if (StringUtils.isNotBlank(path))
        {
            Content node = ContentUtil.getContent(PlaylistConstants.REPO, path);
            if (node != null)
            {

                playlist = new PlaylistBean();
                playlist.setUuid(node.getUUID());
                playlist.setHandle(node.getHandle());
                playlist.setTitle(NodeDataUtil.getString(node, "title"));
                playlist.setDescription(NodeDataUtil.getString(node, "description"));
                List<PlaylistEntryBean> entries = new ArrayList<PlaylistEntryBean>();

                for (Iterator<MediaNodeAndEntryPath> iterator = PlaylistIterateUtils.iterate(node.getJCRNode()); iterator
                    .hasNext();)
                {
                    MediaNodeAndEntryPath item = iterator.next();
                    AdvancedResultItem media = item.getMediaNode();
                    if (media == null)
                    {
                        continue;
                    }

                    PlaylistEntryBean entry = new PlaylistEntryBean();
                    entry.setHandle(item.getPlaylistEntryPath());
                    entry.setMedia(NodeUtil.getNodeIdentifierIfPossible(media));
                    entry.setMediaHandle(media.getHandle());
                    MediaTypeConfiguration typeConf = MediaConfigurationManager
                        .getInstance()
                        .getMediaTypeConfigurationFromMedia(media);
                    if (typeConf != null)
                    {
                        entry.setMediaDialog(typeConf.getDialog());
                    }
                    entry.setThumbnail(MediaEl.thumbnail(media));
                    entry.setType(PropertyUtil.getString(media, "type"));
                    entry.setTitle(PropertyUtil.getString(media, "title"));
                    entry.setDescription(PropertyUtil.getString(media, "description"));
                    entry.setTags(PropertyUtil.getString(media, "tags"));
                    entries.add(entry);
                }
                playlist.setEntries(entries);

                try
                {
                    playlist.setSearchBased(node.hasContent("search"));
                }
                catch (RepositoryException e)
                {
                    // should never happen
                }
            }
            else
            {
                log.warn("Node {} does not exist in playlist repository", path);
            }
        }
        return VIEW_SHOW;
    }

    public String save()
    {
        success = false;
        Content node = ContentUtil.getContent(PlaylistConstants.REPO, path);
        if (node != null)
        {
            try
            {
                NodeDataUtil.getOrCreateAndSet(node, "title", title);
                NodeDataUtil.getOrCreateAndSet(node, "description", description);
                if (MediaEl.module().isSingleinstance())
                {
                    node.getMetaData().setActivated();
                }
                node.save();
                success = true;
            }
            catch (RepositoryException e)
            {
            }
        }
        return VIEW_EXTJS;
    }

    public String saveMedia()
    {
        success = false;
        Content node = ContentUtil.getContent(MediaModule.REPO, mediaHandle);
        if (node != null)
        {
            try
            {
                NodeDataUtil.getOrCreateAndSet(node, "title", title);
                NodeDataUtil.getOrCreateAndSet(node, "description", description);
                if (MediaEl.module().isSingleinstance())
                {
                    node.getMetaData().setActivated();
                }
                node.save();
                success = true;
            }
            catch (RepositoryException e)
            {
            }
        }
        return VIEW_EXTJS;
    }

    public String xspf() throws IOException
    {
        String viewName = this.show();
        if (this.getPlaylist() == null)
        {
            this.response.sendError(404);
            return viewName;
        }
        else
        {
            // Set mediaContent
            mediaContentList = new ArrayList<Content>();
            for (PlaylistEntryBean plb : this.getPlaylist().getEntries())
            {
                try
                {
                    mediaContentList.add(MgnlContext.getHierarchyManager(MediaModule.REPO).getContentByUUID(
                        plb.getMedia()));
                }
                catch (RepositoryException e)
                {
                    log.error("Error retrieving media {}", e);
                }
            }

            // Get locale
            if (StringUtils.isEmpty(locale))
            {
                locale = Locale.getDefault().toString();
            }

            if (xml)
            {
                this.response.setContentType("text/xml");
            }
            else
            {
                this.response.setContentType("application/xspf+xml");
            }
        }
        return VIEW_XSPF;
    }

    public String mediaFolder()
    {
        Content folder = ContentUtil.getContent(MediaModule.REPO, path);
        if (folder != null)
        {
            Collection<Content> nodes = folder.getChildren(MediaConfigurationManager.MEDIA);
            mediaBeans = new ArrayList<MediaBean>(nodes.size());
            for (Content node : nodes)
            {
                MediaBean bean = new MediaBeanBuilder().apply(node);
                mediaBeans.add(bean);
            }
        }
        return "-mediaFolder";
    }

    /**
     * Returns the locale.
     * @return the locale
     */
    public String getLocale()
    {
        return locale;
    }

    /**
     * Sets the locale.
     * @param locale the locale to set
     */
    public void setLocale(String locale)
    {
        this.locale = locale;
    }

    /**
     * Returns the mediaContentList.
     * @return the mediaContentList
     */
    public List<Content> getMediaContentList()
    {
        return mediaContentList;
    }

    /**
     * Sets the mediaContentList.
     * @param mediaContentList the mediaContentList to set
     */
    public void setMediaContentList(List<Content> mediaContentList)
    {
        this.mediaContentList = mediaContentList;
    }

    /**
     * Returns the mediaBeans.
     * @return the mediaBeans
     */
    public List<MediaBean> getMediaBeans()
    {
        return mediaBeans;
    }

    /**
     * Sets the xml.
     * @param xml the xml to set
     */
    public void setXml(boolean xml)
    {
        this.xml = xml;
    }

    public String playlistTitle()
    {
        return playlist.getTitle();
    }

    public String playlistLocation()
    {
        return "/playlists" + playlist.getHandle() + ".xspf?locale=" + locale;
    }

    public Map<String, Object> playlistMetas()
    {
        Map<String, Object> metas = new LinkedHashMap<String, Object>();
        metas.put("media:locale", locale);
        metas.put("media:playlist-id", playlist.getUuid());
        return metas;
    }

    public void writePlaylistTrackExtension(Content media, PrintWriter writer)
    {
        MediaModule module = (MediaModule) ModuleRegistry.Factory.getInstance().getModuleInstance(MediaModule.NAME);
        for (Object item : module.getPlaylistTrackExtensionContributors())
        {
            PlaylistTrackExtensionContributor contributor = (PlaylistTrackExtensionContributor) item;
            contributor.addMediaAttributes(media, writer);
        }
    }

}
