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

package net.sourceforge.openutils.mgnlmedia.media.pages;

import info.magnolia.cms.core.MetaData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;

import net.sourceforge.openutils.mgnlcriteria.jcr.query.AdvancedResult;


/**
 * Store media info for rendering in {@link MediaFolderViewPage}
 * @author molaschi
 * @version $Id: MediaFolderViewPage.java 1366 2009-09-05 08:56:03Z molaschi $
 */
public class MediaBean
{

    private String handle;

    private String name;

    private String filename;

    private String title;

    private String description;

    private String thumbnailUrl;

    private String previewUrl;

    private String dialog;

    private Node content;

    private MetaData metaData;

    private String uuid;

    private String type;

    private String icon;

    private AdvancedResult usedInWebPages = AdvancedResult.EMPTY_RESULT;

    private Map<String, AdvancedResult> usedInNodes = new HashMap<String, AdvancedResult>();

    private Integer numberOfReferences;

    private boolean canPublish;

    private boolean writable;

    private Map<String, String> mediaInfo;

    private boolean external;

    /**
     * Returns the metaData.
     * @return the metaData
     */
    public MetaData getMetaData()
    {
        return metaData;
    }

    /**
     * Sets the metaData.
     * @param metaData the metaData to set
     */
    public void setMetaData(MetaData metaData)
    {
        this.metaData = metaData;
    }

    /**
     * Returns the handle.
     * @return the handle
     */
    public String getHandle()
    {
        return handle;
    }

    /**
     * Sets the handle.
     * @param handle the handle to set
     */
    public void setHandle(String handle)
    {
        this.handle = handle;
    }

    /**
     * Returns the name.
     * @return the name
     */
    public String getName()
    {
        return name;
    }

    /**
     * Sets the name.
     * @param name the name to set
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Returns the filename.
     * @return the filename
     */
    public String getFilename()
    {
        return filename;
    }

    /**
     * Sets the filename.
     * @param filename the filename to set
     */
    public void setFilename(String filename)
    {
        this.filename = filename;
    }

    /**
     * Returns the description.
     * @return the description
     */
    public String getDescription()
    {
        return description;
    }

    /**
     * Sets the description.
     * @param description the description to set
     */
    public void setDescription(String description)
    {
        this.description = description;
    }

    /**
     * Returns the thumbnailUrl.
     * @return the thumbnailUrl
     */
    public String getThumbnailUrl()
    {
        return thumbnailUrl;
    }

    /**
     * Sets the thumbnailUrl.
     * @param thumbnailUrl the thumbnailUrl to set
     */
    public void setThumbnailUrl(String thumbnailUrl)
    {
        this.thumbnailUrl = thumbnailUrl;
    }

    /**
     * Returns the previewUrl.
     * @return the previewUrl
     */
    public String getPreviewUrl()
    {
        return previewUrl;
    }

    /**
     * Sets the previewUrl.
     * @param previewUrl the previewUrl to set
     */
    public void setPreviewUrl(String previewUrl)
    {
        this.previewUrl = previewUrl;
    }

    /**
     * Returns the content.
     * @return the content
     */
    public Node getContent()
    {
        return content;
    }

    /**
     * Sets the content.
     * @param content the content to set
     */
    public void setContent(Node content)
    {
        this.content = content;
    }

    /**
     * Returns the uuid.
     * @return the uuid
     */
    public String getUuid()
    {
        return uuid;
    }

    /**
     * Sets the uuid.
     * @param uuid the uuid to set
     */
    public void setUuid(String uuid)
    {
        this.uuid = uuid;
    }

    /**
     * Returns the usedInWebPages.
     * @return the usedInWebPages
     */
    public AdvancedResult getUsedInWebPages()
    {
        return usedInWebPages;
    }

    /**
     * Sets the usedInWebPages.
     * @param usedInWebPages the usedInWebPages to set
     */
    public void setUsedInWebPages(AdvancedResult usedInWebPages)
    {
        this.usedInWebPages = usedInWebPages;
    }

    /**
     * Returns the usedInUris.
     * @return the usedInUris
     */
    public Map<String, AdvancedResult> getUsedInNodes()
    {
        return usedInNodes;
    }

    /**
     * Sets the usedInUris.
     * @param usedInUris the usedInUris to set
     */
    public void setUsedInNodes(Map<String, AdvancedResult> usedInUris)
    {
        this.usedInNodes = usedInUris;
    }

    /**
     * Returns the canPublish.
     * @return the canPublish
     */
    public boolean isCanPublish()
    {
        return canPublish;
    }

    /**
     * Sets the canPublish.
     * @param canPublish the canPublish to set
     */
    public void setCanPublish(boolean canPublish)
    {
        this.canPublish = canPublish;
    }

    /**
     * Returns the writable.
     * @return the writable
     */
    public boolean isWritable()
    {
        return writable;
    }

    /**
     * Sets the writable.
     * @param writable the writable to set
     */
    public void setWritable(boolean writable)
    {
        this.writable = writable;
    }

    /**
     * Returns the mediaInfo
     * @return the mediaInfo
     */
    public Map<String, String> getMediaInfo()
    {
        return mediaInfo;
    }

    /**
     * Sets the mediaInfo.
     * @param mediaInfo the mediaInfo to set
     */
    public void setMediaInfo(Map<String, String> mediaInfo)
    {
        this.mediaInfo = mediaInfo;
    }

    public boolean isExternal()
    {
        return external;
    }

    public void setExternal(boolean external)
    {
        this.external = external;
    }

    /**
     * Returns the title.
     * @return the title
     */
    public String getTitle()
    {
        return title;
    }

    /**
     * Sets the title.
     * @param title the title to set
     */
    public void setTitle(String title)
    {
        this.title = title;
    }

    /**
     * Returns the dialog.
     * @return the dialog
     */
    public String getDialog()
    {
        return dialog;
    }

    /**
     * Sets the dialog.
     * @param dialog the dialog to set
     */
    public void setDialog(String dialog)
    {
        this.dialog = dialog;
    }

    /**
     * Returns the type.
     * @return the type
     */
    public String getType()
    {
        return type;
    }

    /**
     * Sets the type.
     * @param type the type to set
     */
    public void setType(String type)
    {
        this.type = type;
    }

    /**
     * Returns the icon.
     * @return the icon
     */
    public String getIcon()
    {
        return icon;
    }

    /**
     * Sets the icon.
     * @param icon the icon to set
     */
    public void setIcon(String icon)
    {
        this.icon = icon;
    }

    public Integer getNumberOfReferences()
    {
        return numberOfReferences;
    }

    public void setNumberOfReferences(Integer numberOfReferences)
    {
        this.numberOfReferences = numberOfReferences;
    }

}