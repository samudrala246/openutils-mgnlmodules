/**
 *
 * Repository tools for Magnolia CMS (http://www.openmindlab.com/lab/products/repotools.html)
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

package org.apache.jackrabbit.core.query.lucene;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Utility class used to run a consistency check on the jackrabbit index. This class needs to be in the
 * org.apache.jackrabbit.core.query.lucene package due to the usage of protected members.
 * @author fgiust
 * @version $Id$
 */
public class IndexUtils
{

    private final SearchIndex searchIndex;

    /**
     *
     */
    public IndexUtils(SearchIndex searchIndex)
    {
        this.searchIndex = searchIndex;
    }

    /**
     * Logger.
     */
    private static Logger log = LoggerFactory.getLogger(IndexUtils.class);

    public void runConsistencyCheck(boolean fix) throws IOException
    {

        MultiIndex index = searchIndex.getIndex();

        ConsistencyCheck check = ConsistencyCheck.run(index, searchIndex.getContext().getItemStateManager());

        List<ConsistencyCheckError> errors = check.getErrors();

        log.info("Error detected: {}", errors.size());

        if (fix)
        {
            check.repair(true);
        }
    }

}
