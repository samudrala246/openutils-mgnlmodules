/**
 *
 * Generic utilities for Magnolia CMS (http://www.openmindlab.com/lab/products/mgnlutils.html)
 * Copyright(C) 2009-2012, Openmind S.r.l. http://www.openmindonline.it
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

/**
 * A task that checks for pages that contains invalid (missing) paragraphs and logs the result as an error on startup.
 * Please note that this task collect the paragraph list extracting any direct subnode of the "paragraphs" nodes in each
 * installed module, and not using ParagraphManager (not yet initialized at startup).
 * @author fgiust
 * @version $Id$
 */
public class CheckMissingParagraphsTask extends BaseCheckMissingTask
{

    public CheckMissingParagraphsTask()
    {
        super("paragraph", "mgnl:contentNode");
    }

}
