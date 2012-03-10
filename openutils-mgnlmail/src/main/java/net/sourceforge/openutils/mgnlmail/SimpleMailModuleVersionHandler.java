/**
 *
 * simplemail module for Magnolia CMS (http://www.openmindlab.com/lab/products/mgnlmail.html)
 * Copyright(C) 2011-2011, Openmind S.r.l. http://www.openmindonline.it
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

package net.sourceforge.openutils.mgnlmail;

import info.magnolia.cms.security.Permission;
import info.magnolia.module.InstallContext;
import info.magnolia.module.delta.Task;
import it.openutils.mgnltasks.AddPermissionTask;
import it.openutils.mgnltasks.DiffSimpleModuleVersionHandler;

import java.util.ArrayList;
import java.util.List;


/**
 * @author fgiust
 * @version $Id$
 */
public class SimpleMailModuleVersionHandler extends DiffSimpleModuleVersionHandler
{

    @Override
    protected List<Task> getStartupTasks(InstallContext installContext)
    {
        List<Task> tasks = new ArrayList<Task>();

        tasks.add(new AddPermissionTask("anonymous", "email", "/*", Permission.READ));
        tasks.add(new AddPermissionTask("anonymous", "uri", "/email*", Permission.ALL));

        return tasks;

    }
}
