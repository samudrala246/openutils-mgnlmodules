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
import info.magnolia.cms.core.MgnlNodeType;
import info.magnolia.jcr.util.NodeUtil;
import info.magnolia.jcr.util.PropertyUtil;
import info.magnolia.module.InstallContext;
import info.magnolia.module.delta.AbstractTask;
import info.magnolia.module.delta.TaskExecutionException;

import java.io.InputStream;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.nodetype.NodeType;

import net.sourceforge.openutils.mgnlcriteria.jcr.query.AdvancedResult;
import net.sourceforge.openutils.mgnlcriteria.jcr.query.AdvancedResultItem;
import net.sourceforge.openutils.mgnlcriteria.jcr.query.Criteria;
import net.sourceforge.openutils.mgnlcriteria.jcr.query.JCRCriteriaFactory;
import net.sourceforge.openutils.mgnlcriteria.jcr.query.criterion.Order;
import net.sourceforge.openutils.mgnlmedia.media.configuration.MediaConfigurationManager;
import net.sourceforge.openutils.mgnlmedia.media.lifecycle.MediaModule;

import org.apache.jackrabbit.value.BinaryValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author molaschi
 * @version $Id$
 */
public class RenameThumbToImageTask extends AbstractTask
{

    /**
     * Logger.
     */
    private Logger log = LoggerFactory.getLogger(RenameThumbToImageTask.class);

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
        try
        {
            // [LB] FIXME?
            Session hm = installContext.getJCRSession(MediaModule.REPO);
            Criteria criteria = JCRCriteriaFactory
                .createCriteria()
                .setWorkspace(MediaConfigurationManager.NT_MEDIA)
                .setBasePath("//*")
                // .add(Restrictions.eq("jcr:primaryType", "mgnl:contentNode"))
                .addOrder(Order.desc("@jcr:score"));
            AdvancedResult result = criteria.execute();
            for (AdvancedResultItem node : result.getItems())
            {
                if (node.hasNode("thumbnail"))
                {
                    Node thumbnail = node.getNode("thumbnail");
                    if (NodeUtil.isNodeType(thumbnail, NodeType.NT_RESOURCE))
                    {
                        Node image = node.addNode("image", NodeType.NT_RESOURCE);
                        InputStream stream = node.getProperty(MgnlNodeType.JCR_DATA).getValue().getBinary().getStream();
                        try
                        {
                            image.setProperty(MgnlNodeType.JCR_DATA, new BinaryValue(stream));
                        }
                        catch (RepositoryException e)
                        {
                            log.error(e.getMessage(), e);
                        }

                        image.setProperty(
                            FileProperties.PROPERTY_EXTENSION,
                            PropertyUtil.getString(thumbnail, FileProperties.PROPERTY_EXTENSION));
                        image.setProperty(
                            FileProperties.PROPERTY_FILENAME,
                            PropertyUtil.getString(thumbnail, FileProperties.PROPERTY_FILENAME));
                        image.setProperty(
                            FileProperties.PROPERTY_LASTMODIFIED,
                            PropertyUtil.getString(thumbnail, FileProperties.PROPERTY_LASTMODIFIED));
                        image.setProperty(
                            FileProperties.PROPERTY_WIDTH,
                            PropertyUtil.getString(thumbnail, FileProperties.PROPERTY_WIDTH));
                        image.setProperty(
                            FileProperties.PROPERTY_HEIGHT,
                            PropertyUtil.getString(thumbnail, FileProperties.PROPERTY_HEIGHT));

                        thumbnail.remove();
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
