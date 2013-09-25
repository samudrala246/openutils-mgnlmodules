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

package net.sourceforge.openutils.mgnlmedia.media.dialog;

import info.magnolia.cms.beans.runtime.FileProperties;
import info.magnolia.cms.core.Content;
import info.magnolia.cms.core.NodeData;
import info.magnolia.cms.i18n.Messages;
import info.magnolia.cms.i18n.MessagesUtil;
import info.magnolia.cms.util.NodeDataUtil;
import info.magnolia.context.MgnlContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.jcr.ItemNotFoundException;
import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.openutils.mgnlcontrols.dialog.ConfigurableFreemarkerDialog;
import net.sourceforge.openutils.mgnlmedia.media.configuration.MediaConfigurationManager;
import net.sourceforge.openutils.mgnlmedia.media.configuration.MediaTypeConfiguration;
import net.sourceforge.openutils.mgnlmedia.media.lifecycle.MediaModule;
import net.sourceforge.openutils.mgnlmedia.media.save.MediaCustomSaveHandler;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This dialogs draws the control that allows to select a media from repository and store its uuid
 * @author molaschi
 * @version $Id$
 */
public class DialogSelectMedia extends ConfigurableFreemarkerDialog
{

    /**
     * Logger.
     */
    private Logger log = LoggerFactory.getLogger(DialogSelectMedia.class);

    private Long width;

    private Long height;

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<Object> readValues()
    {

        List<Object> values = new ArrayList<Object>();

        if (this.getStorageNode() != null)
        {
            try
            {
                // cycles on website content node to get multiple value
                int size = this.getStorageNode().getContent(this.getName()).getNodeDataCollection().size();
                for (int i = 0; i < size; i++)
                {
                    NodeData data = this.getStorageNode().getContent(this.getName()).getNodeData("" + i);
                    values.add(data.getString());
                }
            }
            catch (PathNotFoundException e)
            {
                // not yet existing: OK
            }
            catch (RepositoryException re)
            {
                log.error("can't set values", re);
            }
        }
        return values;
    }

    /**
     * {@inheritDoc}
     */

    @Override
    public void init(HttpServletRequest request, HttpServletResponse response, Content websiteNode, Content configNode)
        throws RepositoryException
    {
        super.init(request, response, websiteNode, configNode);
        if (StringUtils.isEmpty(getConfigValue("saveHandler")))
        {
            setConfig("saveHandler", MediaCustomSaveHandler.class.getName());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void addToParameters(Map<String, Object> parameters)
    {
        Node media = null;

        NodeData nd = null;
        if (getStorageNode() != null)
        {
            nd = getStorageNode().getNodeData(this.getName());
        }

        if (nd != null && nd.getType() == PropertyType.BINARY)
        {
            parameters.put("binaryfield", true);
        }
        else
        {
            if (this.getValue() != null && this.getValue().length() > 0)
            {
                try
                {
                    Session hm = MgnlContext.getJCRSession(MediaModule.REPO);
                    media = hm.getNodeByIdentifier(this.getValue());
                }
                catch (ItemNotFoundException ex)
                {
                    log.warn("Media not found: {}", value);
                }
                catch (RepositoryException ex)
                {
                    log.error("Error retrieving media " + value, ex);
                }
            }
        }

        parameters.put("thumbnailUrl", this.getThumbnailUrl(media, nd));
        parameters.put("msgs", this.getMessages());
        parameters.put("filename", this.getFilename(media, nd));

        try
        {
            parameters.put("handle", media != null ? media.getPath() : null);
        }
        catch (RepositoryException e)
        {
            log.error("RepositoryException {}", e);
        }

        if ("true".equals(this.getConfigValue("resizing")))
        {
            parameters.put("width", this.getWidth());
            parameters.put("height", this.getHeight());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Messages getMessages()
    {
        return MessagesUtil.chain("net.sourceforge.openutils.mgnlmedia.media.lang.messages", super.getMessages());
    }

    @Override
    protected String getPath()
    {
        return "dialog/selectMedia.ftl";
    }

    /**
     * return current media content
     * @return media content
     * @throws RepositoryException exception retrieving media
     */
    protected Node getMedia() throws RepositoryException
    {
        if (this.getValue() != null && this.getValue().length() > 0)
        {
            Session hm = MgnlContext.getJCRSession(MediaModule.REPO);
            return hm.getNodeByIdentifier(this.getValue());
        }
        return null;
    }

    /**
     * get thumbnail url
     * @param media
     * @param nd
     * @return thumbnail url
     */
    public String getThumbnailUrl(Node media, NodeData nd)
    {

        if (nd != null && nd.getType() == PropertyType.BINARY)
        {
            String url = new FileProperties(getStorageNode(), this.getName()).getProperty(FileProperties.PATH);
            if (StringUtils.isBlank(url))
            {
                return null;
            }
            return getRequest().getContextPath() + url;
        }

        if (media != null)
        {
            MediaTypeConfiguration mtc = MediaConfigurationManager.getInstance().getMediaTypeConfigurationFromMedia(
                media);
            return this.getRequest().getContextPath() + mtc.getHandler().getThumbnailUrl(media);
        }

        return null;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getValue()
    {
        // handle binary values (needed only for properties converted from the "file" type)
        NodeData nd = null;
        if (getStorageNode() != null)
        {
            nd = getStorageNode().getNodeData(this.getName());
        }

        if (nd != null && nd.getType() == PropertyType.BINARY)
        {
            return new FileProperties(getStorageNode(), this.getName()).getProperty(FileProperties.PATH);
        }

        return super.getValue();
    }

    /**
     * Filename
     * @param nd
     * @param media2
     * @return filename
     */
    public String getFilename(Node media, NodeData nd)
    {

        if (nd != null && nd.getType() == PropertyType.BINARY)
        {
            return new FileProperties(getStorageNode(), this.getName()).getProperty(FileProperties.NAME);
        }

        if (media != null)
        {
            MediaTypeConfiguration mtc = MediaConfigurationManager.getInstance().getMediaTypeConfigurationFromMedia(
                media);

            String filename = mtc.getHandler().getFilename(media);
            return StringUtils.contains(filename, "/") ? StringUtils.substringAfterLast(filename, "/") : filename;
        }

        return null;

    }

    public Long getWidth()
    {
        if (width == null)
        {
            if (getStorageNode() != null)
            {
                long w = NodeDataUtil.getLong(getStorageNode(), getName() + "_width", -1);
                width = w >= 0 ? w : null;
            }
        }
        return width;
    }

    public Long getHeight()
    {
        if (height == null)
        {
            if (getStorageNode() != null)
            {
                long h = NodeDataUtil.getLong(getStorageNode(), getName() + "_height", -1);
                height = h >= 0 ? h : null;
            }
        }
        return height;
    }

}
