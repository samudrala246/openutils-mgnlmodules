<jsp:root version="2.0" xmlns:jsp="http://java.sun.com/JSP/Page" xmlns:c="urn:jsptld:http://java.sun.com/jsp/jstl/core"
  xmlns:fmt="urn:jsptld:http://java.sun.com/jsp/jstl/fmt" xmlns:fn="http://java.sun.com/jsp/jstl/functions" 
  xmlns:cms="http://magnolia-cms.com/taglib/templating-components/cms"
  xmlns:tagcloud="http://net.sourceforge.openutils/mgnlTagCloud" xmlns:su="http://openutils.sf.net/openutils-stringutils">
  <jsp:directive.page contentType="text/html; charset=UTF-8" session="false" />
  <jsp:text>
    <![CDATA[<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd"> ]]>
  </jsp:text>
  <html>
    <head>
      <cms:init />
      <title>${actpage.title}</title>
    </head>
    <body>
      <h1>${actpage.title}</h1>
      <h2>Samples:</h2>
      <p>Tagcloud created from scratch, retrieve tags from configured node and set tag url to current page:</p>
      <code>
          <![CDATA[
          &lt;tagcloud:tagcloud id="cloud1" url="${actpage.handle}/" showFreq="true" /&gt;
          ]]>
      </code>
      <p>Produce:</p>
      <tagcloud:tagcloud id="cloud1" url="${actpage.handle}/" showFreq="true" />
      <p>Tagcloud created from scratch, retrieve tags from your configured path (in this sample are /sample-tagcloud/)
      </p>
      <code>
          <![CDATA[
          &lt;tagcloud:tagcloud id="cloud2" orderby="count asc" path="/path/to/my/pages" /&gt;
          ]]>
      </code>
      <p>Produce:</p>
      <tagcloud:tagcloud id="cloud2" orderby="count asc" path="/sample-tagcloud/" />
      <p>Retrieve tagcloud where 'tags' is the name</p>
      <code>
          <![CDATA[
          &lt;tagcloud:tagcloud id="cloud3" name="tags" orderby="name asc" showFreq="true" /&gt;
          ]]>
      </code>
      <p>Produce:</p>
      <tagcloud:tagcloud id="cloud3" name="tags" orderby="name asc" showFreq="true" />
      <p>A sample where you can add tagcloud control on media dialog and retrieve a media tagcloud</p>
      <code>
          <![CDATA[
          &lt;tagcloud:tagcloud id="cloudMedia" name="tagsMedia" orderby="name asc" showFreq="true"&gt;
          ]]>
      </code>
    </body>
  </html>
</jsp:root>