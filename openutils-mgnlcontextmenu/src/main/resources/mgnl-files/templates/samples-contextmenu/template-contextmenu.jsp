<jsp:root version="2.0" xmlns:jsp="http://java.sun.com/JSP/Page" xmlns:c="http://java.sun.com/jsp/jstl/core"
  xmlns:fmt="http://java.sun.com/jsp/jstl/fmt" xmlns:fn="http://java.sun.com/jsp/jstl/functions" xmlns:cms="http://magnolia-cms.com/taglib/templating-components/cms"
  xmlns:cmsu="cms-util-taglib" xmlns:cmsfn="http://magnolia-cms.com/taglib/templating-components/cmsfn" xmlns:media="http://net.sourceforge.openutils/mgnlMedia"
  xmlns:contextmenu="http://openutils/mgnlcontextmenu">
  <jsp:directive.page contentType="text/html; charset=UTF-8" session="false" />
  <jsp:text>
    <![CDATA[<!DOCTYPE html> ]]>
  </jsp:text>
  <html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
    <head>
      <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
      <title>${content.title}</title>
      
      <script src="${pageContext.request.contextPath}/.resources/contextmenu/js/jquery-1.4.2.min.js">/**/</script>
      
      ${contextmenu:links()}
      
      <cms:init />
      
      <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/.resources/contextmenu/css/bootstrap.min.css" />
      
      <!-- samples css/js -->
      <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/.resources/contextmenu/css/contextmenu-samples.css" />
      <script src="${pageContext.request.contextPath}/.resources/contextmenu/js/contextmenu-samples.js">/**/</script>
      <!-- end samples css/js -->
    </head>
    <body>
      <div class="container">
        <h1>${content.title}</h1>
        <section>
          <h2>JSP sample</h2>
          <p>
            <span>Add the following to your template in order to initialize the contextmenu module:</span>
            <ul>
              <li>add <code>${'$'}{contextmenu:links()}</code> (just before <code>&amp;lt;/head&amp;gt;</code>)</li>
              <li>add <code>${'$'}{contextmenu:scripts()}</code> (just before <code>&amp;lt;/body&amp;gt;</code>)</li>
              <li>add the jquery library</li>
            </ul>
          </p>
        </section>
        <cms:area name="main" />
      </div>
      ${contextmenu:scripts()}
    </body>
  </html>
</jsp:root>