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

package net.sourceforge.openutils.mgnlmedia.media.tags.el;

import info.magnolia.cms.beans.runtime.FileProperties;
import info.magnolia.cms.core.Content;
import info.magnolia.cms.core.DefaultContent;
import info.magnolia.cms.core.NodeData;
import info.magnolia.cms.i18n.I18nContentWrapper;
import info.magnolia.cms.util.ContentUtil;
import info.magnolia.cms.util.NodeMapWrapper;
import info.magnolia.context.MgnlContext;
import info.magnolia.jcr.util.ContentMap;
import info.magnolia.jcr.util.PropertyUtil;
import info.magnolia.module.ModuleRegistry;
import info.magnolia.objectfactory.Components;
import it.openutils.mgnlutils.api.NodeUtilsExt;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;
import javax.jcr.Value;
import javax.jcr.query.InvalidQueryException;
import javax.servlet.http.HttpServletRequest;

import net.sourceforge.openutils.mgnlmedia.media.configuration.MediaConfigurationManager;
import net.sourceforge.openutils.mgnlmedia.media.configuration.MediaTypeConfiguration;
import net.sourceforge.openutils.mgnlmedia.media.lifecycle.MediaModule;
import net.sourceforge.openutils.mgnlmedia.media.types.MediaTypeHandler;
import net.sourceforge.openutils.mgnlmedia.media.types.impl.BaseTypeHandler;
import net.sourceforge.openutils.mgnlmedia.media.utils.ImageUtils;
import net.sourceforge.openutils.mgnlmedia.playlist.PlaylistConstants;
import net.sourceforge.openutils.mgnlmedia.playlist.utils.PlaylistIterateUtils;
import net.sourceforge.openutils.mgnlmedia.playlist.utils.PlaylistIterateUtils.MediaNodeAndEntryPath;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterators;


/**
 * Class that holds media el functions methods
 * @author molaschi
 * @version $Id$
 */
public final class MediaEl
{

    private static MediaConfigurationManager mcm = MediaConfigurationManager.getInstance();

    private static Logger log = LoggerFactory.getLogger(MediaEl.class);

    private static final String[] EMPTY_STRING_ARRAY = new String[]{};

    /**
     * Public constructor. Needed for freemarker support
     */
    public MediaEl()
    {

    }

    /**
     * Get the media module instance
     * @return media module instance
     */
    public static MediaModule module()
    {
        return (MediaModule) Components.getComponent(ModuleRegistry.class).getModuleInstance(MediaModule.NAME);
    }

    /**
     * Get content node for media
     * @param obj content node or node UUID or jcr absolute path in media repository
     * @return content node
     */
    public static Node node(Object obj)
    {
        if (obj == null)
        {
            return null;
        }

        Node node = it.openutils.mgnlutils.el.MgnlUtilsElFunctions.node(obj, MediaModule.REPO);

        if (node != null)
        {
            // return Components.getComponent(NodeWrapperFactory.class).wrapNode(node);
            // GRAVE: Servlet.service() for servlet default threw exception
            // info.magnolia.objectfactory.NoSuchComponentException: No component configuration for type
            // [info.magnolia.jcr.wrapper.NodeWrapperFactory] found. Please add a configuration to your module
            // descriptor.
            Content currentpage = null;

            if (MgnlContext.isWebContext())
            {
                currentpage = MgnlContext.getAggregationState().getMainContent();
            }

            if (currentpage == null)
            {
                currentpage = new DefaultContent(node);
            }

            Content content = new NodeMapWrapper(
                new I18nContentWrapper(new DefaultContent(node)),
                currentpage.getHandle());
            return content.getJCRNode();
        }

        return null;
    }

    /**
     * Get url for a media
     * @param media media
     * @return url
     */
    public static String url(Object media)
    {
        return urlParams(node(media), Collections.<String, String> emptyMap());
    }

