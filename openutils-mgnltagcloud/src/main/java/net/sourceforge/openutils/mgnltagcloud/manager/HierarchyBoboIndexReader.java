/**
 *
 * Tagcloud module for Magnolia CMS (http://www.openmindlab.com/lab/products/mgnltagcloud.html)
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

package net.sourceforge.openutils.mgnltagcloud.manager;

import java.io.IOException;
import java.util.Collection;

import org.apache.jackrabbit.core.query.lucene.HierarchyResolver;
import org.apache.lucene.index.IndexReader;

import com.browseengine.bobo.api.BoboIndexReader;
import com.browseengine.bobo.facets.FacetHandler;


/**
 * Wrapper for Bobo index reader to make it compatible with jackrabbit searches
 * @author molaschi
 * @version $Id$
 */
public class HierarchyBoboIndexReader extends BoboIndexReader implements HierarchyResolver
{

    private HierarchyResolver resolver;

    /**
     * Build wrapped Bobo index reader instance
     * @param indexReader jackrabbit index reader
     * @param facetHandlers facet handlers
     * @param workArea work area
     * @throws IOException i/o exception reading indexes
     */
    protected HierarchyBoboIndexReader(
        IndexReader indexReader,
        Collection<FacetHandler<?>> facetHandlers,
        WorkArea workArea) throws IOException
    {
        super(indexReader, facetHandlers, null, workArea);
        resolver = (HierarchyResolver) indexReader;
    }

    /**
     * Build wrapped Bobo index reader instance
     * @param reader jackrabbit index reader
     * @param facetHandlers facet handlers
     * @return wrapped Bobo index reader instance
     * @throws IOException i/o exception reading indexes
     */
    public static HierarchyBoboIndexReader getInstance(IndexReader reader, Collection<FacetHandler<?>> facetHandlers)
        throws IOException
    {

        HierarchyBoboIndexReader boboReader = new HierarchyBoboIndexReader(reader, facetHandlers, new WorkArea());
        boboReader.facetInit();
        return boboReader;
    }

    /**
     * {@inheritDoc}
     */
    public int[] getParents(int n, int[] docNumbers) throws IOException
    {
        return resolver.getParents(n, docNumbers);
    }

}
