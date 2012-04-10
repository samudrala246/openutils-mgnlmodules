<jsp:root version="2.0" xmlns:jsp="http://java.sun.com/JSP/Page" xmlns:cms="http://magnolia-cms.com/taglib/templating-components/cms"
  xmlns:c="urn:jsptld:http://java.sun.com/jsp/jstl/core" xmlns:media="http://net.sourceforge.openutils/mgnlMedia"
  xmlns:cmsfn="http://magnolia-cms.com/taglib/templating-components/cmsfn" xmlns:fn="http://java.sun.com/jsp/jstl/functions"
  xmlns:mu="mgnlutils">
  <jsp:directive.attribute name="property" required="true" rtexprvalue="true" type="java.lang.String" />
  <jsp:directive.attribute name="width" required="true" rtexprvalue="true" type="java.lang.Integer" />
  <jsp:directive.attribute name="height" required="true" rtexprvalue="true" type="java.lang.Integer" />
  <jsp:directive.attribute name="item" required="false" rtexprvalue="true" type="java.lang.Object" />
  <jsp:directive.attribute name="node" required="false" rtexprvalue="true" type="java.lang.String" />
  <jsp:directive.attribute name="jqueryui" required="false" rtexprvalue="true" type="java.lang.Boolean" />
  <jsp:directive.attribute name="jquery" required="false" rtexprvalue="true" type="java.lang.Boolean" />
  <jsp:directive.attribute name="loadjs" required="false" rtexprvalue="true" type="java.lang.Boolean" />
  <jsp:directive.attribute name="isEditMode" required="false" rtexprvalue="true" type="java.lang.Boolean" />
  <c:set var="isEditMode" value="${empty isEditMode ? mgnl.editMode and mu:canEdit() : isEditMode}" />
  <c:if test="${isEditMode}">
    <c:set var="uuidBrandLink" value="uuid=${param.uuid}&amp;" />
  </c:if>
  <c:if test="${empty node}">
    <c:set var="node" value="${content.handle}" />
  </c:if>
  <c:set value="${mu:node(node, 'website')}" var="pzcNode" />
  <c:if test="${empty item}">
    <c:set var="item" value="${pzcNode[property]}" />
  </c:if>
  <c:set var="loadjs" value="${empty loadjs ? true : loadjs}" />
  <c:set var="itemNode" value="${media:node(item)}" />
  <c:choose>
    <c:when test="${(media:width(itemNode) ge width) and (media:height(itemNode) ge height)}">
      <c:set var="property" value="${property}_pzc" />
      <c:set var="pzcId" value="${node}.${property}" />
      <c:set var="pzcProperties" value="${pzcNode[property]}" />
      <c:if test="${isEditMode and (empty pzcJsIncluded or not pzcJsIncluded)}">
        <script type="text/javascript"> var crop_app_ctx = '${pageContext.request.contextPath}';</script>
        <c:if test="${loadjs}">
          <c:if test="${empty jquery or jquery}">
            <script type="text/javascript" src="https://ajax.googleapis.com/ajax/libs/jquery/1.5.2/jquery.min.js"><!-- -->
            </script>
          </c:if>
          <c:if test="${empty jqueryui or jqueryui}">
            <script type="text/javascript" src="https://ajax.googleapis.com/ajax/libs/jqueryui/1.8.12/jquery-ui.min.js"><!-- -->
            </script>
          </c:if>
          <script type="text/javascript" src="${pageContext.request.contextPath}/.resources/media/js/crop/crop.js"><!--  -->
          </script>
        </c:if>
        <c:set var="pzcJsIncluded" value="${true}" scope="request" />
      </c:if>
      <c:choose>
        <c:when
          test="${((mgnl.editMode and mu:canEdit()) or (not empty isEditMode and isEditMode)) and param.pzcId eq pzcId}">
          <c:set var="cropScriptRequired" value="${true}" scope="request" />
          <c:set var="size" value="${media:size(media:node(item), 'original')}" />
          <c:if test="${empty pzcProperties}">
            <c:set var="pzcProperties" value="100|0|0" />
          </c:if>
          <c:set var="pzcParams" value="${fn:split(pzcProperties, '|')}" />
          <c:set var="zoom" value="${(0 + pzcParams[0]) / 100}" />
          <c:set var="zoomW" value="${zoom * size[0]}" />
          <c:set var="zoomH" value="${zoom * size[1]}" />
          <c:set var="marginX" value="${size[0] - zoomW}" />
          <c:set var="marginY" value="${size[1] - zoomH}" />
          <c:set var="panX" value="${size[0] - width - pzcParams[1] - marginX}" /><!-- zoomW - width}" /> -->
          <c:set var="panY" value="${size[1] - height - pzcParams[2] - marginY}" />
          <div id="${pzcId}" class="pzc"
            style="width:${width}px;height:${height}px;padding:0;margin:0;border:0;position:relative" data-width="${size[0]}"
            data-height="${size[1]}" data-property="${property}" data-node="${node}">
            <div class="pzcMask" style="overflow:hidden;width:100%;height:100%;padding:0;margin:0;border:0;position:relative">
              <div class="pzcContainment"
                style="width:${2 * size[0] - width}px;height:${2 * size[1] - height}px;left:${width - size[0]}px;top:${height - size[1]}px;padding:0;margin:0;border:0;position:relative">
                <div class="pzcMedia"
                  style="left:${panX}px;top:${panY}px;width:${zoomW}px;height:${zoomH}px;padding: ${marginY}px ${marginX}px;margin:0;border:0;position:absolute">
                  <img src="${appCtx}${media:url(media:node(item))}" style="width:100%;height:100%" />
                </div>
              </div>
            </div>
            <a class="pzcEdit pzcExit" href="${media:replaceParam('pzcId', '')}" title="exit"
              style="position:absolute;right:5px;top:5px;background:#000 url(${appCtx}/.resources/media/icons/crop/exit.png) center center no-repeat;border-radius:4px;-moz-border-radius:4px;-webkit-border-radius:4px;width:24px;height:24px;">
              <!--  -->
            </a>
            <a class="pzcEdit pzcSave" href="${media:replaceParam('pzcId', '')}" title="save"
              style="position:absolute;right:5px;top:34px;background:#000 url(${appCtx}/.resources/media/icons/crop/save.png) center center no-repeat;border-radius:4px;-moz-border-radius:4px;-webkit-border-radius:4px;width:24px;height:24px;">
              <!--  -->
            </a>
            <div class="pzcZoomPanel"
              style="display:none;position:absolute;border:none;padding:10px;bottom:20px;left:50%;margin-left:-20px;width:40px;text-align:center;background:#000;border-radius:4px;-moz-border-radius:4px;-webkit-border-radius:4px;color:#fff;font-size:16px;font-family:Verdana;line-height:20px;height:20px;"><!--  -->
            </div>
            <div class="pzcSlider"
              style="position:absolute;padding:0;margin:5px;width:100px;height:7px;bottom:0px;left:50%;margin-left:-50px;border:solid 1px #999"><!-- -->
            </div>
          </div>
        </c:when>
        <c:otherwise>
          <c:set var="res" value="p${width}x${height}" />
          <c:if test="${not empty pzcProperties}">
            <c:set var="res" value="${res};pzc=${pzcProperties},quality=1" />
          </c:if>
          <c:choose>
            <c:when test="${((mgnl.editMode and mu:canEdit()) or (not empty isEditMode and isEditMode))}">
              <div id="${pzcId}"
                style="overflow:hidden;width:${width}px;height:${height}px;border:0;margin:0;padding:0;position:relative">
                <img src="${appCtx}${media:urlres(media:node(item), res)}" />
                <!--        <media:media item="${item}" width="${width}" height="${height}" resize="crop" />-->
                <a class="pzcEdit" href="${media:replaceParam('pzcId', pzcId)}" title="edit"
                  style="position:absolute;right:5px;top:5px;background:#000 url(${appCtx}/.resources/media/icons/crop/edit.png) center center no-repeat;border-radius:4px;-moz-border-radius:4px;-webkit-border-radius:4px;width:24px;height:24px;">
                  <!--  -->
                </a>
                <c:if test="${not empty pzcProperties}">
                  <a class="pzcEdit pzcDelete" href="${media:replaceParam('pzcId', '')}" title="reset" id="${node}.${property}"
                    style="position:absolute;right:5px;top:34px;background:#000 url(${appCtx}/.resources/media/icons/crop/delete.png) center center no-repeat;border-radius:4px;-moz-border-radius:4px;-webkit-border-radius:4px;width:24px;height:24px;">
                    <!--  -->
                  </a>
                </c:if>
              </div>
            </c:when>
            <c:otherwise>
              <c:set var="res" value="${res}" />
              <img src="${appCtx}${media:urlres(media:node(item), res)}" />
            </c:otherwise>
          </c:choose>
        </c:otherwise>
      </c:choose>
    </c:when>
    <c:otherwise>
      <span
        style="width:${width - 30}px;height:${height - 30}px;position:relative;border:none;display:block;padding:15px;margin:0;font-size:12px;color:#fff;font-family:Arial;background-color:#C0C0C0;">
        Selected media is too small.
        <br />
        Choose media with minimum size of
        <b>${width}x${height}</b>
      </span>
    </c:otherwise>
  </c:choose>
</jsp:root>