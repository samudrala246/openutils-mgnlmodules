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

import info.magnolia.repository.RepositoryConstants;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author dschivo
 * @version $Id$
 */
public class UsedInWorkspace
{

    public static final UsedInWorkspace DEFAULT_WEBSITE = new UsedInWorkspace(RepositoryConstants.WEBSITE);

    private String workspaceName;

    private List<String> nodetypes = new ArrayList<String>();

    private String basepath = "/";

    private List<String> properties = new ArrayList<String>();

    public UsedInWorkspace()
    {
    }

    public UsedInWorkspace(String workspaceName)
    {
        this.workspaceName = workspaceName;
    }

    public String getWorkspaceName()
    {
        return workspaceName;
    }

    public void setWorkspaceName(String workspaceName)
    {
        this.workspaceName = workspaceName;
    }

    public List<String> getProperties()
    {
        return properties;
    }

    public List<String> getNodetypes()
    {
        return nodetypes;
    }

    public String getBasepath()
    {
        return basepath;
    }

    public void setBasepath(String basepath)
    {
        this.basepath = basepath;
    }

    // required for node2bean
    public void addNodetypes(String nodetype)
    {
        this.nodetypes.add(nodetype);
    }

    // required for node2bean
    public void addProperties(String property)
    {
        this.properties.add(property);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
            .append("workspaceName", this.workspaceName)
            .append("basepath", this.basepath)
            .append("nodetypes", this.nodetypes)
            .append("properties", this.properties)
            .toString();
    }

}
