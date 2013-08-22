/**
 *
 * Groovy Shell for Magnolia CMS (http://www.openmindlab.com/lab/products/groovy.html)
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

package net.sourceforge.openutils.mgnlgroovy.setup;

import info.magnolia.module.InstallContext;
import info.magnolia.module.delta.Task;
import it.openutils.mgnltasks.DeleteNodeTask;
import it.openutils.mgnltasks.SimpleModuleVersionHandler;

import java.util.ArrayList;
import java.util.List;


/**
 * @author fgiust
 * @version $Id: $
 */
public class GroovyShellModuleVersionHandler extends SimpleModuleVersionHandler
{

    @Override
    public List<Task> getStartupTasks(InstallContext installContext)
    {

        List<Task> tasks = new ArrayList<Task>();

        tasks.add(new DeleteNodeTask("config", "/modules/adminIterface/config/menu/tools/groovyShell"));
        tasks.add(new DeleteNodeTask("config", "/modules/groovyshell/pages/groovyShell"));

        return tasks;
    }
}
