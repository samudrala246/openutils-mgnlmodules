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

package net.sourceforge.openutils.mgnlmedia.media.configuration;

import info.magnolia.cms.core.MgnlNodeType;

import java.util.Map;

import javax.jcr.Node;


/**
 * @author fgiust
 * @version $Id$
 */
public interface MediaConfigurationManager
{

    String NT_FOLDER = MgnlNodeType.NT_CONTENT;

    String NT_MEDIA = "mgnl:media";

    String NT_RESOLUTIONS = "mgnl:resolutions";

    String getURIMappingPrefix();

    MediaTypeConfiguration getMediaTypeConfigurationFromMedia(Node media);

    Map<String, MediaTypeConfiguration> getTypes();

    MediaTypeConfiguration getMediaHandlerFromExtension(String extension);
}
