var UUIDLinkField = Ext.extend(Ext.form.TriggerField, {

  triggerClass : 'x-form-link-trigger',

  repository: 'website',

  // extension: undefined,

  onTriggerClick : function() {
    if (this.disabled) return;
    mgnlDialogLinkOpenBrowser(this.el.id, this.repository, this.extension);
  }

});
