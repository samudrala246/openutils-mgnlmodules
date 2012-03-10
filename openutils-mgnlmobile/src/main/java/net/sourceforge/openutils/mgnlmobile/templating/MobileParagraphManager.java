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

package net.sourceforge.openutils.mgnlmobile.templating;

import net.sourceforge.openutils.mgnlmobile.filters.MobileFilter;
import info.magnolia.cms.core.Content;
import info.magnolia.cms.util.ContentUtil;
import info.magnolia.content2bean.Content2BeanException;
import info.magnolia.content2bean.Content2BeanUtil;
import info.magnolia.module.templating.Paragraph;
import info.magnolia.module.templating.ParagraphManager;


/**
 * @author molaschi
 * @version $Id: $
 */
public class MobileParagraphManager extends ParagraphManager
{

    /**
     * {@inheritDoc}
     */
    @Override
    protected void addParagraphToCache(Content c)
    {
        try
        {
            final Paragraph p = (Paragraph) Content2BeanUtil.toBean(c, true, Paragraph.class);
            Content mobileContent = ContentUtil.getContent(c, "mobile");
            if (mobileContent != null)
            {
                BaseMobileParagraphDecorator mpd = (BaseMobileParagraphDecorator) Content2BeanUtil.toBean(
                    mobileContent,
                    true,
                    DefaultMobileParagraphDecorator.class);
                mpd.setInnerParagraph(p);
                addParagraphToCache(mpd);
            }
            else
            {
                addParagraphToCache(p);
            }
        }
        catch (Content2BeanException e)
        {
            throw new RuntimeException(e); // TODO
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Paragraph getParagraphDefinition(String key)
    {
        Paragraph p = super.getParagraphDefinition(key);
        if (p instanceof BaseMobileParagraphDecorator && !MobileFilter.isMobileRequest())
        {
            return ((BaseMobileParagraphDecorator) p).getInnerParagraph();
        }
        return p;
    }

}
