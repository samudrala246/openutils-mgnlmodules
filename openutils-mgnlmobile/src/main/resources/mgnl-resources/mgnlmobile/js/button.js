var previewWindow;

function clean(url) {
	url = mgnlUpdateCK(url);
	url = mgnlRemoveParameter(url, "mgnlDeviceId");
	url = mgnlRemoveParameter(url, "mgnlPreview");
	url = mgnlRemoveParameter(url, "mgnlIntercept");
	url = mgnlAddParameter(url,"mgnlIntercept","PREVIEW");
	url = mgnlAddParameter(url,"mgnlPreview","true");
	return url;
}

function openPreview(device) {
	
	var deviceTokens = device.split(",");
	var deviceId = deviceTokens[0];
	var deviceWidth = parseInt(deviceTokens[1]) + 80;
	var deviceHeight = parseInt(deviceTokens[2]) + 80;
	// freeze current view
	/*
	var overlay = document.getElementById("MB_PREVIEW_OL");
	if (!overlay || typeof(overlay) == 'undefined')
	{
	  overlay = document.createElement("DIV");
	  overlay.id = "MB_PREVIEW_OL";
	  overlay.style = "background: #999;position:absolute;top:0px;left:0px;width:100%;height:100%;z-index:10000;display:none";
	  document.body.appendChild(overlay);
	}
	overlay.style.display = "block";*/
	// open preview window
	previewWindow = window.open(contextPath + "/.magnolia/pages/mobilePreview.html?mgnlDeviceId=" + deviceId + "&url=" + encodeURIComponent(clean(location.href)),"mgnlMobilePreview","status=no,scrollbars=no,status=no,resizable=no,location=no,toolbar=no,menubar=no,width=" + deviceWidth + ",height="+ deviceHeight);
	return true;
}

function selectPreview() {
	SqueezeBox.initialize();
	SqueezeBox.open($('selectPreview'), {
		size : {
        	x :400,
        	y :300
      	}, 
		handler: 'adopt',
		 onOpen: function(e) {
			$('selectPreview').setStyle('display', 'block');
         },
         onClose: function(e) {
        	 redirectToHome();
         }
	});
 
}

function redirectToHome() {
	var url = window.parent.location.href;
    url = mgnlRemoveParameter(url, "mgnlPreview");
 	url = mgnlRemoveParameter(url, "mgnlIntercept");
    url = mgnlAddParameter(url,"mgnlIntercept","PREVIEW");
 	url = mgnlAddParameter(url,"mgnlPreview","false");
 	closePreview();
    window.parent.location.href = url;
}


function closePreview() {
	if (previewWindow) {
		try{
			previewWindow.close();
		}catch(e){}
	}
	
	mgnlPreview(false);
}

