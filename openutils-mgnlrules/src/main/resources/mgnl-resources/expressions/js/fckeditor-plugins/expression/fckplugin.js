var InsertOptionCommand = function(name, options){
  this.Name = name;
  this.Options = options;
}

InsertOptionCommand.prototype = {
  Execute: function(optionName){
    FCKUndo.SaveUndoStep();
    FCK.InsertHtml(this.Options[optionName].value);
  },
  GetState: function(){
    return FCK_TRISTATE_OFF;
  }
};

var InsertOptionCombo = function(commandName, label, options){
  this.CommandName = commandName;
  this.Label = label;
  this.Options = options;
  this.Style = FCK_TOOLBARITEM_ONLYTEXT;
}

InsertOptionCombo.prototype = new FCKToolbarSpecialCombo;

InsertOptionCombo.prototype.GetLabel = function(){
  return this.Label;
}

InsertOptionCombo.prototype.CreateItems = function(targetSpecialCombo){
  for (var optionName in this.Options){
    targetSpecialCombo.AddItem(optionName, this.Options[optionName].caption);
  }
}

/* Expression button */
FCKCommands.RegisterCommand('Expression', new FCKDialogCommand('Expression', FCKLang.ExpressionDlgTitle, FCKPlugins.Items['expression'].Path + '../../../../../.magnolia/pages/expression-picker.html', 640, 300));
var oExpressionItem = new FCKToolbarButton('Expression', FCKLang.ExpressionBtn);
oExpressionItem.IconPath = FCKPlugins.Items['expression'].Path + 'expression.gif';
oExpressionItem.Style = FCK_TOOLBARITEM_ICONTEXT;
FCKToolbarItems.RegisterItem('Expression', oExpressionItem);

/* Arithmetic operators dropdown */
var arithmeticOperatorOptions = {
  'add': {
    caption: '+',
    value: ' + '
  },
  'sub': {
    caption: '-',
    value: ' - '
  },
  'mul': {
    caption: '*',
    value: ' * '
  },
  'div': {
    caption: '/',
    value: ' / '
  },
  'mod': {
    caption: '%',
    value: ' % '
  }
};
FCKCommands.RegisterCommand('ArithmeticOperator', new InsertOptionCommand('ArithmeticOperator', arithmeticOperatorOptions));
FCKToolbarItems.RegisterItem('ArithmeticOperator', new InsertOptionCombo('ArithmeticOperator', 'Arithmetic', arithmeticOperatorOptions));

/* Logical operators dropdown */
var logicalOperatorOptions = {
  'and': {
    caption: 'and',
    value: ' and '
  },
  'or': {
    caption: 'or',
    value: ' or '
  }
};
FCKCommands.RegisterCommand('LogicalOperator', new InsertOptionCommand('LogicalOperator', logicalOperatorOptions));
FCKToolbarItems.RegisterItem('LogicalOperator', new InsertOptionCombo('LogicalOperator', 'Logical', logicalOperatorOptions));

/* Unary operators dropdown */
var unaryOperatorOptions = {
  'not': {
    caption: 'not',
    value: '!'
  },
  'empty': {
    caption: 'empty',
    value: 'empty '
  }
};
FCKCommands.RegisterCommand('UnaryOperator', new InsertOptionCommand('UnaryOperator', unaryOperatorOptions));
FCKToolbarItems.RegisterItem('UnaryOperator', new InsertOptionCombo('UnaryOperator', 'Unary', unaryOperatorOptions));

/* Relational operators dropdown */
var relationalOperatorOptions = {
  'eq': {
    caption: '==',
    value: ' == '
  },
  'ne': {
    caption: '!=',
    value: ' != '
  },
  'lt': {
    caption: '<',
    value: ' < '
  },
  'gt': {
    caption: '>',
    value: ' > '
  },
  'le': {
    caption: '<=',
    value: ' <= '
  },
  'ge': {
    caption: '>=',
    value: ' >= '
  }
};
FCKCommands.RegisterCommand('RelationalOperator', new InsertOptionCommand('RelationalOperator', relationalOperatorOptions));
FCKToolbarItems.RegisterItem('RelationalOperator', new InsertOptionCombo('RelationalOperator', 'Relational', relationalOperatorOptions));

/* Validate expression button */
var ValidateExpressionCommand = function(name){
  this.Name = name;
}

ValidateExpressionCommand.prototype = {
  Execute: function(optionName){
    alert(this.Name);
  },
  GetState: function(){
    return FCK_TRISTATE_OFF;
  }
};

FCKCommands.RegisterCommand('ValidateExpression', new FCKDialogCommand('ValidateExpression', 'Validate', FCKPlugins.Items['expression'].Path + '../../../../../.magnolia/pages/expression-validator.html', 600, 300));
var oValidateExpressionItem = new FCKToolbarButton('ValidateExpression', 'Validate');
oValidateExpressionItem.IconPath = FCKPlugins.Items['expression'].Path + 'validate.gif';
oValidateExpressionItem.Style = FCK_TOOLBARITEM_ICONTEXT;
FCKToolbarItems.RegisterItem('ValidateExpression', oValidateExpressionItem);
