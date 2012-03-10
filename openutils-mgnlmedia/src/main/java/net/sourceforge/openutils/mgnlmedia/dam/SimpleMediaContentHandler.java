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
import info.magnolia.cms.core.HierarchyManager;
import info.magnolia.cms.core.NodeData;
import info.magnolia.context.MgnlContext;
import info.magnolia.module.templatingkit.dam.Asset;
import info.magnolia.module.templatingkit.dam.AssetNotFoundException;
import info.magnolia.module.templatingkit.dam.DAMException;
import info.magnolia.module.templatingkit.dam.handlers.DMSDAMHandler;

import javax.jcr.ItemNotFoundException;
import javax.jcr.RepositoryException;

import net.sourceforge.openutils.mgnlmedia.media.lifecycle.MediaModule;
import net.sourceforge.openutils.mgnlmedia.media.types.impl.BaseTypeHandler;


/**
 * Dam-support for the SimpleMedia module.
 * @author Ernst Bunders
 */
public class SimpleMediaContentHandler extends DMSDAMHandler
{

    @Override
    public Asset getAssetByKey(String key) throws DAMException
    {
        final HierarchyManager hm = MgnlContext.getHierarchyManager(MediaModule.REPO);
        try
        {
            try
            {
                Content node = hm.getContentByUUID(key);
                final NodeData binaryNodeData = node.getNodeData(BaseTypeHandler.ORGINAL_NODEDATA_NAME);
                return new SimpleMediaAsset(this, node, binaryNodeData);
            }
            catch (ItemNotFoundException e)
            {
                throw new AssetNotFoundException("No asset found for key [" + key + "]");
            }
        }
        catch (RepositoryException e)
        {
            throw new DAMException("Can't create asset for key " + key, e);
        }
    }
}
