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

package it.openutils.mgnlutils.el;

import info.magnolia.cms.core.AggregationState;
import info.magnolia.cms.core.Content;
import info.magnolia.cms.core.DefaultContent;
import info.magnolia.cms.core.SystemProperty;
import info.magnolia.cms.util.NodeMapWrapper;
import info.magnolia.context.MgnlContext;
import info.magnolia.context.WebContext;
import info.magnolia.jcr.util.ContentMap;

import java.util.Properties;

import javax.jcr.Node;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Adapters for methods deprecated in magnolia 4.5 without a clear or non deprecated replacement.
 * @author fgiust
 * @version $Id$
 */
@SuppressWarnings("deprecation")
public class MgnlUtilsDeprecatedAdapters
{

    private static Logger log = LoggerFactory.getLogger(MgnlUtilsDeprecatedAdapters.class);

    public static Node getCurrentContent()
    {
        AggregationState aggregationState = getAggregationStateIfAvailable();
        if (aggregationState != null)
        {
            Content mainContent = aggregationState.getCurrentContent();
            return mainContent != null ? mainContent.getJCRNode() : null;
        }
        return null;
    }

    public static Node getMainContent()
    {
        AggregationState aggregationState = getAggregationStateIfAvailable();
        if (aggregationState != null)
        {
            Content mainContent = aggregationState.getMainContent();
            return mainContent != null ? mainContent.getJCRNode() : null;
        }
        return null;
    }

    public static AggregationState getAggregationStateIfAvailable()
    {
        WebContext ctx = MgnlContext.getWebContextOrNull();
        if (ctx != null)
        {
            return ctx.getAggregationState();
        }
        return null;
    }

    public static Node toNode(Object nodeorcontent)
    {
        if (nodeorcontent == null)
        {
            return null;
        }
        if (nodeorcontent instanceof Node)
        {
            return (Node) nodeorcontent;
        }
        else if (nodeorcontent instanceof ContentMap)
        {
            return ((ContentMap) nodeorcontent).getJCRNode();
        }
        else if (nodeorcontent instanceof Content)
        {
            return ((Content) nodeorcontent).getJCRNode();
        }
        else if (nodeorcontent instanceof NodeMapWrapper)
        {
            return ((NodeMapWrapper) nodeorcontent).getJCRNode();
        }
        else
        {
            log.warn("Unable to handle object of type {}", nodeorcontent);
        }

        return null;
    }

    public static Properties systemProperties()
    {
        return SystemProperty.getProperties();
    }

    public static void setCurrentContent(Object content)
    {
        Node node = toNode(content);
        if (node != null)
        {
            AggregationState aggregationState = getAggregationStateIfAvailable();
            if (aggregationState != null)
            {
                aggregationState.setCurrentContent(new DefaultContent(node));
            }
        }
    }
}
