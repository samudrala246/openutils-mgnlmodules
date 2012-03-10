/*
 * Ext JS Library 2.1
 * Copyright(c) 2006-2008, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */

MessagesViewer = {};

Ext.onReady( function() {
	Ext.QuickTips.init();

	var loadingMask = new Ext.LoadMask(Ext.getBody(), {
		msg :'loading...'
	});
	Ext.Ajax.on('beforerequest', function(conn, options) {
		loadingMask.enable();
		loadingMask.show();
	});
	Ext.Ajax.on('requestcomplete', function(conn, response, options) {
		loadingMask.hide();
		loadingMask.disable();
	});
	Ext.Ajax.on('requestexception', function(conn, response, options) {
		loadingMask.hide();
		loadingMask.disable();
	});

	var messages = new MessagesPanel();
	var search = new SearchPanel();
	var mainPanel = new MainPanel();

	messages.on('selectedKey', function(key) {
		mainPanel.changeKey(key);
	});

	search.on('selectedKey', function(key) {
		mainPanel.changeKey(key);
	});

	var viewport = new Ext.Viewport( {
		layout :'border',
		items : [
			{
				region :'west',
				xtype: 'tabpanel',
				split :true,
				width :350,
				minSize :275,
				maxSize :400,
				collapsible :true,
				margins :'0 0 5 5',
				cmargins :'0 5 5 5',
				collapseFirst :false,
				activeTab: 0,
				items: [ messages, search ]
			},
			mainPanel
		]
	});

});