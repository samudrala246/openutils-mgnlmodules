/*
 *
 * Magnolia SimpleMedia Module (http://www.openmindlab.com/lab/products/media.html)
 * Copyright (C)2008 - 2010, Openmind S.r.l. http://www.openmindonline.it
 *
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * You may obtain a copy of the License at
 *
 * http://www.gnu.org/licenses/lgpl-2.1.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

classDef("mgnl.media", {
	searchView :false,

	// this variable is used by the context menu
	selectedPath :"",
	selectedIsFolder :false,

	// this is the object used in the context menu to check the flag
	selectedIsNotFolderCondition : {
		test : function() {
			return !mgnl.media.selectedIsFolder
		}
	},

	show : function(path, dialog) {
		mgnlOpenWindow(".magnolia/dialogs/" + dialog + ".html?mgnlPath=" + path
				+ "&mgnlRepository=media");
	},

	createNew : function(path, dialog) {
		parent.openWindow(".magnolia/dialogs/" + dialog + ".html?mgnlPath=" + path
				+ "&mgnlNode=mgnlNew&mgnlRepository=media");// mgnlOpenWindow
	},

	// check if it is a folder
	showDialogInTree : function(tree, dialog) {
		if (tree.selectedNode.itemType == "mgnl:media") {
			mgnlTreeMenuOpenDialog(tree, '.magnolia/dialogs/' + dialog + '.html');
		}
	},

	reloadAfterEdit : function(path) {
		if (!mgnl.media.searchView) {
			document.location = contextPath + "/.magnolia/trees/media.html?mgnlCK="
					+ mgnlGetCacheKiller() + "&pathSelected=" + path +"&pathOpen=" + path;
		} else {
			document.location.reload();
		}
	},
	
	showInTree : function(path) {
		MgnlAdminCentral.showTree('media', path);
	},

	showTree : function() {
		top.mgnlAdminCentralSwitchExtractTree('media');
	}
});

mgnlTree.prototype.openFolder = function(contextPath, handle, writable) {
	if (parent.reloadFolder) parent.reloadFolder(handle, null, true);
}