    /**
     * Get url for a media, passing some parameters
     * @param mediaref media node or UUID
     * @param options optional parameters
     * @return url
     */
    public static String urlParams(Object mediaref, Map<String, String> options)
    {
        Node media = node(mediaref);
        if (media == null)
        {
            return null;
        }
        MediaTypeConfiguration mtc = mcm.getMediaTypeConfigurationFromMedia(media);
        if (mtc == null || mtc.getHandler() == null)
        {
            return null;
        }
        return appendBaseUrl(mtc.getHandler().getUrl(media, options));
    }

    /**
     * Get url to thumbnail
     * @param mediaref media node or UUID
     * @return the thumbnail url for this media, null otherwise
     */
    public static String thumbnail(Object mediaref)
    {

        Node media = node(mediaref);

        if (media == null)
        {
            return null;
        }
        MediaTypeConfiguration mtc = mcm.getMediaTypeConfigurationFromMedia(media);
        if (mtc == null || mtc.getHandler() == null)
        {
            return null;
        }
        return mtc.getHandler().getThumbnailUrl(media);
    }

    /**
     * Get media type
     * @param media media to get the type
     * @return the type of this media if existing, null otherwise
     */
    public static String type(Object media)
    {
        if (media == null)
        {
            return null;
        }
        return PropertyUtil.getString(node(media), "type");
    }

    /**
     * Get all resolution strings (i.e. 'o200x350;background=45A97B') that generates cached resolutions
     * @param mediaref media node or UUID
     * @return all resolution strings
     */
    public static String[] resolutions(Object mediaref)
    {

        Node media = node(mediaref);

        List<String> res = new ArrayList<String>();

        Content resolutions = ContentUtil.asContent(getResolutionsNode(media));

        Collection<NodeData> nodeDataCollection = resolutions.getNodeDataCollection();

        for (NodeData item : nodeDataCollection)
        {
            if (item.getName().startsWith("res-"))
            {
                if (item.getAttribute(ImageUtils.RESOLUTION_PROPERTY) != null)
                {
                    res.add(item.getAttribute(ImageUtils.RESOLUTION_PROPERTY));
                }
                else
                {
                    res.add(StringUtils.substringAfter(item.getName(), "-"));
                }
            }
        }

        return res.toArray(new String[res.size()]);

    }

    /**
     * Get url for a resolution
     * @param mediaref media node or UUID
     * @param resolution resolution
     * @return url
     */
    public static String urlres(Object mediaref, String resolution)
    {

        Node media = node(mediaref);

        if (media == null)
        {
            return null;
        }

        try
        {
            // MEDIA-90 may be simply a url
            if (media.getProperty(BaseTypeHandler.ORGINAL_NODEDATA_NAME).getType() == PropertyType.BINARY)
            {
                FileProperties prop = new FileProperties(
                    ContentUtil.asContent(media),
                    BaseTypeHandler.ORGINAL_NODEDATA_NAME);
                Integer width = NumberUtils.toInt(prop.getProperty(FileProperties.PROPERTY_WIDTH));
                Integer height = NumberUtils.toInt(prop.getProperty(FileProperties.PROPERTY_HEIGHT));
                Point size = ImageUtils.parseForSize(resolution);
                if (width == size.x && height == size.y)
                {
                    return appendBaseUrl(mcm.getURIMappingPrefix() + prop.getProperty(FileProperties.PATH));
                }
            }

            if (!ImageUtils.checkOrCreateResolution(media, resolution, null, module().isLazyResolutionCreation()))
            {
                return null;
            }

            Node resolutions = getResolutionsNode(media);
            String resString = "res-" + ImageUtils.getResolutionPath(resolution);
            if (resolutions != null && resolutions.hasProperty(resString))
            {
                String resPath = new FileProperties(ContentUtil.asContent(resolutions), resString)
                    .getProperty(FileProperties.PATH);

                return appendBaseUrl(mcm.getURIMappingPrefix() + resPath);

            }
        }
        catch (RepositoryException e)
        {
            log.debug(e.getMessage(), e);
        }
        return null;
    }

