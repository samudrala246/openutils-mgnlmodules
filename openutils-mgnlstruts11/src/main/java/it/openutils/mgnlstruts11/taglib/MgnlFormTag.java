/**
 *
 * Struts 1.1 module for Magnolia CMS (http://www.openmindlab.com/lab/products/mgnlstruts.html)
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

package it.openutils.mgnlstruts11.taglib;

import it.openutils.mgnlstruts11.process.MgnlStrutsUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.taglib.html.FormTag;


/**
 * An extended html:form tags that remaps urls in order to work inside magnolia.
 * @author fgiust
 * @version $Id$
 */
public class MgnlFormTag extends FormTag
{

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * Generates the opening <code>&lt;form&gt;</code> element with appropriate attributes.
     * @since Struts 1.1
     */
    @Override
    protected String renderFormStartElement()
    {
        String pageUri = (String) this.pageContext.getRequest().getAttribute(MgnlStrutsUtils.ATTRIBUTE_ORIGINALURI);

        if (pageUri == null)
        {
            return super.renderFormStartElement();
        }

        HttpServletResponse response = (HttpServletResponse) this.pageContext.getResponse();

        // String actionUrl = response.encodeURL(RequestUtils.getActionMappingURL(this.action, this.pageContext));
        String actionUrl = response.encodeURL(this.action);

        if (actionUrl.endsWith(".do"))
        {
            actionUrl = StringUtils.substringBefore(actionUrl, ".do");
        }

        StringBuffer results = new StringBuffer("<form");
        results.append(" name=\"");
        results.append(beanName);
        results.append("\"");
        results.append(" method=\"");
        results.append(method == null ? "post" : method);
        results.append("\" action=\"");
        // results.append(actionUrl);

        String mgnlDest = MgnlStrutsUtils.extractDestinationFromClass(
            (HttpServletRequest) pageContext.getRequest(),
            styleClass);
        if (StringUtils.isNotBlank(mgnlDest))
        {
            pageUri = mgnlDest;
        }

        if (pageUri != null)
        {
            results.append(pageUri);
            results.append("#" + actionUrl);
        }
        results.append("\"");

        String trimmedStyleClass = MgnlStrutsUtils.trimDestinationFromClass(getStyleClass());

        if (trimmedStyleClass != null)
        {
            results.append(" class=\"");
            results.append(trimmedStyleClass);
            results.append("\"");
        }
        if (enctype != null)
        {
            results.append(" enctype=\"");
            results.append(enctype);
            results.append("\"");
        }
        if (onreset != null)
        {
            results.append(" onreset=\"");
            results.append(onreset);
            results.append("\"");
        }
        if (onsubmit != null)
        {
            results.append(" onsubmit=\"");
            results.append(onsubmit);
            results.append("\"");
        }
        if (style != null)
        {
            results.append(" style=\"");
            results.append(style);
            results.append("\"");
        }
        if (styleId != null)
        {
            results.append(" id=\"");
            results.append(styleId);
            results.append("\"");
        }
        if (target != null)
        {
            results.append(" target=\"");
            results.append(target);
            results.append("\"");
        }
        results.append(">");

        results.append("<input type=\"hidden\" name=\"_mgnlaction\" value=\"");
        results.append(actionUrl);
        results.append("\" />");

        return results.toString();
    }
}
