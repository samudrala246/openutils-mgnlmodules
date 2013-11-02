/**
 *
 * Magnolia5 test webapp (http://openutils.sourceforge.net/magnolia5-test-webapp)
 * Copyright(C) ${project.inceptionYear}-2013, Openmind S.r.l. http://www.openmindonline.it
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
package info.magnolia.module.development;

import info.magnolia.cms.core.SystemProperty;
import info.magnolia.module.DefaultModuleVersionHandler;
import info.magnolia.module.InstallContext;
import info.magnolia.module.delta.SetPropertyTask;
import info.magnolia.module.delta.Task;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple version handler used to set up a few config options during development
 * @author fgiust
 * @version $Revision: 3084 $ ($Author: fgiust $)
 */
public class DevModuleVersionHandler extends DefaultModuleVersionHandler
{

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<Task> getStartupTasks(InstallContext installContext)
    {
        List<Task> tasks = new ArrayList<Task>();

        boolean develop = SystemProperty.getBooleanProperty("magnolia.develop");

        if (develop)
        {
            tasks.add(new SetPropertyTask(
                "config",
                "/server/activation/subscribers/magnoliaPublic8080",
                "active",
                "false"));

        }

        // I hate spending time in looking through the unsorted list of modules...
//        tasks.add(new NodeSortTask(ContentRepository.CONFIG, "/modules"));

        return tasks;
    }
}
