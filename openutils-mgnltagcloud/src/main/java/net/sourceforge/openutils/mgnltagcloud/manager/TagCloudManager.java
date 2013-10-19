package net.sourceforge.openutils.mgnltagcloud.manager;

import java.util.Map;

import net.sourceforge.openutils.mgnltagcloud.bean.TagCloud;


/**
 * @author fgiust
 * @version $Id$
 */
public interface TagCloudManager
{

    TagCloud getTagCloud(String name);

    void calculateTagCloud(TagCloud tagCloud);

    Map<String, TagCloud> getTagClouds(final String repository);

    TagCloud checkForTagCloud(final TagCloud tagCloud);

    void stopObserving();
}
