/**
 *
 * Controls module for Magnolia CMS (http://www.openmindlab.com/lab/products/controls.html)
 * Copyright(C) 2008-2012, Openmind S.r.l. http://www.openmindonline.it
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

package net.sourceforge.openutils.mgnlcontrols.configuration;

import info.magnolia.cms.core.Content;
import info.magnolia.cms.i18n.Messages;
import info.magnolia.cms.security.AccessDeniedException;

import java.util.Map;

import javax.jcr.RepositoryException;


/**
 * Defines a column of the grid control. Methods are called by the grid control, the freemarker template and the save
 * handler.
 * @author dschivo
 * @version $Id$
 */
public interface GridColumnType
{

    /**
     * Gets the html code for Including scripts and styles for the column. Called only once for each column type.
     * @return
     */
    public String getHeadSnippet();

    /**
     * Adds extra markup to be used by the column, if needed.
     * @param propertyName
     * @param colIndex
     * @param colmap
     * @param msgs
     * @return
     */
    public String drawSupportHtml(String propertyName, int colIndex, Map colmap, Messages msgs);

    /**
     * Gets the javascript object for the column model of the grid.
     * @param propertyName
     * @param colIndex
     * @param colmap
     * @param msgs
     * @return
     */
    public String drawColumnJs(String propertyName, int colIndex, Map colmap, Messages msgs);

    /**
     * Possibly transforms the column values on grid load.
     * @param column
     * @param colConfig
     * @param propertyName
     * @param storageNode
     */
    public void processColumnOnLoad(String[] column, Content colConfig, String propertyName, Content storageNode);

    /**
     * Possibly transforms the column values on grid save.
     * @param column
     * @param colConfig
     * @param propertyName
     * @param parentNode
     * @throws RepositoryException
     * @throws AccessDeniedException
     */
    public void processColumnOnSave(String[] column, Content colConfig, String propertyName, Content parentNode)
        throws RepositoryException, AccessDeniedException;
}
