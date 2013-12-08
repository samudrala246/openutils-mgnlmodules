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
package net.sourceforge.openutils.mgnllms.filters;

import info.magnolia.cms.beans.runtime.File;
import info.magnolia.cms.beans.runtime.FileProperties;
import info.magnolia.cms.core.AggregationState;
import info.magnolia.cms.core.HierarchyManager;
import info.magnolia.cms.core.NodeData;
import info.magnolia.cms.core.NonExistingNodeData;
import info.magnolia.cms.filters.AggregatorFilter;
import info.magnolia.context.MgnlContext;

import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Extended aggregator filter that can collect resources from zip files stored as jcr binary properties
 * @author molaschi
 * @version $Id: $
 */
public class AggregatorServingZipFilter extends AggregatorFilter
{

    /**
     * Logger.
     */
    private Logger log = LoggerFactory.getLogger(AggregatorServingZipFilter.class);

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean collect() throws RepositoryException
    {
        boolean result = super.collect();
        HierarchyManager hm = MgnlContext.getHierarchyManager(MgnlContext.getAggregationState().getRepository());
        String handleToCheck = MgnlContext.getAggregationState().getHandle();
        if (!result
            || (result && StringUtils.isNotBlank(handleToCheck) && handleToCheck.lastIndexOf('/') > 0 && (!hm
                .isExist(handleToCheck) || !hm.isNodeData(handleToCheck))))
        {
            NodeData nonExistingN = null;
            try
            {
                nonExistingN = hm.getNodeData(handleToCheck);
            }
            catch (Exception e)
            {
                // go on
            }
            if (nonExistingN == null || nonExistingN instanceof NonExistingNodeData)
            {
                final AggregationState aggregationState = MgnlContext.getAggregationState();
                final String handle = aggregationState.getHandle();
                final String repository = aggregationState.getRepository();

                final HierarchyManager hierarchyManager = MgnlContext.getHierarchyManager(repository);

                String zipHandle = verifyShorterUrlForZip(handle, hierarchyManager);
                if (zipHandle != null)
                {
                    aggregationState.setHandle(zipHandle);
                    NodeData nd = hierarchyManager.getNodeData(zipHandle);
                    File file = new File(nd);
                    aggregationState.setFile(file);
                    aggregationState.setTemplateName(nd.getAttribute(FileProperties.PROPERTY_TEMPLATE));
                    return true;
                }
            }
        }
        return true;
    }

    /**
     * Find zip file property handle
     * @param handle handle on which search for a zip file property
     * @param hm hierarchy manager
     * @return zip file property handle
     */
    protected String verifyShorterUrlForZip(String handle, HierarchyManager hm)
    {
        String prevHandle = StringUtils.substringBeforeLast(handle, "/");

        try
        {
            if (prevHandle.lastIndexOf("/") < 1 || (hm.isExist(prevHandle) && !hm.isNodeData(prevHandle)))
            {
                return null;
            }

            if (hm.isNodeData(prevHandle))
            {
                NodeData nd = hm.getNodeData(prevHandle);
                if (nd.getType() == PropertyType.BINARY
                    && "zip".equals(nd.getAttribute(FileProperties.PROPERTY_EXTENSION)))
                {
                    return prevHandle;
                }
                return null;
            }
        }
        catch (RepositoryException e)
        {
            log.error(e.getMessage(), e);
            return null;
        }
        return verifyShorterUrlForZip(prevHandle, hm);
    }

}
