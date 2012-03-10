/**
 *
 * SimpleMedia Module for Magnolia CMS (http://www.openmindlab.com/lab/products/media.html)
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

package net.sourceforge.openutils.mgnlmedia.media.advancedsearch;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sourceforge.openutils.mgnlcriteria.jcr.query.criterion.Criterion;
import net.sourceforge.openutils.mgnlmedia.media.pages.SortMode;

import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author cstrappazzon
 * @version $Id$
 */
public class SearchFilterAbstract implements SearchFilter
{

    /**
     * Logger.
     */
    protected Logger log = LoggerFactory.getLogger(SearchFilterAbstract.class);

    private Map<String, SearchFilter> subfilters = new LinkedHashMap<String, SearchFilter>();

    private String control;

    private String label;

    /**
     * Returns the subfilters.
     * @return the subfilters
     */
    public Map<String, SearchFilter> getSubfilters()
    {
        return subfilters;
    }

    public void addSubfilters(String name, SearchFilter subfilter)
    {
        subfilters.put(name, subfilter);
    }

    /**
     * {@inheritDoc}
     */
    public String getBasePath(String parameter, HttpServletRequest request)
    {
        return StringUtils.EMPTY;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public List<Criterion> getCriterionList(String parameter, HttpServletRequest request)
    {
        return ListUtils.EMPTY_LIST;
    }

    /**
     * {@inheritDoc}
     */
    public SortMode getSorting(String parameter, HttpServletRequest request)
    {
        return null;
    }

    /**
     * Returns the type.
     * @return the type
     */
    public String getControl()
    {
        return control;
    }

    /**
     * Sets the type.
     * @param type the type to set
     */
    public void setControl(String type)
    {
        this.control = type;
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

}
