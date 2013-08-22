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

import info.magnolia.cms.security.Realm;
import info.magnolia.cms.security.User;
import info.magnolia.cms.security.auth.Entity;
import info.magnolia.cms.security.auth.GroupList;
import info.magnolia.cms.security.auth.RoleList;
import info.magnolia.cms.security.auth.callback.RealmCallback;
import info.magnolia.cms.util.BooleanUtil;
import info.magnolia.jaas.principal.EntityImpl;
import info.magnolia.jaas.principal.GroupListImpl;
import info.magnolia.jaas.principal.RoleListImpl;
import info.magnolia.jaas.sp.AbstractLoginModule;
import info.magnolia.jaas.sp.UserAwareLoginModule;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.TextInputCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.jasig.cas.client.authentication.AttributePrincipal;
import org.jasig.cas.client.validation.Assertion;
import org.jasig.cas.client.validation.Cas20ProxyTicketValidator;
import org.jasig.cas.client.validation.TicketValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This class implements a JAAS <code>LoginModule</code> that defers authentication to CAS. See the <a
 * href="http://java.sun.com/j2se/1.4.2/docs/guide/security/jaas/JAASRefGuide.html"> JAAS documentation</a> for details
 * about configuration and architecture.
 * <p>
 * The calling application's <code>CallbackHandler</code> MUST return the <strong>ticket</strong> for a
 * <code>TextInputCallback</code> whose prompt is "ticket".
 * <p>
 * The CAS <strong>service</strong> MAY be hard-coded into the configuration; if it is not, the calling application's
 * <code>CallbackHandler</code> MAY return the <strong>service</strong> in a <code>TextInputCallback</code> whose prompt
 * is "service".
 * <p>
 * The <strong>cas_validate_url</strong> MUST be hard-coded in the configuration
 * <p>
 * Sample jaas.config configuration:
 * </p>
 * 
 * <pre>
 * magnolia {
 *   net.sourceforge.openutils.mgnlcas.CASAuthenticationModule
 *                  requisite
 *                  realm=cas;
 *  info.magnolia.jaas.sp.jcr.JCRAuthenticationModule
 *                  requisite
 *                  skip_on_previous_success=true;
 *  info.magnolia.jaas.sp.jcr.JCRAuthorizationModule required;
 * };
 *
 *
 * </pre>
 * @author fgiust
 * @version $Id: CASAuthenticationModule.java 803 2010-11-16 13:52:15Z fgiust $
 */
public class CASAuthenticationModule extends AbstractLoginModule implements LoginModule, UserAwareLoginModule
{

    protected Subject subject;

    protected CallbackHandler callbackHandler;

    protected String casValidateUrl;

    protected String service;

    protected AttributePrincipal principal;

    protected User user;

    private boolean skipOnPreviousSuccess;

    private String defaultGroup;

    private String defaultRole;

    private String rolesAttribute = "roles";

    private String groupsAttribute = "groups";

    protected Logger log = LoggerFactory.getLogger(getClass());

    /**
     * @param subject
     * @param callbackHandler
     * @param sharedState
     * @param options can contain
     * <ul>
     * <li><strong>cas_validate_url</strong> (required)</li>
     * <li><strong>service</strong> (optional)</li>
     * </ul>
     */
    @Override
    public void initialize(Subject subject, CallbackHandler callbackHandler, Map sharedState, Map options)
    {
        super.initialize(subject, callbackHandler, sharedState, options);
        this.subject = subject;
        this.callbackHandler = callbackHandler;
        this.casValidateUrl = (String) options.get("cas_validate_url");
        this.service = (String) options.get("service");
        this.defaultGroup = (String) options.get("default_group");
        this.defaultRole = (String) options.get("default_role");

        if (options.get("roles_attribute") != null)
        {
            rolesAttribute = (String) options.get("roles_attribute");
        }

        if (options.get("groups_attribute") != null)
        {
            groupsAttribute = (String) options.get("groups_attribute");
        }

        // private on AbstractLoginModule
        this.skipOnPreviousSuccess = BooleanUtil
            .toBoolean((String) options.get(OPTION_SKIP_ON_PREVIOUS_SUCCESS), false);
    }

