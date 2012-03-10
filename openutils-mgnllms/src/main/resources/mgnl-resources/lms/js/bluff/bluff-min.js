Bluff = {
	VERSION : '0.3.6',
	array : function(c) {
		if (c.length === undefined)
			return [ c ];
		var d = [], f = c.length;
		while (f--)
			d[f] = c[f];
		return d
	},
	array_new : function(c, d) {
		var f = [];
		while (c--)
			f.push(d);
		return f
	},
	each : function(c, d, f) {
		for ( var g = 0, h = c.length; g < h; g++) {
			d.call(f || null, c[g], g)
		}
	},
	index : function(c, d) {
		for ( var f = 0, g = c.length; f < g; f++) {
			if (c[f] === d)
				return f
		}
		return -1
	},
	keys : function(c) {
		var d = [], f;
		for (f in c)
			d.push(f);
		return d
	},
	map : function(d, f, g) {
		var h = [];
		this.each(d, function(c) {
			h.push(f.call(g || null, c))
		});
		return h
	},
	reverse_each : function(c, d, f) {
		var g = c.length;
		while (g--)
			d.call(f || null, c[g], g)
	},
	sum : function(c) {
		var d = 0, f = c.length;
		while (f--)
			d += c[f];
		return d
	},
	Mini : {}
};
Bluff.Base = new JS.Class(
		{
			extend : {
				DEBUG : false,
				DATA_LABEL_INDEX : 0,
				DATA_VALUES_INDEX : 1,
				DATA_COLOR_INDEX : 2,
				LEGEND_MARGIN : 20,
				TITLE_MARGIN : 20,
				LABEL_MARGIN : 10,
				DEFAULT_MARGIN : 20,
				DEFAULT_TARGET_WIDTH : 800,
				THOUSAND_SEPARATOR : ','
			},
			top_margin : null,
			bottom_margin : null,
			right_margin : null,
			left_margin : null,
			title_margin : null,
			legend_margin : null,
			labels : null,
			center_labels_over_point : null,
			has_left_labels : null,
			x_axis_label : null,
			y_axis_label : null,
			y_axis_increment : null,
			colors : null,
			title : null,
			font : null,
			font_color : null,
			hide_line_markers : null,
			hide_legend : null,
			hide_title : null,
			hide_line_numbers : null,
			no_data_message : null,
			title_font_size : null,
			legend_font_size : null,
			marker_font_size : null,
			marker_color : null,
			marker_count : null,
			minimum_value : null,
			maximum_value : null,
			sort : null,
			additional_line_values : null,
			stacked : null,
			legend_box_size : null,
			tooltips : false,
			initialize : function(c, d) {
				this._0 = new Bluff.Renderer(c);
				d = d || this.klass.DEFAULT_TARGET_WIDTH;
				var f;
				if (typeof d !== 'number') {
					f = d.split('x');
					this._j = parseFloat(f[0]);
					this._p = parseFloat(f[1])
				} else {
					this._j = parseFloat(d);
					this._p = this._j * 0.75
				}
				this.initialize_ivars();
				this._1e();
				this.theme_keynote()
			},
			initialize_ivars : function() {
				this._c = 800;
				this._M = 800 * (this._p / this._j);
				this._5 = 0;
				this.marker_count = null;
				this.maximum_value = this.minimum_value = null;
				this._a = false;
				this._2 = [];
				this.labels = {};
				this._q = {};
				this.sort = true;
				this.title = null;
				this._b = this._j / this._c;
				this.marker_font_size = 21.0;
				this.legend_font_size = 20.0;
				this.title_font_size = 36.0;
				this.top_margin = this.bottom_margin = this.left_margin = this.right_margin = this.klass.DEFAULT_MARGIN;
				this.legend_margin = this.klass.LEGEND_MARGIN;
				this.title_margin = this.klass.TITLE_MARGIN;
				this.legend_box_size = 20.0;
				this.no_data_message = "No Data";
				this.hide_line_markers = this.hide_legend = this.hide_title = this.hide_line_numbers = false;
				this.center_labels_over_point = true;
				this.has_left_labels = false;
				this.additional_line_values = [];
				this._1x = [];
				this._k = {};
				this.x_axis_label = this.y_axis_label = null;
				this.y_axis_increment = null;
				this.stacked = null;
				this._9 = null
			},
			set_margins : function(c) {
				this.top_margin = this.left_margin = this.right_margin = this.bottom_margin = c
			},
			set_font : function(c) {
				this.font = c;
				this._0.font = this.font
			},
			add_color : function(c) {
				this.colors.push(c)
			},
			replace_colors : function(c) {
				this.colors = c || [];
				this._u = 0
			},
			set_theme : function(c) {
				this._1e();
				this._k = {
					colors : [ 'black', 'white' ],
					additional_line_colors : [],
					marker_color : 'white',
					font_color : 'black',
					background_colors : null,
					background_image : null
				};
				for ( var d in c)
					this._k[d] = c[d];
				this.colors = this._k.colors;
				this.marker_color = this._k.marker_color;
				this.font_color = this._k.font_color || this.marker_color;
				this._1x = this._k.additional_line_colors;
				this._Y()
			},
			theme_keynote : function() {
				this._Z = '#6886B4';
				this._10 = '#FDD84E';
				this._r = '#72AE6E';
				this._z = '#D1695E';
				this._11 = '#8A6EAF';
				this._A = '#EFAA43';
				this._B = 'white';
				this.colors = [ this._10, this._Z, this._r, this._z, this._11,
						this._A, this._B ];
				this.set_theme( {
					colors : this.colors,
					marker_color : 'white',
					font_color : 'white',
					background_colors : [ 'black', '#4a465a' ]
				})
			},
			theme_37signals : function() {
				this._r = '#339933';
				this._11 = '#cc99cc';
				this._Z = '#336699';
				this._10 = '#FFF804';
				this._z = '#ff0000';
				this._A = '#cf5910';
				this._C = 'black';
				this.colors = [ this._10, this._Z, this._r, this._z, this._11,
						this._A, this._C ];
				this.set_theme( {
					colors : this.colors,
					marker_color : 'black',
					font_color : 'black',
					background_colors : [ '#d1edf5', 'white' ]
				})
			},
			theme_rails_keynote : function() {
				this._r = '#00ff00';
				this._12 = '#333333';
				this._A = '#ff5d00';
				this._z = '#f61100';
				this._B = 'white';
				this._13 = '#999999';
				this._C = 'black';
				this.colors = [ this._r, this._12, this._A, this._z, this._B,
						this._13, this._C ];
				this.set_theme( {
					colors : this.colors,
					marker_color : 'white',
					font_color : 'white',
					background_colors : [ '#0083a3', '#0083a3' ]
				})
			},
			theme_odeo : function() {
				this._12 = '#202020';
				this._B = 'white';
				this._1y = '#a21764';
				this._r = '#8ab438';
				this._13 = '#999999';
				this._1z = '#3a5b87';
				this._C = 'black';
				this.colors = [ this._12, this._B, this._1z, this._1y, this._r,
						this._13, this._C ];
				this.set_theme( {
					colors : this.colors,
					marker_color : 'white',
					font_color : 'white',
					background_colors : [ '#ff47a4', '#ff1f81' ]
				})
			},
			theme_pastel : function() {
				this.colors = [ '#a9dada', '#aedaa9', '#daaea9', '#dadaa9',
						'#a9a9da', '#daaeda', '#dadada' ];
				this.set_theme( {
					colors : this.colors,
					marker_color : '#aea9a9',
					font_color : 'black',
					background_colors : 'white'
				})
			},
			theme_greyscale : function() {
				this.colors = [ '#282828', '#383838', '#686868', '#989898',
						'#c8c8c8', '#e8e8e8' ];
				this.set_theme( {
					colors : this.colors,
					marker_color : '#aea9a9',
					font_color : 'black',
					background_colors : 'white'
				})
			},
			data : function(f, g, h) {
				g = (g === undefined) ? [] : g;
				h = h || null;
				g = Bluff.array(g);
				this._2.push( [ f, g, (h || this._1A()) ]);
				this._5 = (g.length > this._5) ? g.length : this._5;
				Bluff.each(g, function(c, d) {
					if (c === undefined)
						return;
					if (this.maximum_value === null
							&& this.minimum_value === null)
						this.maximum_value = this.minimum_value = c;
					this.maximum_value = this._1f(c) ? c : this.maximum_value;
					if (this.maximum_value >= 0)
						this._a = true;
					this.minimum_value = this._1B(c) ? c : this.minimum_value;
					if (this.minimum_value < 0)
						this._a = true
				}, this)
			},
			draw : function() {
				if (this.stacked)
					this._1C();
				this._1D();
				this._v(function() {
					this._0.rectangle(this.left_margin, this.top_margin,
							this._c - this.right_margin, this._M
									- this.bottom_margin);
					this._0.rectangle(this._1, this._7, this._l, this._g)
				})
			},
			clear : function() {
				this._Y()
			},
			_1D : function() {
				if (!this._a)
					return this._1E();
				this._14();
				this._1F();
				if (this.sort)
					this._1G();
				this._1H();
				this._N();
				this._1I();
				this._1J()
			},
			_14 : function(g) {
				if (this._9 === null || g === true) {
					this._9 = [];
					if (!this._a)
						return;
					this._1g();
					Bluff.each(this._2, function(d) {
						var f = [];
						Bluff.each(d[this.klass.DATA_VALUES_INDEX],
								function(c) {
									if (c === null || c === undefined)
										f.push(null);
									else
										f.push((c - this.minimum_value)
												/ this._i)
								}, this);
						this._9.push( [ d[this.klass.DATA_LABEL_INDEX], f,
								d[this.klass.DATA_COLOR_INDEX] ])
					}, this)
				}
			},
			_1g : function() {
				this._i = this.maximum_value - this.minimum_value;
				this._i = this._i > 0 ? this._i : 1;
				var c = Math.round(Math.LOG10E * Math.log(this._i));
				this._1h = Math.pow(10, 3 - c)
			},
			_1F : function() {
				this._O = this.hide_line_markers ? 0 : this
						._P(this.marker_font_size);
				this._1i = this.hide_title ? 0 : this._P(this.title_font_size);
				this._1j = this.hide_legend ? 0 : this
						._P(this.legend_font_size);
				var c, d, f, g, h, i, j;
				if (this.hide_line_markers) {
					this._1 = this.left_margin;
					this._15 = this.right_margin;
					this._1k = this.bottom_margin
				} else {
					d = 0;
					if (this.has_left_labels) {
						c = '';
						for (j in this.labels) {
							c = c.length > this.labels[j].length ? c
									: this.labels[j]
						}
						d = this._D(this.marker_font_size, c) * 1.25
					} else {
						d = this._D(this.marker_font_size, this
								._Q(this.maximum_value))
					}
					f = this.hide_line_numbers && !this.has_left_labels ? 0.0
							: d + this.klass.LABEL_MARGIN * 2;
					this._1 = this.left_margin
							+ f
							+ (this.y_axis_label === null ? 0.0 : this._O
									+ this.klass.LABEL_MARGIN * 2);
					g = -Infinity;
					for (j in this.labels)
						g = g > Number(j) ? g : Number(j);
					g = Math.round(g);
					h = (g >= (this._5 - 1) && this.center_labels_over_point) ? this
							._D(this.marker_font_size, this.labels[g]) / 2
							: 0;
					this._15 = this.right_margin + h;
					this._1k = this.bottom_margin + this._O
							+ this.klass.LABEL_MARGIN
				}
				this._l = this._c - this._15;
				this._6 = this._c - this._1 - this._15;
				this._7 = this.top_margin
						+ (this.hide_title ? this.title_margin : this._1i
								+ this.title_margin)
						+ (this.hide_legend ? this.legend_margin : this._1j
								+ this.legend_margin);
				i = (this.x_axis_label === null) ? 0.0 : this._O
						+ this.klass.LABEL_MARGIN;
				this._g = this._M - this._1k - i;
				this._3 = this._g - this._7
			},
			_1I : function() {
				if (this.x_axis_label) {
					var c = this._g + this.klass.LABEL_MARGIN * 2 + this._O;
					this._0.fill = this.font_color;
					if (this.font)
						this._0.font = this.font;
					this._0.stroke = 'transparent';
					this._0.pointsize = this._d(this.marker_font_size);
					this._0.gravity = 'north';
					this._0.annotate_scaled(this._c, 1.0, 0.0, c,
							this.x_axis_label, this._b);
					this._v(function() {
						this._0.line(0.0, c, this._c, c)
					})
				}
			},
			_N : function() {
				if (this.hide_line_markers)
					return;
				if (this.y_axis_increment === null) {
					if (this.marker_count === null) {
						Bluff.each( [ 3, 4, 5, 6, 7 ], function(c) {
							if (!this.marker_count && this._i % c === 0)
								this.marker_count = c
						}, this);
						this.marker_count = this.marker_count || 4
					}
					this._16 = (this._i > 0) ? this._17(this._i
							/ this.marker_count) : 1
				} else {
					this.maximum_value = Math.max(
							Math.ceil(this.maximum_value),
							this.y_axis_increment);
					this.minimum_value = Math.floor(this.minimum_value);
					this._1g();
					this._14(true);
					this.marker_count = Math.round(this._i
							/ this.y_axis_increment);
					this._16 = this.y_axis_increment
				}
				this._1K = this._3 / (this._i / this._16);
				var d, f, g, h;
				for (d = 0, f = this.marker_count; d <= f; d++) {
					g = this._7 + this._3 - d * this._1K;
					this._0.stroke = this.marker_color;
					this._0.stroke_width = 1;
					this._0.line(this._1, g, this._l, g);
					h = d * this._16 + this.minimum_value;
					if (!this.hide_line_numbers) {
						this._0.fill = this.font_color;
						if (this.font)
							this._0.font = this.font;
						this._0.font_weight = 'normal';
						this._0.stroke = 'transparent';
						this._0.pointsize = this._d(this.marker_font_size);
						this._0.gravity = 'east';
						this._0.annotate_scaled(this._1
								- this.klass.LABEL_MARGIN, 1.0, 0.0, g, this
								._Q(h), this._b)
					}
				}
			},
			_1l : function(c) {
				return (this._c - c) / 2
			},
			_1H : function() {
				if (this.hide_legend)
					return;
				this._E = Bluff.map(this._2, function(c) {
					return c[this.klass.DATA_LABEL_INDEX]
				}, this);
				var i = this.legend_box_size;
				if (this.font)
					this._0.font = this.font;
				this._0.pointsize = this.legend_font_size;
				var j = [ [] ];
				Bluff.each(this._E, function(c) {
					var d = j.length - 1;
					var f = this._0.get_type_metrics(c);
					var g = f.width + i * 2.7;
					j[d].push(g);
					if (Bluff.sum(j[d]) > (this._c * 0.9))
						j.push( [ j[d].pop() ])
				}, this);
				var k = this._1l(Bluff.sum(j[0]));
				var l = this.hide_title ? this.top_margin + this.title_margin
						: this.top_margin + this.title_margin + this._1i;
				this._v(function() {
					this._0.stroke_width = 1;
					this._0.line(0, l, this._c, l)
				});
				Bluff.each(this._E, function(c, d) {
					this._0.fill = this.font_color;
					if (this.font)
						this._0.font = this.font;
					this._0.pointsize = this._d(this.legend_font_size);
					this._0.stroke = 'transparent';
					this._0.font_weight = 'normal';
					this._0.gravity = 'west';
					this._0.annotate_scaled(this._c, 1.0, k + (i * 1.7), l, c,
							this._b);
					this._0.stroke = 'transparent';
					this._0.fill = this._2[d][this.klass.DATA_COLOR_INDEX];
					this._0.rectangle(k, l - i / 2.0, k + i, l + i / 2.0);
					this._0.pointsize = this.legend_font_size;
					var f = this._0.get_type_metrics(c);
					var g = f.width + (i * 2.7), h;
					j[0].shift();
					if (j[0].length == 0) {
						this._v(function() {
							this._0.line(0.0, l, this._c, l)
						});
						j.shift();
						if (j.length > 0)
							k = this._1l(Bluff.sum(j[0]));
						h = Math.max(this._1j, i) + this.legend_margin;
						if (j.length > 0) {
							l += h;
							this._7 += h;
							this._3 = this._g - this._7
						}
					} else {
						k += g
					}
				}, this);
				this._u = 0
			},
			_1J : function() {
				if (this.hide_title || !this.title)
					return;
				this._0.fill = this.font_color;
				if (this.font)
					this._0.font = this.font;
				this._0.pointsize = this._d(this.title_font_size);
				this._0.font_weight = 'bold';
				this._0.gravity = 'north';
				this._0.annotate_scaled(this._c, 1.0, 0, this.top_margin,
						this.title, this._b)
			},
			_e : function(c, d) {
				if (this.hide_line_markers)
					return;
				var f;
				if (this.labels[d] && !this._q[d]) {
					f = this._g + this.klass.LABEL_MARGIN;
					this._0.fill = this.font_color;
					if (this.font)
						this._0.font = this.font;
					this._0.stroke = 'transparent';
					this._0.font_weight = 'normal';
					this._0.pointsize = this._d(this.marker_font_size);
					this._0.gravity = 'north';
					this._0.annotate_scaled(1.0, 1.0, c, f, this.labels[d],
							this._b);
					this._q[d] = true;
					this._v(function() {
						this._0.stroke_width = 1;
						this._0.line(0.0, f, this._c, f)
					})
				}
			},
			_F : function(c, d, f, g, h, i, j) {
				if (!this.tooltips)
					return;
				this._0.tooltip(c, d, f, g, h, i, j)
			},
			_1E : function() {
				this._0.fill = this.font_color;
				if (this.font)
					this._0.font = this.font;
				this._0.stroke = 'transparent';
				this._0.font_weight = 'normal';
				this._0.pointsize = this._d(80);
				this._0.gravity = 'center';
				this._0.annotate_scaled(this._c, this._M / 2, 0, 10,
						this.no_data_message, this._b)
			},
			_Y : function() {
				var c = this._k.background_colors;
				switch (true) {
				case c instanceof Array:
					this._1L.apply(this, c);
					break;
				case typeof c === 'string':
					this._1M(c);
					break;
				default:
					this._1N(this._k.background_image);
					break
				}
			},
			_1M : function(c) {
				this._0.render_solid_background(this._j, this._p, c)
			},
			_1L : function(c, d) {
				this._0.render_gradiated_background(this._j, this._p, c, d)
			},
			_1N : function(c) {
			},
			_1e : function() {
				this._u = 0;
				this._q = {};
				this._k = {};
				this._0.scale(this._b, this._b)
			},
			_2d : function(c) {
				return this._b * c
			},
			_d : function(c) {
				var d = c * this._b;
				return d
			},
			_R : function(c, d) {
				return (c > d) ? d : c
			},
			_1f : function(c, d) {
				return c > this.maximum_value
			},
			_1B : function(c, d) {
				return c < this.minimum_value
			},
			_1m : function(c, d) {
				return c
			},
			_2e : function(c, d) {
				return c
			},
			_17 : function(c) {
				if (c == 0)
					return 1.0;
				var d = 1.0;
				while (c < 10) {
					c *= 10;
					d /= 10
				}
				while (c > 100) {
					c /= 10;
					d *= 10
				}
				return Math.floor(c) * d
			},
			_1G : function() {
				var f = this._1O, g = this.klass.DATA_VALUES_INDEX;
				this._9.sort(function(c, d) {
					return f(d[g]) - f(c[g])
				});
				this._2.sort(function(c, d) {
					return f(d[g]) - f(c[g])
				})
			},
			_1O : function(d) {
				var f = 0;
				Bluff.each(d, function(c) {
					f += (c || 0)
				});
				return f
			},
			_1C : function() {
				var g = [], h = this._5;
				while (h--)
					g[h] = 0;
				Bluff.each(this._2, function(f) {
					Bluff.each(f[this.klass.DATA_VALUES_INDEX], function(c, d) {
						g[d] += c
					}, this);
					f[this.klass.DATA_VALUES_INDEX] = Bluff.array(g)
				}, this)
			},
			_v : function(c) {
				if (this.klass.DEBUG) {
					this._0.fill = 'transparent';
					this._0.stroke = 'turquoise';
					c.call(this)
				}
			},
			_1A : function() {
				var c = this._u;
				this._u = (this._u + 1) % this.colors.length;
				return this.colors[c]
			},
			_Q : function(c) {
				var d = this.klass.THOUSAND_SEPARATOR, f = (this._i
						% this.marker_count == 0 || this.y_axis_increment !== null) ? String(Math
						.round(c))
						: String(Math.floor(c * this._1h) / this._1h);
				var g = f.split('.');
				g[0] = g[0].replace(/(\d)(?=(\d\d\d)+(?!\d))/g, '$1' + d);
				return g.join('.')
			},
			_P : function(c) {
				return this._0.caps_height(c)
			},
			_D : function(c, d) {
				return this._0.text_width(c, d)
			}
		});
