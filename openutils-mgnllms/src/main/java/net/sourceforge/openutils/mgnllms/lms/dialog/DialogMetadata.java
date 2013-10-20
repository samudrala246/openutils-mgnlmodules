/**
 *
 * E-learning Module for Magnolia CMS (http://www.openmindlab.com/lab/products/lms.html)
 * Copyright(C) 2010-2013, Openmind S.r.l. http://www.openmindonline.it
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
package net.sourceforge.openutils.mgnllms.lms.dialog;

import info.magnolia.cms.gui.dialog.DialogStatic;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.FastDateFormat;


/**
 * @author fgiust
 * @version $Id: DialogMetadata.java 1266 2009-08-14 10:49:28Z fgiust $
 */
public class DialogMetadata extends DialogStatic
{

    @Override
    protected String readValue()
    {
        if (StringUtils.equals(this.getName(), "creationdate") || StringUtils.equals(this.getName(), "lastmodified"))
        {
            if (this.getStorageNode().getMetaData().getDateProperty(this.getName()) != null)
            {
                return FastDateFormat.getInstance("yyyy-MM-dd, HH:mm").format(
                    this.getStorageNode().getMetaData().getDateProperty(this.getName()));
            }
        }

        return this.getStorageNode().getMetaData().getStringProperty(this.getName());
    }

}
