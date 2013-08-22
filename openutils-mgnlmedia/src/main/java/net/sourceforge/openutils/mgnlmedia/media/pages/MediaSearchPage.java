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
import info.magnolia.context.MgnlContext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.jcr.RepositoryException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.openutils.mgnlmedia.media.configuration.MediaConfigurationManager;
import net.sourceforge.openutils.mgnlmedia.media.configuration.MediaTypeConfiguration;
import net.sourceforge.openutils.mgnlmedia.media.lifecycle.MediaModule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Search in medias and shows results
 * @author molaschi
 * @version $Id$
 * @deprecated replaced by MediaAdvancedSearchPage
 */
@Deprecated
public class MediaSearchPage extends MessagesTemplatedMVCHandler
{

    /**
     * Logger.
     */
    private Logger log = LoggerFactory.getLogger(MediaSearchPage.class);

    private List<MediaTypeConfiguration> mtc;

    private String type;

    private String filename;

    private String tags;

    private String[] paths;

    private String attributes;

    /**
     * @param name
     * @param request
     * @param response
     */
    public MediaSearchPage(String name, HttpServletRequest request, HttpServletResponse response)
    {
        super(name, request, response);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String show()
    {
        mtc = new ArrayList<MediaTypeConfiguration>();
        for (Map.Entry<String, MediaTypeConfiguration> entry : MediaConfigurationManager
            .getInstance()
            .getTypes()
            .entrySet())
        {
            mtc.add(entry.getValue());
        }

        HierarchyManager mgr = MgnlContext.getHierarchyManager(MediaModule.REPO);
        try
        {
            Collection<Content> contents = mgr.getRoot().getChildren(MediaConfigurationManager.FOLDER);
            List<String> tmpPaths = new ArrayList<String>();
            for (Content c : contents)
            {
                tmpPaths.add(c.getName());
            }
            paths = tmpPaths.toArray(new String[tmpPaths.size()]);
        }
        catch (RepositoryException ex)
        {
            log.error("Error retrieving root media node", ex);
        }

        return super.show();
    }

    /**
     * Returns the mtc.
     * @return the mtc
     */
    public List<MediaTypeConfiguration> getMtc()
    {
        return mtc;
    }

    /**
     * Sets the mtc.
     * @param mtc the mtc to set
     */
    public void setMtc(List<MediaTypeConfiguration> mtc)
    {
        this.mtc = mtc;
    }

    /**
     * Returns the log.
     * @return the log
     */
    public Logger getLog()
    {
        return log;
    }

    /**
     * Sets the log.
     * @param log the log to set
     */
    public void setLog(Logger log)
    {
        this.log = log;
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
     * Returns the filename.
     * @return the filename
     */
    public String getFilename()
    {
        return filename;
    }

    /**
     * Sets the filename.
     * @param filename the filename to set
     */
    public void setFilename(String filename)
    {
        this.filename = filename;
    }

    /**
     * Returns the tags.
     * @return the tags
     */
    public String getTags()
    {
        return tags;
    }

    /**
     * Sets the tags.
     * @param tags the tags to set
     */
    public void setTags(String tags)
    {
        this.tags = tags;
    }

    /**
     * Returns the paths.
     * @return the paths
     */
    public String[] getPaths()
    {
        return paths;
    }

    /**
     * Sets the paths.
     * @param paths the paths to set
     */
    public void setPaths(String[] paths)
    {
        this.paths = paths;
    }

    /**
     * Returns the attributes.
     * @return the attributes
     */
    public String getAttributes()
    {
        return attributes;
    }

    /**
     * Sets the attributes.
     * @param attributes the attributes to set
     */
    public void setAttributes(String attributes)
    {
        this.attributes = attributes;
    }

}
