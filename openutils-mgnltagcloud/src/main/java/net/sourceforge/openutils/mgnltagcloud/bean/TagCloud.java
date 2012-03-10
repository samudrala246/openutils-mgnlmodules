/**
 *
 * Tagcloud module for Magnolia CMS (http://www.openmindlab.com/lab/products/mgnltagcloud.html)
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

package net.sourceforge.openutils.mgnltagcloud.bean;

import info.magnolia.repository.RepositoryConstants;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;


/**
 * @author molaschi
 * @version $Id$
 */
public class TagCloud
{

    private String repository = RepositoryConstants.WEBSITE;

    private String path = "/";

    private String name;

    private String propertyName = "tags";

    private int count = 20;

    private boolean enabled = true;

    private boolean cacheAndObserve = true;

    private Map<String, Integer> tags = new HashMap<String, Integer>();

    /**
     * Returns the repository.
     * @return the repository
     */
    public String getRepository()
    {
        return repository;
    }

    /**
     * Sets the repository.
     * @param repository the repository to set
     */
    public void setRepository(String repository)
    {
        this.repository = repository;
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
     * Sets the path.
     * @param path the path to set
     */
    public void setPath(String path)
    {
        this.path = path;
    }

    /**
     * Returns the name.
     * @return the name
     */
    public String getName()
    {
        return name;
    }

    /**
     * Sets the name.
     * @param name the name to set
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Returns the propertyName.
     * @return the propertyName
     */
    public String getPropertyName()
    {
        return propertyName;
    }

    /**
     * Sets the propertyName.
     * @param propertyName the propertyName to set
     */
    public void setPropertyName(String propertyName)
    {
        this.propertyName = propertyName;
    }

    /**
     * Returns the count.
     * @return the count
     */
    public int getCount()
    {
        return count;
    }

    /**
     * Sets the count.
     * @param count the count to set
     */
    public void setCount(int count)
    {
        this.count = count;
    }

    /**
     * Returns the enabled.
     * @return the enabled
     */
    public boolean isEnabled()
    {
        return enabled;
    }

    /**
     * Sets the enabled.
     * @param enabled the enabled to set
     */
    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        sb.append(repository).append("@").append(path).append("@").append(propertyName).append("@").append(count);
        return sb.toString();
    }

    /**
     * Returns the tags.
     * @return the tags
     */
    public Map<String, Integer> getTags()
    {
        return tags;
    }

    /**
     * Sets the tags.
     * @param tags the tags to set
     */
    public void setTags(Map<String, Integer> tags)
    {
        this.tags = tags;
    }

    /**
     * Returns the cacheAndObserve.
     * @return the cacheAndObserve
     */
    public boolean isCacheAndObserve()
    {
        return cacheAndObserve;
    }

    /**
     * Sets the cacheAndObserve.
     * @param cacheAndObserve the cacheAndObserve to set
     */
    public void setCacheAndObserve(boolean cacheAndObserve)
    {
        this.cacheAndObserve = cacheAndObserve;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object object)
    {
        if (!(object instanceof TagCloud))
        {
            return false;
        }
        TagCloud rhs = (TagCloud) object;
        return new EqualsBuilder() //
            .append(this.propertyName, rhs.propertyName)
            .append(this.count, rhs.count)
            .append(this.repository, rhs.repository)
            .append(this.name, rhs.name)
            .append(this.path, rhs.path)
            .append(this.cacheAndObserve, rhs.cacheAndObserve)
            .isEquals();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode()
    {
        return new HashCodeBuilder(609434781, 862593375)
            .append(this.propertyName)
            .append(this.enabled)
            .append(this.count)
            .append(this.repository)
            .append(this.name)
            .append(this.path)
            .append(this.cacheAndObserve)
            .toHashCode();
    }

}
