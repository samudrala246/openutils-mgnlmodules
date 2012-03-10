<jsp:root version="2.0" xmlns:jsp="http://java.sun.com/JSP/Page" xmlns:c="http://java.sun.com/jsp/jstl/core"
  xmlns:fmt="http://java.sun.com/jsp/jstl/fmt" xmlns:fn="http://java.sun.com/jsp/jstl/functions"
  xmlns:image="urn:jsptld:http://it.openmindonline/imageTag">
  <!--
    image rendering tag
  -->
  <jsp:directive.attribute name="name" type="java.lang.String" required="true" rtexprvalue="true" />
  <jsp:directive.attribute name="title" type="java.lang.String" required="false" rtexprvalue="true" />
  <jsp:directive.attribute name="altNodeDataName" type="java.lang.String" required="true" rtexprvalue="true" />
  <jsp:directive.attribute name="style" type="java.lang.String" required="false" rtexprvalue="true" />
  <jsp:directive.attribute name="align" type="java.lang.String" required="false" rtexprvalue="true" />
  <jsp:directive.attribute name="classCss" type="java.lang.String" required="false" rtexprvalue="true" />
  <jsp:directive.attribute name="cssClassSqueezeBox" type="java.lang.String" required="false" rtexprvalue="true" />
  <c:set var="globalStyle" value="${style}" />
  <c:if test="${empty align}">
    <c:set var="align" value="left" />
  </c:if>
  <c:set var="globalStyle" value="${globalStyle}; float:${align}" />
  <c:set var="isOriginal" value="${image:checkOriginal(name)}"></c:set>
  <c:set var="description" value="${image:getDescription(name)}"></c:set>
  <div class="imageTag" style="position:relative;padding:0;${globalStyle};">
    <c:if test="${isOriginal}">
      <a class="${cssClassSqueezeBox}" href="${pageContext.request.contextPath}/gallery/${name}/original/data.jpg"
        target="_blank" rel="lightbox[group]">
        <img src="${pageContext.request.contextPath}/gallery/${name}/image/data.jpg" alt="${altNodeDataName}"
          class="${classCss}" title="${description}" />
        <img class="zoom" src="${pageContext.request.contextPath}/theme/lente/data.png" />
      </a>
    </c:if>
    <c:if test="${not isOriginal}">
      <img src="${pageContext.request.contextPath}/gallery/${name}/image/data.jpg" alt="${altNodeDataName}"
        class="${classCss}" title="${description}" />
    </c:if>
  </div>
</jsp:root>