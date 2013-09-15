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

import info.magnolia.jcr.util.NodeUtil;
import info.magnolia.jcr.util.PropertyUtil;

import java.util.ArrayList;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.RepositoryException;


/**
 * @author dschivo
 */
public class RepositoryExpressionLibrary implements ExpressionLibrary
{

    protected final String name;

    protected final String label;

    protected final List<Expression> expressions;

    protected final boolean visible;

    public RepositoryExpressionLibrary(Node configNode)
    {
        name = NodeUtil.getName(configNode);
        label = PropertyUtil.getString(configNode, "label");
        visible = PropertyUtil.getBoolean(configNode, "visible", true);

        expressions = new ArrayList<Expression>();
        try
        {
            if (configNode.hasNode("expressions"))
            {
                Node expressionsNode = configNode.getNode("expressions");

                Iterable<Node> nodes = NodeUtil.getNodes(expressionsNode, NodeUtil.EXCLUDE_META_DATA_FILTER);

                for (Node node : nodes)
                {
                    String label = PropertyUtil.getString(node, "label");
                    String value = PropertyUtil.getString(node, "value");
                    expressions.add(new Expression(label, value));
                }
            }

        }
        catch (RepositoryException e)
        {
        }
    }

    /**
     * {@inheritDoc}
     */
    public String getName()
    {
        return name;
    }

    /**
     * {@inheritDoc}
     */
    public String getLabel()
    {
        return label;
    }

    /**
     * {@inheritDoc}
     */
    public List<Expression> getExpressions()
    {
        return expressions;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isVisible()
    {
        return visible;
    }
}
