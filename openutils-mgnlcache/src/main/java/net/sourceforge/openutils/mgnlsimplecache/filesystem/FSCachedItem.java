/**
 *
 * Simplecache module for Magnolia CMS (http://www.openmindlab.com/lab/products/simplecache.html)
 * Copyright(C) 2010-2012, Openmind S.r.l. http://www.openmindonline.it
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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import net.sourceforge.openutils.mgnlsimplecache.lock.LockableCacheContent;
import net.sourceforge.openutils.mgnlsimplecache.lock.NoArgsSynchedOp;
import net.sourceforge.openutils.mgnlsimplecache.lock.SynchCacheContentOperations;
import net.sourceforge.openutils.mgnlsimplecache.managers.CacheHeaders;
import net.sourceforge.openutils.mgnlsimplecache.managers.CachedItem;
import net.sourceforge.openutils.mgnlsimplecache.managers.ResetableBufferedOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;


/**
 * @author Manuel Molaschi
 * @author Fabrizio Giustina
 * @version $Id$
 */
public final class FSCachedItem extends LockableCacheContent
{

    private String filename;

    private String filenameGzip;

    private String filenameHeaders;

    private long size = -1;

    private long sizeGzip = -1;

    private long lastModified = -1;

    private boolean isnew;

    private boolean gzipable;

    private CacheHeaders cacheHeaders;

    private FSCachedItem(String filename, boolean isnew, boolean gzipable)
    {
        this.filename = filename;
        this.gzipable = gzipable;
        this.filenameGzip = this.filename + ".gz";
        this.filenameHeaders = this.filename + ".headers";
        this.isnew = isnew;
        this.cacheHeaders = new SimpleCacheHeaders();
    }

    public static FSCachedItem createNew(String filename, boolean gzipable)
    {
        FSCachedItem fsCachedItem = new FSCachedItem(filename, true, gzipable);
        try
        {
            SynchCacheContentOperations.doWrite(fsCachedItem, new NoArgsSynchedOp<Void>()
            {

                @Override
                public Void run(CachedItem c) throws Exception
                {
                    c.flush();
                    return null;
                }
            });
        }
        catch (Exception e)
        {

        }
        return fsCachedItem;
    }

