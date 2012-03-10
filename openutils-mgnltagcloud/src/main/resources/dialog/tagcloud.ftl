[#if includejquery]
<script type="text/javascript" src="${request.contextPath}/.resources/tagcloud/js/jquery.min.js"></script>
[/#if]

[#if debugEnabled]
<script type="text/javascript" src="${request.contextPath}/.resources/tagcloud/js/jquery.fcbkcomplete.js"></script>
[#else]
<script type="text/javascript" src="${request.contextPath}/.resources/tagcloud/js/jquery.fcbkcomplete.min.js"></script>
[/#if]

<link rel="stylesheet" type="text/css" href="${request.contextPath}/.resources/tagcloud/css/style.css" />

<select name="${name}" id="${name}" multiple="multiple">
  [#assign i = 0]
  [#if configuredTagList??]
    [#list configuredTagList?keys as tag]
      <option value="${tag?trim}" [#if configuredTagList[tag].selected]class="selected"[/#if]>${tag} (${configuredTagList[tag].count})</option>
    [/#list]
  [/#if]
</select>

<script language="JavaScript">
  // <![CDATA[
  jQuery(document).ready(function() {        
      var tagCloud = jQuery("#${name}");
      
      tagCloud.fcbkcomplete({
        cache: true,
        filter_case: true,
        filter_hide: true,
        firstselected: true,
        filter_selected: true,
        newel: ${((configuration['forcePick']?string)! != 'true')?string},
        maxshownitems: 10,
        tagCloudId: "tagCloud_${name}"        
      });    
      
      var width = "${width}" != "" ? "${width}px" : "97%";
      
      jQuery('#tagCloud_${name}').css('width', width);
      
  });
  // ]]>
</script>
