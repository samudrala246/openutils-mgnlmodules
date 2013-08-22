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

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;

import org.apache.struts.taglib.html.LinkTag;
import org.apache.struts.taglib.logic.IterateTag;
import org.apache.struts.util.RequestUtils;


/**
 * An extended html:form tags that remaps urls in order to work inside magnolia.
 * @author fgiust
 * @version $Id$
 */
public class MgnlLinkTag extends LinkTag
{

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * Return the complete URL to which this hyperlink will direct the user. Support for indexed property since Struts
     * 1.1
     * @exception JspException if an exception is thrown calculating the value
     */
    @Override
    protected String calculateURL() throws JspException
    {
        String pageUri = (String) this.pageContext.getRequest().getAttribute(MgnlStrutsUtils.ATTRIBUTE_ORIGINALURI);

        if (pageUri == null)
        {
            return super.calculateURL();
        }

        // Identify the parameters we will add to the completed URL
        Map<String, String> params = RequestUtils.computeParameters(
            pageContext,
            paramId,
            paramName,
            paramProperty,
            paramScope,
            name,
            property,
            scope,
            transaction);

        if (params == null)
        {
            params = new HashMap<String, String>(); // create new HashMap if no other params
        }

        // if "indexed=true", add "index=x" parameter to query string
        // * @since Struts 1.1
        if (indexed)
        {

            // look for outer iterate tag
            IterateTag iterateTag = (IterateTag) findAncestorWithClass(this, IterateTag.class);
            if (iterateTag == null)
            {
                // This tag should only be nested in an iterate tag
                // If it's not, throw exception
                JspException e = new JspException(messages.getMessage("indexed.noEnclosingIterate"));
                RequestUtils.saveException(pageContext, e);
                throw e;
            }

            // calculate index, and add as a parameter

            if (indexId != null)
            {
                params.put(indexId, Integer.toString(iterateTag.getIndex()));
            }
            else
            {
                params.put("index", Integer.toString(iterateTag.getIndex()));
            }
        }

        String styleClass = getStyleClass();
        String mgnlDest = MgnlStrutsUtils.extractDestinationFromClass(
            (HttpServletRequest) pageContext.getRequest(),
            styleClass);

        if (mgnlDest != null)
        {
            pageUri = mgnlDest;
        }

        String url = null;
        try
        {
            url = MgnlStrutsUtils.computeURL(
                pageContext,
                forward,
                href,
                page,
                action,
                params,
                anchor,
                false,
                true,
                pageUri);
        }
        catch (MalformedURLException e)
        {
            RequestUtils.saveException(pageContext, e);
            throw new JspException(messages.getMessage("rewrite.url", e.toString()));
        }
        return (url);

    }

    /**
     * Prepares the style attributes for inclusion in the component's HTML tag.
     * @return The prepared String for inclusion in the HTML tag.
     * @exception JspException if invalid attributes are specified
     */
    @Override
    protected String prepareStyles() throws JspException
    {
        String value = null;
        StringBuffer styles = new StringBuffer();
        if (getStyle() != null)
        {
            styles.append(" style=\"");
            styles.append(getStyle());
            styles.append("\"");
        }

        String styleClass = MgnlStrutsUtils.trimDestinationFromClass(getStyleClass());
        if (styleClass != null)
        {
            styles.append(" class=\"");
            styles.append(styleClass);
            styles.append("\"");
        }
        if (getStyleId() != null)
        {
            styles.append(" id=\"");
            styles.append(getStyleId());
            styles.append("\"");
        }
        value = message(getTitle(), getTitleKey());
        if (value != null)
        {
            styles.append(" title=\"");
            styles.append(value);
            styles.append("\"");
        }
        value = message(getAlt(), getAltKey());
        if (value != null)
        {
            styles.append(" alt=\"");
            styles.append(value);
            styles.append("\"");
        }
        return styles.toString();
    }

}
