<jsp:root version="2.0" xmlns:jsp="http://java.sun.com/JSP/Page" xmlns:cms="urn:jsptld:cms-taglib"
  xmlns:c="urn:jsptld:http://java.sun.com/jsp/jstl/core" xmlns:fmt="urn:jsptld:http://java.sun.com/jsp/jstl/fmt"
  xmlns:fn="urn:jsptld:http://java.sun.com/jsp/jstl/functions" xmlns:cmsu="urn:jsptld:cms-util-taglib"
  xmlns:cmsfn="http://www.magnolia.info/tlds/cmsfn-taglib.tld" xmlns:media="http://net.sourceforge.openutils/mgnlMedia"
  xmlns:mu="mgnlutils" xmlns:mcmenu="mgnlcontextmenu">
  <jsp:directive.page contentType="text/html; charset=UTF-8" session="false" />
  <pre>
    <![CDATA[&lt;mcmenu:element menu="samples-textmedia"&gt;${'$'}{node.text}&lt;/mcmenu:element&gt;]]>
  </pre>
  <cms:setNode var="node" />
  <div style="float: left; margin-right: 10px;">
    <mcmenu:element menu="samples-textmedia" wrapper="div">
      <media:media item="${node.media}" width="320" height="240" />
    </mcmenu:element>
  </div>
  <mcmenu:element menu="samples-textmedia" wrapper="div">${node.text}</mcmenu:element>
</jsp:root>