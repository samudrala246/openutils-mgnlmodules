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

package net.sourceforge.openutils.mgnlmedia.media.configuration;

import info.magnolia.cms.core.ItemType;
import info.magnolia.cms.core.MgnlNodeType;
import info.magnolia.repository.RepositoryConstants;


/**
 * @author dschivo
 * @version $Id$
 */
public class UsedInWorkspace
{

    public static final UsedInWorkspace DEFAULT_WEBSITE = new UsedInWorkspace(RepositoryConstants.WEBSITE);

    private String workspaceName;

    private String nodeType = MgnlNodeType.NT_PAGE;

    private String basePath = "/";

    private String propertyName = ".";

    /**
     * 
     */
    public UsedInWorkspace()
    {
    }

    /**
     * @param workspaceName
     * @param nodeType
     */
    public UsedInWorkspace(String workspaceName)
    {
        this.workspaceName = workspaceName;
    }

    /**
     * Returns the workspaceName.
     * @return the workspaceName
     */
    public String getWorkspaceName()
    {
        return workspaceName;
    }

    /**
     * Sets the workspaceName.
     * @param workspaceName the workspaceName to set
     */
    public void setWorkspaceName(String workspaceName)
    {
        this.workspaceName = workspaceName;
    }

    /**
     * Returns the nodeType.
     * @return the nodeType
     */
    public String getNodeType()
    {
        return nodeType;
    }

    /**
     * Sets the nodeType.
     * @param nodeType the nodeType to set
     */
    public void setNodeType(String nodeType)
    {
        this.nodeType = nodeType;
    }

    /**
     * Returns the basePath.
     * @return the basePath
     */
    public String getBasePath()
    {
        return basePath;
    }

    /**
     * Sets the basePath.
     * @param basePath the basePath to set
     */
    public void setBasePath(String basePath)
    {
        this.basePath = basePath;
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
}
