[#list request.parameterNames as pName]
[#if pName?starts_with('${name}.')]
[#list request.getParameterValues(pName) as pValue]
<input type="hidden" id="${pName}" name="${pName}" value="${pValue!''}" />
[/#list]
[/#if]
[/#list]
