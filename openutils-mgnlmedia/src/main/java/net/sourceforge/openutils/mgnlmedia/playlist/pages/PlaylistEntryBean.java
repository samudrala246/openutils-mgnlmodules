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

package net.sourceforge.openutils.mgnlmedia.playlist.pages;

/**
 * @author dschivo
 */
public class PlaylistEntryBean
{

    private String handle;

    private String media;

    private String mediaHandle;

    private String mediaDialog;

    private String thumbnail;

    private String type;

    private String title;

    private String description;

    private String tags;

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
     * Returns the media.
     * @return the media
     */
    public String getMedia()
    {
        return media;
    }

    /**
     * Sets the media.
     * @param media the media to set
     */
    public void setMedia(String media)
    {
        this.media = media;
    }

    /**
     * Returns the mediaHandle.
     * @return the mediaHandle
     */
    public String getMediaHandle()
    {
        return mediaHandle;
    }

    /**
     * Sets the mediaHandle.
     * @param mediaHandle the mediaHandle to set
     */
    public void setMediaHandle(String mediaHandle)
    {
        this.mediaHandle = mediaHandle;
    }

    /**
     * Returns the mediaDialog.
     * @return the mediaDialog
     */
    public String getMediaDialog()
    {
        return mediaDialog;
    }

    /**
     * Sets the mediaDialog.
     * @param mediaDialog the mediaDialog to set
     */
    public void setMediaDialog(String mediaDialog)
    {
        this.mediaDialog = mediaDialog;
    }

    /**
     * Returns the thumbnail.
     * @return the thumbnail
     */
    public String getThumbnail()
    {
        return thumbnail;
    }

    /**
     * Sets the thumbnail.
     * @param thumbnail the thumbnail to set
     */
    public void setThumbnail(String thumbnail)
    {
        this.thumbnail = thumbnail;
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
     * Returns the tags.
     * @return the tags
     */
    public String getTags()
    {
        return tags;
    }

    /**
     * Sets the tags.
     * @param tags the tags to set
     */
    public void setTags(String tags)
    {
        this.tags = tags;
    }

}
