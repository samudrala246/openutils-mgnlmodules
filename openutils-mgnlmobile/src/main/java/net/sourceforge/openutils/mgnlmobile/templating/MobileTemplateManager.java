/**
 *
 * Mobile Module for Magnolia CMS (http://www.openmindlab.com/lab/products/mgnlmobile.html)
 * Copyright(C) 2010-2012, Openmind S.r.l. http://www.openmindonline.it
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

package net.sourceforge.openutils.mgnlmobile.templating;

import info.magnolia.cms.core.Content;
import info.magnolia.cms.core.ItemType;
import info.magnolia.cms.util.ContentUtil;
import info.magnolia.content2bean.Content2BeanException;
import info.magnolia.content2bean.Content2BeanUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.jcr.RepositoryException;

import net.sourceforge.openutils.mgnlmobile.filters.MobileFilter;


/**
 * Manages the templates for mobile device
 * @author Luca Boati
 * @version $Id: $
 */
public class MobileTemplateManager extends TemplateManager
{

    /**
     * The cached templates
     */
    private Map<String, Template> cachedContent = new Hashtable<String, Template>();

    /**
     * The templates visible in the templates selection
     */
    private List<Template> visibleTemplates = new ArrayList<Template>();

    /**
     * Called by the ObservedManager
     */
    protected void onRegister(Content node)
    {
        try
        {
            log.info("Loading Template info from {}", node.getHandle()); //$NON-NLS-1$

            // It makes possibly to use templates defined within subfolders of /module/templating/Templates
            Collection<Content> children = collectChildren(node);

            if ((children != null) && !(children.isEmpty()))
            {
                Iterator<Content> templates = children.iterator();
                cacheContent(templates);
            }

            log.debug("Template info loaded from {}", node.getHandle()); //$NON-NLS-1$
        }
        catch (Exception re)
        {
            log.error("Failed to load Template info from " + node.getHandle() + ": " + re.getMessage(), re);
        }

    }

    protected void onClear()
    {
        this.cachedContent.clear();
        this.visibleTemplates.clear();
    }

    /**
     * Returns the cached content of the requested template. TemplateInfo properties:
     * <ol>
     * <li>title - title describing template</li>
     * <li>type - jsp / servlet</li>
     * <li>path - jsp / servlet path</li>
     * <li>description - description of a template</li>
     * </ol>
     * @return TemplateInfo
     * @deprecated since 4.0 Use {@link #getTemplateDefinition(String)} instead
     */
    public Template getInfo(String key)
    {
        return getTemplateDefinition(key);
    }

    /**
     * Returns the cached content of the requested template. TemplateInfo properties:
     * <ol>
     * <li>title - title describing template</li>
     * <li>type - jsp / servlet</li>
     * <li>path - jsp / servlet path</li>
     * <li>description - description of a template</li>
     * </ol>
     * @return TemplateInfo
     */
    public Template getTemplateDefinition(String key)
    {
        Template t = cachedContent.get(key);
        if (t instanceof BaseMobileTemplateDecorator && !MobileFilter.isMobileRequest())
        {
            return ((BaseMobileTemplateDecorator) t).getInnerTemplate();
        }
        return t;
    }

    /**
     * Returns the cached content of the requested template. TemplateInfo properties:
     * <ol>
     * <li>title - title describing template</li>
     * <li>type - jsp / servlet</li>
     * <li>path - jsp / servlet path</li>
     * <li>description - description of a template</li>
     * </ol>
     * @return TemplateInfo
     */
    public Template getInfo(String key, String extension)
    {
        Template template = cachedContent.get(key);

        if (template == null)
        {
            return null;
        }
        Template subtemplate = template.getSubTemplate(extension);
        if (subtemplate != null)
        {
            return subtemplate;
        }

        return template;
    }

    /**
     * Adds templates definition to TemplatesInfo cache.
     * @param templates iterator as read from the repository
     * @param visibleTemplates List in with all visible templates will be added
     */
    private void addTemplatesToCache(Iterator<Content> templates, List<Template> visibleTemplates)
    {
        while (templates.hasNext())
        {
            Content c = templates.next();

            try
            {
                Template ti = (Template) Content2BeanUtil.toBean(c, true, Template.class);
                Content mobileContent = ContentUtil.getContent(c, "mobile");
                if (mobileContent != null)
                {
                    BaseMobileTemplateDecorator mtd = (BaseMobileTemplateDecorator) Content2BeanUtil.toBean(
                        mobileContent,
                        true,
                        DefaultMobileTemplateDecorator.class);
                    mtd.setInnerTemplate(ti);
                    cachedContent.put(ti.getName(), mtd);
                }
                else
                {
                    cachedContent.put(ti.getName(), ti);
                }
                if (ti.isVisible())
                {
                    visibleTemplates.add(ti);
                }

                log.debug("Registering template [{}]", ti.getName());
            }
            catch (Content2BeanException e)
            {
                log.error("Can't register template [" + c.getName() + "]", e);
            }

        }
    }

    /**
     * Load content of this template info page in a hash table caching at the system load, this will save lot of time on
     * every request while matching template info.
     */
    private void cacheContent(Iterator<Content> templates)
    {
        if (templates != null)
        {
            addTemplatesToCache(templates, visibleTemplates);
        }
    }

    /**
     * Recursive search for content nodes contains template data (looks up subfolders)
     * @author <a href="mailto:tm@touk.pl">Tomasz Mazan</a>
     * @param cnt current folder to look for template's nodes
     * @return collection of template's content nodes from current folder and descendants
     */
    private Collection<Content> collectChildren(Content cnt)
    {
        // Collect template's content node - children of current node
        Collection<Content> children = cnt.getChildren(ItemType.CONTENTNODE);

        // Look into subfolders
        Collection<Content> subFolders = cnt.getChildren(ItemType.CONTENT);
        if ((subFolders != null) && !(subFolders.isEmpty()))
        {

            for (Content subCnt : subFolders)
            {
                Collection<Content> grandChildren = collectChildren(subCnt);

                if ((grandChildren != null) && !(grandChildren.isEmpty()))
                {
                    children.addAll(grandChildren);
                }
            }

        }

        return children;
    }

    public Iterator<Template> getAvailableTemplates(Content node)
    {
        List<Template> templateList = new ArrayList<Template>();
        for (Template template : visibleTemplates)
        {

            if (template.isAvailable(node))
            {
                templateList.add(template);
            }
        }
        return templateList.iterator();
    }

    /**
     * Get templates collection.
     * @return Collection list containing templates as Template objects
     */
    public Iterator<Template> getAvailableTemplates()
    {
        return visibleTemplates.iterator();
    }

    public Template getDefaultTemplate(Content node)
    {
        Template tmpl;
        try
        {
            // try to use the same as the parent
            tmpl = this.getTemplateDefinition(node.getParent().getTemplate());
            if (tmpl != null && tmpl.isAvailable(node))
            {
                return tmpl;
            }
            // otherwise use the first available template
            else
            {
                Iterator<Template> templates = getAvailableTemplates(node);
                if (templates.hasNext())
                {
                    return templates.next();
                }
            }
        }
        catch (RepositoryException e)
        {
            log.error("Can't resolve default template for node " + node.getHandle(), e);
        }
        return null;
    }

}
