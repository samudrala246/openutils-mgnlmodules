/**
 *
 * Tasks for for Magnolia CMS (http://www.openmindlab.com/lab/products/mgnltasks.html)
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

package it.openutils.mgnltasks;

import info.magnolia.cms.beans.config.ContentRepository;
import info.magnolia.cms.core.Content;
import info.magnolia.cms.core.HierarchyManager;
import info.magnolia.cms.core.search.Query;
import info.magnolia.cms.core.search.QueryManager;
import info.magnolia.module.InstallContext;
import info.magnolia.module.delta.AbstractRepositoryTask;
import info.magnolia.module.delta.TaskExecutionException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.jcr.RepositoryException;
import javax.jcr.query.InvalidQueryException;

import org.apache.commons.lang.StringUtils;


/**
 * An abstract task that check for invalid templates/paragraphs. Should not be used directly, see
 * {@link CheckMissingTemplatesTask} and {@link CheckMissingParagraphsTask} instead.
 * @author fgiust
 * @version $Id$
 */
public abstract class BaseCheckMissingTask extends AbstractRepositoryTask
{

    private final String templateOrParagraph;

    private final String nodetype;

    /**
     * @param templateOrParagraph "template" or "paragraph"
     * @param nodetype node type foc checked nodes
     */
    public BaseCheckMissingTask(String templateOrParagraph, String nodetype)
    {
        super("Check " + templateOrParagraph + "s task", "Checking pages configured with missing "
            + templateOrParagraph
            + "s");
        this.templateOrParagraph = templateOrParagraph;
        this.nodetype = nodetype;

    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    protected void doExecute(InstallContext installContext) throws RepositoryException, TaskExecutionException
    {

        QueryManager configQueryManager = installContext
            .getHierarchyManager(ContentRepository.CONFIG)
            .getQueryManager();

        Collection<Content> templates = configQueryManager.createQuery(
            "//modules/*/" + templateOrParagraph + "s/*",
            Query.XPATH).execute().getContent("mgnl:contentNode");

        List<String> templ = new ArrayList<String>();
        Iterator<Content> availableTemplates = templates.iterator();
        while (availableTemplates.hasNext())
        {
            Content template = availableTemplates.next();
            templ.add(template.getName());
        }

        checkInvalidPages(installContext, templ);

    }

    /**
     * @param installContext
     * @param templates
     * @throws RepositoryException
     * @throws InvalidQueryException
     */
    @SuppressWarnings("unchecked")
    private void checkInvalidPages(InstallContext installContext, List<String> templates) throws RepositoryException,
        InvalidQueryException
    {
        HierarchyManager hm = installContext.getHierarchyManager(ContentRepository.WEBSITE);

        QueryManager qm = hm.getQueryManager();

        StringBuilder query = new StringBuilder("//*[jcr:primaryType='"
            + this.nodetype
            + "' and MetaData/mgnl:template and not(");

        Iterator<String> nameIterator = templates.iterator();
        while (nameIterator.hasNext())
        {
            String template = nameIterator.next();

            query.append("MetaData/mgnl:template='");
            query.append(template);
            query.append("'");
            if (nameIterator.hasNext())
            {
                query.append(" or ");
            }
        }

        query.append(")]");

        String queryAAsString = query.toString();

        log.debug("Running query: {}", queryAAsString);

        Collection<Content> nodes = qm.createQuery(queryAAsString, Query.XPATH).execute().getContent(this.nodetype);

        int count = 0;
        StringBuilder sb = new StringBuilder();

        for (Content page : nodes)
        {
            String template = page.getMetaData().getTemplate();

            if (StringUtils.isNotEmpty(template))
            {
                count++;
                sb.append(page.getHandle());
                sb.append("   ");
                sb.append(template);
                sb.append("\n");
            }
        }

        if (count > 0)
        {
            log.error("Found {} pages with invalid " + templateOrParagraph + "s:\n{}", count, sb.toString());
        }
    }
}