Bluff.Area = new JS.Class(Bluff.Base, {
	draw : function() {
		this.callSuper();
		if (!this._a)
			return;
		this._S = this._6 / (this._5 - 1);
		this._0.stroke = 'transparent';
		Bluff.each(this._9, function(h) {
			var i = [], j = 0.0, k = 0.0;
			Bluff.each(h[this.klass.DATA_VALUES_INDEX], function(c, d) {
				var f = this._1 + (this._S * d);
				var g = this._7 + (this._3 - c * this._3);
				if (j > 0 && k > 0) {
					i.push(f);
					i.push(g)
				} else {
					i.push(this._1);
					i.push(this._g - 1);
					i.push(f);
					i.push(g)
				}
				this._e(f, d);
				j = f;
				k = g
			}, this);
			i.push(this._l);
			i.push(this._g - 1);
			i.push(this._1);
			i.push(this._g - 1);
			this._0.fill = h[this.klass.DATA_COLOR_INDEX];
			this._0.polyline(i)
		}, this)
	}
});
Bluff.BarConversion = new JS.Class( {
	mode : null,
	zero : null,
	graph_top : null,
	graph_height : null,
	minimum_value : null,
	spread : null,
	getLeftYRightYscaled : function(c, d) {
		var f;
		switch (this.mode) {
		case 1:
			d[0] = this.graph_top + this.graph_height * (1 - c) + 1;
			d[1] = this.graph_top + this.graph_height - 1;
			break;
		case 2:
			d[0] = this.graph_top + 1;
			d[1] = this.graph_top + this.graph_height * (1 - c) - 1;
			break;
		case 3:
			f = c - this.minimum_value / this.spread;
			if (c >= this.zero) {
				d[0] = this.graph_top + this.graph_height
						* (1 - (f - this.zero)) + 1;
				d[1] = this.graph_top + this.graph_height * (1 - this.zero) - 1
			} else {
				d[0] = this.graph_top + this.graph_height
						* (1 - (f - this.zero)) + 1;
				d[1] = this.graph_top + this.graph_height * (1 - this.zero) - 1
			}
			break;
		default:
			d[0] = 0.0;
			d[1] = 0.0
		}
	}
});
Bluff.Bar = new JS.Class(
		Bluff.Base,
		{
			bar_spacing : 0.9,
			draw : function() {
				this.center_labels_over_point = (Bluff.keys(this.labels).length > this._5);
				this.callSuper();
				if (!this._a)
					return;
				this._1P()
			},
			_1P : function() {
				this._8 = this._6 / (this._5 * this._2.length);
				var n = (this._8 * (1 - this.bar_spacing)) / 2;
				this._0.stroke_opacity = 0.0;
				var m = new Bluff.BarConversion();
				m.graph_height = this._3;
				m.graph_top = this._7;
				if (this.minimum_value >= 0) {
					m.mode = 1
				} else {
					if (this.maximum_value <= 0) {
						m.mode = 2
					} else {
						m.mode = 3;
						m.spread = this._i;
						m.minimum_value = this.minimum_value;
						m.zero = -this.minimum_value / this._i
					}
				}
				Bluff
						.each(
								this._9,
								function(j, k) {
									var l = this._2[k][this.klass.DATA_VALUES_INDEX];
									Bluff
											.each(
													j[this.klass.DATA_VALUES_INDEX],
													function(c, d) {
														var f = this._1
																+ (this._8 * (k
																		+ d + ((this._2.length - 1) * d)))
																+ n;
														var g = f
																+ this._8
																* this.bar_spacing;
														var h = [];
														m.getLeftYRightYscaled(
																c, h);
														this._0.fill = j[this.klass.DATA_COLOR_INDEX];
														this._0.rectangle(f,
																h[0], g, h[1]);
														this
																._F(
																		f,
																		h[0],
																		g - f,
																		h[1]
																				- h[0],
																		j[this.klass.DATA_LABEL_INDEX],
																		j[this.klass.DATA_COLOR_INDEX],
																		l[d]);
														var i = this._1
																+ (this._2.length
																		* this._8 * d)
																+ (this._2.length
																		* this._8 / 2.0);
														this
																._e(
																		i
																				- (this.center_labels_over_point ? this._8 / 2.0
																						: 0.0),
																		d)
													}, this)
								}, this);
				if (this.center_labels_over_point)
					this._e(this._l, this._5)
			}
		});
