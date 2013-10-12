/**
 *
 * Tagcloud module for Magnolia CMS (http://www.openmindlab.com/lab/products/mgnltagcloud.html)
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

package net.sourceforge.openutils.mgnltagcloud.util;

import info.magnolia.context.SystemContext;
import info.magnolia.jcr.RuntimeRepositoryException;
import info.magnolia.objectfactory.Components;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Singleton;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.InvalidQueryException;

import org.apache.commons.lang.UnhandledException;
import org.apache.jackrabbit.core.RepositoryImpl;
import org.apache.jackrabbit.core.SearchManager;
import org.apache.jackrabbit.core.SessionImpl;
import org.apache.jackrabbit.core.query.lucene.LuceneQueryBuilder;
import org.apache.jackrabbit.core.query.lucene.SearchIndex;
import org.apache.jackrabbit.spi.commons.query.QueryNodeFactory;
import org.apache.jackrabbit.spi.commons.query.QueryParser;
import org.apache.jackrabbit.spi.commons.query.QueryRootNode;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Utility class to get internals on jackrabbit
 * @author molaschi
 * @version $Id$
 */
@Singleton
public class JackrabbitUtil
{

    private static final String LANG_SQL = "sql";

    /**
     * Logger.
     */
    private Logger log = LoggerFactory.getLogger(JackrabbitUtil.class);

    private Method getSearchManager;

    private Method getQueryNodeFactory;

    /**
     * {@inheritDoc}
     */
    public JackrabbitUtil()
    {
        try
        {
            getSearchManager = RepositoryImpl.class.getDeclaredMethod("getSearchManager", String.class);
            getQueryNodeFactory = SearchIndex.class.getDeclaredMethod("getQueryNodeFactory");

            getSearchManager.setAccessible(true);
            getQueryNodeFactory.setAccessible(true);
        }
        catch (SecurityException e)
        {
            log.error("Exception getting SearchManager", e);
        }
        catch (NoSuchMethodException e)
        {
            log.error("Exception getting SearchManager", e);
        }
    }

    /**
     * Get jcr session
     * @param repository repository
     * @return jcr session
     */
    public Session getSession(String repository)
    {
        try
        {
            return Components.getComponent(SystemContext.class).getJCRSession(repository);
        }
        catch (RepositoryException e)
        {
            throw new RuntimeRepositoryException(e);
        }
    }

    /**
     * Get jackrabbit SearchIndex on a repository
     * @param repository repository
     * @param session jcr session
     * @return {@link SearchIndex}
     * @throws RepositoryException exception getting searchindex
     */
    public SearchIndex getSearchIndex(String repository, Session session) throws RepositoryException
    {
        RepositoryImpl repImpl = (RepositoryImpl) session.getRepository();

        try
        {
            SearchManager searchManager = (SearchManager) getSearchManager.invoke(repImpl, repository);
            return (SearchIndex) searchManager.getQueryHandler();
        }
        catch (Throwable e)
        {
            log.error("Error retrieving SearchIndex", e);
        }
        return null;
    }

    /**
     * Get lucene query to filter faceted search on a path
     * @param path path to filter on
     * @param session jcr session
     * @param si jackr search index
     * @return lucene query
     */
    public Query getQuery(String path, Session session, SearchIndex si)
    {
        QueryNodeFactory factory;
        try
        {
            factory = (QueryNodeFactory) getQueryNodeFactory.invoke(si);
        }
        catch (Throwable e)
        {
            log.error("Error qetting query node factory", e);
            return null;
        }
        try
        {
            QueryRootNode qrn = QueryParser.parse(
                "SELECT * FROM nt:base WHERE jcr:path LIKE '" + path + "/%'",
                LANG_SQL,
                (SessionImpl) session,
                factory);

            Query queryChild = createQuery(qrn, session, si);

            QueryRootNode qrnSelf = QueryParser.parse(
                "SELECT * FROM nt:base WHERE jcr:path = '" + path + "'",
                LANG_SQL,
                (SessionImpl) session,
                factory);

            Query querySelf = createQuery(qrnSelf, session, si);

            Query luceneQuery = new BooleanQuery();
            return luceneQuery.combine(new Query[]{queryChild, querySelf });

        }
        catch (InvalidQueryException e)
        {
            log.error("Invalid query", e);
        }
        catch (RepositoryException e)
        {
            log.error("Repository Exception", e);
        }
        return null;
    }

    private Query createQuery(QueryRootNode root, Session session, SearchIndex si) throws RepositoryException
    {

        // LuceneQueryBuilder.createQuery() signature has changed in 2.2.1 (one more parameter added) so we are forced
        // to use reflection here

        Method[] methods = LuceneQueryBuilder.class.getMethods();
        Method createQuery = null;
        for (Method method : methods)
        {
            if (method.getName().equals("createQuery"))
            {
                createQuery = method;
                break;
            }
        }

        if (createQuery == null)
        {
            throw new UnhandledException(
                "Unsupported version of jackrabbit detected (not in the range 1.6 - 2.6.?)",
                null);
        }

        Class< ? >[] params = createQuery.getParameterTypes();

        List<Object> parameters = new ArrayList<Object>();
        parameters.add(root);
        parameters.add(session);
        parameters.add(si.getContext().getItemStateManager());
        parameters.add(si.getNamespaceMappings());
        parameters.add(si.getTextAnalyzer());
        parameters.add(si.getContext().getPropertyTypeRegistry());
        parameters.add(si.getSynonymProvider());
        parameters.add(si.getIndexFormatVersion());

        if (params.length > 8)
        {
            parameters.add(null);
        }

        try
        {
            return (Query) createQuery.invoke(null, parameters.toArray());
        }
        catch (IllegalArgumentException e)
        {
            throw new RuntimeException(e);
        }
        catch (IllegalAccessException e)
        {
            throw new RuntimeException(e);
        }
        catch (InvocationTargetException e)
        {
            Throwable targetException = e.getTargetException();
            if (targetException instanceof RepositoryException)
            {
                throw (RepositoryException) targetException;
            }
            else if (targetException instanceof RuntimeException)
            {
                throw (RuntimeException) targetException;
            }
            else
            {
                throw new RuntimeException(e);
            }
        }

    }
}
