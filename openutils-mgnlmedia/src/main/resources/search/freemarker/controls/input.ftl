<div class="row">
  [#if filter.label??]
    <label for="${filterKey}">[@msgIfAvail filter.label /]</label>
  [/#if]

  <input type="text" name="${filterKey}" value="${this.request.getParameter(filterKey)!''}" class="text" />
  <br />

  [@rendersFilter filter.subfilters /]
</div>
