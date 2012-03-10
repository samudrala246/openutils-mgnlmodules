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

import info.magnolia.cms.beans.config.ObservedManager;
import info.magnolia.cms.core.Content;
import info.magnolia.cms.util.ContentUtil;
import info.magnolia.content2bean.Content2BeanException;
import info.magnolia.content2bean.Content2BeanUtil;
import info.magnolia.objectfactory.Components;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author molaschi
 * @version $Id: $
 */
public class MobilePreviewManager extends ObservedManager
{

    public static MobilePreviewManager getInstance()
    {
        return Components.getSingleton(MobilePreviewManager.class);
    }

    /**
     * Logger.
     */
    private Logger log = LoggerFactory.getLogger(MobilePreviewManager.class);

    private Map<String, DevicePreview> devices = new HashMap<String, DevicePreview>();

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onClear()
    {
        devices.clear();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onRegister(Content node)
    {
        for (Content deviceNode : ContentUtil.collectAllChildren(node))
        {
            String deviceId = deviceNode.getName();
            DevicePreview device;
            try
            {
                device = (DevicePreview) Content2BeanUtil.toBean(deviceNode, DevicePreview.class);
                if (StringUtils.isBlank(device.getDeviceId()))
                {
                    device.setDeviceId(deviceId);
                }
                devices.put(device.getDeviceId(), device);
            }
            catch (Content2BeanException e)
            {
                log.error("Error converting node {} to DevicePreview", e);
            }
        }
    }

    public DevicePreview getDeviceById(String deviceId)
    {
        DevicePreview devicePreview = devices.get(deviceId);
        if (devicePreview == null)
        {
            // get first one
            for (DevicePreview dp : devices.values())
            {
                devicePreview = dp;
                break;
            }
        }
        return devicePreview;
    }

    public Collection<DevicePreview> getDevicesList()
    {
        return devices.values();
    }

}
