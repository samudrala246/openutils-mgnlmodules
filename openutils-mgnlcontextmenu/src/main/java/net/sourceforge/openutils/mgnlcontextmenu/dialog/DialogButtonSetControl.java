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
import info.magnolia.cms.gui.control.ControlImpl;
import info.magnolia.cms.gui.dialog.DialogButtonSet;

import javax.jcr.RepositoryException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;


/**
 * @author fgiust
 * @version $Id$
 */
public class DialogButtonSetControl extends DialogButtonSet
{

    private HttpServletRequest request;

    @Override
    public void init(HttpServletRequest request, HttpServletResponse response, Content storageNode, Content configNode)
        throws RepositoryException
    {
        super.init(request, response, storageNode, configNode);
        this.request = request;

        String controlType = this.getConfigValue("controlType");

        // custom settings
        if (StringUtils.containsIgnoreCase(controlType, "radio")) { //$NON-NLS-1$
            setButtonType(ControlImpl.BUTTONTYPE_RADIO);
            setOptions(configNode, true);
        }
        else if (StringUtils.containsIgnoreCase(controlType, "checkboxSwitch")) { //$NON-NLS-1$
            setButtonType(ControlImpl.BUTTONTYPE_CHECKBOX);
            setOption(configNode);
        }
        else if (StringUtils.containsIgnoreCase(controlType, "checkbox")) { //$NON-NLS-1$
            setButtonType(ControlImpl.BUTTONTYPE_CHECKBOX);
            setOptions(configNode, false);
            setConfig("valueType", "multiple");
            setConfig("multiple", "true");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getValue()
    {
        if (this.value == null)
        {
            this.value = DialogControlUtils.getValue(
                request,
                getStorageNode().getJCRNode(),
                getConfigValue("scope", "local"));
        }
        return this.value;
    }
}
