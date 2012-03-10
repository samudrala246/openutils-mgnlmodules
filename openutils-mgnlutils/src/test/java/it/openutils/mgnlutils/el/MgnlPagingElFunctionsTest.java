/**
 *
 * Generic utilities for Magnolia CMS (http://www.openmindlab.com/lab/products/mgnlutils.html)
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

package it.openutils.mgnlutils.el;

import info.magnolia.context.MgnlContext;
import info.magnolia.test.mock.MockWebContext;
import it.openutils.mgnlutils.el.MgnlPagingElFunctions.Page;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;


/**
 * @author mmunaretto
 * @version $Id$
 */
public class MgnlPagingElFunctionsTest
{

    @BeforeTest
    public void setUp()
    {
        MockWebContext context = new MockWebContext();
        Map<String, String> parameters = new HashMap<String, String>();
        context.setParameters(parameters);
        MgnlContext.setInstance(context);
    }

    @Test
    public void testOneTotalPageWithOneVisiblePage() throws Exception
    {
        int total = 1;
        int visible = 1;
        String param = "page";
        List<Page> actual = MgnlPagingElFunctions.pageList(total, visible, param);

        Assert.assertEquals(actual.size(), 5);
    }

    @Test
    public void testTenTotalPagesWithThreeVisiblePages() throws Exception
    {
        int total = 10;
        int visible = 3;
        String param = "page";
        List<Page> actual = MgnlPagingElFunctions.pageList(total, visible, param);

        Assert.assertEquals(actual.size(), 8);
    }

    @Test
    public void testFiveTotalPagesWithTwoVisiblePages() throws Exception
    {
        int total = 10;
        int visible = 2;
        String param = "page";
        List<Page> actual = MgnlPagingElFunctions.pageList(total, visible, param);

        Assert.assertEquals(actual.size(), 7);
    }

}
