_mgnlGetWindowSize = mgnlGetWindowSize;

mgnlGetWindowSize = function(){
  var sizeObj = _mgnlGetWindowSize();
  sizeObj.h += 17;
  return sizeObj;
}

mgnlTree.prototype._resize = mgnlTree.prototype.resize;

mgnlTree.prototype.resize = function(columnNumber){
  this._resize(columnNumber);
  if (this.divMain){
    var hb, fb;
    for (hb = this.divMain.previousSibling; hb.className != 'mgnlListHeaderBackground'; hb = hb.previousSibling) {}
    hb.style.width = this.divMain.style.width;
    for (fb = this.divMain.nextSibling; fb.className != 'mgnlFunctionBar'; fb = fb.nextSibling) {}
    fb.style.width = this.divMain.style.width;
  }
}