var ExpressionField = Ext.extend(Ext.form.TriggerField, {

  triggerClass: 'x-form-expression-trigger',

  onTriggerClick: function(){
    if (this.disabled) return;

    window.getExpressionValue = this.getValue.createDelegate(this);
    window.setExpressionValue = function(value) {
      this.setValue(value);
    }.createDelegate(this);
    mgnlOpenWindow('/.resources/expressions/grid-expression.html', 880, 320);
  }

});
