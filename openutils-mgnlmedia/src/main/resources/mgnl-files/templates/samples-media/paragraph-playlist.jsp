<jsp:root version="2.0" xmlns:jsp="http://java.sun.com/JSP/Page" xmlns:c="http://java.sun.com/jsp/jstl/core"
  xmlns:fmt="http://java.sun.com/jsp/jstl/fmt" xmlns:fn="http://java.sun.com/jsp/jstl/functions" xmlns:cms="http://magnolia-cms.com/taglib/templating-components/cms"
  xmlns:cmsfn="http://magnolia-cms.com/taglib/templating-components/cmsfn" xmlns:media="http://net.sourceforge.openutils/mgnlMedia"
  xmlns:mu="mgnlutils">
  <jsp:directive.page contentType="text/html; charset=UTF-8" session="false" />
  <div class="textImage">
    <c:set var="playlistNode" value="${mu:node(content.playlist, 'playlists')}" />
    <div>
      <h3>${playlistNode.title}</h3>
      <div>
        <c:forEach var="mediaNode" items="${media:mediaNodesInPlaylist(playlistNode)}">
          <media:media item="${mediaNode}" width="320" height="240" />
        </c:forEach>
      </div>
    </div>
  </div>
</jsp:root>