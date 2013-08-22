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
 * Extract files to webapp root.
 * @author fgiust
 * @author dschivo
 */
public class FilesExtractionTask extends AbstractTask
{

    public FilesExtractionTask(String taskName, String taskDescription)
    {
        super(taskName, taskDescription);
    }

    public FilesExtractionTask()
    {
        this("Files extraction", "Extracts files to webapp root.");
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
        return resource.startsWith("/mgnl-files/");
    }
}
