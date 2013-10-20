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
package net.sourceforge.openutils.mgnlmedia.externalvideo;

import info.magnolia.cms.beans.runtime.MultipartForm;

import java.util.Map;

import javax.jcr.Node;
import javax.servlet.http.HttpServletRequest;


/**
 * @author molaschi
 * @version $Id: $
 */
public interface ExternalVideoProvider
{

    void processVideo(Node media);

    boolean isAsyncUpload();

    String getUrl(Node media, Map<String, String> options);

    String getNewNodeName(MultipartForm form, HttpServletRequest request);

    String getName();

    String getUploadFileName(Node media);

    String getMediaUUIDFromFileName(String filename);

    void stop();

    String getThumbnailUrl(Node media);

    String getPreviewUrl(Node media);
    
    String getFilename(Node media);
}
