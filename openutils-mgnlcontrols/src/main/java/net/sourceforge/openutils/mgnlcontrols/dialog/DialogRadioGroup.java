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
import info.magnolia.objectfactory.Classes;
import info.magnolia.objectfactory.MgnlInstantiationException;

import java.util.Map;

import javax.jcr.RepositoryException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.openutils.mgnlcontrols.dialog.ConfigurableFreemarkerDialog;

import org.apache.commons.lang.StringUtils;


/**
 * @author diego
 */
public class DialogRadioGroup extends ConfigurableFreemarkerDialog
{

    private RadioOptionsProvider radioOptionsProvider;

    private Map<String, String> radioOptions;

    /**
     * Returns the radioOptionsProvider.
     * @return the radioOptionsProvider
     */
    public RadioOptionsProvider getRadioOptionsProvider()
    {
        if (radioOptionsProvider == null)
        {
            String className = getConfigValue("radioOptionsProvider");
            if (StringUtils.isNotEmpty(className))
            {
                try
                {
                    radioOptionsProvider = Classes.newInstance(className);
                }
                catch (MgnlInstantiationException e)
                {
                    log.error("can't create radio options provider", e);
                }
                catch (ClassNotFoundException e)
                {
                    log.error("can't create radio options provider", e);
                }
            }
        }
        return radioOptionsProvider;
    }

    /**
     * Returns the radioOptions.
     * @return the radioOptions
     */
    public Map<String, String> getRadioOptions()
    {
        if (radioOptions == null)
        {
            radioOptions = getRadioOptionsProvider().getRadioOptions(getThisDialogControl());
        }
        return radioOptions;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getPath()
    {
        return "dialogs/radioGroup.ftl";
    }

    protected DialogControl getThisDialogControl()
    {
        return this;
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
        parameters.put("radioOptions", getRadioOptions());
    }

    public interface RadioOptionsProvider
    {

        public Map<String, String> getRadioOptions(DialogControl dialogControl);
    }
}
