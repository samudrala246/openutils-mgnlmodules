/**
 *
 * Simplecache module for Magnolia CMS (http://www.openmindlab.com/lab/products/simplecache.html)
 * Copyright(C) 2010-2013, Openmind S.r.l. http://www.openmindonline.it
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

package net.sourceforge.openutils.mgnlsimplecache.voters;

import info.magnolia.cms.beans.config.ServerConfiguration;
import info.magnolia.cms.security.UserManager;
import info.magnolia.context.MgnlContext;
import info.magnolia.objectfactory.Components;
import info.magnolia.voting.voters.AbstractBoolVoter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * <p>
 * An easy to use, all purpose voter for the cache filter
 * </p>
 * @author Manuel Molaschi
 * @author Fabrizio Giustina
 * @version $Id$
 */
public class AllInOneCacheVoter extends AbstractBoolVoter
{

    // just to make everything more explicit
    public static final boolean CACHE = false;

    // just to make everything more explicit
    public static final boolean DONTCACHE = true;

    /**
     * Comma separated list of allowed extensions.
     */
    protected String[] extensionsAllowed;

    /**
     * Comma separated list of denied extensions.
     */
    protected String[] extensionsDenied;

    /**
     * Comma separated list of allowed content types.
     */
    protected String[] contentTypesAllowed;

    /**
     * Comma separated list of denied content types.
     */
    protected String[] contentTypesDenied;

    /**
     * Cache requests with parameters?
     */
    protected boolean allowRequestWithParameters;

    /**
     * Cache content on admin instances?
     */
    protected boolean allowAdmin;

    /**
     * Cache content for authenticated users?
     */
    protected boolean allowAuthenticated;

    /**
     * Comma separated list of allowed start paths.
     */
    protected String[] pathsAllowed;

    /**
     * Comma separated list of denied start paths.
     */
    protected String[] pathsDenied;

    /**
     * Logger.
     */
    private Logger log = LoggerFactory.getLogger(AllInOneCacheVoter.class);

    /**
     * Sets a comma separated list of allowed extensions.
     * @param extensionsAllowed comma separated list of allowed extensions
     */
    public void setExtensionsAllowed(String extensionsAllowed)
    {
        this.extensionsAllowed = StringUtils.split(extensionsAllowed, ", ");
    }

    /**
     * Sets a comma separated list of denied extensions.
     * @param extensionsDenied comma separated list of denied extensions
     */
    public void setExtensionsDenied(String extensionsDenied)
    {
        this.extensionsDenied = StringUtils.split(extensionsDenied, ", ");
    }

    /**
     * Sets a comma separated list of allowed content types.
     * @param contentTypesAllowed comma separated list of allowed content types
     */
    public void setContentTypesAllowed(String contentTypesAllowed)
    {
        this.contentTypesAllowed = StringUtils.split(contentTypesAllowed, ", ");
    }

    /**
     * Sets a comma separated list of denied content types.
     * @param contentTypesDenied comma separated list of denied content types.
     */
    public void setContentTypesDenied(String contentTypesDenied)
    {
        this.contentTypesDenied = StringUtils.split(contentTypesDenied, ", ");
    }

    /**
     * Cache requests with parameters?
     * @param allowRequestWithParameters the allowRequestWithParameters to set
     */
    public void setAllowRequestWithParameters(boolean allowRequestWithParameters)
    {
        this.allowRequestWithParameters = allowRequestWithParameters;
    }

    /**
     * Cache content on admin instances?
     * @param allowAdmin the allowAdmin to set
     */
    public void setAllowAdmin(boolean allowAdmin)
    {
        this.allowAdmin = allowAdmin;
    }

    /**
     * Cache content for authenticated users?
     * @param allowAuthenticated the allowAuthenticated to set
     */
    public void setAllowAuthenticated(boolean allowAuthenticated)
    {
        this.allowAuthenticated = allowAuthenticated;
    }

    /**
     * Sets a comma separated list of allowed start paths.
     * @param pathsAllowed comma separated list of allowed start paths.
     */
    public void setPathsAllowed(String pathsAllowed)
    {
        this.pathsAllowed = StringUtils.split(pathsAllowed, ", ");
    }

