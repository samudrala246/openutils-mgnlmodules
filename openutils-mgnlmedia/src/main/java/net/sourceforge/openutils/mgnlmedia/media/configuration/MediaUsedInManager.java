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

package net.sourceforge.openutils.mgnlmedia.media.configuration;

import info.magnolia.cms.beans.config.ContentRepository;
import info.magnolia.cms.beans.config.ObservedManager;
import info.magnolia.cms.core.Content;
import info.magnolia.cms.core.ItemType;
import info.magnolia.cms.util.FactoryUtil;
import info.magnolia.content2bean.Content2BeanException;
import info.magnolia.content2bean.Content2BeanUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jcr.RepositoryException;
import javax.jcr.query.InvalidQueryException;

import net.sourceforge.openutils.mgnlcriteria.jcr.query.AdvancedResult;
import net.sourceforge.openutils.mgnlcriteria.jcr.query.Criteria;
import net.sourceforge.openutils.mgnlcriteria.jcr.query.JCRCriteriaFactory;
import net.sourceforge.openutils.mgnlcriteria.jcr.query.criterion.Order;
import net.sourceforge.openutils.mgnlcriteria.jcr.query.criterion.Restrictions;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author dschivo
 * @version $Id$
 */
public class MediaUsedInManager extends ObservedManager
{

    public static MediaUsedInManager getInstance()
    {
        return (MediaUsedInManager) FactoryUtil.getSingleton(MediaUsedInManager.class);
    }

    private static Logger log = LoggerFactory.getLogger(MediaUsedInManager.class);

    private Map<String, UsedInWorkspace> usedInWorkspaceMap = new HashMap<String, UsedInWorkspace>();

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onClear()
    {
        usedInWorkspaceMap.clear();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onRegister(Content node)
    {
        Collection<Content> uiwNodes = node.getChildren(ItemType.CONTENTNODE);
        for (Content uiwNode : uiwNodes)
        {
            try
            {
                UsedInWorkspace uiw = (UsedInWorkspace) Content2BeanUtil.toBean(uiwNode, UsedInWorkspace.class);
                if (StringUtils.isEmpty(uiw.getWorkspaceName()))
                {
                    uiw.setWorkspaceName(uiwNode.getName());
                }
                if (StringUtils.isEmpty(uiw.getNodeType()))
                {
                    uiw.setNodeType(ItemType.CONTENT.getSystemName());
                }
                usedInWorkspaceMap.put(uiw.getWorkspaceName(), uiw);
            }
            catch (Content2BeanException e)
            {
                log.error("Error getting media used-in for {}", uiwNode.getHandle(), e);
            }
        }
    }

    /**
     * @param mediaUUID
     * @return
     * @throws InvalidQueryException
     * @throws RepositoryException
     */
    public Map<String, List<String>> getUsedInPaths(String mediaUUID) throws InvalidQueryException, RepositoryException
    {
        Map<String, List<String>> map = new HashMap<String, List<String>>(usedInWorkspaceMap.size());
        for (String workspaceName : usedInWorkspaceMap.keySet())
        {
            map.put(workspaceName, getUsedInWorkspacePaths(mediaUUID, workspaceName));
        }
        return map;
    }

    public List<String> getUsedInWorkspacePaths(String mediaUUID, String workspaceName) throws InvalidQueryException,
        RepositoryException
    {
        UsedInWorkspace uiw = usedInWorkspaceMap.get(workspaceName);
        if (uiw == null)
        {
            if (ContentRepository.WEBSITE.equals(workspaceName) && usedInWorkspaceMap.isEmpty())
            {
                // backward compatibility
                uiw = UsedInWorkspace.DEFAULT_WEBSITE;
            }
            else
            {
                return Collections.emptyList();
            }
        }
        List<Content> nodes = getUsedInWorkspaceNodes(mediaUUID, uiw);
        List<String> paths = new ArrayList<String>(nodes.size());
        for (Content node : nodes)
        {
            paths.add(node.getHandle());
        }
        return paths;
    }

    private static List<Content> getUsedInWorkspaceNodes(String mediaUUID, UsedInWorkspace uiw)
        throws InvalidQueryException, RepositoryException
    {
        List<Content> nodes = new ArrayList<Content>();
        Set<String> handles = new HashSet<String>();

        String basepath = "/jcr:root" + StringUtils.defaultString(uiw.getBasePath());

        if (!StringUtils.endsWith(basepath, "/"))
        {
            basepath = basepath + "/";
        }
        basepath = basepath + "/*";

        Criteria criteria = JCRCriteriaFactory
            .createCriteria()
            .setWorkspace(uiw.getWorkspaceName())
            .setBasePath(basepath)
            .add(Restrictions.contains(uiw.getPropertyName(), mediaUUID))
            .add(Restrictions.not(Restrictions.eq("@jcr:primaryType", "nt:frozenNode")))
            .addOrder(Order.desc("@jcr:score"));

        AdvancedResult result = criteria.execute();

        log.debug("{} > {}", criteria.toXpathExpression(), result.getTotalSize());

        for (Content item : result.getItems())
        {
            // log.debug("{} {}", item.getJCRNode().getPrimaryNodeType().getName(), item.getHandle());

            while (!item.getNodeTypeName().equals(uiw.getNodeType()) && item.getLevel() > 1)
            {
                item = item.getParent();
            }
            if (item.getNodeTypeName().equals(uiw.getNodeType()) && !handles.contains(item.getHandle()))
            {
                nodes.add(item);
                handles.add(item.getHandle());
            }
        }

        return nodes;
    }

}
