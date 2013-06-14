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

import info.magnolia.cms.core.Content;
import info.magnolia.cms.gui.dialog.DialogControl;
import info.magnolia.cms.util.NodeDataUtil;
import info.magnolia.objectfactory.Classes;
import info.magnolia.objectfactory.MgnlInstantiationException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.jcr.RepositoryException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.openutils.mgnlcontrols.dialog.ConfigurableFreemarkerDialog;

import org.apache.commons.lang.StringUtils;


/**
 * @author diego
 */
public class DialogDependentSelectList extends ConfigurableFreemarkerDialog
{

    private SelectOptionsProvider selectOptionsProvider;

    private List<String> treePathValues;

    private List<Map<String, String>> selectOptionsList;

    private Boolean leaf;

    /**
     * Returns the selectOptionsProvider.
     * @return the selectOptionsProvider
     */
    public SelectOptionsProvider getSelectOptionsProvider()
    {
        if (selectOptionsProvider == null)
        {
            String className = getConfigValue("selectOptionsProvider");
            if (StringUtils.isNotEmpty(className))
            {
                try
                {
                    selectOptionsProvider = Classes.newInstance(className);
                }
                catch (MgnlInstantiationException e)
                {
                    log.error("can't create select options provider", e);
                }
                catch (ClassNotFoundException e)
                {
                    log.error("can't create select options provider", e);
                }
            }
        }
        return selectOptionsProvider;
    }

    /**
     * Returns the treePathValues.
     * @return the treePathValues
     */
    public List<String> getTreePathValues()
    {
        if (treePathValues == null)
        {
            treePathValues = new ArrayList<String>();
            if (StringUtils.isEmpty(getRequest().getParameter("dependentSelectListCK")))
            {
                if (getStorageNode() != null)
                {
                    String treePathValue;
                    while (!StringUtils.isEmpty(treePathValue = NodeDataUtil.getString(getStorageNode(), getName()
                        + "Select"
                        + treePathValues.size())))
                    {
                        treePathValues.add(treePathValue);
                    }
                }
            }
            else
            {
                String treePathValue;
                while (!StringUtils.isEmpty(treePathValue = getRequest().getParameter(
                    getName() + "Select" + treePathValues.size())))
                {
                    treePathValues.add(treePathValue);
                }
            }
        }
        return treePathValues;
    }

    /**
     * Returns the selectOptionsList.
     * @return the selectOptionsList
     */
    public List<Map<String, String>> getSelectOptionsList()
    {
        if (selectOptionsList == null)
        {
            selectOptionsList = new ArrayList<Map<String, String>>();
            leaf = Boolean.FALSE;
            int length = getTreePathValues().size();
            for (int i = 0; i <= length; i++)
            {
                Map<String, String> options = getSelectOptionsProvider().getSelectOptions(
                    getTreePathValues().subList(0, i).toArray(new String[i]),
                    this);
                if (options.isEmpty())
                {
                    leaf = Boolean.TRUE;
                    break;
                }
                selectOptionsList.add(options);
            }
        }
        return selectOptionsList;
    }

    /**
     * Returns the leaf.
     * @return the leaf
     */
    public Boolean getLeaf()
    {
        if (leaf == null)
        {
            getSelectOptionsList();
        }
        return leaf;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getPath()
    {
        return "dialogs/dependentSelectList.ftl";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void init(HttpServletRequest request, HttpServletResponse response, Content websiteNode, Content configNode)
        throws RepositoryException
    {
        super.init(request, response, websiteNode, configNode);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void addToParameters(Map<String, Object> parameters)
    {
        super.addToParameters(parameters);
        parameters.put("treePathValues", getTreePathValues());
        parameters.put("selectOptionsList", getSelectOptionsList());
        parameters.put("leaf", getLeaf());
    }

    public interface SelectOptionsProvider
    {

        public Map<String, String> getSelectOptions(String[] treePathValues, DialogControl dialogControl);
    }
}
