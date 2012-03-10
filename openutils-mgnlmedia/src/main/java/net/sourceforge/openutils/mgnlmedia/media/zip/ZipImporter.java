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

import java.util.zip.ZipFile;


/**
 * Interface for zip importers. Using this, is possible to extract metadata from the zip file while importing the
 * images. Common cases are: - Title, description or other metadata are included in the media file name or folder. -
 * Metadata for all medias are included in a file (xml, txt) in the zip. - Metadata for the media is stored within the
 * zip file metadata. Implementing this interface you can cover any of the aforementioned cases.
 * @author Danilo Ghirardelli
 */
public interface ZipImporter
{

    /**
     * Stores in the media repository the images found in the zip file. Implementing this method you have control over
     * the (eventual) metadata handling.
     * @param zipFile The file from request
     * @param parentPath Repo folder selected for storing the images. May be null.
     * @throws ZipImporterException
     */
    void importFromZip(ZipFile zip, String parentPath) throws ZipImporterException;
}
