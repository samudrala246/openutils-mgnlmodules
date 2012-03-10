Ext.grid.CheckColumn = function(config){
    Ext.apply(this, config);
    if(!this.id){
        this.id = Ext.id();
    }
    this.renderer = this.renderer.createDelegate(this);
};

Ext.grid.CheckColumn.prototype ={
    init : function(grid){
        this.grid = grid;
        this.grid.on('render', function(){
            var view = this.grid.getView();
            view.mainBody.on('mousedown', this.onMouseDown, this);
        }, this);
    },

    onMouseDown : function(e, t){
        if(t.className && t.className.indexOf('x-grid3-cc-'+this.id) != -1){
            e.stopEvent();
            var index = this.grid.getView().findRowIndex(t);
            var record = this.grid.store.getAt(index);
            var value = !(String(record.data[this.dataIndex]).trim().toLowerCase() == 'true');
            var e2 = {
              grid: this.grid,
              record: record,
              field: this.dataIndex,
              originalValue: record.get(this.dataIndex),
              value: value,
              row: index,
              column: this.grid.colModel.findColumnIndex(t)
            };
            record.set(this.dataIndex, value);
            this.grid.fireEvent("afteredit", e2);
        }
    },

    renderer : function(v, p, record){
        p.css += ' x-grid3-check-col-td';
        return '<div class="x-grid3-check-col'+((String(v).trim().toLowerCase() == 'true')?'-on':'')+' x-grid3-cc-'+this.id+'">&#160;</div>';
    }
};