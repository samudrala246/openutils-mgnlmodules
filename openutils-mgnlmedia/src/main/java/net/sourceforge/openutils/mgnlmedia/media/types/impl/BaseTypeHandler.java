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

package net.sourceforge.openutils.mgnlmedia.media.types.impl;

import info.magnolia.cms.beans.runtime.Document;
import info.magnolia.cms.beans.runtime.FileProperties;
import info.magnolia.cms.beans.runtime.MultipartForm;
import info.magnolia.cms.core.NodeData;
import info.magnolia.cms.i18n.I18nContentSupport;
import info.magnolia.cms.security.AccessDeniedException; 
import info.magnolia.jcr.util.MetaDataUtil;
import info.magnolia.jcr.util.NodeUtil;
import info.magnolia.jcr.util.PropertyUtil;
import info.magnolia.module.admininterface.SaveHandlerImpl;
import info.magnolia.objectfactory.Components;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;
import javax.jcr.ValueFormatException;
import javax.jcr.nodetype.NodeType;
import javax.servlet.http.HttpServletRequest;

import net.sourceforge.openutils.mgnlmedia.media.configuration.MediaConfigurationManager;
import net.sourceforge.openutils.mgnlmedia.media.tags.el.MediaEl;
import net.sourceforge.openutils.mgnlmedia.media.types.MediaTypeHandler;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Base implementation of MediaTypeHandler for common used method
 * @author molaschi
 * @version $Id$
 */
public abstract class BaseTypeHandler implements MediaTypeHandler
{

    /**
     * Nodedata name where original media Node is saved
     */
    public static final String ORGINAL_NODEDATA_NAME = "original";

    /**
     * Logger.
     */
    private Logger log = LoggerFactory.getLogger(BaseTypeHandler.class);

    private String type;

    /**
     * {@inheritDoc}
     */
    public void init(Node typeDefinitionNode)
    {
        try
        {
            type = typeDefinitionNode.getName();
        }
        catch (RepositoryException e)
        {
            // do nothing
        }
    }

    /**
     * {@inheritDoc}
     */
    public String getNewNodeName(MultipartForm form, HttpServletRequest request)
    {
        return request.getParameter(ORGINAL_NODEDATA_NAME + "_" + FileProperties.PROPERTY_FILENAME);
    }

    public String getPreviewImageNodeDataName()
    {
        return ORGINAL_NODEDATA_NAME;
    }

