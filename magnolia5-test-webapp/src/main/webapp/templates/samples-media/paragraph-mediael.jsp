<jsp:root version="2.0" xmlns:jsp="http://java.sun.com/JSP/Page" xmlns:c="http://java.sun.com/jsp/jstl/core"
  xmlns:fmt="http://java.sun.com/jsp/jstl/fmt" xmlns:fn="http://java.sun.com/jsp/jstl/functions" xmlns:cms="http://magnolia-cms.com/taglib/templating-components/cms"
  xmlns:cmsfn="http://magnolia-cms.com/taglib/templating-components/cmsfn" xmlns:media="http://net.sourceforge.openutils/mgnlMedia"
  xmlns:mu="mgnlutils">
  <jsp:directive.page contentType="text/html; charset=UTF-8"
    session="false" />
  <div class="textImage"><c:set var="mediaNode"
    value="${media:node(content.media)}" />
  <div>
  <h3>Original image</h3>
  <div><pre>media:url(mediaNode)</pre> <img
    src="${pageContext.request.contextPath}${media:url(mediaNode)}" /></div>
  </div>
  <div>
  <h3>100x100: resize image in order to contain required resolution
  (100x100) and crop outfitting simmetric bands. Resulting image has the
  required resolution.</h3>
  <div><pre>media:urlres(mediaNode, '100x100')</pre> <img
    src="${pageContext.request.contextPath}${media:urlres(mediaNode, '100x100')}" />
  </div>
  </div>
  <div>
  <h3>L100x100: resize image in order to fit into required
  resolution. Resulting image should have one side smaller than required
  resolution.</h3>
  <div><pre>media:urlres(mediaNode, 'l100x100')</pre> <img
    src="${pageContext.request.contextPath}${media:urlres(mediaNode, 'l100x100')}" />
  </div>
  </div>
  <div>
  <h3>o100x100: resize image in order to fit into required
  resolution and fills empty bands with background color (default =
  White). Resulting image has the required resolution.</h3>
  <div><pre>media:urlres(mediaNode, 'o100x100')</pre> <img
    src="${pageContext.request.contextPath}${media:urlres(mediaNode, 'o100x100')}" />
  </div>
  <div>
  <p>With background color</p>
  <pre>media:urlres(mediaNode, 'o100x100;background=FF0000')</pre> <img
    src="${pageContext.request.contextPath}${media:urlres(mediaNode, 'o100x100;background=FF0000')}" />
  </div>
  </div>
  <div>
  <h3>n100x100: resize image in order to contain the required
  resolution. Resulting image should have one side bigger than required
  resolution.</h3>
  <div><pre>media:urlres(mediaNode, 'n100x100')</pre> <img
    src="${pageContext.request.contextPath}${media:urlres(mediaNode, 'n100x100')}" />
  </div>
  </div>
  <div>
  <h3>Passing parameters: resolution string supports optional
  parameters that may enable post processing.</h3>
  <div>
  <p>Adding parameter bw will make the image in grayscale</p>
  <pre>media:urlres(mediaNode, 'n100x100;bw')</pre> <img
    src="${pageContext.request.contextPath}${media:urlres(mediaNode, 'n100x100;bw')}" />
  </div>
  </div>
  </div>
</jsp:root>