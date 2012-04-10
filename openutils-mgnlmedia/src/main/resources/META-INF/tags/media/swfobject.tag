<jsp:root version="2.0" xmlns:jsp="http://java.sun.com/JSP/Page" xmlns:c="http://java.sun.com/jsp/jstl/core"
  xmlns:cms="http://magnolia-cms.com/taglib/templating-components/cms" xmlns:cmsfn="http://magnolia-cms.com/taglib/templating-components/cmsfn" xmlns:cmsu="cms-util-taglib"
  xmlns:fmt="http://java.sun.com/jsp/jstl/fmt" xmlns:fn="http://java.sun.com/jsp/jstl/functions" xmlns:media="http://net.sourceforge.openutils/mgnlMedia"
  xmlns:su="http://openutils.sf.net/openutils-stringutils">
  <jsp:directive.tag pageEncoding="UTF-8" description="Renders the flash-content and the javascript for replacing it" />
  <jsp:directive.attribute name="player" required="true" rtexprvalue="true" type="java.lang.String" description="the swf file of the player (examples: player.swf (default), jwplayer4, jwplayer5, jwplayer5/player.swf, /.resources/media/players/jwplayer5/player.swf)" />
  <jsp:directive.attribute name="width" required="false" rtexprvalue="true" type="java.lang.Integer" description="flash content width" />
  <jsp:directive.attribute name="height" required="false" rtexprvalue="true" type="java.lang.Integer" description="flash content height" />
  <jsp:directive.attribute name="image" required="false" rtexprvalue="true" description="replacement image" />
  <jsp:directive.attribute name="title" required="false" rtexprvalue="true" description="title/alt" />
  <jsp:directive.tag dynamic-attributes="attrs" />
  <!-- end attributes -->


  <c:set var="debug" value="${false}"/><!-- set to true to print out the generated javascript on page -->
  <c:set var="previewId" value="preview-${su:randomAlphanumeric(6)}" />
  <div id="${previewId}" class="preview"><!-- -->
    <c:if test="${!empty image}">
      <img src="${image}" width="${width}" height="${height}" alt="${title}" />
    </c:if>
  </div>
  <c:if test="${empty requestScope['mgnlmedia_swfobject_link_drawn']}">
    <script type="text/javascript" src="${pageContext.request.contextPath}/.resources/media/js/swfobject.js"><!-- -->
    </script>
    <c:set var="mgnlmedia_swfobject_link_drawn" scope="request" value="true" />
  </c:if>

  <script type='text/javascript'>
    <![CDATA[
    var swfobject = new SWFObject('${player}','player','${width}','${height}','9','#ffffff');]]>
    <c:forEach var="attr" items="${attrs}">
      <c:if test="${!empty attr.value}">
        <![CDATA[
    swfobject.addParam(${su:escJsTxt(attr.key, false)},${su:escJsTxt(attr.value, false)});]]>
      </c:if>
    </c:forEach>
    <![CDATA[
    var version = deconcept.SWFObjectUtil.getPlayerVersion();
    if (!(version["major"] == 0 || version["major"] < 9)) {
      swfobject.write('${previewId}');
    }]]>
  </script>
  <c:if test="${debug}">
    <pre>
    <![CDATA[
    var swfobject = new SWFObject('${fn:startsWith(player, '/')? pageContext.request.contextPath:''}${player}','player','${width}','${height}','9','#ffffff');]]>
    <c:forEach var="attr" items="${attrs}">
      <c:if test="${!empty attr.value}">
        <![CDATA[
    swfobject.addParam(${su:escJsTxt(attr.key, false)},${su:escJsTxt(attr.value, false)});]]>
      </c:if>
    </c:forEach>
    <![CDATA[
    var version = deconcept.SWFObjectUtil.getPlayerVersion();
    if (!(version["major"] == 0 || version["major"] < 9)) {
      swfobject.write('${previewId}');
    }]]>
    </pre>
  </c:if>
  
  
</jsp:root>