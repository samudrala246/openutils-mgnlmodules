/**
 *
 * E-learning Module for Magnolia CMS (http://www.openmindlab.com/lab/products/lms.html)
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
package net.sourceforge.openutils.mgnllms.lms.tree;

import info.magnolia.cms.core.Content;
import info.magnolia.cms.core.NodeData;
import info.magnolia.cms.gui.control.Hidden;
import info.magnolia.cms.gui.control.Tree;
import info.magnolia.cms.gui.control.TreeColumn;
import info.magnolia.cms.util.NodeDataUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javax.jcr.RepositoryException;
import javax.servlet.http.HttpServletRequest;

import net.sourceforge.openutils.mgnllms.lms.configuration.LmsTypeConfiguration;
import net.sourceforge.openutils.mgnllms.module.LmsTypesManager;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author luca boati
 */
public class LmsModuleTree extends Tree
{

    /**
     * Logger.
     */
    private Logger log = LoggerFactory.getLogger(LmsModuleTree.class);

    /**
     * @param name
     * @param repository
     */
    public LmsModuleTree(String name, String repository)
    {
        super(name, repository);
    }

    /**
     * @param repository
     * @param request
     * @deprecated
     */
    @Deprecated
    public LmsModuleTree(String repository, HttpServletRequest request)
    {
        super(repository, request);
    }

