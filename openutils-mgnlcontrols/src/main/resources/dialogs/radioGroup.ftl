[#list radioOptions?keys as key]
<input type="radio" id="${name}_${key?html!}" name="${name}" value="${key?html!}"[#if key = value!] checked="checked"[/#if] />${radioOptions[key]!}<br />
[/#list]
