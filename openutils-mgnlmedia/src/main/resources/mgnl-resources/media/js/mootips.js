var TipsEx = new Class( {

	Extends :Tips,

	initialize : function(elements, options) {
		this.parent(elements, options);
		this.tip.addEvents( {
			mouseenter :this.enterTip.bind(this),
			mouseleave :this.leaveTip.bind(this)
		});
	},

	enterTip : function() {
		this.timer = $clear(this.timer);
	},

	leaveTip : function() {
		$clear(this.timer);
		this.timer = this.hide.delay(this.options.hideDelay, this);
	},

	position : function(event) {
		var size = window.getSize(), scroll = window.getScroll();
		var tip = {
			x :this.tip.offsetWidth,
			y :this.tip.offsetHeight
		};
		var props = {
			x :'left',
			y :'top'
		};
		for ( var z in props) {
			var pos = event.page[z] + this.options.offsets[z];
			if ((pos + tip[z] - scroll[z]) > size[z]) {
				pos = pos - ((pos + tip[z] - scroll[z]) - size[z]);
				if (pos < 0)
				{
					pos = 0;
				}
			}
			this.tip.setStyle(props[z], pos);
		}
	}

});