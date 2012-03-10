/**
 *
 * Criteria API for Magnolia CMS (http://www.openmindlab.com/lab/products/mgnlcriteria.html)
 * Copyright(C) 2009-2011, Openmind S.r.l. http://www.openmindonline.it
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

package net.sourceforge.openutils.mgnlcriteria.advanced;

import info.magnolia.context.MgnlContext;
import info.magnolia.repository.RepositoryConstants;
import it.openutils.mgnlutils.test.RepositoryTestConfiguration;
import it.openutils.mgnlutils.test.TestNgRepositoryTestcase;
import net.sourceforge.openutils.mgnlcriteria.jcr.query.AdvancedResult;
import net.sourceforge.openutils.mgnlcriteria.jcr.query.AdvancedResultItem;
import net.sourceforge.openutils.mgnlcriteria.jcr.query.Criteria;
import net.sourceforge.openutils.mgnlcriteria.jcr.query.JCRCriteriaFactory;
import net.sourceforge.openutils.mgnlcriteria.jcr.query.ResultIterator;
import net.sourceforge.openutils.mgnlcriteria.jcr.query.criterion.Restrictions;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;


/**
 * Tests pagination in criteria queries.
 * @author fnecci
 */
@RepositoryTestConfiguration(jackrabbitRepositoryConfig = "/crit-repository/jackrabbit-test-configuration.xml", repositoryConfig = "/crit-repository/test-repositories.xml", bootstrapFiles = "/crit-bootstrap/website.letters.xml")
public class OrderingTest extends TestNgRepositoryTestcase
{

    private static String[] LETTERS_ARRAY = {
        "A",
        "B",
        "C",
        "D",
        "E",
        "F",
        "G",
        "H",
        "I",
        "J",
        "K",
        "L",
        "M",
        "N",
        "O",
        "P",
        "Q",
        "R",
        "S",
        "T",
        "U",
        "V",
        "W",
        "X",
        "Y",
        "Z" };

    private int PAGINATION_LENGTH = 10;

    /**
     * {@inheritDoc}
     */
    @Override
    @BeforeClass
    public void setUp() throws Exception
    {

        super.setUp();

        // Titles of the nodes in this workspace:
        // - Letters
        // --- A
        // --- B
        // --- C
        // --- ...
        // --- X
        // --- Y
        // --- Z
        MgnlContext.getHierarchyManager(RepositoryConstants.WEBSITE).save();
    }

    /**
     * Retrieves all letters.
     * @throws Exception
     */
    @Test
    public void testNoOrderNoPagination() throws Exception
    {
        Criteria criteria = JCRCriteriaFactory.createCriteria().setWorkspace(RepositoryConstants.WEBSITE);
        criteria.setBasePath("/letters");
        criteria.add(Restrictions.eq("@jcr:primaryType", "mgnl:content"));

        AdvancedResult advResult = criteria.execute();

        Assert.assertEquals(advResult.getTotalSize(), LETTERS_ARRAY.length);

        ResultIterator<AdvancedResultItem> resultIterator = advResult.getItems();
        Assert.assertEquals(resultIterator.getSize(), LETTERS_ARRAY.length);

        int i = 0;
        for (AdvancedResultItem currentResult : resultIterator)
        {
            Assert.assertEquals(currentResult.getTitle(), LETTERS_ARRAY[i]);
            i++;
        }
    }

    /**
     * Retrieves all letters.
     * @throws Exception
     */
    @Test
    public void testNoOrderWithPagination() throws Exception
    {

        Criteria criteria = JCRCriteriaFactory.createCriteria().setWorkspace(RepositoryConstants.WEBSITE);
        criteria.setBasePath("/letters");
        criteria.add(Restrictions.eq("@jcr:primaryType", "mgnl:content"));
        criteria.setPaging(PAGINATION_LENGTH, 1);
        criteria.setForcePagingWithDocumentOrder(true);

        log.debug(criteria.toXpathExpression());

        AdvancedResult advResult = criteria.execute();

        Assert.assertEquals(advResult.getTotalSize(), LETTERS_ARRAY.length, "Unset total size.");

        ResultIterator<AdvancedResultItem> resultIterator = advResult.getItems();
        Assert.assertEquals(resultIterator.getSize(), PAGINATION_LENGTH, "Wrong iterator size.");

        int i = 0;
        for (AdvancedResultItem currentResult : resultIterator)
        {
            Assert.assertEquals(currentResult.getTitle(), LETTERS_ARRAY[i], "Position "
                + i
                + ": found "
                + currentResult.getTitle()
                + " instead of "
                + LETTERS_ARRAY[i]);
            i++;
        }
        Assert.assertEquals(i, PAGINATION_LENGTH, "Wrong number of results returned by the iterator.");

        resultIterator = advResult.getItems();
        i = 0;

        // check that hasNext doesn't change the result of getPosition()
        while (resultIterator.hasNext() && resultIterator.hasNext() && resultIterator.hasNext())
        {
            AdvancedResultItem currentResult = resultIterator.next();
            Assert.assertEquals(currentResult.getTitle(), LETTERS_ARRAY[i], "Position "
                + i
                + ": found "
                + currentResult.getTitle()
                + " instead of "
                + LETTERS_ARRAY[i]);
            i++;
        }

        Assert.assertEquals(
            i,
            PAGINATION_LENGTH,
            "Wrong number of results returned by the iterator when calling hasNext() more than once.");
    }

