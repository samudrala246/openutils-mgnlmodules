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

import java.io.ByteArrayOutputStream;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;


/**
 * @author fgiust
 * @version $Id:WrappedResponse.java 344 2007-06-30 15:31:28Z fgiust $
 */
public class WrappedResponse extends HttpServletResponseWrapper
{

    /**
     * The buffered response.
     */
    private CharArrayWriter outputWriter;

    /**
     * The outputWriter stream.
     */
    private SimpleServletOutputStream servletOutputStream;

    /**
     * @param httpServletResponse the response to wrap
     */
    public WrappedResponse(HttpServletResponse httpServletResponse)
    {
        super(httpServletResponse);
        this.outputWriter = new CharArrayWriter();
        this.servletOutputStream = new SimpleServletOutputStream();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PrintWriter getWriter() throws IOException
    {
        return new PrintWriter(this.outputWriter);
    }

    /**
     * Flush the buffer, not the response.
     * @throws IOException if encountered when flushing
     */
    @Override
    public void flushBuffer() throws IOException
    {
        if (outputWriter != null)
        {
            this.outputWriter.flush();
            this.servletOutputStream.outputStream.reset();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ServletOutputStream getOutputStream() throws IOException
    {
        return this.servletOutputStream;
    }

    /**
     * @return buffered response
     */
    public char[] getContent()
    {
        return this.outputWriter.toCharArray();
    }

    class SimpleServletOutputStream extends ServletOutputStream
    {

        /**
         * My outputWriter stream, a buffer.
         */
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        /**
         * {@inheritDoc}
         */
        @Override
        public void write(int b)
        {
            this.outputStream.write(b);
        }

        /**
         * {@inheritDoc} Get the contents of the outputStream.
         * @return contents of the outputStream
         */
        @Override
        public String toString()
        {
            return this.outputStream.toString();
        }

        /**
         * Reset the wrapped ByteArrayOutputStream.
         */
        public void reset()
        {
            outputStream.reset();
        }
    }
}