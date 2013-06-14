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
[#assign found = false]
[#list radioOptions?keys as key]
  [#assign checked = (!refresh && key = value!) || (refresh && key = request.getParameter(name)!)]
  [#assign found = found || checked]
<input type="radio" id="${name}_${key?html!}" name="${name}" value="${key?html!}"[#if checked] checked="checked"[/#if] />${radioOptions[key]!}<br />
[/#list]
[#if !refresh]
  [#if !found]
<input type="hidden" name="${name}" value="${value?html}" />
  [/#if]
<script type="text/javascript">
(function($){
  function init(){
    var dialogBoxInput = $('label[for="${name}"]').closest("tr").find("td.mgnlDialogBoxInput");
    var selects = dialogBoxInput.find('select[name!="${name}"]');
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
[/#if]
