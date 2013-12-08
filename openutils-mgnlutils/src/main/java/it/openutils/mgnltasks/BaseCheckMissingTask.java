/**
 *
 * Generic utilities for Magnolia CMS (http://www.openmindlab.com/lab/products/mgnlutils.html)
 * Copyright(C) 2009-2012, Openmind S.r.l. http://www.openmindonline.it
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

import info.magnolia.cms.core.MgnlNodeType;
import info.magnolia.jcr.util.MetaDataUtil;
import info.magnolia.module.InstallContext;
import info.magnolia.module.delta.AbstractRepositoryTask;
import info.magnolia.module.delta.TaskExecutionException;
import info.magnolia.repository.RepositoryConstants;

import java.util.ArrayList;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.query.InvalidQueryException;

import net.sourceforge.openutils.mgnlcriteria.jcr.query.AdvancedResult;
import net.sourceforge.openutils.mgnlcriteria.jcr.query.AdvancedResultItem;
import net.sourceforge.openutils.mgnlcriteria.jcr.query.Criteria;
import net.sourceforge.openutils.mgnlcriteria.jcr.query.JCRCriteriaFactory;
import net.sourceforge.openutils.mgnlcriteria.jcr.query.criterion.Order;
import net.sourceforge.openutils.mgnlcriteria.jcr.query.criterion.Restrictions;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


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

    private Logger log = LoggerFactory.getLogger(BaseCheckMissingTask.class);

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

        Criteria criteria = JCRCriteriaFactory
            .createCriteria()
            .setWorkspace(RepositoryConstants.CONFIG)
            .add(Restrictions.eq("@jcr:primaryType", MgnlNodeType.NT_CONTENTNODE))
            .addOrder(Order.desc("@jcr:score"));

        if (StringUtils.equals(templateOrParagraph, "template"))
        {
            criteria.setBasePath("//modules/*/templates/pages/*");
        }
        else
        {
            criteria.setBasePath("//modules/*/templates/components/*");
        }

        AdvancedResult result = criteria.execute();

        List<String> templ = new ArrayList<String>();

        for (AdvancedResultItem template : result.getItems())
        {
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

        Criteria criteria = JCRCriteriaFactory
            .createCriteria()
            .setWorkspace(RepositoryConstants.WEBSITE)
            .add(Restrictions.eq("@jcr:primaryType", nodetype))
            .add(Restrictions.not(Restrictions.in("MetaData/mgnl:template", templates)))
            .addOrder(Order.desc("@jcr:score"));

        log.debug("Running query: {}", criteria.toXpathExpression());

        AdvancedResult result = criteria.execute();

        int count = 0;
        StringBuilder sb = new StringBuilder();

        for (Node page : result.getItems())
        {
            String template = MetaDataUtil.getTemplate(page);

            if (StringUtils.isNotEmpty(template))
            {
                count++;
                sb.append(page.getPath());
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
