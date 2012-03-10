/**
 *
 * BootstrapSync for Magnolia CMS (http://www.openmindlab.com/lab/products/bootstrapsync.html)
 * Copyright(C) ${project.inceptionYear}-2012, Openmind S.r.l. http://www.openmindonline.it
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

package it.openutils.mgnlbootstrapsync;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;


/**
 * @author mmolaschi luca boati
 * @version $Id: $
 */
public class BootstrapExportRoot
{

    private String exportRoots;

    private List<String[]> rootTokensList;

    /**
     *
     */
    public BootstrapExportRoot()
    {

    }

    /**
     * @param exportRoots comma separeted list of nodes (every modified to children will be exported as this node)
     */
    public BootstrapExportRoot(String exportRoots)
    {
        this.setExportRoots(exportRoots);
    }

    /**
     * Returns the exportRoots.
     * @return the exportRoots
     */
    public String getExportRoots()
    {
        return exportRoots;
    }

    /**
     * Sets the exportRoots.
     * @param exportRoots the exportRoots to set
     */
    public void setExportRoots(String exportRoots)
    {
        this.exportRoots = exportRoots;

        if (exportRoots != null)
        {
            String[] roots = StringUtils.split(exportRoots, ",");
            rootTokensList = new ArrayList<String[]>(roots.length);
            for (String root : roots)
            {
                String[] rootTokens = StringUtils.split(root, "/");
                int i = 0;
                for (String[] rt : rootTokensList)
                {
                    if (rootTokens.length > rt.length)
                    {
                        rootTokensList.add(i, rootTokens);
                        i = -1;
                        break;
                    }
                    i++;
                }
                if (i >= 0)
                {
                    rootTokensList.add(rootTokens);
                }
            }
        }
    }

    /**
     * Check if path match some root node
     * @param path path to check
     * @return true if a path match some root
     */
    public boolean match(String path)
    {
        return null != getRootPath(path);
    }

    /**
     * Get matching root for path
     * @param path path
     * @return matching root
     */
    public String getRootPath(String path)
    {
        if (rootTokensList == null || rootTokensList.size() == 0)
        {
            return null;
        }

        String[] pathTokens = StringUtils.split(path, "/");
        for (String[] rootTokens : rootTokensList)
        {
            if (pathTokens.length < rootTokens.length)
            {
                continue;
            }
            boolean match = true;
            for (int i = 0; i < rootTokens.length; i++)
            {
                if (rootTokens[i].equals("*"))
                {
                    continue;
                }
                if (!rootTokens[i].equals(pathTokens[i]))
                {
                    match = false;
                    break;
                }
            }
            if (match)
            {
                String[] pathRootTokens = (String[]) ArrayUtils.subarray(pathTokens, 0, rootTokens.length);

                String pathRoot = StringUtils.join(pathRootTokens, "/");
                if (!pathRoot.startsWith("/"))
                {
                    pathRoot = "/" + pathRoot;
                }
                return pathRoot;
            }
        }
        return null;

    }
}
