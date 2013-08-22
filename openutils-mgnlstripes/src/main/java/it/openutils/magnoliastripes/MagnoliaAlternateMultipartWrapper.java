/**
 *
 * Stripes module for Magnolia CMS (http://www.openmindlab.com/lab/products/mgnlstripes.html)
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

package it.openutils.magnoliastripes;

import java.util.Enumeration;

import net.sourceforge.stripes.controller.multipart.MultipartWrapper;


/**
 * An implementation of MultipartWrapper that delegates to the standard magnolia multipart form handling. This version
 * avoids stripes' merge of params between multipart and standard request, because the merge was already done by
 * Magnolia itself.
 * @author danilo ghirardelli
 * @version $Id$
 * @deprecated use MagnoliaMultipartWrapper
 */
@Deprecated
public class MagnoliaAlternateMultipartWrapper extends MagnoliaMultipartWrapper implements MultipartWrapper
{

    /**
     * {@inheritDoc}
     */
    public Enumeration<String> getParameterNames()
    {
        // All params are already included in request
        return new Enumeration<String>()
        {

            public boolean hasMoreElements()
            {
                return false;
            }

            public String nextElement()
            {
                return null;
            }
        };
    }

    /**
     * {@inheritDoc}
     */
    public String[] getParameterValues(String name)
    {
        // All params are already included in request
        return null;
    }
}