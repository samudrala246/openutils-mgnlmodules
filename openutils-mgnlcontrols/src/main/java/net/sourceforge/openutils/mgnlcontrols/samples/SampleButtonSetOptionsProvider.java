/**
 *
 * Controls module for Magnolia CMS (http://www.openmindlab.com/lab/products/controls.html)
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

package net.sourceforge.openutils.mgnlcontrols.samples;

import info.magnolia.cms.core.Content;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.sourceforge.openutils.mgnlcontrols.dialog.DialogButtonSet;
import net.sourceforge.openutils.mgnlcontrols.dialog.DialogButtonSet.Option;

import com.google.common.base.Function;
import com.google.common.collect.Iterators;


/**
 * @author diego
 * @version $Id: $
 */
public class SampleButtonSetOptionsProvider implements DialogButtonSet.OptionsProvider
{

    /**
     * {@inheritDoc}
     */
    public Iterator<Option> getOptions(DialogButtonSet control, Content configNode) throws Exception
    {
        return Iterators.transform(
            Iterators.forArray(new String[]{"foo", "bar", "baz", "qux" }),
            new Function<String, Option>()
            {

                public Option apply(String input)
                {
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("value", input);
                    map.put("label", input);
                    return new DialogButtonSet.Option.MapAdapter(map, "value", "label");
                }
            });
    }

}
