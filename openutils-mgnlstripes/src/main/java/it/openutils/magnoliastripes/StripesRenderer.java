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

import info.magnolia.cms.core.Content;
import info.magnolia.cms.core.ItemType;
import info.magnolia.cms.core.NodeData;
import info.magnolia.context.MgnlContext;
import info.magnolia.context.WebContext;
import info.magnolia.rendering.context.RenderingContext;
import info.magnolia.rendering.engine.RenderException;
import info.magnolia.rendering.renderer.JspRenderer;
import info.magnolia.rendering.template.RenderableDefinition;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.stripes.config.Configuration;
import net.sourceforge.stripes.controller.DispatcherServlet;
import net.sourceforge.stripes.controller.StripesConstants;
import net.sourceforge.stripes.controller.StripesFilter;
import net.sourceforge.stripes.controller.StripesRequestWrapper;
import net.sourceforge.stripes.exception.StripesServletException;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * <p>
 * A Magnolia paragraph renderer that delegates to Stripes actions. Most of the code is just a cut and paste from
 * Stripes' {@link DispatcherServlet}, adapted to work withing magnolia by:
 * </p>
 * <ul>
 * <li>wrap the request in order to modify the request path and provide a custom requestDispatcher</li>
 * <li>wrap the response in order to provide a custom Writer</li>
 * <li>use a fake servlet instance/context to setup a Stripe context (there is no servlet here)</li>
 * <li>injiect any paragraph property as a parameter</li>
 * </ul>
 * <p>
 * <strong>Todo:</strong>
 * </p>
 * <ul>
 * <li>Handle multipart forms in request wrapper (should be easy to do)</li>
 * <li>A better way of handling multivalued properties in paragraph</li>
 * <li>Handle binary properties in paragraph</li>
 * </ul>
 * @author fgiust
 * @version $Id$
 */
public class StripesRenderer extends JspRenderer //implements ParagraphRenderer
{

    private StripesDispatcherServlet stripesDispatcherServlet = new StripesDispatcherServlet();

    /**
     * Logger.
     */
    private Logger log = LoggerFactory.getLogger(StripesRenderer.class);

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onRender(Node content, RenderableDefinition definition, RenderingContext renderingCtx,
        Map<String, Object> ctx, String templateScript) throws RenderException
    {

        Map<String, String[]> templateDataMap = contentToMap(getTemplateContent());
        Map<String, String[]> nodeDataMap = contentToMap(
            MgnlContext.getAggregationState().getCurrentContent(),
            templateDataMap);

        try
        {
            renderCommon(definition.getTemplateScript(), nodeDataMap, (Writer) renderingCtx.getOutputProvider().getAppendable());
        }
        catch (IOException e)
        {
            throw new RenderException(e);
        }
    }

    protected Content getTemplateContent()
    {
        Collection ancestors = null;
        try
        {
            ancestors = MgnlContext.getAggregationState().getCurrentContent().getAncestors();
            for (Iterator iterator = ancestors.iterator(); iterator.hasNext();)
            {
                Content ancestor = (Content) iterator.next();
                if (ancestor.getItemType().equals(ItemType.PAGE))
                {
                    return ancestor;
                }
            }
        }
        catch (PathNotFoundException e1)
        {
            return null;
        }
        catch (RepositoryException e1)
        {
            return null;
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Map newContext()
    {
        return MgnlContext.getWebContext("StripesRenderer can only be used with a WebContext");
    }

    protected void renderCommon(String templatePath, Map<String, String[]> nodeDataMap, Writer out) throws IOException
    {

        WebContext webContext = (WebContext) MgnlContext.getInstance();
        HttpServletResponse response = new MgnlStripesResponseWrapper(webContext.getResponse(), out);

        HttpServletRequest request;
        try
        {
            HttpServletRequest unwrappedRequest = ((WebContext) MgnlContext.getInstance()).getRequest();

            HttpServletRequest originalreq = new MgnlStripesRequestWrapper(
                unwrappedRequest,
                templatePath,
                nodeDataMap,
                out);

            // locale needs to be reset here
            Configuration config = StripesFilter.getConfiguration();
            Locale locale = config.getLocalePicker().pickLocale(unwrappedRequest);

            request = new StripesRequestWrapperExt(originalreq, locale);

        }
        catch (StripesServletException e)
        {
            throw new RuntimeException(e);
        }

        // required in 4.4
        request.setAttribute("info.magnolia.cms.filters.WebContainerResources", Boolean.TRUE);

        // force include instead of forwarding.
        request.setAttribute(StripesConstants.REQ_ATTR_INCLUDE_PATH, templatePath);

        try
        {
            stripesDispatcherServlet.service(request, response);
        }
        catch (ServletException e)
        {
            throw new RuntimeException(e);
        }

    }

    /**
     * @param content paragraph node
     * @return a map of Strings (converted nodedata)
     */
    @SuppressWarnings("unchecked")
    protected Map<String, String[]> contentToMap(Content content, Map<String, String[]> nodeDataMap)
    {
        Collection<NodeData> paragraphsData = content.getNodeDataCollection();
        for (NodeData nodeData : paragraphsData)
        {
            String name = StringUtils.replaceChars(nodeData.getName(), "{}", "[]");
            String value = nodeData.getString();
            if (StringUtils.contains(name, "multiple"))
            {
                nodeDataMap.put(name, StringUtils.split(value, "\n"));
            }
            else
            {
                nodeDataMap.put(name, new String[]{value });
            }
        }
        return nodeDataMap;
    }

    /**
     * @param content paragraph node
     * @return a map of Strings (converted nodedata)
     */
    @SuppressWarnings("unchecked")
    protected Map<String, String[]> contentToMap(Content content)
    {
        Map<String, String[]> nodeDataMap = new HashMap<String, String[]>();
        return contentToMap(content, nodeDataMap);
    }

    /**
     * just needed to make the protected doPost() accessible
     * @author fgiust
     * @version $Id$
     */
    protected class StripesDispatcherServlet extends DispatcherServlet
    {

        /**
         * Stable serialVersionUID.
         */
        private static final long serialVersionUID = 42L;

        /**
         * {@inheritDoc}
         */
        @Override
        public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException
        {
            super.service(request, response);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public ServletContext getServletContext()
        {
            return ((WebContext) MgnlContext.getInstance()).getServletContext();
        }

    }

    /**
     * just needed to make the protected setLocale() accessible
     * @author fgiust
     * @version $Id$
     */
    protected class StripesRequestWrapperExt extends StripesRequestWrapper
    {

        /**
         * @param request
         * @throws StripesServletException
         */
        public StripesRequestWrapperExt(HttpServletRequest request, Locale locale) throws StripesServletException
        {
            super(request);
            setLocale(locale);
        }

    }

}
