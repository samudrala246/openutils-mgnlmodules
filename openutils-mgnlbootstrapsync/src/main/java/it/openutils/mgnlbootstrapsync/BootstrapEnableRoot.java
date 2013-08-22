/**
 *
 * BootstrapSync for Magnolia CMS (http://www.openmindlab.com/lab/products/bootstrapsync.html)
 * Copyright(C) ${project.inceptionYear}-2013, Openmind S.r.l. http://www.openmindonline.it
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

import org.apache.commons.lang.StringUtils;


/**
 * @author mmolaschi luca boati
 * @version $Id: $
 */
public class BootstrapEnableRoot
{

    private String enableRoots;

    private List<String[]> rootTokensList;

    /**
     *
     */
    public BootstrapEnableRoot()
    {

    }

    /**
     * @param enableRoots comma separeted list of enabled roots
     */
    public BootstrapEnableRoot(String enableRoots)
    {
        this.setEnableRoots(enableRoots);
    }

    /**
     * Returns the enableRoots.
     * @return the enableRoots
     */
    public String getEnableRoots()
    {
        return enableRoots;
    }

    /**
     * Sets the enableRoots.
     * @param enableRoots the enableRoots to set
     */
    public void setEnableRoots(String enableRoots)
    {
        this.enableRoots = enableRoots;

        if (enableRoots != null)
        {
            String[] roots = StringUtils.split(enableRoots, ",");
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
     * Check if a path is enabled
     * @param path path to check
     * @return true if path is enabled
     */
    public boolean isEnable(String path)
    {
        // check if a node can be exported
        if (path == null)
        {
            return true;
        }

        // if enablePath is not set, the node can be exported
        if (rootTokensList == null || rootTokensList.size() == 0)
        {
            return true;
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
                if (!rootTokens[i].equals(pathTokens[i]))
                {
                    match = false;
                    break;
                }
            }

            if (match)
            {
                return true;
            }
        }

        return false;

    }
}
