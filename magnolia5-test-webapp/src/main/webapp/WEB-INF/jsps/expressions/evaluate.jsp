<jsp:root version="2.1" xmlns:jsp="http://java.sun.com/JSP/Page" xmlns:c="http://java.sun.com/jsp/jstl/core"
  xmlns:fmt="http://java.sun.com/jsp/jstl/fmt" xmlns:fn="http://java.sun.com/jsp/jstl/functions" xmlns:cms="urn:jsptld:cms-taglib"
  xmlns:cmsu="urn:jsptld:cms-util-taglib" xmlns:cmsfn="http://www.magnolia.info/tlds/cmsfn-taglib.tld" xmlns:mexpr="mgnlexpressions">
  <jsp:directive.page contentType="text/html; charset=UTF-8" session="true" />
  <c:catch var="e">
    <![CDATA[${mexpr:evaluate(requestScope['net.sourceforge.openutils.mgnlrules.el.ExpressionsElFunctions.expression'], pageContext)}]]>
  </c:catch>
  <c:if test="${not empty e}">
    <![CDATA[${e.cause.message}]]>
  </c:if>
</jsp:root>