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

import info.magnolia.cms.beans.config.URI2RepositoryManager;
import info.magnolia.cms.exchange.ActivationManagerFactory;
import info.magnolia.cms.security.Permission;
import info.magnolia.jcr.util.MetaDataUtil;
import info.magnolia.jcr.util.NodeUtil;
import info.magnolia.jcr.util.PropertyUtil;
import info.magnolia.repository.RepositoryConstants;

import java.util.List;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import net.sourceforge.openutils.mgnlmedia.media.configuration.MediaConfigurationManager;
import net.sourceforge.openutils.mgnlmedia.media.configuration.MediaTypeConfiguration;
import net.sourceforge.openutils.mgnlmedia.media.configuration.MediaUsedInManager;
import net.sourceforge.openutils.mgnlmedia.media.tags.el.MediaEl;
import net.sourceforge.openutils.mgnlmedia.media.types.MediaTypeHandler;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;


/**
 * @author dschivo
 * @version $Id$
 */
public class MediaBeanBuilder implements Function<Node, MediaBean>
{

    /**
     * Logger.
     */
    private Logger log = LoggerFactory.getLogger(getClass());

    /**
     * {@inheritDoc}
     */
    public MediaBean apply(Node media)
    {

        String mediatype = PropertyUtil.getString(media, "type");
        MediaTypeConfiguration mtc = MediaConfigurationManager.getInstance().getTypes().get(mediatype);
        MediaBean mb = new MediaBean();
        try
        {
            if (mtc == null)
            {
                log.warn("Skipping media {} with invalid media type \"{}\"", media.getPath(), mediatype);
                return null;
            }

            mb.setContent(media);
            mb.setMetaData(MetaDataUtil.getMetaData(media));
            mb.setHandle(media.getPath());
            mb.setName(PropertyUtil.getString(media, MediaTypeHandler.METADATA_NAME));
            mb.setFilename(mtc.getHandler().getFilename(media));
            // backward compatibility
            if (StringUtils.isEmpty(mb.getName()) && !StringUtils.isEmpty(mb.getFilename()))
            {
                int p = StringUtils.lastIndexOf(mb.getFilename(), '/');
                mb.setName(p != -1 ? mb.getFilename().substring(p + 1) : mb.getFilename());
            }
            mb.setTitle(mtc.getHandler().getTitle(media));
            mb.setThumbnailUrl(mtc.getHandler().getThumbnailUrl(media));
            mb.setPreviewUrl(mtc.getHandler().getPreviewUrl(media));
            mb.setDescription(mtc.getHandler().getDescription(media));
            mb.setDialog(mtc.getDialog());
            mb.setUuid(media.getIdentifier());
            mb.setType(mediatype);
            mb.setIcon(mtc.getMenuIcon());

            mb.setWritable(NodeUtil.isGranted(media.getParent(), Permission.SET));
            mb.setCanPublish(!MediaEl.module().isSingleinstance()
                && mb.isWritable()
                && ActivationManagerFactory.getActivationManager().hasAnyActiveSubscriber());

            Map<String, List<String>> workspacePaths = MediaUsedInManager.getInstance().getUsedInPaths(
                media.getIdentifier());
            mb.getUsedInWebPages().addAll(workspacePaths.get(RepositoryConstants.WEBSITE));
            for (Map.Entry<String, List<String>> entry : workspacePaths.entrySet())
            {
                String repository = entry.getKey();
                for (String handle : entry.getValue())
                {
                    String uri = URI2RepositoryManager.getInstance().getURI(repository, handle);
                    mb.getUsedInUris().add(uri);
                }
            }
        }
        catch (RepositoryException ex)
        {
            log.error("Exception caught", ex);
        }
        mb.setMediaInfo(mtc.getHandler().getMediaInfo(media));
        mb.setExternal(mtc.getHandler().isExternal(media));

        return mb;
    }
}
