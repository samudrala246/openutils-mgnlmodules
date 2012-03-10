/**
 *
 * Criteria API for Magnolia CMS (http://www.openmindlab.com/lab/products/mgnlcriteria.html)
 * Copyright(C) 2009-2011, Openmind S.r.l. http://www.openmindonline.it
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

package net.sourceforge.openutils.mgnlcriteria.jcr.query.criterion;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Base abstract criterion, just to share some useful fields/constants that should not end up in the interface.
 * @author fgiust
 * @version $Id$
 */
public abstract class BaseCriterion implements Criterion
{

    /**
     * Stable serialVersionUID
     */
    private static final long serialVersionUID = 42L;

    /**
     * Logger.
     */
    protected Logger log = LoggerFactory.getLogger(getClass());

}
