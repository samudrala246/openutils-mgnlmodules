/**
 *
 * Test utilities for Magnolia CMS (http://www.openmindlab.com/lab/products/mgnltests.html)
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

package it.openutils.mgnlutils.test.junit;

import info.magnolia.context.MgnlContext;
import info.magnolia.jcr.util.NodeUtil;
import info.magnolia.module.ModuleRegistry;
import info.magnolia.objectfactory.Components;
import info.magnolia.repository.RepositoryConstants;
import it.openutils.mgnlutils.test.JUnitRepositoryTestcase;
import it.openutils.mgnlutils.test.ModuleConfiguration;
import it.openutils.mgnlutils.test.RepositoryTestConfiguration;
import it.openutils.mgnlutils.test.TestModule;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.junit.Assert;
import org.junit.Test;


@RepositoryTestConfiguration(repositoryConfig = "/utils-repository/test-repositories.xml", jackrabbitRepositoryConfig = "/utils-repository/jackrabbit-test-configuration.xml", bootstrapFiles = {
    "/utils-bootstrap/website.pets.xml",
    "/utils-bootstrap/website.letters.xml",
    "/utils-bootstrap/config.modules.testmodule.xml" }, magnoliaProperties = "/test-magnolia.properties", startModules = {@ModuleConfiguration(name = "testmodule", moduleclass = TestModule.class) })
public class JUnitRepositoryTestcaseTest extends JUnitRepositoryTestcase
{

    @Test
    public void repositoryTestConfigurationTest() throws RepositoryException
    {

        Session session = MgnlContext.getJCRSession(RepositoryConstants.WEBSITE);
        Assert.assertNotNull(session);

        Node pets = session.getNode("/pets");
        Assert.assertNotNull(pets);

        Node letters = session.getNode("/letters");
        Assert.assertNotNull(letters);

        Assert.assertEquals(NodeUtil.getCollectionFromNodeIterator(letters.getNodes()).size(), 26);
    }

    @Test
    public void moduleConfigAndStartTest()
    {

        ModuleRegistry mr = Components.getComponent(ModuleRegistry.class);

        Assert.assertTrue(mr.getModuleNames().contains("testmodule"));

        TestModule tm = (TestModule) Components.getComponent(ModuleRegistry.class).getModuleInstance("testmodule");
        Assert.assertNotNull(tm);
        Assert.assertTrue(tm.isStarted());

        Assert.assertEquals(tm.getTestConfigValue(), "test");
    }
}
