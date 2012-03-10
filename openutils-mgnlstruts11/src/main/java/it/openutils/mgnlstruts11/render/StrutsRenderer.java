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

package it.openutils.mgnlstruts11.render;

import info.magnolia.cms.core.Content;
import info.magnolia.context.MgnlContext;
import info.magnolia.context.WebContext;
import info.magnolia.module.templating.Paragraph;
import info.magnolia.module.templating.ParagraphRenderer;
import info.magnolia.voting.voters.DontDispatchOnForwardAttributeVoter;
import it.openutils.mgnlstruts11.process.MgnlMultipartRequestHandler;
import it.openutils.mgnlstruts11.process.MgnlRequestProcessor;
import it.openutils.mgnlstruts11.process.MgnlStrutsUtils;

import java.io.IOException;
import java.io.Writer;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionServlet;
import org.apache.struts.action.RequestProcessor;
import org.apache.struts.config.ForwardConfig;
import org.apache.struts.config.ModuleConfig;
import org.apache.struts.util.RequestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author fgiust
 * @version $Id$
 */
public class StrutsRenderer implements ParagraphRenderer
{

    public static final String PARAGRAPH_PATH = MgnlRequestProcessor.class.getName() + ".path";

    /**
     * Logger.
     */
    private Logger log = LoggerFactory.getLogger(StrutsRenderer.class);

    /**
     * {@inheritDoc}
     */
    public void render(Content content, Paragraph paragraph, Writer out) throws IOException
    {

        WebContext wc = ((WebContext) MgnlContext.getInstance());

        HttpServletRequest request = wc.getRequest();
        HttpServletResponse response = wc.getResponse();
        ServletContext servletContext = wc.getServletContext();

        request.setAttribute(MgnlStrutsUtils.ATTRIBUTE_ORIGINALURI, wc.getContextPath()
            + wc.getAggregationState().getOriginalURI());

        String actionParameter = MgnlContext.getParameter(MgnlStrutsUtils.PARAMETER_MGNLACTION);

        // TODO da verificare
        String urifornavigation = actionParameter;
        if (!StringUtils.isEmpty(urifornavigation))
        {

            if (!urifornavigation.startsWith("/do/"))
            {
                urifornavigation = "/do" + urifornavigation;
            }
            urifornavigation = request.getContextPath() + urifornavigation;
            request.setAttribute(MgnlStrutsUtils.ATTRIBUTE_URIFORNAVIGATION, urifornavigation);
        }

        request.setAttribute(DontDispatchOnForwardAttributeVoter.DONT_DISPATCH_ON_FORWARD_ATTRIBUTE, Boolean.TRUE);

        // force magnolia multipart handler
        request.setAttribute(Globals.MULTIPART_KEY, MgnlMultipartRequestHandler.class.getName());

        if (paragraph instanceof StrutsParagraph
            && StrutsParagraph.PARAGRAPHTYPE_FORWARD.equals(((StrutsParagraph) paragraph).getStrutsType())
            && actionParameter == null)
        {
            try
            {
                wc.include(paragraph.getTemplatePath(), out);
            }
            catch (ServletException e)
            {
                throw new RuntimeException(e);
            }
            return;
        }

        ActionServlet dispatcher = (ActionServlet) servletContext.getAttribute(MgnlStrutsServlet.DISPATCHER_KEY);

        if (dispatcher == null)
        {
            log.error(
                "An action Servlet instance can't be located, please check that {} is correctly configured in web.xml",
                MgnlStrutsServlet.class.getName());
            return;
        }

        if (actionParameter != null)
        {
            request.setAttribute(PARAGRAPH_PATH, actionParameter);
        }
        else
        {
            // expose the original struts path, needed by MgnlRequestProcessor
            request.setAttribute(PARAGRAPH_PATH, paragraph.getTemplatePath());
        }

        RequestUtils.selectModule(request, servletContext);
        try
        {
            ModuleConfig mc = getModuleConfig(request, servletContext);

            if (actionParameter != null)
            {

                ForwardConfig[] forwardConfigs = mc.findForwardConfigs();
                for (ForwardConfig forwardConfig : forwardConfigs)
                {
                    if (actionParameter.equals(forwardConfig.getName()))
                    {
                        try
                        {
                            log.info("Found global config: " + actionParameter + " -> " + forwardConfig.getPath());
                            request.setAttribute(PARAGRAPH_PATH, forwardConfig.getPath());
                            wc.include(forwardConfig.getPath(), out);
                        }
                        catch (ServletException e)
                        {
                            throw new RuntimeException(e);
                        }
                        return;
                    }
                }
            }
            RequestProcessor rp = getRequestProcessor(mc, servletContext, dispatcher);
            rp.process(request, response);
        }
        catch (ServletException e)
        {
            throw new StrutsProcessingException(e);
        }
    }

    /**
     * Return the module configuration object for the currently selected module.
     * @param request The servlet request we are processing
     * @since Struts 1.1
     */
    protected ModuleConfig getModuleConfig(HttpServletRequest request, ServletContext servletContext)
    {

        ModuleConfig config = (ModuleConfig) request.getAttribute(Globals.MODULE_KEY);
        if (config == null)
        {
            config = (ModuleConfig) servletContext.getAttribute(Globals.MODULE_KEY);
        }
        return (config);

    }

    /**
     * Look up and return the {@link RequestProcessor} responsible for the specified module, creating a new one if
     * necessary.
     * @param config The module configuration for which to acquire and return a RequestProcessor.
     * @exception ServletException if we cannot instantiate a RequestProcessor instance
     * @since Struts 1.1
     */
    protected synchronized RequestProcessor getRequestProcessor(ModuleConfig config, ServletContext servletContext,
        ActionServlet dispatcher) throws ServletException
    {

        return ((MgnlStrutsServlet) dispatcher).getRequestProcessor(config);
    }
}
