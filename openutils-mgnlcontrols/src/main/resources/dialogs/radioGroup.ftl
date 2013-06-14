[#list radioOptions.items?keys as key]
<input type="radio" id="${name}_${key?html!}" name="${name}" value="${key?html!}"[#if key = value!] checked="checked"[/#if] />${radioOptions.items[key]!}<br />
[/#list]
[#if radioOptions.more]
<a href="#" id="${name}More">More</a>
[/#if]
[#assign refresh = request.getParameter("radioGroupCK")?has_content]
[#if !refresh]
<script type="text/javascript">
(function($){
  var page = 1;
  
  function init(){
    var dialogBoxInput = $('label[for="${name}"]').closest("tr").find("td.mgnlDialogBoxInput");
    var link = $("a#${name}More");
    link.click(function(e){
      e.preventDefault();
      var $this = $(this);
      var data = $("#mgnlPath,#mgnlParagraph,#mgnlRepository,#mgnlLocale,#mgnlRichE,#mgnlRichEPaste");
      $.get($("#mgnlFormMain").attr("action"), data.serialize() + "&radioGroupPage=" + (++page) + "&radioGroupCK=" + new Date().getTime(), function(data){
        $(data).find('label[for="${name}"]').closest("tr").find("td.mgnlDialogBoxInput").replaceAll(link);
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
