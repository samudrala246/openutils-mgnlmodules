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

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import net.sourceforge.openutils.mgnlcontrols.dialog.DialogRadioGroup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author diego
 * @version $Id: $
 */
public class SampleRadioOptionsProvider implements DialogRadioGroup.RadioOptionsProvider
{

    /**
     * Logger.
     */
    private static final Logger log = LoggerFactory.getLogger(SampleRadioOptionsProvider.class);

    /**
     * {@inheritDoc}
     */
    public DialogRadioGroup.RadioOptions getRadioOptions(int itemsPerPage, int pageNumberStartingFromOne,
        DialogControl dialogControl)
    {
        DialogRadioGroup.RadioOptions options = new DialogRadioGroup.RadioOptions();
        options.setItems(new LinkedHashMap<String, String>());
        options.setMore(false);
        try
        {
            Node parent = MgnlContext.getJCRSession("config").getNode("/modules/controls/gridColumnTypes");
            Iterator<Node> iter = NodeUtil.getNodes(parent, "mgnl:contentNode").iterator();
            int start = itemsPerPage > 0 ? Math.max(pageNumberStartingFromOne - 1, 0) * itemsPerPage : 0;
            int i = 0;
            while (i < start && iter.hasNext())
            {
                iter.next();
                i++;
            }
            int end = itemsPerPage > 0 ? start + itemsPerPage : 0;
            while ((end == 0 || i < end) && iter.hasNext())
            {
                Node node = iter.next();
                options.getItems().put(node.getIdentifier(), node.getName());
                i++;
            }
            options.setMore(iter.hasNext());
        }
        catch (RepositoryException e)
        {
            // TODO Auto-generated catch block
            log.error(e.getMessage(), e);
        }
        return options;
    }

}
