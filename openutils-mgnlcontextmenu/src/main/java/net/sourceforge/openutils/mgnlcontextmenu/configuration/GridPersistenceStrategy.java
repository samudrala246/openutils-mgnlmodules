/**
 *
 * ContextMenu Module for Magnolia CMS (http://www.openmindlab.com/lab/products/mgnlcontextmenu.html)
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

package net.sourceforge.openutils.mgnlcontextmenu.configuration;

import info.magnolia.cms.core.Content;
import info.magnolia.cms.core.NodeData;
import info.magnolia.cms.util.NodeDataUtil;
import info.magnolia.context.MgnlContext;
import it.openutils.mgnlutils.el.MgnlUtilsElFunctions;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.jcr.RepositoryException;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;


/**
 * @author dschivo
 * @version $Id$
 */
public class GridPersistenceStrategy extends PersistenceStrategy
{

    private static final String LOCAL_CACHE_KEY = GridPersistenceStrategy.class.getName() + ".localCache";

    private static final String GLOBAL_CACHE_KEY = GridPersistenceStrategy.class.getName() + ".globalCache";

    public static final String LOCAL_PROPERTY_NAME = "localGrid";

    public static final String GLOBAL_PROPERTY_NAME = "globalGrid";

    /**
     * {@inheritDoc}
     */
    @Override
    public String readEntry(Content node, String name, Scope scope)
    {
        switch (scope)
        {
            case local :
                return getLocalEntries(node).get(name);
            case global :
                return getGlobalEntries(node).get(name);
            default :
                return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeEntry(Content node, String name, String value, Scope scope) throws RepositoryException
    {
        switch (scope)
        {
            case local :
                update(node, LOCAL_PROPERTY_NAME, name, value, getLocalEntries(node), false);
                break;
            case global :
                Content globalNode = getGlobalNode(node);
                update(globalNode, GLOBAL_PROPERTY_NAME, name, value, getGlobalEntries(node), true);
                break;
        }
    }

    /**
     * Gets the global entries for the given node. Global entries are probably stored in an ancestor node (tipically the
     * website homepage) of the passed one, so they can be shared between many nodes. A cache is maintained in
     * application scope, and is refreshed after a modification occurs on the node storing the global entries.
     * @param node
     * @return
     */
    @SuppressWarnings("unchecked")
    protected Map<String, String> getGlobalEntries(Content node)
    {
        ServletContext servletContext = MgnlContext.getWebContext().getServletContext();
        Map map = (Map) servletContext.getAttribute(GLOBAL_CACHE_KEY);
        if (map == null)
        {
            map = new HashMap();
            servletContext.setAttribute(GLOBAL_CACHE_KEY, map);
        }
        Content globalNode = getGlobalNode(node);
        String mapKey = globalNode.getUUID();
        GlobalCache globalCache = (GlobalCache) map.get(mapKey);
        Calendar modificationDate = globalNode.getMetaData().getModificationDate();
        if (globalCache != null && globalCache.getCreationDate().compareTo(modificationDate) < 0)
        {
            globalCache = null;
        }
        if (globalCache == null)
        {
            globalCache = new GlobalCache(modificationDate);
            collectEntries(globalNode, GLOBAL_PROPERTY_NAME, globalCache.getEntries());
            map.put(mapKey, globalCache);
        }
        return globalCache.getEntries();
    }

    /**
     * Gets the global contents for the given node. A cache is maintained in request scope.
     * @param node
     * @return
     */
    @SuppressWarnings("unchecked")
    protected Map<String, String> getLocalEntries(Content node)
    {
        HttpServletRequest request = MgnlContext.getWebContext().getRequest();
        Map map = (Map) request.getAttribute(LOCAL_CACHE_KEY);
        if (map == null)
        {
            map = new HashMap();
            request.setAttribute(LOCAL_CACHE_KEY, map);
        }
        String mapKey = node.getUUID();
        Map<String, String> contents = (Map<String, String>) map.get(mapKey);
        if (contents == null)
        {
            contents = new HashMap<String, String>();
            collectEntries(node, LOCAL_PROPERTY_NAME, contents);
            map.put(mapKey, contents);
        }
        return contents;
    }

    private static void collectEntries(Content node, String propertyName, Map<String, String> entries)
    {
        String propertyValue = NodeDataUtil.getString(node, propertyName);
        if (!StringUtils.isEmpty(propertyValue))
        {
            for (String[] entry : MgnlUtilsElFunctions.splitAndTokenize(propertyValue))
            {
                if (entry.length >= 2)
                {
                    entries.put(entry[0], entry[1]);
                }
            }
        }
    }

    private void update(Content node, String propertyName, String name, String value, Map<String, String> entries,
        boolean updateMetaData) throws RepositoryException
    {
        value = StringUtils.remove(StringUtils.replaceChars(value, "\n\t", "  "), '\r');
        boolean changed = StringUtils.isEmpty(value) ? entries.containsKey(name) : !value.equals(entries.get(name));
        if (changed)
        {
            NodeData property = NodeDataUtil.getOrCreate(node, propertyName);
            String propertyValue = property.getString();
            String[][] array = StringUtils.isEmpty(propertyValue) ? new String[0][] : MgnlUtilsElFunctions
                .splitAndTokenize(propertyValue);
            StringBuilder sb = new StringBuilder();
            boolean found = false;
            for (int i = 0; i < array.length; i++)
            {
                if (StringUtils.equals(name, array[i][0]))
                {
                    found = true;
                    if (!StringUtils.isEmpty(value))
                    {
                        if (sb.length() > 0)
                        {
                            sb.append('\n');
                        }
                        sb.append(name).append('\t').append(value);
                    }
                }
                else
                {
                    if (sb.length() > 0)
                    {
                        sb.append('\n');
                    }
                    sb.append(array[i][0]).append('\t').append(array[i][1]);
                }
            }
            if (!found && !StringUtils.isEmpty(value))
            {
                if (sb.length() > 0)
                {
                    sb.append('\n');
                }
                sb.append(name).append('\t').append(value);
            }
            property.setValue(new String(sb));
            if (updateMetaData)
            {
                node.updateMetaData();
            }
        }
    }

    private static class GlobalCache
    {

        private final Calendar creationDate;

        private final Map<String, String> entries = new HashMap<String, String>();

        public GlobalCache(Calendar creationDate)
        {
            this.creationDate = creationDate;
        }

        /**
         * Returns the creationDate.
         * @return the creationDate
         */
        public Calendar getCreationDate()
        {
            return creationDate;
        }

        /**
         * Returns the entries.
         * @return the entries
         */
        public Map<String, String> getEntries()
        {
            return entries;
        }

    }

}
