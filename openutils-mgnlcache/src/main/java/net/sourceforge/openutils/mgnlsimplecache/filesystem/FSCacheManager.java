/**
 *
 * Simplecache module for Magnolia CMS (http://www.openmindlab.com/lab/products/simplecache.html)
 * Copyright(C) 2010-2013, Openmind S.r.l. http://www.openmindonline.it
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

package net.sourceforge.openutils.mgnlsimplecache.filesystem;

import info.magnolia.cms.core.Path;
import info.magnolia.cms.util.MBeanUtil;
import info.magnolia.cms.util.ObservationUtil;
import info.magnolia.module.files.MD5CheckingFileExtractor;
import info.magnolia.repository.RepositoryConstants;
import info.magnolia.voting.Voter;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.jcr.observation.EventIterator;
import javax.jcr.observation.EventListener;
import javax.servlet.http.HttpServletRequest;

import net.sourceforge.openutils.mgnlsimplecache.managers.CacheManager;
import net.sourceforge.openutils.mgnlsimplecache.managers.CachedItem;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author Manuel Molaschi
 * @author Fabrizio Giustina
 * @version $Id$
 */
public class FSCacheManager implements CacheManager, FSCacheManagerMBean, EventListener
{

    private List<String> repositoryUrls = new ArrayList<String>();

    private final String cacheKeyInRequest;

    private final String cacheDir;

    private Map<String, FSCachedItem> contents = new ConcurrentHashMap<String, FSCachedItem>();

    private String basePath = "fscache";

    private boolean flushOnStop;

    private boolean reloadAtStartup;

    private int cacheHits;

    private int cacheMisses;

    private int cachePuts;

    private Voter gzipVoter;

    private volatile boolean active = true;

    private MessageDigest hashDigest;

    /**
     * Logger.
     */
    private Logger log = LoggerFactory.getLogger(FSCacheManager.class);

