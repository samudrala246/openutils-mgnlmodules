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

package net.sourceforge.openutils.mgnlsimplecache.lock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import net.sourceforge.openutils.mgnlsimplecache.managers.CachedItem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author Manuel Molaschi
 * @author Fabrizio Giustina
 * @version $Id$
 */
public abstract class LockableCacheContent implements CachedItem
{

    /**
     * Logger.
     */
    private Logger log = LoggerFactory.getLogger(LockableCacheContent.class);

    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    private ReadLock readLock;

    private WriteLock writeLock;

    /**
     *
     */
    public LockableCacheContent()
    {
        readLock = lock.readLock();
        writeLock = lock.writeLock();
    }

    public boolean lockToRead()
    {
        try
        {
            return readLock.tryLock(60, TimeUnit.SECONDS);
        }
        catch (InterruptedException e)
        {
            return false;
        }
    }

    public boolean lockToWrite()
    {
        if (lock.isWriteLockedByCurrentThread())
        {
            RuntimeException ex = new RuntimeException("Double call to write lock while caching!!!");
            ex.fillInStackTrace();
            log.error("Exception getting lock for writing on cache: ", ex);
            throw ex;
        }
        return writeLock.tryLock();
    }

    public void waitForWritingLock()
    {
        if (isWritingLocked())
        {
            try
            {
                writeLock.tryLock(10, TimeUnit.SECONDS);
            }
            catch (InterruptedException e)
            {

            }
            finally
            {
                int holdCount = lock.getWriteHoldCount();
                for (int i = 0; i < holdCount; i++)
                {
                    writeLock.unlock();
                }
            }
        }
    }

    public void releaseLockToWrite()
    {
        if (lock.isWriteLockedByCurrentThread())
        {
            writeLock.unlock();
        }
    }

    public void releaseLockToRead()
    {
        readLock.unlock();
    }

    public boolean isWritingLocked()
    {
        return lock.isWriteLocked();
    }
}
