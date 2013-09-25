/**
 *
 * SimpleMedia Module for Magnolia CMS (http://www.openmindlab.com/lab/products/media.html)
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

package net.sourceforge.openutils.mgnlmedia.media.setup;

import info.magnolia.jcr.util.PropertyUtil;
import info.magnolia.module.InstallContext;
import info.magnolia.module.delta.AbstractRepositoryTask;
import info.magnolia.module.delta.TaskExecutionException;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.ValueFormatException;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.version.VersionException;

import org.apache.commons.lang.StringUtils;


/**
 * @author fgiust
 * @version $Id$
 */
public class AddExtensionToType extends AbstractRepositoryTask
{

    private final String extension;

    private final String mediatype;

    private final String control;

    /**
     * @param name
     * @param description
     */
    public AddExtensionToType(String extension, String mediatype, String control)
    {
        super("Adding " + extension + " extension to mediatype " + mediatype, "Adding "
            + extension
            + " extension to mediatype "
            + mediatype);
        this.extension = extension;
        this.mediatype = mediatype;
        this.control = control;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doExecute(InstallContext ctx) throws RepositoryException, TaskExecutionException
    {

        final Session hm = ctx.getConfigJCRSession();
        String nodePath = "/modules/media/mediatypes/" + this.mediatype;

        try
        {
            final Node mediatypenode = hm.getNode(nodePath);
            setExtensions(mediatypenode);
        }
        catch (RepositoryException e)
        {
            // ignore and skip
        }

        try
        {
            final Node mediatypenode = hm.getNode(control);
            setExtensions(mediatypenode);
        }
        catch (RepositoryException e)
        {
            // ignore and skip
        }

    }

    /**
     * @param mediatypenode
     * @throws ValueFormatException
     * @throws VersionException
     * @throws LockException
     * @throws ConstraintViolationException
     * @throws RepositoryException
     */
    private void setExtensions(final Node mediatypenode) throws ValueFormatException, VersionException, LockException,
        ConstraintViolationException, RepositoryException
    {
        String extensions = PropertyUtil.getString(mediatypenode, "extensions");
        if (!StringUtils.contains(extensions, this.extension))
        {
            mediatypenode.setProperty("extensions", extensions + "," + this.extension);
        }
    }

}
