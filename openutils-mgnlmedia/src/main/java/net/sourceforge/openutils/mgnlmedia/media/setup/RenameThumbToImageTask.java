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
import info.magnolia.cms.core.search.Query;
import info.magnolia.cms.core.search.QueryManager;
import info.magnolia.cms.core.search.QueryResult;
import info.magnolia.module.InstallContext;
import info.magnolia.module.delta.AbstractTask;
import info.magnolia.module.delta.TaskExecutionException;
import it.openutils.mgnlutils.api.NodeUtilsExt;

import java.util.Collection;

import javax.jcr.Binary;
import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import net.sourceforge.openutils.mgnlcriteria.jcr.query.AdvancedResult;
import net.sourceforge.openutils.mgnlcriteria.jcr.query.AdvancedResultItem;
import net.sourceforge.openutils.mgnlcriteria.jcr.query.Criteria;
import net.sourceforge.openutils.mgnlcriteria.jcr.query.JCRCriteriaFactory;
import net.sourceforge.openutils.mgnlcriteria.jcr.query.criterion.Order;
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
        try
        {
            // [LB] FIXME
            Session hm = installContext.getJCRSession(MediaModule.REPO);
            Criteria criteria = JCRCriteriaFactory
                .createCriteria()
                .setWorkspace(MediaConfigurationManager.MEDIA.getSystemName())
                .setBasePath("//*")
                // .add(Restrictions.eq("jcr:primaryType", "mgnl:contentNode"))
                .addOrder(Order.desc("@jcr:score"));
            AdvancedResult result = criteria.execute();
            for (AdvancedResultItem node : result.getItems())
            {
                if (node.hasProperty("thumbnail"))
                {
                    Property thumbnail = node.getProperty("thumbnail");
                    if (thumbnail.getType() == PropertyType.BINARY)
                    {
                        // [LB] FIXME
                        NodeUtilsExt.setAttribute(
                            thumbnail,
                            "image",
                            FileProperties.PROPERTY_EXTENSION,
                            NodeUtilsExt.getAttribute(thumbnail, FileProperties.PROPERTY_EXTENSION));
                        NodeUtilsExt.setAttribute(
                            thumbnail,
                            "image",
                            FileProperties.PROPERTY_FILENAME,
                            NodeUtilsExt.getAttribute(thumbnail, FileProperties.PROPERTY_FILENAME));
                        NodeUtilsExt.setAttribute(
                            thumbnail,
                            "image",
                            FileProperties.PROPERTY_CONTENTTYPE,
                            NodeUtilsExt.getAttribute(thumbnail, FileProperties.PROPERTY_CONTENTTYPE));
                        NodeUtilsExt.setAttribute(
                            thumbnail,
                            "image",
                            FileProperties.PROPERTY_LASTMODIFIED,
                            NodeUtilsExt.getAttribute(thumbnail, FileProperties.PROPERTY_LASTMODIFIED));
                        NodeUtilsExt.setAttribute(
                            thumbnail,
                            "image",
                            FileProperties.PROPERTY_WIDTH,
                            NodeUtilsExt.getAttribute(thumbnail, FileProperties.PROPERTY_WIDTH));
                        NodeUtilsExt.setAttribute(
                            thumbnail,
                            "image",
                            FileProperties.PROPERTY_HEIGHT,
                            NodeUtilsExt.getAttribute(thumbnail, FileProperties.PROPERTY_HEIGHT));

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
