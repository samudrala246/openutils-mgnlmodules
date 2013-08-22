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

import javax.jcr.RepositoryException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * A default strategy that returns the ancestor at level one.
 * @author dschivo
 * @version $Id$
 */
public class DefaultGetGlobalEntriesNodeStrategy implements GetGlobalEntriesNodeStrategy
{

    /**
     * Logger.
     */
    private Logger log = LoggerFactory.getLogger(getClass());

    private int ancestorLevel = 1;

    /**
     * Returns the ancestorLevel.
     * @return the ancestorLevel
     */
    public int getAncestorLevel()
    {
        return ancestorLevel;
    }

    /**
     * Sets the ancestorLevel.
     * @param ancestorLevel the ancestorLevel to set
     */
    public void setAncestorLevel(int ancestorLevel)
    {
        this.ancestorLevel = ancestorLevel;
    }

    /**
     * {@inheritDoc}
     */
    public Content getGlobalEntriesNode(Content node)
    {
        try
        {
            return node.getAncestor(ancestorLevel);
        }
        catch (RepositoryException e)
        {
            log.error("Cannot find global entries node for " + node, e);
            return null;
        }
    }
}
