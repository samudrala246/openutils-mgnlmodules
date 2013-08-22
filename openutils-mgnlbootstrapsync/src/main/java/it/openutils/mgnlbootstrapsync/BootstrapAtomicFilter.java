/**
 *
 * BootstrapSync for Magnolia CMS (http://www.openmindlab.com/lab/products/bootstrapsync.html)
 * Copyright(C) ${project.inceptionYear}-2013, Openmind S.r.l. http://www.openmindonline.it
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

package it.openutils.mgnlbootstrapsync;

import info.magnolia.importexport.filters.VersionFilter;

import org.apache.commons.lang.ArrayUtils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLFilterImpl;


/**
 * @author mmolaschi
 * @version $Id: $
 */
public class BootstrapAtomicFilter extends XMLFilterImpl
{

    private int inLevel;

    private int inVersionElement;

    private boolean inMetadata;

    /**
     * @param parent parent reader
     */
    public BootstrapAtomicFilter(XMLReader parent)
    {
        super(parent);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException
    {
        if (inVersionElement > 0)
        {
            inVersionElement--;
            return;
        }

        if (inLevel > 0)
        {
            if ("sv:node".equals(qName))
            {
                if (inMetadata && inLevel == 3)
                {
                    inMetadata = false;
                    inLevel--;
                    super.endElement(uri, localName, qName);
                    return;
                }
                inLevel--;

                if (inLevel > 1)
                {
                    return;
                }
            }
            if (inMetadata && inLevel >= 2)
            {
                super.endElement(uri, localName, qName);
                return;
            }
            if (inLevel > 2)
            {
                return;
            }
        }

        super.endElement(uri, localName, qName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void characters(char[] ch, int start, int length) throws SAXException
    {
        // filter content
        if (inVersionElement == 0 && (inMetadata || inLevel <= 2))
        {
            super.characters(ch, start, length);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException
    {
        if (inVersionElement > 0)
        {
            inVersionElement++;
            return;
        }
        if ("sv:node".equals(qName))
        {
            String attName = atts.getValue("sv:name"); //$NON-NLS-1$
            if (attName != null && ArrayUtils.contains(VersionFilter.FILTERED_NODES, attName))
            {
                inVersionElement++;
                return;
            }
        }
        else if ("sv:property".equals(qName))
        {
            String attName = atts.getValue("sv:name"); //$NON-NLS-1$
            if (attName != null && ArrayUtils.contains(VersionFilter.FILTERED_PROPERTIES, attName))
            {
                inVersionElement++;
                return;
            }
        }

        if (!inMetadata && inLevel > 2)
        {
            if ("sv:node".equals(qName))
            {
                inLevel++;
            }
            return;
        }

        if ("sv:node".equals(qName))
        {
            String attName = atts.getValue("sv:name");
            inLevel++;
            if ("MetaData".equals(attName) && inLevel == 3)
            {
                inMetadata = true;
            }
            else if (inLevel > 2)
            {
                return;
            }
        }
        super.startElement(uri, localName, qName, atts);
    }
}
