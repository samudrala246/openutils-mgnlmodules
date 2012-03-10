/**
 *
 * Messages Module for Magnolia CMS (http://www.openmindlab.com/lab/products/messages.html)
 * Copyright(C) 2008-2012, Openmind S.r.l. http://www.openmindonline.it
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

package net.sourceforge.openutils.mgnlmessages.el;

import org.apache.commons.lang.StringUtils;
import org.testng.Assert;
import org.testng.annotations.Test;


public class MessagesElTest
{

    @Test
    public void testMessageTextContains()
    {
        Assert.assertTrue(MessagesEl.messageTextContains("L'impostazione non è stata salvata", "impostazione"));
        Assert.assertFalse(MessagesEl.messageTextContains("L'impostazione non è stata salvata", "impostazioni"));
    }

    @Test
    public void testMessageTextContainsEmpty()
    {
        Assert.assertFalse(MessagesEl.messageTextContains(null, null));
        Assert.assertFalse(MessagesEl.messageTextContains(StringUtils.EMPTY, StringUtils.EMPTY));
        Assert.assertFalse(MessagesEl.messageTextContains(null, "impostazioni"));
        Assert.assertFalse(MessagesEl.messageTextContains(StringUtils.EMPTY, "impostazioni"));
        Assert.assertFalse(MessagesEl.messageTextContains("L'impostazione non è stata salvata", null));
        Assert.assertTrue(MessagesEl.messageTextContains("L'impostazione non è stata salvata", StringUtils.EMPTY));
    }

    @Test
    public void testMessageTextContainsHtmlEntities()
    {
        Assert.assertTrue(MessagesEl.messageTextContains(
            "L&#39;impostazione non &egrave; stata salvata",
            "L'impostazione non è"));
    }

    @Test
    public void testMessageTextContainsHtmlTags()
    {
        Assert.assertTrue(MessagesEl.messageTextContains(
            "L&#39;impostazione <strong>non</strong> &egrave; stata salvata",
            "L'impostazione non è"));
    }
}
