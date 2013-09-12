/**
 *
 * Tasks for for Magnolia CMS (http://www.openmindlab.com/lab/products/mgnltasks.html)
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

package it.openutils.mgnltasks.dev;

import info.magnolia.cms.beans.config.ContentRepository;
import info.magnolia.cms.core.Content;
import info.magnolia.cms.core.HierarchyManager;
import info.magnolia.cms.core.ItemType;
import info.magnolia.cms.core.MgnlNodeType;
import info.magnolia.cms.core.Path;
import info.magnolia.cms.security.AccessDeniedException;
import info.magnolia.cms.util.AlertUtil;
import info.magnolia.cms.util.ContentUtil;
import info.magnolia.context.MgnlContext;
import info.magnolia.importexport.DataTransporter;
import info.magnolia.jcr.util.NodeUtil;
import info.magnolia.module.InstallContext;
import info.magnolia.module.ModuleRegistry;
import info.magnolia.module.admininterface.TemplatedMVCHandler;
import info.magnolia.module.delta.AbstractRepositoryTask;
import info.magnolia.module.delta.TaskExecutionException;
import info.magnolia.repository.RepositoryConstants;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * <p>
 * Similar to info.magnolia.module.admininterface.pages.DevelopmentUtilsPage, but tweaked to export all the resources to
 * separate bootstrap folder like the ones used by SimpleModuleVersionHandler.
 * </p>
 * <p>
 * Configure it by adding to your module version handler:
 * </p>
 * 
 * <pre>
 * 
 *  tasks.add(new ModuleDevelopmentUtilsPage.AddDevPageToMenuTask("modulename",
 *              "../../../../modulename-magnolia/src/main/resources/mgnl-bootstrap/modulename"));
 *              
 * </pre>
 * @author fgiust
 * @version $Id$
 */
public class ModuleDevelopmentUtilsPage extends TemplatedMVCHandler
{

    private boolean templates;

    private boolean paragraphs;

    private boolean dialogs;

    private boolean pages;

    private boolean website;

    private boolean media;

    private boolean data;

    private boolean users;

    private boolean groups;

    private boolean roles;

    private boolean virtualURIs;

    private String rootdir;

    private String parentpath;

    private String repository;

    private String module;

    public static Logger log = LoggerFactory.getLogger(ModuleDevelopmentUtilsPage.class);

    public ModuleDevelopmentUtilsPage(String name, HttpServletRequest request, HttpServletResponse response)
    {
        super(name, request, response);
    }

    public boolean isTemplates()
    {
        return this.templates;
    }

    public boolean isParagraphs()
    {
        return this.paragraphs;
    }

    public boolean isDialogs()
    {
        return this.dialogs;
    }

    public boolean isPages()
    {
        return this.pages;
    }

    public void setPages(boolean pages)
    {
        this.pages = pages;
    }

    public boolean isWebsite()
    {
        return this.website;
    }

    public boolean isMedia()
    {
        return media;
    }

    public void setMedia(boolean media)
    {
        this.media = media;
    }

    public boolean isData()
    {
        return data;
    }

    public void setData(boolean data)
    {
        this.data = data;
    }

    public boolean isUsers()
    {
        return this.users;
    }

    public boolean isGroups()
    {
        return this.groups;
    }

    public boolean isRoles()
    {
        return this.roles;
    }

    public String getRootdir()
    {
        return this.rootdir;
    }

    public String getParentpath()
    {
        return this.parentpath;
    }

    public String getRepository()
    {
        return this.repository;
    }

    public void setDialogs(boolean dialogs)
    {
        this.dialogs = dialogs;
    }

    public void setParagraphs(boolean paragraphs)
    {
        this.paragraphs = paragraphs;
    }

    public void setTemplates(boolean templates)
    {
        this.templates = templates;
    }

    public void setRootdir(String rootdir)
    {
        this.rootdir = rootdir;
    }

    public void setWebsite(boolean website)
    {
        this.website = website;
    }

    public void setParentpath(String parentpath)
    {
        this.parentpath = parentpath;
    }

    public void setGroups(boolean groups)
    {
        this.groups = groups;
    }

    public void setRoles(boolean roles)
    {
        this.roles = roles;
    }

    public void setUsers(boolean users)
    {
        this.users = users;
    }

