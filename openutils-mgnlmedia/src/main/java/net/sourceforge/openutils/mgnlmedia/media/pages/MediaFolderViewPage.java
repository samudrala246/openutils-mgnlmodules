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

package net.sourceforge.openutils.mgnlmedia.media.pages;

import info.magnolia.cms.core.Content;
import info.magnolia.cms.core.HierarchyManager;
import info.magnolia.cms.core.ItemType;
import info.magnolia.cms.core.Path;
import info.magnolia.cms.exchange.ActivationManagerFactory;
import info.magnolia.cms.exchange.ExchangeException;
import info.magnolia.cms.security.Permission;
import info.magnolia.cms.util.AlertUtil;
import info.magnolia.commands.CommandsManager;
import info.magnolia.context.Context;
import info.magnolia.context.MgnlContext;
import info.magnolia.init.MagnoliaConfigurationProperties;
import info.magnolia.jcr.util.MetaDataUtil;
import info.magnolia.jcr.util.NodeUtil;
import info.magnolia.module.admininterface.commands.ActivationCommand;
import info.magnolia.objectfactory.Components;
import it.openutils.mgnlutils.api.NodeUtilsExt;
import it.openutils.mgnlutils.el.MgnlPagingElFunctions;
import it.openutils.mgnlutils.el.MgnlPagingElFunctions.Page;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.openutils.mgnlcriteria.jcr.query.AdvancedResult;
import net.sourceforge.openutils.mgnlmedia.media.configuration.MediaConfigurationManager;
import net.sourceforge.openutils.mgnlmedia.media.configuration.MediaTypeConfiguration;
import net.sourceforge.openutils.mgnlmedia.media.lifecycle.MediaModule;
import net.sourceforge.openutils.mgnlmedia.media.tags.el.MediaEl;

import org.apache.commons.chain.Command;
import org.apache.commons.lang.StringUtils;

import com.google.common.collect.Iterators;


/**
 * Page that renders the folder view.
 * @author molaschi
 * @version $Id$
 */
public class MediaFolderViewPage extends MessagesTemplatedMVCHandler
{

    protected String path;

    protected String node;

    protected String dest;

    protected String type;

    protected String bgSelector;

    protected String pagesizeSelector;

    protected String sorting;

    protected boolean selectMedia;

    protected String actMediaHandle;

    protected String mediaType;

    protected Iterator<MediaBean> medias;

    protected Collection<MediaTypeConfiguration> types;

    protected Map<String, Integer> numberOfMedia;

    protected boolean writable;

    protected boolean canPublish;

    protected boolean develop;

    protected String search;

    protected List<Page> pages;

    protected int page = 1;

    protected AdvancedResult searchResult;

