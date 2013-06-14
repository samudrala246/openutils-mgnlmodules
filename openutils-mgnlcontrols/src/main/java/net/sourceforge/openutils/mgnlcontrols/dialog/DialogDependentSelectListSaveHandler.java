/**
 *
 * Controls module for Magnolia CMS (http://www.openmindlab.com/lab/products/controls.html)
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

package net.sourceforge.openutils.mgnlcontrols.dialog;

import info.magnolia.cms.beans.runtime.MultipartForm;
import info.magnolia.cms.core.Content;
import info.magnolia.cms.security.AccessDeniedException;
import info.magnolia.cms.util.NodeDataUtil;
import info.magnolia.module.admininterface.FieldSaveHandler;

import javax.jcr.RepositoryException;

import org.apache.commons.lang.StringUtils;


/**
 * @author diego
 */
public class DialogDependentSelectListSaveHandler implements FieldSaveHandler
{

    /**
     * {@inheritDoc}
     */
    public void save(Content parentNode, Content configNode, String name, MultipartForm form, int type, int valueType,
        int isRichEditValue, int encoding) throws RepositoryException, AccessDeniedException
    {
        String selectName, selectValue;
        int i = 0;
        while (!StringUtils.isEmpty(selectValue = form.getParameter(selectName = name + "Select" + i++)))
        {
            NodeDataUtil.getOrCreateAndSet(parentNode, selectName, selectValue);
        }
        while (parentNode.hasNodeData(selectName))
        {
            parentNode.deleteNodeData(selectName);
            selectName = name + "Select" + i++;
        }
        NodeDataUtil.getOrCreateAndSet(parentNode, name, form.getParameter(name));
    }

}
