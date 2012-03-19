/**
 *
 * Stripes module for Magnolia CMS (http://www.openmindlab.com/lab/products/mgnlstripes.html)
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

package it.openutils.magnoliastripes.samples;

import net.sourceforge.stripes.action.ActionBean;
import net.sourceforge.stripes.action.ActionBeanContext;
import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;


/**
 * @author diego
 * @version $Id: $
 */
public class SampleStripesActionBean implements ActionBean
{

    private ActionBeanContext context;

    private String text;

    /**
     * {@inheritDoc}
     */
    public void setContext(ActionBeanContext context)
    {
        this.context = context;
    }

    /**
     * {@inheritDoc}
     */
    public ActionBeanContext getContext()
    {
        return context;
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

    @DefaultHandler
    public Resolution show()
    {
        return new ForwardResolution("/templates/samples-stripes/paragraph.jsp");
    }
}