    /**
     * Get size of an image
     * @param mediaref media node or UUID
     * @param resolution resolution for witch calculate size
     * @return size of an image
     */
    public static int[] size(Object mediaref, String resolution)
    {

        Node media = node(mediaref);

        if (media != null)
        {

            Property res = null;
            if ("original".equals(resolution))
            {
                res = PropertyUtil.getPropertyOrNull(media, "original");
            }
            else
            {
                Node resolutions = getResolutionsNode(media);
                if (resolutions != null)
                {
                    try
                    {
                        if (resolutions.hasProperty(ImageUtils.getResolutionPath("res-" + resolution)))
                        {
                            res = resolutions.getProperty(ImageUtils.getResolutionPath("res-" + resolution));
                        }
                    }
                    catch (RepositoryException e)
                    {
                        // do nothing
                    }
                }
            }

            if (res != null)
            {
                return new int[]{
                    NumberUtils.toInt(NodeUtilsExt.getAttribute(res, FileProperties.PROPERTY_WIDTH)),
                    NumberUtils.toInt(NodeUtilsExt.getAttribute(res, FileProperties.PROPERTY_HEIGHT)) };
            }
            else
            {
                // MEDIA-231
                res = PropertyUtil.getPropertyOrNull(media, "original");
                if (res != null)
                {
                    Point size = ImageUtils.parseForSize(resolution);
                    if (NumberUtils.toInt(NodeUtilsExt.getAttribute(res, FileProperties.PROPERTY_WIDTH)) == size.x
                        && NumberUtils.toInt(NodeUtilsExt.getAttribute(res, FileProperties.PROPERTY_HEIGHT)) == size.y)
                    {
                        return new int[]{size.x, size.y };
                    }
                }

            }
        }

        return new int[]{-1, -1 };
    }

    /**
     * Get url to media preview
     * @param mediaref media node or UUID
     * @return the preview url for this media if existing, null otherwise
     */
    public static String preview(Object mediaref)
    {

        Node media = node(mediaref);

        if (media == null)
        {
            return null;
        }
        MediaTypeConfiguration mtc = mcm.getMediaTypeConfigurationFromMedia(media);
        if (mtc == null || mtc.getHandler() == null)
        {
            return null;
        }
        return mtc.getHandler().getPreviewUrl(media);
    }

    /**
     * Get an array of String(s) containing a list of web pages where this media is used, an empty array otherwise
     * @param mediaref media node or UUID
     * @return an array of String(s) containing a list of web pages where this media is used, an empty array otherwise
     */
    public static String[] usedInWebPages(Object mediaref)
    {

        Node media = node(mediaref);

        if (media == null)
        {
            log.warn("findMediaUsedInWebPages called with a null media");
            return new String[]{};
        }
        try
        {
            List<String> retVal = mcm.getUsedInWebPages(media.getIdentifier());
            return (CollectionUtils.isNotEmpty(retVal) ? retVal.toArray(new String[retVal.size()]) : EMPTY_STRING_ARRAY);
        }
        catch (InvalidQueryException e)
        {
            log.error("JCR Query returned the following error: ", e);
            // ignore this and return an empty List
        }
        catch (RepositoryException e)
        {
            log.error("JCR Repository returned the following error: ", e);
            // ignore this and return an empty List
        }
        return EMPTY_STRING_ARRAY;
    }

    /**
     * Returns a property (nodeData) of the media Content.
     * @param mediaref media node or UUID
     * @param property property name
     * @return the value of the given nodedata or null if not found
     */
    public static Object property(Object mediaref, String property)
    {

        Node media = node(mediaref);

        try
        {
            if (media == null || !media.hasProperty(property))
            {
                return null;
            }
        }
        catch (RepositoryException e)
        {
            // return null;
        }

        try
        {
            return getValueAsObject(media.getProperty(property).getValue());
        }
        catch (RepositoryException e)
        {
            log.debug("RepositoryException reading property " + property + " from " + media, e);
        }

        return null;

    }