    /**
     * {@inheritDoc}
     */
    public boolean onSavingPropertyMedia(Node media, Node parentNode, Node configNode, String name,
        HttpServletRequest request, MultipartForm form, int type, int valueType, int isRichEditValue, int encoding)
        throws RepositoryException, AccessDeniedException
    {
        String uuid = request.getParameter(name);

        Property nd;

        if (!parentNode.hasProperty(name))
        {
            nd = parentNode.setProperty(name, uuid);
        }
        else
        {
            nd = parentNode.getProperty(name);
            if (nd.getType() == PropertyType.BINARY)
            {
                nd.remove();
                nd = parentNode.setProperty(name, uuid);
            }
            else
            {
                nd.setValue(uuid);
            }
        }

        return true;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("deprecation")
    public void saveFromZipFile(Node media, File f, String cleanFileName, String extension)
        throws AccessDeniedException, RepositoryException
    {
        Document doc = new Document(f, type + extension);
        doc.setExtention(extension);
        SaveHandlerImpl.saveDocument(info.magnolia.cms.util.ContentUtil.asContent(media), doc, ORGINAL_NODEDATA_NAME, cleanFileName, null);
        this.onPostSave(media);
    }

    /**
     * {@inheritDoc}
     */
    public boolean onPostSave(Node media)
    {
        try
        {
            String filename = getFilename(media);
            int p = StringUtils.lastIndexOf(filename, '/');
            String name = p != -1 ? filename.substring(p + 1) : filename;
            // lowercase at saving for case insensitive sorting
            name = StringUtils.lowerCase(name);
            if (!StringUtils.equals(name, PropertyUtil.getString(media, METADATA_NAME)))
            {
                media.setProperty(METADATA_NAME, name);
                media.getSession().save();
            }

            if (media.hasNode("resolutions"))
            {
                Collection<NodeData> nodedatas = info.magnolia.cms.util.ContentUtil
                    .asContent(media.getNode("resolutions"))
                    .getNodeDataCollection();
                for (NodeData nd : nodedatas)
                {
                    nd.delete();
                }
                media.getSession().save();
            }

            if (MediaEl.module().isSingleinstance())
            {
                MetaDataUtil.getMetaData(media).setActivated();
                media.getSession().save();
            }
        }
        catch (RepositoryException ex)
        {
            log.error("Error removing resolutions", ex);
        }

        return true;
    }

    /**
     * Get the default "original" nodedata
     * @param media media
     * @return default nodedata
     */
    protected Node getOriginalFileNodeData(Node media)
    {
        try
        {
            return media.getNode(ORGINAL_NODEDATA_NAME);
        }
        catch (PathNotFoundException e)
        {
            return null;
        }
        catch (RepositoryException e)
        {
            return null;
        }
    }

    public boolean isExternal(Node media)
    {
        try
        {
            return !media.hasProperty(ORGINAL_NODEDATA_NAME);
        }
        catch (RepositoryException e)
        {
            log.warn("Error testing for external media", e);
            return true;
        }
    }

    /**
     * {@inheritDoc}
     */
    public String getExtension(Node media)
    {
        return PropertyUtil.getString(getOriginalFileNodeData(media), FileProperties.PROPERTY_EXTENSION);
    }

    /**
     * {@inheritDoc}
     */
    public String getFilename(Node media)
    {
        return PropertyUtil.getString(getOriginalFileNodeData(media), FileProperties.PROPERTY_FILENAME);
    }

    /**
     * {@inheritDoc}
     */
    public String getFullFilename(Node media)
    {
        return getFilename(media)
            + (StringUtils.isNotBlank(getExtension(media)) ? "." + getExtension(media) : StringUtils.EMPTY);
    }

    /**
     * {@inheritDoc}
     */
    public String getUrl(Node media, Map<String, String> options)
    {
        String filenameEncoded = getFullFilename(media);
        try
        {
            filenameEncoded = URLEncoder.encode(filenameEncoded, "UTF-8");
        }
        catch (UnsupportedEncodingException e)
        {
            // should never happen
        }
        return MediaConfigurationManager.getInstance().getURIMappingPrefix()
            + NodeUtil.getPathIfPossible(media)
            + "/"
            + ORGINAL_NODEDATA_NAME
            + "/"
            + filenameEncoded;
    }

    /**
     * {@inheritDoc}
     */
    public String getUrl(Node media)
    {
        return getUrl(media, null);
    }

    /**
     * {@inheritDoc}
     */
    public String getPreviewUrl(Node media)
    {
        return getUrl(media);
    }

    /**
     * {@inheritDoc}
     */
    public String getTitle(Node media)
    {
        try
        {
            return Components.getComponent(I18nContentSupport.class).getProperty(media, "title").getString();
        }
        catch (ValueFormatException e)
        {
            return StringUtils.EMPTY;
        }
        catch (RepositoryException e)
        {
            return StringUtils.EMPTY;
        }
    }

    /**
     * {@inheritDoc}
     */
    public String getTags(Node media)
    {
        try
        {
            return Components.getComponent(I18nContentSupport.class).getProperty(media, "tags").getString();
        }
        catch (ValueFormatException e)
        {
            return StringUtils.EMPTY;
        }
        catch (RepositoryException e)
        {
            return StringUtils.EMPTY;
        }
    }

    /**
     * {@inheritDoc}
     */
    public String getDescription(Node media)
    {
        try
        {
            return Components.getComponent(I18nContentSupport.class).getProperty(media, "description").getString();
        }
        catch (ValueFormatException e)
        {
            return StringUtils.EMPTY;
        }
        catch (RepositoryException e)
        {
            return StringUtils.EMPTY;
        }
    }

    /**
     * {@inheritDoc}
     */
    public String getAbstract(Node media)
    {
        try
        {
            return Components.getComponent(I18nContentSupport.class).getProperty(media, "abstract").getString();
        }
        catch (ValueFormatException e)
        {
            return StringUtils.EMPTY;
        }
        catch (RepositoryException e)
        {
            return StringUtils.EMPTY;
        }
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, String> getMediaInfo(Node media)
    {

        Map<String, String> info = new LinkedHashMap<String, String>();

        try
        {
            Node originalFileNodeData = getOriginalFileNodeData(media);

            if (NodeUtil.isNodeType(originalFileNodeData, NodeType.NT_RESOURCE))
            {

                FileProperties fp = new FileProperties(info.magnolia.cms.util.ContentUtil.asContent(media), ORGINAL_NODEDATA_NAME);

                String extension = fp.getProperty(FileProperties.PROPERTY_EXTENSION);
                info.put(METADATA_EXTENSION, extension);

                String size = StringUtils.EMPTY;

                try
                {
                    size = fp.getProperty(FileProperties.PROPERTY_SIZE);
                }
                catch (NumberFormatException nfe)
                {
                    // just ignore, no file size info
                }
                info.put(METADATA_SIZE, size);

                int width = NumberUtils.toInt(fp.getProperty(FileProperties.PROPERTY_WIDTH));
                if (width > 0)
                {
                    info.put(METADATA_WIDTH, Integer.toString(width));
                }

                int height = NumberUtils.toInt(fp.getProperty(FileProperties.PROPERTY_HEIGHT));
                if (height > 0)
                {
                    info.put(METADATA_HEIGHT, Integer.toString(height));
                }
            }

            Collection<NodeData> propertyList = info.magnolia.cms.util.ContentUtil.asContent(media).getNodeDataCollection("media_*");
            for (NodeData property : propertyList)
            {
                addToInfo(media, info, property.getName());
            }
        }
        catch (Exception e)
        {
            // TODO: handle exception
        }

        return info;
    }

    /**
     * Adds a new metadata to the map, converting an existing nodedata
     * @param media main media node
     * @param info map containing metadata keys/values
     * @param key nodedata name
     */
    protected void addToInfo(Node media, Map<String, String> info, String key)
    {
        try
        {
            Property data = media.getProperty(key);

            String string = null;
            if (data.getType() == PropertyType.LONG || data.getType() == PropertyType.DOUBLE)
            {
                int numeric = (int) data.getLong();

                if (numeric > 0)
                {
                    string = String.valueOf(numeric);
                }
            }
            else
            {
                string = data.getString();
            }

            if (StringUtils.isNotEmpty(string))
            {
                info.put(key, string);
            }
        }
        catch (ValueFormatException e)
        {
            // do nothing
        }
        catch (PathNotFoundException e)
        {
            // do nothing
        }
        catch (RepositoryException e)
        {
            // do nothing
        }
    }

    /**
     * {@inheritDoc}
     */
    public void stop()
    {
        // Do nothing
    }

}
