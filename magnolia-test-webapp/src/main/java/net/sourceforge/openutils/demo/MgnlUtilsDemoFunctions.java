/**
 *
 * Magnolia test webapp (http://openutils.sourceforge.net/magnolia-test-webapp)
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
package net.sourceforge.openutils.demo;

import info.magnolia.repository.RepositoryConstants;
import net.sourceforge.openutils.mgnlcriteria.jcr.query.AdvancedResult;
import net.sourceforge.openutils.mgnlcriteria.jcr.query.Criteria;
import net.sourceforge.openutils.mgnlcriteria.jcr.query.JCRCriteriaFactory;
import net.sourceforge.openutils.mgnlcriteria.jcr.query.criterion.Restrictions;


/**
 * @author fgiust
 * @version $Id$
 */
public class MgnlUtilsDemoFunctions
{

    public static AdvancedResult find(String value)
    {
        Criteria criteria = JCRCriteriaFactory
            .createCriteria()
            .setWorkspace(RepositoryConstants.WEBSITE)
            .add(Restrictions.hasNodeType("mgnl:page"))
            .add(Restrictions.contains(".", value))
            .addOrderByScore();

        return criteria.execute();
    }

}
