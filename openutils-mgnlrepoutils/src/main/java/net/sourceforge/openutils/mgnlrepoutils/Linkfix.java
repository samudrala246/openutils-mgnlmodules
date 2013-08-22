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

package net.sourceforge.openutils.mgnlrepoutils;

import info.magnolia.cms.core.Content;
import info.magnolia.cms.core.Content.ContentFilter;
import info.magnolia.cms.core.ItemType;
import info.magnolia.cms.core.NodeData;
import info.magnolia.context.MgnlContext;

import java.io.StringWriter;
import java.util.Collection;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;
import javax.jcr.Value;

import net.sourceforge.openutils.mgnlcriteria.jcr.query.AdvancedResultItem;
import net.sourceforge.openutils.mgnlcriteria.jcr.query.JCRCriteriaFactory;
import net.sourceforge.openutils.mgnlcriteria.jcr.query.criterion.Restrictions;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author fgiust
 * @version $Id$
 */
public class Linkfix
{

    private static final Pattern UUID_PATTERN = Pattern
        .compile("([a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12})");

    private static final ContentFilter ACCEPTALL_CONTENTFILTER = new ContentFilter()
    {

        public boolean accept(Content content)
        {
            try
            {
                return !ItemType.NT_METADATA.equals(content.getNodeTypeName());
            }
            catch (RepositoryException e)
            {
                return true;
            }
        }
    };

    private Map<String, String> replacements;

    private Collection<String> sourceRepositoriesAndPaths;

    private Collection<String> targetRepositories;

    private int nodesCount;

    private int matchesCount;

    private int substitutionsCount;

    private StringWriter fullLog = new StringWriter();

    /**
     * Logger.
     */
    private Logger log = LoggerFactory.getLogger(Linkfix.class);

    /**
     * @param replacements
     * @param sourceRepositoriesAndPaths
     * @param targetRepositories
     */
    public Linkfix(
        Map<String, String> replacements,
        Collection<String> sourceRepositoriesAndPaths,
        Collection<String> targetRepositories)
    {
        super();
        this.replacements = replacements;
        this.sourceRepositoriesAndPaths = sourceRepositoriesAndPaths;
        this.targetRepositories = targetRepositories;
    }

    /**
     * Returns the matchesCount.
     * @return the matchesCount
     */
    public int getMatchesCount()
    {
        return matchesCount;
    }

    /**
     * Returns the nodesCount.
     * @return the nodesCount
     */
    public int getNodesCount()
    {
        return nodesCount;
    }

    /**
     * Returns the substitutionsCount.
     * @return the substitutionsCount
     */
    public int getSubstitutionsCount()
    {
        return substitutionsCount;
    }

    public String getFullLog()
    {
        return fullLog.toString();
    }

    public void process() throws RepositoryException
    {

        log.info("Starting");
        for (String source : sourceRepositoriesAndPaths)
        {
            String repo = StringUtils.substringBefore(source, ":");
            String path = StringUtils.substringAfter(source, ":");
            if (StringUtils.isEmpty(repo) || StringUtils.isEmpty(path))
            {
                log.error("Skipping entry {}, must be in \"repo:path\" format", source);
                continue;
            }

            log.info("Processing {}", source);

            Content node = MgnlContext.getHierarchyManager(repo).getContent(path);
            processNode(node);
        }

        log.info("Everything done");

    }

    /**
     * @param node
     * @throws RepositoryException
     */
    private void processNode(Content node) throws RepositoryException
    {
        log.debug("Processing {}", node.getHandle());
        nodesCount++;

        Collection<NodeData> nodedatas = node.getNodeDataCollection();

        boolean nodeUpdated = false;
        for (NodeData nodedata : nodedatas)
        {
            nodeUpdated = processNodedata(nodedata) || nodeUpdated;
        }

        if (nodeUpdated)
        {
            node.updateMetaData();
        }

        Collection<Content> children = node.getChildren(ACCEPTALL_CONTENTFILTER);
        for (Content child : children)
        {
            processNode(child);
        }

    }

    /**
     * @param nodedata
     * @throws RepositoryException
     */
    private boolean processNodedata(NodeData data) throws RepositoryException
    {
        if (data.isMultiValue() == NodeData.MULTIVALUE_TRUE)
        {
            return processMultiValue(data);
        }
        else if (data.getType() == PropertyType.STRING)
        {
            return processString(data);
        }

        return false;

    }

