/**
 *
 * Messages Module for Magnolia CMS (http://www.openmindlab.com/lab/products/messages.html)
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

package net.sourceforge.openutils.mgnlmessages.i18n;

import java.util.Enumeration;
import java.util.ResourceBundle;


/**
 * @author molaschi
 * @version $Id: $
 */
public class EmptyResourceBundle extends ResourceBundle
{

    /**
     *
     */
    public EmptyResourceBundle()
    {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Enumeration<String> getKeys()
    {

        return new Enumeration<String>()
        {

            /**
             * {@inheritDoc}
             */
            public boolean hasMoreElements()
            {
                return false;
            }

            /**
             * {@inheritDoc}
             */
            public String nextElement()
            {
                return null;
            }

        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Object handleGetObject(String arg0)
    {
        return null;
    }

}
