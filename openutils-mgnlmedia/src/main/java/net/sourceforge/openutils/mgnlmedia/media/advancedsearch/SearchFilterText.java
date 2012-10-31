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
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.sourceforge.openutils.mgnlcriteria.jcr.query.criterion.Criterion;
import net.sourceforge.openutils.mgnlcriteria.jcr.query.criterion.Restrictions;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;


/**
 * @author cstrappazzon
 * @version $Id$
 */
public class SearchFilterText extends SearchFilterAbstract
{

    private boolean wildcards;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Criterion> getCriterionList(String parameter, HttpServletRequest request)
    {
        String fullTextSearch;
        String[] values = (String[]) request.getParameterMap().get(parameter);

        List<Criterion> criterionList = new ArrayList<Criterion>();

        if (values != null && values.length > 0)
        {
            fullTextSearch = values[0];

            if (wildcards)
            {
                fullTextSearch = StringUtils.replace(fullTextSearch, "*", "%");

                fullTextSearch = StringUtils.stripStart(StringUtils.stripEnd(fullTextSearch, "%"), "%");
            }

            if (MapUtils.isNotEmpty(getSubfilters()) && StringUtils.isNotBlank(fullTextSearch))
            {

                for (String key : getSubfilters().keySet())
                {
                    values = request.getParameterValues(key);

                    if (wildcards && (values == null || values.length == 0))
                    {

                        SearchFilterOptionProvider infilter = (SearchFilterOptionProvider) getSubfilters().get(key);

                        if (infilter != null)
                        {
                            List<Option> options = infilter.getOptions();
                            List<String> optionparams = new ArrayList<String>();
                            for (Option option : options)
                            {
                                optionparams.add(option.getValue());
                            }
                            values = optionparams.toArray(new String[optionparams.size()]);
                        }
                    }

                    if (values != null && values.length != 0)
                    {
                        for (String name : values)
                        {
                            if (wildcards)
                            {
                                criterionList.add(Restrictions.like("@" + name, fullTextSearch));
                            }
                            else
                            {
                                criterionList.add(Restrictions.contains("@" + name, fullTextSearch));
                            }
                        }
                        if (criterionList.size() > 1)
                        {
                            // put criterionList on or
                            Criterion c = Restrictions.or(criterionList.get(0), criterionList.get(1));
                            for (int i = 2; i < criterionList.size(); i++)
                            {
                                c = Restrictions.or(c, criterionList.get(i));
                            }
                            criterionList.removeAll(criterionList);
                            criterionList.add(c);
                        }
                    }
                }
            }

            if (StringUtils.isNotBlank(fullTextSearch))
            {
                if (wildcards && criterionList.size() > 0)
                {
                    // jcr:like doesn't work on fulltext, but put criterionList on or
                    Criterion c = Restrictions.or(criterionList.get(0), Restrictions.contains(".", fullTextSearch));
                    criterionList.remove(0);
                    criterionList.add(0, c);
                }
                else
                {
                    criterionList.add(Restrictions.contains(".", fullTextSearch));
                }
            }
        }
        return criterionList;
    }

    /**
     * Sets the wildcards.
     * @param wildcards the wildcards to set
     */
    public void setWildcards(boolean wildcards)
    {
        this.wildcards = wildcards;
    }
}
