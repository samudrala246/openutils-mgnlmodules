/**
 *
 * E-learning Module for Magnolia CMS (http://www.openmindlab.com/lab/products/lms.html)
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
package net.sourceforge.openutils.mgnllms.lms.uri;

import info.magnolia.cms.beans.config.URI2RepositoryMapping;
import info.magnolia.cms.core.Content;
import info.magnolia.context.MgnlContext;
import info.magnolia.link.Link;

import java.util.Collections;
import java.util.Map;

import javax.jcr.RepositoryException;

import net.sourceforge.openutils.mgnllms.lms.configuration.LmsTypeConfiguration;
import net.sourceforge.openutils.mgnllms.module.LmsTypesManager;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author molaschi
 * @version $Id: $
 */
public class LmsURI2RepositoryMapping extends URI2RepositoryMapping
{

    /**
     * Logger.
     */
    private Logger log = LoggerFactory.getLogger(LmsURI2RepositoryMapping.class);

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
            uri = getUrl(MgnlContext.getHierarchyManager(getRepository()).getContent(urisimple));
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

        return cleanHandle(uri);
    }

    /**
     * Clean a handle. Remove double / and add always a leading /
     * @param handle
     * @return
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
     * Get url for a media
     * @param node media
     * @return url
     */
    public static String getUrl(Content node)
    {
        return getUrl2(node, Collections.<String, String> emptyMap());
    }

    /**
     * Get url for a media
     * @param node media
     * @return url
     */
    public static String getUrl2(Content node, Map<String, String> options)
    {
        if (node == null)
        {
            return null;
        }
        LmsTypeConfiguration mtc = LmsTypesManager.getInstance().getTypeConfigurationFrom(node);
        return mtc.getHandler().getUrl(node, options);
    }

}
