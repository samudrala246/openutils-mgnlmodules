/**
 *
 * Generic utilities for Magnolia CMS (http://www.openmindlab.com/lab/products/mgnlutils.html)
 * Copyright(C) 2009-2011, Openmind S.r.l. http://www.openmindonline.it
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

package it.openutils.mgnlutils.templating;

import info.magnolia.cms.core.Content;
import info.magnolia.cms.util.ContentUtil;
import info.magnolia.rendering.template.TemplateAvailability;
import info.magnolia.rendering.template.TemplateDefinition;
import info.magnolia.rendering.template.configured.ConfiguredTemplateDefinition;

import java.util.HashSet;
import java.util.Set;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * <p>
 * A simple extension of Template that lets you filter available templates in the tree by configuring
 * </p>
 * <ul>
 * <li>A parent path (e.g. /mysite/</li>
 * <li>A comma separated list of levels (e.g. "1,2" to make the template only available at level 1 and 2)</li>
 * <li>A comma separated list of required parent templates (e.g. "home" to make the template available only if one of
 * its ancestor has the "home" template set.</li>
 * </ul>
 * <p>
 * Also allows the configuration of paragraphs to be auto-generated using the "autogenerate" and model class properties:
 * </p>
 * <ul>
 * <li>autogenerate="path1=paragraph1,path2=paragraph2,..." (e.g. "main-column/singleton=p-text")</li>
 * <li>modelClass=it.openutils.mgnlutils.templating.AutoGenerateTemplateModel</li>
 * </ul>
 * @author fgiust
 * @version $Id$
 */
public class ExtendedTemplate extends ConfiguredTemplateDefinition
{

    private String parentPath;

    private Set<Integer> levels = new HashSet<Integer>();

    private Set<String> parentTemplates = new HashSet<String>();

    private Set<String> repositories = new HashSet<String>();

    private String autogenerate;

    /**
     * Logger.
     */
    private Logger log = LoggerFactory.getLogger(ExtendedTemplate.class);

    /**
     * Returns the autogenerate.
     * @return the autogenerate
     */
    public String getAutogenerate()
    {
        return autogenerate;
    }

    /**
     * Sets the autogenerate.
     * @param autogenerate the autogenerate to set
     */
    public void setAutogenerate(String autogenerate)
    {
        this.autogenerate = autogenerate;
    }

    /**
     * Sets the parentPath.
     * @param parentPath the parentPath to set
     */
    public void setParentPath(String parentPath)
    {
        this.parentPath = parentPath;
    }

    /**
     * Sets the levels.
     * @param levels the levels to set
     */
    public void setLevels(String levels)
    {
        synchronized (this.levels)
        {
            this.levels.clear();

            String[] levelsString = StringUtils.split(levels);
            for (String string : levelsString)
            {
                this.levels.add(NumberUtils.toInt(string));
            }
        }
    }

    /**
     * Sets the parentTemplates.
     * @param parentTemplates the parentTemplates to set
     */
    public void setParentTemplates(String parentTemplates)
    {
        synchronized (this.parentTemplates)
        {
            this.parentTemplates.clear();

            String[] levelsString = StringUtils.split(parentTemplates);
            for (String string : levelsString)
            {
                this.parentTemplates.add(StringUtils.trim(string));
            }
        }
    }

    /**
     * @param repositories
     */
    public void setRepositories(String repositories)
    {
        synchronized (this.repositories)
        {
            this.repositories.clear();

            String[] repositoriesString = StringUtils.split(repositories);
            for (String string : repositoriesString)
            {
                this.repositories.add(StringUtils.trim(string));
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TemplateAvailability getTemplateAvailability()
    {
        final TemplateAvailability x = super.getTemplateAvailability();
        return new TemplateAvailability()
        {
            
            public boolean isAvailable(Node content, TemplateDefinition templateDefinition)
            {
                boolean available = x != null ? !x.isAvailable(content, templateDefinition) : true;

                if (!available)
                {
                    return false;
                }

                Content node = ContentUtil.asContent(content);
                if (repositories != null && !repositories.isEmpty())
                {
                    try
                    {
                        if (!repositories.contains(node.getWorkspace().getName()))
                        {
                            return false;
                        }
                    }
                    catch (RepositoryException e)
                    {
                        // ignore, should never happen
                    }
                }

                if (available && (levels != null && !levels.isEmpty()))
                {
                    try
                    {
                        int currentLevel = node.getLevel();
                        if (!levels.contains(currentLevel))
                        {
                            return false;
                        }
                    }
                    catch (RepositoryException e)
                    {
                        // ignore, should never happen
                    }
                }

                if (available && StringUtils.isNotBlank(parentPath))
                {
                    if (!StringUtils.contains(node.getHandle(), parentPath))
                    {
                        return false;
                    }
                }

                if (available && (parentTemplates != null && !parentTemplates.isEmpty()))
                {
                    try
                    {
                        Content parent = node.getParent();
                        while (parent.getLevel() > 0)
                        {
                            if (parentTemplates.contains(parent.getTemplate()))
                            {
                                return true;
                            }
                            parent = parent.getParent();
                        }
                    }
                    catch (RepositoryException e)
                    {
                        log.warn("Error checking parent: " + e.getMessage(), e);
                    }
                    return false;
                }

                return true;
            }
        };
    }

}
