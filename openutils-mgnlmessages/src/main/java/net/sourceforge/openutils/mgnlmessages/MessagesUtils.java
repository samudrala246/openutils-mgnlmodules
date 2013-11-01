/**
 *
 * Messages Module for Magnolia CMS (http://www.openmindlab.com/lab/products/messages.html)
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

package net.sourceforge.openutils.mgnlmessages;

import info.magnolia.cms.core.MgnlNodeType;
import info.magnolia.context.SystemContext;
import info.magnolia.jcr.RuntimeRepositoryException;
import info.magnolia.jcr.util.NodeUtil;
import info.magnolia.objectfactory.Components;
import it.openutils.mgnlutils.util.NodeUtilsExt;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.commons.lang.StringUtils;


/**
 * @author fgiust
 * @version $Id$
 */
public class MessagesUtils
{

    public static void saveKeyValue(String key, String value, String locale) throws RepositoryException
    {
        Session session;
        try
        {
            session = Components.getComponent(SystemContext.class).getJCRSession(MessagesModule.REPO);
        }
        catch (RepositoryException e)
        {
            throw new RuntimeRepositoryException(e);
        }

        String path = "/" + StringUtils.replace(key, ".", "/");

        Node content = NodeUtil.createPath(session.getRootNode(), path, MgnlNodeType.NT_CONTENTNODE);

        if (!StringUtils.isEmpty(locale))
        {
            if (!StringUtils.isEmpty(value))
            {
                content.setProperty(locale, value);
            }
            else
            {
                NodeUtilsExt.deletePropertyIfExist(content, locale);
            }
        }

        session.save();
    }
}
