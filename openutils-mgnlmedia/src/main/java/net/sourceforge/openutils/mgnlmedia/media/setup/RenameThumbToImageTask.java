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

import info.magnolia.cms.beans.runtime.FileProperties;
import info.magnolia.cms.core.HierarchyManager;
import info.magnolia.cms.core.NodeData;
import info.magnolia.cms.core.search.Query;
import info.magnolia.cms.core.search.QueryManager;
import info.magnolia.cms.core.search.QueryResult;
import info.magnolia.module.InstallContext;
import info.magnolia.module.delta.AbstractTask;
import info.magnolia.module.delta.TaskExecutionException;

import java.util.Collection;

import javax.jcr.Node;
import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;

import net.sourceforge.openutils.mgnlmedia.media.configuration.MediaConfigurationManager;
import net.sourceforge.openutils.mgnlmedia.media.lifecycle.MediaModule;


/**
 * @author molaschi
 * @version $Id$
 */
public class RenameThumbToImageTask extends AbstractTask
{

    /**
     * Constructor
     */
    public RenameThumbToImageTask()
    {
        super("Rename thumbnail to image", "Rename thumbnail nodedata to image nodedata for media");
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public void execute(InstallContext installContext) throws TaskExecutionException
    {
        HierarchyManager hm = installContext.getHierarchyManager(MediaModule.REPO);
        QueryManager mgr = hm.getQueryManager();
        try
        {
            Query query = mgr.createQuery("//*", Query.XPATH);
            QueryResult result = query.execute();
            Collection<Node> medias = result.getContent(MediaConfigurationManager.MEDIA.getSystemName());
            for (Node node : medias)
            {
                if (node.hasNodeData("thumbnail"))
                {
                    NodeData thumbnail = node.getNodeData("thumbnail");
                    if (thumbnail.getType() == PropertyType.BINARY)
                    {
                        NodeData image = node.createNodeData("image", PropertyType.BINARY);
                        image.setValue(thumbnail.getStream());

                        image.setAttribute(
                            FileProperties.PROPERTY_EXTENSION,
                            thumbnail.getAttribute(FileProperties.PROPERTY_EXTENSION));
                        image.setAttribute(
                            FileProperties.PROPERTY_FILENAME,
                            thumbnail.getAttribute(FileProperties.PROPERTY_FILENAME));
                        image.setAttribute(
                            FileProperties.PROPERTY_CONTENTTYPE,
                            thumbnail.getAttribute(FileProperties.PROPERTY_CONTENTTYPE));
                        image.setAttribute(
                            FileProperties.PROPERTY_LASTMODIFIED,
                            thumbnail.getAttribute(FileProperties.PROPERTY_LASTMODIFIED));
                        image.setAttribute(
                            FileProperties.PROPERTY_WIDTH,
                            thumbnail.getAttribute(FileProperties.PROPERTY_WIDTH));
                        image.setAttribute(
                            FileProperties.PROPERTY_HEIGHT,
                            thumbnail.getAttribute(FileProperties.PROPERTY_HEIGHT));

                        thumbnail.delete();
                    }
                }
            }
            hm.save();
        }
        catch (RepositoryException ex)
        {
            throw new TaskExecutionException(ex.getMessage(), ex);
        }
    }

}
