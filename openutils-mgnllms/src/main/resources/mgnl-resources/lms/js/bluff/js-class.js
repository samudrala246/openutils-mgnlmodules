this.JS = this.JS || {};
JS.extend = function(a, b) {
	b = b || {};
	for ( var c in b) {
		if (a[c] === b[c])
			continue;
		a[c] = b[c]
	}
	return a
};
JS.extend(JS,
		{
			makeFunction : function() {
				return function() {
					return this.initialize ? (this.initialize.apply(this,
							arguments) || this) : this
				}
			},
			makeBridge : function(a) {
				var b = function() {
				};
				b.prototype = a.prototype;
				return new b
			},
			bind : function() {
				var a = JS.array(arguments), b = a.shift(), c = a.shift()
						|| null;
				return function() {
					return b.apply(c, a.concat(JS.array(arguments)))
				}
			},
			callsSuper : function(a) {
				return a.SUPER === undefined ? a.SUPER = /\bcallSuper\b/.test(a
						.toString()) : a.SUPER
			},
			mask : function(a) {
				var b = a.toString().replace(/callSuper/g, 'super');
				a.toString = function() {
					return b
				};
				return a
			},
			array : function(a) {
				if (!a)
					return [];
				if (a.toArray)
					return a.toArray();
				var b = a.length, c = [];
				while (b--)
					c[b] = a[b];
				return c
			},
			indexOf : function(a, b) {
				for ( var c = 0, d = a.length; c < d; c++) {
					if (a[c] === b)
						return c
				}
				return -1
			},
			isFn : function(a) {
				return a instanceof Function
			},
			isType : function(a, b) {
				if (!a || !b)
					return false;
				return (b instanceof Function && a instanceof b)
						|| (typeof b === 'string' && typeof a === b)
						|| (a.isA && a.isA(b))
			},
			ignore : function(a, b) {
				return /^(include|extend)$/.test(a) && typeof b === 'object'
			}
		});
