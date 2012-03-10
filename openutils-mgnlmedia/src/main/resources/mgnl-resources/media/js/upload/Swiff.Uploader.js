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

Swiff.Uploader = new Class({

	Extends: Swiff,

	Implements: Events,

	options: {
		path: 'Swiff.Uploader.swf',
		multiple: true,
		queued: true,
		typeFilter: null,
		url: null,
		method: 'post',
		data: null,
		fieldName: 'Filedata',
		callBacks: null
	},

	initialize: function(options){
		if (Browser.Plugins.Flash.version < 9) return false;
		this.setOptions(options);

		var callBacks = this.options.callBacks || this;
		if (callBacks.onLoad) this.addEvent('onLoad', callBacks.onLoad);

		var prepare = {}, self = this;
		['onSelect', 'onAllSelect', 'onCancel', 'onBeforeOpen', 'onOpen', 'onProgress', 'onComplete', 'onError', 'onAllComplete'].each(function(index) {
			var fn = (callBacks[index]) ? callBacks[index] : $empty;
			prepare[index] = function() {
				self.fireEvent(index, arguments, 10);
				return fn.apply(self, arguments);
			};
		});

		prepare.onLoad = this.load.create({delay: 10, bind: this});
		this.options.callBacks = prepare;

		var path = this.options.path;
		if (!path.contains('?')) path += '?noCache=' + $time(); // quick fix

		delete this.options.params.wMode;
		this.parent(path);

		if (!this.options.container) document.body.appendChild(this.object);
		return this;
	},

	load: function(){
		this.remote('register', this.instance, this.options.multiple, this.options.queued);
		this.fireEvent('onLoad');
	},

	/*
	Method: browse
		Open the file browser.
	*/

	browse: function(typeFilter){
		return this.remote('browse', $pick(typeFilter, this.options.typeFilter));
	},

	/*
	Method: upload
		Starts the upload of all selected files.
	*/

	upload: function(options){
		var current = this.options;
		options = $extend({data: current.data, url: current.url, method: current.method, fieldName: current.fieldName}, options);
		if ($type(options.data) == 'element') options.data = $(options.data).toQueryString();
		return this.remote('upload', options);
	},

	/*
	Method: removeFile
		For multiple uploads cancels and removes the given file from queue.

	Arguments:
		name - (string) Filename
		name - (string) Filesize in byte
	*/

	removeFile: function(file){
		if (file) file = {name: file.name, size: file.size};
		return this.remote('removeFile', file);
	},

	/*
	Method: getFileList
		Returns one Array with with arrays containing name and size of the file.

	Returns:
		(array) An array with files
	*/

	getFileList: function(){
		return this.remote('getFileList');
	}

});
