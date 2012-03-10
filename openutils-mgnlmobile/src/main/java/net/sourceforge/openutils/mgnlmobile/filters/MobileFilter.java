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

package net.sourceforge.openutils.mgnlmobile.filters;

import info.magnolia.cms.filters.InterceptFilter;
import info.magnolia.cms.filters.OncePerRequestAbstractMgnlFilter;
import info.magnolia.context.MgnlContext;
import info.magnolia.objectfactory.Components;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.wurfl.core.Device;
import net.sourceforge.wurfl.core.DeviceNotDefinedException;
import net.sourceforge.wurfl.core.MarkUp;
import net.sourceforge.wurfl.core.WURFLManager;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author Luca Boati
 * @version $Id: $
 */
public class MobileFilter extends OncePerRequestAbstractMgnlFilter
{

    private static final String DEVICE_ID = "mgnlDeviceId";

    private static final String DEVICE = "mgnlDevice";

    private static final String MARKUP = "mgnlMarkup";

    private static final String IS_MOBILE = "mgnlIsMobile";

    private static final String IS_MOBILE_ACTIVE = "mgnlMobileActive";

    /**
     * Logger.
     */
    private Logger log = LoggerFactory.getLogger(MobileFilter.class);

    private MobileWURFLManager mobileWURFLManager;

    public static boolean isMobileRequest()
    {
        return MgnlContext.isWebContext() && BooleanUtils.isTrue((Boolean) MgnlContext.getWebContext().getRequest().getAttribute(IS_MOBILE));
    }

    public static boolean isActive()
    {
        return MgnlContext.isWebContext() && BooleanUtils.isTrue((Boolean) MgnlContext.getWebContext().getRequest().getAttribute(IS_MOBILE_ACTIVE));
    }

    public static Device getDevice()
    {
        return (Device) MgnlContext.getWebContext().getRequest().getAttribute(DEVICE);
    }

    public static Device getMarkUp()
    {
        return (Device) MgnlContext.getWebContext().getRequest().getAttribute(MARKUP);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void init(FilterConfig filterConfig) throws ServletException
    {
        if (this.isEnabled())
        {
            super.init(filterConfig);

            if (mobileWURFLManager == null)
            {
                mobileWURFLManager = Components.getComponentProvider().newInstance(MobileWURFLManager.class);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
        throws IOException, ServletException
    {
        if (mobileWURFLManager != null)
        {
            request.setAttribute(IS_MOBILE_ACTIVE, true);
            WURFLManager wurfl = mobileWURFLManager.getWURFLManager();
            try
            {
                // check if device is forced
                Device device = getDeviceFromParameterOrSession(request);

                // try to get it from request
                if (device == null)
                {
                    device = wurfl.getDeviceForRequest(request);
                }

                if (device != null)
                {
                    // log capabilities
                    log.debug("Device: " + device.getId());
                    if (log.isDebugEnabled() && MapUtils.isNotEmpty(device.getCapabilities()))
                    {
                        log.debug("Device capabilities:");
                        for (Object key : device.getCapabilities().keySet())
                        {
                            log.debug("{}: {}", new Object[]{key, device.getCapabilities().get(key) });
                        }
                    }

                    // Markup
                    MarkUp markUp = device.getMarkUp();
                    log.debug("MarkUp: " + markUp);

                    request.setAttribute(MARKUP, markUp);
                    request.setAttribute(DEVICE, device);

                    // check if it is mobile
                    if (StringUtils.isNotBlank(device.getCapability("mobile_browser")))
                    {
                        request.setAttribute(IS_MOBILE, true);
                    }
                }
            }
            catch (DeviceNotDefinedException e)
            {
                log.warn(e.getMessage());
            }

            // request.getRequestDispatcher("WEB-INF/jsp/" + jspView).forward(request, response);
        }
        chain.doFilter(request, response);
    }

    public Device getDeviceFromParameterOrSession(HttpServletRequest request)
    {
        // if preview is closing remove device from session (if present)
        if (StringUtils.isNotBlank(request.getParameter(InterceptFilter.INTERCEPT))
            && "preview".equalsIgnoreCase(request.getParameter(InterceptFilter.INTERCEPT))
            && "false".equalsIgnoreCase(request.getParameter("mgnlPreview"))
            && request.getSession(true).getAttribute(DEVICE_ID) != null)
        {
            request.getSession().removeAttribute(DEVICE_ID);
            return null;
        }

        String deviceId = null;
        // check if it is forced and start preview Session
        if (StringUtils.isNotBlank(request.getParameter(DEVICE_ID))
            && request.getSession(true).getAttribute(DEVICE_ID) == null)
        {
            deviceId = request.getParameter(DEVICE_ID);
            if (mobileWURFLManager.getWURFLUtils().isDeviceDefined(deviceId))
            {
                log.debug("Forced device with id: {}", deviceId);
                request.getSession(true).setAttribute(DEVICE_ID, deviceId);
            }
            else
            {
                log.warn("Cannot find device with id {}", deviceId);
                return null;
            }
        }
        if (StringUtils.isBlank(deviceId))
        {
            deviceId = (String) request.getSession(true).getAttribute(DEVICE_ID);
        }
        if (StringUtils.isNotBlank(deviceId))
        {
            return mobileWURFLManager.getWURFLUtils().getDeviceById(deviceId);
        }
        return null;
    }

    /**
     * Returns the mobileWURFLManager.
     * @return the mobileWURFLManager
     */
    public MobileWURFLManager getMobileWURFLManager()
    {
        return mobileWURFLManager;
    }

    /**
     * Sets the mobileWURFLManager.
     * @param mobileWURFLManager the mobileWURFLManager to set
     */
    public void setMobileWURFLManager(MobileWURFLManager mobileWURFLManager)
    {
        this.mobileWURFLManager = mobileWURFLManager;
    }

}
