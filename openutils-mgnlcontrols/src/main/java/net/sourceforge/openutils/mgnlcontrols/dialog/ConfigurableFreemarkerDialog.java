/**
 *
 * Controls module for Magnolia CMS (http://www.openmindlab.com/lab/products/controls.html)
 * Copyright(C) 2008-2012, Openmind S.r.l. http://www.openmindonline.it
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

package net.sourceforge.openutils.mgnlcontrols.dialog;

import freemarker.template.TemplateException;
import info.magnolia.cms.core.Content;
import info.magnolia.cms.core.ItemType;
import info.magnolia.cms.core.NodeData;
import info.magnolia.cms.core.SystemProperty;
import info.magnolia.cms.gui.control.ControlImpl;
import info.magnolia.cms.gui.control.FreemarkerControl;
import info.magnolia.cms.gui.dialog.DialogBox;
import info.magnolia.cms.security.AccessDeniedException;
import info.magnolia.cms.util.NodeDataUtil;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.exception.NestableRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * * A Magnolia dialog that renders by a freemarker template. There are two main properties for the dialog:<br/>
 * <table>
 * <tr>
 * <td>path (required)</td>
 * <td>Path to freemarker template: will be loaded from classpath or from filesystem</td>
 * </tr>
 * <tr>
 * <td>multiple</td>
 * <td>true / false. This property gives support to multiple field values storage.</td>
 * </tr>
 * </table>
 * The dialog passes some parameters to freemarker template:
 * <table>
 * <tr>
 * <td>name</td>
 * <td>Dialog / field name</td>
 * </tr>
 * <tr>
 * <td>value</td>
 * <td>Field value (multiple = false)</td>
 * </tr>
 * <tr>
 * <td>values</td>
 * <td>field values (multiple = true)</td>
 * </tr>
 * <tr>
 * <td>request</td>
 * <td>current HttpServletRequest</td>
 * </tr>
 * <tr>
 * <td>configuration</td>
 * <td>Map of dialog configuration. This allows to pass to template complex dialog configuration.<br/>
 * Eg.
 * 
 * <pre>
 * -+ Dialog node
 *  |-- property 1 = value 1
 *  |-+ subnode1
 *  | |-- property 11 = value 11
 *  | |-- property 12 = value 12
 *  | |-+ subnode 11
 *  |   |-- property 111 = value 111
 *  |
 *  |-- property 2 = value 2
 *
 * The map will contain:
 * configuration = Map {
 *    "property1" = "value1",
 *    "subnode1"  = Map {
 *        "property11" = "value11",
 *        "property12" = "value12",
 *        "subnode11"  =  Map {
 *            "property111" = "value111"
 *        }
 *    },
 *    "property2" = "value2"
 * }
 * </pre>
 * </td>
 * </tr>
 * </table>
 * <p>
 * Similar to DialogFreemarker but with a fixed template, needed till MAGNOLIA-2175 is done
 * </p>
 * @author fgiust
 * @version $Id$
 */
public abstract class ConfigurableFreemarkerDialog extends DialogBox
{

    /**
     * Logger.
     */
    protected Logger log = LoggerFactory.getLogger(getClass());

    /**
     * Map of dialog configuration.
     */
    protected Map<String, Object> configuration;

    /**
     * Configuration node UUID.
     */
    protected String uuid;

    /**
     * Get a recursive map view of a content node
     * @param node content node
     * @return recursive map view on content node properties and children
     * @throws RepositoryException for any exception occurred while accessing the repository
     * @throws AccessDeniedException if the current user can't read the configuration subnodes
     */
    protected Map<String, Object> getSubNodes(Content node) throws RepositoryException, AccessDeniedException
    {

        this.uuid = node.getUUID();
        Map<String, Object> values = new LinkedHashMap<String, Object>();

        // cycles on properties and stores them in map
        Collection<NodeData> properties = node.getNodeDataCollection();

        if (properties != null && properties.size() > 0)
        {
            Iterator<NodeData> propertiesIt = properties.iterator();
            while (propertiesIt.hasNext())
            {
                NodeData property = propertiesIt.next();
                values.put(property.getName(), NodeDataUtil.getValueObject(property));
            }
        }

        // cycle on children
        Collection<Content> children = node.getChildren(ItemType.CONTENTNODE);
        if (children != null && children.size() > 0)
        {
            Iterator<Content> childrenIt = children.iterator();
            while (childrenIt.hasNext())
            {
                Content child = childrenIt.next();

                // gets sub map
                Map<String, Object> subValues = getSubNodes(child);
                // stores it in map
                values.put(child.getName(), subValues);
            }
        }

        return values;
    }

    /**
     * {@inheritDoc}
     */

    @Override
    public void init(HttpServletRequest request, HttpServletResponse response, Content websiteNode, Content configNode)
        throws RepositoryException
    {
        super.init(request, response, websiteNode, configNode);
        configuration = this.getSubNodes(configNode);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void drawHtml(Writer out) throws IOException
    {

        String alreadyRenderedKey = "freemarker/" + getPath();

        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("name", this.getName());
        parameters.put("value", this.getValue());
        parameters.put("values", this.getValues());
        parameters.put("request", this.getRequest());
        parameters.put("debug", SystemProperty.getBooleanProperty("magnolia.develop"));
        parameters.put("configuration", this.configuration);
        parameters.put("uuid", this.uuid);
        parameters.put("alreadyrendered", getRequest().getAttribute(alreadyRenderedKey) != null);
        parameters.put("msgs", this.getMessages());

        addToParameters(parameters);

        this.drawHtmlPre(out);

        try
        {
            FreemarkerControl control = new FreemarkerControl("multiple".equals(this.getConfigValue("valueType"))
                ? ControlImpl.VALUETYPE_MULTIPLE
                : ControlImpl.VALUETYPE_SINGLE);
            control.setType(this.getConfigValue("type", PropertyType.TYPENAME_STRING));
            control.setName(this.getName());
            control.drawHtml(out, getPath(), parameters);
        }
        catch (TemplateException ex)
        {
            log.error("Error processing dialog template:", ex);
            throw new NestableRuntimeException(ex);
        }

        this.drawHtmlPost(out);

        getRequest().setAttribute(alreadyRenderedKey, Boolean.TRUE);
    }

    /**
     * Can be implemented by subclasses in order to add parameters to the Map passed to freemarker.
     * @param parameters parameter map
     */
    protected void addToParameters(Map<String, Object> parameters)
    {
        // do nothing, can be user in subclasses
    }

    /**
     * @return ftl template path
     */
    protected abstract String getPath();

}
