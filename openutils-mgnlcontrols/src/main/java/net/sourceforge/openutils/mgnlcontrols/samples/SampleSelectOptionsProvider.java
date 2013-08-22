/**
 *
 * Controls module for Magnolia CMS (http://www.openmindlab.com/lab/products/controls.html)
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

package net.sourceforge.openutils.mgnlcontrols.samples;

import info.magnolia.cms.gui.dialog.DialogControl;
import info.magnolia.context.MgnlContext;
import info.magnolia.jcr.util.NodeUtil;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.jcr.LoginException;
import javax.jcr.Node;
import javax.jcr.RepositoryException;

import net.sourceforge.openutils.mgnlcontrols.dialog.DialogDependentSelectList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author diego
 * @version $Id: $
 */
public class SampleSelectOptionsProvider implements DialogDependentSelectList.SelectOptionsProvider
{

    /**
     * Logger.
     */
    private static final Logger log = LoggerFactory.getLogger(SampleSelectOptionsProvider.class);

    /**
     * {@inheritDoc}
     */
    public Map<String, String> getSelectOptions(String[] treePathValues, DialogControl dialogControl)
    {
        Map<String, String> options = new LinkedHashMap<String, String>();
        try
        {
            Node parent = treePathValues.length > 0 ? NodeUtil.getNodeByIdentifier(
                "config",
                treePathValues[treePathValues.length - 1]) : MgnlContext.getJCRSession("config").getRootNode();
            for (Iterator<Node> iter = NodeUtil.getNodes(parent, "mgnl:content").iterator(); iter.hasNext();)
            {
                Node node = iter.next();
                options.put(node.getIdentifier(), node.getName());
            }
        }
        catch (LoginException e)
        {
            // TODO Auto-generated catch block
            log.error(e.getMessage(), e);
        }
        catch (RepositoryException e)
        {
            // TODO Auto-generated catch block
            log.error(e.getMessage(), e);
        }
        return options;
    }

}
