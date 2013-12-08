<jsp:root version="2.1" xmlns:jsp="http://java.sun.com/JSP/Page" xmlns:c="http://java.sun.com/jsp/jstl/core"
  xmlns:cms="http://magnolia-cms.com/taglib/templating-components/cms" xmlns:cmsfn="http://magnolia-cms.com/taglib/templating-components/cmsfn" xmlns:cmsu="cms-util-taglib"
  xmlns:fmt="http://java.sun.com/jsp/jstl/fmt" xmlns:fn="http://java.sun.com/jsp/jstl/functions" xmlns:media="http://net.sourceforge.openutils/mgnlMedia"
  xmlns:su="http://openutils.sf.net/openutils-stringutils">
  <jsp:directive.attribute name="url" required="true" rtexprvalue="true" type="java.lang.String"
    description="Url for the flv that will be loaded by the player" />
  <jsp:directive.attribute name="preview" required="false" rtexprvalue="true" type="java.lang.String"
    description="Preview image url" />
  <jsp:directive.attribute name="item" required="true" rtexprvalue="true" type="java.lang.Object"
    description="media uuid or media content node " />
  <jsp:directive.attribute name="playerPath" required="true" rtexprvalue="true" type="java.lang.String"
    description="the swf file of the player (examples: /.resources/media/players/someplayer/player.swf)" />
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
    description="Adds the share features to the player (copy page url and embed code)" />
  <c:set var="flashvars">
    <c:choose>
      <c:when test="${fn:startsWith(url, 'rtmp:') or fn:startsWith(url, 'rtmpt:')}">
        <c:set var="file" value="${fn:split(url, '/')}" />
        <c:set var="file" value="${file[fn:length(file)-1]}" />
              <![CDATA[file=${file}&amp;streamer=${fn:substring(url, 0, fn:length(url) - fn:length(file))}]]>
      </c:when>
      <c:otherwise>
              <![CDATA[file=${fn:contains(url, '://')? '' : pageContext.request.contextPath}${url}]]>
      </c:otherwise>
    </c:choose>
    <c:if test="${fn:startsWith(url, 'http://www.youtube')}">
            <![CDATA[&amp;type=youtube]]>
    </c:if>
    <c:if test="${!empty controlbar}">
            <![CDATA[&amp;controlbar=${controlbar}]]>
    </c:if>
    <c:if test="${autoPlay}">
            <![CDATA[&amp;autostart=true]]>
    </c:if>
    <c:if test="${loop}">
            <![CDATA[&amp;repeat=always]]>
    </c:if>
    <c:if test="${noPlayIcon}">
            <![CDATA[&amp;icons=false]]>
    </c:if>
    <c:if test="${not empty skin}">
            <![CDATA[&amp;skin=${skin}]]>
    </c:if>
    <c:if test="${thumbnail and !empty preview}">
            <![CDATA[&amp;image=${preview}]]>
    </c:if>
    <c:if test="${share}">
      <c:set var="sharinglink">${baseUrl}${actpage.handle}.html</c:set>
      <c:set var="sharingcode">
        <embed src="${playerPath}" flashvars="file=${fn:contains(url, '://')? '' : baseUrl}${url}" width="${width}"
          height="${height}" />
      </c:set>
            <![CDATA[&amp;plugins=sharing-1]]>
            <![CDATA[&amp;sharing.link=${sharinglink}]]>
            <![CDATA[&amp;sharing.code=${fn:escapeXml(sharingcode)}]]>
    </c:if>
  </c:set>
  <media:swfobject player="${playerPath}" width="${width}" height="${height}" allowfullscreen="true"
    allowscriptaccess="always" wmode="opaque" flashvars="${flashvars}" />
</jsp:root>