    /**
     * Returns the width of the <strong>original</strong> media, if available.
     * @param media media Content
     * @return width of the original media, if available
     */
    public static Integer width(Object media)
    {
        Number longproperty = (Number) property(media, MediaTypeHandler.METADATA_WIDTH);
        if (longproperty != null)
        {
            return longproperty.intValue();
        }
        return null;
    }

    /**
     * Returns the height of the <strong>original</strong> media, if available.
     * @param media media Content
     * @return height of the original media, if available
     */
    public static Integer height(Object media)
    {
        Number longproperty = (Number) property(media, MediaTypeHandler.METADATA_HEIGHT);
        if (longproperty != null)
        {
            return longproperty.intValue();
        }
        return null;
    }

    /**
     * Private method, not an EL function
     */
    private static Object getValueAsObject(Value value)
    {
        try
        {
            switch (value.getType())
            {
                case (PropertyType.STRING) :
                    return value.getString();
                case (PropertyType.DOUBLE) :
                    return value.getDouble();
                case (PropertyType.LONG) :
                    return value.getLong();
                case (PropertyType.BOOLEAN) :
                    return value.getBoolean();
                case (PropertyType.DATE) :
                    return value.getDate();
                case (PropertyType.BINARY) :
                    // don't return
                default :
                    return null;
            }
        }
        catch (Exception e)
        {
            log.debug("Exception caught: " + e.getMessage(), e);
        }
        return null;
    }

    /**
     * Returns the "resolutions" node, checking for existence
     * @param media
     * @return
     */
    protected static Node getResolutionsNode(final Node media)
    {
        Node resolutions = null;

        try
        {
            if (media.hasNode("resolutions"))
            {
                resolutions = media.getNode("resolutions");
            }
        }
        catch (RepositoryException e)
        {
            // ignore, try to create it
        }
        return resolutions;
    }

    /**
     * Returns an interator on the playlist content, given the playlist node or UUID. Support both static and dynamic
     * (search based) playlists
     * @param obj playlist node or UUID
     * @return Iterator of media nodes
     */
    public static Iterator<ContentMap> mediaNodesInPlaylist(Object obj)
    {
        if (obj == null)
        {
            return null;
        }

        Node playlistNode = it.openutils.mgnlutils.el.MgnlUtilsElFunctions.node(obj, PlaylistConstants.REPO);

        Iterator<ContentMap> iter = Iterators.transform(
            PlaylistIterateUtils.iterate(playlistNode),
            new Function<MediaNodeAndEntryPath, ContentMap>()
            {

                /**
                 * {@inheritDoc}
                 */
                public ContentMap apply(MediaNodeAndEntryPath from)
                {
                    return (ContentMap) from.getMediaNode();
                }
            });
        return Iterators.filter(iter, Predicates.notNull());
    }

    /**
     * Utility functions used to replace a param in the current URL, Used internally by the crop tag.
     * @param param parm key
     * @param newValue new value
     * @return new URL
     */
    public static String replaceParam(String param, String newValue)
    {
        HttpServletRequest req = MgnlContext.getWebContext().getRequest();
        String url = "?"
            + (StringUtils.isNotBlank(req.getQueryString())
                ? MgnlContext.getWebContext().getRequest().getQueryString()
                : StringUtils.EMPTY);
        if (url.indexOf("?" + param + "=") >= 0 || url.indexOf("&" + param + "=") >= 0)
        {
            int index = 1;
            if (url.indexOf("&" + param + "=") >= 0)
            {
                index = url.indexOf("&" + param + "=");
            }
            url = StringUtils.substring(url, 0, index)
                + StringUtils.substringAfter(StringUtils.substring(url, index + 1), "&");
        }
        if (StringUtils.isNotBlank(newValue))
        {
            url += (url.endsWith("?") ? StringUtils.EMPTY : "&") + param + "=" + newValue;
        }
        return url;
    }

    private static String appendBaseUrl(String url)
    {
        String baseurl = module().getBaseurl();
        if (baseurl != null && StringUtils.isNotEmpty(url) && !StringUtils.contains(url, "://"))
        {
            return baseurl + url;
        }
        return url;
    }

}
