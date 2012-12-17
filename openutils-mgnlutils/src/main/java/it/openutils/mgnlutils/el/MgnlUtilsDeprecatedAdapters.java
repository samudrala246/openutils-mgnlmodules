package it.openutils.mgnlutils.el;

import info.magnolia.cms.core.AggregationState;
import info.magnolia.cms.core.Content;
import info.magnolia.context.MgnlContext;
import info.magnolia.context.WebContext;
import info.magnolia.jcr.util.ContentMap;

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
        AggregationState aggregationState = safeGetAggregationStateIfAvailable();
        if (aggregationState != null)
        {
            Content mainContent = aggregationState.getCurrentContent();
            return mainContent != null ? mainContent.getJCRNode() : null;
        }
        return null;
    }

    public static Node getCurrentMain()
    {
        AggregationState aggregationState = safeGetAggregationStateIfAvailable();
        if (aggregationState != null)
        {
            Content mainContent = aggregationState.getMainContent();
            return mainContent != null ? mainContent.getJCRNode() : null;
        }
        return null;
    }

    public static AggregationState safeGetAggregationStateIfAvailable()
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
        else
        {
            log.warn("Unable to handle object of type {}", nodeorcontent);
        }

        return null;
    }
}