    public static FSCachedItem reload(String filename)
    {
        FSCachedItem fsCachedItem = new FSCachedItem(filename, true, false);
        fsCachedItem.gzipable = fsCachedItem.hasGzip();
        return fsCachedItem;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isNew()
    {
        return isnew;
    }

    public long getTotalSizeOnDiskInBytes()
    {
        return size + sizeGzip;
    }

    /**
     * {@inheritDoc}
     */
    public long getCreationTime()
    {
        if (lastModified < 0)
        {
            lastModified = new File(filename).lastModified();
        }
        return lastModified;
    }

    /**
     * {@inheritDoc}
     */
    public boolean hasGzip()
    {
        // the call to getBodyLength() is needed to force the check if the size is not cached
        getBodyLength(true);
        return sizeGzip > 0;
    }

    /**
     * {@inheritDoc}
     */
    public InputStream beginRead(boolean acceptGzip) throws IOException
    {
        if (lockToRead())
        {
            File f = new File(acceptGzip ? filenameGzip : filename);
            if (f.exists())
            {
                FileInputStream fis = new FileInputStream(f);
                return new BufferedInputStream(fis);
            }
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    public boolean endRead(InputStream is) throws IOException
    {
        IOUtils.closeQuietly(is);
        releaseLockToRead();
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public ResetableBufferedOutputStream beginWrite() throws IOException
    {
        if (lockToWrite())
        {
            isnew = false;
            File f = new File(filename);
            f.getParentFile().mkdirs();
            return new ResetableBufferedFileOutputStream(f, this);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public boolean endWrite(OutputStream outputStream) throws IOException
    {

        if (outputStream == null)
        {
            releaseLockToWrite();
            return false;
        }

        try
        {
            IOUtils.closeQuietly(outputStream);

            File f = ((ResetableBufferedFileOutputStream) outputStream).getFile();
            if (f != null && f.exists() && f.length() > 0)
            {
                FileInputStream fis = new FileInputStream(f);
                byte[] gzipControlChars = new byte[2];
                fis.read(gzipControlChars);
                IOUtils.closeQuietly(fis);

                if (isGZipped(gzipControlChars))
                {
                    FileUtils.moveFile(new File(filename), new File(filenameGzip));
                    File fgz = new File(filenameGzip);
                    f = new File(filename);
                    InputStream is = new GZIPInputStream(new FileInputStream(fgz));
                    OutputStream os = new BufferedOutputStream(new FileOutputStream(f));
                    IOUtils.copyLarge(is, os);
                    IOUtils.closeQuietly(is);
                    IOUtils.closeQuietly(os);
                }
                else if (gzipable)
                {
                    File fgz = new File(filenameGzip);
                    OutputStream os = new GZIPOutputStream(new FileOutputStream(fgz));
                    InputStream is = new FileInputStream(f);
                    IOUtils.copyLarge(is, os);
                    IOUtils.closeQuietly(is);
                    IOUtils.closeQuietly(os);
                }

                size = new File(filename).length();
                sizeGzip = new File(filenameGzip).length();

                getCacheHeaders().getHeaders().put("x-server-cached", "true");

                OutputStream os = new BufferedOutputStream(new FileOutputStream(new File(filenameHeaders)));
                try
                {
                    getCacheHeaders().serialize(os);
                }
                finally
                {
                    IOUtils.closeQuietly(os);
                }

                lastModified = System.currentTimeMillis();
                return true;
            }

            size = 0;
            sizeGzip = 0;
            lastModified = System.currentTimeMillis();
            return false;
        }
        finally
        {
            releaseLockToWrite();
        }
    }

    /**
     * {@inheritDoc}
     */
    public long getBodyLength(boolean usegzipifavailable)
    {
        if (size < 0 || sizeGzip < 0)
        {
            sizeGzip = new File(filenameGzip).length();
            size = new File(filename).length();
        }

        return usegzipifavailable && sizeGzip > 0 ? sizeGzip : size;
    }

    /**
     * {@inheritDoc}
     */
    public ResetableBufferedOutputStream resetWriting(OutputStream outputStream) throws IOException
    {
        IOUtils.closeQuietly(outputStream);
        File f = ((ResetableBufferedFileOutputStream) outputStream).getFile();
        f.delete();

        return new ResetableBufferedFileOutputStream(f, this);
    }

    /**
     * {@inheritDoc}
     */
    public void flush()
    {
        File f = new File(filename);
        if (f.exists())
        {
            f.delete();
        }
        File fgz = new File(filenameGzip);
        if (fgz.exists())
        {
            fgz.delete();
        }
    }

    /**
     * Checks the first two bytes of the candidate byte array for the magic number 0x677a.
     */
    private boolean isGZipped(byte[] candidate)
    {

        final int GZIP_MAGIC_NUMBER_BYTE_1 = 31;
        final int GZIP_MAGIC_NUMBER_BYTE_2 = -117;

        if (candidate == null || candidate.length < 2)
        {
            return false;
        }

        return (candidate[0] == GZIP_MAGIC_NUMBER_BYTE_1 && candidate[1] == GZIP_MAGIC_NUMBER_BYTE_2);

    }

    /**
     * Returns the cacheHeaders.
     * @return the cacheHeaders
     */
    public CacheHeaders getCacheHeaders()
    {
        if (cacheHeaders == null)
        {

            File file = new File(filenameHeaders);

            SimpleCacheHeaders newHeaders = new SimpleCacheHeaders();

            if (file.exists())
            {

                File headersfile = new File(filenameHeaders);
                InputStream is = null;
                try
                {
                    is = new BufferedInputStream(new FileInputStream(headersfile));
                    newHeaders.deserialize(is);
                }
                catch (IOException e)
                {
                    // ignore
                }
                finally
                {
                    IOUtils.closeQuietly(is);
                }
            }

            // copy so we don't have to sync deserialize
            cacheHeaders = newHeaders;

        }
        return cacheHeaders;
    }
}
