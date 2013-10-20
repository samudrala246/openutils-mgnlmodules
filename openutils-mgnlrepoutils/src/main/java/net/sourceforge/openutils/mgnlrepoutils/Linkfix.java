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

import info.magnolia.context.MgnlContext;
import info.magnolia.jcr.util.MetaDataUtil;
import info.magnolia.jcr.util.NodeUtil;
import it.openutils.mgnlutils.util.NodeUtilsExt;

import java.io.StringWriter;
import java.util.Collection;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
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

            Node node = MgnlContext.getJCRSession(repo).getNode(path);
            processNode(node);
        }

        log.info("Everything done");

    }

    /**
     * @param node
     * @throws RepositoryException
     */
    private void processNode(Node node) throws RepositoryException
    {
        log.debug("Processing {}", NodeUtil.getPathIfPossible(node));
        nodesCount++;

        PropertyIterator properties = node.getProperties();

        boolean nodeUpdated = false;
        while (properties.hasNext())
        {
            nodeUpdated = processProperty(properties.nextProperty()) || nodeUpdated;
        }

        if (nodeUpdated)
        {
            MetaDataUtil.updateMetaData(node);
        }

        Iterable<Node> children = NodeUtil.getNodes(node, NodeUtil.EXCLUDE_META_DATA_FILTER);

        for (Node child : children)
        {
            processNode(child);
        }

    }

    /**
     * @param nodedata
     * @throws RepositoryException
     */
    private boolean processProperty(Property data) throws RepositoryException
    {
        if (data.isMultiple())
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
    private boolean processString(Property data) throws RepositoryException
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

                fullLog.append(String.format("%s:%s\n", data.getSession().getWorkspace().getName(), data.getPath()));

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

                fullLog.append(String.format("%s:%s\n", data.getSession().getWorkspace().getName(), data.getPath()));

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
            data.getSession().save();
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
                            Node newcontent = NodeUtilsExt.getNodeIfExists(MgnlContext.getJCRSession(repo), newhandle);

                            if (newcontent != null)
                            {
                                return NodeUtil.getNodeIdentifierIfPossible(newcontent);
                            }
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
    private boolean processMultiValue(Property data) throws RepositoryException
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

                values[j] = data.getSession().getWorkspace().getSession().getValueFactory().createValue(newString);

            }
        }

        if (gotMatches)
        {

            log.debug("Setting value {} {}", values.length, values);

            data.setValue(values);
            data.getSession().save();
            return true;
        }

        return false;
    }
}
