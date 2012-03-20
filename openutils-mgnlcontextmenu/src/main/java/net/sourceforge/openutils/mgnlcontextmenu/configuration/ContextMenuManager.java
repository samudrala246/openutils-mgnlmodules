/**
 *
 * ContextMenu Module for Magnolia CMS (http://www.openmindlab.com/lab/products/mgnlcontextmenu.html)
 * Copyright(C) 2010-2012, Openmind S.r.l. http://www.openmindonline.it
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

package net.sourceforge.openutils.mgnlcontextmenu.configuration;

import info.magnolia.cms.beans.config.ObservedManager;
import info.magnolia.cms.core.Content;
import info.magnolia.cms.util.ContentUtil;
import info.magnolia.cms.util.NodeDataUtil;
import info.magnolia.objectfactory.Components;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.inject.Singleton;
import javax.jcr.RepositoryException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Observes the contextMenus node in the configuration workspace, and exposes menu definitions.
 * @author dschivo
 */
@Singleton
public class ContextMenuManager extends ObservedManager
{

    public static ContextMenuManager getInstance()
    {
        return Components.getSingleton(ContextMenuManager.class);
    }

    private Logger log = LoggerFactory.getLogger(getClass());

    private final List<ContextMenu> menus = new ArrayList<ContextMenu>();

    /**
     * Returns the menus.
     * @return the menus
     */
    public List<ContextMenu> getMenus()
    {
        return menus;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onClear()
    {
        menus.clear();
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    protected void onRegister(Content defNode)
    {
        for (Iterator iter = ContentUtil.getAllChildren(defNode).iterator(); iter.hasNext();)
        {
            Content menuNode = (Content) iter.next();
            ContextMenu menu = new ContextMenu();
            menu.setName(menuNode.getName());

            try
            {
                if (menuNode.hasContent("items"))
                {
                    Content itemsNode = menuNode.getContent("items");
                    for (Iterator iter2 = ContentUtil.getAllChildren(itemsNode).iterator(); iter2.hasNext();)
                    {
                        Content itemNode = (Content) iter2.next();
                        ContextMenuItem item = new ContextMenuItem();
                        item.setName(itemNode.getName());
                        item.setControlType(NodeDataUtil.getString(itemNode, "controlType"));
                        item.setIcon(NodeDataUtil.getString(itemNode, "icon"));
                        item.setText(NodeDataUtil.getString(itemNode, "text"));
                        item.setGlobalEnabled(NodeDataUtil.getBoolean(itemNode, "globalEnabled", false));
                        menu.getItems().add(item);
                    }
                }
            }
            catch (RepositoryException e)
            {
                log.error("Cannot get items of menu " + menu.getName(), e);
            }

            menu.setMouseoverClass(NodeDataUtil.getString(menuNode, "mouseoverClass"));
            menu.setMouseoverIcon(NodeDataUtil.getString(menuNode, "mouseoverIcon"));

            menus.add(menu);
        }

    }

}
