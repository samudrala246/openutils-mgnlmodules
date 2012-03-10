/**
 *
 * simplemail module for Magnolia CMS (http://www.openmindlab.com/lab/products/mgnlmail.html)
 * Copyright(C) 2011-2011, Openmind S.r.l. http://www.openmindonline.it
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

package net.sourceforge.openutils.mgnlmail;

import info.magnolia.cms.gui.control.ContextMenuItem;
import info.magnolia.cms.gui.control.Tree;
import info.magnolia.module.admininterface.trees.WebsiteTreeConfiguration;

import javax.servlet.http.HttpServletRequest;


/**
 * @author fgiust
 * @version $Id$
 */
public class SimplemailTreeConfiguration extends WebsiteTreeConfiguration
{

    private String urlprefix = "email";

    /**
     * {@inheritDoc}
     */
    @Override
    public void prepareContextMenu(Tree tree, boolean browseMode, HttpServletRequest request)
    {
        super.prepareContextMenu(tree, browseMode, request);

        String action = "var w=window.open(mgnlEncodeURL(contextPath + '"
            + (urlprefix != null ? "/" + urlprefix : "")
            + "' + "
            + tree.getJavascriptTree()
            + ".selectedNode.path + '.html'),'mgnlInline','');if (w) w.focus();";

        ContextMenuItem menuOpen = tree.getMenu().getMenuItemByName("open");
        menuOpen.setOnclick(action);

        tree.setIconOndblclick(action);

        // todo: add menu item "send test mail"?
    }

    /**
     * Sets the urlprefix.
     * @param urlprefix the urlprefix to set
     */
    public void setUrlprefix(String urlprefix)
    {
        this.urlprefix = urlprefix;
    }
}
