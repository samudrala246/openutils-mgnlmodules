<jsp:root version="2.0" xmlns:jsp="http://java.sun.com/JSP/Page" xmlns:c="http://java.sun.com/jsp/jstl/core"
  xmlns:cms="cms-taglib" xmlns:cmsfn="http://www.magnolia.info/tlds/cmsfn-taglib.tld" xmlns:cmsu="cms-util-taglib"
  xmlns:fmt="http://java.sun.com/jsp/jstl/fmt" xmlns:fn="http://java.sun.com/jsp/jstl/functions" xmlns:media="http://net.sourceforge.openutils/mgnlMedia"
  xmlns:su="http://openutils.sf.net/openutils-stringutils">
  <jsp:directive.tag description="Displays a media" pageEncoding="UTF-8"/>
  <jsp:directive.attribute name="id" required="false" rtexprvalue="true" description="html element id"/>
  <jsp:directive.attribute name="item" required="false" rtexprvalue="true" type="java.lang.Object" description="media uuid or media content node; if not specified, 'node' and 'property' attributes will be used"/>
  <jsp:directive.attribute name="node" required="false" rtexprvalue="true" type="info.magnolia.cms.core.Content" description="the content object to use; if not specified, object 'content' will be used"/>
  <jsp:directive.attribute name="property" required="false" rtexprvalue="true" type="java.lang.String" description="the name of the nodeData storing the media uuid; if not specified, 'media' will be used"/>
  <jsp:directive.attribute name="player" required="false" rtexprvalue="true" type="java.lang.String" description="the swf file of the player (examples: player.swf (default), jwplayer4, jwplayer5, jwplayer5/player.swf, /.resources/media/players/jwplayer5/player.swf)"/>
  <jsp:directive.attribute name="width" required="false" rtexprvalue="true" type="java.lang.Integer" description="image or player width"/>
  <jsp:directive.attribute name="height" required="false" rtexprvalue="true" type="java.lang.Integer" description="image or player height"/>
  <jsp:directive.attribute name="useEm" required="false" rtexprvalue="true" type="java.lang.Boolean" description="if true, and ignoreDimensions is not true it will apply style attribute on img tags with sidth and height in 'em'. the em values will be computed using as width in pixel * emRatio (see next attribute)"/>
  <jsp:directive.attribute name="emRatio" required="false" rtexprvalue="true" type="java.lang.Float" description="float value to convert dimensions from px to em "/>
  <jsp:directive.attribute name="alt" required="false" rtexprvalue="true" description="alt / title on images. If not specified, the title of the media (if present) will be used"/>
  <jsp:directive.attribute name="cssClass" required="false" rtexprvalue="true" description="cssClass to be applied on html element"/>
  <jsp:directive.attribute name="style" required="false" rtexprvalue="true" description="style to be applied on html element "/>
  <jsp:directive.attribute name="resize" required="false" rtexprvalue="true" description="[fit|nocrop|crop|fitbands]:
fit: makes the new image to fit into required resolution.
nocrop: makes the new image to contain the required resolution.
crop: makes the new image to contain the required res and the crop the simmetric bands that outfit resolution.
fitbands: makes the new image to fit into required res and fills empty areas with background color you pass to in 'parameter' attribute as hex value of 'background' parameter.
pzc: apply zoom and crop to image as specified by the pzc parameter (value must be built with the pattern zoom|pan x|pan y, i.e pzc:80|220|125). If no pzc parameter is found the crop mode is applied to image."/>

  <jsp:directive.attribute name="parameters" required="false" rtexprvalue="true" description="parameters to pass to image processor as couples key=value joined by commas "/>
  <jsp:directive.attribute name="ignoreDim" required="false" rtexprvalue="true" type="java.lang.Boolean" description="if true the img element will be rendered without width and height attributes "/>
  <jsp:directive.attribute name="autoPlay" required="false" rtexprvalue="true" type="java.lang.Boolean" description="auto starts the player without waiting for user play command"/>
  <jsp:directive.attribute name="loop" required="false" rtexprvalue="true" type="java.lang.Boolean" description="loop video or audio"/>
  <jsp:directive.attribute name="noPlayIcon" required="false" rtexprvalue="true" type="java.lang.Boolean" description="hide player icons"/>
  <jsp:directive.attribute name="skin" required="false" rtexprvalue="true" description="set player skin"/>
  <jsp:directive.attribute name="thumbnail" required="false" rtexprvalue="true" type="java.lang.Boolean" description="if set to true the preview of the player shows the thumbnail image"/>
  <jsp:directive.attribute name="videoImagePreview" required="false" rtexprvalue="true" type="java.lang.Boolean" description="if set to true, the tag will not insert the player for videos but only a preview image. For videos or mp3s it needs the following scripts loaded in page: &lt;ul>&lt;li>.resources/media/js/mootools-1.2-core.js&lt;/li>&lt;li>.resources/media/js/mootools-1.2-more.js&lt;/li>&lt;li>.resources/media/js/mootools-1.2-swfobject.js&lt;/li>&lt;/ul>"/>
  <jsp:directive.attribute name="controlbar" required="false" rtexprvalue="true" type="java.lang.String" description="defines controlbar position. Possible values are: 'none' (for hiding), 'over', 'bottom', 'top'. If not set, this value will be 'bottom' by default." />
  <jsp:directive.attribute name="share" required="false" rtexprvalue="true" type="java.lang.Boolean" description="Adds the share features to the player (only supported for jwplayer 4/5)" />
  <jsp:directive.attribute name="analytics" required="false" rtexprvalue="true" type="java.lang.Boolean" description="Track views using google analytics (only supported for jwplayer 5)" />
  <jsp:directive.attribute name="crop" required="false" rtexprvalue="true" type="java.lang.Boolean" description="if true, the zoom and crop mode is enabled on the media. The cropProperty attribute is mandatory"/>
  <jsp:directive.attribute name="cropProperty" required="false" rtexprvalue="true" type="java.lang.String" description="the name of the property where (with '_pzc' suffix) zoom and crop informations are saved "/>
  <jsp:directive.attribute name="cropJs" required="false" rtexprvalue="true" type="java.lang.Boolean" description="whether to load crop js files. Defaults to true. If false you have to manually include jquery, jquery ui and (before closing body tag) /.resources/media/js/crop/crop.js" />
  <jsp:directive.attribute name="cropJquery" required="false" rtexprvalue="true" type="java.lang.Boolean" description="whether to load jquery library or not. Defaults to true; if cropJs is false jquery is not loaded" />
  <jsp:directive.attribute name="cropJqueryUI" required="false" rtexprvalue="true" type="java.lang.Boolean" description="whether to load jquery UI library or not. Defaults to true; if cropJs is false jquery ui is not loaded" />

  <c:if test="${empty item}">
    <c:if test="${empty node}">
      <c:set var="node" value="${content}" />
    </c:if>
    <c:if test="${empty property}">
      <c:set var="property" value="media" />
    </c:if>
    <cms:out var="item" contentNode="${node}" nodeDataName="${property}" />
    <c:if test="${empty width}">
      <cms:out var="width" contentNode="${node}" nodeDataName="${property}_width" />
    </c:if>
    <c:if test="${empty height}">
      <cms:out var="height" contentNode="${node}" nodeDataName="${property}_height" />
    </c:if>
  </c:if>
  <c:set value="${10000}" var="bigValue" />
  <c:if test="${empty width }">
    <c:set var="width" value="${0}" />
  </c:if>
  <c:if test="${empty height }">
    <c:set var="height" value="${0}" />
  </c:if>
  <c:if test="${empty emRatio }">
    <c:set var="emRatio" value="${1.0}" />
  </c:if>
  <c:if test="${empty player}">
    <c:set var="mediamodule" value="${media:module()}"/>
    <c:set var="player" value="${mediamodule.player}" />
  </c:if>
  <c:set var="crop" value="${empty crop ? false : crop}" />
  <c:set value="${media:node(item)}" var="mediaNode" />
  <c:choose>
    <c:when test="${!empty mediaNode}">
      <cms:setNode var="media" content="${mediaNode}" />
      <c:choose>
        <c:when test="${crop and (media.type eq 'image' or media.type eq 'wallpaper' or videoImagePreview)}">
          <media:crop item="${media}" width="${width}" height="${height}" property="${cropProperty}" jquery="${cropJquery}" jqueryui="${cropJqueryUI}" loadjs="${cropJs}" />
        </c:when>
        <c:when test="${media.type eq 'image' or media.type eq 'wallpaper' or videoImagePreview}">
          <c:choose>
            <c:when test="${width eq 0 and height eq 0}">
              <c:set var="url" value="${media:url(mediaNode)}" />
              <c:set var="size" value="${media:size(mediaNode, 'original')}" />
            </c:when>
            <c:otherwise>
              <c:set var="controlChar" value="" />
              <c:choose>
                <c:when test="${resize eq 'nocrop'}">
                  <c:set var="controlChar" value="n" />
                </c:when>
                <c:when test="${resize eq 'fitbands'}">
                  <c:set var="controlChar" value="o" />
                </c:when>
                <c:when test="${resize eq 'fit'}">
                  <c:set var="controlChar" value="l" />
                </c:when>
                <c:when test="${resize eq 'pzc'}">
                  <c:set var="controlChar" value="p" />
                </c:when>
              </c:choose>
              <c:if test="${width eq 0}">
                <c:set var="controlChar" value="l" />
                <c:set var="width" value="${bigValue}" />
              </c:if>
              <c:if test="${height eq 0}">
                <c:set var="controlChar" value="l" />
                <c:set var="height" value="${bigValue}" />
              </c:if>
              <c:set var="resolution">${controlChar}${width}x${height}${empty parameters ? '': ';'}${parameters}</c:set>
              <c:set var="url" value="${media:urlres(mediaNode, resolution)}" />
              <c:set var="size" value="${media:size(mediaNode, resolution)}" />
            </c:otherwise>
          </c:choose>
          <c:if test="${!empty(url)}">
            <c:if test="${empty alt}">
              <c:set var="alt" value="${mediaNode.title}" />
            </c:if>
            <![CDATA[<img src="${pageContext.request.contextPath}${url}" alt="${alt}" title="${alt}"]]>
            <c:if test="${not empty id}">
              <![CDATA[ id="${id}"]]>
            </c:if>
            <c:if test="${not ignoreDim}">
              <c:if test="${ size[0] gt 0}">
                <c:set var="style">${style}${(!empty style)?';':''}width:${size[0]*emRatio}${(not useEm ? 'px':'em')}
                </c:set>
              </c:if>
              <c:if test="${ size[1] gt 1}">
                <c:set var="style">${style}${(!empty style)?';':''}height:${size[1]*emRatio}${(not useEm ? 'px':'em')}
                </c:set>
              </c:if>
            </c:if>
            <c:if test="${not empty cssClass}">
              <![CDATA[ class="${cssClass}"]]>
            </c:if>
            <c:if test="${not empty style}">
              <![CDATA[ style="${style}"]]>
            </c:if>
            <![CDATA[ />]]>
          </c:if>
        </c:when>
        <c:when test="${media.type eq 'video' or media.type eq 'audio' or media.type eq 'youtube' }">
          <c:set var="url" value="${media:url(mediaNode)}"/>
          
          
          
          
          
          
          
          
          
    <c:choose>
      <c:when test="${su:endsWith(fn:toLowerCase(url), 'swf')}">
        <media:swfobject player="${url}" width="${width}" height="${height}" />
      </c:when>
      <c:otherwise>
      
        <media:player url="${url}"
                        preview="${preview}"
                        player="${player}"
                        share="${share}"
                        item="${item}"
                        width="${width}"
                        height="${height}"
                        autoPlay="${autoPlay}"
                        loop="${loop}"  
                        thumbnail="${thumbnail}"
                        noPlayIcon="${noPlayIcon}"
                        controlbar="${controlbar}"
                        skin="${skin}"
                        analytics="${analytics}" />
        
      </c:otherwise>
    </c:choose>
          
          
         
        </c:when>
        <c:when test="${media.type eq 'swf' }">
        
          <c:if test="${!(width gt 0)}">
            <c:set var="width" value="${media:width(mediaNode)}" />
          </c:if>
          <c:if test="${!(height gt 0)}">
            <c:set var="height" value="${media:height(mediaNode)}" />
          </c:if>
          
          <c:choose>
            <c:when test="${width gt 0 and height gt 0}">
              <c:set var="previewres" value="${width}x${height}" />
              <c:set var="preview" value="${media:urlres(mediaNode, previewres)}" />
            </c:when>
            <c:otherwise>
              <c:set var="preview" value="${media:thumbnail(mediaNode)}" />
            </c:otherwise>
          </c:choose>
          
          <c:if test="${!empty preview}">
             <c:set var="preview" value="${pageContext.request.contextPath}${preview}" />
          </c:if>
    
          <media:swfobject 
            player="${pageContext.request.contextPath}${media:url(mediaNode)}"
            width="${width gt 0 ? width : ''}"
            height="${height gt 0? height : ''}" 
            image="${preview}"
            title="${mediaNode.title}"
            />
        </c:when>
         
      </c:choose>
    </c:when>
    <c:when test="${!empty property and fn:startsWith(item, '/')}">
      <!-- fn:startsWith() is needed to avoid passing an uuid to cmsu:img. See MAGNOLIA-3196, don't call the image tag with non-binary properties -->
      <cmsu:img contentNode="${node}" nodeDataName="${property}" width="${width gt 0 ? width : ''}" height="${height gt 0? height : ''}" />
      <![CDATA[<!-- missing media: ${item} -->]]>
    </c:when>
    <c:when test="${fn:startsWith(item, '/')}">
      <![CDATA[<img src="${pageContext.request.contextPath}${item}" alt="" width="${width gt 0 ? width : ''}" height="${height gt 0? height : ''}" />]]>
    </c:when>
    <c:otherwise>
      <![CDATA[<!-- missing media: ${item} -->]]>
    </c:otherwise>
  </c:choose>
</jsp:root>
