/**
 *
 * Generic utilities for Magnolia CMS (http://www.openmindlab.com/lab/products/mgnlutils.html)
 * Copyright(C) 2009-2012, Openmind S.r.l. http://www.openmindonline.it
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

package it.openutils.mgnlutils.api;

import info.magnolia.cms.beans.config.ObservedManager;

import javax.jcr.Node;


/**
 * An adapter for using javax.jcr.Node instead of Content, till Magnolia will cleanup its APIs.
 * @author fgiust
 * @version $Id$
 */
public abstract class ObservedManagerAdapter extends ObservedManager
{

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("deprecation")
    @Override
    protected final void onRegister(info.magnolia.cms.core.Content node)
    {
        onRegister(node.getJCRNode());
    }

    @SuppressWarnings("deprecation")
    @Override
    protected final void reload(info.magnolia.cms.core.Content node)
    {
        reload(node.getJCRNode());
    }

    protected void reload(Node node)
    {
        onRegister(node);
    }

    protected abstract void onRegister(Node node);

}
