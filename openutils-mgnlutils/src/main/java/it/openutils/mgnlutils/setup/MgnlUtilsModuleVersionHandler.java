/**
 *
 * Generic utilities for Magnolia CMS (http://www.openmindlab.com/lab/products/mgnlutils.html)
 * Copyright(C) 2009-2012, Openmind S.r.l. http://www.openmindonline.it
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

package it.openutils.mgnlutils.setup;

import info.magnolia.cms.core.Path;
import info.magnolia.init.MagnoliaConfigurationProperties;
import info.magnolia.module.DefaultModuleVersionHandler;
import info.magnolia.module.InstallContext;
import info.magnolia.module.delta.AbstractTask;
import info.magnolia.module.delta.Task;
import info.magnolia.module.delta.TaskExecutionException;
import info.magnolia.module.files.FileExtractionLogger;
import info.magnolia.module.files.FileExtractor;
import info.magnolia.module.files.MD5CheckingFileExtractor;
import info.magnolia.objectfactory.Components;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;


/**
 * @author fgiust
 * @version $Id$
 */
public class MgnlUtilsModuleVersionHandler extends DefaultModuleVersionHandler
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
            tasks.add(new FilesExtractionTask());
        }

        return tasks;
    }

    public static class FilesExtractionTask extends AbstractTask
    {

        public FilesExtractionTask()
        {
            super("Files extraction", "Extracts files to webapp root.");
        }

        /**
         * {@inheritDoc}
         */
        public void execute(final InstallContext ctx) throws TaskExecutionException
        {
            final MD5CheckingFileExtractor extractor = new MD5CheckingFileExtractor(new FileExtractionLogger()
            {

                public void error(String message)
                {
                    ctx.warn(message);
                }
            }, ctx.getConfigHierarchyManager());
            try
            {
                extractor.extractFiles(new FileExtractor.Transformer()
                {

                    public String accept(String resourcePath)
                    {
                        if (!FilesExtractionTask.this.accept(resourcePath))
                        {
                            return null;
                        }
                        final String relTargetPath = StringUtils.removeStart(resourcePath, "/mgnl-files/");
                        return Path.getAbsoluteFileSystemPath(relTargetPath);
                    }

                });
            }
            catch (IOException e)
            {
                throw new TaskExecutionException("Could not extract files for module "
                    + ctx.getCurrentModuleDefinition()
                    + ": "
                    + e.getMessage(), e);
            }
        }

        protected boolean accept(String resource)
        {
            return StringUtils.contains(resource, "/samples/sample-magnoliautils");
        }
    }

}
