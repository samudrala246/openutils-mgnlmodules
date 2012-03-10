/**
 *
 * Generic utilities for Magnolia CMS (http://www.openmindlab.com/lab/products/mgnlutils.html)
 * Copyright(C) 2009-2011, Openmind S.r.l. http://www.openmindonline.it
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
import info.magnolia.cms.util.SimpleUrlPattern;
import info.magnolia.cms.util.UrlPattern;
import info.magnolia.context.MgnlContext;
import info.magnolia.context.WebContext;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;


/**
 * Simple VirtualURI mapping that can forward to a different url depending on the content locale. The "destinations"
 * property must be a list of locale=destination strings. See below for a sample configuration:
 * 
 * <pre>
 * [] virtualURIMapping
 *    [] default
 *     - class            it.openutils.mgnlutils.virtualuri.LocaleBasedVirtualURIMapping
 *     - fromURI          /
 *     - toURI            redirect:/.magnolia/pages/adminCentral.html
 *       [] destinations
 *        - 1             en=forward:/acme/en/index.html
 *        - 2             it=forward:/acme/it/index.html
 * </pre>
 * @author fgiust
 * @version $Id$
 */
public class LocaleBasedVirtualURIMapping implements VirtualURIMapping
{

    private String fromURI;

    private UrlPattern pattern;

    private String toURI;

    private Map<String, String> destinations = new HashMap<String, String>();

    /**
     *
     */
    public LocaleBasedVirtualURIMapping()
    {
        destinations = new HashMap<String, String>();
    }

    // required by content2bean in order to make addDestination work, do not remove!
    public List<String> getDestinations()
    {
        return null;
    }

    /**
     * Adds a locale mapping (used by content2bean).
     * @param mapping in the form locale=path
     */
    public void addDestination(String mapping)
    {
        String[] localeToPath = StringUtils.split(mapping, "=");
        if (localeToPath != null && localeToPath.length == 2)
        {
            synchronized (destinations)
            {
                destinations.put(localeToPath[0], localeToPath[1]);
            }
        }
    }

    public MappingResult mapURI(String uri)
    {

        String destination = toURI;

        if (pattern != null && pattern.match(uri))
        {

            WebContext webContext = MgnlContext.getWebContextOrNull();

            if (webContext != null)
            {
                String locale = webContext.getAggregationState().getLocale().toString();

                if (destinations != null)
                {

                    Iterator<Map.Entry<String, String>> destIt = destinations.entrySet().iterator();
                    while (destIt.hasNext())
                    {

                        Map.Entry<String, String> hk = destIt.next();
                        if (locale.startsWith(hk.getKey()))
                        {
                            destination = hk.getValue();
                            break;
                        }
                    }
                }
            }

            MappingResult r = new MappingResult();
            r.setLevel(pattern.getLength());
            r.setToURI(destination);
            return r;
        }
        return null;
    }

    public String getFromURI()
    {
        return this.fromURI;
    }

    public void setFromURI(String fromURI)
    {
        this.fromURI = fromURI;
        this.pattern = new SimpleUrlPattern(fromURI);
    }

    public String getToURI()
    {
        return this.toURI;
    }

    public void setToURI(String toURI)
    {
        this.toURI = toURI;
    }

    @Override
    public String toString()
    {
        return fromURI + " --> " + toURI;
    }

}
