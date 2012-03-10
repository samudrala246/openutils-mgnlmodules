/**
 *
 * Stripes module for Magnolia CMS (http://www.openmindlab.com/lab/products/mgnlstripes.html)
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

package it.openutils.magnoliastripes;

import info.magnolia.module.ModuleLifecycle;
import info.magnolia.module.ModuleLifecycleContext;


/**
 * @author fgiust
 * @version $Id$
 */
public class StripesModule implements ModuleLifecycle
{

    private static StripesModule instance;

    /**
     * Basename for Stripes paragraphs.
     */
    private String i18nbasename;

    public StripesModule()
    {
        instance = this;
    }

    public static StripesModule getInstance()
    {
        return instance;
    }

    /**
     * {@inheritDoc}
     */
    public void start(ModuleLifecycleContext moduleLifecycleContext)
    {
        // nothing to do
    }

    /**
     * {@inheritDoc}
     */
    public void stop(ModuleLifecycleContext moduleLifecycleContext)
    {
        // nothing to do
    }

    /**
     * Returns the i18nbasename.
     * @return the i18nbasename
     */
    public String getI18nbasename()
    {
        return i18nbasename;
    }

    /**
     * Sets the i18nbasename.
     * @param i18nbasename the i18nbasename to set
     */
    public void setI18nbasename(String i18nbasename)
    {
        this.i18nbasename = i18nbasename;
    }

}
