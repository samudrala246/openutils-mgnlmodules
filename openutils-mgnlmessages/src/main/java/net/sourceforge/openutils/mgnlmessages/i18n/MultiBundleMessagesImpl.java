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

import info.magnolia.cms.i18n.AbstractMessagesImpl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Set;

import net.sourceforge.openutils.mgnlmessages.configuration.MessagesConfigurationManager;

import org.apache.commons.collections.IteratorUtils;
import org.apache.commons.lang.StringUtils;


/**
 * @author molaschi
 * @version $Id$
 */
public class MultiBundleMessagesImpl extends AbstractMessagesImpl
{

    private List<OpenutilsMessagesImpl> messages;

    /**
     * @param basename
     * @param locale
     */
    public MultiBundleMessagesImpl(Locale locale)
    {
        super(null, locale);
        messages = new ArrayList<OpenutilsMessagesImpl>();
        for (String basename : MessagesConfigurationManager.getBaseNames())
        {
            messages.add(new OpenutilsMessagesImpl(basename, locale));
        }
    }

    /**
     * {@inheritDoc}
     */
    public String get(String key)
    {
        for (OpenutilsMessagesImpl message : messages)
        {
            String value = message.get(key);
            if (StringUtils.isNotBlank(value) && !StringUtils.startsWith(value, "???"))
            {
                return value;
            }
        }
        return "???" + key + "???";
    }

    /**
     * {@inheritDoc}
     */
    public Iterator<String> keys()
    {
        Set<String> keys = new HashSet<String>();
        for (OpenutilsMessagesImpl message : messages)
        {
            try
            {
                keys.addAll(IteratorUtils.toList(message.keys()));
            }
            catch (MissingResourceException ex)
            {
                // ignore
            }
        }
        return keys.iterator();
    }

    /**
     * {@inheritDoc}
     */
    public void reload() throws Exception
    {
        for (OpenutilsMessagesImpl message : messages)
        {
            message.reload();
        }
    }

}
