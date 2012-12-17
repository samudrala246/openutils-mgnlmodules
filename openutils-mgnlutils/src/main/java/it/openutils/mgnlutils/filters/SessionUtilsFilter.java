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

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Trace calls to getSession() in order to avoid unwanted session creation.
 * @author fgiust
 * @version $Id$
 */
public class SessionUtilsFilter implements Filter
{

    /**
     * Logger.
     */
    protected static Logger log = LoggerFactory.getLogger(SessionUtilsFilter.class);

    /**
     * {@inheritDoc}
     */
    public void destroy()
    {
        // nothing to do
    }

    /**
     * {@inheritDoc}
     */
    public void init(FilterConfig filterConfig) throws ServletException
    {
        // nothing to do
    }

    /**
     * {@inheritDoc}
     */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
        ServletException
    {

        chain.doFilter(new TrackSessionRequestWrapper((HttpServletRequest) request), new StripSessionIdWrapper(
            (HttpServletResponse) response));

    }

    /**
     * A request wrapper that can be enabled to trace calls to getSession().
     * @author fgiust
     * @version $Id$
     */
    public static class TrackSessionRequestWrapper extends HttpServletRequestWrapper
    {

        /**
         * @param request original HttpServletRequest
         */
        public TrackSessionRequestWrapper(HttpServletRequest request)
        {
            super(request);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public HttpSession getSession()
        {
            log.warn("getSession() called", new Exception(
                "this exception is only generated to add debugging informations"));

            try
            {
                return super.getSession();
            }
            catch (IllegalStateException e)
            {
                log.error("IllegalStateException got while trying to create a new session for request "
                    + getRequestURI(), e);

                throw e;
            }

        }

    }

    /**
     * Response wrapper that avoid session ids appended to URLs. This means that cookies are required, but it also makes
     * the website friendly to search engines.
     * @author fgiust
     * @version $Id$
     */
    public class StripSessionIdWrapper extends HttpServletResponseWrapper
    {

        /**
         * @param response original HttpServletResponse
         */
        public StripSessionIdWrapper(HttpServletResponse response)
        {
            super(response);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String encodeUrl(String url)
        {
            return url;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String encodeURL(String url)
        {
            return url;
        }

    }

}
