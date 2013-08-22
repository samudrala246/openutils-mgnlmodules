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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import net.sourceforge.openutils.mgnlsimplecache.managers.CachedItem;
import net.sourceforge.openutils.mgnlsimplecache.managers.ResetableBufferedOutputStream;


/**
 * @author Manuel Molaschi
 * @author Fabrizio Giustina
 * @version $Id$
 */
public class ResetableBufferedFileOutputStream extends ResetableBufferedOutputStream
{

    private File file;

    /**
     * @param out
     */
    public ResetableBufferedFileOutputStream(File f, CachedItem cacheContent) throws FileNotFoundException
    {
        super(new FileOutputStream(f), cacheContent);
        this.file = f;
    }

    public File getFile()
    {
        return file;
    }

}
