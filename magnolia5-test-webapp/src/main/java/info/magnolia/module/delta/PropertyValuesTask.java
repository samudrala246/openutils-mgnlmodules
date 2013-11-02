/**
 *
 * Magnolia5 test webapp (http://openutils.sourceforge.net/magnolia5-test-webapp)
 * Copyright(C) ${project.inceptionYear}-2013, Openmind S.r.l. http://www.openmindonline.it
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
package info.magnolia.module.delta;

import info.magnolia.cms.core.Content;
import info.magnolia.cms.core.NodeData;
import info.magnolia.jcr.util.PropertyUtil;
import info.magnolia.module.InstallContext;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collection;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;

import org.apache.commons.lang.StringUtils;


/**
 * A tasks that offers helper methods to check on certain properties.
 *
 * @author gjoseph
 * @version $Revision: $ ($Author: $)
 */
public abstract class PropertyValuesTask extends AbstractTask {

    public PropertyValuesTask(String name, String description) {
        super(name, description);
    }

    /**
     * Checks that the given String property has the expected value. Changes it if so, logs otherwise.
     */
    protected void checkAndModifyPropertyValue(InstallContext ctx, Content node, String propertyName, String expectedCurrentValue, String newValue) throws RepositoryException {
        checkAndModifyPropertyValue(ctx, node.getJCRNode(), propertyName, Arrays.asList(expectedCurrentValue), newValue);
    }

    /**
     * Checks that the given String property has the expected value. Changes it if so, logs otherwise.
     */
    protected void checkAndModifyPropertyValue(InstallContext ctx, Node node, String propertyName, String expectedCurrentValue, String newValue) throws RepositoryException {
        checkAndModifyPropertyValue(ctx, node, propertyName, Arrays.asList(expectedCurrentValue), newValue);
    }


    /**
     * Checks that the given String property has one of the expected values. Changes it if so, logs otherwise.
     */
    protected void checkAndModifyPropertyValue(InstallContext ctx, Node node, String propertyName, Collection<String> expectedCurrentValues, String newValue) throws RepositoryException {
        if (node.hasProperty(propertyName)) {
            final Property prop = node.getProperty(propertyName);
            final String currentvalue = prop.getString();
            if (expectedCurrentValues.contains(currentvalue)) {
                prop.setValue(newValue);
            } else {
                String msg;
                if (expectedCurrentValues.size() == 1){
                    msg = format("Property \"{0}\" was expected to exist at {1} with value \"{2}\" but has the value \"{3}\" instead.",
                            propertyName, node.getPath(), expectedCurrentValues.iterator().next(), currentvalue);
                } else {
                    msg = format("Property \"{0}\" was expected to exist at {1} with one of values {2} but has the value \"{3}\" instead.",
                            propertyName, node.getPath(), expectedCurrentValues.toString(), currentvalue);
                }
                ctx.warn(msg);
            }
        } else {
            String msg;
            if (expectedCurrentValues.size() == 1){
                msg = format("Property \"{0}\" was expected to exist at {1} with value \"{2}\" but does not exist.",
                        propertyName, node.getPath(), expectedCurrentValues.iterator().next());
            } else {
                msg = format("Property \"{0}\" was expected to exist at {1} with one of values {2} but does not exist.",
                        propertyName, node.getPath(), expectedCurrentValues.toString());
            }
            ctx.warn(msg);
        }
    }

    /**
     * Checks if property contains concrete string. If contains then change this part of string, logs otherwise.
     */
    protected void checkAndModifyPartOfPropertyValue(InstallContext ctx, Node node, String propertyName, String expectedValue, String newValue) throws RepositoryException {
        if(node.hasProperty(propertyName)){
            final Property prop = node.getProperty(propertyName);
            final String currentvalue = prop.getString();
            if(currentvalue.contains(expectedValue)) {
                prop.setValue(StringUtils.replace(currentvalue, expectedValue, newValue));
            } else {
                final String msg = format("Property \"{0}\" was expected to exist at {1} with part string \"{2}\" but does not contain this string.",
                            propertyName, node.getPath(), expectedValue);
                ctx.warn(msg);
            }
        } else {
            final String msg = format("Property \"{0}\" was expected to exist at {1} with part string \"{2}\" but does not exist.",
                    propertyName, node.getPath(), expectedValue);
            ctx.warn(msg);
        }
    }

    protected void checkOrCreateProperty(InstallContext ctx, Content node, String propertyName, String expectedValue) throws RepositoryException {
        if (node.hasNodeData(propertyName)) {
            final NodeData prop = node.getNodeData(propertyName);
            final String currentvalue = prop.getString();
            if (!currentvalue.equals(expectedValue)) {
                final String msg = format("Property \"{0}\" was expected to exist at {1} with value \"{2}\" but {3,choice,0#does not exist|1#has the value \"{4}\" instead}.",
                                propertyName, node.getHandle(), expectedValue, Integer.valueOf(prop.isExist() ? 1 : 0),
                                currentvalue);
                ctx.warn(msg);
            }
        } else {
            node.createNodeData(propertyName, expectedValue);
        }
    }

    /**
     * Checks that the given property does not exist and creates it with the given value, logs otherwise.
     */
    @Deprecated
    protected void newProperty(InstallContext ctx, Content node, String propertyName, String value) throws RepositoryException {
        newProperty(ctx, node.getJCRNode(), propertyName, value);
    }

    /**
     * Checks that the given property does not exist and creates it with the given value, logs otherwise.
     */
    protected void newProperty(InstallContext ctx, Node node, String propertyName, Object value) throws RepositoryException {
        
        if(node != null )
        {
        if (!node.hasProperty(propertyName)) {
            PropertyUtil.setProperty(node, propertyName, value);
        } else {
            final String msg = format("Property \"{0}\" was expected not to exist at {1}, but exists with value \"{2}\" and was going to be created with value \"{3}\".",
                    propertyName, node.getPath(), node.getProperty(propertyName).getValue().getString(), value);
            ctx.warn(msg);
        }
        }
    }

    // TODO move this to the InstallContext interface ?
    protected String format(String pattern, Object arg0, Object arg1, Object arg2, Object arg3, Object arg4) {
        return MessageFormat.format(pattern, new Object[]{arg0, arg1, arg2, arg3, arg4});
    }

    protected String format(String pattern, Object arg0, Object arg1, Object arg2, Object arg3) {
        return format(pattern, arg0, arg1, arg2, arg3, null);
    }

    protected String format(String pattern, Object arg0, Object arg1, Object arg2) {
        return format(pattern, arg0, arg1, arg2, null);
    }
}
