<jsp:root version="2.0" xmlns:jsp="http://java.sun.com/JSP/Page" xmlns:c="http://java.sun.com/jsp/jstl/core"
  xmlns:fmt="http://java.sun.com/jsp/jstl/fmt" xmlns:fn="http://java.sun.com/jsp/jstl/functions" xmlns:cms="http://magnolia-cms.com/taglib/templating-components/cms"
  xmlns:cmsu="cms-util-taglib" xmlns:cmsfn="http://magnolia-cms.com/taglib/templating-components/cmsfn" xmlns:media="http://net.sourceforge.openutils/mgnlMedia"
  xmlns:contextmenu="mgnlcontextmenu">
  <jsp:directive.page contentType="text/html; charset=UTF-8" session="false" />
  <section>
    <h3>Message sample (jsp)</h3>
    <p>Right click to edit the message below</p>
    <pre>
    <![CDATA[&lt;contextmenu:element name="message"&gt;${'$'}{content.message}&lt;/contextmenu:element&gt;]]>
    </pre>
    <div class="testcontent">
      <contextmenu:element name="message">${content.message}</contextmenu:element>
    </div>
  </section>
</jsp:root>