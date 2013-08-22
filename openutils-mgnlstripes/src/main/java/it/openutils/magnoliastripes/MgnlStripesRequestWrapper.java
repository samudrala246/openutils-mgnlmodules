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

import info.magnolia.context.MgnlContext;
import info.magnolia.context.WebContext;

import java.io.IOException;
import java.io.Writer;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import net.sourceforge.stripes.exception.StripesServletException;


/**
 * Magnolia request wrapper for Stripes actions.
 * @author fgiust
 * @version $Id: StripesParagraphRequestWrapper.java 10833 2008-09-15 15:39:08Z fgiust $
 */
class MgnlStripesRequestWrapper extends HttpServletRequestWrapper
{

    private static final String SERVLET_PATH = "mgnlStripesServletPath";

    private static final String ALREADY_INCLUDED = "mgnlAlreadyIncluded";

    /**
     * Hacked servlet path.
     */
    private String servletPath;

    /**
     * Parameter map.
     */
    private Map<String, String[]> parameterMap;

    private Writer out;

    /**
     * Instantiate a new request wrapper.
     * @param request original HttpServletRequest
     * @param servletPath modified servlet path (matches Stripes binding)
     * @param paragraphsData map containing paragraph attributes
     * @throws StripesServletException if any other error occurs constructing the wrapper
     */
    public MgnlStripesRequestWrapper(
        HttpServletRequest request,
        String servletPath,
        Map<String, String[]> paragraphsData,
        Writer out) throws StripesServletException
    {
        super(request);
        this.servletPath = servletPath;
        this.out = out;

        parameterMap = new HashMap<String, String[]>();
        parameterMap.putAll(request.getParameterMap());
        parameterMap.putAll(paragraphsData);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getServletPath()
    {
        return servletPath;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public Enumeration<String> getParameterNames()
    {
        return ((Hashtable) parameterMap).keys();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String[] getParameterValues(String name)
    {
        return parameterMap.get(name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, String[]> getParameterMap()
    {
        return parameterMap;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getParameter(String name)
    {
        String[] values = getParameterValues(name);
        if (values != null && values.length > 0)
        {
            return values[0];
        }
        else
        {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RequestDispatcher getRequestDispatcher(String path)
    {
        return new MagnoliaRequestDispatcher(path, out, (HttpServletRequest) this.getRequest());
    }

    /**
     * A {@link RequestDispatcher} that uses {@link WebContext} for including a resource.
     * @author fgiust
     * @version $Id: StripesParagraphRequestWrapper.java 10833 2008-09-15 15:39:08Z fgiust $
     */
    private static class MagnoliaRequestDispatcher implements RequestDispatcher
    {

        /**
         * The url this RequestDispatcher is bound to.
         */
        private String url;

        private Writer out;

        private HttpServletRequest originalRequest;

        /**
         * Created a new MagnoliaRequestDispatcher for the given url.
         * @param url url passed to {@link HttpServletRequest#getRequestDispatcher()}
         */
        public MagnoliaRequestDispatcher(String url, Writer out, HttpServletRequest originalRequest)
        {
            this.url = url;
            this.out = out;
            this.originalRequest = originalRequest;
        }

        /**
         * {@inheritDoc}
         */
        public void forward(ServletRequest request, ServletResponse response) throws ServletException, IOException
        {
            request.setAttribute(SERVLET_PATH, url);
            if (request.getAttribute(ALREADY_INCLUDED) == null)
            {
                request.setAttribute(ALREADY_INCLUDED, true);
                ((WebContext) MgnlContext.getInstance()).include(url, out);
            }
            else
            {
                originalRequest.getRequestDispatcher(url).include(request, response);
            }
        }

        /**
         * {@inheritDoc}
         */
        public void include(ServletRequest request, ServletResponse response) throws ServletException, IOException
        {
            request.setAttribute(SERVLET_PATH, url);
            if (request.getAttribute(ALREADY_INCLUDED) == null)
            {
                request.setAttribute(ALREADY_INCLUDED, true);
                ((WebContext) MgnlContext.getInstance()).include(url, out);
            }
            else
            {
                originalRequest.getRequestDispatcher(url).include(request, response);
            }
        }

    }
}