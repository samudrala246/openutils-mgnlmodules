/**
 *
 * Messages Module for Magnolia CMS (http://www.openmindlab.com/lab/products/messages.html)
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

package net.sourceforge.openutils.mgnlmessages.configuration;

import info.magnolia.cms.beans.config.ContentRepository;
import info.magnolia.cms.beans.config.ObservedManager;
import info.magnolia.cms.core.Content;
import info.magnolia.cms.core.HierarchyManager;
import info.magnolia.cms.core.ItemType;
import info.magnolia.cms.core.NodeData;
import info.magnolia.cms.util.ContentUtil;
import info.magnolia.cms.util.FactoryUtil;
import info.magnolia.cms.util.NodeDataUtil;
import info.magnolia.context.MgnlContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.inject.Singleton;
import javax.jcr.RepositoryException;

import net.sourceforge.openutils.mgnlmessages.lifecycle.MessagesModuleLifecycle;

import org.apache.commons.lang.StringUtils;


/**
 * @author molaschi
 */
@Singleton
public class MessagesConfigurationManager extends ObservedManager
{

    /**
     * Folder type
     */
    public static final ItemType FOLDER = ItemType.CONTENT;

    private List<Locale> locales = new ArrayList<Locale>();

    public static final String MESSAGES_REPO = "messages";

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onClear()
    {
        locales.clear();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    protected void onRegister(Content node)
    {
        if (node.getNodeDataCollection() != null)
        {
            locales.clear();

            for (Iterator iter = ContentUtil.getAllChildren(node).iterator(); iter.hasNext();)
            {
                Content langNode = (Content) iter.next();
                locales.add(new Locale(NodeDataUtil.getString(langNode, "language"), NodeDataUtil.getString(
                    langNode,
                    "country")));
            }

            Collections.sort(locales, new Comparator<Locale>()
            {

                /**
                 * {@inheritDoc}
                 */
                public int compare(Locale o1, Locale o2)
                {
                    if (o1.getLanguage().equals(o2.getLanguage()))
                    {
                        return o1.getCountry().compareTo(o2.getCountry());
                    }

                    return o1.getLanguage().compareTo(o2.getLanguage());
                }

            });
        }
    }

    public static MessagesConfigurationManager getInstance()
    {
        return (MessagesConfigurationManager) FactoryUtil.getSingleton(MessagesConfigurationManager.class);
    }

    public static List<Locale> getAvaiableLocales()
    {
        return getInstance().getLocales();
    }

    @SuppressWarnings("unchecked")
    public static List<String> getBaseNames()
    {
        HierarchyManager mgr = MgnlContext.getSystemContext().getHierarchyManager(ContentRepository.CONFIG);
        try
        {
            Content basenamesNode = mgr.getContent("/modules/messages/basenames");
            if (basenamesNode == null || !basenamesNode.hasChildren(ItemType.CONTENTNODE.getSystemName()))
            {
                return new ArrayList<String>();
            }
            List<String> basenames = new ArrayList<String>();
            for (Iterator it = basenamesNode.getChildren(ItemType.CONTENTNODE.getSystemName()).iterator(); it.hasNext();)
            {
                Content bn = (Content) it.next();
                basenames.add(NodeDataUtil.getString(bn, "basename"));
            }
            return basenames;
        }
        catch (RepositoryException e)
        {
            return new ArrayList<String>();
        }
    }

    public static void saveKeyValue(String key, String value, String locale) throws RepositoryException
    {
        HierarchyManager mgr = MgnlContext.getSystemContext().getHierarchyManager(MessagesModuleLifecycle.REPO);
        String path = "/" + StringUtils.replace(key, ".", "/");
        Content content = getOrCreateFullPath(mgr, path);

        if (!StringUtils.isEmpty(locale))
        {
            NodeData nd = NodeDataUtil.getOrCreate(content, locale);
            if (!StringUtils.isEmpty(value))
            {
                nd.setValue(value);
            }
            else
            {
                nd.delete();
            }
        }

        mgr.save();
    }

    private static Content getOrCreateFullPath(HierarchyManager mgr, String path) throws RepositoryException
    {
        try
        {
            return mgr.getContent(path);
        }
        catch (RepositoryException ex)
        {
            String parent = StringUtils.substringBeforeLast(path, "/");
            String label = StringUtils.substringAfterLast(path, "/");
            if (!StringUtils.isEmpty(parent))
            {
                getOrCreateFullPath(mgr, parent);
            }
            else
            {
                parent = "/";
            }

            return mgr.createContent(parent, label, ItemType.CONTENTNODE.getSystemName());
        }
    }

    /**
     * Returns the locales.
     * @return the locales
     */
    public List<Locale> getLocales()
    {
        return locales;
    }

    /**
     * Sets the languages.
     * @param locales the languages to set
     */
    public void setLocales(List<Locale> locales)
    {
        this.locales = locales;
    }

}
