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

import it.openutils.mgnlstruts11.render.StrutsRenderer;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.RequestProcessor;
import org.apache.struts.config.ForwardConfig;


/**
 * A request processor implementation that makes struts dispatching work inside magnolia.
 * @author fgiust
 * @version $Id$
 */
public class MgnlRequestProcessor extends RequestProcessor
{

    /**
     * {@inheritDoc}
     */
    @Override
    protected String processPath(HttpServletRequest request, HttpServletResponse response) throws IOException
    {

        String path = (String) request.getAttribute(StrutsRenderer.PARAGRAPH_PATH);
        if (path != null)
        {
            log.debug("Processing path " + path);
            return (path);
        }

        return super.processPath(request, response);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void processForwardConfig(HttpServletRequest request, HttpServletResponse response, ForwardConfig forward)
        throws IOException, ServletException
    {
        MgnlRequestProcessorHelper.doProcessForwardConfig(request, response, forward);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doForward(String uri, HttpServletRequest request, HttpServletResponse response) throws IOException,
        ServletException
    {
        // forwards are translated to include when running inside magnolia
        doInclude(uri, request, response);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doInclude(String uri, HttpServletRequest request, HttpServletResponse response) throws IOException,
        ServletException
    {
        MgnlRequestProcessorHelper.doInclude(uri, request, response);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected ActionMapping processMapping(HttpServletRequest request, HttpServletResponse response, String path)
        throws IOException
    {
        return MgnlRequestProcessorHelper.doProcessMapping(request, response, path, moduleConfig);
    }
}
