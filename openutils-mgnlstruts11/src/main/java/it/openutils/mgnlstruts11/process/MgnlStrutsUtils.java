/**
 *
 * Struts 1.1 module for Magnolia CMS (http://www.openmindlab.com/lab/products/mgnlstruts.html)
 * Copyright(C) ${project.inceptionYear}-2012, Openmind S.r.l. http://www.openmindonline.it
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

package it.openutils.mgnlstruts11.process;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.PageContext;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionServlet;
import org.apache.struts.action.RequestProcessor;
import org.apache.struts.config.ForwardConfig;
import org.apache.struts.config.ModuleConfig;
import org.apache.struts.util.MessageResources;
import org.apache.struts.util.RequestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Magnolia-Struts utility methods.
 * @author fgiust
 * @version $Id$
 */
public final class MgnlStrutsUtils
{

    /**
     * Request parameter containing the current magnolia action.
     */
    public static final String PARAMETER_MGNLACTION = "_mgnlaction";

    /**
     *
     */
    public static final String ATTRIBUTE_ORIGINALURI = "_originaluri";

    /**
     *
     */
    public static final String ATTRIBUTE_URIFORNAVIGATION = "_urifornavigation";

    /**
    *
    */
    public static final String ATTRIBUTE_STRUTSACTION = "_strutsaction";

    /**
     * Logger.
     */
    private static Logger log = LoggerFactory.getLogger(MgnlStrutsUtils.class);

    /**
     * The message resources for this package.
     */
    private static MessageResources messages = MessageResources
        .getMessageResources("org.apache.struts.util.LocalStrings");

    /**
     * Look up and return the {@link RequestProcessor} responsible for the specified module, creating a new one if
     * necessary.
     * @param config The module configuration for which to acquire and return a RequestProcessor.
     * @exception ServletException if we cannot instantiate a RequestProcessor instance
     * @since Struts 1.1
     */
    public static RequestProcessor getRequestProcessor(ModuleConfig config, ServletContext servletContext,
        ActionServlet dispatcher, Class mgnlProcessorClass) throws ServletException
    {

        String key = Globals.REQUEST_PROCESSOR_KEY + config.getPrefix();
        RequestProcessor processor = (RequestProcessor) servletContext.getAttribute(key);

        if (processor == null)
        {
            String processorClass = config.getControllerConfig().getProcessorClass();
            try
            {
                processor = (RequestProcessor) RequestUtils.applicationInstance(StringUtils.isNotBlank(processorClass)
                    ? processorClass
                    : mgnlProcessorClass.getName());

                if (!processor.getClass().isAssignableFrom(mgnlProcessorClass))
                {
                    if (StringUtils.equalsIgnoreCase(
                        org.apache.struts.action.RequestProcessor.class.getName(),
                        processorClass))
                    {
                        // It's safe to replace the default processor
                        log.warn("The default processor class ("
                            + processorClass
                            + ") has been specified for struts module, this will be replaced by "
                            + mgnlProcessorClass.getName()
                            + " to enable the magnolia integration");
                    }
                    else
                    {
                        // It's NOT safe to replace a custom class, should stop the application...
                        log.error("A custom processor class ("
                            + processorClass
                            + ") has been specified for struts module, this will be replaced by "
                            + mgnlProcessorClass.getName()
                            + " to enable the magnolia integration");
                    }
                    processor = (RequestProcessor) RequestUtils.applicationInstance(mgnlProcessorClass.getName());
                }
            }
            catch (Exception e)
            {
                throw new UnavailableException("Cannot initialize RequestProcessor of class "
                    + config.getControllerConfig().getProcessorClass()
                    + ": "
                    + e);
            }

            processor.init(dispatcher, config);
            servletContext.setAttribute(key, processor);

        }
        return (processor);

    }

