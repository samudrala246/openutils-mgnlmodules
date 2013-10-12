/**
 *
 * Generic utilities for Magnolia CMS (http://www.openmindlab.com/lab/products/mgnlutils.html)
 * Copyright(C) 2009-2012, Openmind S.r.l. http://www.openmindonline.it
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

package it.openutils.mgnlutils.filters;

import info.magnolia.cms.filters.AbstractMgnlFilter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * THIS HAS NOTHING TO DO WITH SERVER SIDE CACHE: since the original filter has been moved to the magnolia cache module
 * I am adding it back here.
 * <p>
 * Filter that sets cache headers, allowing or denying cache at client-side. By default the filter adds the
 * "Cache-Control: public" and expire directives to resources so that everything can be cached by the browser. Setting
 * the <code>nocache</code> property to <code>true</code> has the opposite effect, forcing browsers to avoid caching.
 * </p>
 * <p>
 * The following example shows how to configure the filter so that static resources (images, css, js) gets cached by the
 * browser, and deny cache for html pages.
 * </p>
 * 
 * <pre>
 * + server
 *    + filters
 *      + ...
 *      + headers-cache
 *        - class                  info.magnolia.module.cache.filter.CacheHeadersFilter
 *        - expirationMinutes      1440 <em>(default)</em>
 *        + bypasses
 *          + extensions
 *            - class              info.magnolia.voting.voters.ExtensionVoter
 *            - allow              gif,jpg,png,swf,css,js
 *            - not                true
 *      + headers-nocache
 *        - class                  info.magnolia.module.cache.filter.CacheHeadersFilter
 *        - nocache                true
 *        + bypasses
 *          + extensions
 *            - class              info.magnolia.voting.voters.ExtensionVoter
 *            - allow              html
 *            - not                true
 * </pre>
 * @author Fabrizio Giustina
 * @version $Id$
 */
public class ClientHeadersFilter extends AbstractMgnlFilter
{

    /**
     * Number of minutes this item must be kept in cache.
     */
    private long expirationMinutes = 1440;

    /**
     * Cache should be avoided for filtered items.
     */
    private boolean nocache;

    /**
     * Sets the expirationMinutes.
     * @param expirationMinutes the expirationMinutes to set
     */
    public void setExpirationMinutes(long expirationMinutes)
    {
        this.expirationMinutes = expirationMinutes;
    }

    /**
     * Sets the nocache.
     * @param nocache the nocache to set
     */
    public void setNocache(boolean nocache)
    {
        this.nocache = nocache;
    }

    @Override
    public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
        throws IOException, ServletException
    {
        if (nocache)
        {
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate, max-age=0");
            response.setDateHeader("Expires", 0L);
        }
        else
        {
            response.setHeader("Pragma", "");
            response.setHeader("Cache-Control", "max-age=" + expirationMinutes * 60 + ", public");
            final long expiration = System.currentTimeMillis() + expirationMinutes * 60000;
            response.setDateHeader("Expires", expiration);
        }

        chain.doFilter(request, response);
    }
}
