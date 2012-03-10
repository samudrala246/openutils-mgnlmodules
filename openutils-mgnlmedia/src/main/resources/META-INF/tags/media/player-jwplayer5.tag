<jsp:root version="2.0" xmlns:jsp="http://java.sun.com/JSP/Page" xmlns:c="http://java.sun.com/jsp/jstl/core"
  xmlns:cms="cms-taglib" xmlns:cmsfn="http://www.magnolia.info/tlds/cmsfn-taglib.tld" xmlns:cmsu="cms-util-taglib"
  xmlns:fmt="http://java.sun.com/jsp/jstl/fmt" xmlns:fn="http://java.sun.com/jsp/jstl/functions" xmlns:media="http://net.sourceforge.openutils/mgnlMedia"
  xmlns:su="http://openutils.sf.net/openutils-stringutils">
  <jsp:directive.tag description="Renders the audio or video player" pageEncoding="UTF-8" />
  <jsp:directive.attribute name="url" required="true" rtexprvalue="true" type="java.lang.String"
    description="Url for the flv that will be loaded by the player" />
  <jsp:directive.attribute name="preview" required="false" rtexprvalue="true" type="java.lang.String"
    description="Preview image url" />
  <jsp:directive.attribute name="item" required="true" rtexprvalue="true" type="java.lang.Object"
    description="media uuid or media content node " />
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
  <c:if test="${empty requestScope['mgnlmedia_jwplayer5_link_drawn']}">
    <script type="text/javascript" src="${pageContext.request.contextPath}/.resources/media/players/jwplayer5/jwplayer.js"><!-- -->
    </script>
    <c:set var="mgnlmedia_jwplayer5_link_drawn" scope="request" value="true" />
  </c:if>
  <c:set var="previewId" value="preview-${su:randomAlphanumeric(6)}" />
  <div id="${previewId}" class="preview"><!-- -->
  </div>
  <script type="text/javascript">
     <![CDATA[
jwplayer("${previewId}").setup({
autostart: ${autoPlay? 'true' : 'false'},
controlbar: "${controlbar}",
repeat: "${loop? 'always': 'none'}",
icons: "${noPlayIcon? 'false': 'true'}",
width: ${width},
height: ${height},
]]>
    <c:if test="${not empty skin}">skin:"${skin}",</c:if>
    <c:if test="${thumbnail and !empty preview}"> image:"${preview}",</c:if>
    <jsp:text>'plugins': {</jsp:text>
    <c:if test="${analytics}"> 'gapro-2': {},</c:if>
    <c:if test="${share}">
      <c:set var="sharinglink">${baseUrl}${actpage.handle}.html</c:set>
      <c:set var="sharingcode">
        <embed src="${playerPath}" flashvars="file=${fn:contains(url, '://')? '' : baseUrl}${url}" width="${width}"
          height="${height}" />
      </c:set>
      <![CDATA[
      'sharing-3': {
        link: "${sharinglink}",
        code: '${sharingcode}'
        },
      ]]>
    </c:if>
    <jsp:text>},</jsp:text>
    <c:choose>
      <c:when test="${fn:startsWith(url, 'rtmp:') or fn:startsWith(url, 'rtmpt:')}">
        <c:set var="file" value="${fn:split(url, '/')}" />
        <c:set var="file" value="${file[fn:length(file)-1]}" />
              <![CDATA[
              file:"${file}",
              streamer: "${fn:substring(url, 0, fn:length(url) - fn:length(file))}",
              ]]>
      </c:when>
      <c:otherwise> file:"${fn:contains(url, '://')? '' : pageContext.request.contextPath}${url}",</c:otherwise>
    </c:choose>
    

    <![CDATA[
modes: [
  { type: "flash", 
      src: "${pageContext.request.contextPath}/.resources/media/players/jwplayer5/player.swf" 
  },
  { type: "html5" },
  { type: "download" }
]
});
]]>
  </script>
      
     
    <!-- 
    
    
    
     
     
    
    
    
    
     -->
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
  </c:set>
</jsp:root>