    /**
     * Compute a hyperlink URL based on the <code>forward</code>, <code>href</code>, <code>action</code> or
     * <code>page</code> parameter that is not null. The returned URL will have already been passed to
     * <code>response.encodeURL()</code> for adding a session identifier.
     * @param pageContext PageContext for the tag making this call
     * @param forward Logical forward name for which to look up the context-relative URI (if specified)
     * @param href URL to be utilized unmodified (if specified)
     * @param page Module-relative page for which a URL should be created (if specified)
     * @param action Logical action name for which to look up the context-relative URI (if specified)
     * @param params Map of parameters to be dynamically included (if any)
     * @param anchor Anchor to be dynamically included (if any)
     * @param redirect Is this URL for a <code>response.sendRedirect()</code>?
     * @param encodeSeparator This is only checked if redirect is set to false (never encoded for a redirect). If true,
     * query string parameter separators are encoded as &gt;amp;, else &amp; is used.
     * @return URL with session identifier
     * @exception MalformedURLException if a URL cannot be created for the specified parameters
     */
    public static String computeURL(PageContext pageContext, String forward, String href, String page, String action,
        Map<String, String> params, String anchor, boolean redirect, boolean encodeSeparator, String destinationPage)
        throws MalformedURLException
    {

        // TODO All the computeURL() methods need refactoring!

        // Validate that exactly one specifier was included
        int n = 0;
        if (forward != null)
        {
            n++;
        }
        if (href != null)
        {
            n++;
        }
        if (page != null)
        {
            n++;
        }
        if (action != null)
        {
            n++;
        }
        if (n != 1)
        {
            throw new MalformedURLException(messages.getMessage("computeURL.specifier"));
        }

        // Look up the module configuration for this request
        ModuleConfig config = (ModuleConfig) pageContext.getRequest().getAttribute(Globals.MODULE_KEY);
        if (config == null)
        { // Backwards compatibility hack
            config = (ModuleConfig) pageContext.getServletContext().getAttribute(Globals.MODULE_KEY);
            pageContext.getRequest().setAttribute(Globals.MODULE_KEY, config);
        }

        // Calculate the appropriate URL
        StringBuffer url = new StringBuffer();
        HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();

        String mgnlaction = null;

        if (destinationPage != null && href == null)
        {
            url.append(destinationPage);
        }

        if (forward != null)
        {
            ForwardConfig fc = config.findForwardConfig(forward);
            if (fc == null)
            {
                throw new MalformedURLException(messages.getMessage("computeURL.forward", forward));
            }
            if (fc.getRedirect())
            {
                redirect = true;
            }

            mgnlaction = forward;

        }
        else if (href != null)
        {
            url.append(href);
        }
        else if (action != null)
        {
            mgnlaction = RequestUtils.getActionMappingURL(action, pageContext);
            // url.append(RequestUtils.getActionMappingURL(action, pageContext));

        }
        else
        /* if (page != null) */{

            mgnlaction = RequestUtils.pageURL(request, page);
            // url.append(request.getContextPath());
            // url.append(RequestUtils.pageURL(request, page));
        }

        if (mgnlaction != null)
        {
            // remove parameters (how ugly is struts 1 :( )
            if (mgnlaction.contains("?"))
            {
                String actionparams = StringUtils.substringAfter(mgnlaction, "?");

                String[] paramz = StringUtils.split(actionparams, "&");
                for (String string : paramz)
                {
                    if (StringUtils.contains(string, "="))
                    {
                        params.put(StringUtils.substringBefore(string, "="), StringUtils.substringAfter(string, "="));
                    }

                }
                mgnlaction = StringUtils.substringBefore(mgnlaction, "?");
            }

            if (mgnlaction.endsWith(".do"))
            {
                mgnlaction = StringUtils.substringBeforeLast(mgnlaction, ".do");
            }

            params.put(PARAMETER_MGNLACTION, mgnlaction);
        }

        // Add anchor if requested (replacing any existing anchor)
        if (anchor != null)
        {
            String temp = url.toString();
            int hash = temp.indexOf('#');
            if (hash >= 0)
            {
                url.setLength(hash);
            }
            url.append('#');
            url.append(encode(anchor));
        }

        // Add dynamic parameters if requested
        if ((params != null) && (params.size() > 0))
        {

            // Save any existing anchor
            String temp = url.toString();
            int hash = temp.indexOf('#');
            if (hash >= 0)
            {
                anchor = temp.substring(hash + 1);
                url.setLength(hash);
                temp = url.toString();
            }
            else
            {
                anchor = null;
            }

            // Define the parameter separator
            String separator = null;
            if (redirect)
            {
                separator = "&";
            }
            else if (encodeSeparator)
            {
                separator = "&amp;";
            }
            else
            {
                separator = "&";
            }

            // Add the required request parameters
            boolean question = temp.indexOf('?') >= 0;
            Iterator keys = params.keySet().iterator();
            while (keys.hasNext())
            {
                String key = (String) keys.next();
                Object value = params.get(key);
                if (value == null)
                {
                    if (!question)
                    {
                        url.append('?');
                        question = true;
                    }
                    else
                    {
                        url.append(separator);
                    }
                    url.append(encode(key));
                    url.append('='); // Interpret null as "no value"
                }
                else if (value instanceof String)
                {
                    if (!question)
                    {
                        url.append('?');
                        question = true;
                    }
                    else
                    {
                        url.append(separator);
                    }
                    url.append(encode(key));
                    url.append('=');
                    url.append(encode((String) value));
                }
                else if (value instanceof String[])
                {
                    String values[] = (String[]) value;
                    for (int i = 0; i < values.length; i++)
                    {
                        if (!question)
                        {
                            url.append('?');
                            question = true;
                        }
                        else
                        {
                            url.append(separator);
                        }
                        url.append(encode((key)));
                        url.append('=');
                        url.append(encode(values[i]));
                    }
                }
                else
                /* Convert other objects to a string */{
                    if (!question)
                    {
                        url.append('?');
                        question = true;
                    }
                    else
                    {
                        url.append(separator);
                    }
                    url.append(encode(key));
                    url.append('=');
                    url.append(encode(value.toString()));
                }
            }

            // Re-add the saved anchor (if any)
            if (anchor != null)
            {
                url.append('#');
                url.append(encode(anchor));
            }

        }

        // Perform URL rewriting to include our session ID (if any)
        if (pageContext.getSession() != null)
        {
            HttpServletResponse response = (HttpServletResponse) pageContext.getResponse();
            if (redirect)
            {
                return (response.encodeRedirectURL(url.toString()));
            }
            else
            {
                return (response.encodeURL(url.toString()));
            }
        }
        else
        {
            return (url.toString());
        }

    }

