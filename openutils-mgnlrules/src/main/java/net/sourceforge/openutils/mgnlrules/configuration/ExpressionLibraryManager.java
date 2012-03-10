/**
 *
 * Rules module for Magnolia CMS (http://www.openmindlab.com/lab/products/mgnlrules.html)
 * Copyright(C) 2010-2012, Openmind S.r.l. http://www.openmindonline.it
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

package net.sourceforge.openutils.mgnlrules.configuration;

import info.magnolia.cms.beans.config.ObservedManager;
import info.magnolia.cms.core.Content;
import info.magnolia.cms.util.ContentUtil;
import info.magnolia.cms.util.NodeDataUtil;
import info.magnolia.objectfactory.Components;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author dschivo
 * @version $Id$
 */
public class ExpressionLibraryManager extends ObservedManager
{

    public static ExpressionLibraryManager getInstance()
    {
        return Components.getSingleton(ExpressionLibraryManager.class);
    }

    private Logger log = LoggerFactory.getLogger(getClass());

    private final List<ExpressionLibrary> libraries = new ArrayList<ExpressionLibrary>();

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onClear()
    {
        libraries.clear();
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    protected void onRegister(Content defNode)
    {
        for (Iterator iter = ContentUtil.getAllChildren(defNode).iterator(); iter.hasNext();)
        {
            Content providerNode = (Content) iter.next();
            String providerClassName = StringUtils.defaultIfEmpty(
                NodeDataUtil.getString(providerNode, "class"),
                RepositoryExpressionLibrary.class.getName());
            try
            {
                Class providerClass = Class.forName(providerClassName);
                ExpressionLibrary library = (ExpressionLibrary) providerClass
                    .getConstructor(Content.class)
                    .newInstance(providerNode);
                if (library.isVisible())
                {
                    libraries.add(library);
                }
            }
            catch (Exception e)
            {
                log.error("Cannot instantiate expression library " + providerNode.getName(), e);
            }
        }

    }

    /**
     * Returns the libraries.
     * @return the libraries
     */
    public List<ExpressionLibrary> getLibraries()
    {
        return libraries;
    }
}
