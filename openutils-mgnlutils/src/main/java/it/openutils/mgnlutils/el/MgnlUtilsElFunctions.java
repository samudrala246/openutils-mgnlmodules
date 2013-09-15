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

package it.openutils.mgnlutils.el;

import info.magnolia.cms.beans.config.ServerConfiguration;
import info.magnolia.cms.core.AggregationState;
import info.magnolia.cms.core.MgnlNodeType;
import info.magnolia.cms.core.Path;
import info.magnolia.cms.i18n.I18nContentSupport;
import info.magnolia.cms.i18n.MessagesManager;
import info.magnolia.cms.security.Permission;
import info.magnolia.cms.security.PermissionUtil;
import info.magnolia.cms.security.SecurityUtil;
import info.magnolia.cms.security.User;
import info.magnolia.cms.security.auth.Entity;
import info.magnolia.context.MgnlContext;
import info.magnolia.context.WebContext;
import info.magnolia.init.MagnoliaConfigurationProperties;
import info.magnolia.jaas.principal.EntityImpl;
import info.magnolia.jcr.util.ContentMap;
import info.magnolia.jcr.util.NodeUtil;
import info.magnolia.link.LinkException;
import info.magnolia.link.LinkUtil;
import info.magnolia.objectfactory.Components;
import info.magnolia.repository.RepositoryConstants;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import javax.jcr.ItemNotFoundException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.security.auth.Subject;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.jackrabbit.util.ISO9075;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Generic utility EL functions.
 * @author fgiust
 * @version $Id$
 */
public final class MgnlUtilsElFunctions
{

    /**
     * Logger.
     */
    private static Logger log = LoggerFactory.getLogger(MgnlUtilsElFunctions.class);

    // public constructor required for freemarker support
    public MgnlUtilsElFunctions()
    {
    }

    /**
     * Test if exists a parent page with content in the collectionName given as parameter, if exist set the parent page
     * as actpage
     * @param collectionName (column) of magnolia contentNode
     * @return true if exists a parent page with content in the collectionName given as parameter, if exist set the
     * parent page as actpage
     */
    public static boolean firstPageWithCollection(String collectionName)
    {
        Node actpage = MgnlUtilsDeprecatedAdapters.getCurrentContent();
        if (actpage == null)
        {
            return false;
        }

        try
        {
            while (actpage.getDepth() > 1)
            {
                actpage = actpage.getParent();

                if (actpage.hasNode(collectionName) && actpage.getNode(collectionName).hasNodes())
                {
                    MgnlUtilsDeprecatedAdapters.setCurrentContent(actpage);
                    return true;
                }
            }
        }
        catch (RepositoryException e)
        {
            log.error("{} looking for collection {} in {}: {}", new Object[]{
                e.getClass().getName(),
                collectionName,
                NodeUtil.getPathIfPossible(actpage),
                e.getMessage() });
        }

        return false;
    }

    /**
     * Return the content of a given path and repository
     * @param path content path
     * @param repository repository type
     * @return the content of a given path and repository
     */
    public static ContentMap contentByPath(String path, String repository)
    {
        if (path == null || repository == null)
        {
            return null;
        }
        try
        {
            Node node = MgnlContext.getJCRSession(repository).getNode(path);
            if (node != null)
            {
                return wrapNode(node);
            }
        }
        catch (RepositoryException e)
        {
            log.debug("{} loading path {} from workspace {}:{}", new Object[]{
                e.getClass().getName(),
                path,
                repository,
                e.getMessage() });
        }
        return null;
    }

    /**
     * Return a map key=value of all magnolia request attributes
     * @return a map key=value of all magnolia request attributes
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> getRequestAttributeMap()
    {

        Map<String, Object> attrs = new HashMap<String, Object>();
        HttpServletRequest request = MgnlContext.getWebContext().getRequest();
        Enumeration<String> attributeNames = request.getAttributeNames();

        while (attributeNames.hasMoreElements())
        {
            String key = attributeNames.nextElement();
            attrs.put(key, request.getAttribute(key));
            attrs.put(key, key);
        }

        return attrs;
    }

    /**
     * Return the message of a given message key, localized according to the content i18n settings.
     * @param key string
     * @return return the message string of a given message key
     */
    public static String message(String key)
    {
        String value = MessagesManager.getMessages(Components.getComponent(I18nContentSupport.class).getLocale()).get(
            key);

        return value;
    }

