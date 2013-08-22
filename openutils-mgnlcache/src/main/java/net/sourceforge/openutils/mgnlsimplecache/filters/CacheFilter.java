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

package net.sourceforge.openutils.mgnlsimplecache.filters;

import info.magnolia.cms.filters.AbstractMgnlFilter;
import info.magnolia.cms.util.RequestHeaderUtil;
import info.magnolia.context.MgnlContext;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.openutils.mgnlsimplecache.managers.CacheHeaders;
import net.sourceforge.openutils.mgnlsimplecache.managers.CacheManager;
import net.sourceforge.openutils.mgnlsimplecache.managers.CacheResponseWrapper;
import net.sourceforge.openutils.mgnlsimplecache.managers.CachedItem;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Main cache filter, to be configured in magnolia filter chain.
 * @author Manuel Molaschi
 * @author Fabrizio Giustina
 * @version $Id$
 */
public class CacheFilter extends AbstractMgnlFilter
{

    private CacheManager cacheManager;

    private boolean waitwhenwriting = false;

    /**
     * Logger.
     */
    private Logger log = LoggerFactory.getLogger(CacheFilter.class);

    /**
     * Returns the cacheContentManager.
     * @return the cacheContentManager
     */
    public CacheManager getCacheManager()
    {
        return cacheManager;
    }

    /**
     * Sets the cacheContentManager.
     * @param cacheContentManager the cacheContentManager to set
     */
    public void setCacheManager(CacheManager cacheContentManager)
    {
        this.cacheManager = cacheContentManager;
    }

    private String getAlreadyProcessingRequestKey()
    {
        return "PROC" + this.getClass() + "@" + this.hashCode();
    }

    protected void startProcessing()
    {
        MgnlContext.getWebContext().getRequest().setAttribute(getAlreadyProcessingRequestKey(), true);
    }

    protected boolean isAlreadyProcessing()
    {
        return MgnlContext.getWebContext().getRequest().getAttribute(getAlreadyProcessingRequestKey()) != null;
    }

    /**
     * Sets the waitwhenwriting.
     * @param waitwhenwriting the waitwhenwriting to set
     */
    public void setWaitwhenwriting(boolean waitwhenwriting)
    {
        this.waitwhenwriting = waitwhenwriting;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void init(FilterConfig filterConfig) throws ServletException
    {
        super.init(filterConfig);
        if (cacheManager != null)
        {
            cacheManager.start();
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void destroy()
    {
        super.destroy();
        if (cacheManager != null)
        {
            cacheManager.stop();
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doFilter(final HttpServletRequest request, final HttpServletResponse response, FilterChain chain)
        throws IOException, ServletException
    {
        // null check for broken configurations, needed
        if (cacheManager != null && cacheManager.isActive())
        {
            doFilterForContent(request, response, chain);
        }
        else
        {
            chain.doFilter(request, response);
        }
    }

    public void doFilterForContent(final HttpServletRequest request, final HttpServletResponse response,
        FilterChain chain) throws IOException, ServletException
    {
        if (!isAlreadyProcessing())
        {

            final boolean acceptGzip = RequestHeaderUtil.acceptsGzipEncoding(request);

            // get cache from manager
            CachedItem cacheContent = cacheManager.get(request);

            // additional check, it doesn't hurt
            // can happen when a flush occours in the middle of item caching
            if (cacheContent == null)
            {
                chain.doFilter(request, response);
                return;
            }

            OutputStream os = null;
            if (cacheContent.isNew())
            {
                // if someone is writing, returns null
                os = cacheContent.beginWrite();

                if (os == null)
                {
                    log.debug("Concurrent request for {} while caching", cacheManager.getKey(request));
                    if (!waitwhenwriting)
                    {
                        chain.doFilter(request, response);
                        return;
                    }
                }
            }

            // if request is cached (not new or cacheContent.beginWrite returns null)
            if (os == null)
            {
                boolean gotContentFromCache = returnCachedContent(request, response, acceptGzip, cacheContent);

                if (!gotContentFromCache)
                {
                    log.debug("No content got from cache for {}", cacheManager.getKey(request));
                }
                return;
            }

            this.startProcessing();
            // wrap response and start writing
            CacheResponseWrapper cacheContentResponse = new CacheResponseWrapper(response, cacheContent, os);

            MgnlContext.push(request, cacheContentResponse);

            boolean hasContent = false;
            try
            {
                // do chain
                chain.doFilter(request, cacheContentResponse);
            }
            catch (Throwable t)
            {
                hasContent = cacheContent.endWrite(os);
                cacheManager.reset(request);
                throw new ServletException(t);
            }

            MgnlContext.pop();
            hasContent = cacheContent.endWrite(os);

            response.setDateHeader("Last-Modified", cacheContent.getCreationTime());

            if (cacheContentResponse.isError() || cacheContentResponse.isRedirect() || !hasContent)
            {
                log.debug("Resetting {}: error={} redirect={}, empty={}", new Object[]{
                    cacheManager.getKey(request),
                    cacheContentResponse.isError(),
                    cacheContentResponse.isRedirect(),
                    !hasContent });
                cacheManager.reset(request);
            }
        }
        else
        {
            chain.doFilter(request, response);
        }
    }

    /**
     * @param request
     * @param response
     * @param acceptGzip
     * @param cacheContent
     * @throws IOException
     * @throws ServletException
     */
    private boolean returnCachedContent(final HttpServletRequest request, final HttpServletResponse response,
        final boolean acceptGzip, CachedItem cacheContent) throws IOException, ServletException
    {

        boolean getGzip = acceptGzip && cacheContent.hasGzip();
        InputStream cacheContentInputStream = cacheContent.beginRead(getGzip);

        try
        {
            if (cacheContentInputStream != null)
            {
                // user requires cache refresh
                // don't confuse client cache with server cache, user requested a refresh of the browser cache,
                // this doesn't mean the server can't send the cached page back!
                if (!"no-cache".equals(request.getHeader("Cache-Control")))
                {
                    if (!this.ifModifiedSince(request, cacheContent.getCreationTime()))
                    {
                        response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
                        return true;
                    }
                }

                CacheHeaders cacheHeaders = cacheContent.getCacheHeaders();
                cacheHeaders.apply(response);

                // stream from cache
                response.setDateHeader("Last-Modified", cacheContent.getCreationTime());
                response.setContentLength((int) cacheContent.getBodyLength(getGzip));
                if (getGzip)
                {
                    response.setHeader("Content-Encoding", "gzip");
                    response.setHeader("Vary", "Accept-Encoding");
                }
                IOUtils.copy(cacheContentInputStream, response.getOutputStream());
                response.setStatus(HttpServletResponse.SC_OK);

                return true;
            }
        }
        catch (Throwable e)
        {
            throw new ServletException(e);
        }
        finally
        {
            if (cacheContentInputStream != null)
            {
                cacheContent.endRead(cacheContentInputStream);
            }
        }
        return false;
    }

    /**
     * Check if server cache is newer then the client cache
     * @param request The servlet request we are processing
     * @return boolean true if the server resource is newer
     */
    public boolean ifModifiedSince(HttpServletRequest request, long lastModified)
    {
        try
        {
            long headerValue = request.getDateHeader("If-Modified-Since");
            if (headerValue != -1)
            {
                // If an If-None-Match header has been specified, if modified since
                // is ignored.
                if ((request.getHeader("If-None-Match") == null)
                    && (lastModified > 0 && lastModified <= headerValue + 1000))
                {
                    return false;
                }
            }
        }
        catch (IllegalArgumentException illegalArgument)
        {
            return true;
        }
        return true;
    }

}
