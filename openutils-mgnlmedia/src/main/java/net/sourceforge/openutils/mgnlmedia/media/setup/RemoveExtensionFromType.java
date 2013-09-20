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


import info.magnolia.cms.core.Content;
import info.magnolia.cms.core.HierarchyManager;
import info.magnolia.module.InstallContext;
import info.magnolia.module.delta.AbstractRepositoryTask;
import info.magnolia.module.delta.TaskExecutionException;
import info.magnolia.repository.RepositoryConstants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.jcr.RepositoryException;

import org.apache.commons.lang.StringUtils;


/**
 * @author fgiust
 * @version $Id$
 */
public class RemoveExtensionFromType extends AbstractRepositoryTask
{

    private final String extension;

    private final String mediatype;

    private final String control;

    /**
     * @param name
     * @param description
     */
    public RemoveExtensionFromType(String extension, String mediatype, String control)
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

        final HierarchyManager hm = ctx.getHierarchyManager(RepositoryConstants.CONFIG);
        String nodePath = "/modules/media/mediatypes/" + this.mediatype;

        try
        {
            final Content mediatypenode = hm.getContent(nodePath);
            String extensions = mediatypenode.getNodeData("extensions").getString();
            if (StringUtils.contains(extensions, this.extension))
            {
                List<String> exts = new ArrayList<String>(Arrays.asList(StringUtils.split(extensions, ",")));
                exts.remove(this.extension);
                mediatypenode.setNodeData("extensions", StringUtils.join(exts, ","));
            }
        }
        catch (RepositoryException e)
        {
            // ignore and skip
        }

        try
        {
            final Content mediatypenode = hm.getContent(control);
            String extensions = mediatypenode.getNodeData("extensions").getString();
            if (StringUtils.contains(extensions, this.extension))
            {
                List<String> exts = new ArrayList<String>(Arrays.asList(StringUtils.split(extensions, ",")));
                exts.remove(this.extension);
                mediatypenode.setNodeData("extensions", StringUtils.join(exts, ","));
            }
        }
        catch (RepositoryException e)
        {
            // ignore and skip
        }

    }

}
