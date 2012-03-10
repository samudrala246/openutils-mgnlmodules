/**
 *
 * Spring integration module for Magnolia CMS (http://openutils.sourceforge.net/openutils-mgnlspring)
 * Copyright(C) ${project.inceptionYear}-2012, Openmind S.r.l. http://www.openmindonline.it
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

package it.openutils.mgnlspring;

import info.magnolia.context.MgnlContext;
import info.magnolia.context.WebContextImpl;

import org.springframework.mock.web.MockHttpServletRequest;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;


/**
 * @author fgiust
 * @version $Id:ContentBridgeTagTest.java 344 2007-06-30 15:31:28Z fgiust $
 */
public class ContentBridgeTagTest
{

    private ContentBridgeTag tag = new ContentBridgeTag();

    @BeforeClass
    public void setUp()
    {
        MockHttpServletRequest mockServletRequest = new MockHttpServletRequest();
        mockServletRequest.setContextPath("context");
        WebContextImpl webContext = new WebContextImpl();
        webContext.init(mockServletRequest, null, null);
        MgnlContext.setInstance(webContext);

    }

    @Test
    public void testRewriteSimple()
    {

        String action = tag.makeUrl("", "/test/it.html", "/spring/myaction.do", "/spring/start.do");
        Assert.assertEquals(action, "/spring/myaction.do"); // <!-- include this

        String pageUrl = RewriteVarsThreadLocal.getCurrentPageUrl();
        Assert.assertEquals(pageUrl, "/test/it.html");

        String finalUrl = UrlFunctions.url("/spring/pippo.do");
        Assert.assertEquals(finalUrl, "/test/it.html?_action=/spring/pippo.do");
    }

    @Test
    public void testRewriteNoPath()
    {

        String action = tag.makeUrl("", "/test/it.html", "myaction.do", "/spring/start.do");
        Assert.assertEquals(action, "/spring/myaction.do"); // <!-- include this

        String pageUrl = RewriteVarsThreadLocal.getCurrentPageUrl();
        Assert.assertEquals(pageUrl, "/test/it.html");

        String finalUrl = UrlFunctions.url("/spring/pippo.do");
        Assert.assertEquals(finalUrl, "/test/it.html?_action=/spring/pippo.do");
    }

    @Test
    public void testRewriteParams()
    {

        String action = tag.makeUrl("", "/test/it.html", "myaction.do", "/spring/start.do");
        Assert.assertEquals(action, "/spring/myaction.do"); // <!-- include this

        String pageUrl = RewriteVarsThreadLocal.getCurrentPageUrl();
        Assert.assertEquals(pageUrl, "/test/it.html");

        String finalUrl = UrlFunctions.url("/spring/pippo.do?new=1&old=2");
        Assert.assertEquals(finalUrl, "/test/it.html?_action=/spring/pippo.do&amp;new=1&amp;old=2");
    }

    @Test
    public void testRewriteActionParams()
    {

        String action = tag.makeUrl("", "/mac/it/index.html", "iscrizioneEvento.do?idEvento=2", "/spring/start.do");
        Assert.assertEquals(action, "/spring/iscrizioneEvento.do?idEvento=2"); // <!-- include this

        String pageUrl = RewriteVarsThreadLocal.getCurrentPageUrl();
        Assert.assertEquals(pageUrl, "/mac/it/index.html");

        String finalUrl = UrlFunctions.url("iscrizioneEvento.do?idEvento=2");
        Assert.assertEquals(finalUrl, "/mac/it/index.html?_action=iscrizioneEvento.do&amp;idEvento=2");
    }

}