JS.Module = JS.makeFunction();
JS
		.extend(
				JS.Module.prototype,
				{
					END_WITHOUT_DOT : /([^\.])$/,
					initialize : function(a, b, c) {
						this.__mod__ = this;
						this.__inc__ = [];
						this.__fns__ = {};
						this.__dep__ = [];
						this.__mct__ = {};
						if (typeof a === 'string') {
							this.__nom__ = this.displayName = a
						} else {
							this.__nom__ = this.displayName = '';
							c = b;
							b = a
						}
						c = c || {};
						this.__res__ = c._1 || null;
						if (b)
							this.include(b, false);
						if (JS.Module.__chainq__)
							JS.Module.__chainq__.push(this)
					},
					setName : function(a) {
						this.__nom__ = this.displayName = a || '';
						for ( var b in this.__mod__.__fns__)
							this.__name__(b);
						if (a && this.__meta__)
							this.__meta__.setName(a + '.')
					},
					__name__ : function(a) {
						if (!this.__nom__)
							return;
						var b = this.__mod__.__fns__[a] || {};
						a = this.__nom__.replace(this.END_WITHOUT_DOT, '$1#')
								+ a;
						if (JS.isFn(b.setName))
							return b.setName(a);
						if (JS.isFn(b))
							b.displayName = a
					},
					define : function(a, b, c, d) {
						var f = (d || {})._0 || this;
						this.__fns__[a] = b;
						this.__name__(a);
						if (JS.Module._0 && f && JS.isFn(b))
							JS.Module._0(a, f);
						if (c !== false)
							this.resolve()
					},
					instanceMethod : function(a) {
						var b = this.lookup(a).pop();
						return JS.isFn(b) ? b : null
					},
					instanceMethods : function(a, b) {
						var c = this.__mod__, b = b || [], d = c.ancestors(), f = d.length, e;
						for (e in c.__fns__) {
							if (c.__fns__.hasOwnProperty(e)
									&& JS.isFn(c.__fns__[e])
									&& JS.indexOf(b, e) === -1)
								b.push(e)
						}
						if (a === false)
							return b;
						while (f--)
							d[f].instanceMethods(false, b);
						return b
					},
					include : function(a, b, c) {
						b = (b !== false);
						if (!a)
							return b ? this.resolve() : this.uncache();
						c = c || {};
						if (a.__mod__)
							a = a.__mod__;
						var d = a.include, f = a.extend, e = c._4 || this, g, h, i, j;
						if (a.__inc__ && a.__fns__) {
							this.__inc__.push(a);
							a.__dep__.push(this);
							if (c._2)
								a.extended && a.extended(c._2);
							else
								a.included && a.included(e)
						} else {
							if (c._5) {
								for (h in a) {
									if (JS.ignore(h, a[h]))
										continue;
									this.define(h, a[h], false, {
										_0 : e || c._2 || this
									})
								}
							} else {
								if (typeof d === 'object'
										|| JS.isType(d, JS.Module)) {
									g = [].concat(d);
									for (i = 0, j = g.length; i < j; i++)
										e.include(g[i], b, c)
								}
								if (typeof f === 'object'
										|| JS.isType(f, JS.Module)) {
									g = [].concat(f);
									for (i = 0, j = g.length; i < j; i++)
										e.extend(g[i], false);
									e.extend()
								}
								c._5 = true;
								return e.include(a, b, c)
							}
						}
						b ? this.resolve() : this.uncache()
					},
					includes : function(a) {
						var b = this.__mod__, c = b.__inc__.length;
						if (Object === a || b === a
								|| b.__res__ === a.prototype)
							return true;
						while (c--) {
							if (b.__inc__[c].includes(a))
								return true
						}
						return false
					},
					match : function(a) {
						return a.isA && a.isA(this)
					},
					ancestors : function(a) {
						var b = this.__mod__, c = (a === undefined), d = (b.__res__ || {}).klass, f = (d && b.__res__ === d.prototype) ? d
								: b, e, g;
						if (c && b.__anc__)
							return b.__anc__.slice();
						a = a || [];
						for (e = 0, g = b.__inc__.length; e < g; e++)
							b.__inc__[e].ancestors(a);
						if (JS.indexOf(a, f) === -1)
							a.push(f);
						if (c)
							b.__anc__ = a.slice();
						return a
					},
					lookup : function(a) {
						var b = this.__mod__, c = b.__mct__;
						if (c[a])
							return c[a].slice();
						var d = b.ancestors(), f = [], e, g, h;
						for (e = 0, g = d.length; e < g; e++) {
							h = d[e].__mod__.__fns__[a];
							if (h)
								f.push(h)
						}
						c[a] = f.slice();
						return f
					},
					make : function(a, b) {
						if (!JS.isFn(b) || !JS.callsSuper(b))
							return b;
						var c = this;
						return function() {
							return c.chain(this, a, arguments)
						}
					},
					chain : JS
							.mask(function(c, d, f) {
								var e = this.lookup(d), g = e.length - 1, h = c.callSuper, i = JS
										.array(f), j;
								c.callSuper = function() {
									var a = arguments.length;
									while (a--)
										i[a] = arguments[a];
									g -= 1;
									var b = e[g].apply(c, i);
									g += 1;
									return b
								};
								j = e.pop().apply(c, i);
								h ? c.callSuper = h : delete c.callSuper;
								return j
							}),
					resolve : function(a) {
						var b = this.__mod__, a = a || b, c = a.__res__, d, f, e, g;
						if (a === b) {
							b.uncache(false);
							d = b.__dep__.length;
							while (d--)
								b.__dep__[d].resolve()
						}
						if (!c)
							return;
						for (d = 0, f = b.__inc__.length; d < f; d++)
							b.__inc__[d].resolve(a);
						for (e in b.__fns__) {
							g = a.make(e, b.__fns__[e]);
							if (c[e] !== g)
								c[e] = g
						}
					},
					uncache : function(a) {
						var b = this.__mod__, c = b.__dep__.length;
						b.__anc__ = null;
						b.__mct__ = {};
						if (a === false)
							return;
						while (c--)
							b.__dep__[c].uncache()
					}
				});
