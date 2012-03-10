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

package net.sourceforge.openutils.mgnlmessages.el;

import info.magnolia.cms.i18n.I18nContentSupportFactory;
import info.magnolia.cms.i18n.MessagesManager;
import info.magnolia.cms.security.Permission;
import info.magnolia.context.MgnlContext;

import java.text.MessageFormat;

import net.sourceforge.openutils.elfunctions.ElStringUtils;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;


public class MessagesEl
{

    /*
     * Per rendere editabili i messaggi complessi con argomenti e link è necessario: - scrivere nel tag il message non
     * formattato in default e locale corrente - modificare msg-global.tag per recuperare e visualizzare correttamente i
     * dati passati al tag - per i messaggi con link e necessario non raddoppiare gli apici o poter discrimininare nel
     * salvataggio o far inserire i doppi apici a chi modifica le label in pagina
     */

    public static String message(String key, boolean fallback, boolean defaultLocale, Object[] arguments)
    {
        if (defaultLocale)
        {
            return MessagesManager.getMessages(I18nContentSupportFactory.getI18nSupport().getFallbackLocale()).get(
                key,
                arguments);
        }
        else
        {
            String value = MessagesManager.getMessages(I18nContentSupportFactory.getI18nSupport().getLocale()).get(
                key,
                arguments);
            if (fallback && (StringUtils.isBlank(value) || StringUtils.startsWith(value, "???")))
            {
                return MessagesManager.getMessages(I18nContentSupportFactory.getI18nSupport().getFallbackLocale()).get(
                    key,
                    arguments);
            }
            return value;
        }
    }

    private static final String MSGS_TPL = "<span class=\"msgEdit {0}\" data-msgkey=\"{1}\" data-msglocale=\"{2}\" data-msgdefaultlocale=\"{3}\" data-msgdefault=\"{4}\">{5}</span>";

    public static String messageSimple(String key, Object[] arguments)
    {
        if (MgnlContext.getAggregationState().getMainContent() != null
            && MagnoliaTemplatingUtilities.getInstance().isEditMode()
            && MgnlContext.getAggregationState().getMainContent().isGranted(Permission.WRITE)) // equivalent of
                                                                                               // CmsFunctions.canEdit()
        {
            String keyCssClass = StringUtils.replace(key, ".", "_");

            String tag = MessageFormat.format(MSGS_TPL, keyCssClass, key, I18nContentSupportFactory
                .getI18nSupport()
                .getLocale(), I18nContentSupportFactory.getI18nSupport().getFallbackLocale(),
            // evito di sostituire i placeholder quando sono in
            // edit-mode
            // StringEscapeUtils.escapeHtml(message(key, false, true,
            // arguments)),
                StringEscapeUtils.escapeHtml(message(key, false, true)),
                // evito di sostituire i placeholder quando sono in
                // edit-mode
                // message(key, false, false, arguments));
                message(key, false, false));
            return tag;
        }
        return message(key, true, false, arguments);
    }

    public static String messagePlain(String key, Object[] arguments)
    {
        return message(key, true, false, arguments);
    }

    public static String messageSimple(String key)
    {
        return messageSimple(key, null);
    }

    public static String message(String key, boolean fallback, boolean defaultLocale)
    {
        return message(key, fallback, defaultLocale, null);
    }

    public static String messagePlain(String key)
    {
        return messagePlain(key, null);
    }

    public static String messageSimple(String key, Object argument)
    {
        return messageSimple(key, new Object[]{argument });
    }

    public static String message(String key, boolean fallback, boolean defaultLocale, Object argument)
    {
        return message(key, fallback, defaultLocale, new Object[]{argument });
    }

    public static String messagePlain(String key, Object argument)
    {
        return messagePlain(key, new Object[]{argument });
    }

    public static String messageSimple(String key, Object arg1, Object arg2)
    {
        return messageSimple(key, new Object[]{arg1, arg2 });
    }

    public static String message(String key, boolean fallback, boolean defaultLocale, Object arg1, Object arg2)
    {
        return message(key, fallback, defaultLocale, new Object[]{arg1, arg2 });
    }

    public static String messagePlain(String key, Object arg1, Object arg2)
    {
        return messagePlain(key, new Object[]{arg1, arg2 });
    }

    public static String messagePlain(String key, Object arg1, Object arg2, Object arg3)
    {
        return messagePlain(key, new Object[]{arg1, arg2, arg3 });
    }

    public static String escapeJs(String msg)
    {
        return StringEscapeUtils.escapeJavaScript(msg);
    }

    public static boolean messageTextContains(String str, String searchStr)
    {
        return StringUtils.containsIgnoreCase(
            ElStringUtils.stripHtmlTags(StringEscapeUtils.unescapeHtml(str)),
            searchStr);
    }
}
