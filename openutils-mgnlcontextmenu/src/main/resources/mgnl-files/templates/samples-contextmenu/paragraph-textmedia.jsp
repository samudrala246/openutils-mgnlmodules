<jsp:root version="2.0" xmlns:jsp="http://java.sun.com/JSP/Page" xmlns:c="http://java.sun.com/jsp/jstl/core"
  xmlns:fmt="http://java.sun.com/jsp/jstl/fmt" xmlns:fn="http://java.sun.com/jsp/jstl/functions" xmlns:cms="http://magnolia-cms.com/taglib/templating-components/cms"
  xmlns:cmsu="cms-util-taglib" xmlns:cmsfn="http://magnolia-cms.com/taglib/templating-components/cmsfn" xmlns:media="http://net.sourceforge.openutils/mgnlMedia"
  xmlns:contextmenu="mgnlcontextmenu">
  <jsp:directive.page contentType="text/html; charset=UTF-8" session="false" />
  <section>
    <h3>Text/media sample (jsp)</h3>
    <p>Right click to edit text or media</p>
    <pre>
      <![CDATA[&lt;contextmenu:element menu="samples-textmedia"&gt;${'$'}{cmsfn:decode(content).text}&lt;/contextmenu:element&gt;]]>
    </pre>
    <div class="testcontent">
      <div style="float: left; margin-right: 10px;">
        <contextmenu:element menu="samples-textmedia" wrapper="div">
          <media:media item="${content.media}" width="320" height="240" />
        </contextmenu:element>
      </div>
      <contextmenu:element menu="samples-textmedia" wrapper="div">${cmsfn:decode(content).text}</contextmenu:element>
    </div>
  </section>
</jsp:root>