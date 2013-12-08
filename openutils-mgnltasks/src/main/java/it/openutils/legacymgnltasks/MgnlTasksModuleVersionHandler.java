/**
 *
 * Tasks for for Magnolia CMS (http://www.openmindlab.com/lab/products/mgnltasks.html)
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
package it.openutils.legacymgnltasks;

import info.magnolia.module.DefaultModuleVersionHandler;
import info.magnolia.module.InstallContext;
import info.magnolia.module.delta.Task;
import info.magnolia.module.delta.TaskExecutionException;

import java.util.ArrayList;
import java.util.List;


/**
 * @author fgiust
 * @version $Id$
 */
public class MgnlTasksModuleVersionHandler extends DefaultModuleVersionHandler
{

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<Task> getStartupTasks(InstallContext installContext)
    {
        List<Task> tasks = new ArrayList<Task>();
        tasks.add(new Task()
        {

            public void execute(InstallContext installContext) throws TaskExecutionException
            {
                installContext
                    .warn("Tasks module is deprecated. Since version 5.0.10 has been merged into mgnlutils, you can freely remove this module");
            }

            public String getName()
            {
                return "Tasks module deprecation warning";
            }

            public String getDescription()
            {
                return "Tasks module deprecation warning";
            }
        });

        return tasks;
    }

}