    @Override
    public boolean login() throws LoginException
    {
        if (skipOnPreviousSuccess && this.getSharedStatus() == STATUS_SUCCEEDED)
        {
            return true;
        }

        if (this.callbackHandler == null)
        {
            throw new LoginException("Error: no CallbackHandler available");
        }

        List<Callback> callbacksList = new ArrayList<Callback>();

        TextInputCallback ticketCallback = new TextInputCallback("ticket");
        TextInputCallback serviceCallback = null;
        TextInputCallback casValidateUrlCallback = null;
        RealmCallback realmCallback = null;

        callbacksList.add(ticketCallback);

        if (StringUtils.isBlank(service))
        {
            // the service has not been hardcoded, so give the application
            // a callback which can be used to specify it
            serviceCallback = new TextInputCallback("service");
            callbacksList.add(serviceCallback);
        }

        if (StringUtils.isBlank(service))
        {
            // the casValidateUrl has not been hardcoded, so give the application
            // a callback which can be used to specify it
            casValidateUrlCallback = new TextInputCallback("casValidateUrl");
            callbacksList.add(casValidateUrlCallback);
        }

        // if the realm is not defined in the jaas configuration
        // we ask use a callback to get the value
        if (this.useRealmCallback)
        {
            realmCallback = new RealmCallback();
            callbacksList.add(realmCallback);
        }

        this.success = false;

        Callback[] callbacks = callbacksList.toArray(new Callback[callbacksList.size()]);

        try
        {
            this.callbackHandler.handle(callbacks);

        }
        catch (IOException e)
        {
            if (log.isDebugEnabled())
            {
                log.debug("Exception caught", e);
            }
            throw new LoginException(e.toString());
        }
        catch (UnsupportedCallbackException e)
        {
            if (log.isDebugEnabled())
            {
                log.debug(e.getMessage(), e);
            }
            throw new LoginException(e.getCallback().toString() + " not available");
        }

        // this.name = ((NameCallback) callbacks[0]).getName();
        // this.pswd = ((PasswordCallback) callbacks[1]).getPassword();
        if (this.useRealmCallback)
        {
            this.realm = StringUtils.isBlank(realmCallback.getRealm()) ? this.realm : Realm.Factory
                .newRealm(realmCallback.getRealm());
        }

        String ticket = ticketCallback.getText();
        if (StringUtils.isNotEmpty(ticket))
        {
            if (serviceCallback != null)
            {
                service = serviceCallback.getText();
            }

            if (casValidateUrlCallback != null)
            {
                casValidateUrl = casValidateUrlCallback.getText();
            }

            Cas20ProxyTicketValidator pv = new Cas20ProxyTicketValidator(casValidateUrl);

            Assertion assertion;
            try
            {
                assertion = pv.validate(ticket, service);
            }
            catch (TicketValidationException e)
            {
                throw new LoginException(e.getMessage());
            }

            if (assertion.getPrincipal() != null)
            {

                principal = assertion.getPrincipal();

                log.debug("principal is {}", principal.getName());

                subject.getPrincipals().add(principal);

                setEntity();

                user = new CasMagnoliaUser(subject);

                this.success = true;
                this.setSharedStatus(STATUS_SUCCEEDED);
            }

        }

        return this.success;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validateUser() throws LoginException
    {

    }

    @Override
    public boolean commit() throws LoginException
    {
        if (principal != null)
        {
            subject.getPrincipals().add(principal);
            return true;
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public User getUser()
    {
        return user;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean release()
    {
        if (principal != null)
        {
            principal = null;
            user = null;
            return true;
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setACL()
    {
        // delegate to the Authorization module
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public void setEntity()
    {
        EntityImpl entity = new EntityImpl();
        entity.addProperty(Entity.NAME, this.principal.getName());

        Map<String, Object> attributes = this.principal.getAttributes();

        for (Map.Entry<String, Object> attr : attributes.entrySet())
        {
            entity.addProperty(attr.getKey(), attr.getValue());
        }

        String fullName = (String) attributes.get("title");

        if (fullName != null)
        {
            log.debug("full name is {}", fullName);
            entity.addProperty(Entity.FULL_NAME, fullName);
        }

        entity.addProperty(Entity.LANGUAGE, "en");

        this.subject.getPrincipals().add(entity);

        String[] roles = StringUtils.split((String) attributes.get(rolesAttribute));
        String[] groups = StringUtils.split((String) attributes.get(groupsAttribute));

        if (roles == null)
        {
            roles = new String[0];
        }
        if (groups == null)
        {
            groups = new String[0];
        }

        if (defaultGroup != null)
        {
            groups = (String[]) ArrayUtils.add(groups, defaultGroup);
        }

        if (defaultRole != null)
        {
            roles = (String[]) ArrayUtils.add(roles, defaultRole);
        }

        addGroups(groups);
        addRoles(roles);

    }

    /**
     * Set the list of groups, info.magnolia.jaas.principal.GroupList.
     * @param groups array of group names
     */
    protected void addGroups(String[] groups)
    {
        GroupList groupList = new GroupListImpl();

        for (int j = 0; j < groups.length; j++)
        {
            String group = groups[j];
            groupList.add(group);
            addGroupName(group);
        }

        this.subject.getPrincipals().add(groupList);
    }

    /**
     * Set the list of roles, info.magnolia.jaas.principal.RoleList.
     * @param roles array of role names
     */
    protected void addRoles(String[] roles)
    {
        RoleList roleList = new RoleListImpl();

        for (int j = 0; j < roles.length; j++)
        {
            String role = roles[j];
            roleList.add(role);
            addRoleName(role);
        }

        this.subject.getPrincipals().add(roleList);
    }

}