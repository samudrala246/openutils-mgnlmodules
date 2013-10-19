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

import info.magnolia.objectfactory.Components;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.sourceforge.openutils.mgnlmedia.media.configuration.MediaConfigurationManager;
import net.sourceforge.openutils.mgnlmedia.media.configuration.MediaTypeConfiguration;


/**
 * @author cstrappazzon
 * @version $Id$
 */
public class SearchFilterSpecFieldMedia extends SearchFilterMultiplevalue
{

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Option> getOptions()
    {
        if (super.getOptions().size() > 0)
        {
            return super.getOptions();
        }
        List<Option> options = new ArrayList<Option>();
        Map<String, MediaTypeConfiguration> mediatype = Components
            .getComponent(MediaConfigurationManager.class)
            .getTypes();
        Option option;
        for (String media : mediatype.keySet())
        {
            option = new Option();
            option.setValue(media);
            option.setLabel(mediatype.get(media).getLabel());
            options.add(option);
        }
        return options;
    }
}