    /**
     * @param name
     * @param repository
     * @param request
     * @deprecated
     */
    @Deprecated
    public LmsModuleTree(String name, String repository, HttpServletRequest request)
    {
        super(name, repository, request);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getHtmlFooter()
    {
        return super.getHtmlFooter();

        // StringBuffer html = new StringBuffer();
        // html.append("</div>"); //$NON-NLS-1$
        //
        // Map params = populateTemplateParameters();
        //
        // params.put("selectMedia", this.getRequest().getParameter("selectMedia") != null);
        //
        // // include the tree footer / menu divs
        // html.append(FreemarkerUtil.process("info/magnolia/cms/gui/control/MediaTreeFooter.ftl", params));
        //
        // return html.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void getHtmlOfSingleItem(StringBuffer html, Content parentNode, String itemType, Object item)
        throws RepositoryException
    {
        Content c = null;
        NodeData d = null;
        String handle;
        String name;
        boolean hasSub = false;
        boolean showSub = false;
        boolean isActivated = false;
        boolean permissionWrite = false;
        boolean permissionWriteParent = false;
        if (itemType.equals(ITEM_TYPE_NODEDATA))
        {
            d = (NodeData) item;
            handle = d.getHandle();
            name = d.getName();

            if (d.isGranted(info.magnolia.cms.security.Permission.WRITE))
            {
                permissionWrite = true;
            }
        }
        else
        {
            c = (Content) item;

            handle = c.getHandle();
            if (this.getColumns().size() == 0)
            {
                name = c.getName();
            }
            else
            {
                this.getColumns(0).setWebsiteNode(c);
                name = this.getColumns(0).getHtml();
            }
            if (c.isGranted(info.magnolia.cms.security.Permission.WRITE))
            {
                permissionWrite = true;
            }
            if (c.getAncestor(c.getLevel() - 1).isGranted(info.magnolia.cms.security.Permission.WRITE))
            {
                permissionWriteParent = true;
            }
            isActivated = c.getMetaData().getIsActivated();
            for (int i = 0; i < this.getItemTypes().size(); i++)
            {
                String type = (String) this.getItemTypes().get(i);

                hasSub = hasSub(c, type);

                if (hasSub)
                {
                    if (this.getPathOpen() != null
                        && (this.getPathOpen().indexOf(handle + "/") == 0 || this.getPathOpen().equals(handle)))
                    {
                        showSub = true;
                    }
                    break;
                }
            }
        }

        // get next if this node is not shown
        if (!showNode(c, d, itemType))
        {
            return;
        }

        // do not past anything on mgnl:course node
        boolean permissionWriteCourse = permissionWrite;
        if (c.getItemType().equals(LmsTypesManager.COURSE))
        {
            permissionWriteCourse = false;
        }

        String icon = getIcon(c, d, itemType);

        String idPre = this.getJavascriptTree() + "_" + handle; //$NON-NLS-1$
        String jsHighlightNode = this.getJavascriptTree() + ".nodeHighlight(this,'" //$NON-NLS-1$
            + handle
            + "'," //$NON-NLS-1$
            + Boolean.toString(permissionWriteCourse)
            + ");"; //$NON-NLS-1$
        String jsResetNode = this.getJavascriptTree() + ".nodeReset(this,'" + handle + "');"; //$NON-NLS-1$ //$NON-NLS-2$
        String jsSelectNode = this.getJavascriptTree() + ".selectNode('" //$NON-NLS-1$
            + handle
            + "'," //$NON-NLS-1$
            + Boolean.toString(permissionWrite)
            + ",'" //$NON-NLS-1$
            + itemType
            + "');"; //$NON-NLS-1$
        String jsExpandNode;
        if (this.getDrawShifter())
        {
            jsExpandNode = this.getJavascriptTree() + ".expandNode('" + handle + "');"; //$NON-NLS-1$ //$NON-NLS-2$
        }
        else
        {
            jsExpandNode = jsSelectNode;
        }
        String jsHighlightLine = this.getJavascriptTree() + ".moveNodeHighlightLine('" + idPre + "_LineInter');"; //$NON-NLS-1$ //$NON-NLS-2$
        String jsResetLine = this.getJavascriptTree() + ".moveNodeResetLine('" + idPre + "_LineInter');"; //$NON-NLS-1$ //$NON-NLS-2$

        // lineInter: line between nodes, to allow set cursor between nodes
        // try to avoid blank images, setting js actions on divs should be ok
        if (permissionWriteParent)
        {
            html.append("<div id=\"");
            html.append(idPre);
            html.append("_LineInter\" class=\"mgnlTreeLineInter mgnlLineEnabled\" onmouseover=\"");
            html.append(jsHighlightLine);
            html.append("\" onmouseout=\"");
            html.append(jsResetLine);
            html.append("\" onmousedown=\"");
            html.append(this.getJavascriptTree());
            html.append(".pasteNode('");
            html.append(handle);
            html.append("'," + Tree.PASTETYPE_ABOVE + ",true);\" ></div>");
        }
        else
        {
            html.append("<div id=\"");
            html.append(idPre);
            html.append("_LineInter\" class=\"mgnlTreeLineInter mgnlLineDisabled\"></div>");
        }

        html.append("<div id=\"");
        html.append(idPre);
        html.append("_DivMain\" style=\"position:relative;top:0;left:0;width:100%;height:18px;\">");
        html.append("&nbsp;"); // do not remove! //$NON-NLS-1$

        html.append("<span id=\"");
        html.append(idPre);
        html.append("_Column0Outer\" class=\"mgnlTreeColumn ");
        html.append(this.getJavascriptTree());
        html.append("CssClassColumn0\" style=\"padding-left:");

        html.append(getPaddingLeft(parentNode));
        html.append("px;\">");
        if (this.getDrawShifter())
        {
            String shifter = StringUtils.EMPTY;
            if (hasSub)
            {
                if (showSub)
                {
                    if (this.getShifterCollapse() != null)
                    {
                        shifter = this.getShifterCollapse();
                    }
                }
                else
                {
                    if (this.getShifterExpand() != null)
                    {
                        shifter = this.getShifterExpand();
                    }
                }
            }
            else
            {
                if (this.getShifterEmpty() != null)
                {
                    shifter = this.getShifterEmpty();
                }
            }
            if (StringUtils.isNotEmpty(shifter))
            {
                html.append("<img id=\"");
                html.append(idPre);
                html.append("_Shifter\" onmousedown=\"");
                html.append(this.getJavascriptTree());
                html.append(".shifterDown('");
                html.append(handle);
                html.append("');\" onmouseout=\"");
                html.append(this.getJavascriptTree());
                html.append(".shifterOut();\" class=\"mgnlTreeShifter\" src=\"");
                html.append(this.getRequest().getContextPath());
                html.append(shifter);
                html.append("\" />");
            }
        }
        html.append("<span id=");
        html.append(idPre);
        html.append("_Name onmouseover=\"");
        html.append(jsHighlightNode);
        html.append("\" onmouseout=\"");
        html.append(jsResetNode);
        html.append("\" onmousedown=\"");
        html.append(jsSelectNode);
        html.append(this.getJavascriptTree());
        html.append(".pasteNode('");
        html.append(handle);
        html.append("'," + Tree.PASTETYPE_SUB + ",");
        html.append(permissionWrite);
        html.append(");\">");
        if (StringUtils.isNotEmpty(icon))
        {
            html.append("<img id=\"");
            html.append(idPre);
            html.append("_Icon\" class=\"mgnlTreeIcon\" src=\"");
            html.append(this.getRequest().getContextPath());
            html.append(icon);
            html.append("\" onmousedown=\"");
            html.append(jsExpandNode);
            html.append("\"");
            // replaced this:
            // if (this.getIconOndblclick() != null)
            // {
            // html.append(" ondblclick=\"");
            // html.append(this.getIconOndblclick());
            // html.append("\"");
            // }
            // with this:
            LmsTypeConfiguration ltc = LmsTypesManager.getInstance().getTypeConfigurationFrom(c);
            if (ltc != null)
            {
                html.append(" ondblclick=\"mgnlOpenWindow('.magnolia/dialogs/"
                    + ltc.getDialog()
                    + ".html?mgnlPath="
                    + c.getHandle()
                    + "&mgnlRepository=lms')\"");
            }
            html.append(" />"); //$NON-NLS-1$
        }
        String dblclick = StringUtils.EMPTY;
        String htmlEdit = this.getColumns(0).getHtmlEdit();
        if (permissionWrite && StringUtils.isNotEmpty(htmlEdit) && !this.isBrowseMode())
        // && this.getRequest().getParameter("selectMedia") == null)
        {
            dblclick = " ondblclick=\"" + this.getJavascriptTree() + ".editNodeData(this,'" + handle + "',0,'" + htmlEdit.replace("\"", "&quot;") + "');\""; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        }
        html.append("<span class=\"mgnlTreeText\" id=\"");
        html.append(idPre);
        html.append("_Column0Main\"");
        html.append(dblclick);
        html.append(">");
        html.append(name);
        html.append("</span></span></span>"); //$NON-NLS-1$

        // this is done because js is not executed when you get it with ajax
        html.append(new Hidden(idPre + "_PermissionWrite", Boolean.toString(permissionWrite), false).getHtml()); //$NON-NLS-1$
        html.append(new Hidden(idPre + "_ItemType", itemType, false).getHtml()); //$NON-NLS-1$
        html.append(new Hidden(idPre + "_IsActivated", Boolean.toString(isActivated), false).getHtml()); //$NON-NLS-1$

        // Put your own stuff here. Good luck!
        onGetHtmlOfSingleItem(html, parentNode, itemType, item, idPre);

        for (int i = 1; i < this.getColumns().size(); i++)
        {
            String str = StringUtils.EMPTY;
            TreeColumn tc = this.getColumns(i);
            if (!itemType.equals(ITEM_TYPE_NODEDATA))
            {
                // content node ItemType.NT_CONTENTNODE and ItemType.NT_CONTENT
                if (!tc.getIsNodeDataType() && !tc.getIsNodeDataValue())
                {
                    tc.setWebsiteNode(c);
                    tc.setId(handle);
                    str = tc.getHtml();
                }
            }
            else
            {
                if (tc.getIsNodeDataType())
                {
                    str = NodeDataUtil.getTypeName(d);
                }
                else if (tc.getIsNodeDataValue())
                {
                    final String stringValue = NodeDataUtil.getValueString(d);
                    str = StringEscapeUtils.escapeXml(stringValue);
                }
                if (StringUtils.isEmpty(str))
                {
                    str = TreeColumn.EMPTY;
                }
                tc.setName(name); // workaround, will be passed to js TreeColumn object
            }
            tc.setEvent("onmouseover", jsHighlightNode, true); //$NON-NLS-1$
            tc.setEvent("onmouseout", jsResetNode, true); //$NON-NLS-1$
            tc.setEvent("onmousedown", jsSelectNode, true); //$NON-NLS-1$
            html.append("<span class=\"mgnlTreeColumn ");
            html.append(this.getJavascriptTree());
            html.append("CssClassColumn");
            html.append(i);
            html.append("\"><span id=\"");
            html.append(idPre);
            html.append("_Column");
            html.append(i);
            html.append("Main\"");
            html.append(tc.getHtmlCssClass());
            html.append(tc.getHtmlEvents());
            if (permissionWrite && StringUtils.isNotEmpty(tc.getHtmlEdit()))
            {
                html.append(" ondblclick=\"");
                html.append(this.getJavascriptTree());
                html.append(".editNodeData(this,'");
                html.append(handle);
                html.append("',");
                html.append(i);
                html.append(");\"");
            }
            html.append(">");
            html.append(str);
            html.append("</span></span>");
        }
        html.append("</div>"); //$NON-NLS-1$
        String display = "none"; //$NON-NLS-1$
        if (showSub)
        {
            display = "block"; //$NON-NLS-1$
        }
        html.append("<div id=\"");
        html.append(idPre);
        html.append("_DivSub\" style=\"display:");
        html.append(display);
        html.append(";\">");
        if (hasSub)
        {
            if (showSub)
            {
                String pathRemaining = this.getPathOpen().substring(this.getPathCurrent().length());
                if (pathRemaining.length() > 0)
                {
                    // get rid of first slash (/people/franz -> people/franz)
                    String slash = "/"; //$NON-NLS-1$
                    if (this.getPathCurrent().equals("/"))
                    {
                        // first slash already removed
                        slash = StringUtils.EMPTY; // no slash needed between pathCurrent and nextChunk
                    }
                    else
                    {
                        pathRemaining = pathRemaining.substring(1);
                    }
                    String nextChunk = StringUtils.substringBefore(pathRemaining, "/"); //$NON-NLS-1$

                    String pathNext = this.getPathCurrent() + slash + nextChunk;
                    this.setPathCurrent(pathNext);
                    html.append(this.getHtmlChildren());
                }
            }
        }
        html.append("</div>\n"); //$NON-NLS-1$
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    protected Iterator collectRenderedItems(Content parentNode, final String itemType)
    {
        Iterator it;
        if (itemType.equalsIgnoreCase(ITEM_TYPE_NODEDATA))
        {
            List nodeDatas = new ArrayList(parentNode.getNodeDataCollection());
            // order them alphabetically
            Collections.sort(nodeDatas, new Comparator()
            {

                public int compare(Object arg0, Object arg1)
                {
                    return ((NodeData) arg0).getName().compareTo(((NodeData) arg1).getName());
                }
            });
            it = nodeDatas.iterator();
        }
        else
        {
            Collection nodes = parentNode.getChildren();
            CollectionUtils.filter(nodes, new Predicate()
            {

                /**
                 * {@inheritDoc}
                 */
                public boolean evaluate(Object object)
                {
                    try
                    {
                        return ((Content) object).getItemType().getSystemName().equals(itemType);
                    }
                    catch (RepositoryException e)
                    {
                        log.error(e.getMessage(), e);
                        return false;
                    }
                }

            });
            Comparator comp = this.getSortComparator();
            if (comp != null)
            {
                List sortedNodes = new ArrayList(nodes);
                Collections.sort(sortedNodes, comp);
                nodes = sortedNodes;
            }
            it = nodes.iterator();
        }
        return it;
    }

}
