/**
 *
 * ContextMenu Module for Magnolia CMS (http://www.openmindlab.com/lab/products/mgnlcontextmenu.html)
 * Copyright(C) 2010-2013, Openmind S.r.l. http://www.openmindonline.it
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

package net.sourceforge.openutils.mgnlcontextmenu.dialog;

import info.magnolia.cms.core.Content;
import info.magnolia.cms.util.RequestFormUtil;
import info.magnolia.module.ModuleRegistry;

import javax.servlet.http.HttpServletRequest;

import net.sourceforge.openutils.mgnlcontextmenu.configuration.PersistenceStrategy;
import net.sourceforge.openutils.mgnlcontextmenu.configuration.Scope;
import net.sourceforge.openutils.mgnlcontextmenu.module.ContextMenuModule;

import org.apache.commons.lang.StringUtils;


/**
 * @author dschivo
 * @version $Id$
 */
public class DialogControlUtils
{

    public static String getValue(HttpServletRequest request, Content node, String scope)
    {
        String name = new RequestFormUtil(request).getParameter("entryName");
        String value = null;
        if (name != null)
        {
            ContextMenuModule module = ModuleRegistry.Factory.getInstance().getModuleInstance(ContextMenuModule.class);
            PersistenceStrategy strategy = module.getPersistenceStrategy();
            if (strategy != null)
            {
                value = strategy.readEntry(node, name, Enum.valueOf(Scope.class, scope));
            }
        }
        return StringUtils.defaultString(value);
    }

}
