/**
 *
 * Simplecache module for Magnolia CMS (http://www.openmindlab.com/lab/products/simplecache.html)
 * Copyright(C) 2010-2013, Openmind S.r.l. http://www.openmindonline.it
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

package net.sourceforge.openutils.mgnlsimplecache.filesystem;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import net.sourceforge.openutils.mgnlsimplecache.managers.CacheHeaders;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;


/**
 * @author Manuel Molaschi
 * @author Fabrizio Giustina
 * @version $Id$
 */
public class SimpleCacheHeaders implements CacheHeaders, Serializable
{

    /**
     * 
     */
    private static final long serialVersionUID = 42L;

    private boolean error;

    private int status;

    private String message;

    private Multimap<String, String> headers = HashMultimap.create(10, 1);

    /**
     * Logger.
     */
    private Logger log = LoggerFactory.getLogger(SimpleCacheHeaders.class);

    /**
     * {@inheritDoc}
     */
    public void apply(HttpServletResponse response)
    {
        if (isError())
        {
            try
            {
                if (StringUtils.isNotBlank(message))
                {
                    response.sendError(status, message);
                }
                else
                {
                    response.sendError(status);
                }
            }
            catch (IOException ex)
            {

            }
        }
        else if (isRedirect())
        {
            Collection<String> values = headers.get("Location");
            if (values == null || values.size() == 0)
            {
                throw new IllegalStateException("Cannot find location to redirect to");
            }
            response.setStatus(status);
            response.setHeader("Location", values.iterator().next());
            response.setHeader("Connection", "close");
        }
        else
        {
            addHeaders(response);
        }
    }

    /**
     * Set the headers in the response object
     */
    protected void addHeaders(HttpServletResponse response)
    {
        final Multimap<String, String> headers = getHeaders();

        for (String header : headers.keySet())
        {

            if (response.containsHeader(header))
            {
                continue;
            }

            final Collection<String> values = headers.get(header);
            final Iterator<String> valIt = values.iterator();
            while (valIt.hasNext())
            {
                final String val = valIt.next();
                log.debug("Adding header {}: {}", header, val);
                response.addHeader(header, val);
            }
        }
    }

    /**
     * Returns the headers.
     * @return the headers
     */
    public Multimap<String, String> getHeaders()
    {
        return headers;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isError()
    {
        return error;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isNotModified()
    {
        return status == HttpServletResponse.SC_NOT_MODIFIED;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isRedirect()
    {
        return status == HttpServletResponse.SC_MOVED_PERMANENTLY || status == HttpServletResponse.SC_MOVED_TEMPORARILY;
    }

    /**
     * Sets the error.
     * @param error the error to set
     */
    public void setError(boolean error)
    {
        this.error = error;
    }

    /**
     * Sets the status.
     * @param status the status to set
     */
    public void setStatus(int status)
    {
        this.status = status;
    }

    /**
     * Sets the message.
     * @param message the message to set
     */
    public void setMessage(String message)
    {
        this.message = message;
    }

    /**
     * {@inheritDoc}
     */
    public void serialize(OutputStream stream) throws IOException
    {
        Multimap<String, String> hdrs = getHeaders();

        for (String header : hdrs.keySet())
        {

            final Collection<String> values = hdrs.get(header);
            final Iterator<String> valIt = values.iterator();
            while (valIt.hasNext())
            {
                writeToStream(header, stream);
                writeToStream(":", stream);
                writeToStream(valIt.next(), stream);
                writeToStream(SystemUtils.LINE_SEPARATOR, stream);
            }
        }

    }

    private void writeToStream(String string, OutputStream stream) throws IOException
    {
        IOUtils.write(string, stream, "utf-8");
    }

    /**
     * {@inheritDoc}
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
    public void deserialize(InputStream stream) throws IOException
    {

        Multimap<String, String> hmap = getHeaders();

        List<String> lines = IOUtils.readLines(stream, "utf-8");

        for (String line : lines)
        {

            int separator = StringUtils.indexOf(line, ':');
            hmap.put(StringUtils.substring(line, 0, separator), StringUtils.substring(line, separator + 1));
        }
    }

}