    /**
     * Retrieves all letters.
     * @throws Exception
     */
    @Test
    public void testWithBeans() throws Exception
    {

        Criteria criteria = JCRCriteriaFactory.createCriteria().setWorkspace(RepositoryConstants.WEBSITE);
        criteria.setBasePath("/letters");
        criteria.add(Restrictions.eq("@jcr:primaryType", "mgnl:content"));
        criteria.setPaging(PAGINATION_LENGTH, 1);
        criteria.setForcePagingWithDocumentOrder(true);

        log.debug(criteria.toXpathExpression());

        AdvancedResult advResult = criteria.execute();

        Assert.assertEquals(advResult.getTotalSize(), LETTERS_ARRAY.length, "Unset total size.");

        ResultIterator<Content2BeanTest.Page> resultIterator = advResult.getItems(Content2BeanTest.Page.class);
        Assert.assertEquals(resultIterator.getSize(), PAGINATION_LENGTH, "Wrong iterator size.");

        int i = 0;
        for (Content2BeanTest.Page currentResult : resultIterator)
        {
            Assert.assertEquals(currentResult.getTitle(), LETTERS_ARRAY[i], "Position "
                + i
                + ": found "
                + currentResult.getTitle()
                + " instead of "
                + LETTERS_ARRAY[i]);
            i++;
        }
        Assert.assertEquals(i, PAGINATION_LENGTH, "Wrong number of results returned by the iterator.");

    }

    /**
     * Retrieves all letters.
     * @throws Exception
     */
    @Test
    public void testNoOrderWithOffset() throws Exception
    {

        int offset = 9;

        Criteria criteria = JCRCriteriaFactory.createCriteria().setWorkspace(RepositoryConstants.WEBSITE);
        criteria.setBasePath("/letters");
        criteria.add(Restrictions.eq("@jcr:primaryType", "mgnl:content"));
        criteria.setForcePagingWithDocumentOrder(true);
        criteria.setFirstResult(offset);
        criteria.setMaxResults(Integer.MAX_VALUE);

        log.debug(criteria.toXpathExpression());

        AdvancedResult advResult = criteria.execute();

        Assert.assertEquals(advResult.getTotalSize(), LETTERS_ARRAY.length, "Unset total size.");

        ResultIterator<AdvancedResultItem> resultIterator = advResult.getItems();
        Assert.assertEquals(resultIterator.getSize(), LETTERS_ARRAY.length - offset, "Wrong iterator size.");

        int i = 0;
        for (AdvancedResultItem currentResult : resultIterator)
        {
            Assert.assertEquals(currentResult.getTitle(), LETTERS_ARRAY[i + offset], "Position "
                + i
                + ": found "
                + currentResult.getTitle()
                + " instead of "
                + LETTERS_ARRAY[i + offset]);
            i++;
        }
        Assert.assertEquals(i, LETTERS_ARRAY.length - offset, "Wrong number of results returned by the iterator.");

        resultIterator = advResult.getItems();
        i = 0;

        // check that hasNext doesn't change the result of getPosition()
        while (resultIterator.hasNext() && resultIterator.hasNext() && resultIterator.hasNext())
        {
            AdvancedResultItem currentResult = resultIterator.next();
            Assert.assertEquals(currentResult.getTitle(), LETTERS_ARRAY[i + offset], "Position "
                + i
                + ": found "
                + currentResult.getTitle()
                + " instead of "
                + LETTERS_ARRAY[i + offset]);
            i++;
        }

        Assert.assertEquals(
            i,
            LETTERS_ARRAY.length - offset,
            "Wrong number of results returned by the iterator when calling hasNext() more than once.");
    }

}
