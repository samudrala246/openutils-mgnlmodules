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

package net.sourceforge.openutils.mgnlmedia.media.lifecycle;

import info.magnolia.cms.beans.config.ObservedManager;
import info.magnolia.module.ModuleLifecycle;
import info.magnolia.module.ModuleLifecycleContext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import net.sourceforge.openutils.mgnlmedia.media.advancedsearch.configuration.SearchMediaQueryConfiguration;
import net.sourceforge.openutils.mgnlmedia.media.configuration.MediaConfigurationManager;
import net.sourceforge.openutils.mgnlmedia.media.configuration.MediaTypeConfiguration;
import net.sourceforge.openutils.mgnlmedia.media.utils.LockUtils;
import net.sourceforge.openutils.mgnlmedia.media.zip.ZipImporter;
import net.sourceforge.openutils.mgnlmedia.playlist.pages.PlaylistLink;
import net.sourceforge.openutils.mgnlmedia.playlist.pages.PlaylistTrackExtensionContributor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Media module lifecycle manager
 * @author molaschi
 */
public class MediaModule implements ModuleLifecycle
{

    /**
     * Media module name.
     */
    public static final String NAME = "media";

    /**
     * Media repository name.
     */
    public static final String REPO = "media";

    private Logger log = LoggerFactory.getLogger(MediaModule.class);

    private boolean singleinstance;

    private String player = "player.swf";

    private String version;

    private SearchMediaQueryConfiguration search;

    private int folderViewItemsPerPage;

    private Map<String, Integer> folderViewPageSizes = new HashMap<String, Integer>();

    private ZipImporter zipimporter;

    private List playlistTrackExtensionContributors = new ArrayList();

    private List playlistLinks = new ArrayList();

    private boolean lazyResolutionCreation;

    private int maxConcurrentThreads = 4;

    private LockUtils locks = new LockUtils(4);

    private String baseurl;

    @Inject
    private MediaConfigurationManager mediaConfigurationManager;

    /**
     * Constructor
     */
    public MediaModule()
    {
    }

