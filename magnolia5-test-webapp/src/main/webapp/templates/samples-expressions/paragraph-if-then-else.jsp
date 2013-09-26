<jsp:root version="2.0" xmlns:jsp="http://java.sun.com/JSP/Page" xmlns:cms="urn:jsptld:cms-taglib"
  xmlns:c="urn:jsptld:http://java.sun.com/jsp/jstl/core" xmlns:fmt="urn:jsptld:http://java.sun.com/jsp/jstl/fmt"
  xmlns:fn="urn:jsptld:http://java.sun.com/jsp/jstl/functions" xmlns:cmsu="urn:jsptld:cms-util-taglib"
  xmlns:cmsfn="http://www.magnolia.info/tlds/cmsfn-taglib.tld" xmlns:media="http://net.sourceforge.openutils/mgnlMedia"
  xmlns:mu="mgnlutils" xmlns:mexpr="mgnlexpressions">
  <jsp:directive.page contentType="text/html; charset=UTF-8" session="false" />
  
  <pre>
    <![CDATA[${'$'}{mexpr:evaluate('${content.ifCondition}', pageContext) ? '${fn:escapeXml(content.thenText)}' : '${fn:escapeXml(content.elseText)}'}]]>
  </pre>
  <![CDATA[${mexpr:evaluate(content.ifCondition, pageContext) ? content.thenText : content.elseText}]]>
</jsp:root>