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
package net.sourceforge.openutils.mgnllms.module;

import info.magnolia.module.ModuleLifecycle;
import info.magnolia.module.ModuleLifecycleContext;
import info.magnolia.module.ModuleRegistry;
import net.sourceforge.openutils.mgnllms.managers.LearnerActivitiesManager;
import net.sourceforge.openutils.mgnllms.managers.LearnerManager;
import net.sourceforge.openutils.mgnllms.managers.ScormCMIManager;
import net.sourceforge.openutils.mgnllms.managers.impl.LearnerManagerWithMagnoliaUsers;
import net.sourceforge.openutils.mgnllms.managers.impl.MagnoliaLearnerActivitiesManager;
import net.sourceforge.openutils.mgnllms.managers.impl.MagnoliaScormCmiManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author molaschi
 * @version $Id: $
 */
public class LMSModule implements ModuleLifecycle
{

    /**
     * Logger.
     */
    private Logger log = LoggerFactory.getLogger(LMSModule.class);

    /**
     *
     */
    public static final String REPO = "lms";

    public static final String STATUS_NODEDATA = "status";

    public static final String USERS_NODEDATA = "users";

    public static final String JSON_KEY_ACTIVITIES_TM = "track";

    public static final String SATISFIED = "satisfied";

    public static final String DATAMODEL_NODEDATA = "data_model";

    public static final String ADLDATA_NODEDATA = "adl_data";

    public static final String ACTIVITIES_NODEDATA = "activities";

    public static final String SATISFACTION_RULE = "satisfactionRule";

    private boolean singleInstance;

    private LearnerManager learnerManager;

    private ScormCMIManager scormCMIManager;

    private LearnerActivitiesManager learnerActivitiesManager;

    public static LMSModule getInstance()
    {
        return ModuleRegistry.Factory.getInstance().getModuleInstance(LMSModule.class);
    }

    /**
     * {@inheritDoc}
     */
    public void start(ModuleLifecycleContext moduleLifecycleContext)
    {
        log.info("Starting module lms");
        moduleLifecycleContext.registerModuleObservingComponent("lms-config", LMSConfigurationModuleManager
            .getInstance());
        moduleLifecycleContext.registerModuleObservingComponent("lms-types", LmsTypesManager.getInstance());

        if (learnerManager == null)
        {
            learnerManager = new LearnerManagerWithMagnoliaUsers();
        }
        if (scormCMIManager == null)
        {
            scormCMIManager = new MagnoliaScormCmiManager();
        }
        if (learnerActivitiesManager == null)
        {
            learnerActivitiesManager = new MagnoliaLearnerActivitiesManager();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void stop(ModuleLifecycleContext moduleLifecycleContext)
    {

    }

    /**
     * Returns the learnerManager.
     * @return the learnerManager
     */
    public LearnerManager getLearnerManager()
    {
        return learnerManager;
    }

    /**
     * Sets the learnerManager.
     * @param learnerManager the learnerManager to set
     */
    public void setLearnerManager(LearnerManager learnerManager)
    {
        this.learnerManager = learnerManager;
    }

    /**
     * Returns the scormCMIManager.
     * @return the scormCMIManager
     */
    public ScormCMIManager getScormCMIManager()
    {
        return scormCMIManager;
    }

    /**
     * Sets the scormCMIManager.
     * @param scormCMIManager the scormCMIManager to set
     */
    public void setScormCMIManager(ScormCMIManager scormCMIManager)
    {
        this.scormCMIManager = scormCMIManager;
    }

    /**
     * Returns the learnerActivitiesManager.
     * @return the learnerActivitiesManager
     */
    public LearnerActivitiesManager getLearnerActivitiesManager()
    {
        return learnerActivitiesManager;
    }

    /**
     * Sets the learnerActivitiesManager.
     * @param learnerActivitiesManager the learnerActivitiesManager to set
     */
    public void setLearnerActivitiesManager(LearnerActivitiesManager learnerActivitiesManager)
    {
        this.learnerActivitiesManager = learnerActivitiesManager;
    }

    /**
     * Returns the singleInstance.
     * @return the singleInstance
     */
    public boolean isSingleInstance()
    {
        return singleInstance;
    }

    /**
     * Sets the singleInstance.
     * @param singleInstance the singleInstance to set
     */
    public void setSingleInstance(boolean singleInstance)
    {
        this.singleInstance = singleInstance;
    }

}
