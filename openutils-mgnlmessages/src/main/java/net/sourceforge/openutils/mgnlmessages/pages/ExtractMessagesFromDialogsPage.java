/**
 *
 * Messages Module for Magnolia CMS (http://www.openmindlab.com/lab/products/messages.html)
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

package net.sourceforge.openutils.mgnlmessages.pages;

import info.magnolia.cms.core.Content;
import info.magnolia.cms.core.HierarchyManager;
import info.magnolia.cms.core.ItemType;
import info.magnolia.cms.core.NodeData;
import info.magnolia.cms.core.search.Query;
import info.magnolia.cms.core.search.QueryManager;
import info.magnolia.cms.core.search.QueryResult;
import info.magnolia.cms.util.ContentUtil;
import info.magnolia.cms.util.NodeDataUtil;
import info.magnolia.context.MgnlContext;
import info.magnolia.module.admininterface.TemplatedMVCHandler;
import info.magnolia.repository.RepositoryConstants;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.jcr.RepositoryException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.openutils.mgnlmessages.lifecycle.MessagesModuleLifecycle;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author molaschi
 * @version $Id: $
 */
public class ExtractMessagesFromDialogsPage extends TemplatedMVCHandler
{

    private List<String> dialogsRoot;

    /**
     * Logger.
     */
    private Logger log = LoggerFactory.getLogger(ExtractMessagesFromDialogsPage.class);

    /**
     * @param name
     * @param request
     * @param response
     */
    public ExtractMessagesFromDialogsPage(String name, HttpServletRequest request, HttpServletResponse response)
    {
        super(name, request, response);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public String show()
    {
        dialogsRoot = new ArrayList<String>();
        QueryManager qm = MgnlContext.getQueryManager(RepositoryConstants.CONFIG);
        Query q;
        try
        {
            q = qm.createQuery("//dialogs", Query.XPATH);

            QueryResult qr = q.execute();
            Collection<Content> dialogs = qr.getContent();
            for (Content dialog : dialogs)
            {
                dialogsRoot.add(dialog.getHandle());
            }

            q = qm.createQuery("//paragraphs", Query.XPATH);

            qr = q.execute();
            dialogs = qr.getContent();
            for (Content dialog : dialogs)
            {
                dialogsRoot.add(dialog.getHandle());
            }
        }
        catch (RepositoryException e)
        {
            // ignore
        }

        return super.show();
    }

    /**
     * Extract messages keys
     * @return view
     */
    public String extract()
    {
        HierarchyManager hmConfig = MgnlContext.getHierarchyManager(RepositoryConstants.CONFIG);
        QueryManager qm = hmConfig.getQueryManager();
        HierarchyManager hm = MgnlContext.getHierarchyManager(MessagesModuleLifecycle.REPO);
        for (String dialogRoot : this.request.getParameterValues("dialogsRoots"))
        {
            try
            {
                doExtraction(dialogRoot, "label", qm, hm, hmConfig);
            }
            catch (RepositoryException e)
            {
                log.error("Error extracting labels from dialogs and paragraphs", e);
            }

            try
            {
                doExtraction(dialogRoot, "description", qm, hm, hmConfig);
            }
            catch (RepositoryException e)
            {
                log.error("Error extracting description from dialogs and paragraphs", e);
            }

            try
            {
                doExtraction(dialogRoot, "title", qm, hm, hmConfig);
            }
            catch (RepositoryException e)
            {
                log.error("Error extracting description from dialogs and paragraphs", e);
            }
        }

        return this.show();
    }

    @SuppressWarnings("unchecked")
    private void doExtraction(String root, String property, QueryManager qm, HierarchyManager hmMessages,
        HierarchyManager hmConfig) throws RepositoryException
    {
        Query q = qm.createQuery(root.substring(1) + "//*[@" + property + "]", Query.XPATH);

        QueryResult qr = q.execute();
        Collection<Content> labelParents = new ArrayList<Content>();
        labelParents.addAll(qr.getContent(ItemType.CONTENTNODE.getSystemName()));
        labelParents.addAll(qr.getContent(ItemType.CONTENT.getSystemName()));
        for (Content labelParent : labelParents)
        {
            NodeData labelNd = labelParent.getNodeData(property);
            String label = labelNd.getString();
            if (!StringUtils.isEmpty(label) && MgnlContext.getMessages().get(label).startsWith("???"))
            {
                String parentPath = labelParent.getHandle();
                parentPath = StringUtils.replace(parentPath, "/modules", "");
                Content parent = getOrCreateFullPath(hmMessages, parentPath);
                Content message = ContentUtil.getOrCreateContent(parent, property, ItemType.CONTENTNODE);
                NodeData nd = NodeDataUtil.getOrCreate(message, "en");
                nd.setValue(label);
                nd = NodeDataUtil.getOrCreate(message, "it");
                nd.setValue(label);

                String messageKey = message.getHandle().substring(1);
                messageKey = StringUtils.replace(messageKey, "/", ".");
                labelNd.setValue(messageKey);
            }
        }
        hmMessages.save();
        hmConfig.save();
    }

    private Content getOrCreateFullPath(HierarchyManager mgr, String path) throws RepositoryException
    {
        try
        {
            return mgr.getContent(path);
        }
        catch (RepositoryException ex)
        {
            String parent = StringUtils.substringBeforeLast(path, "/");
            String label = StringUtils.substringAfterLast(path, "/");
            if (!StringUtils.isEmpty(parent))
            {
                getOrCreateFullPath(mgr, parent);
            }
            else
            {
                parent = "/";
            }

            Content c = mgr.createContent(parent, label, ItemType.CONTENTNODE.getSystemName());
            mgr.save();
            return c;
        }
    }

    /**
     * Returns the dialogsRoot.
     * @return the dialogsRoot
     */
    public List<String> getDialogsRoot()
    {
        return dialogsRoot;
    }

    /**
     * Sets the dialogsRoot.
     * @param dialogsRoot the dialogsRoot to set
     */
    public void setDialogsRoot(List<String> dialogsRoot)
    {
        this.dialogsRoot = dialogsRoot;
    }

}