    /**
     * Sets a comma separated list of denied start paths.
     * @param pathsDenied comma separated list of denied start paths.
     */
    public void setPathsDenied(String pathsDenied)
    {
        this.pathsDenied = StringUtils.split(pathsDenied, ", ");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean boolVote(Object value)
    {

        String uri = resolveURIFromValue(value);
        String logmessage = "[" + uri + "] not cacheable because {}";

        if (!allowAdmin)
        {
            if (Components.getComponent(ServerConfiguration.class).isAdmin())
            {
                log.debug(logmessage, "on admin server");
                return DONTCACHE;
            }
        }

        if (!allowAuthenticated)
        {
            if (!MgnlContext.getUser().getName().equals(UserManager.ANONYMOUS_USER))
            {
                log.debug(logmessage, "there is a logged in user");
                return DONTCACHE;
            }
        }

        HttpServletRequest request = MgnlContext.getWebContext().getRequest();
        if (!allowRequestWithParameters)
        {
            if (StringUtils.equalsIgnoreCase(request.getMethod(), "POST") || !request.getParameterMap().isEmpty())
            {
                log.debug(logmessage, "request has parameters");
                return DONTCACHE;
            }
        }

        String extension = MgnlContext.getAggregationState().getExtension();
        if ((extensionsAllowed.length > 0 && !ArrayUtils.contains(extensionsAllowed, extension))
            || (extensionsDenied.length > 0 && ArrayUtils.contains(extensionsDenied, extension)))
        {
            if (log.isDebugEnabled())
            {
                log.debug(
                    logmessage,
                    "extension ["
                        + extension
                        + "] doesn't match allowed:"
                        + ArrayUtils.toString(extensionsAllowed)
                        + " denied: "
                        + ArrayUtils.toString(extensionsDenied));
            }
            return DONTCACHE;
        }

        HttpServletResponse response = MgnlContext.getWebContext().getResponse();
        final String contentType = StringUtils.substringBefore(response.getContentType(), ";");

        if ((contentTypesAllowed.length > 0 && !ArrayUtils.contains(contentTypesAllowed, contentType))
            || (contentTypesDenied.length > 0 && ArrayUtils.contains(contentTypesDenied, contentType)))
        {
            if (log.isDebugEnabled())
            {
                log.debug(
                    logmessage,
                    "content type ["
                        + contentType
                        + "] doesn't match allowed:"
                        + ArrayUtils.toString(contentTypesAllowed)
                        + " denied: "
                        + ArrayUtils.toString(contentTypesDenied));
            }
            return DONTCACHE;
        }

        if ((pathsAllowed.length > 0 && !startsWithAny(pathsAllowed, uri))
            || (pathsDenied.length > 0 && startsWithAny(pathsDenied, uri)))
        {
            if (log.isDebugEnabled())
            {
                log.debug(logmessage, "path ["
                    + uri
                    + "] doesn't match allowed:"
                    + ArrayUtils.toString(pathsAllowed)
                    + " denied: "
                    + ArrayUtils.toString(pathsDenied));
            }
            return DONTCACHE;
        }

        log.debug("[{}] is cacheable!", uri);

        return CACHE;

    }

    protected boolean startsWithAny(String[] values, String start)
    {
        for (String string : values)
        {
            if (StringUtils.startsWith(start, string))
            {
                return true;
            }
        }
        return false;
    }

    protected String resolveURIFromValue(Object value)
    {
        String uri = null;
        if (value instanceof String)
        {
            uri = (String) value;
        }
        else
        {
            if (MgnlContext.hasInstance())
            {
                uri = MgnlContext.getAggregationState().getCurrentURI();
            }
            else
            {
                if (value instanceof HttpServletRequest)
                {
                    HttpServletRequest request = (HttpServletRequest) value;
                    uri = StringUtils.substringAfter(request.getRequestURI(), request.getContextPath());
                }
            }
        }
        return uri;
    }

}
