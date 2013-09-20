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

package net.sourceforge.openutils.mgnlmedia.media.uri;

import info.magnolia.cms.beans.config.URI2RepositoryMapping;
import info.magnolia.cms.core.NodeData;
import info.magnolia.cms.util.ContentUtil;
import info.magnolia.context.MgnlContext;
import info.magnolia.link.Link;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import net.sourceforge.openutils.mgnlmedia.media.lifecycle.MediaModule;
import net.sourceforge.openutils.mgnlmedia.media.tags.el.MediaEl;
import net.sourceforge.openutils.mgnlmedia.media.utils.ImageUtils;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author fgiust
 * @version $Id$
 */
public class MediaURI2RepositoryMapping extends URI2RepositoryMapping
{

    /**
     * Logger.
     */
    private Logger log = LoggerFactory.getLogger(MediaURI2RepositoryMapping.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public String getURI(Link uuidLink)
    {
        String urisimple = uuidLink.getHandle();

        String uri;
        try
        {
            uri = MediaEl.url(MgnlContext.getHierarchyManager(getRepository()).getContent(urisimple));
        }
        catch (RepositoryException e)
        {
            log.warn(e.getClass().getName() + " resolving " + urisimple, e);
            uri = urisimple;

            if (StringUtils.isNotEmpty(getHandlePrefix()))
            {
                uri = StringUtils.removeStart(uri, getHandlePrefix());
            }
            if (StringUtils.isNotEmpty(getURIPrefix()))
            {
                uri = getURIPrefix() + "/" + uri;
            }
        }

        if (uri == null)
        {
            return null;
        }

        return cleanHandle(uri);
    }

    /**
     * Clean a handle. Remove double / and add always a leading /.
     */
    protected String cleanHandle(String handle)
    {
        if (!handle.startsWith("/"))
        {
            handle = "/" + handle;
        }
        while (handle.indexOf("//") != -1)
        {
            handle = StringUtils.replace(handle, "//", "/");
        }
        return handle;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getHandle(String uri)
    {
        String handle = super.getHandle(uri);
        if (MediaEl.module().isLazyResolutionCreation())
        {
            String resolutionsName = "resolutions";
            String search = "/" + resolutionsName + "/";
            int p = StringUtils.indexOf(handle, search);
            if (p != -1)
            {
                String mediaPath = handle.substring(0, p);
                Node mediaNode = ContentUtil.getContent(MediaModule.REPO, mediaPath);
                if (mediaNode != null)
                {
                    Node resolutionsNode = ContentUtil.getContent(mediaNode, resolutionsName);
                    String ndName = StringUtils.substringBefore(handle.substring(p + search.length()), "/");
                    if (resolutionsNode != null && !StringUtils.isEmpty(ndName))
                    {
                        NodeData nd = resolutionsNode.getNodeData(ndName);
                        String resolution = nd.getAttribute("resolutionNotYetCreated");
                        if (!StringUtils.isEmpty(resolution))
                        {
                            ImageUtils.checkOrCreateResolution(mediaNode, resolution, null);
                        }
                    }
                }
            }
        }
        return handle;
    }

}
