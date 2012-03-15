/**
 *
 * CAS integration module for Magnolia CMS (http://www.openmindlab.com/lab/products/mgnlcas.html)
 * Copyright(C) 2007-2012, Openmind S.r.l. http://www.openmindonline.it
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

package net.sourceforge.openutils.mgnlcas;

import info.magnolia.cms.security.ExternalUser;
import info.magnolia.cms.security.auth.Entity;

import java.util.Iterator;
import java.util.Set;

import javax.security.auth.Subject;


/**
 * @author fgiust
 * @version $Id: CasMagnoliaUser.java 2244 2008-08-11 19:31:53Z fgiust $
 */
public class CasMagnoliaUser extends ExternalUser
{

    /**
     * Stable serialVersionUID.
     */
    private static final long serialVersionUID = 42L;

    /**
     * User properties.
     */
    private Entity userDetails;

    /**
     * @param subject
     */
    protected CasMagnoliaUser(Subject subject)
    {
        super(subject);

        final Set<Entity> principalDetails = subject.getPrincipals(Entity.class);
        final Iterator<Entity> entityIterator = principalDetails.iterator();
        this.userDetails = entityIterator.next();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getProperty(String propertyName)
    {
        // todo: why this is not in ExternalUser?
        return (String) this.userDetails.getProperty(propertyName);
    }

}
