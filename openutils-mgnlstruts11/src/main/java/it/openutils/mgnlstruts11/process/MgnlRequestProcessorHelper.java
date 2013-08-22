/**
 *
 * Struts 1.1 module for Magnolia CMS (http://www.openmindlab.com/lab/products/mgnlstruts.html)
 * Copyright(C) ${project.inceptionYear}-2013, Openmind S.r.l. http://www.openmindonline.it
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

import info.magnolia.cms.beans.config.ServerConfiguration;
import info.magnolia.context.MgnlContext;
import info.magnolia.context.WebContext;
import it.openutils.mgnlstruts11.render.StrutsRenderer;

import java.io.IOException;
import java.io.Writer;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.PageContext;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.config.ActionConfig;
import org.apache.struts.config.ForwardConfig;
import org.apache.struts.config.ModuleConfig;
import org.apache.struts.upload.MultipartRequestWrapper;
import org.apache.struts.util.RequestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author fgiust
 * @version $Id$
 */
public class MgnlRequestProcessorHelper
{

    /**
     * Logger.
     */
    private static Logger log = LoggerFactory.getLogger(MgnlRequestProcessorHelper.class);

    public static void doProcessForwardConfig(HttpServletRequest request, HttpServletResponse response,
        ForwardConfig forward) throws IOException, ServletException
    {

        log.info("processForwardConfig " + forward);
        if (forward == null)
        {
            return;
        }

        if (log.isDebugEnabled())
        {
            log.debug("processForwardConfig(" + forward + ")");
        }

        String forwardPath = forward.getPath();
        String uri = null;

        // paths not starting with / should be passed through without any
        // processing
        // (ie. they're absolute)
        if (forwardPath.startsWith("/"))
        {
            uri = RequestUtils.forwardURL(request, forward); // get module
            // relative uri
        }
        else
        {
            uri = forwardPath;
        }

        // TODO da verificare
        String urifornavigation;
        if (!StringUtils.isEmpty(uri))
        {
            urifornavigation = uri;

            if (!uri.endsWith(".jsp") && !uri.startsWith("/do"))
            {
                urifornavigation = "/do" + uri;
            }
            urifornavigation = request.getContextPath() + urifornavigation;
            request.setAttribute(MgnlStrutsUtils.ATTRIBUTE_URIFORNAVIGATION, urifornavigation);
        }

        if (forward.getRedirect() && !uri.endsWith(".jsp"))
        {
            // @todo redirect in templates
            // only prepend context path for relative uri
            // if (uri.startsWith("/")) {
            // uri = request.getContextPath() + uri;
            // }
            // log.info("Redirecting to " + uri);
            //
            // response.sendRedirect(response.encodeRedirectURL(uri));

            // force includes instead of redirects
            doInclude(uri, request, response);

        }
        else
        {
            doInclude(uri, request, response);
        }

    }

    public static void doInclude(String uri, HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException
    {

        // needed for chained forwards, avoid loops!
        request.setAttribute(StrutsRenderer.PARAGRAPH_PATH, uri);

        request.setAttribute("process_jsps", Boolean.TRUE);

        // Unwrap the multipart request, if there is one.
        if (request instanceof MultipartRequestWrapper)
        {
            request = ((MultipartRequestWrapper) request).getRequest();
        }

        RequestDispatcher rd = getWebContext().getServletContext().getRequestDispatcher(uri);

		final String requestURI = request.getContextPath() + uri;
		WebContext wc = getWebContext();
		wc.push(new HttpServletRequestWrapper(request) {
			@Override
			public String getRequestURI() {
				return requestURI;
			}
		}, response);
		try {
			wc.include(uri, getOut());
		} finally {
			wc.pop();
		}
    }

    public static ActionMapping doProcessMapping(HttpServletRequest request, HttpServletResponse response, String path,
        ModuleConfig moduleConfig) throws IOException
    {

        log.info("Process mapping " + path);

        // Is there a directly defined mapping for this path?
        ActionMapping mapping = (ActionMapping) moduleConfig.findActionConfig(path);

        if (mapping == null)
        {
            // @todo This is almost a copy-paste of the end of RequestProcessor.processPath method, it should be
            // refactored in a "simplify path" somewhere else.
            // This is a temporary patch to handle forwards correctly.
        	String retryPath = StringUtils.defaultString(path);
        	String previousPath = StringUtils.EMPTY;
        	while ((mapping == null) && (!StringUtils.equals(retryPath, previousPath)) && ((StringUtils.indexOf(retryPath, ".") >= 0) || (StringUtils.indexOf(retryPath, "/", 1) > 0))) { // First / does NOT count!
        		previousPath = new String(retryPath); // Avoid infinite loops.
                retryPath = StringUtils.defaultString(StringUtils.substring(StringUtils
                        .substringBeforeLast(retryPath, "."), (StringUtils.startsWith(retryPath, "/") && (StringUtils.indexOf(retryPath, "/", 1) > 0)) ? StringUtils.indexOf(retryPath, "/", 1) : 0));
                retryPath = retryPath.startsWith("/") ? retryPath : ("/" + retryPath);
                mapping = (ActionMapping) moduleConfig.findActionConfig(retryPath);
        	}
        }

        if (mapping != null)
        {
            request.setAttribute(Globals.MAPPING_KEY, mapping);
            return (mapping);
        }

        // Locate the mapping for unknown paths (if any)
        ActionConfig configs[] = moduleConfig.findActionConfigs();
        for (int i = 0; i < configs.length; i++)
        {
            if (configs[i].getUnknown())
            {
                mapping = (ActionMapping) configs[i];
                request.setAttribute(Globals.MAPPING_KEY, mapping);
                return (mapping);
            }
        }

        // No mapping can be found to process this request
        log.error("Invalid path: " + path);

        // debug only on admin instances
        if (ServerConfiguration.getInstance().isAdmin())
        {
            getOut().write("Invalid path: " + path);
        }
        return null;

    }

    private static WebContext getWebContext()
    {
        return ((WebContext) MgnlContext.getInstance());
    }

    /**
     * @return
     * @throws IOException
     */
    private static Writer getOut() throws IOException
    {
        Writer out = null;

        PageContext pageContext = getWebContext().getPageContext();

        if (pageContext != null)
        {
            out = pageContext.getOut();
        }
        else
        {
            out = getWebContext().getResponse().getWriter();
        }
        return out;
    }

}
