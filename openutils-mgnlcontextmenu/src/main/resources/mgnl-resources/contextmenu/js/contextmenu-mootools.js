window.addEvent('domready', function(){
  mgnlContextMenuInfo.menus.each(function(menu){
    var ul = new Element('ul');
    ul.inject(document.body);
    ul.set('id', 'contextmenu-' + menu.name).addClass('contextMenu');
    menu.items.each(function(item){
      var li = new Element('li');
      li.inject(ul);
      li.set('html', '<a href="#' + item.name + '" style=\"background-image: url(' + mgnlContextMenuInfo.contextPath + item.icon + ')\">' + item.text + '</a>');
    });
    if (menu.mouseoverIcon){
      new Element('img', {
        id: 'contextmenufollow-' + menu.name,
        src: mgnlContextMenuInfo.contextPath + menu.mouseoverIcon
      }).setStyles({ position: 'absolute', display: 'none' }).inject(document.body);
    }
  });

  $H(mgnlContextMenuInfo.elements).each(function(info, id){
    var el = document.id(id);
    var trigger = info.parentTrigger ? el.getParent(info.parentTrigger) : el;
    var menuName = info.contextMenu || 'default';
    var menu;
    mgnlContextMenuInfo.menus.each(function(m){
      if (m.name == menuName) menu = m;
    });
    trigger.contextMenu({
      menu: 'contextmenu-' + menuName,
      showCallback: info.showCallback,
      callback: function(itemName) {
        var entryName = info.entryName || '';
        if (itemName && itemName != 'default'){
          if (entryName) entryName += '-';
          entryName += itemName;
        }
        var menuItem;
        menu.items.each(function(item){
          if (item.name == itemName) menuItem = item;
        });
        mgnlOpenDialogEx(info.path,null,null,null,'website','.magnolia/dialogs/contextmenu-' + menuItem.controlType + '.html',null,null,null,{
            entryName: entryName,
            globalEnabled: menuItem.globalEnabled
        });
      }
    });
    if (menu.mouseoverClass || menu.mouseoverIcon){
      var followId = 'contextmenufollow-' + menuName;
      var follow = document.id(followId);
      trigger.addEvent('mousemove', function(e){
        if (menu.mouseoverClass) trigger.addClass(menu.mouseoverClass);
        if (follow) follow.setStyles({ display: 'block', top: e.page.y - 80, left: e.page.x - 40 });
      });
      var fnLeave = function(){
        if (menu.mouseoverClass) trigger.removeClass(menu.mouseoverClass);
        if (follow) follow.setStyle('display', 'none');
      };
      trigger.addEvent('mouseleave', function(e){
        var relatedTarget = document.id(e.relatedTarget);
        if (!relatedTarget || relatedTarget.get('id') != followId) fnLeave();
      });
      if (follow){
        var preventLeave;
        follow.addEvents({
          mousemove: function(e){
            preventLeave = true;
            follow.setStyles({ top: e.page.y - 80, left: e.page.x - 40 });
            setTimeout(function() { preventLeave = false; }, 0);
          },
          mouseleave: function(e){
            if (preventLeave) return;
            var relatedTarget = document.id(e.relatedTarget);
            if (!relatedTarget || relatedTarget.get('id') != trigger.get('id')) fnLeave();
          }
        });
      }
    }
  });
});

