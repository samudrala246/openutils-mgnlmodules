/**
 *
 * Controls module for Magnolia CMS (http://www.openmindlab.com/lab/products/controls.html)
 * Copyright(C) 2008-2013, Openmind S.r.l. http://www.openmindonline.it
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

import info.magnolia.cms.beans.config.ContentRepository;
import info.magnolia.cms.core.Content;
import info.magnolia.cms.gui.control.Button;
import info.magnolia.cms.gui.control.ControlImpl;
import info.magnolia.cms.util.ContentUtil;
import info.magnolia.cms.util.NodeDataUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.jcr.RepositoryException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.collect.Iterators;


public class DialogButtonSet extends info.magnolia.cms.gui.dialog.DialogButtonSet
{

    /**
     * Logger.
     */
    private static Logger log = LoggerFactory.getLogger(DialogButtonSet.class);

    private OptionsProvider optionsProvider = new OptionsProvider.DefaultImpl();;

    private boolean initHack;

    @Override
    public void init(HttpServletRequest request, HttpServletResponse response, Content websiteNode, Content configNode)
        throws RepositoryException
    {
        // super.init(request, response, websiteNode, configNode);
        // String optionsProviderClass = getConfigValue("optionsProvider");
        String optionsProviderClass = NodeDataUtil.getString(configNode, "optionsProvider");
        try
        {
            optionsProvider = (OptionsProvider) Class.forName(optionsProviderClass).newInstance();
        }
        catch (Throwable e)
        {
            log.error(e.getMessage(), e);
        }
        initHack = true;
        super.init(request, response, websiteNode, configNode);
        initHack = false;
    }

    @Override
    public String getConfigValue(String key, String nullValue)
    {
        String val = super.getConfigValue(key, nullValue);
        if (initHack && StringUtils.equals(key, "selectType") && StringUtils.equals(val, nullValue))
        {
            val = StringUtils.removeStart(val, "controls-");
        }
        return val;
    }

    @Override
    public void setOptions(Content configNode, boolean setDefaultSelected)
    {
        List options = new ArrayList();
        try
        {
            Iterator<Option> it = optionsProvider.getOptions(this, configNode);
            while (it.hasNext())
            {
                Option o = it.next();
                Button button = new Button(this.getName(), o.getValue());
                button.setLabel(o.getLabel());
                if (StringUtils.isNotEmpty(o.getIconSrc()))
                {
                    button.setIconSrc(o.getIconSrc());
                }
                if (setDefaultSelected && o.isSelected())
                {
                    button.setState(ControlImpl.BUTTONSTATE_PUSHED);
                }
                options.add(button);
            }
        }
        catch (Exception e)
        {
            if (log.isDebugEnabled())
            {
                log.debug("Exception caught: " + e.getMessage(), e);
            }
        }
        this.setOptions(options);
    }

    public static interface Option
    {

        String getValue();

        String getLabel();

        String getIconSrc();

        boolean isSelected();

        public static class ContentAdapter implements Option
        {

            private final Content node;

            private final String valueNodeData;

            private final String labelNodeData;

            public ContentAdapter(Content node, String valueNodeData, String labelNodeData)
            {
                this.node = node;
                this.valueNodeData = valueNodeData;
                this.labelNodeData = labelNodeData;
            }

            public String getValue()
            {
                return NodeDataUtil.getString(node, valueNodeData);
            }

            public String getLabel()
            {
                return NodeDataUtil.getString(node, labelNodeData);
            }

            public String getIconSrc()
            {
                return node.getNodeData("iconSrc").getString();
            }

            public boolean isSelected()
            {
                return node.getNodeData("selected").getBoolean();
            }
        }

        public static class MapAdapter implements Option
        {

            private final Map map;

            private final String valueNodeData;

            private final String labelNodeData;

            public MapAdapter(Map map, String valueNodeData, String labelNodeData)
            {
                this.map = map;
                this.valueNodeData = valueNodeData;
                this.labelNodeData = labelNodeData;
            }

            public String getValue()
            {
                return ObjectUtils.toString(map.get(valueNodeData));
            }

            public String getLabel()
            {
                return ObjectUtils.toString(map.get(labelNodeData));
            }

            public String getIconSrc()
            {
                return ObjectUtils.toString(map.get("iconSrc"));
            }

            public boolean isSelected()
            {
                return BooleanUtils.toBoolean(ObjectUtils.toString(map.get("selected")));
            }
        }
    }

    public interface OptionsProvider
    {

        Iterator<Option> getOptions(DialogButtonSet control, Content configNode) throws Exception;

        public static class DefaultImpl implements OptionsProvider
        {

            public Iterator<Option> getOptions(final DialogButtonSet control, Content configNode) throws Exception
            {
                // info.magnolia.cms.gui.dialog.DialogButtonSet.getOptionNodes(Content)
                Content optionsNode = null;

                if (configNode.hasContent("options"))
                {
                    optionsNode = configNode.getContent("options");
                }
                else
                {
                    String repository = control.getConfigValue("repository", ContentRepository.WEBSITE);
                    String path = control.getConfigValue("path");
                    if (StringUtils.isNotEmpty(path))
                    {
                        optionsNode = ContentUtil.getContent(repository, path);
                    }
                }

                if (optionsNode != null)
                {
                    return Iterators.transform(
                        ContentUtil.getAllChildren(optionsNode).iterator(),
                        new Function<Content, Option>()
                        {

                            public Option apply(Content input)
                            {
                                return new Option.ContentAdapter(input, control
                                    .getConfigValue("valueNodeData", "value"), control.getConfigValue(
                                    "labelNodeData",
                                    "label"));
                            }
                        });
                }
                return new ArrayList().iterator();
            }
        }
    }
}
