/**
 *
 * Generic utilities for Magnolia CMS (http://www.openmindlab.com/lab/products/mgnlutils.html)
 * Copyright(C) 2009-2012, Openmind S.r.l. http://www.openmindonline.it
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

package it.openutils.mgnltasks;

import info.magnolia.cms.core.MgnlNodeType;
import info.magnolia.importexport.DataTransporter;
import info.magnolia.jcr.util.NodeUtil;
import info.magnolia.module.InstallContext;
import info.magnolia.repository.RepositoryConstants;
import it.openutils.mgnlutils.util.NodeUtilsExt;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Extends ModuleConfigBootstrapTask, optimizing the bootstrap by only loading changed files.
 * @author dschivo
 * @version $Id$
 */
public class DiffModuleConfigBootstrapTask extends ModuleConfigBootstrapTask
{

    /**
     * Logger.
     */
    private Logger log = LoggerFactory.getLogger(getClass());

    public DiffModuleConfigBootstrapTask(String modulename)
    {
        super(modulename);
    }

    public DiffModuleConfigBootstrapTask(String modulename, Set<String> includedRepositories)
    {
        super(modulename, includedRepositories);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean skipResource(InstallContext installContext, String name) throws RepositoryException
    {
        // export the current node in the repository to a temporary file; compare it with the new bootstrap; only if the
        // files are different import the new file (the previous node gets automatically deleted), else skip
        // it.
        boolean unchanged = bootstrapResourceEqualsExisting(installContext, name);
        if (unchanged)
        {
            log.debug("Skipping file {}", name);
        }
        return unchanged;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void deleteNode(InstallContext installContext, String nodePath) throws RepositoryException
    {
        Session hm = installContext.getJCRSession(RepositoryConstants.CONFIG);

        if (NodeUtilsExt.exists(hm, nodePath))
        {
            Node node = hm.getNode(nodePath);

            Set<String> lookup = new HashSet<String>();
            for (String name : resourcesToBootstrap)
            {
                String[] repositoryAndPath = getRepositoryAndPathFromBootstrapName(name);
                if (RepositoryConstants.CONFIG.equals(repositoryAndPath[0])
                    && StringUtils.startsWith(repositoryAndPath[1], nodePath))
                {
                    lookup.add(name);
                }
            }

            for (Node childNode : NodeUtil.getNodes(node, MgnlNodeType.NT_CONTENTNODE))
            {
                String fileName = childNode.getSession().getWorkspace().getName()
                    + childNode.getPath().replace("/", ".")
                    + ".xml";
                String resourceToBootstrap = "/mgnl-bootstrap/" + modulename + "/" + fileName;
                // delete only the templates not available anymore (just to handle the template removal/renaming, should
                // not be a common case)
                if (!lookup.contains(resourceToBootstrap))
                {
                    log.warn("Deleting node {}", childNode.getPath());
                    childNode.remove();
                }
            }
        }
    }

    private String[] getRepositoryAndPathFromBootstrapName(String resourceName)
    {
        // windows again
        resourceName = StringUtils.replace(resourceName, "\\", "/");

        String name = StringUtils.removeEnd(StringUtils.substringAfterLast(resourceName, "/"), ".xml");

        String repository = StringUtils.substringBefore(name, ".");
        String pathName = StringUtils.substringAfter(StringUtils.substringBeforeLast(name, "."), "."); //$NON-NLS-1$
        String nodeName = StringUtils.substringAfterLast(name, ".");
        String fullPath;
        if (StringUtils.isEmpty(pathName))
        {
            pathName = "/";
            fullPath = "/" + nodeName;
        }
        else
        {
            pathName = "/" + StringUtils.replace(pathName, ".", "/");
            fullPath = pathName + "/" + nodeName;
        }
        return new String[]{repository, fullPath };
    }

    private boolean bootstrapResourceEqualsExisting(InstallContext installContext, String name)
        throws RepositoryException
    {
        String[] repositoryAndPath = getRepositoryAndPathFromBootstrapName(name);
        String repository = repositoryAndPath[0];
        String path = repositoryAndPath[1];

        Node content = NodeUtilsExt.getNodeIfExists(installContext.getJCRSession(repository), path);
        if (content != null)
        {
            File file = null;
            try
            {
                String filenameWithoutExtension = StringUtils.removeEnd(
                    StringUtils.substringAfterLast(name, "/"),
                    ".xml");
                file = exportToTempFile(content, filenameWithoutExtension);
            }
            catch (Exception e)
            {
                log.warn("Cannot export content " + path + " from repository " + repository, e);
            }

            if (file != null)
            {
                try
                {
                    InputStream boostrapStream = getClass().getResourceAsStream(name);
                    InputStream existingStream = new FileInputStream(file);
                    return IOUtils.contentEquals(boostrapStream, existingStream);
                }
                catch (IOException e)
                {
                    log.warn("Cannot compare bootstrap resource " + name + " with the existing one", e);
                }
                finally
                {
                    file.delete();
                }
            }
        }
        return false;
    }

    private File exportToTempFile(Node content, String filenameWithoutExtension) throws IOException,
        FileNotFoundException, RepositoryException
    {
        File file = File.createTempFile(filenameWithoutExtension + '-', ".xml");
        {
            FileOutputStream out = new FileOutputStream(file);
            try
            {
                DataTransporter.executeExport(out, false, true, content.getSession(), content.getPath(), content
                    .getSession()
                    .getWorkspace()
                    .getName(), DataTransporter.XML);
            }
            finally
            {
                IOUtils.closeQuietly(out);
            }
        }
        return file;
    }

}