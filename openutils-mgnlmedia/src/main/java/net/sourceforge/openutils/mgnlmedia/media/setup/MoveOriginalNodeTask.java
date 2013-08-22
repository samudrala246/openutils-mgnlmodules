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

import info.magnolia.cms.core.search.Query;
import info.magnolia.context.MgnlContext;
import info.magnolia.module.InstallContext;
import info.magnolia.module.delta.Task;
import info.magnolia.module.delta.TaskExecutionException;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

import net.sourceforge.openutils.mgnlcriteria.jcr.query.AdvancedResult;
import net.sourceforge.openutils.mgnlcriteria.jcr.query.AdvancedResultItem;
import net.sourceforge.openutils.mgnlcriteria.jcr.query.DirectJcrQuery;
import net.sourceforge.openutils.mgnlcriteria.jcr.query.JCRCriteriaFactory;
import net.sourceforge.openutils.mgnlcriteria.jcr.query.ResultIterator;
import net.sourceforge.openutils.mgnlmedia.media.configuration.MediaConfigurationManager;
import net.sourceforge.openutils.mgnlmedia.media.lifecycle.MediaModule;
import net.sourceforge.openutils.mgnlmedia.media.types.impl.BaseTypeHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author fgiust
 * @version $Id$
 */
public class MoveOriginalNodeTask implements Task
{

    /**
     * Logger.
     */
    private static Logger log = LoggerFactory.getLogger(MoveOriginalNodeTask.class);

    public String getName()
    {
        return "file store nodedata name updater";
    }

    public String getDescription()
    {
        return "update custom nodedata names (where original files are stored) to \"original\"";
    }

    public void execute(InstallContext installContext) throws TaskExecutionException
    {
        try
        {
            Session session = installContext.getJCRSession(MediaModule.REPO);
            reset(session, "image", "image");
            reset(session, "video", "video");
            reset(session, "audio", "audio");
        }
        catch (RepositoryException ex)
        {
            log.error(ex.getMessage(), ex);
        }
    }

    public static void execute() throws RepositoryException
    {
        Session session = MgnlContext.getJCRSession(MediaModule.REPO);

        reset(session, "image", "image");
        reset(session, "video", "video");
        reset(session, "audio", "audio");
    }

    public static void reset(Session session, String type, String nodedataOldName) throws RepositoryException
    {

        DirectJcrQuery query = JCRCriteriaFactory.createDirectJcrQuery(session, "//*[@jcr:primaryType = '"
            + MediaConfigurationManager.MEDIA.getSystemName()
            + "' and "
            + nodedataOldName
            + " and @type='"
            + type
            + "']", Query.XPATH);

        AdvancedResult result = query.execute();

        ResultIterator<AdvancedResultItem> items = result.getItems();

        while (items.hasNext())
        {
            AdvancedResultItem media = items.next();

            if (media.hasProperty(nodedataOldName))
            {
                try
                {
                    // it's a binary nodedata -> node
                    session.getWorkspace().move(
                        media.getPath() + "/" + nodedataOldName,
                        media.getPath() + "/" + BaseTypeHandler.ORGINAL_NODEDATA_NAME);
                }
                catch (RepositoryException e)
                {
                    log.warn(
                        "Unable to move node from {} to {}",
                        media.getPath() + "/" + nodedataOldName,
                        media.getPath() + "/" + BaseTypeHandler.ORGINAL_NODEDATA_NAME);
                }
            }
        }
    }
}