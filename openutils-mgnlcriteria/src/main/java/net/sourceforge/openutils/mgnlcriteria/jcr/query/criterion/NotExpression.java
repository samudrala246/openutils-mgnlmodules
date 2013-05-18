/**
 *
 * Criteria API for Magnolia CMS (http://www.openmindlab.com/lab/products/mgnlcriteria.html)
 * Copyright(C) 2009-2013, Openmind S.r.l. http://www.openmindonline.it
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

import net.sourceforge.openutils.mgnlcriteria.jcr.query.Criteria;
import net.sourceforge.openutils.mgnlcriteria.jcr.query.JCRQueryException;


/**
 * @author fgrilli
 * @version $Id$
 */
public class NotExpression extends BaseCriterion implements Criterion
{

    private static final long serialVersionUID = -5057676844499041929L;

    private Criterion expression;

    public NotExpression(Criterion expression)
    {
        this.expression = expression;
    }

    @Override
    public String toString()
    {
        return expression.toString();
    }

    public String toXPathString(Criteria criteria) throws JCRQueryException
    {
        StringBuilder fragment = new StringBuilder(" not(");
        fragment.append(expression.toXPathString(criteria)).append(") ");
        log.debug("xpathString is {} ", fragment);
        return fragment.toString();
    }
}
