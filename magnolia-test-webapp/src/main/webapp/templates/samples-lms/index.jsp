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
      <p>Welcome to Magnolia LMS Module sample page.</p>
      <p>The sample course repository is structured as:</p>
      <ul>
        <li>
          <b>samples</b>
          <ul>
            <li>
              <b>std-level-1</b>
              <ol>
                <li>Simple course 1</li>
                <li>Simple course 2</li>
              </ol>
              <ul>
                <li>
                  <b>std-level2a</b>
                  <ol>
                    <li>Simple course 1</li>
                  </ol>
                </li>
                <li>
                  <b>std-level2b</b>
                  <ol>
                    <li>Simple course 1</li>
                  </ol>
                </li>
              </ul>
            </li>
          </ul>
        </li>
      </ul>
      <p>There are three roles, one for each folder:</p>
      <ul>
        <li><b>std-level-1</b> which can view its courses but not subfolders</li>
        <li><b>std-level-2a</b> which can view its courses but not subfolders</li>
        <li><b>std-level-3a</b> which can view its courses but not subfolders</li>
      </ul>
      <p>The user student/student has the role std-level-1; there is an example listener (net.sourceforge.openutils.mgnllms.samples.listeners.StudentLevelListener defined in /modules/lms/lms-config/listeners/student-level), 
      that is called whenever a user complete a course and performs following actions:
        <ol>
          <li>check if the user has successfully completed all courses to which he has access</li>
          <li>if true assign to user additional roles corresponding to the names of subfolders of the last completed course</li>
        </ol>
      </p>
      <p><a href="${pageContext.request.contextPath}/sample-lms/courses.html">Go to the courses list!</a> (use student/student to access)</p>
    </body>
  </html>
</jsp:root>