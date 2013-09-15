<jsp:root version="2.0" xmlns:jsp="http://java.sun.com/JSP/Page" xmlns:c="http://java.sun.com/jsp/jstl/core"
  xmlns:fmt="http://java.sun.com/jsp/jstl/fmt" xmlns:fn="http://java.sun.com/jsp/jstl/functions" xmlns:cms="http://magnolia-cms.com/taglib/templating-components/cms"
  xmlns:cmsfn="http://magnolia-cms.com/taglib/templating-components/cmsfn" xmlns:media="http://net.sourceforge.openutils/mgnlMedia"
  xmlns:mu="mgnlutils" xmlns:rules="mgnlrules">
  <jsp:directive.page contentType="text/html; charset=UTF-8" session="false" />
  <section>
    <h3>If-then-else sample (jsp)</h3>
    <div>
      <div class="highlight">
        <pre>
          <code class="html">
    <![CDATA[${'$'}{rules:evaluate('${cmsfn:decode(content).ifCondition}', pageContext) ? '${'$'}{cmsfn:decode(content).thenText}' : '${'$'}{cmsfn:decode(content).elseText}'}]]>
          </code>
        </pre>
      </div>
  <![CDATA[${rules:evaluate(cmsfn:decode(content).ifCondition, pageContext) ? cmsfn:decode(content).thenText : cmsfn:decode(content).elseText}]]>
    </div>
  </section>
</jsp:root>