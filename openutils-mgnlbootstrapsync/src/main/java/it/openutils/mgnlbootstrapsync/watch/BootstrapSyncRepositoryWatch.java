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

package it.openutils.mgnlbootstrapsync.watch;

import it.openutils.mgnlbootstrapsync.BootstrapEnableRoot;
import it.openutils.mgnlbootstrapsync.BootstrapExportRoot;


/**
 * @author mmolaschi
 * @version $Id: $
 */
public class BootstrapSyncRepositoryWatch
{

    private String repository;

    private String exportPath;

    private BootstrapExportRoot exportRoots;

    private BootstrapEnableRoot enableRoots;

    private String nodeType;

    /**
     * @param repository repository to watch
     * @param exportPath folder to export files to
     * @param exportRoots comma separeted list of nodes (every modified to children will be exported as this node)
     * @param enableRoots enabled branches for listener
     * @param nodeType nodeType
     */
    public BootstrapSyncRepositoryWatch(
        String repository,
        String exportPath,
        String exportRoots,
        String enableRoots,
        String nodeType)
    {
        this.repository = repository;
        this.exportPath = exportPath;
        this.exportRoots = new BootstrapExportRoot(exportRoots);
        this.enableRoots = new BootstrapEnableRoot(enableRoots);
        this.nodeType = nodeType;
    }

    /**
     * Returns the repository.
     * @return the repository
     */
    public String getRepository()
    {
        return repository;
    }

    /**
     * Sets the repository.
     * @param repository the repository to set
     */
    public void setRepository(String repository)
    {
        this.repository = repository;
    }

    /**
     * Returns the exportPath.
     * @return the exportPath
     */
    public String getExportPath()
    {
        return exportPath;
    }

    /**
     * Sets the exportPath.
     * @param exportPath the exportPath to set
     */
    public void setExportPath(String exportPath)
    {
        this.exportPath = exportPath;
    }

    /**
     * Returns the exportRoots.
     * @return the exportRoots
     */
    public BootstrapExportRoot getExportRoots()
    {
        return exportRoots;
    }

    /**
     * Sets the exportRoots.
     * @param exportRoots the exportRoots to set
     */
    public void setExportRoots(BootstrapExportRoot exportRoots)
    {
        this.exportRoots = exportRoots;
    }

    /**
     * Returns the enableRoots.
     * @return the enableRoots
     */
    public BootstrapEnableRoot getEnableRoots()
    {
        return enableRoots;
    }

    /**
     * Sets the enableRoots.
     * @param enableRoots the enableRoots to set
     */
    public void setEnableRoots(BootstrapEnableRoot enableRoots)
    {
        this.enableRoots = enableRoots;
    }

    /**
     * Returns the nodeType.
     * @return the nodeType
     */
    public String getNodeType()
    {
        return nodeType;
    }

    /**
     * Sets the nodeType.
     * @param nodeType the nodeType to set
     */
    public void setNodeType(String nodeType)
    {
        this.nodeType = nodeType;
    }

}
