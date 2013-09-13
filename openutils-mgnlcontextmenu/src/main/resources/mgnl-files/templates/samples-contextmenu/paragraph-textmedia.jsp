<jsp:root version="2.0" xmlns:jsp="http://java.sun.com/JSP/Page" xmlns:c="http://java.sun.com/jsp/jstl/core"
  xmlns:fmt="http://java.sun.com/jsp/jstl/fmt" xmlns:fn="http://java.sun.com/jsp/jstl/functions" xmlns:cms="http://magnolia-cms.com/taglib/templating-components/cms"
  xmlns:cmsu="cms-util-taglib" xmlns:cmsfn="http://magnolia-cms.com/taglib/templating-components/cmsfn" xmlns:media="http://net.sourceforge.openutils/mgnlMedia"
  xmlns:mcmenu="mgnlcontextmenu">
  <jsp:directive.page contentType="text/html; charset=UTF-8" session="false" />
  <pre>
    <![CDATA[&lt;mcmenu:element menu="samples-textmedia"&gt;${'$'}{cmsfn:decode(content).text}&lt;/mcmenu:element&gt;]]>
  </pre>
  <div style="float: left; margin-right: 10px;">
    <mcmenu:element menu="samples-textmedia" wrapper="div">
      <media:media item="${content.media}" width="320" height="240" />
    </mcmenu:element>
  </div>
  <mcmenu:element menu="samples-textmedia" wrapper="div">${cmsfn:decode(content).text}</mcmenu:element>
</jsp:root>