    /**
     * @param data
     * @throws RepositoryException
     */
    private boolean processString(NodeData data) throws RepositoryException
    {
        String dataAsString = data.getString();
        if (StringUtils.isEmpty(dataAsString))
        {
            // quick skip
            return false;
        }

        boolean gotMatches = false;
        Matcher matcher = UUID_PATTERN.matcher(dataAsString);
        StringBuffer sb = new StringBuffer();
        while (matcher.find())
        {
            matchesCount++;
            final String uuid = matcher.group(0);

            String newuuid = findAndReplaceUuid(uuid);
            if (newuuid != null)
            {
                log.debug("Replacing UUID {} with {}", uuid, newuuid);

                fullLog.append(String.format(
                    "%s:%s\n",
                    data.getHierarchyManager().getWorkspace().getName(),
                    data.getHandle()));

                gotMatches = true;
                substitutionsCount++;
                matcher.appendReplacement(sb, newuuid);
            }

        }

        matcher.appendTail(sb);
        String newString = sb.toString();

        // replace hardcoded path
        for (Map.Entry<String, String> repl : replacements.entrySet())
        {
            Pattern pattern = Pattern.compile(repl.getKey());

            matcher = pattern.matcher(newString);
            sb = new StringBuffer();
            while (matcher.find())
            {
                matchesCount++;
                final String plainpath = matcher.group(0);

                log.debug("Replacing text {} with {}", plainpath, repl.getValue());

                fullLog.append(String.format(
                    "%s:%s\n",
                    data.getHierarchyManager().getWorkspace().getName(),
                    data.getHandle()));

                gotMatches = true;
                substitutionsCount++;
                matcher.appendReplacement(sb, repl.getValue());

            }

            matcher.appendTail(sb);
            newString = sb.toString();
        }

        if (gotMatches)
        {
            data.setValue(newString);
            data.save();
            return true;
        }

        return false;

    }

    /**
     * @param uuid
     * @return
     */
    private String findAndReplaceUuid(String uuid)
    {
        for (String repo : targetRepositories)
        {
            AdvancedResultItem result = JCRCriteriaFactory
                .createCriteria()
                .setWorkspace(repo)
                .add(Restrictions.eq("jcr:uuid", uuid))
                .execute()
                .getFirstResult();

            if (result != null)
            {

                String handle = result.getHandle();

                log.debug("Found uuid {} in repo {} with handle {}", new Object[]{uuid, repo, handle });

                for (Map.Entry<String, String> replacement : replacements.entrySet())
                {
                    if (StringUtils.contains(handle, replacement.getKey()))
                    {
                        String newhandle = StringUtils.replace(handle, replacement.getKey(), replacement.getValue());
                        try
                        {
                            Content newcontent = MgnlContext.getHierarchyManager(repo).getContent(newhandle);
                            return newcontent.getUUID();
                        }
                        catch (RepositoryException e)
                        {
                            // not existing at the expected path, go on
                        }
                    }
                }
            }

        }
        return null;
    }

    /**
     * @param data
     * @throws RepositoryException
     */
    private boolean processMultiValue(NodeData data) throws RepositoryException
    {
        Value[] values = data.getValues();

        boolean gotMatches = false;

        for (int j = 0; j < values.length; j++)
        {
            Value value = values[j];
            String dataAsString = value.getString();

            boolean gotValueMatches = false;
            Matcher matcher = UUID_PATTERN.matcher(dataAsString);
            StringBuffer sb = new StringBuffer();
            while (matcher.find())
            {
                matchesCount++;
                final String uuid = matcher.group(0);

                String newuuid = findAndReplaceUuid(uuid);
                if (newuuid != null)
                {
                    log.debug("Replacing UUID {} with {} (multivalue)", uuid, newuuid);
                    gotMatches = true;
                    gotValueMatches = true;
                    substitutionsCount++;
                    matcher.appendReplacement(sb, newuuid);
                }
                else
                {
                    matcher.appendTail(sb);
                }
            }
            if (gotValueMatches)
            {
                matcher.appendTail(sb);
                String newString = sb.toString();
                log.info("Creating value {}", newString);

                values[j] = data
                    .getHierarchyManager()
                    .getWorkspace()
                    .getSession()
                    .getValueFactory()
                    .createValue(newString);

            }
        }

        if (gotMatches)
        {

            log.debug("Setting value {} {}", values.length, values);

            data.setValue(values);
            data.save();
            return true;
        }

        return false;
    }
}
