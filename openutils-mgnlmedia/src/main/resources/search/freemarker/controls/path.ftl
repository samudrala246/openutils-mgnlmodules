<div class="row">
  [#if filter.label??]
    <label for="${filterKey}">[@msgIfAvail filter.label /]</label>
  [/#if]

  <input type="text" name="${filterKey}" id="${filterKey}" value="${this.request.getParameter(filterKey)!''}" class="input-path" />
  <span class="mgnlControlButton" id="butt_${filterKey}" onclick="mgnlDialogLinkOpenBrowser('${filterKey}', 'media', '')">${this.msgs['buttons.select']}</span>

  [@rendersFilter filter.subfilters /]
</div>