    private static String encode(String url)
    {
        try
        {

            return URLEncoder.encode(url, "UTF-8");
        }
        catch (UnsupportedEncodingException e)
        {
            // should never happen
            throw new RuntimeException(e);
        }

    }

    /**
     * @param styleClass
     */
    public static String extractDestinationFromClass(HttpServletRequest request, String styleClass)
    {
        String mgnlDest = null;
        if (styleClass != null && StringUtils.contains(styleClass, "mgnl:"))
        {
            mgnlDest = request.getContextPath() + StringUtils.substringAfter(styleClass, "mgnl:");
            if (StringUtils.contains(mgnlDest, " "))
            {
                mgnlDest = StringUtils.substringBefore(mgnlDest, " ");
            }
        }
        return mgnlDest;
    }

    /**
     * @param styleClass
     */
    public static String trimDestinationFromClass(String styleClass)
    {
        String mgnlDest = styleClass;
        if (styleClass != null && StringUtils.contains(styleClass, "mgnl:"))
        {
            mgnlDest = StringUtils.substringBefore(styleClass, "mgnl:");
            String leftover = StringUtils.substringAfter(styleClass, "mgnl:");
            return mgnlDest + StringUtils.substringAfter(leftover, " ");
        }
        return mgnlDest;
    }
}