    /**
     * @param name
     * @param request
     * @param response
     */
    public MediaFolderViewPage(String name, HttpServletRequest request, HttpServletResponse response)
    {
        super(name, request, response);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void init()
    {
        super.init();

        // left side of navigation bar: media type selectors
        if (!StringUtils.isEmpty(mediaType))
        {
            // subset of types specified by selectMedia control
            String[] mediaTypes = StringUtils.split(mediaType, ",");
            type = mediaTypes[0];
            types = new ArrayList<MediaTypeConfiguration>();
            for (String mt : mediaTypes)
            {
                types.add(MediaConfigurationManager.getInstance().getTypes().get(mt));
            }
        }
        else
        {
            // if (StringUtils.isEmpty(type))
            // {
            // type = MediaConfigurationManager.getInstance().getTypes().keySet().iterator().next();
            // }
            types = MediaConfigurationManager.getInstance().getTypes().values();
        }

        // right side of navigation bar: sorting and background selectors
        bgSelector = null;
        pagesizeSelector = null;
        sorting = null;
        for (Cookie cookie : request.getCookies())
        {
            if ("bgselector".equals(cookie.getName()))
            {
                bgSelector = cookie.getValue();
            }
            else if ("pagesizeselector".equals(cookie.getName()))
            {
                pagesizeSelector = cookie.getValue();
            }
            else if ("sorting".equals(cookie.getName()))
            {
                sorting = cookie.getValue();
            }
        }
        bgSelector = StringUtils.defaultIfEmpty(bgSelector, "white");
        pagesizeSelector = StringUtils.defaultIfEmpty(pagesizeSelector, "size1");

        try
        {
            if (sorting == null || SortMode.valueOf(sorting) == null)
            {
                sorting = getDefaultSorting().name();
            }
        }
        catch (IllegalArgumentException e)
        {
            sorting = getDefaultSorting().name();
        }
        // for activation status handling
        develop = Components.getComponent(MagnoliaConfigurationProperties.class).getBooleanProperty("magnolia.develop");
    }

    /**
     * Counts media items for the specified type.
     * @param type
     * @return
     */
    protected int countMediaItems(MediaTypeConfiguration type)
    {
        if (StringUtils.isEmpty(path) && StringUtils.isBlank(search))
        {
            return 0;
        }
        // sorting must be specified for total-size to work (-1 otherwise)
        AdvancedResult typeResult = MediaEl
            .module()
            .getSearch()
            .search(request, type.getName(), path, false, getDefaultSorting(), 0, 1);
        return typeResult.getTotalSize();
    }

    /**
     * Finds media items to show.
     * @return
     */
    protected AdvancedResult findMediaItems()
    {
        if (StringUtils.isEmpty(path))
        {
            return null;
        }
        Node folder = NodeUtilsExt.getNodeByIdOrPath(MediaModule.REPO, path);
        if (folder == null)
        {
            return null;
        }

        writable = NodeUtil.isGranted(folder, Permission.SET);
        canPublish = writable && ActivationManagerFactory.getActivationManager().hasAnyActiveSubscriber();
        MediaModule module = MediaEl.module();
        Integer itemsPerPage = module.getFolderViewPageSizes().get(pagesizeSelector);
        if (itemsPerPage == null)
        {
            itemsPerPage = module.getFolderViewItemsPerPage();
        }

        AdvancedResult result = module.getSearch().search(
            request,
            type,
            path,
            false,
            SortMode.valueOf(sorting),
            itemsPerPage,
            page);

        // back to page 1 if an invalid page number is selected
        if (result.getTotalSize() > 0 && result.getNumberOfPages() < page)
        {
            page = 1;
            result = module.getSearch().search(
                request,
                type,
                path,
                false,
                SortMode.valueOf(sorting),
                itemsPerPage,
                page);
        }

        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String show()
    {
        // counts media items group by type
        numberOfMedia = new HashMap<String, Integer>();
        int total = 0;
        for (MediaTypeConfiguration type : types)
        {
            int countMediaItems = countMediaItems(type);
            numberOfMedia.put(type.getName(), countMediaItems);
            total += countMediaItems;
        }
        numberOfMedia.put("", total);

        searchResult = findMediaItems();
        if (searchResult != null)
        {

            // casts Iterator<AdvancedResultItem> to Iterator<Content>
            Iterator<Node> contentIterator = Iterators.filter(searchResult.getItems(), Node.class);
            medias = Iterators.transform(contentIterator, new MediaBeanBuilder());
            pages = MgnlPagingElFunctions.pageList(searchResult.getNumberOfPages(), 10, "page");
        }
        if (medias == null)
        {
            medias = Iterators.emptyIterator();
        }
        if (pages == null)
        {
            pages = Collections.emptyList();
        }

        return super.show();
    }

    /**
     * delete a node
     * @return view
     */
    public String delete()
    {
        HierarchyManager hm = MgnlContext.getInstance().getHierarchyManager(MediaModule.REPO);

        try
        {
            hm.delete(this.node);
            hm.save();
        }
        catch (RepositoryException ex)
        {
            log.error("Exception deleting node {} from repository media", node, ex);
        }

        return show();
    }

    /**
     * moves a node
     * @return view
     */
    public String move()
    {
        String nodeName = StringUtils.substringAfterLast(node, "/");
        String destinationNode = dest + "/" + nodeName;
        try
        {
            copyMoveNode(node, destinationNode, true);
        }
        catch (ExchangeException e)
        {
            log.error("Exception deactivating node", e);
            AlertUtil.setMessage("Problem during deactivation");
        }
        catch (RepositoryException e)
        {
            log.error("Exception moving node", e);
            AlertUtil.setMessage("Problem during moving");
        }
        return show();
    }

    /**
     * copy a node
     * @return view
     */
    public String copy()
    {
        String nodeName = StringUtils.substringAfterLast(node, "/");
        String destinationNode = dest + "/" + nodeName;
        try
        {
            copyMoveNode(node, destinationNode, false);
        }
        catch (ExchangeException e)
        {
            log.error("Exception deactivating node", e);
            AlertUtil.setMessage("Problem during deactivation");
        }
        catch (RepositoryException e)
        {
            log.error("Exception copying node", e);
            AlertUtil.setMessage("Problem during copying");
        }
        return show();
    }

    /**
     * Copy or move a node (from AdminTreeMVCHandler)
     * @param source source node
     * @param destination destination folder
     * @param move move or copy?
     * @return new content
     * @throws ExchangeException publication problem
     * @throws RepositoryException repository exception
     */
    public Node copyMoveNode(String source, String destination, boolean move) throws ExchangeException,
        RepositoryException
    {
        HierarchyManager hm = MgnlContext.getHierarchyManager(MediaModule.REPO);

        String goTo = destination;

        if (hm.isExist(destination))
        {
            String parentPath = StringUtils.substringBeforeLast(destination, "/"); //$NON-NLS-1$
            String label = StringUtils.substringAfterLast(destination, "/"); //$NON-NLS-1$
            label = Path.getUniqueLabel(hm, parentPath, label);
            goTo = parentPath + "/" + label; //$NON-NLS-1$
        }
        if (move)
        {
            if (destination.indexOf(source + "/") == 0)
            {
                // move source into destinatin not possible
                return null;
            }
            // TODO verify if it works anyway
            // this.deactivateNode(source);
            try
            {
                hm.moveTo(source, goTo);
            }
            catch (Exception e)
            {
                // try to move below node data
                return null;
            }
        }
        else
        {
            // copy
            hm.copyTo(source, goTo);
        }
        Content newContent = hm.getContent(destination);
        try
        {
            MetaDataUtil.updateMetaData(newContent.getJCRNode());
            MetaDataUtil.getMetaData(newContent.getJCRNode()).setUnActivated();
        }
        catch (Exception e)
        {
            if (log.isDebugEnabled())
            {
                log.debug("Exception caught: " + e.getMessage(), e); //$NON-NLS-1$
            }
        }
        newContent.save();
        return newContent.getJCRNode();
    }

    /**
     * Activates single media node
     * @return view
     */
    @SuppressWarnings("unchecked")
    public String activate()
    {
        Command cmd = CommandsManager.getInstance().getCommand("media", "activate");

        ActivationCommand actCmd = (ActivationCommand) cmd;

        StringBuffer sb = new StringBuffer();
        sb.append(MediaConfigurationManager.MEDIA.getSystemName());
        sb.append(",");
        sb.append(ItemType.CONTENTNODE.getSystemName());
        actCmd.setItemTypes(sb.toString());

        Context context = MgnlContext.getInstance();
        context.put(Context.ATTRIBUTE_REPOSITORY, MediaModule.REPO);
        context.put(Context.ATTRIBUTE_PATH, this.node);
        context.put(Context.ATTRIBUTE_RECURSIVE, false);

        try
        {
            cmd.execute(context);
        }
        catch (Exception e)
        {
            AlertUtil.setMessage("Activation failed: " + e.getMessage());

            log.error("Exception activating media", e);
        }

        return this.show();
    }

    /**
     * Deactivates single media node
     * @return view
     */
    @SuppressWarnings("unchecked")
    public String deactivate()
    {
        Command cmd = CommandsManager.getInstance().getCommand("media", "deactivate");

        Context context = MgnlContext.getInstance();
        context.put(Context.ATTRIBUTE_REPOSITORY, MediaModule.REPO);
        context.put(Context.ATTRIBUTE_PATH, this.node);

        try
        {
            cmd.execute(context);
        }
        catch (Exception e)
        {
            AlertUtil.setMessage("Deactivation failed: " + e.getMessage());

            log.error("Exception activating media", e);
        }

        return this.show();
    }

    /**
     * Download media
     * @return nothing, but attach downloadable file to response
     */
    public String download()
    {

        Node media = NodeUtilsExt.getNodeByIdOrPath(MediaModule.REPO, path);

        if (media == null)
        {
            return null;
        }
        MediaTypeConfiguration mtc = MediaConfigurationManager.getInstance().getMediaTypeConfigurationFromMedia(media);
        String url = mtc.getHandler().getUrl(media);

        String filename = mtc.getHandler().getFullFilename(media);

        this.response.addHeader("Content-Disposition", "attachment; filename=" + filename);
        try
        {
            this.request.getRequestDispatcher(url).forward(request, response);
        }
        catch (ServletException e)
        {
            log.error("Error downloading media " + path, e);
        }
        catch (IOException e)
        {
            log.error("Error downloading media " + path, e);
        }

        return null;
    }

    /**
     * Returns the path.
     * @return the path
     */
    public String getPath()
    {
        return path;
    }

    /**
     * Returns the medias.
     * @return the medias
     */
    public Iterator<MediaBean> getMedias()
    {
        return medias;
    }

    /**
     * Sets the path.
     * @param path the path to set
     */
    public void setPath(String path)
    {
        this.path = path;
    }

    /**
     * Returns the types.
     * @return the types
     */
    public Collection<MediaTypeConfiguration> getTypes()
    {
        return types;
    }

    /**
     * Sets the types.
     * @param types the types to set
     */
    public void setTypes(Collection<MediaTypeConfiguration> types)
    {
        this.types = types;
    }

    /**
     * Returns the type.
     * @return the type
     */
    public String getType()
    {
        return type;
    }

    /**
     * Sets the type.
     * @param type the type to set
     */
    public void setType(String type)
    {
        this.type = type;
    }

    /**
     * Returns the bgSelector.
     * @return the bgSelector
     */
    public String getBgSelector()
    {
        return bgSelector;
    }

    /**
     * Returns the pagesizeSelector.
     * @return the pagesizeSelector
     */
    public String getPagesizeSelector()
    {
        return pagesizeSelector;
    }

    /**
     * Returns the sorting.
     * @return the sorting
     */
    public String getSorting()
    {
        return sorting;
    }

    /**
     * Returns the writable.
     * @return the writable
     */
    public boolean isWritable()
    {
        return writable;
    }

    /**
     * Sets the writable.
     * @param writable the writable to set
     */
    public void setWritable(boolean writable)
    {
        this.writable = writable;
    }

    /**
     * Returns the canPublish.
     * @return the canPublish
     */
    public boolean isCanPublish()
    {
        return canPublish;
    }

    /**
     * Sets the canPublish.
     * @param canPublish the canPublish to set
     */
    public void setCanPublish(boolean canPublish)
    {
        this.canPublish = canPublish;
    }

    /**
     * Returns the node.
     * @return the node
     */
    public String getNode()
    {
        return node;
    }

    /**
     * Sets the node.
     * @param node the node to set
     */
    public void setNode(String node)
    {
        this.node = node;
    }

    /**
     * Returns the dest.
     * @return the dest
     */
    public String getDest()
    {
        return dest;
    }

    /**
     * Sets the dest.
     * @param dest the dest to set
     */
    public void setDest(String dest)
    {
        this.dest = dest;
    }

    /**
     * Returns the selectMedia.
     * @return the selectMedia
     */
    public boolean isSelectMedia()
    {
        return selectMedia;
    }

    /**
     * Sets the selectMedia.
     * @param selectMedia the selectMedia to set
     */
    public void setSelectMedia(boolean selectMedia)
    {
        this.selectMedia = selectMedia;
    }

    /**
     * Returns the actMediaHandle.
     * @return the actMediaHandle
     */
    public String getActMediaHandle()
    {
        return actMediaHandle;
    }

    /**
     * Sets the actMediaHandle.
     * @param actMediaHandle the actMediaHandle to set
     */
    public void setActMediaHandle(String actMediaHandle)
    {
        this.actMediaHandle = actMediaHandle;
    }

    /**
     * Returns the mediaType.
     * @return the mediaType
     */
    public String getMediaType()
    {
        return mediaType;
    }

    /**
     * Sets the mediaType.
     * @param mediaType the mediaType to set
     */
    public void setMediaType(String mediaType)
    {
        this.mediaType = mediaType;
    }

    /**
     * Returns the numberOfMedia.
     * @return the numberOfMedia
     */
    public Map<String, Integer> getNumberOfMedia()
    {
        return numberOfMedia;
    }

    /**
     * Sets the numberOfMedia.
     * @param numberOfMedia the numberOfMedia to set
     */
    public void setNumberOfMedia(Map<String, Integer> numberOfMedia)
    {
        this.numberOfMedia = numberOfMedia;
    }

    /**
     * Returns the develop.
     * @return the develop
     */
    public boolean isDevelop()
    {
        return develop;
    }

    /**
     * Sets the develop.
     * @param develop the develop to set
     */
    public void setDevelop(boolean develop)
    {
        this.develop = develop;
    }

    /**
     * Returns the search.
     * @return the search
     */
    public String getSearch()
    {
        return search;
    }

    /**
     * Sets the search.
     * @param search the search to set
     */
    public void setSearch(String search)
    {
        this.search = search;
    }

    /**
     * Returns the pages.
     * @return the pages
     */
    public List<Page> getPages()
    {
        return pages;
    }

    /**
     * Returns the page.
     * @return the page
     */
    public int getPage()
    {
        return page;
    }

    /**
     * Sets the page.
     * @param page the page to set
     */
    public void setPage(int page)
    {
        this.page = page;
    }

    /**
     * Returns the searchResult.
     * @return the searchResult
     */
    public AdvancedResult getSearchResult()
    {
        return searchResult;
    }

    public boolean isSingleInstance()
    {
        return MediaEl.module().isSingleinstance();
    }

    public String getMessage(String key, String param)
    {
        return getMsgs().get(key, new String[]{param });
    }

    /**
     * Obtains the query string to use for reloading the current page.<br>
     * Unwanted parameters are removed.
     * @return
     */
    public String currentQueryString()
    {
        String s = request.getQueryString();
        s = "&" + s;
        int p1;
        while ((p1 = StringUtils.indexOfAny(s, new String[]{"&type=", "&command=", "&page=" })) != -1)
        {
            int p2 = s.indexOf('&', p1 + 1);
            s = s.substring(0, p1) + (p2 != -1 ? s.substring(p2) : StringUtils.EMPTY);
        }
        return s.substring(1);
    }

    /**
     * Builds the href for the media type link in the navigation bar.
     * @param type
     * @return
     */
    public String mediatabLink(MediaTypeConfiguration type)
    {
        String s = currentQueryString();

        if (type == null)
        {
            return '?' + s;
        }
        return '?' + s + (s.length() > 0 ? '&' : StringUtils.EMPTY) + "type=" + type.getName();
    }

    protected SortMode getDefaultSorting()
    {
        return SortMode.FILENAME_ASC;
    }

    /**
     * Returns the installed media module version.
     * @return version as string.
     */
    public String getModuleVersion()
    {
        return MediaEl.module().getVersion();
    }
}
