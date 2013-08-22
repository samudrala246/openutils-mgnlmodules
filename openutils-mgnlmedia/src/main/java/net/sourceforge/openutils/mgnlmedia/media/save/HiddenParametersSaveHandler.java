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

package net.sourceforge.openutils.mgnlmedia.media.save;

import info.magnolia.cms.beans.runtime.MultipartForm;
import info.magnolia.cms.core.Content;
import info.magnolia.cms.core.ItemType;
import info.magnolia.cms.core.Path;
import info.magnolia.cms.security.AccessDeniedException;
import info.magnolia.cms.util.NodeDataUtil;
import info.magnolia.module.admininterface.FieldSaveHandler;

import java.util.Enumeration;

import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;
import javax.jcr.Value;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author ADMIN
 * @version $Id: $
 */
public class HiddenParametersSaveHandler implements FieldSaveHandler
{

    /**
     * Logger.
     */
    private Logger log = LoggerFactory.getLogger(HiddenParametersSaveHandler.class);

    /**
     * {@inheritDoc}
     */
    public void save(Content parentNode, Content configNode, String name, MultipartForm form, int type, int valueType,
        int isRichEditValue, int encoding) throws RepositoryException, AccessDeniedException
    {
        if (parentNode.hasContent(name))
        {
            parentNode.getContent(name).delete();
        }
        Content node = parentNode.createContent(name, ItemType.CONTENTNODE);
        for (Enumeration paramNames = form.getParameterNames(); paramNames.hasMoreElements();)
        {
            String paramName = (String) paramNames.nextElement();
            if (!StringUtils.startsWith(paramName, name + "."))
            {
                continue;
            }
            String[] paramValues = form.getParameterValues(paramName);

            Content paramNode = node.createContent(Path.getUniqueLabel(node, "0"), ItemType.CONTENTNODE);
            paramNode.setNodeData("name", StringUtils.removeStart(paramName, name + "."));
            Value[] jcrValues = new Value[paramValues.length];
            for (int i = 0; i < paramValues.length; i++)
            {
                jcrValues[i] = NodeDataUtil.createValue(paramValues[i], PropertyType.STRING);
            }
            paramNode.setNodeData("value", jcrValues);
        }
    }
}
