var FileField = Ext.extend(Ext.form.TriggerField, {

  triggerClass: 'x-form-fileupload-trigger',

  onTriggerClick : function() {
    if (this.disabled) return;
    window.setFileValue = function(value) {
      this.setValue(value);
    }.createDelegate(this);
    mgnlOpenWindow('/.resources/controls/grid-fileupload.html', 400, 150);
  }

});