    /**
     * Return the message of a given message key, formatted with the given arguments and localized according to the
     * content i18n settings.
     * @param key string
     * @return return the message string of a given message key
     */
    public static String messageWithArgs(String key, Object[] arguments)
    {
        String value = MessagesManager.getMessages(Components.getComponent(I18nContentSupport.class).getLocale()).get(
            key,
            arguments);

        return value;
    }

    /**
     * Test the system property 'magnolia.develop'
     * @return true if the system property = 'magnolia.develop'
     */
    public static boolean develop()
    {
        return Components.getComponent(MagnoliaConfigurationProperties.class).getBooleanProperty("magnolia.develop");
    }

    /**
     * Parse uuidOrPathOrUrl string and return a url.
     * @param uuidOrPathOrUrl url (http://, internal link, UUID, generic url)
     * @return cleaned url (external or internal)
     */
    public static String link(String uuidOrPathOrUrl)
    {
        String cleanedurl = StringUtils.replace(StringUtils.trim(uuidOrPathOrUrl), "&", "&amp;");
        String contextPath = ((WebContext) MgnlContext.getInstance()).getContextPath();

        if (StringUtils.isBlank(cleanedurl))
        {
            return contextPath;
        }

        if (cleanedurl.startsWith("http") || cleanedurl.startsWith("#"))
        {
            return cleanedurl;
        }

        // Check if there is already an extensions, else add default one
        if (cleanedurl.startsWith("/"))
        {
            String configuredExtension = Components.getComponent(ServerConfiguration.class).getDefaultExtension();

            if (StringUtils.isNotBlank(configuredExtension))
            {

                String defaultExtension = "." + configuredExtension;
                cleanedurl = Components.getComponent(I18nContentSupport.class).toI18NURI(cleanedurl);
                cleanedurl = contextPath + cleanedurl;

                if (!cleanedurl.endsWith(defaultExtension) && cleanedurl.indexOf(".") < 0)
                {
                    return cleanedurl + defaultExtension;
                }
            }

            return cleanedurl;
        }

        // Check if uuidOrPathOrUrl is an UUID
        try
        {
            cleanedurl = MgnlContext.getContextPath()
                + LinkUtil.convertUUIDtoURI(cleanedurl, RepositoryConstants.WEBSITE);
        }
        catch (LinkException e)
        {
            log.debug("Failed to parse links with from "
                + MgnlContext.getAggregationState().getCurrentURI()
                + e.getMessage());
        }

        // If got an error return the cleaned string
        return cleanedurl;
    }

    /**
     * Create an html complete link from a string composed by link \t link text. If the link is empty the function
     * return only the text
     * @param tabseparatedstring string
     * @return a link or a text
     */
    public static String tolinkOrText(String tabseparatedstring)
    {

        if (StringUtils.isBlank(tabseparatedstring))
        {
            return StringUtils.EMPTY;
        }

        String[] splitted = StringUtils.split(tabseparatedstring, "\t");

        if (splitted.length > 1)
        {
            String url = link(splitted[1]);

            boolean external = false;
            if (splitted.length > 2 && "true".equals(StringUtils.trim(splitted[2])))
            {
                external = true;
            }
            StringBuilder sb = new StringBuilder();
            if (!StringUtils.isBlank(url))
            {
                sb.append("<a href=\"");
                sb.append(url);
                if (external)
                {
                    sb.append("\" class=\"external");
                }
                sb.append("\">");

                sb.append(splitted[0]);
                sb.append("</a>");
            }
            else
            {
                sb.append(splitted[0]);
            }

            return sb.toString();
        }
        // param external
        if (splitted[0].equals("false") || splitted[0].equals("true"))
        {
            return StringUtils.EMPTY;
        }
        else
        {
            return splitted[0];
        }
    }

    /**
     * Count the nodes in a collection with a given content and the name of the collection
     * @param content Content
     * @param subnode String
     * @return the number of nodes in a collection
     */
    public static int countNodesInCollection(Object content, String subnode)
    {
        Node node = MgnlUtilsDeprecatedAdapters.toNode(content);
        int count = 0;

        if (content != null)
        {

            try
            {
                if (node.hasNode(subnode))
                {
                    Node collection = node.getNode(subnode);
                    Iterator<Node> iterator = NodeUtil
                        .getNodes(node, collection.getPrimaryNodeType().getName())
                        .iterator();

                    while (iterator.hasNext())
                    {
                        count++;
                    }
                }
            }
            catch (RepositoryException e)
            {
                // ignore
            }
        }

        return count;
    }