Bluff.Line = new JS.Class(
		Bluff.Base,
		{
			baseline_value : null,
			baseline_color : null,
			line_width : null,
			dot_radius : null,
			hide_dots : null,
			hide_lines : null,
			initialize : function(c) {
				if (arguments.length > 3)
					throw 'Wrong number of arguments';
				if (arguments.length === 1
						|| (typeof arguments[1] !== 'number' && typeof arguments[1] !== 'string'))
					this.callSuper(c, null);
				else
					this.callSuper();
				this.hide_dots = this.hide_lines = false;
				this.baseline_color = 'red';
				this.baseline_value = null
			},
			draw : function() {
				this.callSuper();
				if (!this._a)
					return;
				this.x_increment = (this._5 > 1) ? (this._6 / (this._5 - 1))
						: this._6;
				var m;
				if (this._T !== undefined) {
					m = this._7 + (this._3 - this._T * this._3);
					this._0.push();
					this._0.stroke = this.baseline_color;
					this._0.fill_opacity = 0.0;
					this._0.stroke_width = 3.0;
					this._0.line(this._1, m, this._1 + this._6, m);
					this._0.pop()
				}
				Bluff
						.each(
								this._9,
								function(i, j) {
									var k = null, l = null;
									var n = this._2[j][this.klass.DATA_VALUES_INDEX];
									this._1Q = this._1R(i);
									Bluff
											.each(
													i[this.klass.DATA_VALUES_INDEX],
													function(c, d) {
														var f = this._1
																+ (this.x_increment * d);
														if (typeof c !== 'number')
															return;
														this._e(f, d);
														var g = this._7
																+ (this._3 - c
																		* this._3);
														this._0.stroke = i[this.klass.DATA_COLOR_INDEX];
														this._0.fill = i[this.klass.DATA_COLOR_INDEX];
														this._0.stroke_opacity = 1.0;
														this._0.stroke_width = this.line_width
																|| this
																		._R(
																				this._j
																						/ (this._9[0][this.klass.DATA_VALUES_INDEX].length * 6),
																				3.0);
														var h = this.dot_radius
																|| this
																		._R(
																				this._j
																						/ (this._9[0][this.klass.DATA_VALUES_INDEX].length * 2),
																				7.0);
														if (!this.hide_lines
																&& k !== null
																&& l !== null) {
															this._0.line(k, l,
																	f, g)
														} else if (this._1Q) {
															this._0
																	.circle(
																			f,
																			g,
																			f
																					- h,
																			g)
														}
														if (!this.hide_dots)
															this._0
																	.circle(
																			f,
																			g,
																			f
																					- h,
																			g);
														this
																._F(
																		f - h,
																		g - h,
																		2 * h,
																		2 * h,
																		i[this.klass.DATA_LABEL_INDEX],
																		i[this.klass.DATA_COLOR_INDEX],
																		n[d]);
														k = f;
														l = g
													}, this)
								}, this)
			},
			_14 : function() {
				this.maximum_value = Math.max(this.maximum_value,
						this.baseline_value);
				this.callSuper();
				if (this.baseline_value !== null)
					this._T = this.baseline_value / this.maximum_value
			},
			_1R : function(d) {
				var f = 0;
				Bluff.each(d[this.klass.DATA_VALUES_INDEX], function(c) {
					if (c !== undefined)
						f += 1
				});
				return f === 1
			}
		});
