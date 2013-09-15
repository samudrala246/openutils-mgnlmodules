/**
 *
 * Rules module for Magnolia CMS (http://www.openmindlab.com/lab/products/mgnlrules.html)
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

package net.sourceforge.openutils.mgnlrules.setup;

import info.magnolia.cms.core.SystemProperty;
import info.magnolia.module.InstallContext;
import info.magnolia.module.delta.Task;
import it.openutils.mgnltasks.SimpleModuleVersionHandler;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;


/**
 * @author dschivo
 * @version $Id$
 */
public class RuleModuleVersionHandler extends SimpleModuleVersionHandler
{

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<Task> getStartupTasks(InstallContext installContext)
    {
        List<Task> tasks = new ArrayList<Task>();

        tasks.add(new FilesExtractionTask()
        {

            /**
             * {@inheritDoc}
             */
            @Override
            protected boolean accept(String resource)
            {
                return super.accept(resource) && StringUtils.contains(resource, "/WEB-INF/jsps/");
            }
        });

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
                    return super.accept(resource) && StringUtils.contains(resource, "/samples-rules/");
                }
            });
        }

        return tasks;
    }
}
