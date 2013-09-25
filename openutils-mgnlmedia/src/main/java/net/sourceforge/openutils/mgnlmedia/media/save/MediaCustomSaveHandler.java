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
import info.magnolia.cms.security.AccessDeniedException;
import info.magnolia.cms.util.NodeDataUtil;
import info.magnolia.context.MgnlContext;
import info.magnolia.module.admininterface.FieldSaveHandler;

import javax.jcr.ItemNotFoundException;
import javax.jcr.Node;
import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.servlet.http.HttpServletRequest;

import net.sourceforge.openutils.mgnlmedia.media.configuration.MediaConfigurationManager;
import net.sourceforge.openutils.mgnlmedia.media.configuration.MediaTypeConfiguration;
import net.sourceforge.openutils.mgnlmedia.media.lifecycle.MediaModule;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * {@link FieldSaveHandler} implementation that calls the onSavingPropertyMedia method on the handler of target media
 * type when a media is associated to a page
 * @author molaschi
 * @version $Id$
 */
public class MediaCustomSaveHandler implements FieldSaveHandler
{

    /**
     * Logger.
     */
    private Logger log = LoggerFactory.getLogger(MediaCustomSaveHandler.class);

    /**
     * {@inheritDoc}
     */
    public void save(Content parentNode, Content configNode, String name, MultipartForm form, int type, int valueType,
        int isRichEditValue, int encoding) throws RepositoryException, AccessDeniedException
    {
        HttpServletRequest request = MgnlContext.getWebContext().getRequest();
        String value = request.getParameter(name);

        if (StringUtils.isBlank(value))
        {
            for (String property : new String[]{name, name + "_width", name + "_height" })
            {
                if (parentNode.hasNodeData(property))
                {
                    if (parentNode.getNodeData(property).getType() != PropertyType.BINARY)
                    {
                        parentNode.deleteNodeData(property);
                    }
                }
            }

            return;
        }
        else
        {

            Session hm = MgnlContext.getJCRSession(MediaModule.REPO);

            try
            {
                Node media = hm.getNodeByIdentifier(value);

                MediaTypeConfiguration mtc = MediaConfigurationManager
                    .getInstance()
                    .getMediaTypeConfigurationFromMedia(media);
                mtc.getHandler().onSavingPropertyMedia(
                    media,
                    parentNode.getJCRNode(),
                    configNode.getJCRNode(),
                    name,
                    request,
                    form,
                    type,
                    valueType,
                    isRichEditValue,
                    encoding);
            }
            catch (ItemNotFoundException e)
            {
                log.warn("Missing media {} referenced in node {}", value, parentNode.getHandle());
            }

            boolean resizing = NodeDataUtil.getBoolean(configNode, "resizing", false);
            for (String dimKey : new String[]{"width", "height" })
            {
                String paramName = name + '_' + dimKey;
                long dimValue = NumberUtils.toLong(request.getParameter(paramName), -1);
                if (resizing && dimValue >= 0)
                {
                    NodeDataUtil.getOrCreateAndSet(parentNode, paramName, dimValue);
                }
                else if (parentNode.hasNodeData(paramName))
                {
                    parentNode.deleteNodeData(paramName);
                }
            }
        }
    }

}
