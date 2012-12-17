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

package it.openutils.mgnlutils.el;

import info.magnolia.cms.beans.config.ServerConfiguration;
import info.magnolia.cms.beans.config.URI2RepositoryManager;
import info.magnolia.cms.i18n.DefaultI18nContentSupport;
import info.magnolia.cms.i18n.I18nContentSupportFactory;
import info.magnolia.cms.i18n.LocaleDefinition;
import info.magnolia.context.MgnlContext;
import info.magnolia.link.LinkTransformerManager;
import info.magnolia.test.ComponentsTestUtil;
import info.magnolia.test.mock.MockWebContext;
import it.openutils.mgnlutils.test.RepositoryTestConfiguration;
import it.openutils.mgnlutils.test.TestNgRepositoryTestcase;

import java.lang.reflect.Method;
import java.util.Locale;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;


/**
 * @author dschivo
 * @version $Id$
 */
@RepositoryTestConfiguration(repositoryConfig = "/utils-repository/test-repositories.xml", jackrabbitRepositoryConfig = "/utils-repository/jackrabbit-test-configuration.xml", bootstrapFiles = {
    "/utils-bootstrap/config.server.xml",
    "/utils-bootstrap/website.pets.xml",
    "/utils-bootstrap/config.modules.testmodule.xml" }, magnoliaProperties = "/test-magnolia.properties")
public class MgnlUtilsElFunctionsTest extends TestNgRepositoryTestcase
{

    LocaleDefinition en = LocaleDefinition.make("en", null, true);

    LocaleDefinition it = LocaleDefinition.make("it", null, true);

    @Test
    public void testSplitAndTokenize() throws Exception
    {
        String text = "lorem" + '\t' + "ipsum" + "\r\n" + "dolor" + '\t' + "sit";
        String[][] result = MgnlUtilsElFunctions.splitAndTokenize(text);
        Assert.assertNotNull(result);
        Assert.assertEquals(result.length, 2);
        String[] row1 = result[0];
        String[] row2 = result[1];
        Assert.assertNotNull(row1);
        Assert.assertEquals(row1.length, 2);
        Assert.assertNotNull(row2);
        Assert.assertEquals(row2.length, 2);
        Assert.assertEquals(row1[0], "lorem");
        Assert.assertEquals(row1[1], "ipsum");
        Assert.assertEquals(row2[0], "dolor");
        Assert.assertEquals(row2[1], "sit");
    }

    @Test
    public void testLink() throws Exception
    {
        String linkBlankTest = MgnlUtilsElFunctions.link("");
        Assert.assertEquals(linkBlankTest, "/testcontextpath");
        String linkExternalTest = MgnlUtilsElFunctions.link("http://www.google.com");
        Assert.assertEquals(linkExternalTest, "http://www.google.com");
        String linkDefaultTest = MgnlUtilsElFunctions.link("/pets/cats");
        Assert.assertEquals(linkDefaultTest, "/testcontextpath/pets/cats.html");
        String linkDefaultHtmlTest = MgnlUtilsElFunctions.link("/pets/cats.html");
        Assert.assertEquals(linkDefaultHtmlTest, "/testcontextpath/pets/cats.html");
        String linkUUIDTest = MgnlUtilsElFunctions.link("ceda8658-6df8-4b67-be4d-f8ea831e46ab");
        Assert.assertEquals(linkUUIDTest, "/testcontextpath/pets/cats.html");

        // Change Locale to It
        MgnlContext.setLocale(it.getLocale());

        String linkBlankItTest = MgnlUtilsElFunctions.link("");
        Assert.assertEquals(linkBlankItTest, "/testcontextpath");
        String linkExternalItTest = MgnlUtilsElFunctions.link("http://www.google.com");
        Assert.assertEquals(linkExternalItTest, "http://www.google.com");
        String linkDefaultItTest = MgnlUtilsElFunctions.link("/pets/cats");
        Assert.assertEquals(linkDefaultItTest, "/testcontextpath/it/pets/cats.html");
        String linkDefaultHtmlItTest = MgnlUtilsElFunctions.link("/pets/cats.html");
        Assert.assertEquals(linkDefaultHtmlItTest, "/testcontextpath/it/pets/cats.html");
        String linkUUIDItTest = MgnlUtilsElFunctions.link("ceda8658-6df8-4b67-be4d-f8ea831e46ab");
        Assert.assertEquals(linkUUIDItTest, "/testcontextpath/it/pets/cats.html");
    }

    @Override
    @BeforeClass
    public void setUp() throws Exception
    {
        super.setUp();

        DefaultI18nContentSupport i18nSupport = (DefaultI18nContentSupport) I18nContentSupportFactory.getI18nSupport();
        i18nSupport.setEnabled(true);
        i18nSupport.addLocale(en);
        i18nSupport.addLocale(it);
        i18nSupport.setFallbackLocale(en.getLocale());

        ((MockWebContext) MgnlContext.getInstance()).setContextPath("/testcontextpath");
        ServerConfiguration.getInstance().setDefaultExtension("html");
        try
        {
            Class classI18nContentSupport = Class.forName("info.magnolia.cms.i18n.DefaultI18nContentSupport");
            Method i18nGetLocale = classI18nContentSupport.getMethod("getDefaultLocale");
            // Magnolia version >= 4.3.x
            i18nGetLocale.invoke(i18nSupport, en.getLocale());
        }
        catch (Throwable e)
        {
            // Magnolia version < 4.3.x
            MgnlContext.getInstance().setLocale(new Locale(""));
        }

        // info.magnolia.link.BaseLinkTest.setUp()
        // not configured in the repository
        ComponentsTestUtil.setImplementation(URI2RepositoryManager.class, URI2RepositoryManager.class);
        ComponentsTestUtil.setInstance(LinkTransformerManager.class, new LinkTransformerManager());

    }
}