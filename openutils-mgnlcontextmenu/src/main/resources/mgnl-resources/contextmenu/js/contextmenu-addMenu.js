jQuery.extend({
  mgnlAddContextMenu: function( mgnlEditMessages, mgnlContextMenuInfo ) {

	jQuery.each(mgnlContextMenuInfo.menus, function(){
	    var ul = document.createElement('ul');
	    document.body.appendChild(ul);
	    jQuery(ul).attr('id', 'contextmenu-' + this.name).attr('class', 'contextMenu');
	    jQuery.each(this.items, function(){
	      var li = document.createElement('li');
	      ul.appendChild(li);
	      li.innerHTML = '<a href="#' + this.name + '" style=\"background-image: url(' + mgnlContextMenuInfo.contextPath + this.icon + ')\">' + this.text + '</a>';
	    });
	    if (this.mouseoverIcon){
	      var img = document.createElement('img');
	      jQuery(img)
	        .attr('id', 'contextmenufollow-' + this.name)
	        .attr('src', mgnlContextMenuInfo.contextPath + this.mouseoverIcon)
	        .css({ position: 'absolute', display: 'none' });
	      document.body.appendChild(img);
	    }
	  });  
	  
    jQuery.each(mgnlEditMessages, function(id, info){
      var initTrigger = function(trigger){
        $(trigger).contextMenu({
          menu: 'contextmenu-' + (info.contextMenu || 'default'),
          showCallback: info.showCallback
        }, function(action) {
          switch (action){
            case 'default':
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
