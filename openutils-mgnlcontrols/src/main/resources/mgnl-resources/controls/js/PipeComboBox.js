var PipeComboBox = Ext.extend(Ext.form.ComboBox, {

  getValue: function() {
    var v = PipeComboBox.superclass.getValue.call(this);
    return this.lastSelectionText + '|' + v;
  },
  
  // Combobox must be patched because of a bug. For reference, see:
  // http://www.sencha.com/forum/showthread.php?17465-1.1.1-Local-ComboBox-data-store-filter-not-cleared-on-call-to-setValue%28%29
  setValue: function(v) {
    this.store.clearFilter();
    PipeComboBox.superclass.setValue.call(this, v);
  },
  
  findRecord : function(prop, value) {
    return PipeComboBox.superclass.findRecord.call(this, prop, value.replace(/^.*\|(.*)$/, '$1'));
  }

});
