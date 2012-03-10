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

package net.sourceforge.openutils.mgnlmedia.media.zip;

import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import net.sourceforge.openutils.mgnlmedia.media.utils.MediaLoadUtils;

import org.apache.commons.lang.StringUtils;


/**
 * Default zip importer. Simply takes files in the provided zip file and store them in the media repository.
 * @author Danilo Ghirardelli
 */
public class DefaultZipImporter implements ZipImporter
{

    /**
     * {@inheritDoc}
     */
    public void importFromZip(ZipFile zip, String parentPath) throws ZipImporterException
    {
        try
        {
            Enumeration< ? extends ZipEntry> entries = zip.entries();
            while (entries.hasMoreElements())
            {
                ZipEntry entry = entries.nextElement();

                String path = entry.getName();

                if (!entry.isDirectory())
                {
                    InputStream inputStream = zip.getInputStream(entry);

                    String parent = StringUtils.trimToEmpty(parentPath) + "/" + path;
                    parent = StringUtils.replace(parent, "\\", "/");
                    parent = StringUtils.replace(parent, "//", "/");
                    if (!parent.startsWith("/"))
                    {
                        parent = "/" + parent;
                    }

                    String filename = StringUtils.substringAfterLast(parent, "/");

                    // skip OSX artifacts "._"
                    // ".DS_Store" (Mac OS)
                    // "Thumbs.db",
                    if (StringUtils.startsWith(filename, ".") || StringUtils.equalsIgnoreCase(filename, "Thumbs.db"))
                    {
                        continue;
                    }

                    parent = StringUtils.substringBeforeLast(parent, "/");

                    MediaLoadUtils.loadEntry(
                        inputStream,
                        StringUtils.defaultIfEmpty(parent, "/fromzip"),
                        filename,
                        false);

                }
            }
        }
        catch (Exception e)
        {
            throw new ZipImporterException(e.getMessage(), e);
        }
    }
}
