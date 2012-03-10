<div class="row">
  [#if filter.label??]
    <label for="${filterKey}">[@msgIfAvail filter.label /]</label>
  [/#if]

  [#list filter.options as option]
    <div class="inner-row">
      <input type="checkbox" id="${filterKey}_${option.value}" name="${filterKey}" value="${option.value}"[#if ((this.request.getParameterValues(filterKey)![])?seq_contains(option.value))] checked="checked"[/#if] /> 
      <label for="${filterKey}_${option.value}">[@msgIfAvail option.label /]</span>
    </div>
  [/#list]

  [@rendersFilter filter.subfilters /]
</div>
