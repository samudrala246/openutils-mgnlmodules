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
package it.openutils.mgnlutils.util;

import info.magnolia.cms.beans.config.ServerConfiguration;
import info.magnolia.init.MagnoliaConfigurationProperties;
import info.magnolia.rendering.context.RenderingContext;
import info.magnolia.rendering.engine.RenderException;
import info.magnolia.rendering.engine.RenderExceptionHandler;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;

import javax.inject.Inject;
import javax.jcr.RepositoryException;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Simple exception renderer that avoid the ugly yellow box that popped up in magnolia 4.5. Just let the exception flow
 * up to tomcat, which usually shows a more meaningful error page.
 * @author fgiust
 * @version $Id$
 */
public class ClassicRenderExceptionHandler implements RenderExceptionHandler
{

    private static Logger log = LoggerFactory.getLogger(ClassicRenderExceptionHandler.class);

    private ServerConfiguration serverConfiguration;

    private MagnoliaConfigurationProperties configurationProperties;

    @Inject
    public ClassicRenderExceptionHandler(ServerConfiguration config)
    {
        this.serverConfiguration = config;
    }

    @Inject
    public void setConfigurationProperties(MagnoliaConfigurationProperties configurationProperties)
    {
        this.configurationProperties = configurationProperties;
    }

    @Override
    public void handleException(RenderException renderException, RenderingContext renderingContext)
    {
        String path;
        try
        {
            path = renderingContext.getCurrentContent().getPath();
        }
        catch (RepositoryException e)
        {
            path = "Can't read content";
        }
        String id = renderingContext.getRenderableDefinition().getId();

        String msg = "Error while rendering ["
            + path
            + "] with template ["
            + id
            + "]: "
            + ExceptionUtils.getMessage(renderException);
        if (serverConfiguration.isAdmin() || configurationProperties.getBooleanProperty("magnolia.develop"))
        {
            log.error(msg, renderException);

            throw new RuntimeRenderException(renderException);
        }
        else
        {
            log.error(msg, renderException);

            try
            {
                PrintWriter out = getPrintWriterFor(renderingContext.getAppendable());
                out.write("\n <!-- ");
                out.write(msg);
                out.write("\n-->\n");

                out.flush();
            }

            catch (IOException e)
            {
                throw new RuntimeException("Can't log template exception.", e);
            }
        }

    }

    private PrintWriter getPrintWriterFor(Writer out)
    {
        return (out instanceof PrintWriter) ? (PrintWriter) out : new PrintWriter(out);
    }

    protected void inPublicMode(String msg, RenderException renderException, PrintWriter out)
    {
        log.error(msg, renderException);
    }

}
