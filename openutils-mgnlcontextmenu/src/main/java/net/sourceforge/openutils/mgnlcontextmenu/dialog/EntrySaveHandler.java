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

package net.sourceforge.openutils.mgnlcontextmenu.dialog;

import info.magnolia.cms.beans.runtime.MultipartForm;
import info.magnolia.cms.core.Content;
import info.magnolia.cms.security.AccessDeniedException;
import info.magnolia.cms.util.NodeDataUtil;
import info.magnolia.module.ModuleRegistry;
import info.magnolia.module.admininterface.FieldSaveHandler;

import javax.jcr.RepositoryException;

import net.sourceforge.openutils.elfunctions.ElStringUtils;
import net.sourceforge.openutils.mgnlcontextmenu.configuration.PersistenceStrategy;
import net.sourceforge.openutils.mgnlcontextmenu.configuration.Scope;
import net.sourceforge.openutils.mgnlcontextmenu.module.ContextMenuModule;

import org.apache.commons.lang.StringUtils;


/**
 * @author dschivo
 * @version $Id$
 */
public class EntrySaveHandler implements FieldSaveHandler
{

    public void save(Content parentNode, Content configNode, String name, MultipartForm form, int type, int valueType,
        int isRichEditValue, int encoding) throws RepositoryException, AccessDeniedException
    {
        String entryName = getRequestParameter(form, "entryName");
        String entryValue = getRequestParameter(form, name);

        if (StringUtils.isBlank(ElStringUtils.stripHtmlTags(entryValue)))
        {
            entryValue = null;
        }

        ContextMenuModule module = ModuleRegistry.Factory.getInstance().getModuleInstance(ContextMenuModule.class);
        PersistenceStrategy strategy = module.getPersistenceStrategy();
        if (strategy != null)
        {
            strategy.writeEntry(parentNode, entryName, entryValue, Enum.valueOf(Scope.class, NodeDataUtil.getString(
                configNode,
                "scope",
                "local")));
        }
    }

    protected String getRequestParameter(MultipartForm form, String name)
    {
        return form.getParameter(name);
    }

}
