jQuery.extend({
  mgnlAddContextMenu: function( mgnlEditMessages ) {
    jQuery.each(mgnlEditMessages, function(id, info){
      var initTrigger = function(trigger){
        $(trigger).contextMenu({
          menu: (info.contextMenu || 'default') + 'ContextMenu',
          showCallback: info.showCallback
        }, function(action) {
          switch (action){
            case 'text':
            case 'header':
              mgnlOpenDialogEx(info.path,null,null,null,'website','.magnolia/dialogs/contextmenu-edit.html',null,null,null,{
            	entryName: info.key
              });
              break;
            case 'fckText':
              mgnlOpenDialogEx(info.path,null,null,null,'website','.magnolia/dialogs/contextmenu-fckEdit.html',null,null,null,{
            	entryName: info.key,
                enterMode: info.enterMode
              });
              break;
            case 'label':
              mgnlOpenDialogEx(info.path,null,null,null,'website','.magnolia/dialogs/contextmenu-edit.html',null,null,null,{
                entryName: info.key
              });
              break;
            case 'description':
              mgnlOpenDialogEx(info.path,null,null,null,'website','.magnolia/dialogs/contextmenu-edit.html',null,null,null,{
                entryName: info.key + '.description'
              });
              break;
            case 'media':
               mgnlOpenDialogEx(info.path,null,null,null,'website','.magnolia/dialogs/contextmenu-media.html',null,null,null,{
                entryName: info.key 
              });
              break;  
            case 'textmedia':
            	// TO BE FIX
              mgnlOpenDialogEx(info.path,null,null,null,'website','.magnolia/dialogs/samples-contextmenu-textmedia.html',null,null,null,{
            	 entryName: info.key 
            	});
            	break;  
            case 'help':
              mgnlOpenDialogEx(info.path,null,null,null,'website','.magnolia/dialogs/contextmenu-fckEdit.html',null,null,null,{
                entryName: info.key + '.help',
                enterMode: 'br'
              });
              break;
            case 'info':
              mgnlOpenDialogEx(info.path,null,null,null,'website','.magnolia/dialogs/contextmenu-fckEdit.html',null,null,null,{
                entryName: info.key + '.info',
                enterMode: info.enterMode || 'br'
              });
              break;
            case 'move':
              el.parents('.control-group:first').trigger('drag');
              break;
            
          }
        });
      };
      var el = jQuery('#' + id);
      initTrigger(info.parentTrigger ? el.parents(info.parentTrigger + ':first') : el);
      el.data("initTrigger", initTrigger);
    });
  }
});
