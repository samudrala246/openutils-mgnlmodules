<jsp:root version="2.0" xmlns:jsp="http://java.sun.com/JSP/Page" xmlns:cms="urn:jsptld:cms-taglib" xmlns:cmsu="urn:jsptld:cms-util-taglib"
  xmlns:c="urn:jsptld:http://java.sun.com/jsp/jstl/core" xmlns:fmt="urn:jsptld:http://java.sun.com/jsp/jstl/fmt"
  xmlns:cmsfn="http://www.magnolia.info/tlds/cmsfn-taglib.tld" >
  <jsp:directive.page contentType="text/html; charset=UTF-8" session="false" />
  <jsp:text>
    <![CDATA[<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"> ]]>
  </jsp:text>
  <html xmlns="http://www.w3.org/1999/xhtml" lang="en">
    <head>
      <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
      <title>${actpage.title} ipad version</title>
      <cms:links />
	<style type="text/css">
		h1,h2 {
			color: blue;
			font-weight: bold;
		}
		
		body {
			background-color: #ddf;
			font-size: x-small;
		}
		
		div.ipad {
			border: solid 1px blue;
			margin:5px;
			
		}
		.ipad h3 {
			text-decoration: underline;
		}
		
		.ipad > div {
			text-align: center;
			border-bottom: 1px solid darkBlue;
			background-color: lightBlue;
			color: darkBlue;
		}
		
		blockquote {
			font-style: italic;
			color:darkBlue;
		}
		
		.ipad p {
			padding: 0 5px;
		}
		
		.ipad > p {
			-moz-column-count:2;
			-webkit-column-count:2;
			column-count:2;
			text-align:justify;
		}
	</style>
	</head>
    <body>
      <h1 style="">${actpage.title}</h1>
      <p>Welcome to Magnolia Mobile Module sample page.</p>
      <h2 style="text-decoration: blink;">iPad Version!</h2>
      <cms:contentNodeIterator contentNodeCollectionName="mobile-paragraphs" >
      	<cms:includeTemplate/>
      </cms:contentNodeIterator>
      <cms:newBar paragraph="p-sample-mobile" contentNodeCollectionName="mobile-paragraphs"></cms:newBar>
    </body>
  </html>
</jsp:root>