/**
 *
 * SimpleMedia Module for Magnolia CMS (http://www.openmindlab.com/lab/products/media.html)
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

package net.sourceforge.openutils.mgnlmedia.media.setup;

import info.magnolia.cms.core.SystemProperty;
import info.magnolia.module.InstallContext;
import info.magnolia.module.ModuleRegistry;
import info.magnolia.module.delta.SetupModuleRepositoriesTask;
import info.magnolia.module.delta.Task;
import info.magnolia.repository.RepositoryConstants;
import it.openutils.mgnltasks.BootstrapMissingNodesTask;
import it.openutils.mgnltasks.ChangeExistingPropertyTask;
import it.openutils.mgnltasks.CreateMissingPropertyTask;
import it.openutils.mgnltasks.NodeSortTask;
import it.openutils.mgnltasks.SamplesExtractionTask;
import it.openutils.mgnltasks.SimpleModuleVersionHandler;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.openutils.mgnlmedia.media.types.impl.ExternalVideoTypeHandler;


/**
 * Module version handler for media module
 * @author manuel
 * @version $Id
 */
public class MediaModuleVersionHandler extends SimpleModuleVersionHandler
{

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<Task> getBasicInstallTasks(InstallContext installContext)
    {
        List<Task> basicInstallTasks = super.getBasicInstallTasks(installContext);

        for (int j = 0; j < basicInstallTasks.size(); j++)
        {
            if (basicInstallTasks.get(j) instanceof SetupModuleRepositoriesTask)
            {
                // replace SetupModuleRepositoriesTask with SetupModuleRepositoriesWithoutSubscriberTask
                basicInstallTasks.remove(j);
                basicInstallTasks.add(j, new SetupModuleRepositoriesWithoutSubscriberTask());
                break;
            }

        }

        basicInstallTasks.add(new ConditionallySubscribeMediaRepositoriesTask());

        return basicInstallTasks;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<Task> getStartupTasks(InstallContext installContext)
    {
        List<Task> tasks = new ArrayList<Task>();

        if (SystemProperty.getBooleanProperty(SystemProperty.MAGNOLIA_BOOTSTRAP_SAMPLES))
        {
            tasks.add(new SamplesExtractionTask());
        }

        // this task is now disabled, it can now be run manually if needed
        // tasks.add(new MoveOriginalNodeTask());

        tasks.add(new CreateMissingPropertyTask(
            RepositoryConstants.CONFIG,
            "/modules/media/config",
            "singleinstance",
            Boolean.FALSE));

        tasks.add(new CreateMissingPropertyTask(
            RepositoryConstants.CONFIG,
            "/modules/media/config",
            "player",
            "jwplayer5"));

        tasks.add(new CreateMissingPropertyTask(
            RepositoryConstants.CONFIG,
            "/modules/media/config",
            "folderViewItemsPerPage",
            10L));

        // MEDIA-70 new enabled property for media type
        tasks.add(new CreateMissingPropertyTask(
            RepositoryConstants.CONFIG,
            "/modules/media/mediatypes/image",
            "enabled",
            Boolean.TRUE));
        tasks.add(new CreateMissingPropertyTask(
            RepositoryConstants.CONFIG,
            "/modules/media/mediatypes/video",
            "enabled",
            Boolean.TRUE));
        tasks.add(new CreateMissingPropertyTask(
            RepositoryConstants.CONFIG,
            "/modules/media/mediatypes/youtube",
            "enabled",
            Boolean.TRUE));
        tasks.add(new CreateMissingPropertyTask(
            RepositoryConstants.CONFIG,
            "/modules/media/mediatypes/audio",
            "enabled",
            Boolean.TRUE));

        // change "external video" handler
        tasks.add(new ChangeExistingPropertyTask(
            RepositoryConstants.CONFIG,
            "/modules/media/mediatypes/youtube/handler",
            "class",
            "net.sourceforge.openutils.mgnlmedia.media.types.impl.YouTubeVideoTypeHandler",
            ExternalVideoTypeHandler.class.getName()));

        // change "external video" icon
        tasks.add(new ChangeExistingPropertyTask(
            RepositoryConstants.CONFIG,
            "/modules/media/mediatypes/youtube",
            "menuIcon",
            "/.resources/media/icons/film.gif",
            "/.resources/media/icons/type-youtube.png"));

        // change "audio" icon
        tasks.add(new ChangeExistingPropertyTask(
            RepositoryConstants.CONFIG,
            "/modules/media/mediatypes/audio",
            "menuIcon",
            "/.resources/media/icons/audio.gif",
            "/.resources/media/icons/type-audio.png"));

        // change "video" icon
        tasks.add(new ChangeExistingPropertyTask(
            RepositoryConstants.CONFIG,
            "/modules/media/mediatypes/video",
            "menuIcon",
            "/.resources/media/icons/film.gif",
            "/.resources/media/icons/type-video.png"));

        // change "image" icon
        tasks.add(new ChangeExistingPropertyTask(
            RepositoryConstants.CONFIG,
            "/modules/media/mediatypes/image",
            "menuIcon",
            "/.resources/media/icons/image16.gif",
            "/.resources/media/icons/type-image.png"));

        // update configuration, changed for the introduction of content2bean
        tasks.add(new MoveHandlerNodedataToNode());

        // sort mediatypes on the "order" property
        tasks.add(new NodeSortTask("config", "/modules/media/mediatypes", "order"));

        tasks.add(new CreateMissingPropertyTask(
            RepositoryConstants.CONFIG,
            "/modules/media/mediatypes/youtube/handler",
            "parseremotefiles",
            Boolean.TRUE));

        tasks.add(new AddExtensionToType("jpeg", "image", "/modules/media/dialogs/mediaImageDlg/tabImage/original"));
        tasks.add(new AddExtensionToType("ico", "image", "/modules/media/dialogs/mediaImageDlg/tabImage/original"));

        tasks.add(new AddExtensionToType("mp4", "video", "/modules/media/dialogs/mediaVideoDlg/tabVideo/original"));
        tasks.add(new AddExtensionToType("mpv", "video", "/modules/media/dialogs/mediaVideoDlg/tabVideo/original"));
        tasks.add(new AddExtensionToType("ogv", "video", "/modules/media/dialogs/mediaVideoDlg/tabVideo/original"));

        tasks
            .add(new RemoveExtensionFromType("swf", "video", "/modules/media/dialogs/mediaVideoDlg/tabVideo/original"));

        // REVERT old stk support for magnolia < 4.3
        if (isModuleInstalled("standard-templating-kit"))
        {
            tasks.add(new BootstrapMissingNodesTask("media-stk"));

            // extended template renderer
            tasks.add(new ChangeExistingPropertyTask(
                RepositoryConstants.CONFIG,
                "/modules/standard-templating-kit/template-renderers/stk",
                "renderer",
                "net.sourceforge.openutils.mgnlmedia.freemarker.SktSimpleMediaTemplateRenderer",
                "info.magnolia.module.templatingkit.renderers.STKTemplateRenderer"));

            // extended paragraph renderer
            tasks.add(new ChangeExistingPropertyTask(
                RepositoryConstants.CONFIG,
                "/modules/standard-templating-kit/paragraph-renderers/stk",
                "class",
                "net.sourceforge.openutils.mgnlmedia.freemarker.SktSimpleMediaParagraphRenderer",
                "info.magnolia.module.templatingkit.renderers.STKParagraphRenderer"));
        }

        // install etk support
        if (isModuleInstalled("extended-templating-kit"))
        {
            tasks.add(new BootstrapMissingNodesTask("media-etk"));
        }

        // change "playlists" icon and description
        tasks.add(new ChangeExistingPropertyTask(
            RepositoryConstants.CONFIG,
            "/modules/adminInterface/config/menu/media/playlists",
            "icon",
            "/.resources/media/icons/ico16-playlist.png",
            "/.resources/media/icons/ico16-playlists.png"));
        tasks.add(new ChangeExistingPropertyTask(
            RepositoryConstants.CONFIG,
            "/modules/adminInterface/config/menu/media/playlists",
            "label",
            "Playlists",
            "media.menu.playlists"));

        tasks.add(new CreateMissingPropertyTask(
            RepositoryConstants.CONFIG,
            "/modules/media/config/search/filters/query",
            "wildcards",
            Boolean.TRUE));

        tasks.add(new ChangeExistingPropertyTask(
            RepositoryConstants.CONFIG,
            "/modules/media/mediausedin/website",
            "nodeType",
            "mgnl:content",
            "mgnl:page"));

        // empty placeholder
        tasks.add(new CreateMissingPropertyTask(RepositoryConstants.CONFIG, "/modules/media/config", "baseurl", ""));

        return tasks;
    }

    private boolean isModuleInstalled(String module)
    {
        try
        {
            return ModuleRegistry.Factory.getInstance().getDefinition(module) != null;
        }
        catch (IllegalArgumentException e)
        {
            log.debug("Module {} not installed ({} {})", new Object[]{module, e.getClass().getName(), e.getMessage() });
        }
        return false;
    }
}
