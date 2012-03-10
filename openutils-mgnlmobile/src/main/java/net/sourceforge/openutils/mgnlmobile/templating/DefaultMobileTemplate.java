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

package net.sourceforge.openutils.mgnlmobile.templating;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections.MapUtils;

import net.sourceforge.wurfl.core.Device;


/**
 * @author molaschi
 * @version $Id: $
 */
public class DefaultMobileTemplate extends MobileTemplate
{

    private String userAgent;

    private String id;

    private Map<String, String> capabilities = new HashMap<String, String>();

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean matchDevice(Device device)
    {
        if (id != null)
        {
            return device.getId().matches(id);
        }
        else if (userAgent != null)
        {
            return device.getUserAgent().matches(userAgent);
        }
        else if (MapUtils.isNotEmpty(capabilities))
        {
            for (String key : capabilities.keySet())
            {
                if (!device.getCapabilities().containsKey(key))
                {
                    return false;
                }
                if (!((String) device.getCapabilities().get(key)).matches(capabilities.get(key)))
                {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    /**
     * Returns the userAgent.
     * @return the userAgent
     */
    public String getUserAgent()
    {
        return userAgent;
    }

    /**
     * Sets the userAgent.
     * @param userAgent the userAgent to set
     */
    public void setUserAgent(String userAgent)
    {
        this.userAgent = userAgent;
    }

    /**
     * Returns the id.
     * @return the id
     */
    public String getId()
    {
        return id;
    }

    /**
     * Sets the id.
     * @param id the id to set
     */
    public void setId(String id)
    {
        this.id = id;
    }

    /**
     * Returns the capabilities.
     * @return the capabilities
     */
    public Map<String, String> getCapabilities()
    {
        return capabilities;
    }

    /**
     * Sets the capabilities.
     * @param capabilities the capabilities to set
     */
    public void setCapabilities(Map<String, String> capabilities)
    {
        this.capabilities = capabilities;
    }

    public void addCapability(String capability, String value)
    {
        this.capabilities.put(capability, value);
    }

}
