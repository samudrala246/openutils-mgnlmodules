/**
 *
 * Mobile Module for Magnolia CMS (http://www.openmindlab.com/lab/products/mgnlmobile.html)
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

package net.sourceforge.openutils.mgnlmobile.filters;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import net.sourceforge.wurfl.core.CustomWURFLHolder;
import net.sourceforge.wurfl.core.WURFLHolder;
import net.sourceforge.wurfl.core.WURFLManager;
import net.sourceforge.wurfl.core.WURFLUtils;
import net.sourceforge.wurfl.core.resource.WURFLResource;
import net.sourceforge.wurfl.core.resource.WURFLResources;
import net.sourceforge.wurfl.core.resource.XMLResource;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author molaschi
 * @version $Id: $
 */
public class DefaultMobileWURFLManager implements MobileWURFLManager
{

    /**
     * Logger.
     */
    private Logger log = LoggerFactory.getLogger(DefaultMobileWURFLManager.class);

    private WURFLHolder wurflHolder;

    public DefaultMobileWURFLManager()
    {
        init();
    }

    /**
     * 
     */
    private void init()
    {

        String mainPath = "/mgnl-resources/mgnlmobile/wurfl-2.0.18.xml.gz";
        File fileMain = getResource(mainPath, "wurfl", ".xml.gz");
        if (fileMain != null)
        {
            WURFLResource root = new XMLResource(fileMain);
            WURFLResources patches = new WURFLResources();
            String[] patchesPath = new String[]{"/mgnl-resources/mgnlmobile/web_browsers_patch.xml" };
            for (int index = 0; index < patchesPath.length; index++)
            {
                File filePatch = getResource(patchesPath[index], "web_browsers_patch", ".xml");
                if (filePatch != null)
                {
                    WURFLResource patch = new XMLResource(filePatch);
                    patches.add(patch);
                }
                else
                {
                    log.error("Error on reading file: " + filePatch);
                }
            }
            wurflHolder = new CustomWURFLHolder(root, patches);
        }
        else
        {
            log.error("Error on reading file: " + mainPath);
        }
    }

    protected File getResource(String name, String prefix, String suffix)
    {
        File fileTemp = null;
        try
        {
            InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(
                StringUtils.removeStart(name, "/"));
            fileTemp = File.createTempFile(prefix, suffix);
            fileTemp.deleteOnExit();
            OutputStream fos = new FileOutputStream(fileTemp);
            IOUtils.copy(is, fos);
            IOUtils.closeQuietly(fos);
        }
        catch (IOException e)
        {
            log.error("Error on reading file: " + name, e);
        }
        return fileTemp;
    }

    /**
     * {@inheritDoc}
     */
    public WURFLManager getWURFLManager()
    {
        return wurflHolder.getWURFLManager();
    }

    /**
     * {@inheritDoc}
     */
    public WURFLUtils getWURFLUtils()
    {
        return wurflHolder.getWURFLUtils();
    }

}
