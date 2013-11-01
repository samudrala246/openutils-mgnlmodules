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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.fail;
import info.magnolia.cms.core.MgnlNodeType;
import info.magnolia.cms.core.Path;
import info.magnolia.cms.util.ClasspathResourcesUtil;
import info.magnolia.context.MgnlContext;
import info.magnolia.jcr.util.NodeUtil;
import info.magnolia.objectfactory.Components;
import info.magnolia.repository.RepositoryConstants;
import info.magnolia.test.ComponentsTestUtil;
import info.magnolia.test.RepositoryTestCase;

import java.util.Map;
import java.util.Set;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;

import net.sourceforge.openutils.mgnltagcloud.el.TagCloudElFunctions;
import net.sourceforge.openutils.mgnltagcloud.module.TagCloudModule;
import net.sourceforge.openutils.mgnltagcloud.util.JackrabbitUtil;

import org.apache.commons.lang.ArrayUtils;
import org.apache.jackrabbit.value.ValueFactoryImpl;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author cstrappazzon
 * @version $Id$
 */
public class TagCloudManagerTest extends RepositoryTestCase
{

    /**
     * Set list of tag values
     */
    Value[] tagList = {
        ValueFactoryImpl.getInstance().createValue("tag1 tag4"),
        ValueFactoryImpl.getInstance().createValue("tag2"),
        ValueFactoryImpl.getInstance().createValue("tag3"),
        ValueFactoryImpl.getInstance().createValue("tag4") };

    /**
     * Set list of tags
     */
    String[] tagString = {"tag1 tag4", "tag2", "tag3", "tag4" };

    /**
     * List of path
     */
    String[] pathList = {"/site/path0", "/site/path1", "/site/path2", "/site/path3", "/site/path4", "/site/path5" };

    /**
     * Set Hierarchy manager
     */
    Session session;

    /**
     * Nodes number
     */
    Integer nodesNumber = 3;

    private Logger log = LoggerFactory.getLogger(TagCloudModule.class);

    /**
     * Test performance query index: show only result
     */
    @Test
    public void testPerformanceTagCloud()
    {
        long time = System.currentTimeMillis();
        Map<String, Integer> tags = TagCloudElFunctions.notcached(RepositoryConstants.WEBSITE, "/", "tags", 10);
        log.debug("Time: " + (System.currentTimeMillis() - time));
        log.debug("Map: {}", tags);
        time = System.currentTimeMillis();
        tags = TagCloudElFunctions.notcached(RepositoryConstants.WEBSITE, "/", "tags", 12);
        log.debug("Time: " + (System.currentTimeMillis() - time));
        log.debug("Map: {}", tags);
    }

    /**
     * Test get tagCloud by path
     */
    @Test
    public void testGetTagCloudByPath()
    {
        long time = System.currentTimeMillis();
        Map<String, Integer> tags = TagCloudElFunctions.notcached(RepositoryConstants.WEBSITE, pathList[0], "tags", 2);
        log.debug("Time: " + (System.currentTimeMillis() - time));
        log.debug("PathMap freq: {}", tags);
        assertEquals(nodesNumber, tags.get("tag1 tag4"));
    }

    /**
     * Test cached tagCloud
     */
    @Test
    public void testGetCachedTagCloud()
    {
        long time = System.currentTimeMillis();
        Map<String, Integer> tags = TagCloudElFunctions.cached(RepositoryConstants.WEBSITE, pathList[1], "tags", 2);
        log.debug("Time: " + (System.currentTimeMillis() - time));
        log.debug("tagCloud freq: {}", tags);
        assertEquals(nodesNumber, tags.get("tag2"));
        time = System.currentTimeMillis();
        tags = TagCloudElFunctions.cached(RepositoryConstants.WEBSITE, pathList[1], "tags", 2);
        log.debug("Time: " + (System.currentTimeMillis() - time));
        log.debug("tagCloud freq: {}", tags);
        assertEquals(nodesNumber, tags.get("tag1 tag4"));
    }

    /**
     * Test sort tagCloud tags
     */
    @Test
    public void testSortByName()
    {
        Map<String, Integer> tags = TagCloudElFunctions.cached(RepositoryConstants.WEBSITE, "/", "tags", 2);
        Map<String, Integer> orderedTags = TagCloudElFunctions.sortbyname(tags, false);
        log.debug("Tags: {}", tags);
        log.debug("Ordered Tags: {}", orderedTags);

        assertNotSame(tags.keySet(), orderedTags.keySet());
    }

    /**
     * Test sort tagCloud by count
     */
    @Test
    public void testSortByCount()
    {
        Map<String, Integer> tags = TagCloudElFunctions.cached(RepositoryConstants.WEBSITE, "/site", "tags", 10);
        log.debug("Tags: {}", tags);
        Map<String, Integer> countedTags = TagCloudElFunctions.sortbycount(tags, false);
        log.debug("Tags: {}", countedTags);

        assertNotSame(tags, countedTags);
        Set<String> listOrderedTag = countedTags.keySet();
        assertEquals("tag32", listOrderedTag.iterator().next().toString());
    }

