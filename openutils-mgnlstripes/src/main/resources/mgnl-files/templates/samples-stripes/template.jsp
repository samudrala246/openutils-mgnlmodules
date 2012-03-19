<jsp:root version="2.0" xmlns:jsp="http://java.sun.com/JSP/Page" xmlns:c="http://java.sun.com/jsp/jstl/core"
  xmlns:fmt="http://java.sun.com/jsp/jstl/fmt" xmlns:fn="http://java.sun.com/jsp/jstl/functions" xmlns:cms="urn:jsptld:cms-taglib"
  xmlns:cmsu="urn:jsptld:cms-util-taglib" xmlns:cmsfn="http://www.magnolia.info/tlds/cmsfn-taglib.tld">
  <jsp:directive.page contentType="text/html; charset=UTF-8" session="false" />
  <jsp:text>
    <![CDATA[<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd"> ]]>
  </jsp:text>
  <html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
    <head>
      <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
      <title>${actpage.title}</title>
      <cms:links />
    </head>
    <body>
      <cms:mainBar dialog="o-page-home" />
      <h1>${actpage.title}</h1>
      <div>
        <cms:contentNodeIterator contentNodeCollectionName="main">
          <cms:editBar />
          <cms:includeTemplate />
        </cms:contentNodeIterator>
        <cms:newBar contentNodeCollectionName="main" paragraph="samplestripes" />
      </div>
    </body>
  </html>
</jsp:root>