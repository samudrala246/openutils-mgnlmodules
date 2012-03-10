/**
 *
 * ContextMenu Module for Magnolia CMS (http://www.openmindlab.com/lab/products/mgnlcontextmenu.html)
 * Copyright(C) 2010-2012, Openmind S.r.l. http://www.openmindonline.it
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

package net.sourceforge.openutils.mgnlcontextmenu.setup;

import info.magnolia.cms.beans.config.ContentRepository;
import info.magnolia.cms.core.SystemProperty;
import info.magnolia.module.InstallContext;
import info.magnolia.module.delta.Task;
import it.openutils.mgnltasks.CreateMissingPropertyTask;
import it.openutils.mgnltasks.SimpleModuleVersionHandler;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.openutils.mgnlcontextmenu.configuration.DefaultGetGlobalEntriesNodeStrategy;
import net.sourceforge.openutils.mgnlcontextmenu.configuration.DefaultPersistenceStrategy;

import org.apache.commons.lang.StringUtils;


/**
 * @author dschivo
 */
public class ContextMenuModuleVersionHandler extends SimpleModuleVersionHandler
{

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<Task> getStartupTasks(InstallContext installContext)
    {
        List<Task> tasks = new ArrayList<Task>();

        if (SystemProperty.getBooleanProperty(SystemProperty.MAGNOLIA_BOOTSTRAP_SAMPLES))
        {
            tasks.add(new FilesExtractionTask("Samples extraction", "Extracts jsp files for samples.")
            {

                /**
                 * {@inheritDoc}
                 */
                @Override
                protected boolean accept(String resource)
                {
                    return super.accept(resource) && StringUtils.contains(resource, "/samples-contextmenu/");
                }
            });
        }

        tasks.add(new CreateMissingPropertyTask(
            ContentRepository.CONFIG,
            "/modules/contextmenu/config/getGlobalEntriesNodeStrategy",
            "class",
            DefaultGetGlobalEntriesNodeStrategy.class.getName()));

        tasks.add(new CreateMissingPropertyTask(
            ContentRepository.CONFIG,
            "/modules/contextmenu/config/getGlobalEntriesNodeStrategy",
            "ancestorLevel",
            Long.valueOf(1)));

        tasks.add(new CreateMissingPropertyTask(
            ContentRepository.CONFIG,
            "/modules/contextmenu/config/persistenceStrategy",
            "class",
            DefaultPersistenceStrategy.class.getName()));

        return tasks;
    }
}
