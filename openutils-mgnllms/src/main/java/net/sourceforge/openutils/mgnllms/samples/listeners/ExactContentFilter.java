/**
 *
 * E-learning Module for Magnolia CMS (http://www.openmindlab.com/lab/products/lms.html)
 * Copyright(C) 2010-2012, Openmind S.r.l. http://www.openmindonline.it
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

package net.sourceforge.openutils.mgnllms.samples.listeners;

import info.magnolia.cms.core.Content;
import info.magnolia.cms.core.Content.ContentFilter;
import info.magnolia.cms.core.ItemType;

import javax.jcr.RepositoryException;


/**
 * @author carlo
 * @version $Id: $
 */
public class ExactContentFilter implements ContentFilter
{

    private ItemType compareTo;

    public ExactContentFilter(ItemType compareTo)
    {
        this.compareTo = compareTo;
    }

    /**
     * {@inheritDoc}
     */
    public boolean accept(Content content)
    {
        try
        {
            return content.getItemType().equals(compareTo);
        }
        catch (RepositoryException e)
        {
            return false;
        }
    }

}