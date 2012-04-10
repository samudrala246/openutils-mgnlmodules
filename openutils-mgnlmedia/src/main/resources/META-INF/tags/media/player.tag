<jsp:root version="2.0" xmlns:jsp="http://java.sun.com/JSP/Page" xmlns:c="http://java.sun.com/jsp/jstl/core"
  xmlns:cms="http://magnolia-cms.com/taglib/templating-components/cms" xmlns:cmsfn="http://magnolia-cms.com/taglib/templating-components/cmsfn" xmlns:cmsu="cms-util-taglib"
  xmlns:fmt="http://java.sun.com/jsp/jstl/fmt" xmlns:fn="http://java.sun.com/jsp/jstl/functions" xmlns:media="http://net.sourceforge.openutils/mgnlMedia"
  xmlns:su="http://openutils.sf.net/openutils-stringutils">
  <jsp:directive.tag description="Renders the audio or video player" pageEncoding="UTF-8" />
  <jsp:directive.attribute name="url" required="true" rtexprvalue="true" type="java.lang.String"
    description="Url for the flv that will be loaded by the player" />
  <jsp:directive.attribute name="preview" required="false" rtexprvalue="true" type="java.lang.String"
    description="Preview image url" />
  <jsp:directive.attribute name="item" required="true" rtexprvalue="true" type="java.lang.Object"
    description="media uuid or media content node " />
  <jsp:directive.attribute name="player" required="true" rtexprvalue="true" type="java.lang.String"
    description="the swf file of the player (examples: player.swf (default), jwplayer4, jwplayer5, jwplayer5/player.swf, /.resources/media/players/jwplayer5/player.swf)" />
  <jsp:directive.attribute name="width" required="false" rtexprvalue="true" type="java.lang.Integer"
    description="image or player width" />
  <jsp:directive.attribute name="height" required="false" rtexprvalue="true" type="java.lang.Integer"
    description="image or player height" />
  <jsp:directive.attribute name="ignoreDim" required="false" rtexprvalue="true" type="java.lang.Boolean"
    description="if true the img element will be rendered without width and height attributes " />
  <jsp:directive.attribute name="autoPlay" required="false" rtexprvalue="true" type="java.lang.Boolean"
    description="auto starts the player without waiting for user play command" />
  <jsp:directive.attribute name="loop" required="false" rtexprvalue="true" type="java.lang.Boolean"
    description="loop video or audio" />
  <jsp:directive.attribute name="noPlayIcon" required="false" rtexprvalue="true" type="java.lang.Boolean"
    description="hide player icons" />
  <jsp:directive.attribute name="skin" required="false" rtexprvalue="true" description="set player skin" />
  <jsp:directive.attribute name="thumbnail" required="false" rtexprvalue="true" type="java.lang.Boolean"
    description="if set to true the preview of the player shows the thumbnail image" />
  <jsp:directive.attribute name="controlbar" required="false" rtexprvalue="true" type="java.lang.String"
    description="defines controlbar position. Possible values are: 'none' (for hiding), 'over', 'bottom', 'top'. If not set, this value will be 'bottom' by default." />
  <jsp:directive.attribute name="share" required="false" rtexprvalue="true" type="java.lang.Boolean"
    description="Adds the share features to the player (only supported for jwplayer 4/5)" />
  <jsp:directive.attribute name="analytics" required="false" rtexprvalue="true" type="java.lang.Boolean"
    description="Track views using google analytics (only supported for jwplayer 5)" />
  <!-- end attributes -->
  <c:set value="${media:node(item)}" var="mediaNode" />
  <c:if test="${empty controlbar}">
    <c:set var="controlbar" value="bottom" />
  </c:if>
  <!-- end defaults -->
  <c:set var="baseUrl" scope="request">
    <jsp:text>${pageContext.request.scheme}://${pageContext.request.serverName}</jsp:text>
    <c:if test="${(pageContext.request.scheme eq 'http' and pageContext.request.serverPort != 80)}">:${pageContext.request.serverPort}</c:if>
    <jsp:text>${pageContext.request.contextPath}</jsp:text>
  </c:set>
  <c:if test="${empty width or width eq 0 }">
    <c:set var="width" value="${media:width(mediaNode)}" />
  </c:if>
  <c:if test="${empty height or height eq 0}">
    <c:set var="height" value="${media:height(mediaNode)}" />
    <c:if test="${(controlbar eq 'bottom' or controlbar eq 'top') and height gt 20}">
      <!-- the control bar adds 20px -->
      <c:set var="height" value="${height + 20}" />
    </c:if>
  </c:if>
  <c:if test="${empty width or width eq 0 }">
    <c:set var="width" value="320" />
  </c:if>
  <c:if test="${empty height or height eq 0}">
    <c:set var="height" value="${media.type eq 'audio' ? 20 : 240}" />
  </c:if>
  <c:set var="previewwidth" value="${width}" />
  <c:set var="previewheight" value="${height}" />
  <c:if test="${previewheight gt 20 and (controlbar eq 'bottom' or controlbar eq 'top')}">
    <c:set var="previewheight" value="${previewheight - 20}" />
  </c:if>
  <c:if test="${thumbnail and empty preview}">
    <c:choose>
      <c:when test="${previewwidth gt 0 and previewheight gt 0}">
        <c:set var="previewres" value="${previewwidth}x${previewheight}" />
        <c:set var="preview" value="${pageContext.request.contextPath}${media:urlres(mediaNode, previewres)}" />
      </c:when>
      <c:otherwise>
        <c:set var="preview" value="${pageContext.request.contextPath}${media:thumbnail(mediaNode)}" />
      </c:otherwise>
    </c:choose>
  </c:if>
  <c:if test="${!empty(url)}">
    <c:choose>
      <c:when test="${player eq 'jwplayer5' or player eq 'jwplayer'}">
        <media:player-jwplayer5 url="${url}" preview="${preview}" share="${share}"
          item="${item}" width="${width}" height="${height}" autoPlay="${autoPlay}" loop="${loop}" thumbnail="${thumbnail}"
          noPlayIcon="${noPlayIcon}" controlbar="${controlbar}" skin="${skin}" analytics="${analytics}"/>
      </c:when>
      <c:otherwise>
        <c:choose>
          <c:when test="${player eq 'jwplayer4'}">
            <c:set var="player" value="jwplayer4/player.swf" />
          </c:when>
        </c:choose>
        <c:set var="playerPath">
          <c:choose>
            <c:when test="${!fn:startsWith(player, '/')}">${pageContext.request.contextPath}/.resources/media/players/${player}</c:when>
          </c:choose>
        </c:set>
        <media:player-genericswfobject url="${url}" preview="${preview}" playerPath="${playerPath}"
          share="${share}" item="${item}" width="${width}" height="${height}" autoPlay="${autoPlay}" loop="${loop}"
          thumbnail="${thumbnail}" noPlayIcon="${noPlayIcon}" controlbar="${controlbar}" skin="${skin}" />
      </c:otherwise>
    </c:choose>
  </c:if>
</jsp:root>