/**
 *
 * CAS integration module for Magnolia CMS (http://www.openmindlab.com/lab/products/mgnlcas.html)
 * Copyright(C) 2007-2013, Openmind S.r.l. http://www.openmindonline.it
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

package net.sourceforge.openutils.mgnlcas;

import javax.servlet.http.HttpServletRequest;


/**
 * @author fgiust
 * @version $Id: RequestUtils.java 4124 2008-09-22 14:56:46Z fgiust $
 */
public final class RequestUtils
{

    private RequestUtils()
    {
        // don't instantiate
    }

    public static String absoluteUrl(HttpServletRequest request)
    {

        StringBuilder sb = new StringBuilder();
        sb.append(serverName(request));
        sb.append(request.getRequestURI());

        return sb.toString();

    }

    public static String serverName(HttpServletRequest request)
    {

        String host = request.getServerName();
        int port = request.getServerPort();
        String scheme = request.getScheme();
        StringBuilder sb = new StringBuilder();
        sb.append(scheme);
        sb.append("://");
        sb.append(host);
        if (port != 0 && port != 80 && port != 443)
        {
            sb.append(":");
            sb.append(port);
        }

        return sb.toString();

    }
}
