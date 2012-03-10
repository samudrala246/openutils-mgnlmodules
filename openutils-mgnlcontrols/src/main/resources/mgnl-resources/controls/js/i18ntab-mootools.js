var I18nTab = new Class({

  options: {
    // ctx: undefined,
    // path: undefined,
    // nodeCollection: undefined,
    // node: undefined,
    // dialog: undefined,
    // tab: undefined,
  },

  Implements: Options,

  initialize: function(id, options){
    this.id = id;
    this.setOptions(options);
    document.id(id + '_select').addEvent('change', this.handleChange.bindWithEvent(this));
  },

  handleChange: function(e){
    var locale = document.id(e.target).get('value');
    i18nTabLocales.each(function(item){
      var table = document.id(this.id + '_table_' + item);
      if (item == locale){
        if (table) table.setStyle('display', '');
        else this.loadTable(locale);
      } else if (table) table.setStyle('display', 'none');
    }, this);
  },

  loadTable: function(locale){
    var scripts;
    new I18nTab.Request({
      method: 'get',
      url: this.options.ctx + '/.magnolia/dialogs/i18nTabNewLocale.html',
      data: {
        mgnlPath: this.options.path,
        mgnlNodeCollection: this.options.nodeCollection,
        mgnlNode: this.options.node,
        mgnlI18nDialog: this.options.dialog,
        mgnlI18nTab: this.options.tab,
        mgnlI18nId: this.id,
        mgnlI18nLocale: locale
      },
      evalScripts: function(s) {
        scripts = s;
      },
      onSuccess: function(r) {
        new Element('div', {html: r}).getFirst().inject(this.id + '_append');
        $exec(scripts);
      }.bind(this)
    }).send();
  }

});

I18nTab.Request = new Class({

  Extends: Request,

  success: function(text, xml){
    this.parent(this.fixFckEditor(text), xml);
  },

  fixFckEditor: function(text){
    return text.replace(/<script[^>]*>([\s\S]*?)<\/script>/gi, function(){
      if (arguments[1].contains('fckInstance.Create();')){
        var hiddenId = 'fck_' + $random(0, 1000000);
        return '<input id="' + hiddenId + '" type="hidden" /><script>fckInstance.ToolbarSet = "MagnoliaStandard"; fckInstance._InsertHtmlBefore(fckInstance.CreateHtml(), document.getElementById("' + hiddenId + '"));</script>';
      } else {
        return '<script>' + arguments[1] + '</script>';
      }
    });
  }

});
