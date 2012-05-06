/**
 *
 * E-learning Module for Magnolia CMS (http://www.openmindlab.com/lab/products/lms.html)
 * Copyright(C) 2010-2011, Openmind S.r.l. http://www.openmindonline.it
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

package net.sourceforge.openutils.mgnllms.module;

import info.magnolia.cms.beans.config.ObservedManager;
import info.magnolia.cms.beans.config.URI2RepositoryManager;
import info.magnolia.cms.beans.config.URI2RepositoryMapping;
import info.magnolia.cms.core.Content;
import info.magnolia.cms.core.ItemType;
import info.magnolia.cms.core.search.Query;
import info.magnolia.cms.core.search.QueryManager;
import info.magnolia.cms.core.search.QueryResult;
import info.magnolia.cms.util.ContentUtil;
import info.magnolia.cms.util.NodeDataUtil;
import info.magnolia.context.MgnlContext;
import info.magnolia.objectfactory.Components;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Singleton;
import javax.jcr.RepositoryException;

import net.sourceforge.openutils.mgnllms.lms.configuration.LmsTypeConfiguration;
import net.sourceforge.openutils.mgnllms.lms.types.LmsTypeHandler;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author luca boati
 */
@Singleton
public class LmsTypesManager extends ObservedManager
{

    private Logger log = LoggerFactory.getLogger(LmsTypesManager.class);

    private static final String MGNL_COURSE_TYPE = "mgnl:course";

    /**
     * Folder type
     */
    public static final ItemType FOLDER = ItemType.CONTENT;

    /**
     * Course type
     */
    public static final ItemType COURSE = new ItemType(MGNL_COURSE_TYPE);

    private Map<String, LmsTypeConfiguration> types = new LinkedHashMap<String, LmsTypeConfiguration>();

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onClear()
    {
        types.clear();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    protected void onRegister(Content node)
    {
        for (Iterator iter = ContentUtil.getAllChildren(node).iterator(); iter.hasNext();)
        {
            Content typeNode = (Content) iter.next();

            try
            {
                LmsTypeConfiguration conf = new LmsTypeConfiguration();
                conf.setName(typeNode.getName());
                conf.setDialog(NodeDataUtil.getString(typeNode, "dialog"));
                conf.setLabel(NodeDataUtil.getString(typeNode, "label"));
                conf.setMenuIcon(NodeDataUtil.getString(typeNode, "menuIcon"));
                String classNameHandler = NodeDataUtil.getString(typeNode, "handler");
                Class classHandler = Class.forName(classNameHandler);
                if (!LmsTypeHandler.class.isAssignableFrom(classHandler))
                {
                    log.error(
                        "Error getting lms type configuration for {}: handler class {} not implements LmsTypeHandler",
                        typeNode.getHandle(),
                        classHandler);
                    continue;
                }
                conf.setHandler((LmsTypeHandler) classHandler.newInstance());
                conf.getHandler().init(typeNode);

                types.put(typeNode.getName(), conf);
            }
            catch (InstantiationException ex)
            {
                log.error("Error getting lms type configuration for {}", typeNode.getHandle(), ex);
            }
            catch (IllegalAccessException ex)
            {
                log.error("Error getting lms type configuration for {}", typeNode.getHandle(), ex);
            }
            catch (ClassNotFoundException ex)
            {
                log.error("Error getting lms type configuration for {}", typeNode.getHandle(), ex);
            }
            catch (RuntimeException ex)
            {
                log.error("Error getting lms type configuration for {}", typeNode.getHandle(), ex);
            }
        }
    }

    public static LmsTypesManager getInstance()
    {
        return Components.getSingleton(LmsTypesManager.class);
    }

    public Map<String, LmsTypeConfiguration> getTypes()
    {
        return types;
    }

    /**
     * Get all course nodes in a folder
     * @param folder folder
     * @return all course nodes
     */
    public Collection<Content> getCourseNodes(Content folder)
    {
        return getCourseNodes(folder, null);
    }

    /**
     * Get all scorm nodes of passed type in a folder
     * @param folder folder
     * @param type scorm
     * @return all media nodes of passed type
     */
    public Collection<Content> getCourseNodes(final Content folder, final String type)
    {
        List<Content> courses = (List<Content>) folder.getChildren(new Content.ContentFilter()
        {

            /**
             * {@inheritDoc}
             */
            public boolean accept(Content content)
            {
                if (type != null)
                {
                    return NodeDataUtil.getString(content, "type").equals(type);
                }
                return true;
            }

        });

        return courses;
    }

    @SuppressWarnings("unchecked")
    public Collection<Content> search(String text, final String type) throws RepositoryException
    {
        QueryManager qm = MgnlContext.getQueryManager(LMSModule.REPO);
        StringBuilder sb = new StringBuilder();
        sb.append("//*[jcr:contains(.,'");
        sb.append(StringUtils.replace(text, "'", "''"));
        sb.append("')] order by @jcr:score descending");
        Query q = qm.createQuery(sb.toString(), Query.XPATH);
        QueryResult qr = q.execute();
        Collection collection = qr.getContent(LmsTypesManager.MGNL_COURSE_TYPE);

        if (!StringUtils.isBlank(type))
        {
            CollectionUtils.filter(collection, new Predicate()
            {

                /**
                 * {@inheritDoc}
                 */
                public boolean evaluate(Object object)
                {
                    if (object instanceof Content)
                    {
                        return NodeDataUtil.getString((Content) object, "type").equals(type);
                    }
                    return false;
                }

            });
        }

        return collection;
    }

    /**
     * Get the type configuration for a scorm or course
     * @param scormcourse scorm
     * @return type configuration
     */
    public LmsTypeConfiguration getTypeConfigurationFrom(Content item)
    {
        try
        {
            if (!item.getItemType().equals(COURSE))
            {
                return null;
            }
        }
        catch (RepositoryException e)
        {
            log.error("Error getting item type on node {} module lms", item.getHandle(), e);
            return null;
        }

        return types.get(NodeDataUtil.getString(item, "type"));
    }

    /**
     * Get uri mapping for repo
     * @return uri mapping for repo
     */
    public String getURIMappingPrefix()
    {
        for (URI2RepositoryMapping mapping : URI2RepositoryManager.getInstance().getMappings())
        {
            if (mapping.getRepository().equals(LMSModule.REPO))
            {
                return mapping.getURIPrefix();
            }
        }
        return StringUtils.EMPTY;
    }
}