    /**
     * Test sort tagCloud by count
     */
    @Test
    public void testNamed()
    {
        Map<String, Integer> tags = TagCloudElFunctions.named("cloud1");
        log.debug("Tags: {}", tags);

        try
        {
            Node content = session.getNode("/site/path1/content0");
            content.setProperty("tags", new Value[]{ValueFactoryImpl.getInstance().createValue("prova") });
            session.save();
        }
        catch (RepositoryException e)
        {
            log.error("Error modifying tags", e);
        }

        try
        {
            Thread.sleep(2000);
        }
        catch (InterruptedException e)
        {
            log.error("Interrupt exception", e);
            fail(e.getMessage());
        }

        tags = TagCloudElFunctions.named("cloud1");
        log.debug("Tags: {}", tags);

        assertEquals(true, tags.containsKey("prova"));
    }

    /**
     * Test getting tagCloud properties
     */
    @Test
    public void testGetTagCloudProperties()
    {
        Map<String, Integer> tags = TagCloudElFunctions.notcached(RepositoryConstants.WEBSITE, "/", "tags", 10);
        log.debug("Tags: {}", tags);

        Map<String, Integer> mapProperties = TagCloudElFunctions.props(tags);

        log.debug("Properties {}", mapProperties);

        assertEquals(new Integer(5), mapProperties.get("count"));
        assertEquals(tags.get("tag1 tag4"), mapProperties.get("max"));
    }

    @Override
    public void setUp() throws Exception
    {
        super.setUp();

        session = MgnlContext.getInstance().getJCRSession(RepositoryConstants.WEBSITE);

        // Create content structure
        Node contentRoot = NodeUtil.createPath(
            session.getRootNode(),
            Path.getValidatedLabel("site"),
            MgnlNodeType.NT_CONTENT);

        contentRoot.setProperty("tags", new Value[]{ValueFactoryImpl.getInstance().createValue("tag32") });
        for (String path : pathList)
        {
            NodeUtil.createPath(contentRoot, path.replace("/site/", ""), MgnlNodeType.NT_PAGE);
        }
        session.save();

        Node content = null;
        for (int j = 0; j < pathList.length; j++)
        {
            for (int i = 0; i < nodesNumber; i++)
            {
                content = NodeUtil.createPath(
                    session.getNode(pathList[j]),
                    Path.getValidatedLabel("content" + i),
                    MgnlNodeType.NT_PAGE);
                content.setProperty("tags", (Value[]) ArrayUtils.subarray(tagList, 0, j + 1));
            }
            log.debug("Create path {}, tags: {}", pathList[j], ArrayUtils.subarray(tagString, 0, j + 1));
        }
        session.save();

        Session hmConfig = MgnlContext.getInstance().getJCRSession(RepositoryConstants.CONFIG);
        Node contentTagcloud = NodeUtil.createPath(
            session.getNode("/"),
            Path.getValidatedLabel("clouds"),
            MgnlNodeType.NT_CONTENT);

        Node contentCloud = NodeUtil.createPath(
            contentTagcloud,
            Path.getValidatedLabel("cloud1"),
            MgnlNodeType.NT_CONTENTNODE);

        contentCloud.setProperty("repository", ValueFactoryImpl.getInstance().createValue("website"));
        contentCloud.setProperty("path", ValueFactoryImpl.getInstance().createValue("/site/path1"));
        contentCloud.setProperty("propertyName", ValueFactoryImpl.getInstance().createValue("tags"));
        contentCloud.setProperty("enabled", ValueFactoryImpl.getInstance().createValue(true));
        contentCloud.setProperty("count", ValueFactoryImpl.getInstance().createValue(50));

        hmConfig.save();

        ComponentsTestUtil.setImplementation(TagCloudManager.class, DefaultTagCloudManager.class);
        DefaultTagCloudManager manager = (DefaultTagCloudManager) Components.getComponent(TagCloudManager.class);
        manager.jackrabbitUtil = new JackrabbitUtil();

        manager.onRegister(contentCloud);
    }

    @Override
    public void tearDown() throws Exception
    {
        ComponentsTestUtil.setImplementation(TagCloudManager.class, DefaultTagCloudManager.class);
        Components.getComponent(TagCloudManager.class).stopObserving();
        super.tearDown();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void startRepository() throws Exception
    {
        extractConfigFile(
            "magnolia.indexingConfiguration",
            ClasspathResourcesUtil.getResource("/indexing_configuration.xml").openStream(),
            "target/repositories/magnolia/indexing_configuration.xml");

        super.startRepository();
    }

}