Bluff.Dot = new JS.Class(Bluff.Base, {
	draw : function() {
		this.has_left_labels = true;
		this.callSuper();
		if (!this._a)
			return;
		var k = 1.0;
		this._G = this._3 / this._5;
		this._18 = this._G * k / this._9.length;
		this._0.stroke_opacity = 0.0;
		var l = Bluff.array_new(this._5, 0), n = Bluff.array_new(this._5,
				this._1), m = (this._G * (1 - k)) / 2;
		Bluff.each(this._9, function(i, j) {
			Bluff.each(i[this.klass.DATA_VALUES_INDEX], function(c, d) {
				var f = this._1 + (c * this._6) - Math.round(this._18 / 6.0);
				var g = this._7 + (this._G * d) + m
						+ Math.round(this._18 / 2.0);
				if (j === 0) {
					this._0.stroke = this.marker_color;
					this._0.stroke_width = 1.0;
					this._0.opacity = 0.1;
					this._0.line(this._1, g, this._1 + this._6, g)
				}
				this._0.fill = i[this.klass.DATA_COLOR_INDEX];
				this._0.stroke = 'transparent';
				this._0.circle(f, g, f + Math.round(this._18 / 3.0), g);
				var h = this._7 + (this._G * d + this._G / 2) + m;
				this._e(h, d)
			}, this)
		}, this)
	},
	_N : function() {
		if (this.hide_line_markers)
			return;
		this._0.stroke_antialias = false;
		this._0.stroke_width = 1;
		var c = 5;
		var d = this._17(this.maximum_value / c);
		for ( var f = 0; f <= c; f++) {
			var g = (this._l - this._1) / c, h = this._l - (g * f) - 1, i = f
					- c, j = Math.abs(i) * d;
			this._0.stroke = this.marker_color;
			this._0
					.line(h, this._g, h, this._g + 0.5
							* this.klass.LABEL_MARGIN);
			if (!this.hide_line_numbers) {
				this._0.fill = this.font_color;
				if (this.font)
					this._0.font = this.font;
				this._0.stroke = 'transparent';
				this._0.pointsize = this._d(this.marker_font_size);
				this._0.gravity = 'center';
				this._0.annotate_scaled(0, 0, h, this._g
						+ (this.klass.LABEL_MARGIN * 2.0), j, this._b)
			}
			this._0.stroke_antialias = true
		}
	},
	_e : function(c, d) {
		if (this.labels[d] && !this._q[d]) {
			this._0.fill = this.font_color;
			if (this.font)
				this._0.font = this.font;
			this._0.stroke = 'transparent';
			this._0.font_weight = 'normal';
			this._0.pointsize = this._d(this.marker_font_size);
			this._0.gravity = 'east';
			this._0.annotate_scaled(1, 1, this._1 - this.klass.LABEL_MARGIN
					* 2.0, c, this.labels[d], this._b);
			this._q[d] = true
		}
	}
});
Bluff.Net = new JS.Class(
		Bluff.Base,
		{
			hide_dots : null,
			line_width : null,
			dot_radius : null,
			initialize : function() {
				this.callSuper();
				this.hide_dots = false;
				this.hide_line_numbers = true
			},
			draw : function() {
				this.callSuper();
				if (!this._a)
					return;
				this._w = this._3 / 2.0;
				this._x = this._1 + (this._6 / 2.0);
				this._y = this._7 + (this._3 / 2.0) - 10;
				this._S = this._6 / (this._5 - 1);
				var s = this.dot_radius
						|| this
								._R(
										this._j
												/ (this._9[0][this.klass.DATA_VALUES_INDEX].length * 2.5),
										7.0);
				this._0.stroke_opacity = 1.0;
				this._0.stroke_width = this.line_width
						|| this
								._R(
										this._j
												/ (this._9[0][this.klass.DATA_VALUES_INDEX].length * 4),
										3.0);
				var r;
				if (this._T !== undefined) {
					r = this._7 + (this._3 - this._T * this._3);
					this._0.push();
					this._0.stroke_color = this.baseline_color;
					this._0.fill_opacity = 0.0;
					this._0.stroke_width = 5;
					this._0.line(this._1, r, this._1 + this._6, r);
					this._0.pop()
				}
				Bluff
						.each(
								this._9,
								function(o) {
									var p = null, q = null;
									Bluff
											.each(
													o[this.klass.DATA_VALUES_INDEX],
													function(c, d) {
														if (c === undefined)
															return;
														var f = d * Math.PI * 2
																/ this._5, g = c
																* this._w, h = this._x
																+ Math.sin(f)
																* g, i = this._y
																- Math.cos(f)
																* g, j = (d + 1 < o[this.klass.DATA_VALUES_INDEX].length) ? d + 1
																: 0, k = j
																* Math.PI * 2
																/ this._5, l = o[this.klass.DATA_VALUES_INDEX][j]
																* this._w, n = this._x
																+ Math.sin(k)
																* l, m = this._y
																- Math.cos(k)
																* l;
														this._0.stroke = o[this.klass.DATA_COLOR_INDEX];
														this._0.fill = o[this.klass.DATA_COLOR_INDEX];
														this._0
																.line(h, i, n,
																		m);
														if (!this.hide_dots)
															this._0
																	.circle(
																			h,
																			i,
																			h
																					- s,
																			i)
													}, this)
								}, this)
			},
			_N : function() {
				if (this.hide_line_markers)
					return;
				this._w = this._3 / 2.0;
				this._x = this._1 + (this._6 / 2.0);
				this._y = this._7 + (this._3 / 2.0) - 10;
				var c, d;
				for ( var f = 0, g = this._5; f < g; f++) {
					c = f * Math.PI * 2 / this._5;
					this._0.stroke = this.marker_color;
					this._0.stroke_width = 1;
					this._0.line(this._x, this._y, this._x + Math.sin(c)
							* this._w, this._y - Math.cos(c) * this._w);
					d = this.labels[f] ? this.labels[f] : '000';
					this._e(this._x, this._y, c * 360 / (2 * Math.PI), this._w,
							d)
				}
			},
			_e : function(c, d, f, g, h) {
				var i = 1.1, j = c, k = d, l = f * Math.PI / 180, n = j
						+ (g * i * Math.sin(l)), m = k - (g * i * Math.cos(l));
				this._0.fill = this.marker_color;
				if (this.font)
					this._0.font = this.font;
				this._0.pointsize = this._d(20);
				this._0.stroke = 'transparent';
				this._0.font_weight = 'bold';
				this._0.gravity = 'center';
				this._0.annotate_scaled(0, 0, n, m, h, this._b)
			}
		});
