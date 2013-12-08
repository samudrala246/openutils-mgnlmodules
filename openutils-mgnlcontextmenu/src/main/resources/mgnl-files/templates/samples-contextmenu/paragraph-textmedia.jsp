<jsp:root version="2.1" xmlns:jsp="http://java.sun.com/JSP/Page" xmlns:c="http://java.sun.com/jsp/jstl/core"
  xmlns:fmt="http://java.sun.com/jsp/jstl/fmt" xmlns:fn="http://java.sun.com/jsp/jstl/functions" xmlns:cms="http://magnolia-cms.com/taglib/templating-components/cms"
  xmlns:cmsu="cms-util-taglib" xmlns:cmsfn="http://magnolia-cms.com/taglib/templating-components/cmsfn" xmlns:media="http://net.sourceforge.openutils/mgnlMedia"
  xmlns:contextmenu="http://openutils/mgnlcontextmenu">
  <jsp:directive.page contentType="text/html; charset=UTF-8" session="false" />
  <section>
    <h3>Text/media sample (jsp)</h3>
    <p>Right click to edit text or media</p>
    <pre>
      <![CDATA[
        &ltmedia:media item="${'$'}{content.text_media}" width="60" height="60" /&gt
        &lt;contextmenu:element menu="textmedia" name="text" &gt;${'$'}{cmsfn:decode(content).text}&lt;/contextmenu:element&gt;
      ]]>
    </pre>
    <div class="testcontent">
      <div style="float: left; margin-right: 10px;">
        <media:media item="${content.text_media}" width="60" height="60" />
      </div>
      <contextmenu:element menu="textmedia" name="text" wrapper="div"> Right click to modify text
      </contextmenu:element>
    </div>
  </section>
</jsp:root>