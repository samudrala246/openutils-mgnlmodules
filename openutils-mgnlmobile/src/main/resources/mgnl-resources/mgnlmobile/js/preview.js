var DevicePreview = new Class( {

	Implements : [ Options ],

	options : {},

	initialize : function(device, url) {
		this.device = device;
		this.url = url;
	},

	show : function() {
		// create iframe
		var iframe = new Element("IFRAME").setStyles( {
			'position' : 'absolute',
			'width' : '' + (this.device.screen.size[0] + 16) + 'px',
			'height' : '' + this.device.screen.size[1] + 'px',
			'left' : '' + this.device.screen.position[0] + 'px',
			'top' : '' + this.device.screen.position[1] + 'px',
			'overflow-y' : 'scroll',
			'background-color' : '#fff',
			'border' : 'none'
		}).set('frameborder','0').set('scrolling','no').set('src', this.url); 
		// + "?mgnlDeviceId=" + this.device.id
		
		var div = new Element("DIV").setStyles( {
			'margin' : 'auto',
			'margin-top' : '10px',
			'background' : '#fff url(' + this.device.image + ') top left no-repeat' ,
			'width' : '' + this.device.size[0] + 'px',
			'height' : '' + this.device.size[1] + 'px',
			'position' : 'relative'
		}).adopt(iframe);
		$(document.body).adopt(div);
	}
});