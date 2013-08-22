/**
 *
 * Controls module for Magnolia CMS (http://www.openmindlab.com/lab/products/controls.html)
 * Copyright(C) 2008-2013, Openmind S.r.l. http://www.openmindonline.it
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

import info.magnolia.cms.core.Content;
import info.magnolia.cms.gui.dialog.DialogControl;

import java.util.Map;

import javax.jcr.RepositoryException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;


/**
 * @author diego
 */
public class DialogDependentSelectListAndRadioGroup extends ConfigurableFreemarkerDialog
{

    private DialogDependentSelectList dependentSelectList;

    private DialogRadioGroup radioGroup;

    /**
	 * 
	 */
    public DialogDependentSelectListAndRadioGroup()
    {
    }

    /**
     * Returns the dependentSelectList.
     * @return the dependentSelectList
     */
    public DialogDependentSelectList getDependentSelectList()
    {
        if (dependentSelectList == null)
        {
            dependentSelectList = new DialogDependentSelectList();
        }
        return dependentSelectList;
    }

    /**
     * Returns the radioGroup.
     * @return the radioGroup
     */
    public DialogRadioGroup getRadioGroup()
    {
        if (radioGroup == null)
        {
            radioGroup = new DialogRadioGroup()
            {

                /**
                 * {@inheritDoc}
                 */
                @Override
                protected DialogControl getThisDialogControl()
                {
                    return DialogDependentSelectListAndRadioGroup.this;
                }

            };
        }
        return radioGroup;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setName(String s)
    {
        super.setName(s);
        getDependentSelectList().setName(s);
        getRadioGroup().setName(s);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getPath()
    {
        return "dialogs/dependentSelectListAndRadioGroup.ftl";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void init(HttpServletRequest request, HttpServletResponse response, Content websiteNode, Content configNode)
        throws RepositoryException
    {
        super.init(request, response, websiteNode, configNode);
        getDependentSelectList().init(request, response, websiteNode, configNode);
        getRadioGroup().init(request, response, websiteNode, configNode);
        if (StringUtils.isEmpty(getConfigValue("saveHandler")))
        {
            setConfig(
                "saveHandler",
                "net.sourceforge.openutils.mgnlcontrols.dialog.DialogDependentSelectListSaveHandler");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void addToParameters(Map<String, Object> parameters)
    {
        super.addToParameters(parameters);
        getDependentSelectList().addToParameters(parameters);
        getRadioGroup().addToParameters(parameters);
    }

}
