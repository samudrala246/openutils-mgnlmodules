/**
 *
 * Criteria API for Magnolia CMS (http://www.openmindlab.com/lab/products/mgnlcriteria.html)
 * Copyright(C) 2009-2013, Openmind S.r.l. http://www.openmindonline.it
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

package net.sourceforge.openutils.mgnlcriteria.jcr.query;

import info.magnolia.cms.core.MgnlNodeType;
import info.magnolia.cms.i18n.DefaultI18nContentSupport;
import info.magnolia.cms.i18n.I18nContentSupport;
import info.magnolia.cms.security.MgnlRoleManager;
import info.magnolia.cms.security.Realm;
import info.magnolia.cms.security.SecuritySupport;
import info.magnolia.cms.security.SecuritySupportImpl;
import info.magnolia.cms.security.SystemUserManager;
import info.magnolia.context.MgnlContext;
import info.magnolia.repository.RepositoryConstants;
import info.magnolia.test.ComponentsTestUtil;
import it.openutils.mgnlutils.test.RepositoryTestConfiguration;
import it.openutils.mgnlutils.test.TestNgRepositoryTestcase;

import javax.jcr.Node;

import net.sourceforge.openutils.mgnlcriteria.jcr.query.criterion.Restrictions;
import net.sourceforge.openutils.mgnlcriteria.tests.CriteriaTestUtils;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;


/**
 * @author dschivo
 */
@RepositoryTestConfiguration(jackrabbitRepositoryConfig = "/crit-repository/jackrabbit-test-configuration.xml", repositoryConfig = "/crit-repository/test-repositories.xml", bootstrapFiles = {
    "/crit-bootstrap/website.escape-sign.xml",
    "/crit-bootstrap/userroles.anonymous.xml",
    "/crit-bootstrap/users.system.anonymous.xml" })
public class EscapeSignTest extends TestNgRepositoryTestcase
{

    /**
     * {@inheritDoc}
     */
    @Override
    @BeforeClass
    public void setUp() throws Exception
    {
        super.setUp();

        // Nodes in this workspace:
        // - escape-sign (title=escape-sign)
        // --- fo°o (title=fo°o)
        // ----- bar (title=bar)
        // --- fo$o (title=fo$o)
        // ----- baz (title=baz)
        MgnlContext.getJCRSession(RepositoryConstants.WEBSITE).save();

        ComponentsTestUtil.setInstance(I18nContentSupport.class, new DefaultI18nContentSupport());

        // info.magnolia.cms.security.SecurityTest.setUp()
        final SecuritySupportImpl sec = new SecuritySupportImpl();
        sec.addUserManager(Realm.REALM_SYSTEM.getName(), new SystemUserManager());
        sec.setRoleManager(new MgnlRoleManager());
        ComponentsTestUtil.setInstance(SecuritySupport.class, sec);
    }

    /**
     * CRIT-53
     * @throws Exception
     */
    @Test
    public void testDegree() throws Exception
    {
        Criteria criteria = JCRCriteriaFactory
            .createCriteria()
            .setWorkspace(RepositoryConstants.WEBSITE)
            .setBasePath("/jcr:root/escape-sign/fo°o/*")
            .add(Restrictions.eq("@jcr:primaryType", MgnlNodeType.NT_PAGE));
        AdvancedResult result = criteria.execute();
        ResultIterator< ? extends Node> iterator = result.getItems();
        Assert.assertTrue(iterator.hasNext());
        Node resultNode = iterator.next();
        Assert.assertEquals(CriteriaTestUtils.title(resultNode), "bar");
    }

    /**
     * CRIT-54
     * @throws Exception
     */
    @Test
    public void testDollar() throws Exception
    {
        Criteria criteria = JCRCriteriaFactory
            .createCriteria()
            .setWorkspace(RepositoryConstants.WEBSITE)
            .setBasePath("/jcr:root/escape-sign/fo$o/*")
            .add(Restrictions.eq("@jcr:primaryType", MgnlNodeType.NT_PAGE));
        AdvancedResult result = criteria.execute();
        ResultIterator< ? extends Node> iterator = result.getItems();
        Assert.assertTrue(iterator.hasNext());
        Node resultNode = iterator.next();
        Assert.assertEquals(CriteriaTestUtils.title(resultNode), "baz");
    }

}
