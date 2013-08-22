/**
 *
 * Rules module for Magnolia CMS (http://www.openmindlab.com/lab/products/mgnlrules.html)
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

package net.sourceforge.openutils.mgnlrules.dialog;

import java.util.Map;

import net.sourceforge.openutils.mgnlcontrols.dialog.ConfigurableFreemarkerDialog;


/**
 * @author dschivo
 */
public class ExpressionDialog extends ConfigurableFreemarkerDialog
{

    private static final String ATTRIBUTE_FCKED_LOADED = "info.magnolia.cms.gui.dialog.fckedit.loaded";

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getPath()
    {
        return "dialog/expression.ftl";
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    protected void addToParameters(Map parameters)
    {
        boolean fckalreadyrendered = getRequest().getAttribute(ATTRIBUTE_FCKED_LOADED) != null;
        if (!fckalreadyrendered)
        {
            getRequest().setAttribute(ATTRIBUTE_FCKED_LOADED, "true");
        }
        parameters.put("fckalreadyrendered", fckalreadyrendered);
        super.addToParameters(parameters);
    }
}
