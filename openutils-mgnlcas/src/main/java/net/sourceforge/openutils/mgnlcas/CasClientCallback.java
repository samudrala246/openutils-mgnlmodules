/**
 *
 * CAS integration module for Magnolia CMS (http://www.openmindlab.com/lab/products/mgnlcas.html)
 * Copyright(C) 2007-2013, Openmind S.r.l. http://www.openmindonline.it
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

package net.sourceforge.openutils.mgnlcas;

import info.magnolia.cms.security.Security;
import info.magnolia.cms.security.auth.callback.AbstractHttpClientCallback;
import info.magnolia.context.MgnlContext;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.jasig.cas.client.util.CommonUtils;
import org.jasig.cas.client.validation.Assertion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Magnolia CAS Callback, based on CAS org.jasig.cas.client.authentication.AuthenticationFilter
 * @author fgiust
 * @version $Id: CasClientCallback.java 4896 2008-10-08 16:23:10Z manuel $
 */
public class CasClientCallback extends AbstractHttpClientCallback
{

    /**
     * Represents the constant for where the gateway flag will be located in session.
     */
    public static final String CONST_CAS_GATEWAY = "_const_cas_gateway_";

    /**
     * Represents the constant for where the assertion will be located in session.
     */
    public static final String CONST_CAS_ASSERTION = "_const_cas_assertion_";

    private Logger log = LoggerFactory.getLogger(CasClientCallback.class);

    /**
     * Defines the parameter to look for for the artifact.
     */
    private String artifactParameterName = "ticket";

    /**
     * Defines the parameter to look for for the service.
     */
    private String serviceParameterName = "service";

    /**
     * Sets where response.encodeUrl should be called on service urls when constructed.
     */
    private boolean encodeServiceUrl = true;

    /**
     * The URL to the CAS Server login.
     */
    private String casServerLoginUrl;

    /**
     * Whether to send the renew request or not.
     */
    private boolean renew;

    /**
     * Whether to send the gateway request or not.
     */
    private boolean gateway;

    public final void setRenew(final boolean renew)
    {
        this.renew = renew;
    }

    public final void setGateway(final boolean gateway)
    {
        this.gateway = gateway;
    }

    public final void setCasServerLoginUrl(final String casServerLoginUrl)
    {
        this.casServerLoginUrl = casServerLoginUrl;
    }

    /**
     * Sets the artifactParameterName.
     * @param artifactParameterName the artifactParameterName to set
     */
    public void setArtifactParameterName(String artifactParameterName)
    {
        this.artifactParameterName = artifactParameterName;
    }

    /**
     * Sets the serviceParameterName.
     * @param serviceParameterName the serviceParameterName to set
     */
    public void setServiceParameterName(String serviceParameterName)
    {
        this.serviceParameterName = serviceParameterName;
    }

    /**
     * Sets the encodeServiceUrl.
     * @param encodeServiceUrl the encodeServiceUrl to set
     */
    public void setEncodeServiceUrl(boolean encodeServiceUrl)
    {
        this.encodeServiceUrl = encodeServiceUrl;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doCallback(HttpServletRequest request, HttpServletResponse response)
    {

        final HttpSession session = request.getSession(false);

        if (session != null)
        {
            // don't redirect to cas if user is already logged in
            if (MgnlContext.getUser() != null
                && !MgnlContext.getUser().getName().equals(Security.getAnonymousUser().getName()))
            {
                try
                {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                }
                catch (IOException e)
                {
                    // ignore
                }
                return;
            }
        }
        final String ticket = request.getParameter(this.artifactParameterName);
        final Assertion assertion = session != null ? (Assertion) session.getAttribute(CONST_CAS_ASSERTION) : null;
        final boolean wasGatewayed = session != null && session.getAttribute(CONST_CAS_GATEWAY) != null;

        if (CommonUtils.isBlank(ticket) && assertion == null && !wasGatewayed)
        {

            // String service = RequestUtils.absoluteUrl(request);
            log.debug("no ticket and no assertion found");
            if (this.gateway)
            {
                log.debug("setting gateway attribute in session");
                request.getSession(true).setAttribute(CONST_CAS_GATEWAY, "yes");
            }

            final String serviceUrl = CommonUtils.constructServiceUrl(
                request,
                response,
                null,
                RequestUtils.serverName(request),
                this.artifactParameterName,
                this.encodeServiceUrl);

            if (log.isDebugEnabled())
            {
                log.debug("Constructed service url: " + serviceUrl);
            }

            String urlToRedirectTo = CommonUtils.constructRedirectUrl(
                this.casServerLoginUrl,
                this.serviceParameterName,
                serviceUrl,
                this.renew,
                this.gateway);

            urlToRedirectTo = urlToRedirectTo + "&locale=" + request.getLocale().toString();

            if (log.isDebugEnabled())
            {
                log.debug("redirecting to \"" + urlToRedirectTo + "\"");
            }

            try
            {
                response.sendRedirect(urlToRedirectTo);
            }
            catch (IOException e)
            {
                log.error(e.getMessage(), e);
            }
            return;
        }

        if (session != null)
        {
            log.debug("removing gateway attribute from session");
            session.setAttribute(CONST_CAS_GATEWAY, null);
        }
    }

}
