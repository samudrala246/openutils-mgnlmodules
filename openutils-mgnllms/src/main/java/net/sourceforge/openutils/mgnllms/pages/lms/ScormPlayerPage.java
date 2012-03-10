/**
 *
 * E-learning Module for Magnolia CMS (http://www.openmindlab.com/lab/products/lms.html)
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

package net.sourceforge.openutils.mgnllms.pages.lms;

import info.magnolia.cms.util.AlertUtil;
import info.magnolia.context.MgnlContext;
import info.magnolia.module.admininterface.TemplatedMVCHandler;

import javax.jcr.RepositoryException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBException;

import net.sf.json.JSONSerializer;
import net.sf.json.JsonConfig;
import net.sourceforge.openutils.mgnllms.scorm.model.Manifest;
import net.sourceforge.openutils.mgnllms.scorm.utils.JaxbUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author carlo
 * @version $Id: $
 */
public class ScormPlayerPage extends TemplatedMVCHandler
{

    /**
     * Logger.
     */
    private Logger log = LoggerFactory.getLogger(ScormPlayerPage.class);

    private String manifest;

    private String mgnlPath;

    private String mgnlRepository;

    private String user;

    /**
     * @param name
     * @param request
     * @param response
     */
    public ScormPlayerPage(String name, HttpServletRequest request, HttpServletResponse response)
    {
        super(name, request, response);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String show()
    {
        try
        {

            Manifest manifestFromXml = JaxbUtils.getManifest(mgnlPath, mgnlRepository);

            JsonConfig jc = new JsonConfig();
            manifest = JSONSerializer.toJSON(manifestFromXml, jc).toString();
        }
        catch (JAXBException e)
        {
            log.error("Error getting manifest from {}@{}", new Object[]{mgnlRepository, mgnlPath }, e);
            AlertUtil.setException(e);
        }
        catch (RepositoryException e)
        {
            log.error("Error parsing manifest from {}@{}", new Object[]{mgnlRepository, mgnlPath }, e);
            AlertUtil.setException(e);
        }

        this.setUser(MgnlContext.getUser().getName());

        return super.show();
    }

    /**
     * Returns the manifest.
     * @return the manifest
     */
    public String getManifest()
    {
        return manifest;
    }

    /**
     * Returns the mgnlPath.
     * @return the mgnlPath
     */
    public String getMgnlPath()
    {
        return mgnlPath;
    }

    /**
     * Sets the mgnlPath.
     * @param mgnlPath the mgnlPath to set
     */
    public void setMgnlPath(String mgnlPath)
    {
        this.mgnlPath = mgnlPath;
    }

    /**
     * Returns the mgnlRepository.
     * @return the mgnlRepository
     */
    public String getMgnlRepository()
    {
        return mgnlRepository;
    }

    /**
     * Sets the mgnlRepository.
     * @param mgnlRepository the mgnlRepository to set
     */
    public void setMgnlRepository(String mgnlRepository)
    {
        this.mgnlRepository = mgnlRepository;
    }

    /**
     * Returns the user.
     * @return the user
     */
    public String getUser()
    {
        return user;
    }

    /**
     * Sets the user.
     * @param user the user to set
     */
    public void setUser(String user)
    {
        this.user = user;
    }

}
