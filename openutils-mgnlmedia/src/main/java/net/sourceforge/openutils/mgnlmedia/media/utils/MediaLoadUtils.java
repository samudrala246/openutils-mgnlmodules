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

import info.magnolia.cms.core.Content;
import info.magnolia.cms.core.HierarchyManager;
import info.magnolia.cms.core.Path;
import info.magnolia.cms.security.AccessDeniedException;
import info.magnolia.cms.util.ContentUtil;
import info.magnolia.cms.util.NodeDataUtil;
import info.magnolia.context.MgnlContext;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;

import javax.jcr.RepositoryException;

import net.sourceforge.openutils.mgnlmedia.media.configuration.MediaConfigurationManager;
import net.sourceforge.openutils.mgnlmedia.media.configuration.MediaTypeConfiguration;
import net.sourceforge.openutils.mgnlmedia.media.lifecycle.MediaModule;
import net.sourceforge.openutils.mgnlmedia.media.tags.el.MediaEl;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Utility Class that manages loading files into media repository
 * @author fgiust
 * @version $Id$
 */
public class MediaLoadUtils
{

    /**
     * Logger.
     */
    private static Logger log = LoggerFactory.getLogger(MediaLoadUtils.class);

    /**
     * Loads a media linking to an external video.
     * @param videourl absolute video url
     * @param parent media folder
     * @param filename video filename
     * @param overwrite overwrite an exxisting media
     * @return loaded media
     * @throws RepositoryException exception working on media repository
     * @throws IOException exception working with file stream
     */
    public static Content loadExternalVideo(String videourl, String parent, String filename, boolean overwrite)
        throws RepositoryException, IOException
    {
        log.debug("loading external video {}/{} with url {}", new Object[]{parent, filename, videourl });

        MediaTypeConfiguration mtc = MediaConfigurationManager.getInstance().getTypes().get("youtube");

        String cleanFilename = Path.getValidatedLabel(videourl);

        Content media = createMediaNode(mtc, parent, cleanFilename, overwrite);
        media.setNodeData("videoUrl", videourl);

        mtc.getHandler().onPostSave(media);

        return media;

    }

    /**
     * Loads a file in the media repository
     * @param inputStream file input stream
     * @param parent parent folder node
     * @param filename filename
     * @param overwrite overwrite if already exists?
     * @return create media node
     * @throws RepositoryException exception working on media repository
     * @throws IOException exception working with file stream
     */
    public static Content loadEntry(InputStream inputStream, String parent, String filename, boolean overwrite)
        throws RepositoryException, IOException
    {

        log.debug("loading image {} {}", parent, filename);

        String extension = StringUtils.substringAfterLast(filename, ".");
        String cleanFilename = StringUtils.substringBeforeLast(filename, ".");
        MediaTypeConfiguration mtc = MediaConfigurationManager.getMediaHandlerFromExtension(extension);

        if (mtc != null)
        {
            Content media = createMediaNode(mtc, parent, cleanFilename, overwrite);

            File f = File.createTempFile("entry", "." + extension);
            FileOutputStream fTemp = new FileOutputStream(f);

            IOUtils.copy(inputStream, fTemp);
            IOUtils.closeQuietly(fTemp);

            mtc.getHandler().saveFromZipFile(media, f, cleanFilename, extension);

            MgnlContext.getSystemContext().getHierarchyManager(MediaModule.REPO).save();
            FileUtils.deleteQuietly(f);

            return media;
        }
        return null;
    }

    /**
     * @param mtc
     * @param parent
     * @param filename
     * @param overwrite
     * @return
     * @throws RepositoryException
     * @throws AccessDeniedException
     */
    private static Content createMediaNode(MediaTypeConfiguration mtc, String parent, String filename, boolean overwrite)
        throws RepositoryException, AccessDeniedException
    {

        HierarchyManager mgr = MgnlContext.getSystemContext().getHierarchyManager(MediaModule.REPO);

        Content parentNode = getOrCreateFullPath(mgr, parent);
        String mediaName = Path.getValidatedLabel(filename);

        if (overwrite)
        {
            Content existing = parentNode.getChildByName(mediaName);
            if (existing != null)
            {
                existing.delete();
                mgr.save();
            }
        }

        Content media = mgr.createContent(
            parent,
            Path.getUniqueLabel(parentNode, mediaName),
            MediaConfigurationManager.MEDIA.getSystemName());

        setNodedataOnlyIfNotExisting(media, "creator", MgnlContext.getUser().getName());
        setNodedataOnlyIfNotExisting(media, "creationDate", Calendar.getInstance());

        NodeDataUtil.getOrCreateAndSet(media, "type", mtc.getName());
        NodeDataUtil.getOrCreateAndSet(media, "modificationDate", Calendar.getInstance());
        NodeDataUtil.getOrCreateAndSet(media, "modificationUser", MgnlContext.getUser().getName());

        if (MediaEl.module().isSingleinstance())
        {
            media.getMetaData().setActivated();
        }

        mgr.save();
        return media;
    }

    /**
     * @param media
     * @param key
     * @param value
     * @throws RepositoryException
     * @throws AccessDeniedException
     */
    private static void setNodedataOnlyIfNotExisting(Content media, String key, Object value)
        throws RepositoryException, AccessDeniedException
    {
        if (media.hasNodeData(key))
        {
            NodeDataUtil.getOrCreateAndSet(media, key, value);
        }
    }

    /**
     * Get the content node matching required path using hierarchy manager. If the required path doesn't exist create
     * it.
     * @param mgr hierarchy manager
     * @param path path to get or create
     * @return content to required path
     * @throws RepositoryException exception getting or creating path
     */
    public static Content getOrCreateFullPath(HierarchyManager mgr, String path) throws RepositoryException
    {
        String[] contentNodeNames = path.split("/");
        Content currContent = mgr.getRoot();
        for (String contentNodeName : contentNodeNames)
        {
            if (StringUtils.isNotEmpty(contentNodeName))
            {
                currContent = ContentUtil.getOrCreateContent(
                    currContent,
                    contentNodeName,
                    MediaConfigurationManager.FOLDER);

                if (MediaEl.module().isSingleinstance() && !currContent.getMetaData().getIsActivated())
                {
                    currContent.getMetaData().setActivated();
                }
            }
        }
        return currContent;

    }
}
