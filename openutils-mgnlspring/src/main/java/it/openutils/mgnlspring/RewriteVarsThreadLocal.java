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

/**
 * @author fgiust
 * @version $Id:RewriteVarsThreadLocal.java 344 2007-06-30 15:31:28Z fgiust $
 */
public final class RewriteVarsThreadLocal
{

    private static ThreadLocal<String> currentPageUrl = new ThreadLocal<String>();

    private static ThreadLocal<String> contextPath = new ThreadLocal<String>();

    /**
     * Don't instantiate.
     */
    private RewriteVarsThreadLocal()
    {
        // unused
    }

    /**
     * Returns the currentPageUrl.
     * @return the currentPageUrl
     */
    public static String getCurrentPageUrl()
    {
        return currentPageUrl.get();
    }

    /**
     * Sets the currentPageUrl.
     * @param url the currentPageUrl to set
     */
    public static void setCurrentPageUrl(String url)
    {
        currentPageUrl.set(url);
    }

    /**
     * Returns the contextPath.
     * @return the contextPath
     */
    public static String getContextPath()
    {
        return contextPath.get();
    }

    /**
     * Sets the contextPath.
     * @param context the contextPath to set
     */
    public static void setContextPath(String context)
    {
        contextPath.set(context);
    }

}
