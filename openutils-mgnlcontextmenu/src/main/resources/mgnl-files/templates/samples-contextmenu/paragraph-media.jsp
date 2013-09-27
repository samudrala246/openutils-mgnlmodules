<jsp:root version="2.0" xmlns:jsp="http://java.sun.com/JSP/Page" xmlns:c="http://java.sun.com/jsp/jstl/core"
  xmlns:fmt="http://java.sun.com/jsp/jstl/fmt" xmlns:fn="http://java.sun.com/jsp/jstl/functions" xmlns:cms="http://magnolia-cms.com/taglib/templating-components/cms"
  xmlns:cmsu="cms-util-taglib" xmlns:cmsfn="http://magnolia-cms.com/taglib/templating-components/cmsfn" xmlns:media="http://net.sourceforge.openutils/mgnlMedia"
  xmlns:contextmenu="http://openutils/mgnlcontextmenu">
  <jsp:directive.page contentType="text/html; charset=UTF-8" session="false" />
  <section>
    <h3>Media sample (jsp)</h3>
    <p>Right click to edit text or media</p>
    <pre>
      <![CDATA[
        &lt;contextmenu:element menu="media" name="media" &gt;
        &lt;media:media item="${'$'}{content.media}" width="60" height="60" usebody="true" /&gt;
        &lt;/contextmenu:element&gt;
      ]]>
    </pre>
    <div class="testcontent">
      <contextmenu:element menu="media" name="media" wrapper="div" usebody="true">
        Right Click on media to modify it
        <media:media item="${content.media}" width="60" height="60" />
      </contextmenu:element>
    </div>
  </section>
</jsp:root>