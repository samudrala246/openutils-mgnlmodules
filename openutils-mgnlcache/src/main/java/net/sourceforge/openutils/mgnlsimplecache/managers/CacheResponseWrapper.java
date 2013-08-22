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

package net.sourceforge.openutils.mgnlsimplecache.managers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;


/**
 * @author Manuel Molaschi
 * @author Fabrizio Giustina
 * @version $Id$
 */
public class CacheResponseWrapper extends HttpServletResponseWrapper
{

    private OutputStream cachingStream;

    private PrintWriter cachingWriter;

    private CachedItem content;

    private boolean redirect;

    private boolean error;

    public CacheResponseWrapper(HttpServletResponse response, CachedItem content, OutputStream cachingStream)
    {
        super(response);
        this.cachingStream = cachingStream;
        this.content = content;
    }

    /**
     * @see javax.servlet.ServletResponseWrapper#getOutputStream()
     */
    @Override
    public ServletOutputStream getOutputStream() throws IOException
    {
        if (cachingStream == null)
        {
            cachingStream = new ByteArrayOutputStream();
        }
        return new MultiplexServletOutputStream(super.getOutputStream(), cachingStream);
    }

    @Override
    public PrintWriter getWriter() throws IOException
    {
        if (cachingWriter == null)
        {
            String encoding = getCharacterEncoding();
            cachingWriter = encoding != null
                ? new PrintWriter(new OutputStreamWriter(getOutputStream(), encoding))
                : new PrintWriter(new OutputStreamWriter(getOutputStream()));
        }

        return cachingWriter;
    }

    @Override
    public void flushBuffer() throws IOException
    {
        try
        {
            super.flushBuffer();
        }
        catch (IOException e)
        {
            error = true;
            throw e;
        }

        if (cachingStream != null)
        {
            cachingStream.flush();
        }

        if (cachingWriter != null)
        {
            cachingWriter.flush();
        }
    }

    @Override
    public void reset()
    {
        super.reset();
        resetStreamAndWriter();
    }

    @Override
    public void resetBuffer()
    {
        super.resetBuffer();
        resetStreamAndWriter();
    }

    protected void resetStreamAndWriter()
    {
        if (cachingStream != null && cachingStream instanceof ResetableBufferedOutputStream)
        {
            try
            {
                cachingStream = content.resetWriting(cachingStream);
            }
            catch (IOException e)
            {
                // do nothing
            }
        }
        cachingWriter = null;
    }

    /**
     * Returns the redirect.
     * @return the redirect
     */
    public boolean isRedirect()
    {
        return redirect;
    }

    /**
     * Returns the error.
     * @return the error
     */
    public boolean isError()
    {
        return error;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendError(int sc) throws IOException
    {
        super.sendError(sc);
        error = true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendError(int sc, String msg) throws IOException
    {
        super.sendError(sc, msg);
        error = true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendRedirect(String location) throws IOException
    {
        super.sendRedirect(location);
        redirect = true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setStatus(int sc, String sm)
    {
        super.setStatus(sc, sm);
        if (sc == HttpServletResponse.SC_MOVED_PERMANENTLY || sc == HttpServletResponse.SC_MOVED_TEMPORARILY)
        {
            redirect = true;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setStatus(int sc)
    {
        super.setStatus(sc);
        if (sc == HttpServletResponse.SC_MOVED_PERMANENTLY || sc == HttpServletResponse.SC_MOVED_TEMPORARILY)
        {
            redirect = true;
        }
    }

    public static class MultiplexServletOutputStream extends ServletOutputStream
    {

        private final OutputStream stream1;

        private final OutputStream stream2;

        public MultiplexServletOutputStream(OutputStream stream1, OutputStream stream2)
        {
            this.stream1 = stream1;
            this.stream2 = stream2;
        }

        @Override
        public void write(int value) throws IOException
        {
            stream1.write(value);
            stream2.write(value);
        }

        @Override
        public void write(byte[] value) throws IOException
        {
            stream1.write(value);
            stream2.write(value);
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException
        {
            stream1.write(b, off, len);
            stream2.write(b, off, len);
        }

        @Override
        public void flush() throws IOException
        {
            stream1.flush();
            stream2.flush();
        }

        @Override
        public void close() throws IOException
        {
            try
            {
                stream1.close();
            }
            finally
            {
                stream2.close();
            }
        }
    }

    @Override
    public void setDateHeader(String name, long date)
    {
        super.setDateHeader(name, date);
        removeHeader(name);
        appendHeader(name, Long.toString(date));
    }

    @Override
    public void addDateHeader(String name, long date)
    {
        super.addDateHeader(name, date);
        appendHeader(name, Long.toString(date));
    }

    @Override
    public void setHeader(String name, String value)
    {
        super.setHeader(name, value);
        removeHeader(name);
        appendHeader(name, value);
    }

    @Override
    public void addHeader(String name, String value)
    {
        super.addHeader(name, value);
        appendHeader(name, value);
    }

    @Override
    public void setIntHeader(String name, int value)
    {
        super.setIntHeader(name, value);
        removeHeader(name);
        appendHeader(name, Integer.toString(value));
    }

    @Override
    public void addIntHeader(String name, int value)
    {
        super.addIntHeader(name, value);
        appendHeader(name, Integer.toString(value));
    }

    private void appendHeader(String name, String value)
    {
        content.getCacheHeaders().getHeaders().put(name, value);
    }

    private void removeHeader(String name)
    {
        content.getCacheHeaders().getHeaders().removeAll(name);
    }

}
