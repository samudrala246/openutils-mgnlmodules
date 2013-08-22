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

package it.openutils.mgnltasks;

import info.magnolia.cms.core.HierarchyManager;
import info.magnolia.cms.core.Path;
import info.magnolia.module.InstallContext;
import info.magnolia.module.delta.AbstractTask;
import info.magnolia.module.delta.TaskExecutionException;
import info.magnolia.module.files.FileExtractionLogger;
import info.magnolia.module.files.FileExtractor;
import info.magnolia.module.files.MD5CheckingFileExtractor;

import java.io.IOException;

import org.apache.commons.lang.StringUtils;


/**
 * Extract samples files to webapp root.
 * @author fgiust
 * @version $Id: SamplesExtractionTask.java 1628 2010-01-09 17:38:55Z fgiust $
 */
public class SamplesExtractionTask extends AbstractTask
{

    public SamplesExtractionTask()
    {
        super("Samples extraction", "Extracts jsp files for samples.");
    }

    /**
     * {@inheritDoc}
     */
    public void execute(final InstallContext ctx) throws TaskExecutionException
    {
        final HierarchyManager hm = ctx.getConfigHierarchyManager();
        final MD5CheckingFileExtractor extractor = new MD5CheckingFileExtractor(new FileExtractionLogger()
        {

            public void error(String message)
            {
                ctx.warn(message);
            }
        }, hm);
        try
        {
            extractor.extractFiles(new FileExtractor.Transformer()
            {

                public String accept(String resourcePath)
                {
                    final boolean thisIsAFileWeWant = resourcePath.startsWith("/mgnl-files/")
                        && StringUtils.contains(resourcePath, "/samples-"
                            + ctx.getCurrentModuleDefinition().getName()
                            + "/");
                    if (!thisIsAFileWeWant)
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

}
