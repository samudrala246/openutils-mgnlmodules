/**
 *
 * Generic utilities for Magnolia CMS (http://www.openmindlab.com/lab/products/mgnlutils.html)
 * Copyright(C) 2009-2011, Openmind S.r.l. http://www.openmindonline.it
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

package it.openutils.mgnlutils.test;

import info.magnolia.module.ModuleLifecycle;
import info.magnolia.module.ModuleLifecycleContext;


/**
 * @author molaschi
 * @version $Id: $
 */
public class TestModule implements ModuleLifecycle
{

    private String testConfigValue;

    private boolean started = false;

    /**
     * {@inheritDoc}
     */
    public void start(ModuleLifecycleContext moduleLifecycleContext)
    {
        started = true;
    }

    /**
     * {@inheritDoc}
     */
    public void stop(ModuleLifecycleContext moduleLifecycleContext)
    {
        started = false;
    }

    public void setTestConfigValue(String testConfigValue)
    {
        this.testConfigValue = testConfigValue;
    }

    public String getTestConfigValue()
    {
        return testConfigValue;
    }

    public void setStarted(boolean started)
    {
        this.started = started;
    }

    public boolean isStarted()
    {
        return started;
    }

}
