/**
 *
 * Tagcloud module for Magnolia CMS (http://www.openmindlab.com/lab/products/mgnltagcloud.html)
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

package net.sourceforge.openutils.mgnltagcloud.manager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jcr.RepositoryException;
import javax.jcr.observation.Event;
import javax.jcr.observation.EventIterator;
import javax.jcr.observation.EventListener;

import net.sourceforge.openutils.mgnltagcloud.bean.TagCloud;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Observe a repository and refresh tagclouds on any event
 * @author molaschi
 * @version $Id$
 */
public class TagCloudRepositoryObserver implements EventListener
{

    /**
     * Logger.
     */
    private Logger log = LoggerFactory.getLogger(TagCloudRepositoryObserver.class);

    private String repository;

    /**
     * 
     */
    public TagCloudRepositoryObserver(String repository)
    {
        this.repository = repository;
    }

    /**
     * {@inheritDoc}
     */
    public void onEvent(EventIterator events)
    {
        // make a list of paths with events
        Set<String> paths = new HashSet<String>();

        while (events.hasNext())
        {
            Event event = events.nextEvent();
            try
            {
                paths.add(event.getPath());
            }
            catch (RepositoryException e)
            {
                log.error("Error getting event path", e);
            }
        }

        // make a list of tagClouds to refresh
        List<TagCloud> tagCloudsToRefresh = new ArrayList<TagCloud>();
        for (String path : paths)
        {
            for (Map.Entry<String, TagCloud> entry : TagCloudManager
                .getInstance()
                .getTagClouds(this.repository)
                .entrySet())
            {
                if (path.startsWith(entry.getValue().getPath()))
                {
                    if (!tagCloudsToRefresh.contains(entry.getValue()))
                    {
                        tagCloudsToRefresh.add(entry.getValue());
                    }
                }
            }
        }

        // refresh tagClouds
        for (TagCloud tc : tagCloudsToRefresh)
        {
            TagCloudManager.getInstance().calculateTagCloud(tc);
        }
    }

    /**
     * Returns the repository.
     * @return the repository
     */
    public String getRepository()
    {
        return repository;
    }

}
