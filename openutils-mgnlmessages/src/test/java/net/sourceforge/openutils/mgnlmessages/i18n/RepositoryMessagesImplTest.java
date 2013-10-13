/**
 *
 * Messages Module for Magnolia CMS (http://www.openmindlab.com/lab/products/messages.html)
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

package net.sourceforge.openutils.mgnlmessages.i18n;

import info.magnolia.cms.i18n.MessagesManager;
import info.magnolia.context.MgnlContext;
import it.openutils.mgnlutils.test.RepositoryTestConfiguration;
import it.openutils.mgnlutils.test.TestNgRepositoryTestcase;

import java.util.Locale;

import net.sourceforge.openutils.mgnlmessages.configuration.MessagesConfigurationManager;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;


/**
 * @author diego
 * @version $Id: $
 */
@RepositoryTestConfiguration(jackrabbitRepositoryConfig = "/msg-repository/jackrabbit-test-configuration.xml", repositoryConfig = "/msg-repository/test-repositories.xml", bootstrapFiles = "/msg-bootstrap/messages.buttons.xml")
public class RepositoryMessagesImplTest extends TestNgRepositoryTestcase
{

    /**
     * {@inheritDoc}
     */
    @Override
    @BeforeClass
    public void setUp() throws Exception
    {
        super.setUp();
        MgnlContext.getJCRSession(MessagesConfigurationManager.MESSAGES_REPO).save();
    }

    /**
     * @throws Exception
     */
    @Test
    public void testButtonsSave() throws Exception
    {
        MgnlContext.getInstance().setLocale(Locale.ENGLISH);
        Assert.assertEquals(MessagesManager.get("buttons.save"), "Save");
        MgnlContext.getInstance().setLocale(Locale.ITALIAN);
        Assert.assertEquals(MessagesManager.get("buttons.save"), "Salva");
        MgnlContext.getInstance().setLocale(Locale.JAPANESE);
        Assert.assertEquals(MessagesManager.get("buttons.save"), "\u4fdd\u5b58");
    }

    /**
     * @throws Exception
     */
    @Test
    public void testFixIllegalArgumentExceptionOnJackrabbit2_4_1() throws Exception
    {
        MgnlContext.getInstance().setLocale(Locale.ENGLISH);
        // MESSAGES-19 Uncaught IllegalArgumentException using jackrabbit 2.4.1
        // at org.apache.jackrabbit.core.id.NodeId.<init>(NodeId.java:129)
        Assert.assertEquals(MessagesManager.get("[foo]"), "???[foo]???");
    }

}
