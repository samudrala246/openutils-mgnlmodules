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

package net.sourceforge.openutils.mgnlmedia.playlist.utils;

import info.magnolia.cms.core.Content;
import info.magnolia.cms.core.ItemType;
import info.magnolia.cms.util.NodeDataUtil;
import info.magnolia.context.MgnlContext;
import info.magnolia.jcr.util.ContentMap;
import info.magnolia.jcr.util.PropertyUtil;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Value;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import net.sourceforge.openutils.mgnlcriteria.jcr.query.AdvancedResult;
import net.sourceforge.openutils.mgnlcriteria.jcr.query.AdvancedResultItem;
import net.sourceforge.openutils.mgnlmedia.media.pages.SortMode;
import net.sourceforge.openutils.mgnlmedia.media.tags.el.MediaEl;
import net.sourceforge.openutils.mgnlmedia.playlist.PlaylistConstants;

import org.apache.commons.collections.IteratorUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.collect.Iterators;


/**
 * @author ADMIN
 * @version $Id: $
 */
public final class PlaylistIterateUtils
{

    /**
     * Logger.
     */
    private static Logger log = LoggerFactory.getLogger(PlaylistIterateUtils.class);

    /**
     * 
     */
    private PlaylistIterateUtils()
    {
    }

    /**
     * @param obj
     * @return
     */
    @SuppressWarnings("unchecked")
    public static Iterator<MediaNodeAndEntryPath> iterate(final Node playlistNode)
    {
        if (playlistNode == null)
        {
            return IteratorUtils.emptyIterator();
        }

        try
        {
            if (playlistNode.hasNode("search"))
            {
                Node searchNode = playlistNode.getNode("search");
                Collection<Content> paramNodes = searchNode.getChildren(ItemType.CONTENTNODE);
                final Map<String, Object> map = new HashMap<String, Object>();
                for (Content paramNode : paramNodes)
                {
                    String paramName = NodeDataUtil.getString(paramNode, "name");
                    Value[] jcrValues = paramNode.getNodeData("value").getValues();
                    String[] paramValues = new String[jcrValues.length];
                    for (int i = 0; i < jcrValues.length; i++)
                    {
                        paramValues[i] = jcrValues[i].getString();
                    }
                    if (paramValues.length == 1)
                    {
                        map.put(paramName, paramValues[0]);
                    }
                    else if (paramValues.length > 1)
                    {
                        map.put(paramName, paramValues);
                    }
                }

                Property maxresultProperty = PropertyUtil.getPropertyOrNull(playlistNode, "maxResults");
                int maxresults = maxresultProperty != null ? (int) maxresultProperty.getLong() : 0;
                AdvancedResult searchResult = MediaEl
                    .module()
                    .getSearch()
                    .search(
                        new CustomParamsRequest(MgnlContext.getWebContext().getRequest(), map, false),
                        null,
                        "/",
                        true,
                        SortMode.SCORE,
                        maxresults,
                        1);
                return Iterators.transform(
                    searchResult.getItems(),
                    new Function<AdvancedResultItem, MediaNodeAndEntryPath>()
                    {

                        /**
                         * {@inheritDoc}
                         */
                        public MediaNodeAndEntryPath apply(AdvancedResultItem from)
                        {
                            return new MediaNodeAndEntryPath(from, null);
                        }
                    });
            }
            else
            {
                return Iterators.transform(
                    playlistNode.getChildren(PlaylistConstants.PLAYLIST_ENTRY).iterator(),
                    new Function<AdvancedResultItem, MediaNodeAndEntryPath>()
                    {

                        /**
                         * {@inheritDoc}
                         */
                        public MediaNodeAndEntryPath apply(AdvancedResultItem playlistEntry)
                        {
                            String mediaUUID = PropertyUtil.getString(playlistEntry, "media");
                            Content mediaNode = MediaEl.node(mediaUUID);
                            if (mediaNode == null)
                            {
                                log.warn(
                                    "Node {} referenced by entry {} of playlist {} does not exist in media repository",
                                    new Object[]{mediaUUID, playlistEntry.getName(), playlistNode.getPath() });
                            }

                            return new MediaNodeAndEntryPath(mediaNode, playlistEntry.getPath());
                        }
                    });
            }
        }
        catch (RepositoryException e)
        {
            // should never happen
            return null;
        }
    }

