/**
 *
 * SimpleMedia Module for Magnolia CMS (http://www.openmindlab.com/lab/products/media.html)
 * Copyright(C) 2008-2012, Openmind S.r.l. http://www.openmindonline.it
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

import java.util.Collection;

import javax.jcr.RepositoryException;

import info.magnolia.cms.core.Content;
import info.magnolia.cms.core.HierarchyManager;
import info.magnolia.cms.core.ItemType;
import info.magnolia.cms.core.search.Query;
import info.magnolia.cms.core.search.QueryManager;
import info.magnolia.cms.core.search.QueryResult;
import info.magnolia.module.InstallContext;
import info.magnolia.module.delta.AbstractTask;
import info.magnolia.module.delta.TaskExecutionException;
import net.sourceforge.openutils.mgnlmedia.media.configuration.MediaConfigurationManager;
import net.sourceforge.openutils.mgnlmedia.media.lifecycle.MediaModule;


/**
 * @author molaschi
 * @version $Id$
 */
public class RemoveResolutionsNodeTask extends AbstractTask
{

    /**
     * Constructor
     */
    public RemoveResolutionsNodeTask()
    {
        super("Remove resolutions nodes", "Remove resolutions nodes which type is not mgnl:resolutions");
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
            Query query = mgr.createQuery("//resolutions", Query.XPATH);
            QueryResult result = query.execute();
            Collection<Content> resolutions = result.getContent(ItemType.CONTENTNODE.getSystemName());
            for (Content node : resolutions)
            {
                if (!node.isNodeType(MediaConfigurationManager.RESOLUTIONS.getSystemName()))
                {
                    hm.delete(node.getHandle());
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
