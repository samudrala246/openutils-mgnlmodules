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

package net.sourceforge.openutils.mgnlmessages.pages;

import info.magnolia.cms.core.Content;
import info.magnolia.cms.core.HierarchyManager;
import info.magnolia.cms.util.ClasspathResourcesUtil;
import info.magnolia.cms.util.NodeDataUtil;
import info.magnolia.context.MgnlContext;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import javax.jcr.RepositoryException;

import net.sourceforge.openutils.mgnlmessages.configuration.MessagesConfigurationManager;
import net.sourceforge.openutils.mgnlmessages.i18n.EmptyResourceBundle;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.slf4j.LoggerFactory;


/**
 * @author molaschi
 * @version $Id: DefaultMessagesImpl.java 4124 2008-09-22 14:56:46Z fgiust $
 */
public class NoDefaultBundlesMessagesImpl extends info.magnolia.cms.i18n.DefaultMessagesImpl
{

    /**
     * @param basename
     * @param locale
     */
    public NoDefaultBundlesMessagesImpl(String basename, Locale locale)
    {
        super(basename, locale);
        log = LoggerFactory.getLogger(getClass());

        log.debug("initializing bundle {}", basename);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String get(String key)
    {
        if (this.getBundle() == null)
        {
            return null;
        }
        String value = getFromRepository(key);

        if (value == null)
        {
            try
            {
                value = getBundle().getString(key);
            }
            catch (MissingResourceException e)
            {
                // ignore
            }
        }

        return value;
    }

    /**
     * @param key
     * @param value
     * @return
     */
    private String getFromRepository(String key)
    {
        try
        {
            String handle = StringUtils.replace(key, ".", "/");
            HierarchyManager hm = MgnlContext.getSystemContext().getHierarchyManager(
                MessagesConfigurationManager.MESSAGES_REPO);
            Content c = hm.getContent(handle);
            String locale1 = this.locale.getLanguage()
                + (StringUtils.isEmpty(this.locale.getCountry()) ? "" : "_" + this.locale.getCountry());

            if (c != null && c.hasNodeData(locale1))
            {
                return NodeDataUtil.getString(c, locale1);
            }

        }
        catch (MissingResourceException e)
        {
            // ignore
        }
        catch (RepositoryException e)
        {
            // ignore
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected ResourceBundle getBundle()
    {
        if (bundle == null)
        {
            InputStream stream = null;
            try
            {

                String file = "/"
                    + StringUtils.replace(basename, ".", "/")
                    + "_"
                    + getLocale().getLanguage()
                    + (StringUtils.isEmpty(getLocale().getCountry()) ? "" : ("_" + getLocale().getCountry()))
                    + ".properties";

                log.debug("loading bundle {}", file);

                stream = ClasspathResourcesUtil.getStream(file, false);

                if (stream != null)
                {
                    bundle = new PropertyResourceBundle(stream);
                }
                else
                {
                    // empty bundle
                    bundle = new EmptyResourceBundle();
                }
            }
            catch (IOException e)
            {
                log.error("can't load messages for " + basename);
            }
            finally
            {
                IOUtils.closeQuietly(stream);
            }
        }
        return bundle;
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals(Object object)
    {
        if (!(object instanceof NoDefaultBundlesMessagesImpl))
        {
            return false;
        }
        NoDefaultBundlesMessagesImpl rhs = (NoDefaultBundlesMessagesImpl) object;
        return new EqualsBuilder().append(this.basename, rhs.basename).append(this.locale, rhs.locale).isEquals();
    }

    /**
     * {@inheritDoc}
     */
    public int hashCode()
    {
        return new HashCodeBuilder(-399088031, -1971683455).append(basename).append(locale).toHashCode();
    }

}
