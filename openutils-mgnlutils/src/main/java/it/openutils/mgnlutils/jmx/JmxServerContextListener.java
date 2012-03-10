/**
 *
 * Generic utilities for Magnolia CMS (http://www.openmindlab.com/lab/products/mgnlutils.html)
 * Copyright(C) 2009-2011, Openmind S.r.l. http://www.openmindonline.it
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

package it.openutils.mgnlutils.jmx;

import info.magnolia.cms.core.SystemProperty;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.util.HashMap;

import javax.management.MBeanServer;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * A ServletContextListener that can start a JMX connector. Requires the jmx.serviceUrl magnolia property to be set to
 * something similar to: jmx.serviceUrl=service:jmx:ws://myserver:9007/jmxws
 * @author fgiust
 * @version $Id$
 */
public class JmxServerContextListener implements ServletContextListener
{

    private JMXConnectorServer connectorServer;

    /**
     * Logger.
     */
    private Logger log = LoggerFactory.getLogger(JmxServerContextListener.class);

    /**
     * {@inheritDoc}
     */
    public void contextInitialized(ServletContextEvent sce)
    {

        String serviceUrl = SystemProperty.getProperty("jmx.serviceUrl");

        if (StringUtils.isBlank(serviceUrl))
        {
            return;
        }

        if (StringUtils.contains(serviceUrl, "[server]"))
        {

            String hostname = "127.0.0.1";

            try
            {
                hostname = InetAddress.getLocalHost().getHostName();

                if (StringUtils.contains(hostname, "."))
                {
                    hostname = StringUtils.substringBefore(hostname, ".");
                }
            }
            catch (UnknownHostException e)
            {
                // ignore
            }

            serviceUrl = StringUtils.replace(serviceUrl, "[server]", hostname);
        }

        try
        {
            MBeanServer server = ManagementFactory.getPlatformMBeanServer();

            // Create the JMX service URL.
            JMXServiceURL url = new JMXServiceURL(serviceUrl);

            // Create the connector server now.
            this.connectorServer = JMXConnectorServerFactory.newJMXConnectorServer(
                url,
                new HashMap<String, String>(),
                server);

            // Start the connector server asynchronously (in a separate thread).
            Thread connectorThread = new Thread()
            {

                public void run()
                {
                    try
                    {
                        connectorServer.start();
                    }
                    catch (IOException ex)
                    {
                        throw new RuntimeException("Could not start JMX connector server after delay", ex);
                    }
                }
            };

            connectorThread.setName("JMX Connector Thread [" + serviceUrl + "]");
            connectorThread.start();

            log.info("\n\n****\nJMX server started at url " + serviceUrl + "\n****\n");
        }
        catch (MalformedURLException e)
        {
            log
                .error("\n\n****\nUnable to start JMX server at url "
                    + serviceUrl
                    + " due to a "
                    + e.getClass().getName()
                    + " ("
                    + e.getMessage()
                    + ").\nYou need to add the jsr262-ri and jaxws-2.1.3 jars in the classpath in order to enable jmx over ws\n****\n");
        }
        catch (Throwable e)
        {
            log.error("Unable to start JMX server at url " + serviceUrl + " due to a " + e.getClass().getName(), e);
        }

    }

    /**
     * {@inheritDoc}
     */
    public void contextDestroyed(ServletContextEvent sce)
    {
        if (this.connectorServer == null)
        {
            return;
        }
        if (log.isInfoEnabled())
        {
            log.info("Stopping JMX connector server: " + this.connectorServer);
        }

        try
        {
            this.connectorServer.stop();
        }
        catch (IOException e)
        {
            // ignore
        }

    }

}