    /**
     * Count subpages with a given content
     * @param content Content
     * @return the number of subpages
     */
    public static int countSubpages(Object content)
    {
        Node node = MgnlUtilsDeprecatedAdapters.toNode(content);
        if (node == null)
        {
            return 0;
        }

        int count = 0;

        try
        {

            Iterator<Node> iterator = NodeUtil.getNodes(node, MgnlNodeType.NT_PAGE).iterator();

            while (iterator.hasNext())
            {
                count++;
            }
        }
        catch (RepositoryException e)
        {
            log.debug(e.getMessage());
        }

        return count;
    }

    /**
     * Return the collection of subpages of a given page
     * @param content Content
     * @return a Collection<Content> of subpages of a given page
     * @deprecated avoid methods that works with collections, use iterators
     */
    @Deprecated
    public static Collection<ContentMap> subpages(Object content)
    {
        Node node = MgnlUtilsDeprecatedAdapters.toNode(content);
        if (node == null)
        {
            return Collections.EMPTY_LIST;
        }

        Iterable<Node> nodes;
        try
        {
            nodes = NodeUtil.getNodes(node, MgnlNodeType.NT_PAGE);
        }
        catch (RepositoryException e)
        {
            return Collections.EMPTY_LIST;
        }
        Iterator<Node> iterator = nodes.iterator();

        Collection<ContentMap> result = new ArrayList<ContentMap>();
        while (iterator.hasNext())
        {
            ContentMap contentMap = wrapNode(iterator.next());
            if (contentMap != null)
            {
                result.add(contentMap);
            }
        }

        return result;
    }

    /**
     * Return true if the current user has a given role
     * @param role String
     * @return boolean value
     */
    public static boolean userInRole(String role)
    {
        return MgnlContext.getUser().hasRole(role);
    }

    /**
     * Evaluates if primary node type of the associated Node of an object is a mgnl:contentNode
     * @param content Content
     * @return boolean value
     */
    public static boolean isPage(Object content)
    {
        Node node = MgnlUtilsDeprecatedAdapters.toNode(content);
        if (node != null)
        {
            try
            {
                return NodeUtil.isNodeType(node, MgnlNodeType.NT_PAGE);
            }
            catch (RepositoryException e)
            {
                // ignore
            }
        }
        return false;
    }

    /**
     * Return the Content of the object passed or of the first parent page that has content type = mgnl:content
     * @param content Content
     * @return the Content of the object passed or of the first parent page that has content type = mgnl:content
     */
    public static ContentMap getPage(Object content)
    {

        Node node = MgnlUtilsDeprecatedAdapters.toNode(content);

        try
        {
            while (node != null && !NodeUtil.isNodeType(node, MgnlNodeType.NT_PAGE))
            {
                if (node.getDepth() <= 1)
                {
                    node = null;
                }
                else
                {
                    node = node.getParent();
                }
            }
        }
        catch (RepositoryException e)
        {
            log.debug(
                "Got a {} while loading parent of {}: {}",
                new Object[]{e.getClass().getName(), NodeUtil.getPathIfPossible(node), e.getMessage() });
            return null;
        }

        if (node != null)
        {
            return wrapNode(node);
        }
        return null;
    }

    /**
     * retrieve validate label for input string
     * @param value the string to validate
     * @return the validated label, or null if value was null
     * @see {@link Path#getValidatedLabel(String)}
     */
    public static String getValidatedLabel(String value)
    {
        if (value == null)
        {
            return null;
        }
        return Path.getValidatedLabel(value);
    }

