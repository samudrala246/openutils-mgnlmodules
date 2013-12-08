<jsp:root version="2.1" xmlns:jsp="http://java.sun.com/JSP/Page" xmlns:c="http://java.sun.com/jsp/jstl/core"
  xmlns:fmt="http://java.sun.com/jsp/jstl/fmt" xmlns:fn="http://java.sun.com/jsp/jstl/functions" xmlns:cms="http://magnolia-cms.com/taglib/templating-components/cms"
  xmlns:cmsfn="http://magnolia-cms.com/taglib/templating-components/cmsfn" xmlns:mu="mgnlutils"
  xmlns:tags="urn:jsptagdir:/WEB-INF/tags/openutils">
  <jsp:directive.page contentType="text/html; charset=UTF-8" session="false" />
<![CDATA[
<!DOCTYPE html>
]]>
  <html>
    <head>
      <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
      <title>${content.title}</title>
      <link rel="stylesheet" type="text/css"
        href="${pageContext.request.contextPath}/.resources/bootstrap/3.0/css/bootstrap.min.css" />
      <meta name="viewport" content="width=device-width, initial-scale=1.0" />
      <cms:init />
    </head>
    <body>
      <div id="main" class="container">
        <h1>${content.title}</h1>
        <cms:area name="main" />
      </div>
    </body>
  </html>
</jsp:root>