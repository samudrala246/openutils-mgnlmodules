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
      var menu;
      var menuName = info.contextMenu || 'default';
      jQuery.each(mgnlContextMenuInfo.menus, function(){
          if (this.name == menuName) {
        	menu = this;
          }
      });
      
      var initTrigger = function(trigger){
    	jQuery(trigger).contextMenu({
          menu: 'contextmenu-' + (info.contextMenu || 'default'),
          showCallback: info.showCallback
        }, function(itemName) {
          switch (itemName){
            case 'move':
              el.parents('.control-group:first').trigger('drag');
              return;
          }
          
          var entryName = info.entryName || '';
          if (itemName && itemName != 'default'){
            if (entryName) {
            	entryName += '_';
            }
            entryName += itemName;
          }
          var menuItem;
          jQuery.each(menu.items, function(){
            if (this.name == itemName) 
            {
            	menuItem = this;
            }
          });
          mgnlOpenDialogEx(info.path,null,null,null,'website','.magnolia/dialogs/contextmenu-' + menuItem.controlType + '.html',null,null,null,{
            entryName: entryName,
            globalEnabled: menuItem.globalEnabled,
            enterMode: info.enterMode 
          });
          
        });
      };
      
      var el = jQuery('#' + id);
      var trigger = info.parentTrigger ? el.parents(info.parentTrigger + ':first') : el;
      initTrigger(trigger);
      el.data("initTrigger", initTrigger);
      
    });
    
    
  }
});