    /**
     * splits a list according to a separator and returns true if one of the values equals the input value
     * @param list the string to be split
     * @param value the value to check
     * @param separator the separator
     * @return true if the value equals one of the values obtained from splitting the string, false otherwise
     * @see {@link String#split()}
     */
    public static boolean isStringInSeparatedList(String list, String value, String separator)
    {
        if (StringUtils.isNotEmpty(list) && StringUtils.isNotEmpty(value))
        {
            String[] listValues = list.split(separator);
            for (String v : listValues)
            {
                if (v.equals(value))
                {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * check if the second argument equals at least one line of the first argument
     * @param list the list of lines
     * @param value the value to check
     * @return true if the value equals one of the lines, false otherwise
     */
    public static boolean isLine(String list, String value)
    {
        return isStringInSeparatedList(list, value, "\r\n") || isStringInSeparatedList(list, value, "\n");
    }

    /**
     * encode handle for a JackRabbit search
     * @param handle the page handle
     * @return handle encoded ISO9075
     */
    public static String encodeISO9075(String handle)
    {
        return ISO9075.encodePath(handle);
    }

    /**
     * Splits the given strings on newline (<code>\n</code>) and after on tabs (<code>\t</code>) Usually used to
     * retrieve data from a magnolia grid component
     * @param string string to be split
     * @return array
     */
    public static String[][] splitAndTokenize(String string)
    {
        List<String[]> list = new ArrayList<String[]>();
        for (String line : StringUtils.splitPreserveAllTokens(string, '\n'))
        {
            line = StringUtils.removeEnd(line, "\r");
            if (StringUtils.isNotBlank(line))
            {
                list.add(StringUtils.splitPreserveAllTokens(line, '\t'));
            }
        }
        return list.toArray(new String[0][]);
    }

    /**
     * Extracts a map from a bi-dimensional array of strings, getting keys and values from the specified columns
     * @param tokens
     * @param keyIndex index of the column storing keys
     * @param valueIndex index of the column storing values
     * @return
     */
    public static Map<String, String> mapTokens(String[][] tokens, int keyIndex, int valueIndex)
    {
        Map<String, String> map = new HashMap<String, String>(tokens.length);
        for (String[] row : tokens)
        {
            if (keyIndex < row.length && valueIndex < row.length)
            {
                map.put(row[keyIndex], row[valueIndex]);
            }
        }
        return map;
    }

    /**
     * Same as mapTokens, but the resulting map is multi-value, i.e. the same key can have more than one value. Rows
     * having no key specified are considered entries of the last used key.
     * @param tokens
     * @param keyIndex
     * @param valueIndex
     * @return
     */
    public static Map<String, String[]> multiMapTokens(String[][] tokens, int keyIndex, int valueIndex)
    {
        Map<String, List<String>> tmpMap = new HashMap<String, List<String>>();
        String currentKey = null;
        for (String[] row : tokens)
        {
            if (keyIndex < row.length && valueIndex < row.length)
            {
                String key = StringUtils.trimToNull(row[keyIndex]);
                if (key == null)
                {
                    key = currentKey;
                }
                if (key != null)
                {
                    List<String> values = tmpMap.get(key);
                    if (values == null)
                    {
                        values = new ArrayList<String>();
                        tmpMap.put(key, values);
                    }
                    values.add(row[valueIndex]);
                }
            }
        }
        Map<String, String[]> map = new HashMap<String, String[]>(tmpMap.size());
        for (Entry<String, List<String>> entry : tmpMap.entrySet())
        {
            map.put(entry.getKey(), entry.getValue().toArray(new String[0]));
        }
        return map;
    }

    /**
     * Returns the base URL for the request (scheme + server + port + context path, like http://server:81/context/)
     * @return the base URL for the request (scheme + server + port + context path, like http://server:81/context/
     */
    public static String baseUrl()
    {
        return baseUrlWithoutContextPath() + MgnlContext.getWebContext().getRequest().getContextPath();
    }

    /**
     * Convert a relative url to absolute, using the current base url. If the link is already absolute it will not be
     * altered.
     * @param relativeOrAbsolute input url, considered absolute if it contains the protocol separator ("://")
     * @return absolute url
     */
    public static String toAbsoluteUrl(String relativeOrAbsolute)
    {
        if (StringUtils.isBlank(relativeOrAbsolute))
        {
            return relativeOrAbsolute;
        }

        // check for :// after protocol
        if (StringUtils.contains(StringUtils.substring(relativeOrAbsolute, 0, 10), "://"))
        {
            return relativeOrAbsolute;
        }

        return baseUrl() + relativeOrAbsolute;
    }

    /**
     * Returns the base URL for the request without the context path (scheme + server + port, like http://server:81/)
     * @return the base URL for the request (scheme + server + port , like http://server:81/
     */
    public static String baseUrlWithoutContextPath()
    {
        HttpServletRequest request = MgnlContext.getWebContext().getRequest();

        StringBuffer baseUrl = new StringBuffer();
        baseUrl.append(request.getScheme());
        baseUrl.append("://");
        baseUrl.append(request.getServerName());

        if (("http".equals(request.getScheme()) && request.getServerPort() != 80)
            || ("https".equals(request.getScheme()) && request.getServerPort() != 443))
        {
            baseUrl.append(":");
            baseUrl.append(request.getServerPort());
        }

        return baseUrl.toString();
    }

    /**
     * Returns the full url to the given content (starting with http)
     * @param content magnolia content
     * @return the full url to the given content (starting with http)
     */
    public static String pageFullUrl(Object content)
    {

        Node node = MgnlUtilsDeprecatedAdapters.toNode(content);

        if (node == null)
        {
            return null;
        }

        return baseUrl() + NodeUtil.getPathIfPossible(node) + ".html";

    }

    /**
     * Returns the full url to the current page (starting with http)
     * @return the full url to the current page (starting with http)
     */
    public static String activePageFullUrl()
    {
        try
        {
            Node mainContent = MgnlUtilsDeprecatedAdapters.getMainContent();
            if (mainContent == null)
            {
                return null;
            }

            return baseUrl()
                + LinkUtil.convertUUIDtoURI(mainContent.getIdentifier(), MgnlContext
                    .getAggregationState()
                    .getRepository());
        }
        catch (RepositoryException e)
        {
            log.warn("Error generating link", e);
        }
        catch (LinkException e)
        {
            log.warn("Error generating link", e);
        }
        return null;
    }

    /**
     * Returns a link from an uuid. Accepts in input uuid. Returns "#" if provided uuid is not found.
     * @param uuid uuid to find
     * @param repo repository within search - can be null - default is 'website'
     * @return a link from an uuid.
     */
    public static String repoUuidLink(String uuid, String repo)
    {
        String url = "#";
        try
        {
            url = MgnlContext.getContextPath()
                + LinkUtil.convertUUIDtoURI(uuid, StringUtils.isNotBlank(repo) ? repo : RepositoryConstants.WEBSITE);
        }
        catch (LinkException e)
        {
            log.debug("Failed to parse links with from "
                + MgnlContext.getAggregationState().getCurrentURI()
                + e.getMessage());
        }

        return url;
    }

    /**
     * Convert a content list into a collection, also wrapping the content inside a I18NNodeMapWrapper
     * @param list the list to be converted
     * @return a collection with the user's content
     */
    @Deprecated
    public static List<ContentMap> convertToCollection(List<Object> list)
    {
        List<ContentMap> result = new ArrayList<ContentMap>();

        for (Object content : list)
        {
            ContentMap contentMap = wrapNode(MgnlUtilsDeprecatedAdapters.toNode(content));
            if (contentMap != null)
            {
                result.add(contentMap);
            }
        }
        return result;
    }

    /**
     * Check if the content parameter has a child with title equal the title parameter
     * @param content
     * @param title
     * @return
     */
    public static Boolean hasChildWithTitle(Object content, String title)
    {

        Node node = MgnlUtilsDeprecatedAdapters.toNode(content);

        if (node == null)
        {
            return false;
        }
        try
        {
            NodeIterator children = node.getNodes();

            while (children.hasNext())
            {
                Node child = (Node) children.next();

                if (child.hasProperty("title")
                    && StringUtils.equalsIgnoreCase(child.getProperty("title").getString(), title))
                {
                    return true;
                }
            }
        }
        catch (RepositoryException e)
        {
            log.debug(e.getMessage());
        }

        return false;
    }

    /**
     * Get child nodes of specified content type and its subtypes
     * @param content
     * @param contentType
     * @return
     */
    public static Collection<ContentMap> contentChildrenOfType(Object content, String contentType)
    {

        Node node = MgnlUtilsDeprecatedAdapters.toNode(content);
        if (node == null)
        {
            return Collections.EMPTY_LIST;
        }

        Iterable<Node> nodes;
        try
        {
            nodes = NodeUtil.getNodes(node, contentType);
        }
        catch (RepositoryException e)
        {
            log.debug(e.getMessage());
            return Collections.EMPTY_LIST;
        }
        Iterator<Node> iterator = nodes.iterator();

        Collection<ContentMap> result = new ArrayList<ContentMap>();
        while (iterator.hasNext())
        {
            ContentMap contentMap = wrapNode(iterator.next());
            if (contentMap != null)
            {
                result.add(contentMap);
            }
        }

        return result;
    }

    /**
     * Builds a query string with the actual request parameters, optionally excluding a list of parameters.
     * @param excludedParamsWhitespaceSeparated list of parameters to exclude, in a single whitespace separated string
     * @return queryString, without the leading "?" and with parameters already urlencoded (but with ampersands not html
     * escaped)
     */
    public static String buildQuerystringExcluding(String excludedParamsWhitespaceSeparated)
    {
        return buildQuerystringInternal(
            StringUtils.split(StringUtils.defaultString(excludedParamsWhitespaceSeparated), ' '),
            new String[0]);
    }

    /**
     * Builds a query string with the actual request parameters, optionally excluding a list of parameters
     * @param includedParamsWhitespaceSeparated list of parameters to include, in a single whitespace separated string
     * @return queryString, without the leading "?" and with parameters already urlencoded (but with ampersands not html
     * escaped)
     */
    public static String buildQuerystringIncluding(String includedParamsWhitespaceSeparated)
    {
        return buildQuerystringInternal(
            new String[0],
            StringUtils.split(StringUtils.defaultString(includedParamsWhitespaceSeparated), ' '));
    }

    /**
     * @param excluded
     * @return
     */
    private static String buildQuerystringInternal(String[] excluded, String[] included)
    {
        Map<String, String> parameters = MgnlContext.getParameters();
        StringBuffer sb = new StringBuffer();

        boolean first = true;

        for (Map.Entry<String, String> entry : parameters.entrySet())
        {
            String key = entry.getKey();
            if (!ArrayUtils.contains(excluded, key)
                && (included == null || included.length == 0 || ArrayUtils.contains(included, key)))
            {
                if (!first)
                {
                    sb.append("&");
                }

                String[] parameterValues = MgnlContext.getParameterValues(key);

                if (parameterValues != null)
                {
                    for (int j = 0; j < parameterValues.length; j++)
                    {
                        String value = parameterValues[j];
                        sb.append(urlencode(key));
                        sb.append("=");
                        sb.append(urlencode(value));
                        if (j + 1 < parameterValues.length)
                        {
                            sb.append("&");
                        }
                    }
                }

                first = false;
            }
        }

        return sb.toString();
    }

    /**
     * Encodes a url in UTF-8 format
     * @param string url to be encoded
     * @return a url UTF-8 encoded
     */
    private static String urlencode(String string)
    {
        try
        {
            return URLEncoder.encode(string, "UTF-8");
        }
        catch (UnsupportedEncodingException e)
        {
            // should never happen
            return string;
        }
    }

    /**
     * @param obj
     * @param repo
     * @return
     * @deprecated, use node()
     */
    @Deprecated
    public static ContentMap content(Object obj, String repo)
    {

        return node(obj, repo);
    }

    /**
     * @param obj
     * @param repo
     * @return
     */
    public static ContentMap node(Object obj, String repo)
    {

        if (obj == null)
        {
            return null;
        }

        ContentMap content = null;

        if (obj instanceof String)
        {
            String identifier = (String) obj;

            if (StringUtils.isBlank(identifier))
            {
                return null;
            }

            try
            {
                Session session = MgnlContext.getJCRSession(repo);
                if (identifier.startsWith("/"))
                {
                    if (session.nodeExists(identifier))
                    {
                        content = wrapNode(session.getNode(identifier));
                    }
                }
                else
                {
                    content = wrapNode(session.getNodeByIdentifier(StringUtils.trim(identifier)));
                }
            }
            catch (ItemNotFoundException e)
            {
                log.debug("Node \"" + identifier + "\" not found");
            }
            catch (RepositoryException e)
            {
                log.error(e.getClass().getName() + " getting node \"" + identifier + "\"", e);
            }
        }
        else
        {
            content = wrapNode(MgnlUtilsDeprecatedAdapters.toNode(content));
        }

        return content;
    }

    /**
     * Get a user property calling MgnlContext.getUser().getProperty(property)
     * @param property property name
     * @return
     */
    public static Object userPropertyCurrent(String property)
    {
        return MgnlContext.getUser().getProperty(property);
    }

    /**
     * Get a user property calling getProperty(property) on the given User object.
     * @param user info.magnolia.cms.security.User instance
     * @param property property name
     * @return
     */
    public static Object userProperty(User user, String property)
    {
        if (user == null)
        {
            return null;
        }
        return user.getProperty(property);
    }

    /**
     * Returns a Map with all the properties of the current user, taken from the current jaas Principal (note: this uses
     * info.magnolia.jaas.principal.EntityImpl internals)
     * @return
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> userProperties()
    {
        Subject subject = MgnlContext.getSubject();

        Set<Entity> principalDetails = subject.getPrincipals(Entity.class);
        Iterator<Entity> entityIterator = principalDetails.iterator();
        Entity userDetails = entityIterator.next();

        if (userDetails instanceof EntityImpl)
        {
            try
            {
                Field properties = userDetails.getClass().getDeclaredField("properties");
                properties.setAccessible(true);
                Object value = properties.get(userDetails);
                return (Map<String, Object>) value;
            }
            catch (Exception e)
            {
                log.error(e.getMessage(), e);
            }
        }

        return null;
    }

    /**
     * Returns the value of a page property, by iterating on active page ancestor till a value is found
     * @param property property name
     * @return property value
     */
    public static String pagePropertyInherited(String property)
    {
        Node node = MgnlUtilsDeprecatedAdapters.getMainContent();

        try
        {
            while (node.getDepth() >= 1)
            {
                if (node.hasProperty(property))
                {

                    String value = node.getProperty(property).getString();

                    if (!StringUtils.isEmpty(value))
                    {
                        return value;
                    }

                }
                node = node.getParent();
            }
        }
        catch (RepositoryException e)
        {
            log.warn("{} caught while reading property {} from {}: {}", new Object[]{
                e.getClass().getName(),
                property,
                NodeUtil.getPathIfPossible(MgnlUtilsDeprecatedAdapters.getMainContent()),
                e.getMessage() });
        }

        return null;
    }

    /**
     * Sets the given content as the active page, calling MgnlContext.getAggregationState().setCurrentContent)
     * @param content current content to set
     */
    public static void setActivePage(Object content)
    {
        MgnlUtilsDeprecatedAdapters.setCurrentContent(content);
    }

    /**
     * Check if the current user can edit the active page.
     * @return true if the current user can edit the active page.
     */
    public static boolean canEdit()
    {
        Node mainContent = MgnlUtilsDeprecatedAdapters.getMainContent();

        if (mainContent != null)
        {
            return NodeUtil.isGranted(mainContent, Permission.SET);
        }
        return false;
    }

    /**
     * Get a node by its UUID, wrapped as ContentMap.
     * @param uuid content UUID
     * @param repo workspace name
     * @return ContentMap or null if not found
     */
    public static ContentMap contentByUUID(String uuid, String repo) throws RepositoryException
    {
        if (StringUtils.isBlank(uuid))
        {
            return null;
        }

        Session session;
        try
        {
            session = MgnlContext.getJCRSession(repo);
            Node loaded = session.getNodeByIdentifier(uuid);

            if (loaded != null)
            {
                return wrapNode(loaded);
            }
        }
        catch (ItemNotFoundException e)
        {
            // ignore
        }

        return null;
    }

    /**
     * Check if the current user belongs the given role
     * @param role role name
     * @return true if the user belongs to the role
     */
    public static boolean userHasRole(String role)
    {
        return MgnlContext.getUser().getAllRoles().contains(role);
    }

    /**
     * Check if the current user belongs the given group
     * @param group group name
     * @return true if the user belongs to the group
     */
    public static boolean userHasGroup(String group)
    {
        return MgnlContext.getUser().getAllGroups().contains(group);
    }

    /**
     * Returns the current active page (can be set using the loadPage tag).
     * @return current page
     */
    public static ContentMap currentPage()
    {
        return wrapNode(MgnlUtilsDeprecatedAdapters.getCurrentContent());
    }

    /**
     * Returns the main loaded page (doesn't change when using the loadPage tag).
     * @return loaded page
     */
    public static ContentMap mainPage()
    {
        return wrapNode(MgnlUtilsDeprecatedAdapters.getMainContent());

    }

    /**
     * Returns the current paragraph.
     * @return current paragraph
     */
    public static ContentMap currentParagraph()
    {
        return wrapNode(MgnlUtilsDeprecatedAdapters.getCurrentContent());
    }

    /**
     * Returns the value of a system property.
     * @param key property key
     * @return property value
     */
    public static String systemProperty(String key)
    {
        return Components.getComponent(MagnoliaConfigurationProperties.class).getProperty(key);
    }

    /**
     * Returns the system properties.
     * @return Property instance
     */
    public static Properties systemProperties()
    {
        return MgnlUtilsDeprecatedAdapters.systemProperties();
    }

    /**
     * Check if a user is currently logged in (not anonymous).
     * @return true if a user is currently logged in.
     */
    public static boolean isLoggedIn()
    {
        return SecurityUtil.isAuthenticated();
    }

    /**
     * Check if the current page is open in editing mode. Shortcut for checking if the server is admin, preview unset,
     * permissions to modify the page available for the current user.
     * @return true if the page is open in edit mode and user has permissions to edit
     */
    public static boolean isEditMode()
    {
        Node activePage = MgnlUtilsDeprecatedAdapters.getMainContent();
        if (activePage == null)
        {
            return false;
        }

        AggregationState aggregationState = MgnlUtilsDeprecatedAdapters.getAggregationStateIfAvailable();

        if (aggregationState == null)
        {
            return false;
        }

        try
        {
            return Components.getComponent(ServerConfiguration.class).isAdmin()
                && !aggregationState.isPreviewMode()
                && PermissionUtil.isGranted(activePage, Permission.SET);
        }
        catch (RepositoryException e)
        {
            return false;
        }
    }

    /**
     * Find and load the first parent page containing a named node. This function can be useful while building pages
     * that should inherit a paragraph from parent pages. Loaded page must be unloaded using the
     * <code>&lt;cms:unloadPage /></code> tag. Sample use:
     * 
     * <pre>
     * &lt;c:if test="${cmsfn:firstPageWithNode("column", 3)}">
     *      content inherited from page ${cmsfn:currentPage().handle}.html
     *   &lt;cms:contentNodeIterator contentNodeCollectionName="column">
     *     &lt;cms:includeTemplate />
     *   &lt;/cms:contentNodeIterator>
     *   &lt;cms:unloadPage />
     * &lt;/c:if>
     * </pre>
     * @param nodeName paragraph name
     * @param minlevel level at which we will stop also if no page is found
     * @return <code>true</code> if a page has been found and loaded, <code>false</code> otherwise
     */
    public static boolean firstPageWithNode(String nodeName, int minlevel)
    {
        Node node = MgnlUtilsDeprecatedAdapters.getCurrentContent();
        if (node == null)
        {
            node = MgnlUtilsDeprecatedAdapters.getMainContent();
        }

        try
        {
            while (node.getDepth() > minlevel)
            {
                node = node.getParent();

                if (node.hasNode(nodeName))
                {
                    MgnlUtilsDeprecatedAdapters.setCurrentContent(node);
                    return true;
                }
            }
        }
        catch (RepositoryException e)
        {
            log.error("Error looking for node " + nodeName + " in " + NodeUtil.getPathIfPossible(node), e);
        }

        return false;
    }

    /**
     * Function to iterate over a node Data that has "checkbox" as control type, for example. See
     * http://jira.magnolia-cms.com/browse/MAGNOLIA-1969
     * @deprecated use standard jcr Node methods, based on iterators instead of collections
     */
    @Deprecated
    public static Collection<Property> nodeDataIterator(Object c, String collection)
    {
        Node node = MgnlUtilsDeprecatedAdapters.toNode(c);
        try
        {
            if (node.hasNode(collection))
            {
                return Collections.EMPTY_LIST;
            }
            PropertyIterator properties = node.getNode(collection).getProperties();

            Collection<Property> result = new ArrayList<Property>();
            while (properties.hasNext())
            {
                result.add(properties.nextProperty());
            }

            return result;
        }
        catch (RepositoryException e)
        {
            log.error(
                "Error when getting nodedata collection from " + c + " / " + collection + " :" + e.getMessage(),
                e);
            return null;
        }
    }

    public static ContentMap ancestor(Object nodeorcontent, int level)
    {

        Node node = MgnlUtilsDeprecatedAdapters.toNode(nodeorcontent);

        if (node == null)
        {
            return null;
        }

        try
        {
            if (node.getDepth() < level)
            {
                return null;
            }
            if (node.getDepth() == level)
            {
                return wrapNode(node);
            }

            return wrapNode((Node) node.getAncestor(level));
        }
        catch (RepositoryException e)
        {
            log.debug("Got a {} while loading level {} starting from {}: {}", new Object[]{
                e.getClass().getName(),
                level,
                NodeUtil.getPathIfPossible(node),
                e.getMessage() });
            return null;
        }

    }

    private static ContentMap wrapNode(Node node)
    {
        if (node == null)
        {
            return null;
        }
        return new ContentMap(node);
    }
}
