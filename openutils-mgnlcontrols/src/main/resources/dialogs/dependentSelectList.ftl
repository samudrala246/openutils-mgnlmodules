[#list selectOptionsList as options]
[#assign paramSelect]${name}Select${options_index}[/#assign]
<select style="width:100%;" class="mgnlDialogControlSelect" id="${paramSelect}" name="${paramSelect}">
  <option value="">- select -</option>
  [#list options?keys as key]
  <option value="${key?html!}"[#if key = treePathValues[options_index]!] selected="selected"[/#if]>${options[key]!}</option>
  [/#list]
</select>
[/#list]
[#assign refresh = request.getParameter("dependentSelectListCK")?has_content]
[#if !refresh]
  [#if (configuration['showValue']!false)?string == 'true']
<input type="text" id="${name}" name="${name}" value="${value?html!}" class="mgnlDialogControlEdit" style="width: 100%;" />
  [#else]
<input type="hidden" id="${name}" name="${name}" value="${value?html!}" />
  [/#if]
<script type="text/javascript">
(function($){
  function init(){
    var dialogBoxInput = $('label[for="${name}"]').closest("tr").find("td.mgnlDialogBoxInput");
    var selects = dialogBoxInput.find("select");
    selects.change(function(){
      var $this = $(this);
      var data = $("#mgnlPath,#mgnlParagraph,#mgnlRepository,#mgnlLocale,#mgnlRichE,#mgnlRichEPaste").add(selects);
      $.get($("#mgnlFormMain").attr("action"), data.serialize() + "&dependentSelectListCK=" + new Date().getTime(), function(data){
        $(data).find('label[for="${name}"]').closest("tr").find("td.mgnlDialogBoxInput").replaceAll(dialogBoxInput);
        init();
      });
    });
  }
  
  $(function(){
    init();
  });
})(jQuery);
</script>
[#else]
  [#if (configuration['showValue']!false)?string == 'true']
<input type="text" id="${name}" name="${name}" value="[#if leaf]${request.getParameter(paramSelect)?html!}[/#if]" class="mgnlDialogControlEdit" style="width: 100%;" />
  [#else]
<input type="hidden" name="${name}" value="[#if leaf]${request.getParameter(paramSelect)?html!}[/#if]" />
  [/#if]
[/#if]
