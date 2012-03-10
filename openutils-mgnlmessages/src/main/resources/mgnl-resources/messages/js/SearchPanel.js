SearchPanel = function() {
	var ds = new Ext.data.JsonStore( {
		fields : [ 'id', 'key' ],
		url: pageUrl
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

	SearchPanel.superclass.constructor.call(this, {
		id :'search',
		region :'center',
		title :'Search',
		margins :'0 0 0 0',
		cmargins :'0 0 0 0',
		store :ds,
		sm :sm,
		cm :cm,
		autoExpandColumn :'key',
		clicksToEdit :2,
		tbar : [ 'Text: ', ' ', new SearchPanel.TextField( {
			store :ds
		}) ]
	});

}

Ext.extend(SearchPanel, Ext.grid.EditorGridPanel);

SearchPanel.TextField = Ext.extend(Ext.form.TriggerField, {
	initComponent : function() {
		SearchPanel.TextField.superclass.initComponent.call(this);
	},

	enableKeyEvents :true,

	triggerClass :'x-form-search-trigger',

	onTriggerClick : function() {
		var v = this.getRawValue();
		this.store.load({ params: { command: 'search', text: v } });
	}
});