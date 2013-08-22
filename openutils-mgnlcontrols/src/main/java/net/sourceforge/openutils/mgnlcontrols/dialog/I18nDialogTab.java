/**
 *
 * Controls module for Magnolia CMS (http://www.openmindlab.com/lab/products/controls.html)
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

package net.sourceforge.openutils.mgnlcontrols.dialog;

import info.magnolia.cms.core.Content;
import info.magnolia.cms.gui.dialog.DialogControlImpl;
import info.magnolia.cms.gui.dialog.DialogTab;
import info.magnolia.cms.i18n.I18nContentSupportFactory;
import info.magnolia.cms.security.AccessDeniedException;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author dschivo
 * @version $Id$
 */
public class I18nDialogTab extends DialogTab
{

    /**
     * Used to make sure that the javascript files are loaded only once
     */
    private static final String ATTRIBUTE_MOOTOOLS_LOADED = "info.magnolia.cms.gui.dialog.mootools.loaded";

    private static final String ATTRIBUTE_JQUERY_LOADED = "info.magnolia.cms.gui.dialog.jquery.loaded";

    private static final String ATTRIBUTE_I18NTAB_LOADED = "info.magnolia.cms.gui.dialog.i18ntab.loaded";

    private Map<String, String> mapLocales = new LinkedHashMap<String, String>();

    /**
     * Logger.
     */
    private static Logger log = LoggerFactory.getLogger(I18nDialogTab.class);

    private Set<String> localesToRender;

    private String localeSuffixSeparator;

    private String visibleLocale;

    private String newLocale = StringUtils.EMPTY;

    private String jsFramework;