var ContextMenu = new Class({

  Implements: Options,

  options: {
    inSpeed: 150,
    outSpeed: 75,
    callback: $empty
  },

  initialize: function(element, options){
    this.element = document.id(element);
    if(options.inSpeed == 0) options.inSpeed = -1;
    if(options.outSpeed == 0) options.outSpeed = -1;
    this.setOptions(options);
    this.position = this.element.getPosition();
    // Get this context menu
    this.menu = document.id(this.options.menu).addClass('contextMenu');
    this.fadeIn = new Fx.Tween(this.menu, { property: 'opacity', duration: this.options.inSpeed });
    this.fadeOut = new Fx.Tween(this.menu, { property: 'opacity', duration: this.options.outSpeed });
    var contextmenuHandler = this.handleContextmenu.bindWithEvent(this);
    this.element.addEvents({
      'mousedown': this.handleMousedown.bindWithEvent(this),
      'contextmenu': contextmenuHandler
    });

    // Disable text selection
    // TODO
    
    // Disable browser context menu
    $$('ul.contextMenu').addEvent('contextmenu', contextmenuHandler);
  },

  handleMousedown: function(e){
    e.stop();
    this.element.addEvent('mouseup', this.handleMouseup.bindWithEvent(this));
  },

  handleMouseup: function(e){
    e.stop();
    this.element.removeEvents('mouseup');
    if (e.rightClick){
      // Hide context menus that may be showing
      $$('.contextMenu').setStyle('display', 'none');

      if (this.element.hasClass('disabled')) return false;

      // Detect mouse position
      this.x = e.page.x;
      this.y = e.page.y;

      // Show the menu
      if (this.options.showCallback) this.options.showCallback(this.menu);
      this.menu.setStyles({ top: this.y, left: this.x, display: 'block' });
      this.fadeIn.start(0, 1);
      // Hover events
      this.menu.getElements('a').addEvents({
        'mouseover': this.handleMouseover.bindWithEvent(this),
        'mouseout': this.handleMouseout.bindWithEvent(this)
      });

      // Keyboard
      document.addEvent('keypress', this.handleKeypress.bindWithEvent(this));

      // When items are selected
      this.menu.getElements('a').removeEvents('click');
      this.menu.getElements('li:not(.disabled) a').addEvent('click', this.handleClick.bindWithEvent(this));

      // Hide bindings
      (function(){
        document.addEvent('click', this.handleDocumentClick.bindWithEvent(this));
      }).delay(10, this);
    }
  },

  handleMouseover: function(e){
    this.menu.getElements('li.hover').removeClass('hover');
    document.id(e.target).getParent('li').addClass('hover');
  },

  handleMouseout: function(e){
    this.menu.getElements('li.hover').removeClass('hover');
  },

  handleKeypress: function(e){
    var liHover;
    switch (e.key){
      case 'up':
        liHover = this.menu.getElement('li.hover');
        if (!liHover){
          this.menu.getElement('li:last-child').addClass('hover');
        } else {
          var liPrev = liHover.removeClass('hover').getPrevious('li:not(.disabled)');
          if (liPrev) liPrev.addClass('hover');
          else this.menu.getElement('li:last-child').addClass('hover');
        }
        break;
      case 'down':
        liHover = this.menu.getElement('li.hover');
        if (!liHover){
          this.menu.getElement('li:first-child').addClass('hover');
        } else {
          var liNext = liHover.removeClass('hover').getNext('li:not(.disabled)');
          if (liNext) liNext.addClass('hover');
          else this.menu.getElement('li:first-child').addClass('hover');
        }
        break;
      case 'enter':
        this.menu.getElement('li.hover a').fireEvent('click');
        break;
      case 'esc':
        document.fireEvent('click');
        break;
    }
  },

  handleClick: function(e){
    e.stop();
    document.removeEvents('click');
    document.removeEvents('keypress');
    $$('.contextMenu').setStyle('display', 'none');
    // Callback
    this.options.callback(document.id(e.target).get('href').substr(1), this.element, {
      x: this.x - this.position.x,
      y: this.y - this.position.y,
      docX: this.x,
      docY: this.y
    });
  },

  handleDocumentClick: function(e){
    e.stop();
    document.removeEvents('click').removeEvents('keypress');
    this.fadeOut.start(1, 0);
  },

  handleContextmenu: function(e){
    e.stop();
  }

});

Element.implement('contextMenu', function(options){
  if (!$defined(options.menu)) return false;
  new ContextMenu(this, options);
  return this;
});
