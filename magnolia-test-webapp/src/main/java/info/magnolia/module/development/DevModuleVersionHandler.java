package info.magnolia.module.development;

import info.magnolia.init.MagnoliaConfigurationProperties;
import info.magnolia.module.DefaultModuleVersionHandler;
import info.magnolia.module.InstallContext;
import info.magnolia.module.delta.SetPropertyTask;
import info.magnolia.module.delta.Task;
import info.magnolia.objectfactory.Components;
import info.magnolia.repository.RepositoryConstants;
import it.openutils.mgnltasks.NodeSortTask;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple version handler used to set up a few config options during development
 * @author fgiust
 * @version $Revision$ ($Author$)
 */
public class DevModuleVersionHandler extends DefaultModuleVersionHandler
{

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<Task> getStartupTasks(InstallContext installContext)
    {
        List<Task> tasks = new ArrayList<Task>();

        boolean develop = Components.getComponent(MagnoliaConfigurationProperties.class).getBooleanProperty(
            "magnolia.develop");

        if (develop)
        {
            tasks.add(new SetPropertyTask(
                "config",
                "/server/activation/subscribers/magnoliaPublic8080",
                "active",
                "false"));
        }

        // I hate spending time in looking through the unsorted list of modules...
        tasks.add(new NodeSortTask(RepositoryConstants.CONFIG, "/modules"));

       // tasks.add(new SetPropertyTask("config", "/server/1i8n/content", "enabled", "true"));

        return tasks;
    }
}
