<div class="row">
  [#if filter.label??]
    <label for="${filterKey}">[@msgIfAvail filter.label /]</label>
  [/#if]

  <select name="${filterKey}" id="${filterKey}">
    [#list filter.options as option]
      <option value="${option.value}" [#if (!this.request.getParameter(filterKey)?has_content & option.defaultValue?? & option.defaultValue) || (this.request.getParameter(filterKey)?has_content & this.request.getParameter(filterKey) == option.value)]selected="selected"[/#if]>[@msgIfAvail option.label /]</option>
    [/#list]
  </select>

  [@rendersFilter filter.subfilters /]
</div>
