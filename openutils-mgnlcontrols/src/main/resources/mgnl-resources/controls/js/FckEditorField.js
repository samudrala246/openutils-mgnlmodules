var FckEditorField = Ext.extend(Ext.form.TriggerField, {

  triggerClass: 'x-form-fckedit-trigger',

  contextPath: '',

  enterMode: '',

  getContextPath: function() {
    return this.contextPath;
  },

  onTriggerClick : function() {
    if (this.disabled) return;
    window.getFckEditorContextPath = this.getContextPath.createDelegate(this);
    window.getFckEditorValue = this.getValue.createDelegate(this);
    window.setFckEditorValue = function(value) {
      this.setValue(value);
    }.createDelegate(this);
    mgnlOpenWindow('/.resources/controls/grid-fckeditor.html?enterMode=' + this.enterMode, 880, 300);
  }

});
