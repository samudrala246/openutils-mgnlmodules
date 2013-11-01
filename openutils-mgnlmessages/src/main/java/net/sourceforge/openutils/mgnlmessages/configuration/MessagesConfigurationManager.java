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

package net.sourceforge.openutils.mgnlmessages.configuration;

import info.magnolia.cms.core.MgnlNodeType;
import info.magnolia.jcr.RuntimeRepositoryException;
import info.magnolia.jcr.util.NodeUtil;
import info.magnolia.jcr.util.PropertyUtil;
import it.openutils.mgnlutils.util.ObservedManagerAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import javax.inject.Singleton;
import javax.jcr.Node;
import javax.jcr.RepositoryException;


/**
 * @author molaschi
 */
@Singleton
public class MessagesConfigurationManager extends ObservedManagerAdapter
{

    /**
     * Folder type
     */
    public static final String FOLDER = MgnlNodeType.NT_CONTENT;

    private List<Locale> locales = new ArrayList<Locale>();

    public static final String MESSAGES_REPO = "messages";

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onClear()
    {
        locales.clear();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    protected void onRegister(Node node)
    {
        try
        {
            if (node.hasProperties())
            {
                locales.clear();

                Iterable<Node> nodes = NodeUtil.getNodes(node, NodeUtil.EXCLUDE_META_DATA_FILTER);
                for (Node langNode : nodes)
                {
                    locales.add(new Locale(PropertyUtil.getString(langNode, "language"), PropertyUtil.getString(
                        langNode,
                        "country")));
                }

                Collections.sort(locales, new Comparator<Locale>()
                {

                    /**
                     * {@inheritDoc}
                     */
                    public int compare(Locale o1, Locale o2)
                    {
                        if (o1.getLanguage().equals(o2.getLanguage()))
                        {
                            return o1.getCountry().compareTo(o2.getCountry());
                        }

                        return o1.getLanguage().compareTo(o2.getLanguage());
                    }

                });
            }
        }
        catch (RepositoryException e)
        {
            throw new RuntimeRepositoryException(e);
        }
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
     * Sets the languages.
     * @param locales the languages to set
     */
    public void setLocales(List<Locale> locales)
    {
        this.locales = locales;
    }

}
