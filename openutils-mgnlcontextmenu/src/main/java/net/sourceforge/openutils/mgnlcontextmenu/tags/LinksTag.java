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
import info.magnolia.context.MgnlContext;
import info.magnolia.jcr.util.NodeUtil;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;


/**
 * @author dschivo
 * @version $Id$
 */
public class LinksTag extends TagSupport
{

    private String jsFramework;

    private boolean skipJsFramework;

    /**
     * Sets the jsFramework.
     * @param jsFramework the jsFramework to set
     */
    public void setJsFramework(String jsFramework)
    {
        this.jsFramework = jsFramework;
    }

    /**
     * Sets the skipJsFramework.
     * @param skipJsFramework the skipJsFramework to set
     */
    public void setSkipJsFramework(boolean skipJsFramework)
    {
        this.skipJsFramework = skipJsFramework;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int doStartTag() throws JspException
    {
        String ctx = MgnlContext.getContextPath();
        boolean canEdit = canEdit();
        JspWriter out = pageContext.getOut();
        try
        {
            if (canEdit)
            {
                out.print("<link rel=\"stylesheet\" type=\"text/css\" href=\""
                    + ctx
                    + "/.resources/contextmenu/css/contextmenu.css\" media=\"screen\" />");
                out.print("<script src=\"" + ctx + "/.resources/contextmenu/js/mgnladmin-custom.js\"></script>");
            }
            if ("jquery".equalsIgnoreCase(jsFramework))
            {
                if (!skipJsFramework)
                {
                    out.print("<script src=\"" + ctx + "/.resources/contextmenu/js/jquery-1.4.2.min.js\"></script>");
                }
                if (canEdit)
                {
                    out.print("<script src=\"" + ctx + "/.resources/contextmenu/js/contextmenu-jquery.js\"></script>");
                }
            }
            else if ("mootools".equalsIgnoreCase(jsFramework))
            {
                if (!skipJsFramework)
                {
                    out.print("<script src=\""
                        + ctx
                        + "/.resources/contextmenu/js/mootools-1.2.4-core-yc.js\"></script>");
                }
                if (canEdit)
                {
                    out
                        .print("<script src=\""
                            + ctx
                            + "/.resources/contextmenu/js/contextmenu-mootools.js\"></script>");
                }
            }
        }
        catch (IOException e)
        {
            throw new JspException(e);
        }
        return EVAL_PAGE;
    }

    @SuppressWarnings("deprecation")
    private boolean canEdit()
    {
        return NodeUtil.isGranted(MgnlContext.getAggregationState().getMainContent().getJCRNode(), Permission.SET);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void release()
    {
        jsFramework = null;
        skipJsFramework = false;
        super.release();
    }
}
