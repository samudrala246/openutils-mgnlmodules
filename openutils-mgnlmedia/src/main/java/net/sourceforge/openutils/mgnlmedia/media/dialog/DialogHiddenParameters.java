/**
 *
 * SimpleMedia Module for Magnolia CMS (http://www.openmindlab.com/lab/products/media.html)
 * Copyright(C) 2008-2013, Openmind S.r.l. http://www.openmindonline.it
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

package net.sourceforge.openutils.mgnlmedia.media.dialog;



import info.magnolia.cms.core.Content;

import javax.jcr.RepositoryException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.openutils.mgnlcontrols.dialog.ConfigurableFreemarkerDialog;
import net.sourceforge.openutils.mgnlmedia.media.save.HiddenParametersSaveHandler;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author ADMIN
 * @version $Id: $
 */
@SuppressWarnings("deprecation")
public class DialogHiddenParameters extends ConfigurableFreemarkerDialog
{

    /**
     * Logger.
     */
    private Logger log = LoggerFactory.getLogger(DialogHiddenParameters.class);

    /**
     * {@inheritDoc}
     */

    @Override
    public void init(HttpServletRequest request, HttpServletResponse response, Content websiteNode, Content configNode)
        throws RepositoryException
    {
        super.init(request, response, websiteNode, configNode);
        if (StringUtils.isEmpty(getConfigValue("saveHandler")))
        {
            setConfig("saveHandler", HiddenParametersSaveHandler.class.getName());
        }
    }

    @Override
    protected String getPath()
    {
        return "dialog/hiddenParameters.ftl";
    }
}
