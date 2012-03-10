var EditCodeField = Ext.extend(Ext.form.TriggerField, {

  triggerClass: 'x-form-editcode-trigger',
  
  onTriggerClick: function() {
    if (this.disabled) return;
    window.getEditCodeValue = this.getValue.createDelegate(this);
    window.setEditCodeValue = function(value) {
      this.setValue(value);
    }.createDelegate(this);
    mgnlOpenWindow('/.resources/controls/grid-editcode.html', 640, 480);
  }

});
