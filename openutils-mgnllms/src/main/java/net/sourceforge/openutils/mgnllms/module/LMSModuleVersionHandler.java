/**
 *
 * E-learning Module for Magnolia CMS (http://www.openmindlab.com/lab/products/lms.html)
 * Copyright(C) 2010-2011, Openmind S.r.l. http://www.openmindonline.it
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

package net.sourceforge.openutils.mgnllms.module;

import info.magnolia.cms.core.SystemProperty;
import info.magnolia.cms.security.Permission;
import info.magnolia.module.InstallContext;
import info.magnolia.module.delta.SetPropertyTask;
import info.magnolia.module.delta.Task;
import it.openutils.mgnltasks.AddPermissionTask;
import it.openutils.mgnltasks.SamplesExtractionTask;
import it.openutils.mgnltasks.SimpleModuleVersionHandler;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.openutils.mgnllms.filters.AggregatorServingZipFilter;
import net.sourceforge.openutils.mgnllms.filters.RenderingServingZipFilter;


/**
 * @author molaschi
 * @version $Id: $
 */
public class LMSModuleVersionHandler extends SimpleModuleVersionHandler
{

    /**
     *
     */
    public LMSModuleVersionHandler()
    {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<Task> getStartupTasks(InstallContext installContext)
    {
        List<Task> tasks = new ArrayList<Task>();

        if (SystemProperty.getBooleanProperty(SystemProperty.MAGNOLIA_BOOTSTRAP_SAMPLES))
        {
            tasks.add(new SamplesExtractionTask());
            tasks.add(new AddPermissionTask("anonymous", "website", "/sample-lms", Permission.READ));
            tasks.add(new AddPermissionTask("anonymous", "website", "/sample-lms/*", Permission.NONE));
            tasks.add(new AddPermissionTask("anonymous", "uri", "/sample-lms.html", Permission.READ));
        }

        tasks.add(new SetPropertyTask(
            "config",
            "/server/filters/cms/rendering",
            "class",
            RenderingServingZipFilter.class.getName())); // net.sourceforge.openutils.mgnllms.filters.RenderingServingZipFilter
        tasks.add(new SetPropertyTask(
            "config",
            "/server/filters/cms/aggregator",
            "class",
            AggregatorServingZipFilter.class.getName())); // net.sourceforge.openutils.mgnllms.filters.AggregatorServingZipFilter
        return tasks;
    }

}
