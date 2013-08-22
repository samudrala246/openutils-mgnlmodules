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
import info.magnolia.cms.util.NodeDataUtil;

import javax.jcr.RepositoryException;

import org.apache.commons.lang.StringUtils;


/**
 * @author dschivo
 * @version $Id$
 */
public class DefaultPersistenceStrategy extends PersistenceStrategy
{

    /**
     * {@inheritDoc}
     */
    @Override
    public String readEntry(Content node, String name, Scope scope)
    {
        switch (scope)
        {
            case local :
                return NodeDataUtil.getString(node, name);
            case global :
                Content globalNode = getGlobalNode(node);
                return NodeDataUtil.getString(globalNode, name);
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
                setOrDelete(node, name, value);
                break;
            case global :
                Content globalNode = getGlobalNode(node);
                setOrDelete(globalNode, name, value);
                globalNode.updateMetaData();
                break;
        }
    }

    protected void setOrDelete(Content node, String name, String value) throws RepositoryException
    {
        if (!StringUtils.isEmpty(value))
        {
            NodeDataUtil.getOrCreateAndSet(node, name, value);
        }
        else
        {
            if (node != null && node.hasNodeData(name))
            {
                node.deleteNodeData(name);
            }
        }
    }
}
