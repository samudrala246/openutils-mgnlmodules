/**
 *
 * Generic utilities for Magnolia CMS (http://www.openmindlab.com/lab/products/mgnlutils.html)
 * Copyright(C) 2009-2012, Openmind S.r.l. http://www.openmindonline.it
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

package it.openutils.mgnlutils.virtualuri;

import info.magnolia.cms.beans.config.VirtualURIMapping;
import info.magnolia.context.MgnlContext;
import info.magnolia.context.WebContext;

import org.apache.commons.lang.StringUtils;


/**
 * <p>
 * A simple virtualuri mapping that handles a virtual path containing the current version number. Useful to avoid
 * caching of css and resources between releases, by linking them using /docroot/(version)/css/...
 * </p>
 * <p>
 * The application version can be set manually, but if not set it will be read from the "app_version" init parameter
 * from web.xml.
 * </p>
 * @author Fabrizio Giustina
 * @version $Id$
 */
public class ApplicationVersionVirtualUriMapping implements VirtualURIMapping
{

    private String appversion;

    /**
     * Sets the application version.
     * @param appversion the application version to set
     */
    public void setAppversion(String appversion)
    {
        this.appversion = appversion;
    }

    /**
     *
     */
    private void initAppVersion()
    {
        appversion = ((WebContext) MgnlContext.getInstance()).getServletContext().getInitParameter("app_version");
    }

    /**
     * {@inheritDoc}
     */
    public MappingResult mapURI(String uri)
    {
        if (appversion == null)
        {
            initAppVersion();
        }

        // just avoid NPEs if the init parameter is not set
        if (appversion != null)
        {

            if (StringUtils.contains(uri, "/" + appversion + "/"))
            {
                String newuri = "forward:" + StringUtils.remove(uri, "/" + appversion);

                // handle remapped extensions, used in aggregator, i.e. doc.jsp.css
                String pagename = StringUtils.substringAfterLast(newuri, "/");
                if (StringUtils.countMatches(pagename, ".") > 1)
                {
                    String newpage = StringUtils.substringBeforeLast(newuri, ".");
                    if ("jsp".equals(StringUtils.substringAfterLast(newpage, ".")))
                    {
                        newuri = newpage;
                    }
                }

                MappingResult r = new MappingResult();
                r.setLevel(1);
                r.setToURI(newuri);
                return r;
            }
        }

        return null;
    }

}
