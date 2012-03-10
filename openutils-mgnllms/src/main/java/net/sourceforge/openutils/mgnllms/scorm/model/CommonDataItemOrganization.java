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

package net.sourceforge.openutils.mgnllms.scorm.model;

import java.util.List;

import net.sourceforge.openutils.mgnllms.scorm.model.cp.CompletionThreshold;
import net.sourceforge.openutils.mgnllms.scorm.model.cp.Data;
import net.sourceforge.openutils.mgnllms.scorm.model.cp.TimeLimitAction;
import net.sourceforge.openutils.mgnllms.scorm.model.imsss.Sequencing;


/**
 * @author carlo
 * @version $Id: $
 */
public interface CommonDataItemOrganization
{

    public CompletionThreshold getCompletionThreshold();

    public Sequencing getSequencing();

    public String getDataFromLMS();

    public TimeLimitAction getTimeLimitAction();

    public Data getData();

    public String getTitle();

    public String getIdentifier();

    public List<Item> getItem();

}
