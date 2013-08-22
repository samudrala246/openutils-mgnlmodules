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

import info.magnolia.cms.core.Content;
import info.magnolia.cms.security.Permission;
import info.magnolia.context.MgnlContext;
import info.magnolia.jcr.util.NodeUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import net.sourceforge.openutils.mgnlcontextmenu.el.ContextMenuElFunctions;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;


/**
 * Wraps an HTML snippet and possibly replaces it with content set by the editor user. Editor users have access to a
 * context menu which opens on mouse right click.
 * @author dschivo
 * @version $Id$
 */
public class ElementTag extends BodyTagSupport
{

    public static final String ELEMENT_INFOS_KEY = ElementTag.class.getName() + ".elementInfos";

    private String name;

    private String menu;

    private Content node;

    private String wrapper;

    private boolean readonly;

    private String elementId;

    /**
     * Sets the name.
     * @param name the name to set
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Sets the menu.
     * @param menu the menu to set
     */
    public void setMenu(String menu)
    {
        this.menu = menu;
    }

    /**
     * Sets the node.
     * @param node the node to set
     */
    public void setNode(Content node)
    {
        this.node = node;
    }

    /**
     * Sets the wrapper.
     * @param wrapper the wrapper to set
     */
    public void setWrapper(String wrapper)
    {
        this.wrapper = wrapper;
    }

    /**
     * Sets the readonly.
     * @param readonly the readonly to set
     */
    public void setReadonly(boolean readonly)
    {
        this.readonly = readonly;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int doStartTag() throws JspException
    {
        Content node = this.node != null ? this.node : currentParagraph();
        boolean readonly = this.readonly || !canEdit();
        if (!readonly)
        {
            if (StringUtils.isEmpty(wrapper))
            {
                wrapper = "span";
            }
            elementId = "content_" + RandomStringUtils.randomAlphabetic(6);
            try
            {
                pageContext.getOut().print("<" + wrapper + " id=\"" + elementId + "\" class=\"edit-content\">");
            }
            catch (IOException e)
            {
                throw new JspException(e);
            }
        }
        String value = !StringUtils.isEmpty(name) ? ContextMenuElFunctions.entryValue(node, name) : null;
        if (!StringUtils.isEmpty(value))
        {
            try
            {
                pageContext.getOut().print(value);
            }
            catch (IOException e)
            {
                throw new JspException(e);
            }
            return SKIP_BODY;
        }
        else
        {
            return EVAL_BODY_INCLUDE;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int doEndTag() throws JspException
    {
        Content node = this.node != null ? this.node : currentParagraph();
        boolean readonly = this.readonly || !canEdit();
        if (!readonly)
        {
            try
            {
                pageContext.getOut().print("</" + wrapper + ">");
            }
            catch (IOException e)
            {
                throw new JspException(e);
            }

            ElementInfo info = addElementInfo(node.getHandle(), elementId);
            if (!StringUtils.isEmpty(name))
            {
                info.setEntryName(name);
            }
            if (!StringUtils.isEmpty(menu))
            {
                info.setContextMenu(menu);
            }
        }
        return EVAL_PAGE;
    }

    @SuppressWarnings("unchecked")
    protected ElementInfo addElementInfo(String path, String elementId)
    {
        HttpServletRequest request = MgnlContext.getWebContext().getRequest();
        List infos = (List) request.getAttribute(ELEMENT_INFOS_KEY);
        if (infos == null)
        {
            infos = new ArrayList();
            request.setAttribute(ELEMENT_INFOS_KEY, infos);
        }
        ElementInfo info = new ElementInfo(path, elementId);
        infos.add(info);
        return info;
    }

    private boolean canEdit()
    {
        return NodeUtil.isGranted(MgnlContext.getAggregationState().getMainContent().getJCRNode(), Permission.SET);
    }

    private Content currentParagraph()
    {
        return MgnlContext.getAggregationState().getCurrentContent();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void release()
    {
        node = null;
        name = null;
        menu = null;
        wrapper = null;
        readonly = false;
        elementId = null;
        super.release();
    }
}
