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

import info.magnolia.module.admininterface.TemplatedMVCHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.openutils.mgnlmobile.filters.MobileFilter;


/**
 * @author molaschi
 * @version $Id: $
 */
public class MobilePreviewPage extends TemplatedMVCHandler
{

    private String deviceId;

    private String url;

    private DevicePreview device;

    /**
     * @param name
     * @param request
     * @param response
     */
    public MobilePreviewPage(String name, HttpServletRequest request, HttpServletResponse response)
    {
        super(name, request, response);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String show()
    {
        device = MobilePreviewManager.getInstance().getDeviceById(MobileFilter.getDevice().getId());
        return super.show();
    }

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
     * Returns the device.
     * @return the device
     */
    public DevicePreview getDevice()
    {
        return device;
    }

    /**
     * Sets the device.
     * @param device the device to set
     */
    public void setDevice(DevicePreview device)
    {
        this.device = device;
    }

    /**
     * Returns the url.
     * @return the url
     */
    public String getUrl()
    {
        return url;
    }

    /**
     * Sets the url.
     * @param url the url to set
     */
    public void setUrl(String url)
    {
        this.url = url;
    }

}
