/**
 *
 * Tagcloud module for Magnolia CMS (http://www.openmindlab.com/lab/products/mgnltagcloud.html)
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

package net.sourceforge.openutils.mgnltagcloud.el;

import info.magnolia.objectfactory.Components;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import net.sourceforge.openutils.mgnltagcloud.bean.TagCloud;
import net.sourceforge.openutils.mgnltagcloud.manager.TagCloudManager;


/**
 * El functions on tagClouds
 * @author molaschi
 * @version $Id$
 */
public class TagCloudElFunctions
{

    private static final String PROP_MIN = "min";

    private static final String PROP_MAX = "max";

    private static final String PROP_COUNT = "count";

    /**
     * Get tagCloud by name
     * @param name of the tagCloud
     * @return map of tags
     */
    public static Map<String, Integer> named(String name)
    {
        TagCloud tagCloud = Components.getComponent(TagCloudManager.class).getTagCloud(name);
        return tagCloud != null ? tagCloud.getTags() : new HashMap<String, Integer>();
    }

    /**
     * Get a tagCloud
     * @param repository content
     * @param path of search
     * @param propertyName name of tag
     * @param count how many tags
     * @return map of tags
     */
    public static Map<String, Integer> notcached(String repository, String path, String propertyName, int count)
    {
        return internalGetTagCloud(repository, path, propertyName, count, false);
    }

    /**
     * Get a tagCloud and cache it
     * @param repository content
     * @param path of search
     * @param propertyName name of tag
     * @param count how many tags
     * @return map of tags
     */
    public static Map<String, Integer> cached(String repository, String path, String propertyName, int count)
    {
        return internalGetTagCloud(repository, path, propertyName, count, true);
    }

    /**
     * Get a tagCloud and cache it
     * @param repository content
     * @param path of search
     * @param propertyName name of tag
     * @param count how many tags
     * @return map of tags
     */
    private static Map<String, Integer> internalGetTagCloud(String repository, String path, String propertyName,
        int count, boolean cacheAndObserve)
    {
        TagCloud tagCloud = new TagCloud();
        tagCloud.setRepository(repository);
        tagCloud.setPath(path);
        tagCloud.setPropertyName(propertyName);
        tagCloud.setCount(count);
        tagCloud.setName(tagCloud.toString());
        tagCloud.setEnabled(true);
        tagCloud.setCacheAndObserve(cacheAndObserve);

        if (cacheAndObserve)
        {
            tagCloud = Components.getComponent(TagCloudManager.class).checkForTagCloud(tagCloud);
        }
        else
        {
            Components.getComponent(TagCloudManager.class).calculateTagCloud(tagCloud);
        }

        return tagCloud != null ? sortbyname(tagCloud.getTags(), true) : new HashMap<String, Integer>();
    }

    /**
     * Sort a tag cloud by tag name
     * @param tags tag cloud to order
     * @param ascending sort direction
     * @return sorted tag cloud
     */
    public static Map<String, Integer> sortbyname(Map<String, Integer> tags, final boolean ascending)
    {
        return sort(tags, new Comparator<String>()
        {

            public int compare(String o1, String o2)
            {
                int compareResult = o1.compareToIgnoreCase(o2) * (ascending ? 1 : -1);
                if (compareResult == 0)
                {
                    return o1.compareTo(o2) * (ascending ? 1 : -1);
                }
                return compareResult;
            }

        });
    }

    /**
     * Sort a tag cloud by tag count
     * @param tags tag cloud to order
     * @param ascending sort direction
     * @return sorted tag cloud
     */
    public static Map<String, Integer> sortbycount(Map<String, Integer> tags, boolean ascending)
    {
        return sort(tags, new ValueComparer<String, Integer>(tags, false, ascending));
    }

    /**
     * Sort a tag cloud by comparator
     * @param tags tag cloud to order
     * @param c comparator to apply
     * @return sorted tag cloud
     */
    private static Map<String, Integer> sort(Map<String, Integer> tags, Comparator<String> c)
    {
        SortedMap<String, Integer> map = new TreeMap<String, Integer>(c);
        map.putAll(tags);
        return map;
    }

    /**
     * Get tag cloud properties (min value, max value, count)
     * @param tagCloud tagCloud
     * @return tag cloud properties
     */
    public static Map<String, Integer> props(Map<String, Integer> tagCloud)
    {
        Map<String, Integer> sorted = sortbycount(tagCloud, true);
        Integer[] values = sorted.values().toArray(new Integer[sorted.size()]);

        Map<String, Integer> properties = new HashMap<String, Integer>(3);
        properties.put(PROP_MIN, values[0]);
        properties.put(PROP_MAX, values[values.length - 1]);
        properties.put(PROP_COUNT, sorted.size());
        return properties;
    }

    /**
     * Comparator by key or value
     * @author molaschi
     * @version $Id$
     * @param <K> key type
     * @param <V> value type
     */
    public static class ValueComparer<K extends Comparable<K>, V extends Comparable<V>> implements Comparator<K>
    {

        private boolean orderByKey;

        private boolean ascending = true;

        private Map<K, V> map;

        public ValueComparer(Map<K, V> map, boolean orderByKey, boolean ascending)
        {
            this.map = map;
            this.orderByKey = orderByKey;
            this.ascending = ascending;
        }

        /**
         * {@inheritDoc}
         */
        public int compare(K key1, K key2)
        {
            if (!orderByKey)
            {
                int c = compareByValue(key1, key2) * (ascending ? 1 : -1);
                if (c != 0)
                {
                    return c;
                }
            }

            return key1.compareTo(key2) * (ascending ? 1 : -1);
        }

        private int compareByValue(K key1, K key2)
        {
            V value1 = this.map.get(key1);
            V value2 = this.map.get(key2);
            int c = value1.compareTo(value2) * (ascending ? 1 : -1);
            return c;
        }
    }

}
