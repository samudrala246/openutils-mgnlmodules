/**
 *
 * Messages Module for Magnolia CMS (http://www.openmindlab.com/lab/products/messages.html)
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

package net.sourceforge.openutils.mgnlmessages.i18n;

import info.magnolia.cms.i18n.DefaultMessagesImpl;

import java.util.Locale;


/**
 * Needed just because DefaultMessagesImpl has a protected constructor.
 * @author fgiust
 * @version $Id$
 */
public class OpenutilsMessagesImpl extends DefaultMessagesImpl
{

    /**
     * @param basename
     * @param locale
     */
    protected OpenutilsMessagesImpl(String basename, Locale locale)
    {
        super(basename, locale);
    }

}
