/**
 *
 * ContextMenu Module for Magnolia CMS (http://www.openmindlab.com/lab/products/mgnlcontextmenu.html)
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

package net.sourceforge.openutils.mgnlcontextmenu.tags;

import info.magnolia.cms.security.Permission;
import info.magnolia.cms.util.NodeDataUtil;
import info.magnolia.context.MgnlContext;
import info.magnolia.jcr.util.NodeUtil;
import it.openutils.mgnlutils.el.MgnlUtilsDeprecatedAdapters;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import net.sourceforge.openutils.mgnlcontextmenu.el.ContextMenuElFunctions;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.NestableRuntimeException;


public class SortListTag extends TagSupport
{

    private String containerId;

    private String orderProperty;

    public void setContainerId(String containerId)
    {
        this.containerId = containerId;
    }

    public void setOrderProperty(String orderProperty)
    {
        this.orderProperty = orderProperty;
    }

    @Override
    public int doStartTag() throws JspException
    {
        ContextMenuElFunctions.beginSortList();
        return EVAL_BODY_INCLUDE;
    }

    @Override
    public int doEndTag() throws JspException
    {
        if (StringUtils.isEmpty(orderProperty))
        {
            orderProperty = containerId;
        }
        String order = StringUtils.defaultString(NodeDataUtil.getString(MgnlContext
            .getAggregationState()
            .getCurrentContent(), orderProperty));

        JspWriter out = pageContext.getOut();
        try
        {
            for (Object item : ContextMenuElFunctions.endSortList(order))
            {
                out.print(item);
            }
        }
        catch (IOException e)
        {
            throw new NestableRuntimeException(e);
        }
        boolean canEdit;
        try
        {
            canEdit = NodeUtil.isGranted(
                MgnlUtilsDeprecatedAdapters.getMainContent(),
                Permission.SET);

        }
        catch (Throwable e)
        {
            canEdit = false;
        }
        if (canEdit)
        {
            Map<String, String> item = new HashMap<String, String>();
            item.put("containerId", containerId);
            item.put("url", MgnlContext.getContextPath()  + "/mgnl-set-property");
            item.put("path", NodeUtil.getPathIfPossible(MgnlUtilsDeprecatedAdapters.getCurrentContent()));
            item.put("name", orderProperty);
            item.put("order", '[' + order + ']');
            List items = (List) pageContext.getRequest().getAttribute(ContextMenuElFunctions.SORT_LIST_KEY);
            if (items == null)
            {
                items = new ArrayList();
                pageContext.getRequest().setAttribute(ContextMenuElFunctions.SORT_LIST_KEY, items);
            }
            items.add(item);
        }
        containerId = null;
        orderProperty = null;
        return EVAL_PAGE;
    }

    @Override
    public void release()
    {
        super.release();
        containerId = null;
        orderProperty = null;
    }
}
