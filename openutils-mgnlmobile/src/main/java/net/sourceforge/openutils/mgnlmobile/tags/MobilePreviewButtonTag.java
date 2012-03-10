/**
 *
 * Mobile Module for Magnolia CMS (http://www.openmindlab.com/lab/products/mgnlmobile.html)
 * Copyright(C) 2010-2012, Openmind S.r.l. http://www.openmindonline.it
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

package net.sourceforge.openutils.mgnlmobile.tags;

import info.magnolia.cms.taglibs.BarTag;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import net.sourceforge.openutils.mgnlmobile.filters.MobileFilter;


/**
 * @author molaschi
 * @version $Id: $
 */
public class MobilePreviewButtonTag extends TagSupport
{

    /**
     * 
     */
    private static final long serialVersionUID = 4539087366872958772L;

    /**
     * position (<code>left|right</code>).
     */
    private String position;

    /**
     * Where to add this button. Can be "left" or "right". Default is "left".
     * @jsp.attribute required="true" rtexprvalue="true"
     */
    public void setPosition(String position)
    {
        this.position = position;
    }

    public int doEndTag() throws JspException
    {

        if (MobileFilter.isActive())
        {
            BarTag bartag = (BarTag) findAncestorWithClass(this, BarTag.class);
            if (bartag == null)
            {
                throw new JspException("button tag should be enclosed in a mainbar or newbar tag");
            }

            MobilePreviewButton button = new MobilePreviewButton();

            if ("right".equalsIgnoreCase(position))
            {
                bartag.addButtonRight(button);
            }
            else
            {
                bartag.addButtonLeft(button);
            }
        }

        return EVAL_PAGE;
    }

    public void release()
    {
        super.release();
        this.position = null;
    }
}