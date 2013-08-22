/**
 *
 * SimpleMedia Module for Magnolia CMS (http://www.openmindlab.com/lab/products/media.html)
 * Copyright(C) 2008-2013, Openmind S.r.l. http://www.openmindonline.it
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

package net.sourceforge.openutils.mgnlmedia.media.advancedsearch;

import javax.servlet.http.HttpServletRequest;

import net.sourceforge.openutils.mgnlmedia.media.pages.SortMode;

import org.apache.commons.lang.StringUtils;


/**
 * @author ADMIN
 * @version $Id: $
 */
public class SearchFilterSorting extends SearchFilterOptionProvider
{

    /**
     * {@inheritDoc}
     */
    @Override
    public SortMode getSorting(String parameter, HttpServletRequest request)
    {
        String[] values = (String[]) request.getParameterMap().get(parameter);
        if (values != null && values.length > 0 && StringUtils.isNotBlank(values[0]))
        {
            try
            {
                return SortMode.valueOf(values[0]);
            }
            catch (IllegalArgumentException e)
            {
                // ignore
            }
        }
        return super.getSorting(parameter, request);
    }
}
