/**
 *
 * E-learning Module for Magnolia CMS (http://www.openmindlab.com/lab/products/lms.html)
 * Copyright(C) 2010-2012, Openmind S.r.l. http://www.openmindonline.it
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

package net.sourceforge.openutils.mgnllms.report;

/**
 * @author carlo
 * @version $Id: $
 */
public class Helper
{

    public static Boolean getBoolean(Object o)
    {
        if (o == null)
            return null;
        String s = o.toString();
        if (s.equals("unknown"))
            return null;
        if (s.equals("true"))
            return true;
        if (s.equals("false"))
            return false;
        return null;
    }

    public static Double getDouble(Object o)
    {
        if (o == null)
            return null;
        if (o.toString().equals("unknown"))
            return null;
        else
            return Double.valueOf(o.toString());
    }

    public static Integer getInt(Object o)
    {
        if (o == null)
            return null;
        if (o.toString().equals("unknown"))
            return null;
        else
            return (Integer) o;
    }
}
