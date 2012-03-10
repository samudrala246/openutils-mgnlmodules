/**
 *
 * Mobile Module for Magnolia CMS (http://www.openmindlab.com/lab/products/mgnlmobile.html)
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

package net.sourceforge.openutils.mgnlmobile.preview;

/**
 * @author molaschi
 * @version $Id: $
 */
public class DevicePreview
{

    private String deviceId;

    private String deviceDescription;

    private int previewWidth;

    private int previewHeight;

    private int screenWidth;

    private int screenHeight;

    private int screenTop = -1;

    private int screenLeft = -1;

    private String previewImage;

    /**
     * Returns the deviceId.
     * @return the deviceId
     */
    public String getDeviceId()
    {
        return deviceId;
    }

    /**
     * Sets the deviceId.
     * @param deviceId the deviceId to set
     */
    public void setDeviceId(String deviceId)
    {
        this.deviceId = deviceId;
    }

    /**
     * Returns the previewWidth.
     * @return the previewWidth
     */
    public int getPreviewWidth()
    {
        return previewWidth;
    }

    /**
     * Sets the previewWidth.
     * @param previewWidth the previewWidth to set
     */
    public void setPreviewWidth(int previewWidth)
    {
        this.previewWidth = previewWidth;
    }

    /**
     * Returns the previewHeight.
     * @return the previewHeight
     */
    public int getPreviewHeight()
    {
        return previewHeight;
    }

    /**
     * Sets the previewHeight.
     * @param previewHeight the previewHeight to set
     */
    public void setPreviewHeight(int previewHeight)
    {
        this.previewHeight = previewHeight;
    }

    /**
     * Returns the screenWidth.
     * @return the screenWidth
     */
    public int getScreenWidth()
    {
        return screenWidth;
    }

    /**
     * Sets the screenWidth.
     * @param screenWidth the screenWidth to set
     */
    public void setScreenWidth(int screenWidth)
    {
        this.screenWidth = screenWidth;
    }

    /**
     * Returns the screenHeight.
     * @return the screenHeight
     */
    public int getScreenHeight()
    {
        return screenHeight;
    }

    /**
     * Sets the screenHeight.
     * @param screenHeight the screenHeight to set
     */
    public void setScreenHeight(int screenHeight)
    {
        this.screenHeight = screenHeight;
    }

    /**
     * Returns the screenTop.
     * @return the screenTop
     */
    public int getScreenTop()
    {
        if (screenTop == -1)
        {
            screenTop = (previewHeight - screenHeight) / 2;
        }
        return screenTop;
    }

    /**
     * Sets the screenTop.
     * @param screenTop the screenTop to set
     */
    public void setScreenTop(int screenTop)
    {
        this.screenTop = screenTop;
    }

    /**
     * Returns the screenLeft.
     * @return the screenLeft
     */
    public int getScreenLeft()
    {
        if (screenLeft == -1)
        {
            screenLeft = (previewWidth - screenWidth) / 2;
        }
        return screenLeft;
    }

    /**
     * Sets the screenLeft.
     * @param screenLeft the screenLeft to set
     */
    public void setScreenLeft(int screenLeft)
    {
        this.screenLeft = screenLeft;
    }

    /**
     * Returns the previewImage.
     * @return the previewImage
     */
    public String getPreviewImage()
    {
        return previewImage;
    }

    /**
     * Sets the previewImage.
     * @param previewImage the previewImage to set
     */
    public void setPreviewImage(String previewImage)
    {
        this.previewImage = previewImage;
    }

    /**
     * Returns the deviceDescription.
     * @return the deviceDescription
     */
    public String getDeviceDescription()
    {
        return deviceDescription;
    }

    /**
     * Sets the deviceDescription.
     * @param deviceDescription the deviceDescription to set
     */
    public void setDeviceDescription(String deviceDescription)
    {
        this.deviceDescription = deviceDescription;
    }

}
