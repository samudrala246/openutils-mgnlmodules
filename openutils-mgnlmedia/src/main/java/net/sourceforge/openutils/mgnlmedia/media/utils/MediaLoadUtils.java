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

import info.magnolia.cms.core.Path;
import info.magnolia.cms.security.AccessDeniedException;
import info.magnolia.context.MgnlContext;
import info.magnolia.context.SystemContext;
import info.magnolia.jcr.util.MetaDataUtil;
import info.magnolia.objectfactory.Components;
import it.openutils.mgnlutils.util.NodeUtilsExt;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

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
    public static Node loadExternalVideo(String videourl, String parent, String filename, boolean overwrite)
        throws RepositoryException, IOException
    {
        log.debug("loading external video {}/{} with url {}", new Object[]{parent, filename, videourl });

        MediaTypeConfiguration mtc = Components.getComponent(MediaConfigurationManager.class).getTypes().get("youtube");

        String cleanFilename = Path.getValidatedLabel(videourl);

        Node media = createMediaNode(mtc, parent, cleanFilename, overwrite);
        media.setProperty("videoUrl", videourl);

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
    public static Node loadEntry(InputStream inputStream, String parent, String filename, boolean overwrite)
        throws RepositoryException, IOException
    {

        log.debug("loading image {} {}", parent, filename);

        String extension = StringUtils.substringAfterLast(filename, ".");
        String cleanFilename = StringUtils.substringBeforeLast(filename, ".");
        MediaTypeConfiguration mtc = Components
            .getComponent(MediaConfigurationManager.class)
            .getMediaHandlerFromExtension(extension);

        if (mtc != null)
        {
            Node media = createMediaNode(mtc, parent, cleanFilename, overwrite);

            File f = File.createTempFile("entry", "." + extension);
            FileOutputStream fTemp = new FileOutputStream(f);

            IOUtils.copy(inputStream, fTemp);
            IOUtils.closeQuietly(fTemp);

            mtc.getHandler().saveFromZipFile(media, f, cleanFilename, extension);

            Components.getComponent(SystemContext.class).getJCRSession(MediaModule.REPO).save();
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
    private static Node createMediaNode(MediaTypeConfiguration mtc, String parent, String filename, boolean overwrite)
        throws RepositoryException, AccessDeniedException
    {

        Session session = MgnlContext.getJCRSession(MediaModule.REPO);

        Node parentNode = getOrCreateFullPath(session, parent);
        String mediaName = Path.getValidatedLabel(filename);

        if (overwrite)
        {
            if (parentNode.hasNode(mediaName))
            {
                Node existing = parentNode.getNode(mediaName);
                existing.remove();
                session.save();
            }

        }

        // [LB] FIXME TESTME
        Node media = parentNode.addNode(
            NodeUtilsExt.getUniqueLabel(parentNode, mediaName),
            MediaConfigurationManager.NT_MEDIA);

        if (!media.hasProperty("creator"))
        {
            media.setProperty("creator", MgnlContext.getUser().getName());
        }
        if (!media.hasProperty("creationDate"))
        {
            media.setProperty("creationDate", Calendar.getInstance());
        }

        media.setProperty("type", mtc.getName());
        media.setProperty("modificationDate", Calendar.getInstance());
        media.setProperty("modificationUser", MgnlContext.getUser().getName());

        if (MediaEl.module().isSingleinstance())
        {
            MetaDataUtil.getMetaData(media).setActivated();
        }

        session.save();
        return media;
    }

    /**
     * @param media
     * @param key
     * @param value
     * @throws RepositoryException
     * @throws AccessDeniedException
     */
    private static void setNodedataOnlyIfNotExisting(Node media, String key, String value) throws RepositoryException,
        AccessDeniedException
    {
        if (media.hasProperty(key))
        {
            media.setProperty(key, value);
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
    public static Node getOrCreateFullPath(Session session, String path) throws RepositoryException
    {
        String[] contentNodeNames = path.split("/");
        Node currContent = session.getRootNode();
        for (String contentNodeName : contentNodeNames)
        {
            if (StringUtils.isNotEmpty(contentNodeName))
            {

                if (currContent.hasNode(contentNodeName))
                {
                    currContent = currContent.getNode(contentNodeName);
                }
                else
                {
                    currContent = currContent.addNode(contentNodeName, MediaConfigurationManager.NT_FOLDER);

                    if (MediaEl.module().isSingleinstance() && !MetaDataUtil.getMetaData(currContent).getIsActivated())
                    {
                        MetaDataUtil.getMetaData(currContent).setActivated();
                    }
                }
            }

        }
        return currContent;

    }
}
