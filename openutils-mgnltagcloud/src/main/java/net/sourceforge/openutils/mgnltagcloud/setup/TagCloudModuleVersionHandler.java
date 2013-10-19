/**
 *
 * Tagcloud module for Magnolia CMS (http://www.openmindlab.com/lab/products/mgnltagcloud.html)
 * Copyright(C) 2010-2013, Openmind S.r.l. http://www.openmindonline.it
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

package net.sourceforge.openutils.mgnltagcloud.setup;

import info.magnolia.init.MagnoliaConfigurationProperties;
import info.magnolia.module.InstallContext;
import info.magnolia.module.delta.Task;
import info.magnolia.objectfactory.Components;
import it.openutils.mgnltasks.CreateMissingPropertyTask;
import it.openutils.mgnltasks.SamplesExtractionTask;
import it.openutils.mgnltasks.SimpleModuleVersionHandler;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.openutils.mgnltagcloud.manager.DefaultTagCloudManager;


/**
 * @author cstrappazzon
 * @version $Id$
 */
public class TagCloudModuleVersionHandler extends SimpleModuleVersionHandler
{

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<Task> getStartupTasks(InstallContext installContext)
    {

        List<Task> tasks = new ArrayList<Task>();

        if (Components.getComponent(MagnoliaConfigurationProperties.class).getBooleanProperty(
            "magnolia.bootstrap.samples"))
        {
            tasks.add(new SamplesExtractionTask());
        }

        tasks.add(new CreateMissingPropertyTask(
            "config",
            "/modules/tagcloud/clouds",
            "class",
            DefaultTagCloudManager.class.getName()));

        return tasks;
    }
}
