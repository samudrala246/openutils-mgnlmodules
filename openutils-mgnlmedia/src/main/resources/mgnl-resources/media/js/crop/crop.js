(function($) {
	function init(container, params) {
		var media = container.find(".pzcMedia");
		var mediaImg = container.find(".pzcMedia img");
		var pczZoomPanel = container.find(".pzcZoomPanel");
		var pzcSlider = container.find(".pzcSlider");

		var modified = false;

		$(media).draggable( {
			containment : 'parent',
			cursor : 'move',
			refreshPositions : true,
			start : function() {
				modified = true;
			}
		});

		var width = container.width();
		var height = container.height();

		var imgW = parseInt(container.attr('data-width'));
		var imgH = parseInt(container.attr('data-height'));
		var property = container.attr('data-property');
		var node = container.attr('data-node');

		var actZoom = Math.min(
				parseInt(100 * $(".pzcMedia img").width() / imgW), 100);
		var minZoom = Math.max(Math.ceil(100 * width / imgW), Math.ceil(100
				* height / imgH));

		if (width < 100) {
			pzcSlider.css( {
				width : '' + (width - 10) + 'px',
				'margin-left' : '-' + ((width - 10) / 2) + 'px'
			});
		}

		pczZoomPanel.html('' + actZoom + '%');

		container.find(".pzcExit").click(
				function() {
					return !modified
							|| confirm("Are you sure to exit without saving?");
				});

		container.find(".pzcSave").click(
				function() {
					var href = $(this).attr('href');
					$.ajax( {
						url : crop_app_ctx + '/media/pzc',
						data : {
							zoom : actZoom,
							x : Math.floor(container.offset().left
									- mediaImg.offset().left),
							y : Math.floor(container.offset().top
									- mediaImg.offset().top),
							id : property,
							handle : node
						},
						// type : 'HEAD',
						error : function(err) {
							alert(err);
						},
						success : function(msg) {
							location.href = href;
						}
					});
					return false;
				});

		var zoomFn = function(zoom) {
			actZoom = zoom;
			pczZoomPanel.html('' + actZoom + '%');
			var w = parseInt(actZoom * imgW / 100);
			var h = parseInt(w * imgH / imgW);
			if (w >= width && h >= height) {
				var newLeft = width - w;
				var newTop = height - h;
				var left = parseInt(media.css('left'));
				var top = parseInt(media.css('top'));
				left = (left > w - width) ? w - width : left;
				top = (top > h - height) ? h - height : top;
				$(".pzcMedia").css( {
					width : '' + w + 'px',
					height : '' + h + 'px',
					left : '' + left + 'px',
					top : '' + top + 'px',
					padding : '' + (imgH - h) + 'px ' + (imgW - w) + 'px'
				});
			}
		}

		pzcSlider.slider( {
			min : minZoom,
			max : 100,
			value : actZoom,
			slide : function(event, ui) {
				modified = true;
				zoomFn(ui.value);
			}
		});
		pzcSlider.fadeTo('slow', 0.4);
		pzcSlider.mouseenter(function() {
			pzcSlider.fadeTo('slow', 1.0);
			pczZoomPanel.fadeTo('slow', 0.7);
		});
		pzcSlider.mouseleave(function() {
			pzcSlider.fadeTo('slow', 0.4);
			pczZoomPanel.fadeTo('slow', 0);
		});

		// zoomFn(actZoom);
	}
	;

	$.fn.mgnlMediaPZC = function(params) {
		var params = $.extend( {
		/*
		 * defaultOpened : 1, tabLinks : '.tablinks a[href^=#]', tabContents :
		 * '.tab section'
		 */
		}, params);
		this.each(function(i, el) {
			init($(el), params);
		});
		return this;
	};

})($);

$('head')
	.append(
		'<link type="text/css" rel="stylesheet" href="'+ crop_app_ctx +'/.resources/media/css/crop/pzc.css" />');
$('head')
	.append(
		'<link type="text/css" rel="stylesheet" href="'+ crop_app_ctx +'/.resources/media/css/crop/jquery-ui-1.8.12.custom.css" />');

var refreshMediaPZC = function() {
	$(".pzc").mgnlMediaPZC();
	$(".pzcEdit").fadeTo('slow', 0.4);
	$(".pzcEdit").mouseenter(function() {
		$(this).fadeTo('slow', 1.0);
	});
	$(".pzcEdit").mouseleave(function() {
		$(this).fadeTo('slow', 0.4);
	});
	$(".pzcDelete").click(function() {
		var href = $(this).attr('href');
		var aId = $(this).attr('id');
		var node = aId.split('.')[0];
		var property = aId.split('.')[1];
		$.ajax( {
			url : crop_app_ctx +'/media/pzc',
			data : {
				command : 'delete',
				id : property,
				handle : node
			},
			// type : 'HEAD',
			error : function(err) {
				alert(err);
			},
			success : function(msg) {
				location.href = href;
			}
		});
		return false;
	});
}

$(document).ready(refreshMediaPZC);
$(document).ajaxSuccess(refreshMediaPZC);
