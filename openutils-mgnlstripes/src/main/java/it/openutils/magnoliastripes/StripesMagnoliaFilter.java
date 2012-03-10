/**
 *
 * Stripes module for Magnolia CMS (http://www.openmindlab.com/lab/products/mgnlstripes.html)
 * Copyright(C) 2008-2012, Openmind S.r.l. http://www.openmindonline.it
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

import info.magnolia.cms.filters.DispatchRule;
import info.magnolia.cms.filters.DispatchRules;
import info.magnolia.cms.filters.Mapping;
import info.magnolia.cms.filters.MgnlFilter;
import info.magnolia.cms.filters.WebContainerResources;
import info.magnolia.cms.util.RequestHeaderUtil;
import info.magnolia.cms.util.ServletUtils;
import info.magnolia.context.MgnlContext;
import info.magnolia.context.WebContext;
import info.magnolia.context.WebContextImpl;
import info.magnolia.objectfactory.Components;
import info.magnolia.voting.Voter;
import info.magnolia.voting.Voting;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Pattern;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.stripes.config.Configuration;
import net.sourceforge.stripes.controller.StripesFilter;
import net.sourceforge.stripes.controller.StripesRequestWrapper;
import net.sourceforge.stripes.exception.StripesServletException;

import org.apache.commons.lang.ArrayUtils;


/**
 * @author fgiust
 * @version $Id$
 */
public class StripesMagnoliaFilter extends StripesFilter implements MgnlFilter
{

    private String name;

    private Voter[] bypasses = new Voter[0];

    private boolean enabled = true;

    private DispatchRules dispatchRules = new DispatchRules();

    private Mapping mapping = new Mapping();

    private WebContainerResources webContainerResources = Components.getSingleton(WebContainerResources.class);

    private boolean initdone;

    /**
     * {@inheritDoc}
     */
    @Override
    public void init(FilterConfig filterConfig) throws ServletException
    {
        super.init(filterConfig);
        filterConfig.getServletContext().setAttribute(Configuration.class.getName(), getInstanceConfiguration());
        initdone = true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
        throws IOException, ServletException
    {
        if (initdone)
        {
            super.doFilter(servletRequest, servletResponse, filterChain);
        }
        else
        {
            // not yet ready
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }

    /**
     * Wraps the HttpServletRequest with a StripesServletRequest. This is done to ensure that any form posts that
     * contain file uploads get handled appropriately.
     * @param servletRequest the HttpServletRequest handed to the dispatcher by the container
     * @return an instance of StripesRequestWrapper, which is an HttpServletRequestWrapper
     * @throws StripesServletException if the wrapper cannot be constructed
     */
    @Override
    protected StripesRequestWrapper wrapRequest(HttpServletRequest servletRequest) throws StripesServletException
    {
        try
        {
            return StripesRequestWrapper.findStripesWrapper(servletRequest);
        }
        catch (IllegalStateException e)
        {
            StripesRequestWrapper srw = new StripesRequestWrapper(servletRequest);

            if (MgnlContext.hasInstance())
            {
                // be sure that the request wrapper gets setted in mgnlcontext too
                WebContext webContext = (WebContext) MgnlContext.getInstance();
                RepositoryAcquiringStrategy strategy = ((WebContextImpl) webContext).getRepositoryStrategy();
                webContext.init(servletRequest, webContext.getResponse(), webContext.getServletContext());
                ((WebContextImpl) webContext).setRepositoryStrategy(strategy);
            }
            return srw;
        }
    }

    public boolean matches(HttpServletRequest request)
    {
        return isEnabled() && matchesDispatching(request) && mapsTo(request) && !bypasses(request);
    }

    protected boolean mapsTo(HttpServletRequest request)
    {
        if (getMapping().getMappings().isEmpty())
        {
            return true;
        }
        return getMapping().match(request).isMatching();
    }

    protected boolean matchesDispatching(HttpServletRequest request)
    {
        if (webContainerResources == null)
        {
            return true;
        }
        boolean toWebContainerResource = webContainerResources.isWebContainerResource(request);
        boolean toMagnoliaResource = !toWebContainerResource;

        DispatchRule dispatchRule = getDispatchRules().getDispatchRule(ServletUtils.getDispatcherType(request));
        if (toMagnoliaResource && dispatchRule.isToMagnoliaResources())
        {
            return true;
        }
        if (toWebContainerResource && dispatchRule.isToWebContainerResources())
        {
            return true;
        }
        return false;
    }

    protected boolean bypasses(HttpServletRequest request)
    {
        Voting voting = Voting.HIGHEST_LEVEL;
        if (voting.vote(bypasses, request) > 0)
        {
            return true;
        }

        return false;
    }

    @Override
    public void destroy()
    {
        // nothing to do here
    }

    public Voter[] getBypasses()
    {
        return this.bypasses;
    }

    public void addBypass(Voter voter)
    {
        this.bypasses = (Voter[]) ArrayUtils.add(this.bypasses, voter);
    }

    public String getName()
    {
        return this.name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public boolean isEnabled()
    {
        return this.enabled;
    }

    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }

    public DispatchRules getDispatchRules()
    {
        return dispatchRules;
    }

    public void setDispatchRules(DispatchRules dispatching)
    {
        this.dispatchRules = dispatching;
    }

    public Collection<String> getMappings()
    {
        ArrayList<String> result = new ArrayList<String>();
        for (Pattern pattern : getMapping().getMappings())
        {
            result.add(pattern.pattern());
        }
        return result;
    }

    protected Mapping getMapping()
    {
        return mapping;
    }

    public void addMapping(String mapping)
    {
        this.getMapping().addMapping(mapping);
    }

    // ---- utility methods -----
    protected boolean acceptsGzipEncoding(HttpServletRequest request)
    {
        return RequestHeaderUtil.acceptsGzipEncoding(request);
    }

    protected boolean acceptsEncoding(final HttpServletRequest request, final String name)
    {
        return RequestHeaderUtil.acceptsEncoding(request, name);
    }

    protected boolean headerContains(final HttpServletRequest request, final String header, final String value)
    {
        return RequestHeaderUtil.headerContains(request, header, value);
    }

    protected void addAndVerifyHeader(HttpServletResponse response, String name, String value)
    {
        RequestHeaderUtil.addAndVerifyHeader(response, name, value);
    }
}
