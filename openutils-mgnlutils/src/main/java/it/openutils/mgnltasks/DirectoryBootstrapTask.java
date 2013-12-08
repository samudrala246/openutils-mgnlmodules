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

package it.openutils.mgnltasks;

import info.magnolia.module.InstallContext;
import info.magnolia.module.delta.BootstrapResourcesTask;

import org.apache.commons.lang.StringUtils;


/**
 * Bootstrap al the files in a given directory.
 * @author fgiust
 * @version $Id$
 */
public class DirectoryBootstrapTask extends BootstrapResourcesTask
{

    private final String directory;

    public DirectoryBootstrapTask(String directory)
    {
        super("Bootstrap", "Bootstraps all the file in the mgnl-bootstrap/" + directory + " directory");
        this.directory = directory;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean acceptResource(InstallContext ctx, String resourceName)
    {
        final String resourceFilename = StringUtils.substringAfter(resourceName, "/mgnl-bootstrap/" + directory + "/");
        return !StringUtils.contains(resourceFilename, "/") && resourceFilename.endsWith(".xml");
    }
}
