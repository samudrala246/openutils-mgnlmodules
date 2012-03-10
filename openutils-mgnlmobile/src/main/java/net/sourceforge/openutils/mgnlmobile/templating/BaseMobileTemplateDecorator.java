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
import info.magnolia.rendering.model.RenderingModel;
import info.magnolia.rendering.template.RenderableDefinition;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import net.sourceforge.openutils.mgnlmobile.filters.MobileFilter;


/**
 * @author Luca Boati
 * @version $Id: $
 */
public abstract class BaseMobileTemplateDecorator extends Template
{

    private Template innerTemplate;

    protected abstract boolean hasMobileTemplates();

    protected abstract Template getMobileTemplate();

    /**
     * Returns the innerTemplate.
     * @return the innerTemplate
     */
    public Template getInnerTemplate()
    {
        return innerTemplate;
    }

    /**
     * Sets the innerTemplate.
     * @param innerTemplate the innerTemplate to set
     */
    public void setInnerTemplate(Template innerTemplate)
    {
        this.innerTemplate = innerTemplate;
    }

    // customized template methods

    /**
     * @return
     * @see info.magnolia.module.templating.AbstractRenderable#getTemplatePath()
     */
    public String getTemplatePath()
    {
        return resolveTemplate().getTemplatePath();
    }

    /**
     * @return
     * @see info.magnolia.module.templating.AbstractRenderable#getType()
     */
    public String getType()
    {
        return resolveTemplate().getType();
    }

    protected Template resolveTemplate()
    {
        if (MobileFilter.isMobileRequest() && hasMobileTemplates())
        {
            Template t = getMobileTemplate();
            if (t != null)
            {
                return t;
            }
        }
        return innerTemplate;
    }

    // delegated template methods

    /**
     * @return
     * @deprecated
     * @see info.magnolia.module.templating.Template#getPath()
     */
    public String getPath()
    {
        return this.getTemplatePath();
    }

    /**
     * @return
     * @see info.magnolia.module.templating.Template#getI18NTitle()
     */
    public String getI18NTitle()
    {
        return innerTemplate.getI18NTitle();
    }

    /**
     * @param key
     * @return
     * @see info.magnolia.module.templating.Template#getParameter(java.lang.String)
     */
    public String getParameter(String key)
    {
        return innerTemplate.getParameter(key);
    }

    /**
     * @param content
     * @param definition
     * @param parentModel
     * @return
     * @throws IllegalArgumentException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @see info.magnolia.module.templating.AbstractRenderable#newModel(info.magnolia.cms.core.Content,
     * info.magnolia.module.templating.RenderableDefinition, info.magnolia.module.templating.RenderingModel)
     */
    public RenderingModel newModel(Content content, RenderableDefinition definition, RenderingModel parentModel)
        throws IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException
    {
        return innerTemplate.newModel(content, definition, parentModel);
    }

    /**
     * @return
     * @see info.magnolia.module.templating.Template#isVisible()
     */
    public boolean isVisible()
    {
        return innerTemplate.isVisible();
    }

    /**
     * @param extension
     * @return
     * @see info.magnolia.module.templating.Template#getSubTemplate(java.lang.String)
     */
    public Template getSubTemplate(String extension)
    {
        return innerTemplate.getSubTemplate(extension);
    }

    /**
     * @param extension
     * @param subTemplate
     * @see info.magnolia.module.templating.Template#addSubTemplate(java.lang.String,
     * info.magnolia.module.templating.Template)
     */
    public void addSubTemplate(String extension, Template subTemplate)
    {
        innerTemplate.addSubTemplate(extension, subTemplate);
    }

    /**
     * @return
     * @see info.magnolia.module.templating.Template#getSubTemplates()
     */
    public Map<String, Template> getSubTemplates()
    {
        return innerTemplate.getSubTemplates();
    }

    /**
     * @param subTemplates
     * @see info.magnolia.module.templating.Template#setSubTemplates(java.util.Map)
     */
    public void setSubTemplates(Map<String, Template> subTemplates)
    {
        innerTemplate.setSubTemplates(subTemplates);
    }

    /**
     * @param path
     * @deprecated
     * @see info.magnolia.module.templating.Template#setPath(java.lang.String)
     */
    public void setPath(String path)
    {
        innerTemplate.setPath(path);
    }