    public String getModule()
    {
        return this.module;
    }

    public void setModule(String module)
    {
        this.module = module;
    }

    public void setRepository(String repository)
    {
        this.repository = repository;
    }

    public boolean isVirtualURIs()
    {
        return this.virtualURIs;
    }

    public void setVirtualURIs(boolean virtualURIs)
    {
        this.virtualURIs = virtualURIs;
    }

    @SuppressWarnings("unchecked")
    public Iterator<String> getRepositories()
    {
        return ContentRepository.getAllRepositoryNames();
    }

    public Set<String> getModules()
    {
        return ModuleRegistry.Factory.getInstance().getModuleNames();
    }

    public String backup()
    {
        HierarchyManager hm = MgnlContext.getHierarchyManager(RepositoryConstants.CONFIG);
        Session session = hm.getWorkspace().getSession();

        try
        {
            Content moduleroot = hm.getContent("/modules/" + module);
            if (templates)
            {
                exportChildren(RepositoryConstants.CONFIG, session, moduleroot, "templates", new ItemType[]{
                    ItemType.CONTENT,
                    ItemType.CONTENTNODE }, false);
            }
            if (paragraphs)
            {
                exportChildren(RepositoryConstants.CONFIG, session, moduleroot, "paragraphs", new ItemType[]{
                    ItemType.CONTENT,
                    ItemType.CONTENTNODE }, false);
            }
            if (pages)
            {
                exportChildren(RepositoryConstants.CONFIG, session, moduleroot, "pages", new ItemType[]{
                    ItemType.CONTENT,
                    ItemType.CONTENTNODE }, false);
            }
            if (dialogs)
            {
                exportChildren(RepositoryConstants.CONFIG, session, moduleroot, "dialogs", new ItemType[]{
                    ItemType.CONTENT,
                    ItemType.CONTENTNODE }, true);
            }
            if (virtualURIs)
            {
                exportChildren(
                    RepositoryConstants.CONFIG,
                    session,
                    moduleroot,
                    "virtualURIMapping",
                    new ItemType[]{ItemType.CONTENTNODE },
                    true);
            }
            AlertUtil.setMessage("Backup done to "
                + new File(Path.getAbsoluteFileSystemPath(rootdir)).getCanonicalPath());
        }
        catch (Exception e)
        {
            log.error(e.getMessage(), e);
            AlertUtil.setMessage("Error while processing module " + module, e);
        }

        if (website)
        {
            extractWorkspaceRoots(RepositoryConstants.WEBSITE);
        }

        if (media && ContentRepository.getRepositoryMapping("media") != null)
        {
            extractWorkspaceRoots("media");
        }

        if (data && ContentRepository.getRepositoryMapping("data") != null)
        {
            extractWorkspaceRoots("data");
        }

        if (users)
        {
            backupChildren(RepositoryConstants.USERS, "/admin");
        }

        if (groups)
        {
            extractWorkspaceRoots(RepositoryConstants.USER_GROUPS);
        }

        if (roles)
        {
            extractWorkspaceRoots(RepositoryConstants.USER_ROLES);
        }

        return this.show();
    }

    private void extractWorkspaceRoots(String repositoryName)
    {
        try
        {
            HierarchyManager hm = MgnlContext.getHierarchyManager(repositoryName);
            Content wesiteRoot = hm.getRoot();

            Iterator<Content> children = wesiteRoot.getChildren(ContentUtil.MAGNOLIA_FILTER).iterator();
            while (children.hasNext())
            {
                Content exported = children.next();
                exportNode(repositoryName, hm.getWorkspace().getSession(), exported, true);
            }
        }
        catch (Exception e)
        {
            log.error(e.getMessage(), e);
            AlertUtil.setMessage("Error while processing " + repositoryName + " repository", e);
        }
    }

    private void backupChildren(String repository, String parentpath)
    {
        HierarchyManager hm = MgnlContext.getHierarchyManager(repository);

        Content parentNode = null;
        try
        {
            parentNode = hm.getContent(parentpath);
        }
        catch (RepositoryException e)
        {
            // ignore
            return;
        }
        try
        {
            Iterator<Content> children = parentNode
                .getChildren(ContentUtil.ALL_NODES_EXCEPT_JCR_CONTENT_FILTER)
                .iterator();
            while (children.hasNext())
            {
                Content exported = children.next();
                exportNode(repository, hm.getWorkspace().getSession(), exported, false);
            }

        }
        catch (Exception e)
        {
            log.error(e.getMessage(), e);
            AlertUtil.setMessage("Error while processing actions", e);
        }

    }

