/**
 *
 * SimpleMedia Module for Magnolia CMS (http://www.openmindlab.com/lab/products/media.html)
 * Copyright(C) 2008-2013, Openmind S.r.l. http://www.openmindonline.it
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

package net.sourceforge.openutils.mgnlmedia.media.types;

import info.magnolia.cms.beans.runtime.MultipartForm;
import info.magnolia.cms.security.AccessDeniedException;

import java.io.File;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.servlet.http.HttpServletRequest;


/**
 * Media Type Handler Interface
 * @author molaschi
 * @version $Id$
 */
public interface MediaTypeHandler
{

    String METADATA_NAME = "media_name";

    String METADATA_WIDTH = "media_width";

    String METADATA_HEIGHT = "media_height";

    String METADATA_BITDEPTH = "media_bitdepth";

    String METADATA_DURATION = "media_duration";

    String METADATA_FRAMERATE = "media_framerate";

    String METADATA_EXTENSION = "media_extension";

    String METADATA_SIZE = "media_size";

    /**
     * init handler
     * @param typeDefinitionNode type definition node
     */
    void init(Node typeDefinitionNode);

    /**
     * return true if media has no binary-data
     * @param media media
     * @return true if media has no binary-data
     */
    boolean isExternal(Node media);

    /**
     * return thumbnail absolute url
     * @param media media
     * @return url
     */
    String getUrl(Node media);

    /**
     * @param media
     * @param options
     * @return url
     */
    String getUrl(Node media, Map<String, String> options);

    /**
     * return thumbnail absolute url
     * @param media media
     * @return url
     */
    String getThumbnailUrl(Node media);

    /**
     * return thumbnail absolute url
     * @param media media
     * @return url
     */
    String getPreviewUrl(Node media);

    /**
     * return filename
     * @param media media
     * @return filename
     */
    String getFilename(Node media);

    /**
     * return filename
     * @param media media
     * @return filename
     */
    String getExtension(Node media);

    /**
     * return filename
     * @param media media
     * @return filename
     */
    String getFullFilename(Node media);

    /**
     * return title
     * @param media media
     * @return title
     */
    String getTitle(Node media);

    /**
     * return tags
     * @param media media
     * @return tags
     */
    String getTags(Node media);

    /**
     * return description
     * @param media media
     * @return description
     */
    String getDescription(Node media);

    /**
     * return abstract
     * @param media media
     * @return abstract
     */
    String getAbstract(Node media);

    /**
     * Returns the basic media info (file type, size for images, ...)
     * @param media media
     * @return a formatted string for media info
     */
    Map<String, String> getMediaInfo(Node media);

    /**
     * Called from dialog when saving a media
     * @param media media saving
     * @return true if continue saving
     */
    boolean onPostSave(Node media);

    /**
     * Get the name for a new node
     * @param form multipart form
     * @param request current request
     * @return new name
     */
    String getNewNodeName(MultipartForm form, HttpServletRequest request);

    /**
     * Get the name of the nodedata for the image used for preview
     * @return the name of the nodedata for the image used for preview
     */
    String getPreviewImageNodeDataName();

    /**
     * Called when a media is going to be associated with a node
     * @param media media
     * @param parentNode node to be associated
     * @param configNode configuration node
     * @param name property name
     * @param request current request
     * @param form request form
     * @param type typee
     * @param valueType value type
     * @param isRichEditValue is rich edit
     * @param encoding encoding
     * @return true on success
     * @exception RepositoryException repository exception
     * @exception AccessDeniedException access denied exception
     */
    boolean onSavingPropertyMedia(Node media, Node parentNode, Node configNode, String name,
        HttpServletRequest request, MultipartForm form, int type, int valueType, int isRichEditValue, int encoding)
        throws RepositoryException, AccessDeniedException;

    /**
     * save a media file to a newly created media content
     * @param media media Node to save to
     * @param f file input stream
     * @param cleanFileName file name without extension
     * @param extension file extension
     * @exception RepositoryException exception working on repository
     * @exception AccessDeniedException exception accessing node
     */
    void saveFromZipFile(Node media, File f, String cleanFileName, String extension) throws AccessDeniedException,
        RepositoryException;
    
    /**
     * Notify module stopping to handler
     */
    void stop();
}