    /**
     * {@inheritDoc}
     */
    @Override
    public void init(HttpServletRequest request, HttpServletResponse response, Content storageNode, Content configNode)
        throws RepositoryException
    {
        super.init(request, response, storageNode, configNode);
        localeSuffixSeparator = getConfigValue("localeSuffixSeparator", "_");
        newLocale = request.getParameter("mgnlI18nLocale");

        setConfiguredLanguages(configNode);

        if (newLocale != null)
        {
            localesToRender = Collections.singleton(newLocale);
            visibleLocale = newLocale;
        }
        else
        {
            visibleLocale = getConfigValue("defaultLocale", mapLocales.keySet().iterator().next());
            localesToRender = new HashSet<String>();
            Content node = getStorageNode();
            for (String locale : mapLocales.keySet())
            {
                if (locale.equals(visibleLocale)
                    || (node != null && !node.getNodeDataCollection('*' + localeSuffixSeparator + locale).isEmpty()))
                {
                    localesToRender.add(locale);
                }
            }
        }

        jsFramework = getConfigValue("jsFramework", "jquery");

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void drawHtmlPreSubs(Writer out) throws IOException
    {

        if (newLocale != null)
        {
            return;
        }

        Locale fallbackLocale = I18nContentSupportFactory.getI18nSupport().getFallbackLocale();

        String fallbackLocaleCode = fallbackLocale.toString();

        String jsFrameworkAttributeName;
        String jsFrameworkFilename;
        if ("jquery".equals(jsFramework))
        {
            jsFrameworkAttributeName = ATTRIBUTE_JQUERY_LOADED;
            jsFrameworkFilename = "jquery.min.js";
        }
        else
        {
            jsFrameworkAttributeName = ATTRIBUTE_MOOTOOLS_LOADED;
            jsFrameworkFilename = "mootools-1.2.4-core-yc.js";
        }

        // load the script once: if there are multiple instances
        if (getRequest().getAttribute(jsFrameworkAttributeName) == null)
        {
            out.write("<script type=\"text/javascript\" src=\""
                + this.getRequest().getContextPath()
                + "/.resources/controls/js/"
                + jsFrameworkFilename
                + "\"></script>");
            getRequest().setAttribute(jsFrameworkAttributeName, "true");
        }

        if (getRequest().getAttribute(ATTRIBUTE_I18NTAB_LOADED) == null)
        {
            out.write("<script type=\"text/javascript\" src=\""
                + getRequest().getContextPath()
                + "/.resources/controls/js/i18ntab-"
                + jsFramework
                + ".js\"></script>");

            // array with all locale names
            out.write("<script type=\"text/javascript\">");
            out.write("var i18nTabLocales = [");
            boolean firstElement = true;
            for (String locale : mapLocales.keySet())
            {
                if (!firstElement)
                {
                    out.write(",");
                }
                out.write("'" + locale + "'");
                firstElement = false;
            }
            out.write("];");
            out.write("</script>");
            getRequest().setAttribute(ATTRIBUTE_I18NTAB_LOADED, "true");
        }

        String id = getId();

        out.write("<script type=\"text/javascript\">");
        String options = "{ctx: '"
            + getRequest().getContextPath()
            + "', path: '"
            + getParent().getConfigValue("path")
            + "', nodeCollection: '"
            + getParent().getConfigValue("nodeCollection")
            + "', node: '"
            + getParent().getConfigValue("node")
            + "', dialog: '"
            + getParent().getName()
            + "', tab: '"
            + getName()
            + "'}";
        if ("jquery".equals(jsFramework))
        {
            out.write("$(document).ready(function() { new $.I18nTab('" + id + "', " + options + "); });");
        }
        else
        {
            out.write("window.addEvent('domready', function() { new I18nTab('" + id + "', " + options + "); });");
        }
        out.write("</script>");

        super.drawHtmlPreSubs(out);
        out.write("<tr>");
        out.write("<td class=\"mgnlDialogBoxLabel\">Locale</td>");
        out.write("<td class=\"mgnlDialogBoxInput\">");
        out.write("<select id=\"" + id + "_select\" class=\"mgnlDialogControlSelect\" style=\"width: 50%\">");
        for (String locale : mapLocales.keySet())
        {
            addLocaleOption(out, locale, fallbackLocaleCode);
        }
        out.write("</select>");
        out.write("</td>");
        out.write("</tr>");
        out.write("</table>");
    }

    /**
     * @param out
     * @param locale
     * @param fallbackLocaleCode
     * @throws IOException
     */
    private void addLocaleOption(Writer out, String locale, String fallbackLocaleCode) throws IOException
    {
        out.write("<option value=\"");
        out.write(locale);

        out.write("\"");

        if (localesToRender.contains(locale) || (fallbackLocaleCode.equals(locale) && localesToRender.contains("")))
        {
            out.write(" style=\"background-color: #9DB517 !important; font-weight: bold;\"");
        }

        if (locale.equals(visibleLocale))
        {
            out.write(" selected=\"selected\"");
        }

        out.write(">");
        out.write(mapLocales.get(locale));
        if (StringUtils.isNotBlank(locale))
        {
            out.write(" (" + locale + ")");
        }

        if (StringUtils.equals(locale, fallbackLocaleCode))
        {
            out.write(" default");
        }

        out.write("</option>");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void drawSubs(Writer out) throws IOException
    {
        String id = getId();
        List<String> originalSubNames = getSubNames();
        for (String locale : mapLocales.keySet())
        {
            if (localesToRender.contains(locale))
            {
                changeSubNames(originalSubNames, locale);
                for (Object o : getSubs())
                {
                    ((DialogControlImpl) o).setValue(null);
                }
                out.write("<table id=\""
                    + id
                    + "_table_"
                    + locale
                    + "\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"table-layout:fixed;"
                    + (locale.equals(visibleLocale) ? "" : " display: none;")
                    + "\">");
                out.write("<col width=\"200\" /><col />");
                super.drawSubs(out);
                out.write("</table>");
            }
        }
        changeSubNames(originalSubNames, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void drawHtmlPostSubs(Writer out) throws IOException
    {
        if (newLocale != null)
        {
            return;
        }
        out.write("<div id=\"" + getId() + "_append\"></div>");
        out.write("</td></tr></table></div>");
    }

    private List<String> getSubNames()
    {
        List<String> names = new ArrayList<String>(getSubs().size());
        for (Object o : getSubs())
        {
            names.add(((DialogControlImpl) o).getName());
        }
        return names;
    }

    private void changeSubNames(List<String> names, String suffix)
    {
        int i = 0;
        for (Object o : getSubs())
        {
            DialogControlImpl subControl = (DialogControlImpl) o;
            subControl.setName(names.get(i)
                + (StringUtils.isBlank(suffix) ? StringUtils.EMPTY : localeSuffixSeparator + suffix));
            i++;
        }
    }

    /**
     * @param configNode
     * @throws PathNotFoundException
     * @throws RepositoryException
     * @throws AccessDeniedException
     */
    private void setConfiguredLanguages(Content configNode) throws PathNotFoundException, RepositoryException,
        AccessDeniedException
    {

        Locale fallbackLocale = I18nContentSupportFactory.getI18nSupport().getFallbackLocale();
        Collection<Locale> locales = I18nContentSupportFactory.getI18nSupport().getLocales();
        for (Locale locale : locales)
        {
            String code = locale.toString();

            if (locale.equals(fallbackLocale))
            {
                mapLocales.put("", locale.getDisplayName());
            }
            else
            {
                mapLocales.put(code, locale.getDisplayName());
            }
        }

    }

}
