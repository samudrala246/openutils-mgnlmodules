/*
 * Ext JS Library 2.1
 * Copyright(c) 2006-2008, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */

MessagesPanel = function() {
	var ds = new Ext.data.SimpleStore( {
		fields : [ 'key', 'id' ],
		data :keys,
		id :0
	});

	var pn = this;

	var sm = new Ext.grid.RowSelectionModel( {
		singleSelect :true,
		listeners : {
			rowselect : function(selmodel, rowIndex, r) {
				pn.fireEvent('selectedKey', r.get('key'));
			}
		}
	});

	var cm = new Ext.grid.ColumnModel( [ {
		id :'key',
		header :"Message key",
		dataIndex :'key',
		editor :new Ext.form.TextField( {
			allowBlank :false
		})
	} ]);

	cm.defaultSortable = true;

	var removeButton = new Ext.Button( {
		text :"Remove selected"
	});

	var addButton = new Ext.Button( {
		text :"Add"
	});

	removeKey = function() {
		if (sm.getSelected() != null && confirm("Are you sure?")) {
			var r = sm.getSelected();
			var key = r.get('key');

			Ext.Ajax
					.request( {
						url :pageUrl,
						success : function(response, options) {
							eval(response.responseText);
							if (rootObj.value == "OK") {

								sm.selectNext();

								var r = ds.getById(key);
								/*
								 * remove key
								 */
								ds.remove(r);

								pn.getView().refresh();

							} else if (rootObj.value == "NOTFOUND") {
								alert("Can't remove key, beacuse it is not stored in messages module.");
							} else {
								alert("Can't remove key");
							}
						},
						failure : function() {
							alert('Error');
						},
						params : {
							command :'removekey',
							key :key
						}
					});
		}
	};

	addKey = function() {
		var key = prompt("Key name");
		if (key != null && key.length > 0) {
			var value = prompt(key + " value (EN)");

			if (value != null && value.length > 0) {
				/*
				 * make request
				 */
				Ext.Ajax.request( {
					url :pageUrl,
					success : function(response, options) {
						eval(response.responseText);
						if (rootObj.value == "OK") {

							var rcs = ds.reader.readRecords( [ [ key, '0' ] ]).records;

							/*
							 * add key to list
							 */
							ds.insert(0, rcs);

							/*
							 * fire event to focus on key
							 */
							sm.selectRow(ds.indexOfId(key));

							pn.getView().focusRow(ds.indexOfId(key));
						} else {
							alert("Can't add key");
						}
					},
					failure : function() {
						alert('Error');
					},
					params : {
						command :'savekey',
						key :key,
						locale :'en',
						text :value
					}
				});
			}
		}
	};

	MessagesPanel.superclass.constructor.call(this, {
		id :'feed-tree',
		region :'center',
		title :'Keys',
		margins :'0 0 0 0',
		cmargins :'0 0 0 0',
		store :ds,
		sm :sm,
		cm :cm,
		autoExpandColumn :'key',
		clicksToEdit :2,
		tbar : [ 'Filter: ', ' ', new Ext.app.FilterField( {
			store :ds
		}), {
			xtype :'tbfill'
		}, {
			text :'Add',
			handler :addKey
		}, {
			text :'Remove selected',
			handler :removeKey
		} ]
	});

	pn.on('validateedit', function(e) {

		e.cancel = true;

		var key = e.record.get(e.field);
		var newkey = e.value;

		Ext.Ajax.request( {
			url :pageUrl,
			success : function(response, options) {
				eval(response.responseText);
				if (rootObj.value == "OK") {

					e.record.data[e.field] = newkey;
					pn.stopEditing();

					var r = sm.getSelected();
					sm.clearSelections();
					sm.selectRecords( [ r ]);

					pn.getView().refresh();
				} else {
					alert("Can't rename key");
				}
			},
			failure : function() {
				alert('Error');
			},
			params : {
				command :'renamekey',
				key :key,
				newkey :newkey
			}
		});

	})

}

Ext.extend(MessagesPanel, Ext.grid.EditorGridPanel);

Ext.app.FilterField = Ext.extend(Ext.form.TriggerField, {
	initComponent : function() {
		Ext.app.FilterField.superclass.initComponent.call(this);
		this.on('keyup', function(f, e) {
			this.onTriggerClick();
		}, this);
	},

	enableKeyEvents :true,

	triggerClass :'x-form-search-trigger',

	onTriggerClick : function() {
		var v = this.getRawValue();
		this.store.filter('key', new RegExp(v), false, false);
	}
});