    /**
     * @param visible
     * @see info.magnolia.module.templating.Template#setVisible(boolean)
     */
    public void setVisible(boolean visible)
    {
        innerTemplate.setVisible(visible);
    }

    /**
     * @return
     * @see info.magnolia.module.templating.AbstractRenderable#getName()
     */
    public String getName()
    {
        return innerTemplate.getName();
    }

    /**
     * @param node
     * @return
     * @see info.magnolia.module.templating.Template#isAvailable(info.magnolia.cms.core.Content)
     */
    public boolean isAvailable(Content node)
    {
        return innerTemplate.isAvailable(node);
    }

    /**
     * @return
     * @see info.magnolia.module.templating.AbstractRenderable#getTitle()
     */
    public String getTitle()
    {
        return innerTemplate.getTitle();
    }

    /**
     * @return
     * @see info.magnolia.module.templating.AbstractRenderable#getDescription()
     */
    public String getDescription()
    {
        return innerTemplate.getDescription();
    }

    /**
     * @return
     * @see info.magnolia.module.templating.Template#getContent()
     */
    public Content getContent()
    {
        return innerTemplate.getContent();
    }

    /**
     * @param description
     * @see info.magnolia.module.templating.AbstractRenderable#setDescription(java.lang.String)
     */
    public void setDescription(String description)
    {
        innerTemplate.setDescription(description);
    }

    /**
     * @param content
     * @see info.magnolia.module.templating.Template#setContent(info.magnolia.cms.core.Content)
     */
    public void setContent(Content content)
    {
        innerTemplate.setContent(content);
    }

    /**
     * @param name
     * @see info.magnolia.module.templating.AbstractRenderable#setName(java.lang.String)
     */
    public void setName(String name)
    {
        innerTemplate.setName(name);
    }

    /**
     * @param templatePath
     * @see info.magnolia.module.templating.AbstractRenderable#setTemplatePath(java.lang.String)
     */
    public void setTemplatePath(String templatePath)
    {
        innerTemplate.setTemplatePath(templatePath);
    }

    /**
     * @param type
     * @see info.magnolia.module.templating.AbstractRenderable#setType(java.lang.String)
     */
    public void setType(String type)
    {
        innerTemplate.setType(type);
    }

    /**
     * @param title
     * @see info.magnolia.module.templating.AbstractRenderable#setTitle(java.lang.String)
     */
    public void setTitle(String title)
    {
        innerTemplate.setTitle(title);
    }

    /**
     * @return
     * @see info.magnolia.module.templating.AbstractRenderable#getDialog()
     */
    public String getDialog()
    {
        return innerTemplate.getDialog();
    }

    /**
     * @param dialog
     * @see info.magnolia.module.templating.AbstractRenderable#setDialog(java.lang.String)
     */
    public void setDialog(String dialog)
    {
        innerTemplate.setDialog(dialog);
    }

    /**
     * @return
     * @see info.magnolia.module.templating.AbstractRenderable#getI18nBasename()
     */
    public String getI18nBasename()
    {
        return innerTemplate.getI18nBasename();
    }

    /**
     * @param basename
     * @see info.magnolia.module.templating.AbstractRenderable#setI18nBasename(java.lang.String)
     */
    public void setI18nBasename(String basename)
    {
        innerTemplate.setI18nBasename(basename);
    }

    /**
     * @return
     * @see info.magnolia.module.templating.AbstractRenderable#getParameters()
     */
    public Map getParameters()
    {
        return innerTemplate.getParameters();
    }

    /**
     * @param params
     * @see info.magnolia.module.templating.AbstractRenderable#setParameters(java.util.Map)
     */
    public void setParameters(Map params)
    {
        innerTemplate.setParameters(params);
    }

    /**
     * @return
     * @see info.magnolia.module.templating.AbstractRenderable#getModelClass()
     */
    public Class< ? extends RenderingModel> getModelClass()
    {
        return innerTemplate.getModelClass();
    }

    /**
     * @param modelClass
     * @see info.magnolia.module.templating.AbstractRenderable#setModelClass(java.lang.Class)
     */
    public void setModelClass(Class< ? extends RenderingModel> modelClass)
    {
        innerTemplate.setModelClass(modelClass);
    }

}