    /**
     * {@inheritDoc}
     */
    public void start(ModuleLifecycleContext ctx)
    {
        log.info("Starting module media");
        if (ctx != null)
        {
            ctx.registerModuleObservingComponent("mediatypes", (ObservedManager) mediaConfigurationManager);
            version = ctx.getCurrentModuleDefinition().getVersion().toString();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void stop(ModuleLifecycleContext ctx)
    {
        log.info("Stopping module media");
        Collection<MediaTypeConfiguration> mtcs = mediaConfigurationManager.getTypes().values();
        if (mtcs != null)
        {
            for (MediaTypeConfiguration mtc : mtcs)
            {
                if (mtc.getHandler() != null)
                {
                    mtc.getHandler().stop();
                }
            }
        }
    }

    /**
     * If the singleinstance flag is set, the media module expect that no activation is needed (usually it means the
     * media repository is shared between the author and public instances).
     * @return the value of the singleinstance property
     */
    public boolean isSingleinstance()
    {
        return singleinstance;
    }

    /**
     * If the singleinstance flag is set, the media module expect that no activation is needed (usually it means the
     * media repository is shared between the author and public instances).
     * @param singleinstance true if no activation should be performed
     */
    public void setSingleinstance(boolean singleinstance)
    {
        this.singleinstance = singleinstance;
    }

    public String getPlayer()
    {
        return player;
    }

    public void setPlayer(String player)
    {
        this.player = player;
    }

    /**
     * Returns the version.
     * @return the version
     */
    public String getVersion()
    {
        return version;
    }

    /**
     * Returns the searchConfiguration.
     * @return the searchConfiguration
     */
    public SearchMediaQueryConfiguration getSearch()
    {
        return search;
    }

    /**
     * Sets the searchConfiguration.
     * @param searchConfiguration the searchConfiguration to set
     */
    public void setSearch(SearchMediaQueryConfiguration search)
    {
        this.search = search;
    }

    /**
     * Returns the folderViewItemsPerPage.
     * @return the folderViewItemsPerPage
     */
    public int getFolderViewItemsPerPage()
    {
        return folderViewItemsPerPage;
    }

    /**
     * Sets the folderViewItemsPerPage.
     * @param folderViewItemsPerPage the folderViewItemsPerPage to set
     */
    public void setFolderViewItemsPerPage(int folderViewItemsPerPage)
    {
        this.folderViewItemsPerPage = folderViewItemsPerPage;
    }

    /**
     * Returns the folderViewPageSizes.
     * @return the folderViewPageSizes
     */
    public Map<String, Integer> getFolderViewPageSizes()
    {
        return folderViewPageSizes;
    }

    /**
     * @param sizeKey
     * @param value
     */
    public void addFolderViewPageSizes(String sizeKey, Long value)
    {
        folderViewPageSizes.put(sizeKey, value.intValue());
    }

    /**
     * Returns the zipimporter.
     * @return the zipimporter
     */
    public ZipImporter getZipimporter()
    {
        return zipimporter;
    }

    /**
     * Sets the zipimporter.
     * @param zipimporter the zipimporter to set
     */
    public void setZipimporter(ZipImporter zipimporter)
    {
        this.zipimporter = zipimporter;
    }

    /**
     * Returns the playlistTrackExtensionContributors.
     * @return the playlistTrackExtensionContributors
     */
    public List getPlaylistTrackExtensionContributors()
    {
        return playlistTrackExtensionContributors;
    }

    /**
     * Sets the playlistTrackExtensionContributors.
     * @param playlistTrackExtensionContributors the playlistTrackExtensionContributors to set
     */
    public void setPlaylistTrackExtensionContributors(List playlistTrackExtensionContributors)
    {
        this.playlistTrackExtensionContributors = playlistTrackExtensionContributors;
    }

    public void addPlaylistTrackExtensionContributor(PlaylistTrackExtensionContributor contributor)
    {
        this.playlistTrackExtensionContributors.add(contributor);
    }

    /**
     * Returns the playlistLinks.
     * @return the playlistLinks
     */
    public List getPlaylistLinks()
    {
        return playlistLinks;
    }

    /**
     * Sets the playlistLinks.
     * @param playlistLinks the playlistLinks to set
     */
    public void setPlaylistLinks(List playlistLinks)
    {
        this.playlistLinks = playlistLinks;
    }

    public void addPlaylistLink(PlaylistLink playlistLink)
    {
        this.playlistLinks.add(playlistLink);
    }

    /**
     * Returns the lazyResolutionCreation.
     * @return the lazyResolutionCreation
     */
    public boolean isLazyResolutionCreation()
    {
        return lazyResolutionCreation;
    }

    /**
     * Sets the lazyResolutionCreation.
     * @param lazyResolutionCreation the lazyResolutionCreation to set
     */
    public void setLazyResolutionCreation(boolean lazyResolutionCreation)
    {
        this.lazyResolutionCreation = lazyResolutionCreation;
    }

    /**
     * Returns the maxConcurrentThreads.
     * @return the maxConcurrentThreads
     */
    public int getMaxConcurrentThreads()
    {
        return maxConcurrentThreads;
    }

    /**
     * Sets the maxConcurrentThreads.
     * @param maxConcurrentThreads the maxConcurrentThreads to set
     */
    public void setMaxConcurrentThreads(int maxConcurrentThreads)
    {
        this.maxConcurrentThreads = maxConcurrentThreads;
        locks = new LockUtils(maxConcurrentThreads);
    }

    /**
     * Returns the locks.
     * @return the locks
     */
    public LockUtils getLocks()
    {
        return locks;
    }

    /**
     * Returns the baseurl.
     * @return the baseurl
     */
    public String getBaseurl()
    {
        return baseurl;
    }

    /**
     * Sets the baseurl.
     * @param baseurl the baseurl to set
     */
    public void setBaseurl(String baseurl)
    {
        this.baseurl = baseurl;
    }

}
