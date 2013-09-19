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
import info.magnolia.cms.core.ItemType;
import info.magnolia.cms.core.NodeData;
import info.magnolia.module.InstallContext;
import info.magnolia.module.delta.AbstractRepositoryTask;
import info.magnolia.module.delta.TaskExecutionException;

import java.util.Collection;

import javax.jcr.RepositoryException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Update configuration for media types.
 * @author fgiust
 * @version $Id$
 */
public class MoveHandlerNodedataToNode extends AbstractRepositoryTask
{

    /**
     * Logger.
     */
    private Logger log = LoggerFactory.getLogger(MoveHandlerNodedataToNode.class);

    /**
     * Update mediatype configuration
     */
    public MoveHandlerNodedataToNode()
    {
        super("Update mediatype configuration", "Handler configuration now requires a node instead of a nodedata");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doExecute(InstallContext installContext) throws RepositoryException, TaskExecutionException
    {
        Content types = installContext.getConfigHierarchyManager().getContent("/modules/media/mediatypes");
        Collection<Content> mediatypes = types.getChildren(ItemType.CONTENTNODE);
        String handlerPropertyName = "handler";

        for (Content mediatype : mediatypes)
        {
            if (mediatype.hasNodeData(handlerPropertyName))
            {
                log.info("Legacy configuration found for mediatype {}", mediatype.getName()
                    + ", updating configuration");
                NodeData handlerNd = mediatype.getNodeData(handlerPropertyName);
                String previousHandler = handlerNd.getString();
                handlerNd.delete();
                if (!mediatype.hasContent(handlerPropertyName))
                {
                    mediatype.createContent(handlerPropertyName, ItemType.CONTENTNODE).createNodeData(
                        "class",
                        previousHandler);
                }

            }
        }

    }

}
