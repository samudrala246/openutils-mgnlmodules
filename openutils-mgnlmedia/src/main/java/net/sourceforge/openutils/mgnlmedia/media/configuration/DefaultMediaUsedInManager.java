/**
 *
 * SimpleMedia Module for Magnolia CMS (http://www.openmindlab.com/lab/products/media.html)
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

package net.sourceforge.openutils.mgnlmedia.media.configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Singleton;
import javax.jcr.Node;

import net.sourceforge.openutils.mgnlcriteria.jcr.query.AdvancedResult;
import net.sourceforge.openutils.mgnlcriteria.jcr.query.Criteria;
import net.sourceforge.openutils.mgnlcriteria.jcr.query.JCRCriteriaFactory;
import net.sourceforge.openutils.mgnlcriteria.jcr.query.criterion.Disjunction;
import net.sourceforge.openutils.mgnlcriteria.jcr.query.criterion.Order;
import net.sourceforge.openutils.mgnlcriteria.jcr.query.criterion.Restrictions;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author dschivo
 * @version $Id$
 */
@Singleton
public class DefaultMediaUsedInManager implements MediaUsedInManager
{

    private static Logger log = LoggerFactory.getLogger(MediaUsedInManager.class);

    private Map<String, UsedInWorkspace> usedInWorkspaceMap = new HashMap<String, UsedInWorkspace>();

    public Map<String, UsedInWorkspace> getEntries()
    {
        return usedInWorkspaceMap;
    }

    public void setEntries(Map<String, UsedInWorkspace> entries)
    {
        this.usedInWorkspaceMap = entries;
    }

    public Map<String, AdvancedResult> getUsedIn(String mediaUUID)
    {
        Map<String, AdvancedResult> map = new HashMap<String, AdvancedResult>(usedInWorkspaceMap.size());
        for (String workspaceName : usedInWorkspaceMap.keySet())
        {
            map.put(workspaceName, getUsedInWorkspace(mediaUUID, workspaceName));
        }
        return map;
    }

    public AdvancedResult getUsedInWorkspace(String mediaUUID, String workspaceName)
    {
        UsedInWorkspace uiw = usedInWorkspaceMap.get(workspaceName);
        if (uiw == null)
        {
            return AdvancedResult.EMPTY_RESULT;
        }

        return getUsedInWorkspaceNodes(mediaUUID, uiw);
    }

    private AdvancedResult getUsedInWorkspaceNodes(String mediaUUID, UsedInWorkspace uiw)

    {
        List<Node> nodes = new ArrayList<Node>();
        Set<String> handles = new HashSet<String>();

        String basepath = "/jcr:root" + StringUtils.defaultString(uiw.getBasepath(), "/");

        if (!StringUtils.endsWith(basepath, "/"))
        {
            basepath = basepath + "/";
        }
        basepath = basepath + "/*";

        Criteria criteria = JCRCriteriaFactory
            .createCriteria()
            .setWorkspace(uiw.getWorkspaceName())
            .setBasePath(basepath)
            .addOrder(Order.desc("@jcr:score"));

        if (uiw.getNodetypes() != null && !uiw.getNodetypes().isEmpty())
        {
            Disjunction nodetypes = Restrictions.disjunction();
            for (String string : uiw.getNodetypes())
            {
                nodetypes.add(Restrictions.eq("@jcr:primaryType", string));
            }
            criteria.add(nodetypes);
        }

        Disjunction properties = Restrictions.disjunction();
        for (String string : uiw.getProperties())
        {
            properties.add(Restrictions.contains(string, mediaUUID));
        }
        criteria.add(properties);

        AdvancedResult result = criteria.execute();

        log.debug("{} > {}", criteria.toXpathExpression(), result.getTotalSize());

        return result;
    }
}