    /**
     * Constructor
     */
    public FSCacheManager()
    {
        super();
        cacheKeyInRequest = "__" + this.getClass().getName() + "__" + this.hashCode() + "__key";
        cacheDir = StringUtils.replace(Path.getCacheDirectoryPath(), "\\", "/")
            + ((Path.getCacheDirectoryPath().endsWith("\\") || Path.getCacheDirectoryPath().endsWith("/")) ? "" : "/")
            + this.getBasePath()
            + "/";

        new File(cacheDir).mkdirs();

        try
        {

            MBeanUtil.registerMBean(this.getClass().getSimpleName(), this);
        }
        catch (Throwable e)
        {
            log.error("Error registering cache MBean: " + e.getClass().getName() + " " + e.getMessage());
        }

        try
        {
            hashDigest = MessageDigest.getInstance("MD5");
        }
        catch (NoSuchAlgorithmException e)
        {
            // should never happen
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns the gzip.
     * @return the gzip
     */
    public Voter getGzipVoter()
    {
        return gzipVoter;
    }

    /**
     * Sets the gzip.
     * @param gzip the gzip to set
     */
    public void setGzipVoter(Voter gzip)
    {
        this.gzipVoter = gzip;
    }

    /**
     * Returns the basePath.
     * @return the basePath
     */
    public String getBasePath()
    {
        return basePath;
    }

    /**
     * Sets the basePath.
     * @param basePath the basePath to set
     */
    public void setBasePath(String basePath)
    {
        this.basePath = basePath;
    }

    /**
     * Get cache directory
     * @return cache dir
     */
    public String getCacheDir()
    {
        return this.cacheDir;
    }

    /**
     * Sets the flushOnStop.
     * @param flushOnStop the flushOnStop to set
     */
    public void setFlushOnStop(boolean flushOnStop)
    {
        this.flushOnStop = flushOnStop;
    }

    /**
     * Returns the reloadAtStartup.
     * @return the reloadAtStartup
     */
    public boolean isReloadAtStartup()
    {
        return reloadAtStartup;
    }

    /**
     * Sets the reloadAtStartup.
     * @param reloadAtStartup the reloadAtStartup to set
     */
    public void setReloadAtStartup(boolean reloadAtStartup)
    {
        this.reloadAtStartup = reloadAtStartup;
    }

    /**
     * {@inheritDoc}
     */
    public int getCacheCount()
    {
        return contents.size();
    }

    public int getCacheHits()
    {
        return this.cacheHits;
    }

    public int getCacheMisses()
    {
        return this.cacheMisses;
    }

    public int getCachePuts()
    {
        return this.cachePuts;
    }

    public void resetStatistics()
    {
        this.cacheHits = 0;
        this.cacheMisses = 0;
        this.cachePuts = 0;
    }

    /**
     * {@inheritDoc}
     */
    public void onEvent(EventIterator events)
    {
        flush();
    }

    /**
     * Returns the repositoriesToWatch.
     * @return the repositoriesToWatch
     */
    public List<String> getRepositoryUrls()
    {
        return repositoryUrls;
    }

    /**
     * Sets the repositoriesToWatch.
     * @param repositoriesToWatch the repositoriesToWatch to set
     */
    public void addRepositoryUrl(String repo)
    {
        this.repositoryUrls.add(repo);
    }

    /**
     * {@inheritDoc}
     */
    public long getSizeOnDisk()
    {
        long size = 0;
        for (FSCachedItem content : contents.values())
        {
            size += content.getTotalSizeOnDiskInBytes();
        }

        // size in b
        return size / 1024;
    }

    /**
     * {@inheritDoc}
     */

    public boolean isFlushOnStop()
    {
        return flushOnStop;
    }

    /**
     * Get key
     * @param request
     * @return
     */
    public String getKey(HttpServletRequest request)
    {
        String key = (String) request.getAttribute(cacheKeyInRequest);
        if (key == null)
        {
            String uri = StringUtils.replaceOnce(request.getRequestURI(), request.getContextPath(), StringUtils.EMPTY);
            if (StringUtils.isBlank(uri))
            {
                uri = "/";
            }
            if (uri.endsWith("/"))
            {
                uri += "root";
            }
            String[] uriTokens = StringUtils.split(uri, "/");
            for (int i = 0; i < uriTokens.length; i++)
            {
                try
                {
                    uriTokens[i] = URLEncoder.encode(uriTokens[i], "UTF-8");
                }
                catch (UnsupportedEncodingException e)
                {
                    // never happens
                }
                if (i < uriTokens.length - 1)
                {
                    uriTokens[i] += ".cachedir";
                }
            }
            key = StringUtils.join(uriTokens, "/");
            key = (StringUtils.isNotBlank(request.getScheme()) ? request.getScheme() : "http")
                + "-"
                + request.getServerName()
                + "/"
                + key;
            if (StringUtils.isNotBlank(request.getQueryString()))
            {
                key += "-qs-" + request.getQueryString().hashCode();
            }
        }

        // build hash (avoid long filenames or invalid chars)
        key = String.valueOf(Hex.encodeHex(hashDigest.digest(key.getBytes())));

        // build directories with the first 4 chars
        StringBuffer path = new StringBuffer();
        for (int i = 0; i < 4; i++)
        {
            path.append(key.substring(i, i + 1)).append("/");
        }
        path.append(key);

        return path.toString();
    }

    /**
     * {@inheritDoc}
     */
    public CachedItem get(HttpServletRequest request)
    {

        boolean gzipable = gzipVoter != null && gzipVoter.vote(request) > 0;

        boolean miss = !contents.containsKey(getKey(request));
        if (miss)
        {
            cacheMisses++;
        }
        else
        {
            cacheHits++;
        }

        if (miss)
        {
            String fileName = getCacheDir() + getKey(request);
            contents.put(getKey(request), FSCachedItem.createNew(fileName, gzipable));
            cachePuts++;
        }
        return contents.get(getKey(request));
    }

    /**
     * {@inheritDoc}
     */
    public void reset(HttpServletRequest request) throws IOException
    {
        FSCachedItem content = (FSCachedItem) get(request);

        // handle concurrent cache flushing
        if (content != null)
        {
            boolean locked = content.lockToWrite();
            content.flush();
            contents.remove(getKey(request));
            if (locked)
            {
                content.releaseLockToWrite();
            }
            else
            {
                log.debug("Content not locked: {}", request.getRequestURI());
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public void start()
    {

        // to be sure...
        contents.clear();

        if (reloadAtStartup)
        {

            // restore cache contents
            String cacheDir = getCacheDir();
            Collection<File> files = FileUtils.listFiles(new File(cacheDir), new IOFileFilter()
            {

                /**
                 * {@inheritDoc}
                 */
                public boolean accept(File dir, String name)
                {
                    return accept(new File(dir, name));
                }

                /**
                 * {@inheritDoc}
                 */
                public boolean accept(File file)
                {
                    return !file.isDirectory() && !file.getName().endsWith(".gz");
                }

            }, TrueFileFilter.INSTANCE);
            for (File cached : files)
            {
                String absPath = StringUtils.replace(cached.getAbsolutePath(), "\\", "/");
                String path = StringUtils.substringAfter(absPath, cacheDir);
                contents.put(path, FSCachedItem.reload(absPath));
            }
        }

        // start observing repositories
        if (repositoryUrls.size() == 0)
        {
            repositoryUrls.add(RepositoryConstants.WEBSITE);
        }
        for (String repositoryUrl : repositoryUrls)
        {
            if (StringUtils.isNotBlank(repositoryUrl))
            {
                String repository = repositoryUrl;
                String path = "/";
                if (repositoryUrl.indexOf(":") > 0)
                {
                    repository = StringUtils.substringBefore(repositoryUrl, ":");
                    path = StringUtils.substringAfter(repositoryUrl, ":");
                }
                ObservationUtil.registerDeferredChangeListener(repository, path, this, 5000, 30000);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void stop()
    {
        // stop observing repositories
        for (String repositoryUrl : repositoryUrls)
        {
            if (StringUtils.isNotBlank(repositoryUrl))
            {
                String repository = StringUtils.substringBefore(repositoryUrl, ":");
                ObservationUtil.unregisterChangeListener(repository, this);
            }
        }

        // clean cache
        if (flushOnStop)
        {
            flush();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void flush()
    {
        log.info("Flushing cache");
        active = false;

        try
        {
            final File cacheDir = new File(getCacheDir());

            contents.clear();

            // remove cache folder
            if (cacheDir.exists())
            {

                final File renamedDir = new File(cacheDir.getParentFile(), "deleted." + System.currentTimeMillis());
                cacheDir.renameTo(renamedDir);

                new Thread()
                {

                    /**
                     * {@inheritDoc}
                     */
                    @Override
                    public void run()
                    {
                        log.info("Deleting stale cache dir {}", renamedDir.getAbsolutePath());

                        try
                        {
                            FileUtils.deleteDirectory(renamedDir);
                            log.info("Cache dir {} successfully deleted", renamedDir.getAbsolutePath());
                        }
                        catch (IOException e)
                        {
                            log.info("Unable to delete cache dir {}", renamedDir.getAbsolutePath());
                        }
                    }
                }.start();
            }
        }
        finally
        {
            active = true;
            log.info("Cache restarted");
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean isActive()
    {
        return active;
    }

}
