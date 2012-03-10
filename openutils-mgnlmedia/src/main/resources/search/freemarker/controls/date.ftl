<div class="row">
  [#if filter.label??]
    <label for="${filterKey}">[@msgIfAvail filter.label /]</label>
  [/#if]

  <input type="text" name="${filterKey}" id="${filterKey}" value="${this.request.getParameter(filterKey)!''}" class="input-date" />
  <span class="mgnlControlButton" id="butt_${filterKey}" onclick="cal_butt_${filterKey}.show()">${this.msgs['buttons.date']}</span>
  <script type="text/javascript">
    // <![CDATA[
    var cal_butt_${filterKey} = Calendar.setup({
        inputField: "${filterKey}",
        ifFormat: "%Y-%m-%d",
        showsTime: false,
        timeFormat: "24",
        cache: true,
        button: "butt_${filterKey}",
        singleClick: "true",
        step: 1
      });
    // ]]>
  </script>

  [@rendersFilter filter.subfilters /]
</div>
