/**
 *
 * Tasks for for Magnolia CMS (http://www.openmindlab.com/lab/products/mgnltasks.html)
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

package it.openutils.mgnltasks;

import info.magnolia.cms.core.MgnlNodeType;
import info.magnolia.jcr.util.NodeUtil;
import info.magnolia.module.InstallContext;
import info.magnolia.module.delta.AbstractRepositoryTask;
import info.magnolia.module.delta.TaskExecutionException;
import it.openutils.mgnlutils.util.NodeUtilsExt;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;


/**
 * Change a nodedata value.
 * @author fgiust
 * @version $Id$
 */
public class SetPropertyTask extends AbstractRepositoryTask
{

    private final String workspaceName;

    private final String nodePath;

    private final String propertyName;

    private final Object newPropertyValue;

    public SetPropertyTask(String workspaceName, String nodePath, String propertyName, Object newPropertyValue)
    {
        super("Checking the value of " + nodePath + "/" + propertyName, "Creating property "
            + nodePath
            + "/"
            + propertyName
            + " and setting its value to "
            + newPropertyValue);
        this.workspaceName = workspaceName;
        this.nodePath = nodePath;
        this.propertyName = propertyName;
        this.newPropertyValue = newPropertyValue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doExecute(InstallContext ctx) throws RepositoryException, TaskExecutionException
    {
        Session session = ctx.getJCRSession(workspaceName);

        Node node = NodeUtil.createPath(session.getRootNode(), nodePath, MgnlNodeType.NT_CONTENT);

        NodeUtilsExt.setPropertyIfDifferent(node, propertyName, newPropertyValue);

    }

}
