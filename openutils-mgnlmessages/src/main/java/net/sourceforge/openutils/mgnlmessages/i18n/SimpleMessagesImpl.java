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
import info.magnolia.cms.i18n.MessagesManager;
import info.magnolia.cms.util.ClasspathResourcesUtil;
import info.magnolia.objectfactory.Components;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.apache.commons.collections.IteratorUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;


/**
 * @author fgiust
 * @version $Id: $
 */
public class SimpleMessagesImpl extends AbstractMessagesImpl
{

    /**
     * @param basename
     * @param locale
     */
    protected SimpleMessagesImpl(String basename, Locale locale)
    {
        super(basename, locale);
    }

    /**
     * Get the message from the bundle
     * @param key the key
     * @return message
     */
    public String get(String key)
    {
        if (key == null)
        {
            return null;
        }
        try
        {
            return getBundle().getString(key);
        }
        catch (MissingResourceException e)
        {
            return null;
        }
    }

    /**
     * @return Returns the bundle for the current basename
     */
    protected ResourceBundle getBundle()
    {
        if (bundle == null)
        {
            InputStream stream = null;
            try
            {
                stream = ClasspathResourcesUtil.getStream("/"
                    + StringUtils.replace(basename, ".", "/")
                    + "_"
                    + getLocale().getLanguage()
                    + "_"
                    + getLocale().getCountry()
                    + ".properties", false);
                if (stream == null)
                {
                    stream = ClasspathResourcesUtil.getStream("/"
                        + StringUtils.replace(basename, ".", "/")
                        + "_"
                        + getLocale().getLanguage()
                        + ".properties", false);
                }
                if (stream == null)
                {
                    stream = ClasspathResourcesUtil.getStream("/"
                        + StringUtils.replace(basename, ".", "/")
                        + "_"
                        + Components.getComponent(MessagesManager.class).getDefaultLocale().getLanguage()
                        + ".properties", false);
                }
                if (stream == null)
                {
                    stream = ClasspathResourcesUtil.getStream("/"
                        + StringUtils.replace(basename, ".", "/")
                        + ".properties", false);
                }

                if (stream != null)
                {
                    bundle = new PropertyResourceBundle(stream);
                }
                else
                {
                    bundle = new EmptyResourceBundle();
                }
            }
            catch (IOException e)
            {
                log.error("can't load messages for " + basename, e);

                // bundle must never be null
                bundle = new EmptyResourceBundle();
            }
            finally
            {
                IOUtils.closeQuietly(stream);
            }
        }

        if (bundle == null)
        {
            log.warn("Got a request for a missing bundle {}, please check your configuration", basename);
        }
        return bundle;
    }

    /**
     * {@inheritDoc}
     */
    public void reload() throws Exception
    {
        this.bundle = null;
    }

    /**
     * Iterate over the keys
     * @return iterator
     */
    @SuppressWarnings("unchecked")
    public Iterator keys()
    {
        return IteratorUtils.asIterator(this.getBundle().getKeys());
    }

}