Bluff.Pie = new JS.Class(
		Bluff.Base,
		{
			extend : {
				TEXT_OFFSET_PERCENTAGE : 0.08
			},
			zero_degreee : null,
			hide_labels_less_than : null,
			initialize_ivars : function() {
				this.callSuper();
				this.zero_degree = 0.0;
				this.hide_labels_less_than = 0.0
			},
			draw : function() {
				this.hide_line_markers = true;
				this.callSuper();
				if (!this._a)
					return;
				var j = this._3, k = (Math.min(this._6, this._3) / 2.0) * 0.8, l = this._1
						+ (this._6 - j) / 2.0, n = this._1 + (this._6 / 2.0), m = this._7
						+ (this._3 / 2.0) - 10, o = this._1S(), p = this.zero_degree, q = this.klass.DATA_VALUES_INDEX;
				if (this.sort)
					this._2.sort(function(a, b) {
						return a[q][0] - b[q][0]
					});
				Bluff
						.each(
								this._2,
								function(c, d) {
									if (c[this.klass.DATA_VALUES_INDEX][0] > 0) {
										this._0.fill = c[this.klass.DATA_COLOR_INDEX];
										var f = (c[this.klass.DATA_VALUES_INDEX][0] / o) * 360;
										this._0.circle(n, m, n + k, m, p, p + f
												+ 0.5);
										var g = p + ((p + f) - p) / 2, h = Math
												.round((c[this.klass.DATA_VALUES_INDEX][0] / o) * 100.0), i;
										if (h >= this.hide_labels_less_than) {
											i = this
													._Q(c[this.klass.DATA_VALUES_INDEX][0]);
											this
													._e(
															n,
															m,
															g,
															k
																	+ (k * this.klass.TEXT_OFFSET_PERCENTAGE),
															i)
										}
										p += f
									}
								}, this)
			},
			_e : function(c, d, f, g, h) {
				var i = 20.0, j = c, k = d, l = g + i, n = l * 0.15, m = j
						+ ((l + n) * Math.cos(f * Math.PI / 180)), o = k
						+ (l * Math.sin(f * Math.PI / 180));
				this._0.fill = this.font_color;
				if (this.font)
					this._0.font = this.font;
				this._0.pointsize = this._d(this.marker_font_size);
				this._0.font_weight = 'bold';
				this._0.gravity = 'center';
				this._0.annotate_scaled(0, 0, m, o, h, this._b)
			},
			_1S : function() {
				var d = 0;
				Bluff.each(this._2, function(c) {
					d += c[this.klass.DATA_VALUES_INDEX][0]
				}, this);
				return d
			}
		});
Bluff.SideBar = new JS.Class(Bluff.Base, {
	bar_spacing : 0.9,
	draw : function() {
		this.has_left_labels = true;
		this.callSuper();
		if (!this._a)
			return;
		this._H = this._3 / this._5;
		this._8 = this._H * this.bar_spacing / this._9.length;
		this._0.stroke_opacity = 0.0;
		var q = Bluff.array_new(this._5, 0), s = Bluff.array_new(this._5,
				this._1), r = (this._H * (1 - this.bar_spacing)) / 2;
		Bluff.each(this._9, function(m, o) {
			var p = this._2[o][this.klass.DATA_VALUES_INDEX];
			Bluff.each(m[this.klass.DATA_VALUES_INDEX], function(c, d) {
				var f = this._1 + (this._6 - c * this._6 - q[d]), g = this._1
						+ this._6 - q[d], h = g - f, i = s[d] - 1, j = this._7
						+ (this._H * d) + (this._8 * o) + r, k = i + h, l = j
						+ this._8;
				q[d] += (c * this._6);
				this._0.stroke = 'transparent';
				this._0.fill = m[this.klass.DATA_COLOR_INDEX];
				this._0.rectangle(i, j, k, l);
				this._F(i, j, k - i, l - j, m[this.klass.DATA_LABEL_INDEX],
						m[this.klass.DATA_COLOR_INDEX], p[d]);
				var n = this._7 + (this._H * d + this._H / 2);
				this._e(n, d)
			}, this)
		}, this)
	},
	_N : function() {
		if (this.hide_line_markers)
			return;
		this._0.stroke_antialias = false;
		this._0.stroke_width = 1;
		var c = 5;
		var d = this._17(this.maximum_value / c), f, g, h, i;
		for ( var j = 0; j <= c; j++) {
			f = (this._l - this._1) / c;
			g = this._l - (f * j) - 1;
			h = j - c;
			i = Math.abs(h) * d;
			this._0.stroke = this.marker_color;
			this._0.line(g, this._g, g, this._7);
			if (!this.hide_line_numbers) {
				this._0.fill = this.font_color;
				if (this.font)
					this._0.font = this.font;
				this._0.stroke = 'transparent';
				this._0.pointsize = this._d(this.marker_font_size);
				this._0.gravity = 'center';
				this._0.annotate_scaled(0, 0, g, this._g
						+ (this.klass.LABEL_MARGIN * 2.0), this._Q(i), this._b)
			}
		}
	},
	_e : function(c, d) {
		if (this.labels[d] && !this._q[d]) {
			this._0.fill = this.font_color;
			if (this.font)
				this._0.font = this.font;
			this._0.stroke = 'transparent';
			this._0.font_weight = 'normal';
			this._0.pointsize = this._d(this.marker_font_size);
			this._0.gravity = 'east';
			this._0.annotate_scaled(1, 1, this._1 - this.klass.LABEL_MARGIN
					* 2.0, c, this.labels[d], this._b);
			this._q[d] = true
		}
	}
});
Bluff.Spider = new JS.Class(
		Bluff.Base,
		{
			hide_text : null,
			hide_axes : null,
			transparent_background : null,
			initialize : function(c, d, f) {
				this.callSuper(c, f);
				this._1T = d;
				this.hide_legend = true
			},
			draw : function() {
				this.hide_line_markers = true;
				this.callSuper();
				if (!this._a)
					return;
				var c = this._3, d = this._3 / 2.0, f = this._1 + (this._6 - c)
						/ 2.0, g = this._1 + (this._6 / 2.0), h = this._7
						+ (this._3 / 2.0) - 25;
				this._1U = d / this._1T;
				var i = this._1V(), j = 0.0, k = (2 * Math.PI) / this._2.length, l = 0.0;
				if (!this.hide_axes)
					this._1W(g, h, d, k);
				this._1X(g, h, k)
			},
			_1n : function(c) {
				return c * this._1U
			},
			_e : function(c, d, f, g, h) {
				var i = 50, j = c, k = d + 0, l = j + ((g + i) * Math.cos(f)), n = k
						+ ((g + i) * Math.sin(f));
				this._0.fill = this.marker_color;
				if (this.font)
					this._0.font = this.font;
				this._0.pointsize = this._d(this.legend_font_size);
				this._0.stroke = 'transparent';
				this._0.font_weight = 'bold';
				this._0.gravity = 'center';
				this._0.annotate_scaled(0, 0, l, n, h, this._b)
			},
			_1W : function(g, h, i, j, k) {
				if (this.hide_axes)
					return;
				var l = 0.0;
				Bluff.each(this._2, function(c) {
					this._0.stroke = k || c[this.klass.DATA_COLOR_INDEX];
					this._0.stroke_width = 5.0;
					var d = i * Math.cos(l);
					var f = i * Math.sin(l);
					this._0.line(g, h, g + d, h + f);
					if (!this.hide_text)
						this._e(g, h, l, i, c[this.klass.DATA_LABEL_INDEX]);
					l += j
				}, this)
			},
			_1X : function(d, f, g, h) {
				var i = [], j = 0.0;
				Bluff.each(this._2, function(c) {
					i.push(d + this._1n(c[this.klass.DATA_VALUES_INDEX][0])
							* Math.cos(j));
					i.push(f + this._1n(c[this.klass.DATA_VALUES_INDEX][0])
							* Math.sin(j));
					j += g
				}, this);
				this._0.stroke_width = 1.0;
				this._0.stroke = h || this.marker_color;
				this._0.fill = h || this.marker_color;
				this._0.fill_opacity = 0.4;
				this._0.polyline(i)
			},
			_1V : function() {
				var d = 0.0;
				Bluff.each(this._2, function(c) {
					d += c[this.klass.DATA_VALUES_INDEX][0]
				}, this);
				return d
			}
		});
