<jsp:root version="2.1" xmlns:jsp="http://java.sun.com/JSP/Page" xmlns:c="http://java.sun.com/jsp/jstl/core"
  xmlns:fmt="http://java.sun.com/jsp/jstl/fmt" xmlns:fn="http://java.sun.com/jsp/jstl/functions" xmlns:cms="http://magnolia-cms.com/taglib/templating-components/cms"
  xmlns:cmsu="cms-util-taglib" xmlns:cmsfn="http://magnolia-cms.com/taglib/templating-components/cmsfn" xmlns:media="http://net.sourceforge.openutils/mgnlMedia"
  xmlns:lmsfn="urn:jsptld:lms-fn">
  <jsp:directive.page contentType="text/html; charset=UTF-8" session="false" />
  <jsp:text>
    <![CDATA[<!DOCTYPE html> ]]>
  </jsp:text>
  <html xmlns="http://www.w3.org/1999/xhtml" lang="en">
    <head>
      <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
      <title>${content.title}</title>
      <cms:init />
      <link rel="stylesheet" type="text/css"
        href="${pageContext.request.contextPath}/.resources/bootstrap/3.0/css/bootstrap.min.css" />
      <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    </head>
    <body>
      <div id="main" class="container">
        <div id="header">
          <h1>${content.title}</h1>
        </div>
        <p>Click on a course in the list below. It opens a popup with the course inside</p>
        <ul>
          <c:forEach items="${lmsfn:courseList()}" var="course">
            <li>
              <lmsfn:playerUrl course="${course}" target="blank" />
            </li>
          </c:forEach>
        </ul>
        <p>The code to create the list is:</p>
      <pre>
        &amp;lt;ul&amp;gt;
          &amp;lt;c:forEach items="\${lmsfn:courseList()}" var="course"&amp;gt;
            &amp;lt;li&amp;gt;
              &amp;lt;lmsfn:playerUrl course="\${course}" target="blank" /&amp;gt;
            &amp;lt;/li&amp;gt;
          &amp;lt;/c:forEach&amp;gt;
        &amp;lt;/ul&amp;gt;
      </pre>
      </div>
    </body>
  </html>
</jsp:root>