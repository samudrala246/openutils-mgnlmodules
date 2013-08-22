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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


/**
 * @author Manuel Molaschi
 * @author Fabrizio Giustina
 * @version $Id$
 */
public interface CachedItem
{

    /**
     * Starts writing in a cache element
     * @return the outputstream to write to
     * @throws IOException exception opening outputstream
     */
    OutputStream beginWrite() throws IOException;

    /**
     * Reset the output stream (from response.reset|resetBuffer)
     * @param outputStream output stream
     * @return resetted output stream
     * @throws IOException exception resetting output stream
     */
    OutputStream resetWriting(OutputStream outputStream) throws IOException;

    /**
     * Ends writing to the output stream
     * @param outputStream output stream
     * @return true if outputstream has contents
     * @throws IOException exception
     */
    boolean endWrite(OutputStream outputStream) throws IOException;

    /**
     * Starts reading a cache element
     * @param acceptGzip true to get gzipped stream
     * @return input stream to cached contents
     * @throws IOException exception getting input stream
     */
    InputStream beginRead(boolean acceptGzip) throws IOException;

    /**
     * Ends reading cache contents
     * @param inputStream input stream to cache contents
     * @return false if something fails in closing
     * @throws IOException
     */
    boolean endRead(InputStream inputStream) throws IOException;

    /**
     * Get cached content length
     * @param gzip if true get the length of gzipped stream
     * @return cached content length
     */
    long getBodyLength(boolean gzip);

    /**
     * Verify if gzip cached content exists
     * @return true if gzip cached content exists
     */
    boolean hasGzip();

    /**
     * Get cached content creation time
     * @return true
     */
    long getCreationTime();

    /**
     * Check if cache element is new
     * @return true if cache element is new
     */
    boolean isNew();

    /**
     * Empty cache content
     */
    void flush();

    CacheHeaders getCacheHeaders();

}