    private void exportChildren(String repository, Session session, Content moduleroot, String path,
        ItemType[] itemTypes, boolean exportContentContainingContentNodes) throws PathNotFoundException,
        RepositoryException, AccessDeniedException, FileNotFoundException, IOException
    {
        Content templateRoot = null;
        try
        {
            templateRoot = moduleroot.getContent(path);
        }
        catch (PathNotFoundException e)
        {
            // ignore
            return;
        }

        // we need to track exported paths, or it will export any single control for dialogs
        Set<String> alreadyExported = new HashSet<String>();

        Iterator<Content> children = ContentUtil.collectAllChildren(templateRoot, itemTypes).iterator();
        while (children.hasNext())
        {
            Content exported = children.next();
            if (!exported.getNodeDataCollection().isEmpty() // ignore "directories"
                || (exportContentContainingContentNodes && exported.hasChildren(ItemType.CONTENTNODE.getSystemName())))
            {

                String current = exported.getHandle();
                boolean dontexport = false;

                for (Iterator<String> iterator = alreadyExported.iterator(); iterator.hasNext();)
                {
                    String already = iterator.next();
                    if (current.startsWith(already))
                    {
                        dontexport = true;
                        break;
                    }
                }

                if (!dontexport)
                {
                    alreadyExported.add(exported.getHandle() + "/");
                    exportNode(repository, session, exported, false);
                }
            }
        }
    }

    private void exportNode(String repository, Session session, Content exported, boolean dev)
        throws FileNotFoundException, IOException
    {
        String handle = exported.getHandle();
        String xmlName = repository + StringUtils.replace(handle, "/", ".") + ".xml";
        xmlName = DataTransporter.encodePath(xmlName, ".", "UTF-8");
        // create necessary parent directories
        File folder = new File(Path.getAbsoluteFileSystemPath(dev && !StringUtils.contains(rootdir, "-dev") ? rootdir
            + "-dev" : rootdir));
        folder.mkdirs();
        File xmlFile = new File(folder.getAbsoluteFile(), xmlName);
        FileOutputStream fos = new FileOutputStream(xmlFile);

        try
        {
            DataTransporter.executeExport(fos, false, true, session, handle, repository, DataTransporter.XML);
        }
        finally
        {
            IOUtils.closeQuietly(fos);
        }
    }

    public static class AddDevPageToMenuTask extends AbstractRepositoryTask
    {

        private final String module2;

        private final String bootstrapRootDir;

        public AddDevPageToMenuTask(String module, String bootstrapRootDir)
        {
            super("Dev page for module " + module, "");
            module2 = module;
            this.bootstrapRootDir = bootstrapRootDir;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void doExecute(InstallContext installContext) throws RepositoryException, TaskExecutionException
        {

            Session session = installContext.getJCRSession(RepositoryConstants.CONFIG);

            String pagename = "development-" + module2;

            Node pages = NodeUtil.createPath(
                session.getRootNode(),
                "modules/" + module2 + "/pages",
                MgnlNodeType.NT_CONTENT);

            if (!pages.hasNode(pagename))
            {
                Node page = pages.addNode(pagename, MgnlNodeType.NT_CONTENTNODE);
                page.setProperty("class", ModuleDevelopmentUtilsPage.class.getName());
            }

            Node menu = session.getNode("/modules/adminInterface/config/menu/tools");

            if (!menu.hasNode(pagename))
            {
                Node page = menu.addNode(pagename, MgnlNodeType.NT_CONTENTNODE);
                page.setProperty("icon", "/.resources/tasks/ico16-save.png");
                page.setProperty("label", "Tools * " + module2);
                page.setProperty("onclick", "MgnlAdminCentral.showContent('/.magnolia/pages/"
                    + pagename
                    + ".html?module="
                    + module2
                    + "&rootdir="
                    + bootstrapRootDir
                    + "');");
            }

        }

    }
}
