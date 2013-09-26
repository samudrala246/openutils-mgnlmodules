<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cms" uri="http://magnolia-cms.com/taglib/templating-components/cms"%>
<%@ taglib prefix="cmsfn" uri="http://magnolia-cms.com/taglib/templating-components/cmsfn"%>

<c:set var="flashWidth" value="460"/>
<c:if test="${not empty content.width}">
  <c:set var="flashWidth" value="${content.width}"/>
</c:if>

<c:set var="flashHeight" value="188"/>
<c:if test="${not empty content.height}">
  <c:set var="flashHeight" value="${content.height}"/>
</c:if>

<object data="${cmsfn:link(content.file)}"
 type="application/x-shockwave-flash" width="${flashWidth}" height="${flashHeight}" title="flash" >
</object>
