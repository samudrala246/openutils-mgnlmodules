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

import info.magnolia.cms.util.AlertUtil;
import info.magnolia.context.MgnlContext;
import info.magnolia.module.admininterface.TemplatedMVCHandler;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.RepositoryException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.apache.commons.lang.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author fgiust
 * @version $Id$
 */
public class LinkfixPage extends TemplatedMVCHandler
{

    private static Logger log = LoggerFactory.getLogger(LinkfixPage.class);

    private static final String SUCCESS = "success";

    private String replacements;

    private String sourceRepositoriesAndPaths;

    private String targetRepositories;

    public LinkfixPage(String name, HttpServletRequest request, HttpServletResponse response)
    {
        super(name, request, response);
    }

    /**
     * Sets the replacements.
     * @param replacements the replacements to set
     */
    public void setReplacements(String replacements)
    {
        this.replacements = replacements;
    }

    /**
     * Sets the sourceRepositoriesAndPaths.
     * @param sourceRepositoriesAndPaths the sourceRepositoriesAndPaths to set
     */
    public void setSourceRepositoriesAndPaths(String sourceRepositoriesAndPaths)
    {
        this.sourceRepositoriesAndPaths = sourceRepositoriesAndPaths;
    }

    /**
     * Sets the targetRepositories.
     * @param targetRepositories the targetRepositories to set
     */
    public void setTargetRepositories(String targetRepositories)
    {
        this.targetRepositories = targetRepositories;
    }

    /**
     * Returns the replacements.
     * @return the replacements
     */
    public String getReplacements()
    {
        return replacements;
    }

    /**
     * Returns the sourceRepositoriesAndPaths.
     * @return the sourceRepositoriesAndPaths
     */
    public String getSourceRepositoriesAndPaths()
    {
        return sourceRepositoriesAndPaths;
    }

    /**
     * Returns the targetRepositories.
     * @return the targetRepositories
     */
    public String getTargetRepositories()
    {
        return targetRepositories;
    }

    public String execute() throws RepositoryException
    {

        String[] replacementsSplit = StringUtils.split(replacements, "\n");
        Map<String, String> replacementsMap = new LinkedHashMap<String, String>();
        for (String string : replacementsSplit)
        {
            String[] split = StringUtils.split(StringUtils.trim(string), "=");
            if (split != null & split.length > 1)
            {
                replacementsMap.put(split[0], split[1]);
            }
        }

        String[] sourceRepositoriesAndPathsArray = StringUtils
            .split(StringUtils.trim(sourceRepositoriesAndPaths), "\n");
        List<String> sourceRepositoriesAndPathList = new ArrayList<String>();
        for (String string : sourceRepositoriesAndPathsArray)
        {
            if (StringUtils.contains(string, ":"))
            {
                sourceRepositoriesAndPathList.add(StringUtils.trim(string));
            }
            else
            {
                log.error("Invalid path: {}", string);
            }
        }

        String[] repoArray = StringUtils.split(targetRepositories);
        List<String> reposAsList = new ArrayList<String>();
        for (String string : repoArray)
        {
            String repo = StringUtils.trim(string);
            if (StringUtils.isNotEmpty(repo))
            {

                // test it
                try
                {
                    MgnlContext.getJCRSession(repo);
                }
                catch (IllegalArgumentException e)
                {
                    log.error(e.getMessage(), e);
                    AlertUtil.setMessage(String.format("Invalid repository: %s", repo));
                    return SUCCESS;
                }
                reposAsList.add(repo);
            }
        }

        StopWatch sw = new StopWatch();
        sw.start();

        Linkfix linkfix = new Linkfix(replacementsMap, sourceRepositoriesAndPathList, reposAsList);

        try
        {

            linkfix.process();

        }
        catch (Throwable e)
        {
            log.error(e.getMessage(), e);
            AlertUtil.setMessage(e.getClass().getName()
                + ": "
                + StringUtils.defaultIfEmpty(e.getMessage(), "(no message)"));
            return SUCCESS;
        }

        sw.stop();
        long time = sw.getTime();

        AlertUtil
            .setMessage(String
                .format(
                    "Fix done in %s, %s nodes processed, %s matches and %s substitutions applied. Full list of modified properties:\n%s",
                    DurationFormatUtils.formatDurationWords(time, true, false),
                    linkfix.getNodesCount(),
                    linkfix.getMatchesCount(),
                    linkfix.getSubstitutionsCount(),
                    StringUtils.defaultIfEmpty(linkfix.getFullLog(), "(none)")));

        return SUCCESS;
    }

}
