/**
 *
 * Tasks for for Magnolia CMS (http://www.openmindlab.com/lab/products/mgnltasks.html)
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

package it.openutils.mgnltasks;

import info.magnolia.cms.core.Content;
import info.magnolia.cms.core.HierarchyManager;
import info.magnolia.cms.core.ItemType;
import info.magnolia.cms.core.search.Query;
import info.magnolia.cms.core.search.QueryManager;
import info.magnolia.module.InstallContext;
import info.magnolia.module.delta.AbstractRepositoryTask;
import info.magnolia.module.delta.TaskExecutionException;
import info.magnolia.repository.RepositoryConstants;

import java.util.Collection;

import javax.jcr.RepositoryException;


/**
 * A task that replace any occurrence of a given paragraph with another at startup (handy for renamed paragraphs).
 * @author fgiust
 * @version $Id$
 */
public class ReplaceParagraphTask extends AbstractRepositoryTask
{

    private final String actualTemplate;

    private final String newTemplate;

    /**
     * @param actualTemplate template to be replaced
     * @param newTemplate new template
     */
    public ReplaceParagraphTask(String actualTemplate, String newTemplate)
    {
        super("Replacing template " + actualTemplate + " with " + newTemplate, "Replacing template "
            + actualTemplate
            + " with "
            + newTemplate);
        this.actualTemplate = actualTemplate;
        this.newTemplate = newTemplate;

    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    protected void doExecute(InstallContext installContext) throws RepositoryException, TaskExecutionException
    {

        HierarchyManager hm = installContext.getHierarchyManager(RepositoryConstants.WEBSITE);

        QueryManager qm = hm.getQueryManager();

        StringBuilder query = new StringBuilder("//*[MetaData/mgnl:template ='");
        query.append(actualTemplate);
        query.append("']");

        String queryAAsString = query.toString();

        log.debug("Running query: {}", queryAAsString);

        Collection<Content> nodes = qm.createQuery(queryAAsString, Query.XPATH).execute().getContent(
            ItemType.CONTENTNODE.getSystemName());

        for (Content page : nodes)
        {
            log.warn("Replacing template " + page.getMetaData().getTemplate() + " with {} in {}", newTemplate, page
                .getHandle());
            page.getMetaData().setTemplate(newTemplate);
        }
    }

}
