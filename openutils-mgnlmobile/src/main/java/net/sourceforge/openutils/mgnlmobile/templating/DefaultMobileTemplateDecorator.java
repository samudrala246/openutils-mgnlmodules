/**
 *
 * Mobile Module for Magnolia CMS (http://www.openmindlab.com/lab/products/mgnlmobile.html)
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

package net.sourceforge.openutils.mgnlmobile.templating;

import java.util.HashMap;
import java.util.Map;

import net.sourceforge.openutils.mgnlmobile.filters.MobileFilter;

import org.apache.commons.collections.MapUtils;


/**
 * @author molaschi
 * @version $Id: $
 */
public class DefaultMobileTemplateDecorator extends BaseMobileTemplateDecorator
{

    Map<String, MobileTemplate> templates = new HashMap<String, MobileTemplate>();

    MobileTemplate defaultTemplate;

    /**
     * {@inheritDoc}
     */
    @Override
    protected Template getMobileTemplate()
    {
        if (MapUtils.isNotEmpty(templates))
        {
            for (MobileTemplate template : templates.values())
            {
                if (template.matchDevice(MobileFilter.getDevice()))
                {
                    return template;
                }
            }
        }
        return defaultTemplate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean hasMobileTemplates()
    {
        return defaultTemplate != null || MapUtils.isNotEmpty(templates);
    }

    /**
     * Returns the templates.
     * @return the templates
     */
    public Map<String, MobileTemplate> getTemplates()
    {
        return templates;
    }

    /**
     * Sets the templates.
     * @param templates the templates to set
     */
    public void setTemplates(Map<String, MobileTemplate> templates)
    {
        this.templates = templates;
    }

    /**
     * Sets the templates.
     * @param templates the templates to set
     */
    public void addTemplate(String name, MobileTemplate template)
    {
        this.templates.put(name, template);
    }

    /**
     * Returns the defaultTemplate.
     * @return the defaultTemplate
     */
    public MobileTemplate getDefaultTemplate()
    {
        return defaultTemplate;
    }

    /**
     * Sets the defaultTemplate.
     * @param defaultTemplate the defaultTemplate to set
     */
    public void setDefaultTemplate(MobileTemplate defaultTemplate)
    {
        this.defaultTemplate = defaultTemplate;
    }

}
