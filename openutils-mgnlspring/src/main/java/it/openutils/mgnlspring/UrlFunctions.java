/**
 *
 * Spring integration module for Magnolia CMS (http://openutils.sourceforge.net/openutils-mgnlspring)
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

package it.openutils.mgnlspring;

import info.magnolia.context.MgnlContext;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;


/**
 * @author fgiust
 * @version $Id:UrlFunctions.java 344 2007-06-30 15:31:28Z fgiust $
 */
public final class UrlFunctions
{

    /**
     * Don't instantiate.
     */
    private UrlFunctions()
    {
        // unused
    }

    public static String url(String url)
    {
        return encodeUrl(RewriteVarsThreadLocal.getCurrentPageUrl(), url);
    }

    public static String urlWithDestination(String url, String destination)
    {
        // check RewriteVarsThreadLocal because if not running under magnolia we should not replace urls
        if (RewriteVarsThreadLocal.getCurrentPageUrl() == null || destination == null)
        {
            return url(url);
        }

        return encodeUrl(RewriteVarsThreadLocal.getContextPath() + destination, url);
    }

    /**
     * @param baseUrl
     * @param actionUrl
     * @return
     */
    private static String encodeUrl(final String baseUrl, String actionUrl)
    {
        String contextPath = MgnlContext.getContextPath();
        if (StringUtils.isNotEmpty(contextPath) && actionUrl != null && actionUrl.startsWith(contextPath))
        {
            actionUrl = StringUtils.substringAfter(actionUrl, contextPath);
        }

        if (baseUrl == null)
        {
            if (actionUrl.startsWith("/"))
            {
                return contextPath + actionUrl;
            }

            return actionUrl;
        }

        int paramIndex = baseUrl.indexOf('?');
        String page = baseUrl;
        String params = null;
        if (paramIndex != -1)
        {
            page = StringUtils.substring(baseUrl, 0, paramIndex + 1);
            params = StringUtils.substring(baseUrl, paramIndex + 1);
        }

        StringBuffer outUrl = new StringBuffer();
        outUrl.append(page);
        outUrl.append("?");

        if (params != null)
        {
            outUrl.append(params);
            outUrl.append("&");
        }

        outUrl.append("_action=");
        outUrl.append(StringUtils.replace(actionUrl, "?", "&"));

        String encodedUrl = StringEscapeUtils.escapeXml(outUrl.toString());

        return encodedUrl;
    }

}
