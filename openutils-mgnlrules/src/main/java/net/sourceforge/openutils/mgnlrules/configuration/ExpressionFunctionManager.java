/**
 *
 * Rules module for Magnolia CMS (http://www.openmindlab.com/lab/products/mgnlrules.html)
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

package net.sourceforge.openutils.mgnlrules.configuration;

import info.magnolia.cms.beans.config.ObservedManager;
import info.magnolia.cms.core.Content;
import info.magnolia.cms.util.ContentUtil;
import info.magnolia.cms.util.NodeDataUtil;
import info.magnolia.objectfactory.Components;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.inject.Singleton;
import javax.jcr.RepositoryException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author dschivo
 */
@Singleton
public class ExpressionFunctionManager extends ObservedManager
{

    public static ExpressionFunctionManager getInstance()
    {
        return Components.getSingleton(ExpressionFunctionManager.class);
    }

    private Logger log = LoggerFactory.getLogger(ExpressionFunctionManager.class);

    private final Map<String, Method> functions = new HashMap<String, Method>();

    /**
     * Returns the functions.
     * @return the functions
     */
    public Map<String, Method> getFunctions()
    {
        return functions;
    }

    @Override
    protected void onClear()
    {
        functions.clear();
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onRegister(Content defNode)
    {
        for (Iterator iter = ContentUtil.getAllChildren(defNode).iterator(); iter.hasNext();)
        {
            Content functionNode = (Content) iter.next();
            String functionName = functionNode.getName();
            Class methodClass = null;
            try
            {
                methodClass = Class.forName(NodeDataUtil.getString(functionNode, "methodClass"));
            }
            catch (ClassNotFoundException e1)
            {
                log.error("Cannot get class for function " + functionName, e1);
            }

            if (methodClass != null)
            {
                String methodName = NodeDataUtil.getString(functionNode, "methodName");

                List<Class> parameterTypes = new ArrayList<Class>();
                try
                {
                    if (functionNode.hasContent("parameterTypes"))
                    {
                        Content parameterTypesNode = functionNode.getContent("parameterTypes");
                        for (Iterator iter2 = ContentUtil.getAllChildren(parameterTypesNode).iterator(); iter2.hasNext();)
                        {
                            Content n = (Content) iter2.next();
                            String value = NodeDataUtil.getString(n, "value");
                            try
                            {
                                parameterTypes.add(Class.forName(value));
                            }
                            catch (ClassNotFoundException e)
                            {
                                log.error("Cannot get parameter type for function " + functionName, e);
                            }
                        }
                    }

                }
                catch (RepositoryException e)
                {
                    log.error("Cannot get parameter types for function " + functionName, e);
                }

                try
                {
                    functions.put("rules:" + functionName, methodClass.getMethod(methodName, parameterTypes
                        .toArray(new Class[0])));
                }
                catch (SecurityException e)
                {
                    log.error("Cannot get method for function " + functionName, e);
                }
                catch (NoSuchMethodException e)
                {
                    log.error("Cannot get method for function " + functionName, e);
                }
            }
        }

    }

}
