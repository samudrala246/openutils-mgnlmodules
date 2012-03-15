/**
 *
 * CAS integration module for Magnolia CMS (http://www.openmindlab.com/lab/products/mgnlcas.html)
 * Copyright(C) 2007-2012, Openmind S.r.l. http://www.openmindonline.it
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

import info.magnolia.cms.security.SecuritySupport;
import info.magnolia.cms.security.User;
import info.magnolia.cms.security.auth.callback.RealmCallback;
import info.magnolia.cms.security.auth.login.LoginHandler;
import info.magnolia.cms.security.auth.login.LoginHandlerBase;
import info.magnolia.cms.security.auth.login.LoginResult;

import java.io.IOException;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.TextInputCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.jasig.cas.client.util.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author fgiust
 * @version $Id: CasLogin.java 4266 2008-09-25 08:18:30Z daniela $
 */
public class CasLogin extends LoginHandlerBase implements LoginHandler
{

    private static final Logger log = LoggerFactory.getLogger(CasLogin.class);

    /**
     * Defines the parameter to look for for the artifact.
     */
    private String artifactParameterName = "ticket";

    /**
     * The JAAS chain/module to use.
     */
    private String jaasChain = "magnolia";

    private String realm;

    private String casValidateUrl;

    /**
     * Sets the artifactParameterName.
     * @param artifactParameterName the artifactParameterName to set
     */
    public void setArtifactParameterName(String artifactParameterName)
    {
        this.artifactParameterName = artifactParameterName;
    }

    public String getJaasChain()
    {
        return this.jaasChain;
    }

    public void setJaasChain(String jaasChain)
    {
        this.jaasChain = jaasChain;
    }

    /**
     * Returns the realm.
     * @return the realm
     */
    public String getRealm()
    {
        return realm;
    }

    /**
     * Sets the realm.
     * @param realm the realm to set
     */
    public void setRealm(String realm)
    {
        this.realm = realm;
    }

    /**
     * Returns the casValidateUrl.
     * @return the casValidateUrl
     */
    public String getCasValidateUrl()
    {
        return casValidateUrl;
    }

    /**
     * Sets the casValidateUrl.
     * @param casValidateUrl the casValidateUrl to set
     */
    public void setCasValidateUrl(String casValidateUrl)
    {
        this.casValidateUrl = casValidateUrl;
    }

    public LoginResult handle(HttpServletRequest request, HttpServletResponse response)
    {

        final String ticket = request.getParameter(this.artifactParameterName);

        if (StringUtils.isNotEmpty(ticket))
        {
            String service = CommonUtils.constructServiceUrl(
                request,
                response,
                null,
                RequestUtils.serverName(request),
                this.artifactParameterName,
                true);

            // solo se è l'ultimo carattere
            if (service.lastIndexOf("/") == (service.length() - 1))
            {
                service = service.substring(0, service.lastIndexOf("/"));
            }
            CasCallBackHandler handler = new CasCallBackHandler(service, ticket, realm);

            return authenticateCas(handler, getJaasChain());
        }

        return LoginResult.NOT_HANDLED;
    }

    protected LoginResult authenticateCas(CasCallBackHandler callbackHandler, String customLoginModule)
    {
        Subject subject;
        try
        {
            LoginContext loginContext = new LoginContext(
                StringUtils.defaultString(customLoginModule, "magnolia"),
                callbackHandler);

            loginContext.login();
            subject = loginContext.getSubject();
            User user = SecuritySupport.Factory.getInstance().getUserManager().getUser(subject);

            if (user != null)
            {
            }
            else
            {
                log
                    .error("Unable to obtain a user from userManager, maybe the external user manager is not configured for the cas realm?");
            }

            return new LoginResult(LoginResult.STATUS_SUCCEEDED, subject);
        }
        catch (LoginException e)
        {
            log.info("Can't login due to:", e);
            return new LoginResult(LoginResult.STATUS_FAILED, e);
        }
    }

    public class CasCallBackHandler implements CallbackHandler
    {

        private String service;

        private String ticket;

        private String realm;

        public CasCallBackHandler(String service, String ticket, String realm)
        {
            this.service = service;
            this.ticket = ticket;
            this.realm = realm;
        }

        public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException
        {
            for (int i = 0; i < callbacks.length; i++)
            {
                if (callbacks[i] instanceof TextInputCallback)
                {
                    TextInputCallback text = (TextInputCallback) callbacks[i];
                    if ("ticket".equals(text.getPrompt()))
                    {
                        log.info(getTicket());
                        text.setText(getTicket());
                    }
                    else if ("service".equals(text.getPrompt()))
                    {
                        log.info(getService());
                        text.setText(getService());
                    }
                    else if ("casValidateUrl".equals(text.getPrompt()))
                    {
                        log.info(casValidateUrl);
                        text.setText(casValidateUrl);
                    }
                }
                else if (callbacks[i] instanceof RealmCallback)
                {
                    log.info(this.realm);
                    ((RealmCallback) callbacks[i]).setRealm(this.realm);
                }
                else
                {
                    throw (new UnsupportedCallbackException(callbacks[i], "Callback class not supported"));
                }
            }
        }

        public String getTicket()
        {
            return ticket;
        }

        public String getService()
        {
            return service;
        }

        public String getRealm()
        {
            return realm;
        }

    }
}
