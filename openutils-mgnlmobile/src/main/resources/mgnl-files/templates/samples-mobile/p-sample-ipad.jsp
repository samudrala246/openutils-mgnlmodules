<jsp:root version="2.0" xmlns:jsp="http://java.sun.com/JSP/Page" xmlns:cms="urn:jsptld:cms-taglib" xmlns:cmsu="urn:jsptld:cms-util-taglib"
  xmlns:c="urn:jsptld:http://java.sun.com/jsp/jstl/core" xmlns:fmt="urn:jsptld:http://java.sun.com/jsp/jstl/fmt"
  xmlns:cmsfn="http://www.magnolia.info/tlds/cmsfn-taglib.tld" >
  <jsp:directive.page contentType="text/html; charset=UTF-8" session="false" />
  <cms:editBar/>
  <div class="ipad">
	  <div>iPad version!</div>
	  <h3>${content.title}</h3>
	  <blockquote>
	  ${content.abstract }
	  </blockquote>
	  ${content.text }
  </div>
</jsp:root>