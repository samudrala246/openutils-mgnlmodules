/**
 *
 * E-learning Module for Magnolia CMS (http://www.openmindlab.com/lab/products/lms.html)
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

package net.sourceforge.openutils.mgnllms.lms.types;

import info.magnolia.cms.beans.runtime.MultipartForm;
import info.magnolia.cms.core.Content;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;


/**
 * @author luca boati
 * @version $Id: $
 */
public interface LmsTypeHandler
{

    /**
     * languages
     */
    List<String> LANGUAGES = Arrays.asList("en", "en_us", "it", "fr", "de");

    /**
     * init handler
     * @param typeDefinitionNode type definition node
     */
    void init(Content typeDefinitionNode);

    /**
     * return thumbnail absolute url
     * @param node node
     * @return url
     */
    String getUrl(Content node);

    /**
     * @param node
     * @param options
     * @return
     */
    String getUrl(Content node, Map<String, String> options);

    /**
     * return filename
     * @param node node
     * @return filename
     */
    String getFilename(Content node);

    /**
     * return filename
     * @param node node
     * @return filename
     */
    String getFullFilename(Content node);

    /**
     * return title
     * @param node node
     * @return title
     */
    String getTitle(Content node);

    /**
     * return tags
     * @param node node
     * @return tags
     */
    String getTags(Content node);

    /**
     * return description
     * @param node node
     * @return description
     */
    String getDescription(Content node);

    /**
     * return abstract
     * @param node node
     * @return abstract
     */
    String getAbstract(Content node);

    /**
     * Called from dialog when saving a node
     * @param node node saving
     * @param form multipart form
     * @param request request
     * @return true if continue saving
     */
    boolean onPostSave(Content node, MultipartForm form, HttpServletRequest request);

    /**
     * Get the name for a new node
     * @param parent parent node
     * @param form multipart form
     * @param request request
     * @return new name
     */
    String getNewNodeName(Content parent, MultipartForm form, HttpServletRequest request);
}
