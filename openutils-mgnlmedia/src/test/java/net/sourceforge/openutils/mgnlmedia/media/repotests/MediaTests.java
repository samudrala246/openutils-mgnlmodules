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

package net.sourceforge.openutils.mgnlmedia.media.repotests;

import info.magnolia.cms.core.MgnlNodeType;
import info.magnolia.context.MgnlContext;
import info.magnolia.jcr.util.NodeUtil;
import info.magnolia.test.ComponentsTestUtil;
import it.openutils.mgnlutils.test.RepositoryTestConfiguration;
import it.openutils.mgnlutils.test.TestNgRepositoryTestcase;

import java.io.InputStream;

import javax.jcr.Node;
import javax.jcr.Session;

import net.sourceforge.openutils.mgnlmedia.media.configuration.DefaultMediaConfigurationManager;
import net.sourceforge.openutils.mgnlmedia.media.configuration.MediaConfigurationManager;
import net.sourceforge.openutils.mgnlmedia.media.utils.MediaLoadUtils;

import org.apache.commons.io.IOUtils;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;


/**
 * @author fgiust
 * @version $Id$
 */
@RepositoryTestConfiguration(jackrabbitRepositoryConfig = "/test-repository/jackrabbit-test-configuration.xml", //
repositoryConfig = "/test-repository/test-repositories.xml", //
autostart = true, //
bootstrapDirectory = {"/mgnl-bootstrap/media", "/mgnl-bootstrap/media-nooverwrite" })
// , startModules = @ModuleConfiguration(name = "media", moduleclass = MediaModule.class))
public class MediaTests extends TestNgRepositoryTestcase
{

    @Override
    @BeforeClass
    public void setUp() throws Exception
    {
        super.setUp();
        // MgnlContext.getHierarchyManager("media").save();
        ComponentsTestUtil.setImplementation(MediaConfigurationManager.class, DefaultMediaConfigurationManager.class);
    }

    @Test(enabled = true)
    public void uploadIco() throws Exception
    {

        Session hm = MgnlContext.getJCRSession("media");
        NodeUtil.createPath(hm.getRootNode(), "/test/folder", MgnlNodeType.NT_CONTENT);
        hm.save();

        InputStream is = getClass().getResourceAsStream("/images/openmind.ico");
        Node entry = MediaLoadUtils.loadEntry(is, "/test/folder", "openmind.ico", true);

        IOUtils.closeQuietly(is);

        // Assert.assertNotNull(entry);
    }

}
