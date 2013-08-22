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

import info.magnolia.cms.core.Content;
import info.magnolia.cms.core.HierarchyManager;
import info.magnolia.cms.i18n.AbstractMessagesImpl;
import info.magnolia.cms.util.NodeDataUtil;
import info.magnolia.cms.util.ObservationUtil;
import info.magnolia.context.MgnlContext;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;

import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.RepositoryException;
import javax.jcr.observation.EventIterator;
import javax.jcr.observation.EventListener;

import net.sourceforge.openutils.mgnlcriteria.jcr.query.AdvancedResult;
import net.sourceforge.openutils.mgnlcriteria.jcr.query.AdvancedResultItem;
import net.sourceforge.openutils.mgnlcriteria.jcr.query.Criteria;
import net.sourceforge.openutils.mgnlcriteria.jcr.query.JCRCriteriaFactory;
import net.sourceforge.openutils.mgnlcriteria.jcr.query.criterion.Order;
import net.sourceforge.openutils.mgnlcriteria.jcr.query.criterion.Restrictions;
import net.sourceforge.openutils.mgnlmessages.configuration.MessagesConfigurationManager;

import org.apache.commons.lang.StringUtils;


/**
 * @author molaschi
 * @version $Id: RepositoryMessagesImpl.java 4465 2008-09-28 10:59:58Z fgiust $
 */
public class RepositoryMessagesImpl extends AbstractMessagesImpl
{

    private List<String> keys;

    /**
     * @param basename
     * @param locale
     */
    public RepositoryMessagesImpl(String basename, Locale locale)
    {
        super(basename, locale);
        ObservationUtil.registerChangeListener(MessagesConfigurationManager.MESSAGES_REPO, "/", new EventListener()
        {

            public void onEvent(EventIterator events)
            {
                keys = null;
            }
        });
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
            return "??????";
        }
        try
        {
            String handle = StringUtils.replace(key, ".", "/");
            HierarchyManager hm = MgnlContext.getSystemContext().getHierarchyManager(
                MessagesConfigurationManager.MESSAGES_REPO);
            Content c = hm.getContent(handle);
            String locale1 = this.locale.getLanguage() + "_" + this.locale.getCountry();
            String locale2 = this.locale.getLanguage();
            if (c == null || (!c.hasNodeData(locale1) && !c.hasNodeData(locale2)))
            {
                return "???" + key + "???";
            }
            if (c.hasNodeData(locale1))
            {
                return NodeDataUtil.getString(c, locale1);
            }
            else
            {
                return NodeDataUtil.getString(c, locale2);
            }
        }
        catch (MissingResourceException e)
        {
            return "???" + key + "???";
        }
        catch (RepositoryException e)
        {
            return "???" + key + "???";
        }
        catch (IllegalArgumentException e)
        {
            // MESSAGES-19 Uncaught IllegalArgumentException using jackrabbit 2.4.1
            // at org.apache.jackrabbit.core.id.NodeId.<init>(NodeId.java:129)
            return "???" + key + "???";
        }
    }

    /**
     * {@inheritDoc}
     */
    public void reload() throws Exception
    {
        keys = null;
    }

    /**
     * {@inheritDoc}
     */
    public Iterator<String> keys()
    {

        long ms = System.currentTimeMillis();

        if (keys == null)
        {

            keys = new ArrayList<String>();

            Criteria criteria = JCRCriteriaFactory
                .createCriteria()
                .setWorkspace(MessagesConfigurationManager.MESSAGES_REPO)
                .setBasePath("//*")
                .add(Restrictions.eq("jcr:primaryType", "mgnl:contentNode"))
                .addOrder(Order.desc("@jcr:score"));

            AdvancedResult result = criteria.execute();

            log.debug(
                "Number of messages loaded: {} with query {}",
                result.getTotalSize(),
                criteria.toXpathExpression());

            for (AdvancedResultItem c : result.getItems())
            {

                PropertyIterator properties;
                try
                {
                    properties = c.getProperties();

                    while (properties.hasNext())
                    {
                        Property nextProperty = properties.nextProperty();

                        try
                        {
                            String key = StringUtils
                                .substring(StringUtils.replace(nextProperty.getPath(), "/", "."), 1);
                            if (StringUtils.isNotEmpty(key))
                            {
                                keys.add(key);
                            }
                        }
                        catch (RepositoryException e)
                        {
                            // ignore
                        }
                    }
                }
                catch (RepositoryException e1)
                {
                    // properties can't be read, ignore
                }
            }
        }

        log.debug("Messages loaded in {} ms", System.currentTimeMillis() - ms);

        return keys.iterator();
    }
}