Bluff.Base.StackedMixin = new JS.Module( {
	_19 : function() {
		var g = {};
		Bluff.each(this._2, function(f) {
			Bluff.each(f[this.klass.DATA_VALUES_INDEX], function(c, d) {
				if (!g[d])
					g[d] = 0.0;
				g[d] += c
			}, this)
		}, this);
		for ( var h in g) {
			if (g[h] > this.maximum_value)
				this.maximum_value = g[h]
		}
		this.minimum_value = 0
	}
});
Bluff.StackedArea = new JS.Class(Bluff.Base, {
	include : Bluff.Base.StackedMixin,
	last_series_goes_on_bottom : null,
	draw : function() {
		this._19();
		this.callSuper();
		if (!this._a)
			return;
		this._S = this._6 / (this._5 - 1);
		this._0.stroke = 'transparent';
		var n = Bluff.array_new(this._5, 0);
		var m = null;
		var o = this.last_series_goes_on_bottom ? 'reverse_each' : 'each';
		Bluff[o](this._9, function(h) {
			var i = m;
			m = [];
			Bluff.each(h[this.klass.DATA_VALUES_INDEX], function(c, d) {
				var f = this._1 + (this._S * d);
				var g = this._7 + (this._3 - c * this._3 - n[d]);
				n[d] += (c * this._3);
				m.push(f);
				m.push(g);
				this._e(f, d)
			}, this);
			var j, k, l;
			if (i) {
				j = Bluff.array(m);
				for (k = i.length / 2 - 1; k >= 0; k--) {
					j.push(i[2 * k]);
					j.push(i[2 * k + 1])
				}
				j.push(m[0]);
				j.push(m[1])
			} else {
				j = Bluff.array(m);
				j.push(this._l);
				j.push(this._g - 1);
				j.push(this._1);
				j.push(this._g - 1);
				j.push(m[0]);
				j.push(m[1])
			}
			this._0.fill = h[this.klass.DATA_COLOR_INDEX];
			this._0.polyline(j)
		}, this)
	}
});
Bluff.StackedBar = new JS.Class(Bluff.Base, {
	include : Bluff.Base.StackedMixin,
	bar_spacing : 0.9,
	draw : function() {
		this._19();
		this.callSuper();
		if (!this._a)
			return;
		this._8 = this._6 / this._5;
		var m = (this._8 * (1 - this.bar_spacing)) / 2;
		this._0.stroke_opacity = 0.0;
		var o = Bluff.array_new(this._5, 0);
		Bluff.each(this._9, function(k, l) {
			var n = this._2[l][this.klass.DATA_VALUES_INDEX];
			Bluff.each(k[this.klass.DATA_VALUES_INDEX], function(c, d) {
				var f = this._1 + (this._8 * d)
						+ (this._8 * this.bar_spacing / 2.0);
				this._e(f, d);
				if (c == 0)
					return;
				var g = this._1 + (this._8 * d) + m;
				var h = this._7 + (this._3 - c * this._3 - o[d]) + 1;
				var i = g + this._8 * this.bar_spacing;
				var j = this._7 + this._3 - o[d] - 1;
				o[d] += (c * this._3);
				this._0.fill = k[this.klass.DATA_COLOR_INDEX];
				this._0.rectangle(g, h, i, j);
				this._F(g, h, i - g, j - h, k[this.klass.DATA_LABEL_INDEX],
						k[this.klass.DATA_COLOR_INDEX], n[d])
			}, this)
		}, this)
	}
});
Bluff.AccumulatorBar = new JS.Class(Bluff.StackedBar, {
	draw : function() {
		if (this._2.length !== 1)
			throw 'Incorrect number of datasets';
		var g = [], h = 0, i = [];
		Bluff.each(this._2[0][this.klass.DATA_VALUES_INDEX], function(d) {
			var f = -Infinity;
			Bluff.each(i, function(c) {
				f = Math.max(f, c)
			});
			i.push((h > 0) ? (d + f) : d);
			g.push(i[h] - d);
			h += 1
		}, this);
		this.data("Accumulator", g);
		this.callSuper()
	}
});
Bluff.SideStackedBar = new JS.Class(Bluff.SideBar, {
	include : Bluff.Base.StackedMixin,
	bar_spacing : 0.9,
	draw : function() {
		this.has_left_labels = true;
		this._19();
		this.callSuper();
		if (!this._a)
			return;
		this._8 = this._3 / this._5;
		var q = Bluff.array_new(this._5, 0), s = Bluff.array_new(this._5,
				this._1), r = (this._8 * (1 - this.bar_spacing)) / 2;
		Bluff.each(this._9, function(m, o) {
			this._0.fill = m[this.klass.DATA_COLOR_INDEX];
			var p = this._2[o][this.klass.DATA_VALUES_INDEX];
			Bluff.each(m[this.klass.DATA_VALUES_INDEX], function(c, d) {
				var f = this._1 + (this._6 - c * this._6 - q[d]) + 1;
				var g = this._1 + this._6 - q[d] - 1;
				var h = g - f;
				var i = s[d], j = this._7 + (this._8 * d) + r, k = i + h, l = j
						+ this._8 * this.bar_spacing;
				s[d] += h;
				q[d] += (c * this._6 - 2);
				this._0.rectangle(i, j, k, l);
				this._F(i, j, k - i, l - j, m[this.klass.DATA_LABEL_INDEX],
						m[this.klass.DATA_COLOR_INDEX], p[d]);
				var n = this._7 + (this._8 * d)
						+ (this._8 * this.bar_spacing / 2.0);
				this._e(n, d)
			}, this)
		}, this)
	},
	_1f : function(c, d) {
		d = d || 0;
		return this._1m(c, d) > this.maximum_value
	},
	_1m : function(d, f) {
		var g = 0;
		Bluff.each(this._2, function(c) {
			g += c[this.klass.DATA_VALUES_INDEX][f]
		}, this);
		return g
	}
});
Bluff.Mini.Legend = new JS.Module( {
	hide_mini_legend : false,
	_1a : function() {
		if (this.hide_mini_legend)
			return;
		this._E = Bluff.map(this._2, function(c) {
			return c[this.klass.DATA_LABEL_INDEX]
		}, this);
		var d = this._d(this._2.length * this._1o() + this.top_margin
				+ this.bottom_margin);
		this._1Y = this._M;
		this._1Z = this._c;
		switch (this.legend_position) {
		case 'right':
			this._p = Math.max(this._p, d);
			this._j += this._20() + this.left_margin;
			break;
		default:
			this._p += d;
			break
		}
		this._Y()
	},
	_1o : function() {
		return this._P(this.legend_font_size) * 1.7
	},
	_20 : function() {
		var d = 0;
		Bluff.each(this._E, function(c) {
			d = Math.max(this._D(this.legend_font_size, c), d)
		}, this);
		return this._d(d + 40 * 1.7)
	},
	_1b : function() {
		if (this.hide_mini_legend)
			return;
		var f = 40.0, g = 10.0, h = 100.0, i = 40.0;
		if (this.font)
			this._0.font = this.font;
		this._0.pointsize = this.legend_font_size;
		var j, k;
		switch (this.legend_position) {
		case 'right':
			j = this._1Z + this.left_margin;
			k = this.top_margin + i;
			break;
		default:
			j = h, k = this._1Y + i;
			break
		}
		this._v(function() {
			this._0.line(0.0, k, this._c, k)
		});
		Bluff.each(this._E, function(c, d) {
			this._0.fill = this.font_color;
			if (this.font)
				this._0.font = this.font;
			this._0.pointsize = this._d(this.legend_font_size);
			this._0.stroke = 'transparent';
			this._0.font_weight = 'normal';
			this._0.gravity = 'west';
			this._0.annotate_scaled(this._c, 1.0, j + (f * 1.7), k,
					this._21(c), this._b);
			this._0.stroke = 'transparent';
			this._0.fill = this._2[d][this.klass.DATA_COLOR_INDEX];
			this._0.rectangle(j, k - f / 2.0, j + f, k + f / 2.0);
			k += this._1o()
		}, this);
		this._u = 0
	},
	_21 : function(c) {
		var d = String(c);
		while (this._D(this._d(this.legend_font_size), d) > (this._j
				- this.legend_left_margin - this.right_margin)
				&& (d.length > 1))
			d = d.substr(0, d.length - 1);
		return d + (d.length < String(c).length ? "..." : '')
	}
});
Bluff.Mini.Bar = new JS.Class(Bluff.Bar, {
	include : Bluff.Mini.Legend,
	initialize_ivars : function() {
		this.callSuper();
		this.hide_legend = true;
		this.hide_title = true;
		this.hide_line_numbers = true;
		this.marker_font_size = 50.0;
		this.minimum_value = 0.0;
		this.maximum_value = 0.0;
		this.legend_font_size = 60.0
	},
	draw : function() {
		this._1a();
		this.callSuper();
		this._1b()
	}
});
Bluff.Mini.Pie = new JS.Class(Bluff.Pie, {
	include : Bluff.Mini.Legend,
	initialize_ivars : function() {
		this.callSuper();
		this.hide_legend = true;
		this.hide_title = true;
		this.hide_line_numbers = true;
		this.marker_font_size = 60.0;
		this.legend_font_size = 60.0
	},
	draw : function() {
		this._1a();
		this.callSuper();
		this._1b()
	}
});
Bluff.Mini.SideBar = new JS.Class(Bluff.SideBar, {
	include : Bluff.Mini.Legend,
	initialize_ivars : function() {
		this.callSuper();
		this.hide_legend = true;
		this.hide_title = true;
		this.hide_line_numbers = true;
		this.marker_font_size = 50.0;
		this.legend_font_size = 50.0
	},
	draw : function() {
		this._1a();
		this.callSuper();
		this._1b()
	}
});
Bluff.Renderer = new JS.Class( {
	extend : {
		WRAPPER_CLASS : 'bluff-wrapper',
		TEXT_CLASS : 'bluff-text',
		TARGET_CLASS : 'bluff-tooltip-target'
	},
	font : 'Arial, Helvetica, Verdana, sans-serif',
	gravity : 'north',
	initialize : function(c) {
		this._m = document.getElementById(c);
		this._4 = this._m.getContext('2d')
	},
	scale : function(c, d) {
		this._f = c;
		this._h = d || c
	},
	caps_height : function(c) {
		var d = this._U(c, 'X'), f = this._I(d).height;
		this._V(d);
		return f
	},
	text_width : function(c, d) {
		var f = this._U(c, d);
		var g = this._I(f).width;
		this._V(f);
		return g
	},
	get_type_metrics : function(c) {
		var d = this._U(this.pointsize, c);
		document.body.appendChild(d);
		var f = this._I(d);
		this._V(d);
		return f
	},
	clear : function(c, d) {
		this._m.width = c;
		this._m.height = d;
		this._4.clearRect(0, 0, c, d);
		var f = this._1p(), g = f.childNodes, h = g.length;
		f.style.width = c + 'px';
		f.style.height = d + 'px';
		while (h--) {
			if (g[h].tagName.toLowerCase() !== 'canvas')
				this._V(g[h])
		}
	},
	push : function() {
		this._4.save()
	},
	pop : function() {
		this._4.restore()
	},
	render_gradiated_background : function(c, d, f, g) {
		this.clear(c, d);
		var h = this._4.createLinearGradient(0, 0, 0, d);
		h.addColorStop(0, f);
		h.addColorStop(1, g);
		this._4.fillStyle = h;
		this._4.fillRect(0, 0, c, d)
	},
	render_solid_background : function(c, d, f) {
		this.clear(c, d);
		this._4.fillStyle = f;
		this._4.fillRect(0, 0, c, d)
	},
	annotate_scaled : function(c, d, f, g, h, i) {
		var j = (c * i) >= 1 ? (c * i) : 1;
		var k = (d * i) >= 1 ? (d * i) : 1;
		var h = this._U(this.pointsize, h);
		h.style.color = this.fill;
		h.style.fontWeight = this.font_weight;
		h.style.textAlign = 'center';
		h.style.left = (this._f * f + this._22(h, j)) + 'px';
		h.style.top = (this._h * g + this._23(h, k)) + 'px'
	},
	tooltip : function(d, f, g, h, i, j, k) {
		if (g < 0)
			d += g;
		if (h < 0)
			f += h;
		var l = this._m.parentNode, n = document.createElement('div');
		n.className = this.klass.TARGET_CLASS;
		n.style.position = 'absolute';
		n.style.left = (this._f * d - 3) + 'px';
		n.style.top = (this._h * f - 3) + 'px';
		n.style.width = (this._f * Math.abs(g) + 5) + 'px';
		n.style.height = (this._h * Math.abs(h) + 5) + 'px';
		n.style.fontSize = 0;
		n.style.overflow = 'hidden';
		Bluff.Event.observe(n, 'mouseover', function(c) {
			Bluff.Tooltip.show(i, j, k)
		});
		Bluff.Event.observe(n, 'mouseout', function(c) {
			Bluff.Tooltip.hide()
		});
		l.appendChild(n)
	},
	circle : function(c, d, f, g, h, i) {
		var j = Math.sqrt(Math.pow(f - c, 2) + Math.pow(g - d, 2));
		var k = 0, l = 2 * Math.PI;
		this._4.fillStyle = this.fill;
		this._4.beginPath();
		if (h !== undefined && i !== undefined
				&& Math.abs(Math.floor(i - h)) !== 360) {
			k = h * Math.PI / 180;
			l = i * Math.PI / 180;
			this._4.moveTo(this._f * (c + j * Math.cos(l)), this._h
					* (d + j * Math.sin(l)));
			this._4.lineTo(this._f * c, this._h * d);
			this._4.lineTo(this._f * (c + j * Math.cos(k)), this._h
					* (d + j * Math.sin(k)))
		}
		this._4.arc(this._f * c, this._h * d, this._f * j, k, l, false);
		this._4.fill()
	},
	line : function(c, d, f, g) {
		this._4.strokeStyle = this.stroke;
		this._4.lineWidth = this.stroke_width;
		this._4.beginPath();
		this._4.moveTo(this._f * c, this._h * d);
		this._4.lineTo(this._f * f, this._h * g);
		this._4.stroke()
	},
	polyline : function(c) {
		this._4.fillStyle = this.fill;
		this._4.globalAlpha = this.fill_opacity || 1;
		try {
			this._4.strokeStyle = this.stroke
		} catch (e) {
		}
		var d = c.shift(), f = c.shift();
		this._4.beginPath();
		this._4.moveTo(this._f * d, this._h * f);
		while (c.length > 0) {
			d = c.shift();
			f = c.shift();
			this._4.lineTo(this._f * d, this._h * f)
		}
		this._4.fill()
	},
	rectangle : function(c, d, f, g) {
		var h;
		if (c > f) {
			h = c;
			c = f;
			f = h
		}
		if (d > g) {
			h = d;
			d = g;
			g = h
		}
		try {
			this._4.fillStyle = this.fill;
			this._4.fillRect(this._f * c, this._h * d, this._f * (f - c),
					this._h * (g - d))
		} catch (e) {
		}
		try {
			this._4.strokeStyle = this.stroke;
			if (this.stroke !== 'transparent')
				this._4.strokeRect(this._f * c, this._h * d, this._f * (f - c),
						this._h * (g - d))
		} catch (e) {
		}
	},
	_22 : function(c, d) {
		var f = this._I(c).width;
		switch (this.gravity) {
		case 'west':
			return 0;
		case 'east':
			return d - f;
		case 'north':
		case 'south':
		case 'center':
			return (d - f) / 2
		}
	},
	_23 : function(c, d) {
		var f = this._I(c).height;
		switch (this.gravity) {
		case 'north':
			return 0;
		case 'south':
			return d - f;
		case 'west':
		case 'east':
		case 'center':
			return (d - f) / 2
		}
	},
	_1p : function() {
		var c = this._m.parentNode;
		if (c.className === this.klass.WRAPPER_CLASS)
			return c;
		c = document.createElement('div');
		c.className = this.klass.WRAPPER_CLASS;
		c.style.position = 'relative';
		c.style.border = 'none';
		c.style.padding = '0 0 0 0';
		this._m.parentNode.insertBefore(c, this._m);
		c.appendChild(this._m);
		return c
	},
	_U : function(c, d) {
		var f = this._24(d);
		f.style.fontFamily = this.font;
		f.style.fontSize = (typeof c === 'number') ? c + 'px' : c;
		return f
	},
	_24 : function(c) {
		var d = document.createElement('div');
		d.className = this.klass.TEXT_CLASS;
		d.style.position = 'absolute';
		d.appendChild(document.createTextNode(c));
		this._1p().appendChild(d);
		return d
	},
	_V : function(c) {
		c.parentNode.removeChild(c);
		if (c.className === this.klass.TARGET_CLASS)
			Bluff.Event.stopObserving(c)
	},
	_I : function(c) {
		var d = c.style.display;
		return (d && d !== 'none') ? {
			width : c.offsetWidth,
			height : c.offsetHeight
		} : {
			width : c.clientWidth,
			height : c.clientHeight
		}
	}
});
Bluff.Event = {
	_W : [],
	_1q : (window.attachEvent && navigator.userAgent.indexOf('Opera') === -1),
	observe : function(d, f, g, h) {
		var i = Bluff.map(this._1r(d, f), function(c) {
			return c._25
		});
		if (Bluff.index(i, g) !== -1)
			return;
		var j = function(c) {
			g.call(h || null, d, Bluff.Event._26(c))
		};
		this._W.push( {
			_X : d,
			_1c : f,
			_25 : g,
			_1s : j
		});
		if (d.addEventListener)
			d.addEventListener(f, j, false);
		else
			d.attachEvent('on' + f, j)
	},
	stopObserving : function(d) {
		var f = d ? this._1r(d) : this._W;
		Bluff.each(f, function(c) {
			if (c._X.removeEventListener)
				c._X.removeEventListener(c._1c, c._1s, false);
			else
				c._X.detachEvent('on' + c._1c, c._1s)
		})
	},
	_1r : function(d, f) {
		var g = [];
		Bluff.each(this._W, function(c) {
			if (d && c._X !== d)
				return;
			if (f && c._1c !== f)
				return;
			g.push(c)
		});
		return g
	},
	_26 : function(c) {
		if (!this._1q)
			return c;
		if (!c)
			return false;
		if (c._27)
			return c;
		c._27 = true;
		var d = this._28(c);
		c.target = c.srcElement;
		c.pageX = d.x;
		c.pageY = d.y;
		return c
	},
	_28 : function(c) {
		var d = document.documentElement, f = document.body || {
			scrollLeft : 0,
			scrollTop : 0
		};
		return {
			x : c.pageX
					|| (c.clientX + (d.scrollLeft || f.scrollLeft) - (d.clientLeft || 0)),
			y : c.pageY
					|| (c.clientY + (d.scrollTop || f.scrollTop) - (d.clientTop || 0))
		}
	}
};
if (Bluff.Event._1q)
	window.attachEvent('onunload', function() {
		Bluff.Event.stopObserving();
		Bluff.Event._W = null
	});
