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
import info.magnolia.module.delta.BootstrapSingleResource;
import info.magnolia.module.delta.TaskExecutionException;

import javax.jcr.RepositoryException;


/**
 * A base abstract class for tasks that bootstrap a resource based on a condition.
 * @author fgiust
 * @version $Id$
 */
public abstract class ConditionalBootstrapResource extends BootstrapSingleResource
{

    /**
     * @param name
     * @param description
     * @param resource
     */
    public ConditionalBootstrapResource(String name, String description, String resource)
    {
        super(name, description, resource);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(InstallContext installContext) throws TaskExecutionException
    {
        boolean bootstrap = true;

        try
        {
            bootstrap = shouldBootstrap(installContext);
        }
        catch (RepositoryException e)
        {
            // ignore
        }

        if (bootstrap)
        {
            super.execute(installContext);
        }
    }

    /**
     * Subclasses must override this method
     * @param installContext current InstallContext
     * @return <code>true</code> if the associated bootstrap file must be imported
     * @throws RepositoryException exception accessing the jcr repository
     */
    public abstract boolean shouldBootstrap(InstallContext installContext) throws RepositoryException;

}
