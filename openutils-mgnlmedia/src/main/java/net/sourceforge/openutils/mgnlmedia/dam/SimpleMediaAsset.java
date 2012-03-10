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

package net.sourceforge.openutils.mgnlmedia.dam;

import info.magnolia.cms.core.Content;
import info.magnolia.cms.core.NodeData;
import info.magnolia.module.templatingkit.dam.AssetNotFoundException;
import info.magnolia.module.templatingkit.dam.assets.InternalAsset;

import javax.jcr.RepositoryException;


/**
 * Dam-support for the SimpleMedia module.
 * @author Ernst Bunders
 */
public class SimpleMediaAsset extends InternalAsset
{

    public SimpleMediaAsset(SimpleMediaContentHandler handler, Content metaDataNode, NodeData binaryNodeData)
        throws AssetNotFoundException,
        RepositoryException
    {
        super(handler, metaDataNode, "", binaryNodeData);
    }

    @Override
    public String getName()
    {
        return metaDataNode.getName();
    }

    @Override
    public String getCaption()
    {
        return getMetaDataValue("title", super.getTitle());
    }

}
