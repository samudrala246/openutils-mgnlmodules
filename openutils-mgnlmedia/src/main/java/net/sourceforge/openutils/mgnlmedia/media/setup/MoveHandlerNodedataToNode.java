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

import info.magnolia.cms.core.MgnlNodeType;
import info.magnolia.jcr.util.NodeUtil;
import info.magnolia.module.InstallContext;
import info.magnolia.module.delta.AbstractRepositoryTask;
import info.magnolia.module.delta.TaskExecutionException;

import javax.jcr.Node;
import javax.jcr.Property;
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
        Node types = installContext.getConfigJCRSession().getNode("/modules/media/mediatypes");

        Iterable<Node> mediatypes = NodeUtil.getNodes(types, MgnlNodeType.NT_CONTENTNODE);
        String handlerPropertyName = "handler";

        for (Node mediatype : mediatypes)
        {
            if (mediatype.hasProperty(handlerPropertyName))
            {
                log.info("Legacy configuration found for mediatype {}", mediatype.getName()
                    + ", updating configuration");

                Property handlerNd = mediatype.getProperty(handlerPropertyName);
                String previousHandler = handlerNd.getString();
                handlerNd.remove();

                if (!mediatype.hasNode(handlerPropertyName))
                {
                    mediatype.addNode(handlerPropertyName, MgnlNodeType.NT_CONTENTNODE).setProperty(
                        "class",
                        previousHandler);
                }

            }
        }

    }

}
