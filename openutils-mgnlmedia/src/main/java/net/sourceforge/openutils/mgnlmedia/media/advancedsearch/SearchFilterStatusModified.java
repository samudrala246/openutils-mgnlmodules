/**
 *
 * SimpleMedia Module for Magnolia CMS (http://www.openmindlab.com/lab/products/media.html)
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

package net.sourceforge.openutils.mgnlmedia.media.advancedsearch;

import info.magnolia.cms.core.MetaData;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import net.sourceforge.openutils.mgnlcriteria.jcr.query.criterion.Criterion;
import net.sourceforge.openutils.mgnlcriteria.jcr.query.criterion.Restrictions;

import org.apache.commons.lang.StringUtils;


/**
 * @author ADMIN
 * @version $Id: $
 */
public class SearchFilterStatusModified extends SearchFilterAbstract
{
    private static final String META_DATA_MGNL_ACTIVATED = "MetaData/@mgnl:" + MetaData.ACTIVATED;

    private static final String META_DATA_MGNL_LAST_ACTION = "MetaData/@mgnl:" + MetaData.LAST_ACTION;

    private static final String META_DATA_MGNL_LAST_MODIFIED = "MetaData/@mgnl:" + MetaData.LAST_MODIFIED;
    
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
            criterionList.add(Restrictions.eq(META_DATA_MGNL_ACTIVATED, "true"));

            Calendar date = getDate(values[0]);
            criterionList.add(Restrictions.le(META_DATA_MGNL_LAST_ACTION, date));
            criterionList.add(Restrictions.gt(META_DATA_MGNL_LAST_MODIFIED, date));
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
}