if (navigator.userAgent.indexOf('AppleWebKit/') > -1)
	window.addEventListener('unload', function() {
	}, false);
Bluff.Tooltip = new JS.Singleton( {
	LEFT_OFFSET : 20,
	TOP_OFFSET : -6,
	DATA_LENGTH : 8,
	CLASS_NAME : 'bluff-tooltip',
	setup : function() {
		this._n = document.createElement('div');
		this._n.className = this.CLASS_NAME;
		this._n.style.position = 'absolute';
		this.hide();
		document.body.appendChild(this._n);
		Bluff.Event.observe(document.body, 'mousemove', function(c, d) {
			this._n.style.left = (d.pageX + this.LEFT_OFFSET) + 'px';
			this._n.style.top = (d.pageY + this.TOP_OFFSET) + 'px'
		}, this)
	},
	show : function(c, d, f) {
		f = Number(String(f).substr(0, this.DATA_LENGTH));
		this._n.innerHTML = '<span class="color" style="background: ' + d
				+ ';">&nbsp;</span> <span class="label">' + c
				+ '</span> <span class="data">' + f + '</span>';
		this._n.style.display = ''
	},
	hide : function() {
		this._n.style.display = 'none'
	}
});
Bluff.Event.observe(window, 'load', Bluff.Tooltip.method('setup'));
Bluff.TableReader = new JS.Class( {
	NUMBER_FORMAT : /\-?(0|[1-9]\d*)(\.\d+)?(e[\+\-]?\d+)?/i,
	initialize : function(c, d) {
		this._29 = (typeof c === 'string') ? document.getElementById(c) : c;
		this._1t = !!d
	},
	get_data : function() {
		if (!this._2)
			this._1u();
		return this._2
	},
	get_labels : function() {
		if (!this._1d)
			this._1u();
		return this._1d
	},
	get_title : function() {
		return this._2a
	},
	get_series : function(c) {
		if (this._2[c])
			return this._2[c];
		return this._2[c] = {
			points : []
		}
	},
	_1u : function() {
		this._J = this._o = 0;
		this._K = this._L = 0;
		this._2 = [];
		this._1d = {};
		this._s = [];
		this._t = [];
		this._1v(this._29);
		if ((this._s.length > 1 && this._t.length === 1)
				|| this._s.length < this._t.length) {
			if (!this._1t)
				this._1w()
		} else {
			if (this._1t)
				this._1w()
		}
		Bluff.each(this._t, function(c, d) {
			this.get_series(d - this._L).name = c
		}, this);
		Bluff.each(this._s, function(c, d) {
			this._1d[d - this._K] = c
		}, this)
	},
	_1v : function(c) {
		this._2b(c);
		var d, f = c.childNodes, g = f.length;
		for (d = 0; d < g; d++)
			this._1v(f[d])
	},
	_2b : function(c) {
		if (!c.tagName)
			return;
		var d = this._2c(c.innerHTML), f, g;
		switch (c.tagName.toUpperCase()) {
		case 'TR':
			if (!this._a)
				this._K = this._J;
			this._J += 1;
			this._o = 0;
			break;
		case 'TD':
			if (!this._a)
				this._L = this._o;
			this._a = true;
			this._o += 1;
			d = d.match(this.NUMBER_FORMAT);
			if (d === null) {
				this.get_series(f).points[g] = null
			} else {
				f = this._o - this._L - 1;
				g = this._J - this._K - 1;
				this.get_series(f).points[g] = parseFloat(d[0])
			}
			break;
		case 'TH':
			this._o += 1;
			if (this._o === 1 && this._J === 1)
				this._s[0] = this._t[0] = d;
			else if (c.scope === "row" || this._o === 1)
				this._s[this._J - 1] = d;
			else
				this._t[this._o - 1] = d;
			break;
		case 'CAPTION':
			this._2a = d;
			break
		}
	},
	_1w : function() {
		var h = this._2, i;
		this._2 = [];
		Bluff.each(h, function(f, g) {
			Bluff.each(f.points, function(c, d) {
				this.get_series(d).points[g] = c
			}, this)
		}, this);
		i = this._s;
		this._s = this._t;
		this._t = i;
		i = this._K;
		this._K = this._L;
		this._L = i
	},
	_2c : function(c) {
		return c.replace(/<\/?[^>]+>/gi, '')
	},
	extend : {
		Mixin : new JS.Module( {
			data_from_table : function(d, f) {
				var g = new Bluff.TableReader(d, f), h = g.get_data();
				Bluff.each(h, function(c) {
					this.data(c.name, c.points)
				}, this);
				this.labels = g.get_labels();
				this.title = g.get_title() || this.title
			}
		})
	}
});
Bluff.Base.include(Bluff.TableReader.Mixin);