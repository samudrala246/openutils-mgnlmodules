/**
 *
 * Generic utilities for Magnolia CMS (http://www.openmindlab.com/lab/products/mgnlutils.html)
 * Copyright(C) 2009-2012, Openmind S.r.l. http://www.openmindonline.it
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

package it.openutils.mgnltasks;

import info.magnolia.jcr.util.PropertyUtil;
import info.magnolia.module.InstallContext;
import info.magnolia.module.delta.AbstractRepositoryTask;
import info.magnolia.module.delta.Task;
import info.magnolia.module.delta.TaskExecutionException;
import info.magnolia.repository.RepositoryConstants;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * A task that changes the password of an user if the current password is the default one. Useful to forbide
 * superuser/superuser accounts on a live instance ;)
 * @author fgiust
 * @version $Id$
 */
public class ChangeDefaultPasswordTask extends AbstractRepositoryTask implements Task
{

    private String user;

    private String defaultPassword;

    private String newpassword;

    /**
     * Logger.
     */
    private Logger log = LoggerFactory.getLogger(ChangeDefaultPasswordTask.class);

    /**
     * @param user User path (e.g. /system/superuser)
     * @param defaultPassword default password
     * @param newpassword new password, will be set only if the current password matches the default one
     */
    public ChangeDefaultPasswordTask(String user, String defaultPassword, String newpassword)
    {
        super("Default password check", "Checking default password for " + user);
        this.user = user;
        this.defaultPassword = defaultPassword;
        this.newpassword = newpassword;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doExecute(InstallContext installContext) throws RepositoryException, TaskExecutionException
    {

        Session hm = installContext.getJCRSession(RepositoryConstants.USERS);

        Node role = hm.getNode(user);

        String actualPassword = PropertyUtil.getString(role, "pswd");
        if (StringUtils.equals(defaultPassword, StringUtils.trim(actualPassword)))
        {
            log.info("Found default password for {}, setting new password", user);
            role.setProperty("pswd", newpassword);
        }
    }
}