JS.Class = JS.makeFunction();
JS.extend(JS.Class.prototype = JS.makeBridge(JS.Module), {
	initialize : function(a, b, c) {
		if (typeof a === 'string') {
			this.__nom__ = this.displayName = a
		} else {
			this.__nom__ = this.displayName = '';
			c = b;
			b = a
		}
		var d = JS.extend(JS.makeFunction(), this);
		d.klass = d.constructor = this.klass;
		if (!JS.isFn(b)) {
			c = b;
			b = Object
		}
		d.inherit(b);
		d.include(c, false);
		d.resolve();
		do {
			b.inherited && b.inherited(d)
		} while (b = b.superclass);
		return d
	},
	inherit : function(a) {
		this.superclass = a;
		if (this.__eigen__ && a.__eigen__)
			this.extend(a.__eigen__(), true);
		this.subclasses = [];
		(a.subclasses || []).push(this);
		var b = this.prototype = JS.makeBridge(a);
		b.klass = b.constructor = this;
		this.__mod__ = new JS.Module(this.__nom__, {}, {
			_1 : this.prototype
		});
		this.include(JS.Kernel, false);
		if (a !== Object)
			this.include(a.__mod__ || new JS.Module(a.prototype, {
				_1 : a.prototype
			}), false)
	},
	include : function(a, b, c) {
		if (!a)
			return;
		var d = this.__mod__, c = c || {};
		c._4 = this;
		return d.include(a, b, c)
	},
	define : function(a, b, c, d) {
		var f = this.__mod__;
		d = d || {};
		d._0 = this;
		f.define(a, b, c, d)
	}
});
JS.Module = new JS.Class('Module', JS.Module.prototype);
JS.Class = new JS.Class('Class', JS.Module, JS.Class.prototype);
JS.Module.klass = JS.Module.constructor = JS.Class.klass = JS.Class.constructor = JS.Class;
JS.extend(JS.Module, {
	_3 : [],
	__chainq__ : [],
	methodAdded : function(a, b) {
		this._3.push( [ a, b ])
	},
	_0 : function(a, b) {
		var c = this._3, d = c.length;
		while (d--)
			c[d][0].call(c[d][1] || null, a, b)
	}
});
JS.Kernel = JS
		.extend(
				new JS.Module(
						'Kernel',
						{
							__eigen__ : function() {
								if (this.__meta__)
									return this.__meta__;
								var a = this.__nom__, b = this.klass.__nom__, c = a
										|| (b ? '#<' + b + '>' : ''), d = this.__meta__ = new JS.Module(
										c ? c + '.' : '', {}, {
											_1 : this
										});
								d.include(this.klass.__mod__, false);
								return d
							},
							equals : function(a) {
								return this === a
							},
							extend : function(a, b) {
								return this.__eigen__().include(a, b, {
									_2 : this
								})
							},
							hash : function() {
								return this.__hashcode__ = this.__hashcode__
										|| JS.Kernel.getHashCode()
							},
							isA : function(a) {
								return this.__eigen__().includes(a)
							},
							method : function(a) {
								var b = this, c = b.__mcache__ = b.__mcache__
										|| {};
								if ((c[a] || {}).fn === b[a])
									return c[a].bd;
								return (c[a] = {
									fn : b[a],
									bd : JS.bind(b[a], b)
								}).bd
							},
							methods : function() {
								return this.__eigen__().instanceMethods(true)
							},
							tap : function(a, b) {
								a.call(b || null, this);
								return this
							}
						}),
				{
					__hashIndex__ : 0,
					getHashCode : function() {
						this.__hashIndex__ += 1;
						return (Math.floor(new Date().getTime() / 1000) + this.__hashIndex__)
								.toString(16)
					}
				});
JS.Module.include(JS.Kernel);
JS.extend(JS.Module, JS.Kernel.__fns__);
JS.Class.include(JS.Kernel);
JS.extend(JS.Class, JS.Kernel.__fns__);
JS.Interface = new JS.Class( {
	initialize : function(d) {
		this.test = function(a, b) {
			var c = d.length;
			while (c--) {
				if (!JS.isFn(a[d[c]]))
					return b ? d[c] : false
			}
			return true
		}
	},
	extend : {
		ensure : function() {
			var a = JS.array(arguments), b = a.shift(), c, d;
			while (c = a.shift()) {
				d = c.test(b, true);
				if (d !== true)
					throw new Error('object does not implement ' + d + '()');
			}
		}
	}
});
JS.Singleton = new JS.Class( {
	initialize : function(a, b, c) {
		return new (new JS.Class(a, b, c))
	}
});
