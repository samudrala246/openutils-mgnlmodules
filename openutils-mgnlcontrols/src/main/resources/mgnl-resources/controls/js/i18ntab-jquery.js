$.I18nTab = function(id, options) {
  $('#' + id + '_select').change(handleChange);

  function handleChange(e){
    var locale = $(e.target).val();
    jQuery.each(i18nTabLocales, function(){
      var table = $('#' + id + '_table_' + this);
      if (this == locale){
        if (table.length) table.css('display', '');
        else loadTable(locale);
      } else if (table.length) table.css('display', 'none');
    });
  }

  function loadTable(locale){
    $.ajax({
      url: options.ctx + '/.magnolia/dialogs/i18nTabNewLocale.html',
      data: {
        mgnlPath: options.path,
        mgnlNodeCollection: options.nodeCollection,
        mgnlNode: options.node,
        mgnlI18nDialog: options.dialog,
        mgnlI18nTab: options.tab,
        mgnlI18nId: id,
        mgnlI18nLocale: locale
      },
      success: function(r) {
        var scripts = [];
        r = fixFckEditor(r).replace(/<script[^>]*>([\s\S]*?)<\/script>/gi, function(){
          scripts.push(arguments[1]);
          return '';
        });
        var div = document.createElement('div');
        div.innerHTML = r;
        $('#' + id + '_append').append(div.firstChild);
        jQuery.each(scripts, function() { jQuery.globalEval(this) });
      }
    });
  }

  function fixFckEditor(text){
    return text.replace(/<script[^>]*>([\s\S]*?)<\/script>/gi, function(){
      if (arguments[1].indexOf('fckInstance.Create();') >= 0){
        var hiddenId = 'fck_' + Math.floor(Math.random() * 1000001);
        return '<input id="' + hiddenId + '" type="hidden" /><script>fckInstance.ToolbarSet = "MagnoliaStandard"; fckInstance._InsertHtmlBefore(fckInstance.CreateHtml(), document.getElementById("' + hiddenId + '"));</script>';
      } else {
        return '<script>' + arguments[1] + '</script>';
      }
    });
  }
};