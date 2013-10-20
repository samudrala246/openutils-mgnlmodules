/**
 *
 * E-learning Module for Magnolia CMS (http://www.openmindlab.com/lab/products/lms.html)
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
package net.sourceforge.openutils.mgnllms.lms.dialog;

import info.magnolia.cms.core.Content;
import info.magnolia.cms.core.HierarchyManager;
import info.magnolia.cms.core.Path;
import info.magnolia.cms.util.NodeDataUtil;
import info.magnolia.context.MgnlContext;
import info.magnolia.module.admininterface.SaveHandler;
import info.magnolia.module.admininterface.dialogs.ConfiguredDialog;

import javax.jcr.RepositoryException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.openutils.mgnllms.module.LMSModule;
import net.sourceforge.openutils.mgnllms.module.LmsTypesManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author molaschi
 * @version $Id: LayerDialogMVC.java 1169 2009-04-29 20:39:50Z fgiust $
 */
public class PreSaveDialogMVC extends ConfiguredDialog
{

    /**
     * Logger.
     */
    private Logger log = LoggerFactory.getLogger(PreSaveDialogMVC.class);

    /**
     * @param name
     * @param request
     * @param response
     * @param configNode
     */
    public PreSaveDialogMVC(String name, HttpServletRequest request, HttpServletResponse response, Content configNode)
    {
        super(name, request, response, configNode);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void configureSaveHandler(SaveHandler saveHandler)
    {
        super.configureSaveHandler(saveHandler);

        saveHandler.setCreationItemType(LmsTypesManager.COURSE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean onPostSave(SaveHandler handler)
    {
        super.onPostSave(handler);
        Content node = this.getStorageNode();

        String type = NodeDataUtil.getString(node, "type");

        return LmsTypesManager.getInstance().getTypes().get(type).getHandler().onPostSave(node, form, request);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean onPreSave(SaveHandler control)
    {
        // TODO non mi piace molto...
        String type = this.request.getParameter("type");
        HierarchyManager hm = MgnlContext.getHierarchyManager(LMSModule.REPO);
        Content c = null;

        if (control.getNodeName().equals("mgnlNew"))
        {
            try
            {
                c = hm.getContent(control.getPath());
            }
            catch (RepositoryException e)
            {
                log.error("error getting {}", control.getPath(), e);
                return false;
            }
            control.setNodeName(Path.getUniqueLabel(c, Path.getValidatedLabel(LmsTypesManager
                .getInstance()
                .getTypes()
                .get(type)
                .getHandler()
                .getNewNodeName(c, form, request))));
        }
        else if (form.getDocument("zip") != null)
        {
            // on update
            try
            {
                c = hm.getContent(control.getPath());
                c = c.getParent();
            }
            catch (RepositoryException e)
            {
                log.error("error getting {}", control.getPath(), e);
                return false;
            }
            LmsTypesManager.getInstance().getTypes().get(type).getHandler().getNewNodeName(c, form, request);

        }

        return super.onPreSave(control);
    }
}
