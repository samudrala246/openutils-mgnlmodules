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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import net.sourceforge.openutils.mgnlcriteria.jcr.query.criterion.Criterion;
import net.sourceforge.openutils.mgnlcriteria.jcr.query.criterion.Restrictions;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;


/**
 * @author cstrappazzon
 * @version $Id$
 */
public class SearchFilterBeetweenCreationDate extends SearchFilterAbstract
{

    private static final String META_DATA_MGNL_CREATIONDATE = "MetaData/@mgnl:creationdate";

    private Calendar from;

    private Calendar to;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Criterion> getCriterionList(String parameter, HttpServletRequest request)
    {
        String[] values = (String[]) request.getParameterMap().get(parameter);
        List<Criterion> criterionList = new ArrayList<Criterion>();

        if (values != null && values.length > 0 && StringUtils.isNotBlank(values[0]))
        {
            Criterion c;
            // From
            from = getDate(values[0]);
            c = Restrictions.gt(META_DATA_MGNL_CREATIONDATE, from);
            if (MapUtils.isNotEmpty(getSubfilters()))
            {
                String key = getSubfilters().keySet().iterator().next();
                values = (String[]) request.getParameterMap().get(key);
                if (StringUtils.isNotBlank(values[0]))
                {
                    to = getDate(values[0]);
                    c = Restrictions.between(META_DATA_MGNL_CREATIONDATE, from, to);
                }
            }
            criterionList.add(c);
        }
        return criterionList;

    }

    private Calendar getDate(String date)
    {
        Calendar cal = Calendar.getInstance();

        Pattern datePattern = Pattern.compile("(\\d{4})-(\\d{2})-(\\d{2})");
        Matcher dateMatcher = datePattern.matcher(date);

        try
        {
            if (dateMatcher.find())
            {
                cal.set(
                    Integer.parseInt(dateMatcher.group(1)),
                    Integer.parseInt(dateMatcher.group(2)) - 1,
                    Integer.parseInt(dateMatcher.group(3)));
            }
        }
        catch (Exception e)
        {
            log.error("Invalid date: {}", date);
        }

        return cal;
    }

    /**
     * Returns the from.
     * @return the from
     */
    public Calendar getFrom()
    {
        return from;
    }

    /**
     * Sets the from.
     * @param from the from to set
     */
    public void setFrom(Calendar from)
    {
        this.from = from;
    }

    /**
     * Returns the to.
     * @return the to
     */
    public Calendar getTo()
    {
        return to;
    }

    /**
     * Sets the to.
     * @param to the to to set
     */
    public void setTo(Calendar to)
    {
        this.to = to;
    }
}
