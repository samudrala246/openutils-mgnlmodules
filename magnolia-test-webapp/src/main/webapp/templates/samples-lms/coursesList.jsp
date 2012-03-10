<jsp:root version="2.0" xmlns:jsp="http://java.sun.com/JSP/Page" xmlns:cms="urn:jsptld:cms-taglib" xmlns:cmsu="urn:jsptld:cms-util-taglib"
  xmlns:c="urn:jsptld:http://java.sun.com/jsp/jstl/core" xmlns:fmt="urn:jsptld:http://java.sun.com/jsp/jstl/fmt"
  xmlns:cmsfn="http://www.magnolia.info/tlds/cmsfn-taglib.tld" xmlns:lmsfn="urn:jsptld:lms-fn">
  <jsp:directive.page contentType="text/html; charset=UTF-8" session="false" />
  <jsp:text>
    <![CDATA[<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"> ]]>
  </jsp:text>
  <html xmlns="http://www.w3.org/1999/xhtml" lang="en">
    <head>
      <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
      <title>${actpage.title}</title>
      <cms:links />
    </head>
    <body>
      <h1>${actpage.title}</h1>
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
        &lt;ul&gt;
          &lt;c:forEach items="${lmsfn:courseList()}" var="course"&gt;
            &lt;li&gt;
              &lt;lmsfn:playerUrl course="${course}" target="blank" /&gt;
            &lt;/li&gt;
          &lt;/c:forEach&gt;
        &lt;/ul&gt;
      </pre>
    </body>
  </html>
</jsp:root>