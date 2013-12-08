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

import javax.jcr.RepositoryException;

import net.sourceforge.openutils.mgnlcriteria.jcr.query.AdvancedResult;
import net.sourceforge.openutils.mgnlcriteria.jcr.query.AdvancedResultItem;
import net.sourceforge.openutils.mgnlcriteria.jcr.query.Criteria;
import net.sourceforge.openutils.mgnlcriteria.jcr.query.JCRCriteriaFactory;
import net.sourceforge.openutils.mgnlcriteria.jcr.query.criterion.Order;
import net.sourceforge.openutils.mgnlcriteria.jcr.query.criterion.Restrictions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * A task that replace any occurrence of a given paragraph with another at startup (handy for renamed paragraphs).
 * @author fgiust
 * @version $Id$
 */
public class ReplaceParagraphTask extends AbstractRepositoryTask
{

    private final String actualTemplate;

    private final String newTemplate;

    private Logger log = LoggerFactory.getLogger(AnonymousUserSetupTask.class);

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

        Criteria criteria = JCRCriteriaFactory
            .createCriteria()
            .setWorkspace(RepositoryConstants.WEBSITE)
            .add(Restrictions.eq("@jcr:primaryType", MgnlNodeType.NT_COMPONENT))
            .add(Restrictions.eq("MetaData/mgnl:template", actualTemplate))
            .addOrder(Order.desc("@jcr:score"));

        log.debug("Running query: {}", criteria.toXpathExpression());

        AdvancedResult result = criteria.execute();

        for (AdvancedResultItem page : result.getItems())
        {
            log.warn("Replacing template {} with {} in {}", new Object[]{
                MetaDataUtil.getTemplate(page),
                newTemplate,
                page.getHandle() });

            MetaDataUtil.getMetaData(page).setTemplate(newTemplate);
        }

    }

}
