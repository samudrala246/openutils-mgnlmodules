MainPanel = function() {

	this.languageItem1 = new Language('lang1', true);
	this.languageItem2 = new Language('lang2', false);

	MainPanel.superclass.constructor.call(this, {
		id :'main-tabs',
		region :'center',
		margins :'5 5 5 0',
		cmargins :'5 5 5 0',
		layout :'border',
		items : [ this.languageItem1, this.languageItem2 ]
	});
}

Ext.extend(MainPanel, Ext.Panel, {
	changeKey : function(key) {
		this.key = key;
		this.languageItem1.loadKey(key);
		this.languageItem2.loadKey(key);
	}
});

Language = function(id, top) {

	var lds = new Ext.data.SimpleStore( {
		fields : [ 'label', 'value' ],
		data :languages
	});

	this.textArea = new Ext.form.TextArea( {
		layout :'fit'
	});

	var languageItem = this;

	if (top) {
		this.comboLanguage = new Ext.form.ComboBox( {
			typeAhead :true,
			triggerAction :'all',
			editable :false,
			mode :'local',
			store :lds,
			value :'en',
			displayField :'label',
			valueField :'value',
			forceSelection :true
		});
		Language.superclass.constructor.call(this, {
			id :id,
			tbar : [ 'Language', ' ', this.comboLanguage ],
			items :this.textArea,
			bbar : [ {
				text :'Save',
				handler : function() {
					languageItem.saveValue();
				}
			} ],
			region :'north',
			split :true,
			height :200,
			minSize :200,
			maxSize :375
		});
	} else {
		this.comboLanguage = new Ext.form.ComboBox( {
			typeAhead :true,
			triggerAction :'all',
			displayField :'label',
			valueField :'value',
			mode :'local',
			store :lds,
			value :currentlanguage,
			forceSelection :true
		});
		Language.superclass.constructor.call(this, {
			id :id,
			tbar : [ 'Language', ' ', this.comboLanguage ],
			items :this.textArea,
			bbar : [ {
				text :'Save',
				handler : function() {
					languageItem.saveValue();
				}
			} ],
			id :'bottom-preview',
			region :'center'
		});
	}

	this.comboLanguage.on('select', function(combo, record, index) {
		if (languageItem.key != null) {
			languageItem.loadKey(languageItem.key);
		}
	});

};

Ext.extend(Language, Ext.Panel, {
	layout :'fit',
	loadKey : function(key) {
		this.key = key;
		var lang = this;
		Ext.Ajax.request( {
			url :pageUrl,
			success : function(response, options) {
				eval(response.responseText);
				lang.textArea.setRawValue(rootObj.value);
			},
			failure : function() {
				alert('error');
			},
			params : {
				command :'loadkey',
				key :key,
				locale :this.comboLanguage.getValue()
			}
		});
	},
	saveValue : function() {
		var key = this.key;
		var value = this.textArea.getRawValue();
		var lang = this.comboLanguage.getValue();
		Ext.Ajax.request( {
			url :pageUrl,
			success : function(response, options) {
				eval(response.responseText);
				if (rootObj.value == "OK") {
					alert("Saved");
				} else {
					alert("Can't save");
				}
				// lang.textArea.setRawValue(rootObj.value);
		},
		failure : function() {
			alert('Error');
		},
		params : {
			command :'savekey',
			key :key,
			text :value,
			locale :lang
		}
		});
	}
});