[#assign emptyThumbnailUrl = '${request.contextPath}/.resources/media/assets/empty.gif']
[#if !alreadyrendered]
<script type="text/javascript">
  // <![CDATA[
  function selectMedia(name, value, mediaType) {
    var url = "${request.contextPath}/.magnolia/pages/mediaBrowser.html?nodeid=" + name + "&selectMedia=true&mgnlCK="+
        mgnlGetCacheKiller();
    if (mediaType != null && mediaType != 'null')
    {
      url += "&mediaType=" + mediaType;
    }
    if (value != null && value != '' && value != 'null')
    {
      url += "&actMedia=" + value;
    }
    [#if configuration['openPath']?has_content]
    else
    {
      url += "&openPath=${configuration['openPath']}";
    }
    [/#if]
    var selectMediaWin =
      window.open(url, 'selectMedia', "width=840,height=560,scrollbars=no,status=yes,resizable=yes");
    selectMediaWin.opener = window;
  }

  function removeMedia(name) {
    document.getElementById(name).value = "";

    document.getElementById("dispRem"+ name + "Img").style.display = "none";
    document.getElementById("dispRemBtn"+ name).style.display = "none";
    document.getElementById("dispRem"+ name + "Txt").innerHTML = "";
    document.getElementById("dispSel"+ name).style.display = "inline";
  }

  function setNewMedia(name, uuid, filename, thumbnailurl) {
    document.getElementById(name).value = uuid;

    document.getElementById("dispRem"+ name).style.display = "block";
    document.getElementById("dispRemBtn"+ name).style.display = "block";
    document.getElementById("dispSel"+ name).style.display = "none";

    document.getElementById("dispRem"+ name + "Img").src = "${emptyThumbnailUrl}";
    document.getElementById("dispRem"+ name + "Img").src = thumbnailurl;

    // reset with in case of old binary values
    document.getElementById("dispRem"+ name + "Img").style.width = "auto";
    document.getElementById("dispRem"+ name + "Img").style.height = "auto";
    document.getElementById("dispRem"+ name + "Img").style.display = "inline";
    document.getElementById("dispRem"+ name + "Txt").innerHTML = filename;

  }
  // ]]>
</script>
[/#if]


[#if !binaryfield?exists]
  [#assign valuemediacontent = value!'null']
[#else]
  [#assign valuemediacontent = '']
[/#if]

<input type="hidden" id="${name}" name="${name}" value="${valuemediacontent}" />
[#if value?exists && value?length > 0]
  [#assign dispRem = "block"]
  [#assign dispSel = "none"]
[#else]
  [#assign dispRem = "none"]
  [#assign dispSel = "block"]
[/#if]
[#if (configuration['resizing']!false)?string == 'true']
  [#assign dispResize = "block"]
[#else]
  [#assign dispResize = "none"]
[/#if]

<!--[if lt IE 8]><style>
.imagebox span {
    display: inline-block;
    height: 100%;
}
</style><![endif]-->

<div style="height: 120px;">

  <div class="medialeft" style="width: 106px; float:left; height: 106px;">
    <div id="dispRem${name}">
      <div class="imagebox"
        style="width: 106px; height: 106px;b order: 1px solid #999; display: table-cell; text-align: center; vertical-align: middle; background-image: url(${request.contextPath}/.resources/media/assets/squares.png);">
        <a href="#" onclick="selectMedia('${name}', '${valuemediacontent}', '${configuration['mediaType']!'null'}')"
          title="Select media" style="height: 106px; width: 106px; display: table-cell; vertical-align: middle; text-align: center; color:#396101; text-decoration:none">

          <span style="vertical-align: middle;"></span>
          <img id="dispRem${name}Img" src="${thumbnailUrl!emptyThumbnailUrl}"
               style="text-align:center; border: none; vertical-align: middle; display:[#if thumbnailUrl?exists] inline [#else] none [/#if]; [#if binaryfield?exists] width: 106px; height: 106px[/#if]"
               alt="" />

          <span id="dispSel${name}" style="text-align:center; display:${dispSel}; vertical-align: middle; color:#396101; text-decoration:none; cursor: pointer;height: 20px; width:102px">${msgs.get("media.select")}</span>


        </a>
      </div>
    </div>
  </div>
  <div class="mediaright" style="margin-left: 120px;height: 106px;">



  <div>
    <div id="dispRem${name}Txt" style="height: 18px; overflow: hidden">${filename!''}</div>

    <div id="dispResize${name}" style="display:${dispResize}">
      <table>
        <tbody>
          <tr>
            <td class="mgnlDialogBoxLabel">Width</td>
            <td class="mgnlDialogBoxInput">
              <input type="text" id="${name}_width" name="${name}_width" value="${width!''}" class="mgnlDialogControlEdit"
                style="width: 60px;" />
            </td>
          </tr>
          <tr>
            <td class="mgnlDialogBoxLabel">Height</td>
            <td class="mgnlDialogBoxInput">
              <input type="text" id="${name}_height" name="${name}_height" value="${height!''}" class="mgnlDialogControlEdit"
                style="width: 60px;" />
            </td>
          </tr>
        </tbody>
      </table>
    </div>
    <div id="dispRemBtn${name}" style="display:${dispRem}; margin-top:5px;">
      <span class="mgnlControlButton" onclick="mgnlShiftPushButtonClick(this);removeMedia('${name}');" onmouseout="mgnlShiftPushButtonOut(this);"
        onmousedown="mgnlShiftPushButtonDown(this);" >${msgs.get("media.remove")}</span>
    </div>


  </div>
  </div>

  [#if (configuration['showpath']!false)?string == 'true' && handle?has_content]
    <input type="text" id="${name}_fullpath" name="${name}_fullpath" value="${handle}" class="mgnlDialogControlEdit"
      style="width: 100%;" readonly="readonly" />
  [/#if]

</div>