    public static class MediaNodeAndEntryPath
    {

        private final AdvancedResultItem mediaNode;

        private final String playlistEntryPath;

        /**
         * 
         */
        public MediaNodeAndEntryPath(AdvancedResultItem mediaNode, String playlistEntryPath)
        {
            this.mediaNode = mediaNode;
            this.playlistEntryPath = playlistEntryPath;
        }

        /**
         * Returns the mediaNode.
         * @return the mediaNode
         */
        public AdvancedResultItem getMediaNode()
        {
            return mediaNode;
        }

        /**
         * Returns the playlistEntryPath.
         * @return the playlistEntryPath
         */
        public String getPlaylistEntryPath()
        {
            return playlistEntryPath;
        }
    }

    // freemarker.ext.servlet.IncludePage.CustomParamsRequest
    private static final class CustomParamsRequest extends HttpServletRequestWrapper
    {

        private final HashMap paramsMap;

        private CustomParamsRequest(HttpServletRequest request, Map paramMap, boolean inheritParams)
        {
            super(request);
            paramsMap = inheritParams ? new HashMap(request.getParameterMap()) : new HashMap();
            for (Iterator it = paramMap.entrySet().iterator(); it.hasNext();)
            {
                Map.Entry entry = (Map.Entry) it.next();
                String name = String.valueOf(entry.getKey());
                Object value = entry.getValue();
                final String[] valueArray;
                if (value == null)
                {
                    // Null values are explicitly added (so, among other
                    // things, we can hide inherited param values).
                    valueArray = new String[]{null };
                }
                else if (value instanceof String[])
                {
                    // String[] arrays are just passed through
                    valueArray = (String[]) value;
                }
                else if (value instanceof Collection)
                {
                    // Collections are converted to String[], with
                    // String.valueOf() used on elements
                    Collection col = (Collection) value;
                    valueArray = new String[col.size()];
                    int i = 0;
                    for (Iterator it2 = col.iterator(); it2.hasNext();)
                    {
                        valueArray[i++] = String.valueOf(it2.next());
                    }
                }
                else if (value.getClass().isArray())
                {
                    // Other array types are too converted to String[], with
                    // String.valueOf() used on elements
                    int len = Array.getLength(value);
                    valueArray = new String[len];
                    for (int i = 0; i < len; ++i)
                    {
                        valueArray[i] = String.valueOf(Array.get(value, i));
                    }
                }
                else
                {
                    // All other values (including strings) are converted to a
                    // single-element String[], with String.valueOf applied to
                    // the value.
                    valueArray = new String[]{String.valueOf(value) };
                }
                String[] existingParams = (String[]) paramsMap.get(name);
                int el = existingParams == null ? 0 : existingParams.length;
                if (el == 0)
                {
                    // No original params, just put our array
                    paramsMap.put(name, valueArray);
                }
                else
                {
                    int vl = valueArray.length;
                    if (vl > 0)
                    {
                        // Both original params and new params, prepend our
                        // params to original params
                        String[] newValueArray = new String[el + vl];
                        System.arraycopy(valueArray, 0, newValueArray, 0, vl);
                        System.arraycopy(existingParams, 0, newValueArray, vl, el);
                        paramsMap.put(name, newValueArray);
                    }
                }
            }
        }

        @Override
        public String[] getParameterValues(String name)
        {
            String[] value = ((String[]) paramsMap.get(name));
            return value != null ? (String[]) value.clone() : null;
        }

        @Override
        public String getParameter(String name)
        {
            String[] values = (String[]) paramsMap.get(name);
            return values != null && values.length > 0 ? values[0] : null;
        }

        @Override
        public Enumeration getParameterNames()
        {
            return Collections.enumeration(paramsMap.keySet());
        }

        @Override
        public Map getParameterMap()
        {
            HashMap clone = (HashMap) paramsMap.clone();
            for (Iterator it = clone.entrySet().iterator(); it.hasNext();)
            {
                Map.Entry entry = (Map.Entry) it.next();
                entry.setValue(((String[]) entry.getValue()).clone());
            }
            return Collections.unmodifiableMap(clone);
        }
    }
}
