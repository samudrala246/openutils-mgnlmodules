[#if !fckalreadyrendered]
  <script type="text/javascript" src="${request.contextPath}/.resources/fckeditor/fckeditor.js"></script>

  <script type="text/javascript">
function FCKeditor_OnComplete(oEditor){
  if (oEditor.Config['CustomConfigurationsPath'].indexOf('ExpressionField.config.js') > -1){
    oEditor.Events.AttachEvent('OnAfterLinkedFieldUpdate', function(oExprEditor) {
      var oDOM = oExprEditor.EditorDocument;
      var value;
      if (document.all){
        value = oDOM.body.innerText;
      } else {
        var r = oDOM.createRange();
        r.selectNodeContents(oDOM.body);
        value = r.toString();
      }
      oExprEditor.LinkedField.value = value ;
    });
  }
}
  </script>
[/#if]

  <script type="text/javascript">
var sBasePath = '${request.contextPath}/.resources/fckeditor/';
var oFCKeditor = new FCKeditor('${name}');
oFCKeditor.BasePath = sBasePath;
oFCKeditor.Config['CustomConfigurationsPath'] = '${request.contextPath}/.resources/expressions/js/ExpressionField.config.js';
oFCKeditor.Config['EnterMode'] = 'br';
oFCKeditor.Config['ShiftEnterMode'] = 'br';
oFCKeditor.ToolbarSet = 'Expression';
oFCKeditor.Height = 200;
oFCKeditor.Value = "${value?html}";
oFCKeditor.Create();
  </script>
