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

import info.magnolia.jcr.util.MetaDataUtil;
import info.magnolia.jcr.util.PropertyUtil;
import it.openutils.mgnlutils.api.NodeUtilsExt;

import javax.jcr.Node;
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
    public String readEntry(Node node, String name, Scope scope)
    {
        switch (scope)
        {
            case local :
                return PropertyUtil.getString(node, name);
            case global :
                Node globalNode = getGlobalNode(node);
                return PropertyUtil.getString(globalNode, name);
            default :
                return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeEntry(Node node, String name, String value, Scope scope) throws RepositoryException
    {
        switch (scope)
        {
            case local :
                setOrDelete(node, name, value);
                break;
            case global :
                Node globalNode = getGlobalNode(node);
                setOrDelete(globalNode, name, value);
                MetaDataUtil.updateMetaData(globalNode);
                break;
        }
    }

    protected void setOrDelete(Node node, String name, String value) throws RepositoryException
    {
        if (!StringUtils.isEmpty(value))
        {
            node.setProperty(name, value);
        }
        else
        {
            NodeUtilsExt.deletePropertyIfExist(node, name);
        }
    }
}
