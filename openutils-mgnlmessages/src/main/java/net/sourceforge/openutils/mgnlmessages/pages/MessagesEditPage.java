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

package net.sourceforge.openutils.mgnlmessages.pages;

import info.magnolia.cms.core.MetaData;
import info.magnolia.cms.core.Path;
import info.magnolia.cms.i18n.Messages;
import info.magnolia.context.MgnlContext;
import info.magnolia.context.SystemContext;
import info.magnolia.jcr.util.MetaDataUtil;
import info.magnolia.module.admininterface.TemplatedMVCHandler;
import info.magnolia.objectfactory.Components;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.openutils.mgnlmessages.configuration.MessagesConfigurationManager;
import net.sourceforge.openutils.mgnlmessages.el.MessagesEl;
import net.sourceforge.openutils.mgnlmessages.lifecycle.MessagesModuleLifecycle;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.xml.internal.ws.util.MetadataUtil;


/**
 * @author manuel
 * @version $Id
 */
public class MessagesEditPage extends TemplatedMVCHandler
{

    private static final String JSON_VIEW = "js";

    /**
     * Logger.
     */
    private Logger log = LoggerFactory.getLogger(MessagesEditPage.class);

    private List<String> keys;

    private List<Locale> locales;

    private String currentLanguage;

    private String json;

    private String key;

    private String locale;

    private String text;

    private String newkey;

