/**
 *
 * Stripes module for Magnolia CMS (http://www.openmindlab.com/lab/products/mgnlstripes.html)
 * Copyright(C) 2008-2012, Openmind S.r.l. http://www.openmindonline.it
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

package it.openutils.magnoliastripes.setup;

import info.magnolia.cms.beans.config.ContentRepository;
import info.magnolia.cms.core.SystemProperty;
import info.magnolia.module.DefaultModuleVersionHandler;
import info.magnolia.module.InstallContext;
import info.magnolia.module.delta.AddMimeMappingTask;
import info.magnolia.module.delta.FilterOrderingTask;
import info.magnolia.module.delta.Task;
import it.openutils.mgnltasks.CreateMissingPropertyTask;
import it.openutils.mgnltasks.SamplesExtractionTask;

import java.util.List;


/**
 * @author fgiust
 * @version $Id: $
 */
public class StripesModuleVersionHandler extends DefaultModuleVersionHandler
{

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<Task> getBasicInstallTasks(InstallContext installContext)
    {
        List<Task> tasks = super.getBasicInstallTasks(installContext);

        if (SystemProperty.getBooleanProperty(SystemProperty.MAGNOLIA_BOOTSTRAP_SAMPLES))
        {
            tasks.add(new SamplesExtractionTask());
        }

        tasks.add(new AddMimeMappingTask("action", "text/plain", "/.resources/file-icons/htm.png"));

        tasks.add(new CreateMissingPropertyTask(
            "Adding property: /modules/stripes/config/i18nbasename",
            "Adding property: /modules/stripes/config/i18nbasename",
            ContentRepository.CONFIG,
            "/modules/stripes/config",
            "i18nbasename",
            "it.openutils.magnoliastripes"));

        tasks.add(new FilterOrderingTask(
            "stripes",
            new String[]{"context", "login", "uriSecurity", "multipartRequest" }));

        return tasks;
    }
}
