<jsp:root version="2.0" xmlns:jsp="http://java.sun.com/JSP/Page" xmlns:cms="urn:jsptld:cms-taglib" xmlns:cmsu="urn:jsptld:cms-util-taglib"
  xmlns:c="urn:jsptld:http://java.sun.com/jsp/jstl/core" xmlns:fmt="urn:jsptld:http://java.sun.com/jsp/jstl/fmt"
  xmlns:cmsfn="http://www.magnolia.info/tlds/cmsfn-taglib.tld" xmlns:mobile="mgnlmobile">
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
      <cms:mainBar>
      	<mobile:preview position="left"/>
      </cms:mainBar>
      <h1>${actpage.title}</h1>
      <p>Welcome to Magnolia Mobile Module sample page.</p>
      <cms:contentNodeIterator contentNodeCollectionName="mobile-paragraphs" >
      	<cms:includeTemplate/>
      </cms:contentNodeIterator>
      <cms:newBar paragraph="p-sample-mobile" contentNodeCollectionName="mobile-paragraphs"></cms:newBar>
    </body>
  </html>
</jsp:root>