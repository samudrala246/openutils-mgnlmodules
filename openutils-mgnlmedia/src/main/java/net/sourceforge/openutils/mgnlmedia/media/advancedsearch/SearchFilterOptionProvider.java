/**
 *
 * SimpleMedia Module for Magnolia CMS (http://www.openmindlab.com/lab/products/media.html)
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

package net.sourceforge.openutils.mgnlmedia.media.advancedsearch;

import info.magnolia.cms.core.Content;
import info.magnolia.context.MgnlContext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.jcr.RepositoryException;

import org.apache.commons.lang.StringUtils;


/**
 * @author cstrappazzon
 * @version $Id$
 */
public class SearchFilterOptionProvider extends SearchFilterAbstract
{

    private List<Option> options = new ArrayList<Option>();

    private String reference;

    private boolean addempty;

    /**
     * Returns the options.
     * @return the options
     */
    public List<Option> getOptions()
    {
        return options;
    }

    /**
     * Sets the options.
     * @param option the option to set
     */
    public void addOptions(Option option)
    {
        options.add(option);
    }

    /**
     * Returns the reference.
     * @return the reference
     */
    public String getReference()
    {
        return reference;
    }

    /**
     * Sets the reference.
     * @param reference the reference to set
     */
    public void setReference(String reference)
    {
        this.reference = reference;

        getOptionsFormReference(reference);

    }

    /**
     * Returns the addempty.
     * @return the addempty
     */
    public boolean isAddempty()
    {
        return addempty;
    }

    /**
     * Sets the addempty.
     * @param addempty the addempty to set
     */
    public void setAddempty(boolean addempty)
    {
        this.addempty = addempty;
    }

    /**
     * @param reference
     */
    private void getOptionsFormReference(String reference)
    {
        if (StringUtils.isNotBlank(reference))
        {
            try
            {
                Collection<Content> referenceOptions = MgnlContext
                    .getHierarchyManager("config")
                    .getContent(reference)
                    .getChildren();

                Option opt = new Option();

                if (addempty)
                {
                    opt.setValue("");
                    opt.setLabel("---");
                    addOptions(opt);
                }

                for (Content content : referenceOptions)
                {
                    opt = new Option();
                    opt.setLabel(content.getNodeData("label").getString());
                    opt.setValue(content.getNodeData("value").getString());
                    addOptions(opt);
                }
            }
            catch (RepositoryException e)
            {
                // do nothing
                log.error("Errore reference node on SearchFilterOptionProvider, {}", e);
            }
        }
    }

}
