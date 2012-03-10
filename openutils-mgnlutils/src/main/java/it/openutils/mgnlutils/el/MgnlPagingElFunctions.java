/**
 *
 * Generic utilities for Magnolia CMS (http://www.openmindlab.com/lab/products/mgnlutils.html)
 * Copyright(C) 2009-2011, Openmind S.r.l. http://www.openmindonline.it
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

package it.openutils.mgnlutils.el;

import info.magnolia.context.MgnlContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.commons.lang.math.NumberUtils;


/**
 * @author fgiust
 * @version $Id$
 */
public class MgnlPagingElFunctions
{

    // public constructor required for freemarker support
    public MgnlPagingElFunctions()
    {

    }

    /**
     * Creates a list of Page objects that can be used to draw a pagination bar.
     * @param total total number of pages
     * @param visible maximum number of pages to show
     * @param param name of the request parameter that will hold the current page number
     * @return List of Page objects
     */
    public static List<Page> pageList(int total, int visible, String param)
    {

        Map<String, String> parameters = MgnlContext.getParameters();

        StringBuffer sb = new StringBuffer();
        sb.append("?");
        boolean first = true;
        for (Map.Entry<String, String> entry : parameters.entrySet())
        {
            String key = entry.getKey();
            if (!StringUtils.equals(entry.getKey(), param))
            {
                if (!first)
                {
                    sb.append("&amp;");
                }

                String[] parameterValues = MgnlContext.getParameterValues(key);

                if (parameterValues != null)
                {
                    for (int j = 0; j < parameterValues.length; j++)
                    {
                        String value = parameterValues[j];
                        sb.append(key);
                        sb.append("=");
                        sb.append(value);
                        if (j + 1 < parameterValues.length)
                        {
                            sb.append("&amp;");
                        }
                    }
                }

                first = false;
            }
        }

        if (!first)
        {
            sb.append("&amp;");
        }
        sb.append(param);
        sb.append("=");

        String baseUrl = sb.toString();

        List<Page> result = new ArrayList<Page>();

        int current = Math.max(1, NumberUtils.toInt(MgnlContext.getParameter(param), 1));

        // center pages
        int start = Math.max(1, Math.min(current - visible / 2, total - visible + 1));
        int end = Math.min(start + visible, total);

        Page page = new Page(1, current == 1, baseUrl);
        page.setLabel("&laquo;&laquo;");
        page.setActive(current != 1);
        result.add(page);
        page.setCssclass("page-first");

        int previous = Math.max(current - 1, 1);
        page = new Page(previous, current == previous, baseUrl);
        page.setLabel("&laquo;");
        page.setActive(current != previous);
        result.add(page);
        page.setCssclass("page-previous");

        for (int j = start; j <= end; j++)
        {
            page = new Page(j, current == j, baseUrl);
            page.setLabel(Integer.toString(j));
            page.setCssclass("page-numbered");
            result.add(page);
        }

        int next = Math.min(current + 1, total);
        page = new Page(next, current == next, baseUrl);
        page.setLabel("&raquo;");
        page.setActive(current != next);
        page.setCssclass("page-next");
        result.add(page);

        page = new Page(total, current == total, baseUrl);
        page.setLabel("&raquo;&raquo;");
        page.setActive(current != total);
        page.setCssclass("page-last");
        result.add(page);

        return result;
    }

    public static class Page
    {

        private int number;

        boolean current;

        private String url;

        private String label;

        private boolean active;

        private String cssclass;

        /**
         * @param number
         * @param active
         * @param baseurl
         */
        public Page(int number, boolean current, String baseurl)
        {
            this.number = number;
            this.current = current;
            this.url = baseurl;
            active = true;
        }

        /**
         * Returns the number.
         * @return the number
         */
        public int getNumber()
        {
            return number;
        }

        /**
         * Sets the number.
         * @param number the number to set
         */
        public void setNumber(int number)
        {
            this.number = number;
        }

        /**
         * Returns the current.
         * @return the current
         */
        public boolean isCurrent()
        {
            return current;
        }

        /**
         * Sets the current.
         * @param current the current to set
         */
        public void setCurrent(boolean current)
        {
            this.current = current;
        }

        /**
         * Returns the url.
         * @return the url
         */
        public String getUrl()
        {
            return url + this.number;
        }

        /**
         * Returns the label.
         * @return the label
         */
        public String getLabel()
        {
            return label;
        }

        /**
         * Sets the label.
         * @param label the label to set
         */
        public void setLabel(String label)
        {
            this.label = label;
        }

        /**
         * Returns the active.
         * @return the active
         */
        public boolean isActive()
        {
            return active;
        }

        /**
         * Sets the active.
         * @param active the active to set
         */
        public void setActive(boolean active)
        {
            this.active = active;
        }

        /**
         * Returns the cssclass.
         * @return the cssclass
         */
        public String getCssclass()
        {
            return cssclass;
        }

        /**
         * Sets the cssclass.
         * @param cssclass the cssclass to set
         */
        public void setCssclass(String cssclass)
        {
            this.cssclass = cssclass;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString()
        {
            return new ToStringBuilder(this, ToStringStyle.SIMPLE_STYLE)
                .append("number", this.number)
                .append("url", this.url)
                .append("active", this.active)
                .append("current", this.current)
                .append("label", this.label)
                .append("cssclass", this.cssclass)
                .toString();
        }
    }
}
