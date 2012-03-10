/**
 *
 * Spring integration module for Magnolia CMS (http://openutils.sourceforge.net/openutils-mgnlspring)
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

package it.openutils.mgnlspring;

import info.magnolia.cms.beans.config.ContentRepository;
import info.magnolia.cms.security.AccessManager;
import info.magnolia.cms.security.Permission;
import info.magnolia.context.MgnlContext;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opensymphony.module.sitemesh.Page;
import com.opensymphony.module.sitemesh.PageParser;
import com.opensymphony.module.sitemesh.parser.HTMLPageParser;


/**
 * @author fgiust
 * @version $Id:ContentBridgeTag.java 344 2007-06-30 15:31:28Z fgiust $
 */
public class ContentBridgeTag extends TagSupport
{

    /**
     * Stable serialVersionUID.
     */
    private static final long serialVersionUID = 42L;

    /**
     * Logger.
     */
    private Logger log = LoggerFactory.getLogger(ContentBridgeTag.class);

    /**
     * Url to be included.
     */
    private String url;

    /**
     * Sets the url.
     * @param url the url to set
     */
    public void setUrl(String url)
    {
        this.url = url;
    }

    protected String makeUrl(String contextPath, String requestURI, String actionParam, String baseActionUrl)
    {

        StringBuffer currentUrl = new StringBuffer();
        currentUrl.append(requestURI);

        String actionUrl = actionParam;

        if (actionUrl != null && !actionUrl.startsWith("/"))
        {

            if (!StringUtils.equals(actionUrl, baseActionUrl) && StringUtils.contains(baseActionUrl, "/"))
            {
                actionUrl = StringUtils.substringBeforeLast(baseActionUrl, "/") + "/" + actionUrl;
            }

            if (!actionUrl.startsWith("/"))
            {
                actionUrl = "/" + actionUrl;
            }
        }

        RewriteVarsThreadLocal.setContextPath(contextPath);
        RewriteVarsThreadLocal.setCurrentPageUrl(currentUrl.toString());

        return actionUrl;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int doStartTag() throws JspException
    {

        HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
        HttpServletResponse response = (HttpServletResponse) pageContext.getResponse();

        String requestURI = (String) request.getAttribute("javax.servlet.forward.request_uri");

        if (requestURI == null)
        {
            requestURI = request.getRequestURI();
        }

        String actionParam = url;

        if (request.getParameter("_action") != null)
        {
            actionParam = request.getParameter("_action");
        }

        if (requestURI.startsWith("//")) // buggy url set in javax.servlet.forward.request_uri?
        {
            requestURI = requestURI.substring(1);
        }

        String actionUrl = makeUrl(request.getContextPath(), requestURI, actionParam, url);

        if (!StringUtils.contains(actionUrl, "?"))
        {
            actionUrl = StringUtils.replaceOnce(actionUrl, "&", "?");
        }

        AccessManager am = MgnlContext.getAccessManager(ContentRepository.WEBSITE);
        if (!am.isGranted(actionUrl, Permission.READ))
        {
            log.info("User not allowed to read path {}", actionUrl);
            return super.doStartTag();
        }

        RequestDispatcher rd = pageContext.getServletConfig().getServletContext().getRequestDispatcher(actionUrl);

        WrappedResponse wresponse = new WrappedResponse(response);
        JspWriter out = pageContext.getOut();
        try
        {
            rd.include(request, wresponse);
        }
        catch (ServletException e)
        {
            log.error("error including " + actionUrl + ": " + e.getMessage(), e);
            e.printStackTrace(new PrintWriter(out));
        }
        catch (IOException e)
        {
            log.error("error including " + actionUrl + ": " + e.getMessage(), e);
            e.printStackTrace(new PrintWriter(out));
        }
        finally
        {
            RewriteVarsThreadLocal.setCurrentPageUrl(null);
            RewriteVarsThreadLocal.setContextPath(null);
        }

        char[] content = wresponse.getContent();

        if (content != null && content.length > 0)
        {
            PageParser parser = new HTMLPageParser();
            try
            {
                Page page = parser.parse(content);
                page.writeBody(out);
            }
            catch (IOException e)
            {
                log.error("error parsing " + actionUrl + ": " + e.getMessage(), e);
                e.printStackTrace(new PrintWriter(out));
            }
        }

        return super.doStartTag();
    }
}
