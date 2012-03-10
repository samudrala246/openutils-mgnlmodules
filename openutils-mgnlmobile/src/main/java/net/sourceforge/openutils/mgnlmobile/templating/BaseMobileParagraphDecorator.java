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
 * @author molaschi
 * @version $Id: $
 */
public abstract class BaseMobileParagraphDecorator extends Paragraph
{

    private Paragraph innerParagraph;

    protected abstract boolean hasMobileParagraph();

    protected abstract Paragraph getMobileParagraph();

    /**
     * Returns the innerParagraph.
     * @return the innerParagraph
     */
    public Paragraph getInnerParagraph()
    {
        return innerParagraph;
    }

    /**
     * Sets the innerParagraph.
     * @param innerParagraph the innerParagraph to set
     */
    public void setInnerParagraph(Paragraph innerParagraph)
    {
        this.innerParagraph = innerParagraph;
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

    protected Paragraph resolveTemplate()
    {
        if (MobileFilter.isMobileRequest() && hasMobileParagraph())
        {
            Paragraph p = getMobileParagraph();
            if (p != null)
            {
                return p;
            }
        }
        return innerParagraph;
    }

    // delegated paragraph methods

    /**
     * @param actionResult
     * @param model
     * @return
     * @see info.magnolia.module.templating.AbstractRenderable#determineTemplatePath(java.lang.String,
     * info.magnolia.module.templating.RenderingModel)
     */
    public String determineTemplatePath(String actionResult, RenderingModel model)
    {
        return super.determineTemplatePath(actionResult, model);
    }

    /**
     * @return
     * @see info.magnolia.module.templating.AbstractRenderable#getDescription()
     */
    public String getDescription()
    {
        return innerParagraph.getDescription();
    }

    /**
     * @return
     * @see info.magnolia.module.templating.AbstractRenderable#getDialog()
     */
    public String getDialog()
    {
        return innerParagraph.getDialog();
    }

    /**
     * @param path
     * @return
     * @deprecated
     * @see info.magnolia.module.templating.Paragraph#getDialogPath(java.lang.String)
     */
    public String getDialogPath(String path)
    {
        return innerParagraph.getDialogPath(path);
    }

    /**
     * @return
     * @see info.magnolia.module.templating.AbstractRenderable#getI18nBasename()
     */
    public String getI18nBasename()
    {
        return innerParagraph.getI18nBasename();
    }

    /**
     * @return
     * @see info.magnolia.module.templating.AbstractRenderable#getModelClass()
     */
    public Class< ? extends RenderingModel> getModelClass()
    {
        return innerParagraph.getModelClass();
    }

    /**
     * @return
     * @see info.magnolia.module.templating.AbstractRenderable#getName()
     */
    public String getName()
    {
        return innerParagraph.getName();
    }

    /**
     * @return
     * @see info.magnolia.module.templating.AbstractRenderable#getParameters()
     */
    public Map getParameters()
    {
        return innerParagraph.getParameters();
    }

    /**
     * @return
     * @deprecated
     * @see info.magnolia.module.templating.Paragraph#getTemplateType()
     */
    public String getTemplateType()
    {
        return this.getType();
    }

    /**
     * @return
     * @see info.magnolia.module.templating.AbstractRenderable#getTitle()
     */
    public String getTitle()
    {
        return innerParagraph.getTitle();
    }

    /**
     * @return
     * @see java.lang.Object#hashCode()
     */
    public int hashCode()
    {
        return innerParagraph.hashCode();
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
        return innerParagraph.newModel(content, definition, parentModel);
    }

    /**
     * @param description
     * @see info.magnolia.module.templating.AbstractRenderable#setDescription(java.lang.String)
     */
    public void setDescription(String description)
    {
        innerParagraph.setDescription(description);
    }

    /**
     * @param dialog
     * @see info.magnolia.module.templating.AbstractRenderable#setDialog(java.lang.String)
     */
    public void setDialog(String dialog)
    {
        innerParagraph.setDialog(dialog);
    }

    /**
     * @param path
     * @deprecated
     * @see info.magnolia.module.templating.Paragraph#setDialogPath(java.lang.String)
     */
    public void setDialogPath(String path)
    {
        innerParagraph.setDialogPath(path);
    }

    /**
     * @param basename
     * @see info.magnolia.module.templating.AbstractRenderable#setI18nBasename(java.lang.String)
     */
    public void setI18nBasename(String basename)
    {
        innerParagraph.setI18nBasename(basename);
    }

    /**
     * @param modelClass
     * @see info.magnolia.module.templating.AbstractRenderable#setModelClass(java.lang.Class)
     */
    public void setModelClass(Class< ? extends RenderingModel> modelClass)
    {
        innerParagraph.setModelClass(modelClass);
    }

    /**
     * @param name
     * @see info.magnolia.module.templating.AbstractRenderable#setName(java.lang.String)
     */
    public void setName(String name)
    {
        innerParagraph.setName(name);
    }

    /**
     * @param params
     * @see info.magnolia.module.templating.AbstractRenderable#setParameters(java.util.Map)
     */
    public void setParameters(Map params)
    {
        innerParagraph.setParameters(params);
    }

    /**
     * @param templatePath
     * @see info.magnolia.module.templating.AbstractRenderable#setTemplatePath(java.lang.String)
     */
    public void setTemplatePath(String templatePath)
    {
        innerParagraph.setTemplatePath(templatePath);
    }

    /**
     * @param type
     * @deprecated
     * @see info.magnolia.module.templating.Paragraph#setTemplateType(java.lang.String)
     */
    public void setTemplateType(String type)
    {
        innerParagraph.setTemplateType(type);
    }

    /**
     * @param title
     * @see info.magnolia.module.templating.AbstractRenderable#setTitle(java.lang.String)
     */
    public void setTitle(String title)
    {
        innerParagraph.setTitle(title);
    }

    /**
     * @param type
     * @see info.magnolia.module.templating.AbstractRenderable#setType(java.lang.String)
     */
    public void setType(String type)
    {
        innerParagraph.setType(type);
    }

}
