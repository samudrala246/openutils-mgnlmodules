var PipeComboBox = Ext.extend(Ext.form.ComboBox, {

  getValue: function() {
    var v = PipeComboBox.superclass.getValue.call(this);
    return this.lastSelectionText + '|' + v;
  },

  findRecord : function(prop, value) {
    return PipeComboBox.superclass.findRecord.call(this, prop, value.replace(/^.*\|(.*)$/, '$1'));
  }

});
