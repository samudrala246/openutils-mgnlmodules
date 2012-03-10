/**
 *
 * Stripes module for Magnolia CMS (http://www.openmindlab.com/lab/products/mgnlstripes.html)
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

package it.openutils.magnoliastripes.extra;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import net.sourceforge.stripes.exception.StripesRuntimeException;
import net.sourceforge.stripes.format.Formatter;


/**
 * Date formatter with support for Calendars. Copied from default Stripes DateFormatter.
 * @author Danilo Ghirardelli
 */
public class CalendarFormatter implements Formatter<Calendar>
{

    protected static final Map<String, Integer> NAMED_PATTERNS = new HashMap<String, Integer>();
    static
    {
        NAMED_PATTERNS.put("short", DateFormat.SHORT);
        NAMED_PATTERNS.put("medium", DateFormat.MEDIUM);
        NAMED_PATTERNS.put("long", DateFormat.LONG);
        NAMED_PATTERNS.put("full", DateFormat.FULL);
    }

    private String formatType;

    private String formatPattern;

    private Locale locale;

    private DateFormat format;

    public void setFormatType(String formatType)
    {
        this.formatType = formatType;
    }

    public String getFormatType()
    {
        return formatType;
    }

    public void setFormatPattern(String formatPattern)
    {
        this.formatPattern = formatPattern;
    }

    public String getFormatPattern()
    {
        return formatPattern;
    }

    public void setLocale(Locale locale)
    {
        this.locale = locale;
    }

    public Locale getLocale()
    {
        return locale;
    }

    /**
     * Constructs the DateFormat used for formatting, based on the values passed to the various setter methods on the
     * class. If the formatString is one of the named formats then a DateFormat instance is created of the specified
     * type and format, otherwise a SimpleDateFormat is constructed using the pattern provided and the formatType is
     * ignored.
     */
    public void init()
    {
        // Default these values if they were not supplied
        if (formatPattern == null)
        {
            formatPattern = "short";
        }
        if (formatType == null)
        {
            formatType = "date";
        }
        String lcFormatString = formatPattern.toLowerCase();
        String lcFormatType = formatType.toLowerCase();
        // Now figure out how to construct our date format for our locale
        if (NAMED_PATTERNS.containsKey(lcFormatString))
        {
            if (lcFormatType.equals("date"))
            {
                format = DateFormat.getDateInstance(NAMED_PATTERNS.get(lcFormatString), locale);
            }
            else if (lcFormatType.equals("datetime"))
            {
                format = DateFormat.getDateTimeInstance(
                    NAMED_PATTERNS.get(lcFormatString),
                    NAMED_PATTERNS.get(lcFormatString),
                    locale);
            }
            else if (lcFormatType.equals("time"))
            {
                format = DateFormat.getTimeInstance(NAMED_PATTERNS.get(lcFormatString), locale);
            }
            else
            {
                throw new StripesRuntimeException("Invalid formatType for Date: "
                    + formatType
                    + ". Allowed types are 'date', 'time' and 'datetime'.");
            }
        }
        else
        {
            format = new SimpleDateFormat(formatPattern, locale);
        }
    }

    public String format(Calendar input)
    {
        if (input != null)
        {
            return this.format.format(input.getTime());
        }
        return null;
    }
}