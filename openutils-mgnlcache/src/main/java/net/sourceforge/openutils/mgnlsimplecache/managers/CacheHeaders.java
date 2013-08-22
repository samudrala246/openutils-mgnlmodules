/**
 *
 * Simplecache module for Magnolia CMS (http://www.openmindlab.com/lab/products/simplecache.html)
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

package net.sourceforge.openutils.mgnlsimplecache.managers;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

import com.google.common.collect.Multimap;


/**
 * @author Manuel Molaschi
 * @author Fabrizio Giustina
 * @version $Id$
 */
public interface CacheHeaders
{

    boolean isError();

    boolean isRedirect();

    boolean isNotModified();

    Multimap<String, String> getHeaders();

    void apply(HttpServletResponse response);

    void serialize(OutputStream stream) throws IOException;

    void deserialize(InputStream stream) throws IOException;

}
