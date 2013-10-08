if(jQuery)(function(){
  jQuery.extend(jQuery.fn, {
    sortList: function(options){
      this.each(function(){

        var ol = this;
        var $ol = jQuery(this);
        $ol.addClass('sort-list');

        var drags = [];
        var drops = [];
        $ol.find('> .control-group').each(function(i, el){
          if (el.nodeType === 1){
            drags.push(el);
            var li = jQuery('<div class="control-drop"></div>');
            jQuery(el).before(li);
            drops.push(li[0]);
          }
        });
        
        var li = jQuery('<div class="control-group"></div>');
        $ol.append(li);
        drops.push(li[0]);


        var order = jQuery.merge([], options.order);
        for (var i = order.length, j = drags.length; i < j; i++){
          order.push(i);
        }
        var originalOrder = jQuery.merge([], order);

        var i1, i2;

        jQuery(drags).bind('drag', function(e){
          i1 = originalOrder[drags.indexOf(e.target)];
          
          $selected = jQuery(e.target);

          $selected.prev(".control-drop").addClass('highlighted');
          // $selected.next(".control-drop").addClass('highlighted');
          
          
          $ol.addClass('move');
          $ol.closest('.sort-list').find('> .control-group.hide').removeClass('hide');
          
          var $divShadow=jQuery('#mgnlMoveDivShadow');
          $divShadow.css({visibility: 'visible'});

          // globals per gli effetti di magnolia
          mgnlMoveNodeCollection ="";
          mgnlMoveNode ="02";
          mgnlMove=true;

          mgnlMoveNodeCollection+'__'+mgnlMoveNode
        });

        jQuery(drops).addClass('drop').click(function(e){
          e.stopPropagation();
          $ol.removeClass('move');
          jQuery('#mgnlMoveDivShadow').css({visibility: 'hidden'});
          i2 = drops.indexOf(this);
          i2 = i2 < drops.length - 1 ? originalOrder[i2] : null;
          var newOrder = [];
          jQuery.each(order, function(){
            if (this == i2) newOrder.push(i1);
            if (this != i1) newOrder.push(this);
          });

          if (i2 == null) newOrder.push(i1);

          jQuery.ajax({
            url: options.url,
            data: {
              path: options.path,
              name: options.name,
              value: newOrder.join(',')
            },
            success: function(){
              mgnlDialogReload(document);
            }
          });
        });
      });

    }
  });
})(jQuery);
