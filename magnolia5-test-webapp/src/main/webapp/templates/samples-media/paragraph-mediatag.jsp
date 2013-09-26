<jsp:root version="2.0" xmlns:jsp="http://java.sun.com/JSP/Page" xmlns:c="http://java.sun.com/jsp/jstl/core"
  xmlns:fmt="http://java.sun.com/jsp/jstl/fmt" xmlns:fn="http://java.sun.com/jsp/jstl/functions" xmlns:cms="http://magnolia-cms.com/taglib/templating-components/cms"
  xmlns:cmsfn="http://magnolia-cms.com/taglib/templating-components/cmsfn" xmlns:media="http://net.sourceforge.openutils/mgnlMedia"
  xmlns:mu="mgnlutils">
  <jsp:directive.page contentType="text/html; charset=UTF-8" session="false" />
  <div class="textImage">
    <c:set var="mediaNode" value="${media:node(content.media)}" />
    <div>
      <h3>${mediaNode.description}</h3>
      <div>
        <pre>&amp;lt;media:media property="media" width="50" height="30" videoImagePreview="true" /></pre>
        <media:media property="media" width="50" height="30" videoImagePreview="true" />
        
        <br/>
        <pre>&amp;lt;media:media property="media" thumbnail="true" /></pre>
        <media:media property="media" thumbnail="true" />

      </div>
    </div>
  </div>
</jsp:root>