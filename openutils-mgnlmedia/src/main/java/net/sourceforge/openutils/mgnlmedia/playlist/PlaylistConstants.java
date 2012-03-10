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

package net.sourceforge.openutils.mgnlmedia.playlist;

import info.magnolia.cms.core.ItemType;


/**
 * @author dschivo
 */
public final class PlaylistConstants
{

    /**
     * Playlists repository name
     */
    public static final String REPO = "playlists";

    private static final String MGNL_PLAYLIST_TYPE = "mgnl:playlist";

    private static final String MGNL_PLAYLIST_ENTRY_TYPE = "mgnl:playlistentry";

    /**
     * Folder type
     */
    public static final ItemType FOLDER = ItemType.CONTENT;

    /**
     * Playlist type
     */
    public static final ItemType PLAYLIST = new ItemType(MGNL_PLAYLIST_TYPE);

    /**
     * Playlist entry type
     */
    public static final ItemType PLAYLIST_ENTRY = new ItemType(MGNL_PLAYLIST_ENTRY_TYPE);

    /**
     * 
     */
    private PlaylistConstants()
    {
    }
}
