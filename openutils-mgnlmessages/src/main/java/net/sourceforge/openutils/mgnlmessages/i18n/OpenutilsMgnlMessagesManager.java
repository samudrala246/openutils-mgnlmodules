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

package net.sourceforge.openutils.mgnlmessages.i18n;

import info.magnolia.cms.i18n.DefaultMessagesManager;
import info.magnolia.cms.i18n.Messages;
import info.magnolia.cms.i18n.MessagesChain;


/**
 * @author molaschi
 * @version $Id: $
 */
public class OpenutilsMgnlMessagesManager extends DefaultMessagesManager
{

    /**
     * {@inheritDoc}
     */
    @Override
    protected Messages newMessages(MessagesID messagesID)
    {

        Messages parentMessages = super.newMessages(messagesID);
        Messages repositoryMsg = new RepositoryMessagesImpl(messagesID.getBasename(), messagesID.getLocale());

        return new MessagesChain(repositoryMsg).chain(parentMessages);
    }

}
