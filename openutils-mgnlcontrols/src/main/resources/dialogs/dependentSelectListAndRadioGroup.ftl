[#assign refresh = request.getParameter("dependentSelectListCK")?has_content]
[#list selectOptionsList as options]
[#assign paramSelect]${name}Select${options_index}[/#assign]
<select style="width:100%;" class="mgnlDialogControlSelect" id="${paramSelect}" name="${paramSelect}">
  <option value="">- select -</option>
  [#list options?keys as key]
  <option value="${key?html!}"[#if key = treePathValues[options_index]!] selected="selected"[/#if]>${options[key]!}</option>
  [/#list]
</select>
[/#list]
<div class="radioGroup">
[#list radioOptions.items?keys as key]
  [#assign checked = (!refresh && key = value!) || (refresh && key = request.getParameter(name)!)]
  <input type="radio" id="${name}_${key?html!}" name="${name}Radio" value="${key?html!}"[#if checked] checked="checked"[/#if] />${radioOptions.items[key]!}<br />
[/#list]
[#if radioOptions.more]
  <a href="#" id="${name}More">More</a>
[/#if]
</div>
[#if !refresh]
  [#if (configuration['showValue']!false)?string == 'true']
<input type="text" id="${name}" name="${name}" value="${value?html!}" class="mgnlDialogControlEdit" style="width: 100%;" />
  [#else]
<input type="hidden" id="${name}" name="${name}" value="${value?html!}" />
  [/#if]
<script type="text/javascript">
(function($){
  var dialogBoxInput;
  var selects;
  var page = 1;
  
  function init(){
    dialogBoxInput = $('label[for="${name}"]').closest("tr").find("td.mgnlDialogBoxInput");
    selects = dialogBoxInput.find("select");
    selects.change(function(){
      var $this = $(this);
      var data = $("#mgnlPath,#mgnlParagraph,#mgnlRepository,#mgnlLocale,#mgnlRichE,#mgnlRichEPaste").add(selects);
      $.get($("#mgnlFormMain").attr("action"), data.serialize() + "&dependentSelectListCK=" + new Date().getTime(), function(data){
        $(data).find('label[for="${name}"]').closest("tr").find("td.mgnlDialogBoxInput").replaceAll(dialogBoxInput);
        page = 1;
        init();
      });
    });
    initRadio();
  }
  
  function initRadio(){
    var input = dialogBoxInput.find('input[name="${name}"]');
    dialogBoxInput.find('input[type="radio"]').click(function(){
      input.val($(this).val());
    });
    var link = $("a#${name}More");
    link.click(function(e){
      e.preventDefault();
      var $this = $(this);
      var data = $("#mgnlPath,#mgnlParagraph,#mgnlRepository,#mgnlLocale,#mgnlRichE,#mgnlRichEPaste").add(selects).add(input);
      $.get($("#mgnlFormMain").attr("action"), data.serialize() + "&radioGroupPage=" + (++page) + "&dependentSelectListCK=" + new Date().getTime(), function(data){
        $(data).find('label[for="${name}"]').closest("tr").find("td.mgnlDialogBoxInput .radioGroup").replaceAll(link);
        initRadio();
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
  <input type="text" id="${name}" name="${name}" value="" class="mgnlDialogControlEdit" style="width: 100%;" />
  [#else]
  <input type="hidden" name="${name}" value="" />
  [/#if]
[/#if]
