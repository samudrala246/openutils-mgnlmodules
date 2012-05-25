[#if !alreadyrendered]
  <script type="text/javascript" src="${request.contextPath}/.resources/ext/ext-base.js"></script>

  [#if debug]
  <script type="text/javascript" src="${request.contextPath}/.resources/ext/ext-all-debug.js"></script>
  [#else]
  <script type="text/javascript" src="${request.contextPath}/.resources/ext/ext-all.js"></script>
  [/#if]

  <script type="text/javascript">
    // <![CDATA[

function gridPaste(name, text, expand) {
  var hidden = document.getElementById(name);
  var grid = hidden._grid;
  var rows, i, I;
  var cols, j, J;

  var data = [];
  rows = text.split('\n');
  I = rows.length;
  if (I > 0 && rows[I - 1] == '') {
    I--;
    rows.splice(I, 1);
  }
  J = 0;
  for (i = 0; i < I; i++) {
    cols = rows[i].split('\t');
    data.push(cols);
    if (J < cols.length) J = cols.length;
  }
  // padding
  for (i = 0; i < I; i++) {
    cols = data[i];
    for (j = cols.length; j < J; j++) {
      cols.push('');
    }
  }

  var cell0, cell1;
  if (grid.selModel.selection) {
    cell0 = grid.selModel.selection.cell[0];
    cell1 = grid.selModel.selection.cell[1];
  } else {
    cell0 = 0;
    cell1 = 0;
  }
  var row, col;
  var record, field;
  var rt;
  var e;
  for (i = 0; i < I; i++) {
    row = cell0 + i;
    record = grid.store.getAt(row);
    if (!record && expand) {
      rt = {};
      for (j = 0; j < J; j++) {
       rt[''+j] = '';
      }
      record = new grid.store.recordType(rt);
      grid.store.add(record);
    }
    if (record) {
      for (j = 0; j < J; j++) {
        col = cell1 + j;
        if (col < grid.colModel.getColumnCount()) {
          field = grid.colModel.getDataIndex(col);
          e = {
            grid: grid,
            record: record,
            field: field,
            originalValue: record.get(field),
            value: data[i][j],
            row: row,
            column: col
          };
          record.set(field, e.value);
          grid.fireEvent("afteredit", e);
        }
      }
    }
  }
}

function gridMoveRow(grid, delta) {
  if (grid.selModel.selection) {
    var dat = grid.store.data, r1, r2, j1 = grid.selModel.selection.cell[0], j2 = j1 + delta, k, K = grid.colModel.getColumnCount(), field, e1, e2;
    if (j2 >= 0 && j2 < dat.length) {
      r1 = grid.store.getAt(j1);
      r2 = grid.store.getAt(j2);
      for (k = 0; k < K; k++) {
        field = grid.colModel.getDataIndex(k);
        e1 = {
          grid: grid,
          record: r1,
          field: field,
          originalValue: r1.data[field],
          value: r2.data[field],
          row: j1,
          column: k,
          cancel: false
        };
        e2 = {
          grid: grid,
          record: r2,
          field: field,
          originalValue: r2.data[field],
          value: r1.data[field],
          row: j2,
          column: k,
          cancel: false
        };
        if(grid.fireEvent("validateedit", e1) !== false && !e1.cancel && grid.fireEvent("validateedit", e2) !== false && !e2.cancel){
          r1.set(field, e1.value);
          delete e1.cancel;
          grid.fireEvent("afteredit", e1);

          r2.set(field, e2.value);
          delete e2.cancel;
          grid.fireEvent("afteredit", e2);

          grid.selModel.select(j2, grid.selModel.selection.cell[1]);
        }
      }
    }
  }
}

function gridInsertRow(grid, fixedRows) {
  if (grid.selModel.selection) {
    var c0 = grid.selModel.selection.cell[0], c1 = grid.selModel.selection.cell[1], k, K = grid.colModel.getColumnCount(), rt, record;
    rt = {};
    for (k = 0; k < K; k++) {
     rt[''+k] = '';
    }
    if (fixedRows) {
      record = grid.store.getAt(grid.store.data.length - 1);
      grid.store.remove(record);
    }
    record = new grid.store.recordType(rt);
    grid.store.insert(c0, record);
    grid.selModel.select(c0, c1);
    grid.fireEvent("afteredit", { grid: grid });
  }
}

function gridRemoveRow(grid, fixedRows) {
  if (grid.selModel.selection) {
    var c0 = grid.selModel.selection.cell[0], c1 = grid.selModel.selection.cell[1], k, K = grid.colModel.getColumnCount(), rt, record;
    record = grid.store.getAt(c0);
    grid.store.remove(record);
    if (fixedRows) {
      rt = {};
      for (k = 0; k < K; k++) {
       rt[''+k] = '';
      }
      record = new grid.store.recordType(rt);
      grid.store.add(record);
    }
    grid.selModel.select(c0, c1);
    grid.fireEvent("afteredit", { grid: grid });
  }
}

    // ]]>
  </script>

  <link rel="stylesheet" type="text/css" href="${request.contextPath}/.resources/ext/css/ext-all.css" />
  <link rel="stylesheet" type="text/css" href="${request.contextPath}/.resources/controls/css/grid.css" />
  <!--[if lte IE 7]>
    <link rel="stylesheet" type="text/css" href="${request.contextPath}/.resources/controls/css/grid-ie6fix.css" />
  <![endif]-->

  [#list gridColumnTypes?values as gct]
    ${gct.headSnippet}
  [/#list]
[/#if]

<input type="hidden" id="${name}" name="${name}" value="${gridValue?html}"/>

<div id="grid-container-${name}">
   <div id="grid-${name}"></div>
</div>

[#list configuration.columns?values as colmap]
  [#if (colmap.type?? && gridColumnTypes[colmap.type]??)]
    ${gridColumnTypes[colmap.type].drawSupportHtml(name, colmap_index, colmap, msgs)}
  [/#if]
[/#list]

<script type="text/javascript">
    // <![CDATA[

Ext.onReady(function(){
  var hidden = document.getElementById('${name}');
  var rows = hidden.value.split(/\r?\n/);
  
  [#assign addRowsEnabled = (configuration['addRowsEnabled']!false)?string == 'true']
  [#assign cfgRows = configuration.rows!10]
  [#if (addRowsEnabled)]
  var numOfRows= Math.max(${cfgRows}, rows.length);
  [#else]
  var numOfRows= ${cfgRows};
  [/#if]

  var i, myData = [];
  for (i = 0; i < numOfRows; i++) {
    myData.push((i < rows.length ? rows[i] : '').split('\t').concat([ undefined, undefined ]).slice(0, ${configuration.columns?size}));
  }

  var dataStore = new Ext.data.SimpleStore({
    fields: [ [#list configuration.columns?values as colmap]'${colmap_index}'[#if colmap_has_next],[/#if][/#list] ]
  });
  dataStore.loadData(myData);

  // shorthand alias
  var fm = Ext.form, Ed = Ext.grid.GridEditor;

  var plugins = [];

  var colModel = new Ext.grid.ColumnModel([
    [#list configuration.columns?values as colmap]
      ${(gridColumnTypes[colmap.type!'text']!gridColumnTypes['text']).drawColumnJs(name, colmap_index, colmap, msgs)}[#if colmap_has_next],[/#if]
    [/#list]
  ]);
  colModel.defaultSortable = true;

  var grid = new Ext.grid.EditorGridPanel({
    store: dataStore,
    cm: colModel,
    selModel: new Ext.grid.CellSelectionModel(),
    autoSizeColumns: true,
    enableColLock: false,
    plugins: plugins,
    clicksToEdit: 1,
    renderTo: 'grid-${name}',
    tbar: [{
      text: 'Add row',
      tooltip: 'Add row',
      icon: '${request.contextPath}/.resources/controls/img/icon-add.png',
      iconCls: 'button-add',
      handler: function() {
        dataStore.add(new dataStore.recordType({[#list configuration.columns?values as colmap]'${colmap_index}':''[#if colmap_has_next],[/#if][/#list]}));
      },
      disabled: ${(!addRowsEnabled)?string}
    }, {
      text: 'Clear all',
      tooltip: 'Clear all',
      icon: '${request.contextPath}/.resources/controls/img/icon-delete.png',
      iconCls: 'button-delete',
      handler: function() {
        var dat = grid.store.data, j, r, k, K = grid.colModel.getColumnCount(), field, e;
        for (j = 0; j < dat.length; j++) {
          r = grid.store.getAt(j);
          for (k = 0; k < K; k++) {
            field = grid.colModel.getDataIndex(k);
            e = {
              grid: grid,
              record: r,
              field: field,
              originalValue: r.data[field],
              value: '',
              row: j,
              column: k,
              cancel: false
            };
            if(grid.fireEvent("validateedit", e) !== false && !e.cancel){
              r.set(field, e.value);
              delete e.cancel;
              grid.fireEvent("afteredit", e);
            }
          }
        }
      }
    }, {
      text: 'Paste from spreadsheet',
      tooltip: 'Paste from spreadsheet',
      icon: '${request.contextPath}/.resources/controls/img/icon-paste.png',
      iconCls: 'button-paste',
      handler: function() {
        mgnlOpenWindow('/.resources/controls/clipboard.html?name=${name}&expand=${addRowsEnabled?string}', 320, 200);
      }
    }, {
      text: '',
      tooltip: 'Move row up',
      icon: '${request.contextPath}/.resources/controls/img/icon-move-up.png',
      iconCls: 'button-move-up',
      handler: function() {
        gridMoveRow(grid, -1);
      }
    }, {
      text: '',
      tooltip: 'Move row down',
      icon: '${request.contextPath}/.resources/controls/img/icon-move-down.png',
      iconCls: 'button-move-down',
      handler: function() {
        gridMoveRow(grid, 1);
      }
    }, {
      text: '',
      tooltip: 'Insert row',
      icon: '${request.contextPath}/.resources/controls/img/icon-insert-row.png',
      iconCls: 'button-insert-row',
      handler: function() {
        gridInsertRow(grid, ${(!addRowsEnabled)?string});
      },
      disabled: ${(!addRowsEnabled)?string}
    }, {
      text: '',
      tooltip: 'Remove row',
      icon: '${request.contextPath}/.resources/controls/img/icon-remove-row.png',
      iconCls: 'button-remove-row',
      handler: function() {
        gridRemoveRow(grid, ${(!addRowsEnabled)?string});
      },
      disabled: ${(!addRowsEnabled)?string}
    }],
    [#if (configuration.height??)]
    height: ${configuration.height},
    [/#if]
    viewConfig: {
      forceFit: true
    }
  });

  Ext.EventManager.onWindowResize(function(){
    grid.setWidth(grid.container.getWidth());
  });

  grid.on('afteredit', function(grid) {
    var dat = grid.grid.store.data;
    var cm = grid.grid.getColumnModel();

    var fullValue = '', j, row, k, cell;
    for (j = 0; j < dat.length; j++) {
      row = dat.item(j);
      for (k = 0; k < cm.getColumnCount(); k++) {
        cell = row.get(k);
        fullValue += Ext.isDate(cell) ? cell.format('Y-m-d') : cell;
        if ((k+1) < cm.getColumnCount()) {
          fullValue += '\t';
        }
      }
      fullValue += '\n';
    }
   hidden.value = fullValue.replace(/\r/g, '');
  }, this, true);

  hidden._grid = grid;
});

// ]]>
</script>

