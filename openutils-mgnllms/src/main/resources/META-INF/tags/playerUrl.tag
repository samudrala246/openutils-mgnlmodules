<jsp:root version="2.1" xmlns:jsp="http://java.sun.com/JSP/Page" xmlns:c="http://java.sun.com/jsp/jstl/core"
  xmlns:cms="cms-taglib" xmlns:cmsfn="http://www.magnolia.info/tlds/cmsfn-taglib.tld" xmlns:cmsu="cms-util-taglib"
  xmlns:fmt="http://java.sun.com/jsp/jstl/fmt" xmlns:fn="http://java.sun.com/jsp/jstl/functions" xmlns:lmsfn="urn:jsptld:lms-fn">
  <jsp:directive.attribute name="course" required="true" rtexprvalue="true"
    type="info.magnolia.cms.core.Content" description="Course node" />
  <jsp:directive.attribute name="target" required="false" rtexprvalue="true" type="java.lang.String"
    description="target" />
  <jsp:directive.attribute name="classCss" required="false" rtexprvalue="true" type="java.lang.String"
    description="css class" />
  <c:set var="url"><![CDATA[${pageContext.request.contextPath}/mgnllms/player${course.handle}]]></c:set>
  <c:if test="${not lmsfn:isCourseSatisfied(course.UUID)}">
    <c:choose>
      <c:when test="${target eq 'popup'}">
        <![CDATA[<a href="${url}" onclick="window.open(${url});return false;" target="_blank">]]>
      </c:when>
      <c:otherwise>
  <![CDATA[<a href="${url}" target="_blank" class="${classCss}" >]]>
      </c:otherwise>
    </c:choose>
  </c:if>
  ${course.title}
  <c:choose>
    <c:when test="${not lmsfn:isCourseSatisfied(course.UUID)}">
      <![CDATA[</a>]]>
    </c:when>
    <c:otherwise> - Done</c:otherwise>
  </c:choose>
</jsp:root>