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

package net.sourceforge.openutils.mgnlmedia.media.commands;

import info.magnolia.cms.core.Content;
import info.magnolia.cms.core.MgnlNodeType;
import info.magnolia.cms.exchange.ExchangeException;
import info.magnolia.cms.i18n.MessagesManager;
import info.magnolia.cms.util.AlertUtil;
import info.magnolia.cms.util.ContentUtil;
import info.magnolia.cms.util.Rule;
import info.magnolia.context.Context;
import info.magnolia.jcr.util.MetaDataUtil;
import info.magnolia.jcr.util.NodeUtil;
import info.magnolia.module.admininterface.commands.ActivationCommand;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import net.sourceforge.openutils.mgnlmedia.media.configuration.MediaConfigurationManager;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Overrides default activation, and doesn't publish resolutions node
 * @author molaschi
 * @version $Id$
 */
@SuppressWarnings("deprecation")
// activation APIs need a cleanup in Magnolia, everrything is still based on Content
public class MediaActivationCommand extends ActivationCommand
{

    /**
     * Logger.
     */
    private Logger log = LoggerFactory.getLogger(MediaActivationCommand.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean execute(Context ctx)
    {
        try
        {
            Content thisState = getNode(ctx);
            String parentPath = StringUtils.substringBeforeLast(thisState.getHandle(), "/");
            if (StringUtils.isEmpty(parentPath))
            {
                parentPath = "/";
            }
            // make multiple activations instead of a big bulp
            if (super.isRecursive())
            {
                List versionMap = getVersionMap();
                if (versionMap == null)
                {
                    activateRecursive(parentPath, thisState, ctx);
                }
                else
                {
                    activateRecursive(ctx, versionMap);
                }
            }
            else
            {
                if (StringUtils.isNotEmpty(getVersion()))
                {
                    try
                    {
                        thisState = thisState.getVersionedContent(getVersion());
                    }
                    catch (RepositoryException re)
                    {
                        log.error("Failed to get version " + getVersion() + " for " + thisState.getHandle(), re);
                    }
                }
                activateRecursiveOneLevel(parentPath, thisState);
            }
        }
        catch (Exception e)
        {
            log.error("can't activate", e);
            AlertUtil.setException(MessagesManager.get("tree.error.activate"), e, ctx);
            return false;
        }
        log.info("exec successfully.");
        return true;

    }

    /**
     * Activate recursive one level
     * @param parentPath parent node
     * @param node node to activate
     * @throws RepositoryException exception
     * @throws ExchangeException exception exchanging node
     */
    public void activateRecursiveOneLevel(String parentPath, Content node) throws RepositoryException,
        ExchangeException
    {

        activateSingleNode(parentPath, node.getJCRNode());

        Iterator<Content> children = node.getChildren(new Content.ContentFilter()
        {

            public boolean accept(Content content)
            {
                try
                {
                    return !getRule().isAllowed(content.getNodeTypeName())
                        && !MgnlNodeType.NT_CONTENT.equals(content.getNodeTypeName())
                        && !MediaConfigurationManager.NT_RESOLUTIONS.equals(content.getNodeTypeName());
                }
                catch (RepositoryException e)
                {
                    log.error("can't get nodetype", e);
                    return false;
                }
            }
        }).iterator();

        while (children.hasNext())
        {
            activateRecursiveOneLevel(node.getHandle(), children.next());
        }
    }

    /**
     * @param parentPath
     * @param node
     * @throws RepositoryException
     * @throws ExchangeException
     */
    protected void activateSingleNode(String parentPath, Node node) throws RepositoryException, ExchangeException
    {
        if (MediaConfigurationManager.NT_MEDIA.equals(node.getPrimaryNodeType().getName())
            && MetaDataUtil.getMetaData(node).getIsActivated())
        {
            // already activated media, should deactivate in order to remove stale resolutions
            log
                .warn(
                    "Activating already active media {}, will deactivate existing node in order to remove stale resolutions",
                    NodeUtil.getPathIfPossible(node));

            getSyndicator().deactivate(info.magnolia.cms.util.ContentUtil.asContent(node));
        }

        checkFolderActivation(node);

        getSyndicator().activate(
            parentPath,
            info.magnolia.cms.util.ContentUtil.asContent(node),
            getOrderingInfo(info.magnolia.cms.util.ContentUtil.asContent(node)));
    }

    /**
     * @param node
     * @throws ExchangeException
     */
    private void checkFolderActivation(Node node) throws ExchangeException
    {
        try
        {
            List<Node> foldersInPath = new ArrayList<Node>();

            Node parent = node.getParent();
            while (parent.getDepth() != 0 && !MetaDataUtil.getMetaData(parent).getIsActivated())
            {
                foldersInPath.add(parent);
                parent = parent.getParent();
            }

            if (!foldersInPath.isEmpty())
            {
                Collections.reverse(foldersInPath);
                for (Node folder : foldersInPath)
                {
                    log.info("Activating parent folder {}", NodeUtil.getPathIfPossible(folder));
                    // folder only, no content
                    setRule(new Rule(new String[]{MgnlNodeType.NT_METADATA, MgnlNodeType.NT_RESOURCE }));
                    getSyndicator().activate(
                        NodeUtil.getPathIfPossible(folder.getParent()),
                        ContentUtil.asContent(folder),
                        getOrderingInfo(ContentUtil.asContent(folder)));
                }

            }
        }
        catch (RepositoryException e)
        {
            log.error(e.getMessage(), e);
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void activateRecursive(String parentPath, Content node, Context ctx) throws ExchangeException,
        RepositoryException
    {
        getSyndicator().activate(parentPath, node, getOrderingInfo(node));

        Iterator<Content> children = node.getChildren(new Content.ContentFilter()
        {

            public boolean accept(Content content)
            {
                try
                {
                    return !getRule().isAllowed(content.getNodeTypeName())
                        && !MediaConfigurationManager.NT_RESOLUTIONS.equals(content.getNodeTypeName());
                }
                catch (RepositoryException e)
                {
                    log.error("can't get nodetype", e);
                    return false;
                }
            }
        }).iterator();

        while (children.hasNext())
        {
            this.activateRecursive(node.getHandle(), (children.next()), ctx);
        }
    }
}
