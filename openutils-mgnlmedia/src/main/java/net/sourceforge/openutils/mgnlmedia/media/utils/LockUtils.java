/**
 *
 * SimpleMedia Module for Magnolia CMS (http://www.openmindlab.com/lab/products/media.html)
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

package net.sourceforge.openutils.mgnlmedia.media.utils;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author molaschi
 * @version $Id: LockUtils.java 7734 2012-01-25 22:02:24Z manuel $
 */
public class LockUtils
{

    private Logger log = LoggerFactory.getLogger(LockUtils.class);

    private Object synchObject = new Object();

    private List<Object> locks = null;

    private int actualLock = 0;

    public LockUtils(int maxThreads)
    {
        locks = new ArrayList<Object>(maxThreads);
        for (int t = 0; t < maxThreads; t++)
        {
            locks.add(new Object());
        }
    }

    public Object nextLock()
    {
        synchronized (synchObject)
        {
            if (locks != null && locks.size() > 0)
            {
                Object ret = locks.get(actualLock);
                if (log.isDebugEnabled())
                {
                    log.debug("Returning lock n. {}, id {}", actualLock, ret.toString());
                }
                actualLock++;
                if (actualLock >= locks.size())
                {
                    actualLock = 0;
                }
                return ret;
            }
            return new Object();
        }
    }
}
