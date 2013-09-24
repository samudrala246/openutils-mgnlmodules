/**
 *
 * SimpleMedia Module for Magnolia CMS (http://www.openmindlab.com/lab/products/media.html)
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

package net.sourceforge.openutils.mgnlmedia.media.advancedsearch;

import info.magnolia.cms.core.MgnlNodeType;
import info.magnolia.context.MgnlContext;
import info.magnolia.jcr.util.NodeUtil;
import info.magnolia.repository.RepositoryConstants;

import java.util.ArrayList;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;


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
                Session session = MgnlContext.getJCRSession(RepositoryConstants.CONFIG);
                Node node = session.getNode(reference);
                Iterable<Node> referenceOptions = NodeUtil.getNodes(node, MgnlNodeType.NT_CONTENTNODE); 

                Option opt = new Option();

                if (addempty)
                {
                    opt.setValue("");
                    opt.setLabel("---");
                    addOptions(opt);
                }

                for (Node option : referenceOptions)
                {
                    opt = new Option();
                    opt.setLabel(option.getProperty("label").getString());
                    opt.setValue(option.getProperty("value").getString());
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

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SIMPLE_STYLE)
            .append("reference", this.reference)
            .append("addempty", this.addempty)
            .append("options", this.options)
            .append("control", this.getControl())
            .append("subfilters", this.getSubfilters())
            .append("label", this.getLabel())
            .toString();
    }

}
