/**
 *
 * Tagcloud module for Magnolia CMS (http://www.openmindlab.com/lab/products/mgnltagcloud.html)
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

package net.sourceforge.openutils.mgnltagcloud.dialog;

import info.magnolia.cms.core.Content;
import info.magnolia.cms.i18n.I18nContentSupportFactory;
import info.magnolia.cms.security.AccessDeniedException;
import info.magnolia.module.admininterface.dialogs.ConfiguredDialog;
import info.magnolia.objectfactory.Components;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.jcr.RepositoryException;
import javax.jcr.Value;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.openutils.mgnlcontrols.dialog.ConfigurableFreemarkerDialog;
import net.sourceforge.openutils.mgnltagcloud.bean.TagCloud;
import net.sourceforge.openutils.mgnltagcloud.manager.TagCloudManager;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.LocaleUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author cstrappazzon
 * @version $Id$
 */
@SuppressWarnings("deprecation")
public class TagCloudDialog extends ConfigurableFreemarkerDialog
{

    /**
     * Logger.
     */
    private Logger log = LoggerFactory.getLogger(ConfiguredDialog.class);

    private TagsProvider tagsProvider;

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getPath()
    {
        return "dialog/tagcloud.ftl";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void init(HttpServletRequest request, HttpServletResponse response, Content websiteNode, Content configNode)
        throws RepositoryException
    {
        super.init(request, response, websiteNode, configNode);
        this.setConfig(
            "saveHandler",
            "net.sourceforge.openutils.mgnltagcloud.dialog.TagCloudDialogSafeMultivalueSaveHandler");

        if (!StringUtils.isEmpty(getConfigValue("tagsProvider")))
        {
            try
            {
                tagsProvider = (TagsProvider) Class.forName(getConfigValue("tagsProvider")).newInstance();
            }
            catch (Exception e)
            {
                log.error("Failed to instantiate custom tags provider due to {}", e.getMessage());
            }
        }
        if (tagsProvider == null)
        {
            tagsProvider = new TagsProvider()
            {

                /**
                 * {@inheritDoc}
                 */
                public Collection<String> provideTags(TagCloud tagCloud, Content storageNode)
                {
                    return tagCloud != null ? tagCloud.getTags().keySet() : Collections.<String> emptyList();
                }
            };
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getValue()
    {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void addToParameters(Map<String, Object> parameters)
    {
        super.addToParameters(parameters);

        Collection<String> tagList = new ArrayList<String>();
        try
        {
            if (getStorageNode() != null
                && getStorageNode().getNodeData(this.getName()) != null
                && getStorageNode().getNodeData(this.getName()).getValues() != null)
            {
                for (Value value : getStorageNode().getNodeData(this.getName()).getValues())
                {
                    tagList.add(StringUtils.trim(value.getString()));
                }
            }
        }
        catch (AccessDeniedException e)
        {
            log.error("AccessDeniedException {}", e);
        }
        catch (RepositoryException e)
        {
            log.error("RepositoryException: {}", e);
        }

        TagCloud tagCloud = buildTagCloud();

        Map<String, Map<String, Object>> tags = new HashMap<String, Map<String, Object>>();
        for (String key : tagsProvider.provideTags(tagCloud, getStorageNode()))
        {
            key = StringUtils.trim(key);
            Map<String, Object> values = new HashMap<String, Object>(2);
            values.put("selected", tagList.contains(key));
            Integer count = tagCloud.getTags().get(key);
            values.put("count", count != null ? count : 0);
            tags.put(key, values);
        }

        parameters.put("configuredTagList", tags);

        // load the script once: if there are multiple instances
        String jQueryAttributeName = "info.magnolia.cms.gui.dialog.jquery.loaded";
        boolean includejquery = getRequest().getAttribute(jQueryAttributeName) == null;
        parameters.put("includejquery", includejquery);
        if (includejquery)
        {
            getRequest().setAttribute(jQueryAttributeName, "true");
        }

        parameters.put("debugEnabled", Boolean.valueOf(this.getConfigValue("debugEnabled")));

        parameters.put("width", StringUtils.isNotEmpty(this.getConfigValue("width"))
            ? this.getConfigValue("width")
            : "");

    }

    public interface TagsProvider
    {

        Collection<String> provideTags(TagCloud tagCloud, Content storageNode);
    }

    protected TagCloud buildTagCloud()
    {
        // Get configured node
        TagCloud tagCloud = null;

        String tagCloudName = this.getConfigValue("tagCloudName");
        if (StringUtils.isBlank(tagCloudName))
        {
            tagCloudName = this.getName();
        }
        else if (I18nContentSupportFactory.getI18nSupport().isEnabled() && "true".equals(this.getConfigValue("i18n")))
        {
            Locale locale = LocaleUtils.toLocale(this.getTopParent().getConfigValue("locale", null));
            boolean isFallbackLanguage = locale == null
                || I18nContentSupportFactory.getI18nSupport().getFallbackLocale().equals(locale);
            if (!isFallbackLanguage)
            {
                tagCloudName += "_" + locale;
            }
        }

        if (StringUtils.isNotBlank(tagCloudName))
        {
            TagCloud tagCloudOriginal = Components.getComponent(TagCloudManager.class).getTagCloud(tagCloudName);
            if (tagCloudOriginal != null)
            {
                try
                {
                    tagCloud = (TagCloud) BeanUtils.cloneBean(tagCloudOriginal);
                    tagCloud.setTags(new HashMap<String, Integer>());
                }
                catch (IllegalAccessException e)
                {
                    // should never happen
                }
                catch (InstantiationException e)
                {
                    // should never happen
                }
                catch (InvocationTargetException e)
                {
                    // should never happen
                }
                catch (NoSuchMethodException e)
                {
                    // should never happen
                }
            }
            else if (StringUtils.isNotBlank(this.getConfigValue("tagCloudName")))
            {
                log.warn("No tag cloud configured with name : {}", tagCloudName);
            }

            if (tagCloud == null)
            {

                tagCloud = new TagCloud();
                if (StringUtils.isNotBlank(this.getConfigValue("tagCloudRepository")))
                {
                    tagCloud.setRepository(this.getConfigValue("tagCloudRepository"));
                }
                if (StringUtils.isNotBlank(this.getConfigValue("tagCloudPath")))
                {
                    tagCloud.setPath(this.getConfigValue("tagCloudPath"));
                }
                if (StringUtils.isNotBlank(this.getConfigValue("tagCloudProperty")))
                {
                    tagCloud.setPropertyName(this.getConfigValue("tagCloudProperty"));
                }
                else
                {
                    // Set the actual node name, it's useful in some cases
                    tagCloud.setPropertyName(tagCloudName);
                }
            }

            tagCloud.setCount(10000);
            Components.getComponent(TagCloudManager.class).calculateTagCloud(tagCloud);
        }
        return tagCloud;
    }
}
