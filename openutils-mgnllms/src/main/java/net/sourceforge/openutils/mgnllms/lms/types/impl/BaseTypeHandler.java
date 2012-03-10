/**
 *
 * E-learning Module for Magnolia CMS (http://www.openmindlab.com/lab/products/lms.html)
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

package net.sourceforge.openutils.mgnllms.lms.types.impl;

import info.magnolia.cms.beans.runtime.MultipartForm;
import info.magnolia.cms.core.Content;
import info.magnolia.cms.util.NodeDataUtil;
import info.magnolia.context.MgnlContext;

import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sourceforge.openutils.mgnllms.lms.types.LmsTypeHandler;


/**
 * @author luca boati
 */
public abstract class BaseTypeHandler implements LmsTypeHandler
{

    protected String getPropertyLocalized(Content node, String property)
    {
        Locale locale = MgnlContext.getLocale();
        String language = locale.getLanguage().toLowerCase();
        String languageCountry = language + "-" + locale.getCountry().toLowerCase();
        if (LANGUAGES.contains(languageCountry))
        {
            return NodeDataUtil.getString(node, property + "-" + languageCountry);
        }
        else if (LANGUAGES.contains(language))
        {
            return NodeDataUtil.getString(node, property + "-" + language);
        }
        return NodeDataUtil.getString(node, property + "-en");
    }

    /**
     * {@inheritDoc}
     */
    public String getTitle(Content node)
    {
        return getPropertyLocalized(node, "title");
    }

    /**
     * {@inheritDoc}
     */
    public String getTags(Content node)
    {
        return getPropertyLocalized(node, "tags");
    }

    /**
     * {@inheritDoc}
     */
    public String getDescription(Content node)
    {
        return getPropertyLocalized(node, "description");
    }

    /**
     * {@inheritDoc}
     */
    public String getAbstract(Content node)
    {
        return getPropertyLocalized(node, "abstract");
    }

    /**
     * {@inheritDoc}
     */
    public String getFilename(Content node)
    {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public String getFullFilename(Content node)
    {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public String getNewNodeName(Content parent, MultipartForm form, HttpServletRequest request)
    {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public String getUrl(Content node, Map<String, String> options)
    {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public String getUrl(Content node)
    {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public void init(Content typeDefinitionNode)
    {

    }

    /**
     * {@inheritDoc}
     */
    public boolean onPostSave(Content node, MultipartForm form, HttpServletRequest request)
    {
        return true;
    }

}
