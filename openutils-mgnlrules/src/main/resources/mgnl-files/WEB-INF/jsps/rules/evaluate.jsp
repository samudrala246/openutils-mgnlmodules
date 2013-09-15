<jsp:root version="2.0" xmlns:jsp="http://java.sun.com/JSP/Page" xmlns:c="http://java.sun.com/jsp/jstl/core"
  xmlns:fmt="http://java.sun.com/jsp/jstl/fmt" xmlns:fn="http://java.sun.com/jsp/jstl/functions" xmlns:cms="http://magnolia-cms.com/taglib/templating-components/cms"
  xmlns:cmsfn="http://magnolia-cms.com/taglib/templating-components/cmsfn" xmlns:media="http://net.sourceforge.openutils/mgnlMedia"
  xmlns:mu="mgnlutils" xmlns:rules="mgnlrules">
  <jsp:directive.page contentType="text/html; charset=UTF-8" session="true" />
  <c:catch var="e">
    <![CDATA[${rules:evaluate(requestScope['net.sourceforge.openutils.mgnlrules.el.ExpressionsElFunctions.expression'], pageContext)}]]>
  </c:catch>
  <c:if test="${not empty e}">
    <![CDATA[${e.cause.message}]]>
  </c:if>
</jsp:root>