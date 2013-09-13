<jsp:root version="2.0" xmlns:jsp="http://java.sun.com/JSP/Page" xmlns:c="http://java.sun.com/jsp/jstl/core"
  xmlns:fmt="http://java.sun.com/jsp/jstl/fmt" xmlns:fn="http://java.sun.com/jsp/jstl/functions" xmlns:cms="http://magnolia-cms.com/taglib/templating-components/cms"
  xmlns:cmsu="cms-util-taglib" xmlns:cmsfn="http://magnolia-cms.com/taglib/templating-components/cmsfn" xmlns:media="http://net.sourceforge.openutils/mgnlMedia"
  xmlns:mcmenu="mgnlcontextmenu">
  <jsp:directive.page contentType="text/html; charset=UTF-8" session="false" />
  <jsp:text>
    <![CDATA[<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd"> ]]>
  </jsp:text>
  <html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
    <head>
      <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
      <title>${actpage.title}</title>
      <mcmenu:links jsFramework="jquery" />
      <link rel="stylesheet" type="text/css"
        href="${pageContext.request.contextPath}/docroot/samples-contextmenu/paragraph-form.css" />
      <![CDATA[<script src="${pageContext.request.contextPath}/docroot/samples-contextmenu/paragraph-form.js"></script>]]>
      <cms:init />
    </head>
    <body>
      <h1>${actpage.title}</h1>
      <p>
        <![CDATA[Tags to put in the template jsp:]]>
        <ul>
          <li><![CDATA[<code>&lt;mcmenu:links jsFramework="jquery" /&gt;</code> (just before <code>&lt;/head&gt;</code>)]]></li>
          <li><![CDATA[<code>&lt;mcmenu:script /&gt;</code> (just before <code>&lt;/body&gt;</code>)]]></li>
        </ul>
      </p>
      <cms:area name="main" />
      <!-- <cms:contentNodeIterator contentNodeCollectionName="main"> -->
      <!-- <cms:editBar /> -->
      <!-- <cms:includeTemplate /> -->
      <!-- </cms:contentNodeIterator> -->
      <!-- <cms:newBar contentNodeCollectionName="main" paragraph="samples-contextmenu-message,samples-contextmenu-textmedia,samples-contextmenu-form" 
        /> -->
      <mcmenu:script />
    </body>
  </html>
</jsp:root>