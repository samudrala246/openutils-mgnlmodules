/**
 *
 * SimpleMedia Module for Magnolia CMS (http://www.openmindlab.com/lab/products/media.html)
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

package net.sourceforge.openutils.mgnlmedia.media.commands;

import info.magnolia.commands.CommandsManager;
import info.magnolia.context.Context;
import info.magnolia.module.admininterface.commands.BaseRepositoryCommand;
import net.sourceforge.openutils.mgnlmedia.media.tags.el.MediaEl;

import org.apache.commons.chain.Command;


/**
 * @author molaschi
 * @version $Id: $
 */
public class MediaDeactivationCommand extends BaseRepositoryCommand
{

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean execute(Context context) throws Exception
    {
        if (!MediaEl.module().isSingleinstance())
        {
            Command cmd = CommandsManager.getInstance().getCommand(CommandsManager.DEFAULT_CATALOG, "deactivate");
            return cmd.execute(context);
        }
        return true;
    }

}