    /**
     * @param name
     * @param request
     * @param response
     */
    public MessagesEditPage(String name, HttpServletRequest request, HttpServletResponse response)
    {
        super(name, request, response);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void init()
    {
        super.init();
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public void renderHtml(String view) throws IOException
    {
        if (JSON_VIEW.equals(view))
        {
            this.response.setContentType("text/json");
            this.response.getWriter().write(json);
        }
        else
        {

            keys = new ArrayList<String>();
            Iterator<String> it = MgnlContext.getMessages().keys();
            while (it.hasNext())
            {
                keys.add(it.next());
            }
            Collections.sort(keys);

            locales = MessagesConfigurationManager.getAvaiableLocales();

            Locale loc = this.request.getLocale();
            if (locales != null && !locales.isEmpty() && !locales.contains(loc))
            {
                // MESSAGES-17 Dropdown options may not contain the selected language
                loc = locales.get(0);
            }
            currentLanguage = loc.getLanguage();
            if (loc.getCountry() != null && loc.getCountry().length() > 0)
            {
                currentLanguage += "_" + loc.getCountry();
            }

            super.renderHtml(view);
        }
    }

    public String loadkey()
    {
        Locale locBk = MgnlContext.getLocale();
        Locale newLoc = null;
        if (locale.indexOf('_') > 0)
        {
            newLoc = new Locale(StringUtils.substringBefore(locale, "_"), StringUtils.substringAfter(locale, "_"));
        }
        else
        {
            newLoc = new Locale(locale);
        }
        MgnlContext.setLocale(newLoc);

        json = "rootObj = {value: '"
            + StringUtils.defaultString(StringUtils.replaceEach(MgnlContext.getMessages().get(key), new String[]{
                "'",
                "\n" }, new String[]{"\\'", "\\n" }))
            + "'}";

        MgnlContext.setLocale(locBk);

        return JSON_VIEW;
    }

    public String savekey()
    {
        try
        {
            MessagesConfigurationManager.saveKeyValue(key, text, locale);
            json = "rootObj = {value: 'OK'}";
        }
        catch (RepositoryException e)
        {
            log.error("Error saving key", e);
            json = "rootObj = {value: 'KO'}";
        }

        return JSON_VIEW;
    }

    public String removekey()
    {
        try
        {
            Session session = Components.getComponent(SystemContext.class).getJCRSession(MessagesModuleLifecycle.REPO);

            String path = "/" + StringUtils.replace(key, ".", "/");
            session.removeItem(path);
            session.save();

            String parent = StringUtils.substringBeforeLast(path, "/");
            if (!StringUtils.isEmpty(parent))
            {
                if (!session.getNode(parent).hasNodes())
                {
                    key = StringUtils.replace(parent.substring(1), "/", ".");
                    removekey();
                }
            }
            json = "rootObj = {value: 'OK'}";
        }
        catch (PathNotFoundException e)
        {
            json = "rootObj = {value: 'NOTFOUND'}";
        }
        catch (RepositoryException e)
        {
            log.error("Error removing key", e);
            json = "rootObj = {value: 'KO'}";
        }

        return JSON_VIEW;
    }

    public String renamekey()
    {
        try
        {
            moveNode("/" + StringUtils.replace(key, ".", "/"), "/" + StringUtils.replace(newkey, ".", "/"));
            json = "rootObj = {value: 'OK'}";
        }
        catch (RepositoryException e)
        {
            log.error("Error removing key", e);
            json = "rootObj = {value: 'KO'}";
        }

        return JSON_VIEW;
    }

    @SuppressWarnings("unchecked")
    public String search()
    {
        Set<String> keySet = new HashSet<String>();
        if (!StringUtils.isBlank(text))
        {
            Locale locBk = MgnlContext.getLocale();
            for (Locale newLoc : MessagesConfigurationManager.getAvaiableLocales())
            {
                MgnlContext.setLocale(newLoc);
                Messages messages = MgnlContext.getMessages();
                Iterator<String> it = messages.keys();
                while (it.hasNext())
                {
                    String curKey = it.next();
                    String curText = messages.get(curKey);
                    if (MessagesEl.messageTextContains(curText, text))
                    {
                        keySet.add(curKey);
                    }
                }
            }
            MgnlContext.setLocale(locBk);
        }

        keys = new ArrayList<String>(keySet);
        Collections.sort(keys);

        StringBuilder sb = new StringBuilder("[ ");
        int i = 0;
        for (String curKey : keys)
        {
            if (i > 0)
            {
                sb.append(", ");
            }
            sb.append("{ id: '").append(i++).append("', key: '").append(curKey).append("' }");
        }
        sb.append(" ]");
        json = sb.toString();

        return JSON_VIEW;
    }

    /**
     * move a node (from AdminTreeMVCHandler copymoveNode)
     * @param source source node
     * @param destination destination folder
     * @throws RepositoryException repository exception
     */
    protected void moveNode(String source, String destination) throws RepositoryException
    {
        Session session = Components.getComponent(SystemContext.class).getJCRSession(MessagesModuleLifecycle.REPO);

        String goTo = destination;

        if (session.itemExists(destination))
        {
            String parentPath = StringUtils.substringBeforeLast(destination, "/"); //$NON-NLS-1$
            String label = StringUtils.substringAfterLast(destination, "/"); //$NON-NLS-1$
            label = Path.getUniqueLabel(session, parentPath, label);
            goTo = parentPath + "/" + label; //$NON-NLS-1$
        }
        if (destination.indexOf(source + "/") == 0)
        {

            return;
        }

        try
        {
            session.move(source, goTo);
        }
        catch (Exception e)
        {
            return;
        }

        Node newContent = session.getNode(destination);
        try
        {
            MetaDataUtil.updateMetaData(newContent);
            MetaDataUtil.getMetaData(newContent).setUnActivated();
        }
        catch (RepositoryException e)
        {
            if (log.isDebugEnabled())
            {
                log.debug("Exception caught: " + e.getMessage(), e); //$NON-NLS-1$
            }
        }
        session.save();
    }

    /**
     * Returns the keys.
     * @return the keys
     */
    public List<String> getKeys()
    {
        return keys;
    }

    /**
     * Sets the keys.
     * @param keys the keys to set
     */
    public void setKeys(List<String> keys)
    {
        this.keys = keys;
    }

    /**
     * Returns the locales.
     * @return the locales
     */
    public List<Locale> getLocales()
    {
        return locales;
    }

    /**
     * Sets the locales.
     * @param locales the locales to set
     */
    public void setLocales(List<Locale> locales)
    {
        this.locales = locales;
    }

    /**
     * Returns the currentLanguage.
     * @return the currentLanguage
     */
    public String getCurrentLanguage()
    {
        return currentLanguage;
    }

    /**
     * Sets the currentLanguage.
     * @param currentLanguage the currentLanguage to set
     */
    public void setCurrentLanguage(String currentLanguage)
    {
        this.currentLanguage = currentLanguage;
    }

    /**
     * Returns the key.
     * @return the key
     */
    public String getKey()
    {
        return key;
    }

    /**
     * Sets the key.
     * @param key the key to set
     */
    public void setKey(String key)
    {
        this.key = key;
    }

    /**
     * Returns the locale.
     * @return the locale
     */
    public String getLocale()
    {
        return locale;
    }

    /**
     * Sets the locale.
     * @param locale the locale to set
     */
    public void setLocale(String locale)
    {
        this.locale = locale;
    }

    /**
     * Returns the text.
     * @return the text
     */
    public String getText()
    {
        return text;
    }

    /**
     * Sets the text.
     * @param text the text to set
     */
    public void setText(String text)
    {
        this.text = text;
    }

    /**
     * Returns the newkey.
     * @return the newkey
     */
    public String getNewkey()
    {
        return newkey;
    }

    /**
     * Sets the newkey.
     * @param newkey the newkey to set
     */
    public void setNewkey(String newkey)
    {
        this.newkey = newkey;
    }

}
