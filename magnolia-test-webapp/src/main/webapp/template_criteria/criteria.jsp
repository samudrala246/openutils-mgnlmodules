<jsp:root version="2.0" xmlns:jsp="http://java.sun.com/JSP/Page" xmlns:c="http://java.sun.com/jsp/jstl/core"
  xmlns:fmt="http://java.sun.com/jsp/jstl/fmt" xmlns:fn="http://java.sun.com/jsp/jstl/functions" xmlns:cms="urn:jsptld:cms-taglib"
  xmlns:cmsu="urn:jsptld:cms-util-taglib" xmlns:cmsfn="http://www.magnolia.info/tlds/cmsfn-taglib.tld" xmlns:media="http://net.sourceforge.openutils/mgnlMedia"
  xmlns:mcmenu="mgnlcontextmenu">
  <jsp:directive.page contentType="text/html; charset=UTF-8" session="false" />
  <jsp:directive.page import="info.magnolia.cms.core.ItemType" />
  <jsp:directive.page import="info.magnolia.cms.beans.config.ContentRepository" />
  <jsp:directive.page import="net.sourceforge.openutils.mgnlcriteria.jcr.query.*" />
  <jsp:directive.page import="net.sourceforge.openutils.mgnlcriteria.jcr.query.criterion.*" />
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
      <cms:mainBar />
      <h1>${actpage.title}</h1>
      <p>Steps for testing this sample:</p>
      <ol>
        <li>
          <![CDATA[make sure that AclSearchIndex has been enabled in]]>
          <![CDATA[ /magnolia-test-webapp/src/main/webapp/WEB-INF/config/repo-conf/jackrabbit-memory-search.xml]]>
        </li>
        <li>
          <![CDATA[login as superuser and enter this page: all pets should be displayed]]>
        </li>
        <li>
          <![CDATA[login as criteria-dogsexcluded/criteria and enter this page: dogs should not be displayed]]>
        </li>
        <li>
          <![CDATA[login as criteria-dogsonly/criteria and enter this page: only dogs should be displayed]]>
        </li>
      </ol>
      <br />

      <jsp:scriptlet><![CDATA[
Criteria criteria = JCRCriteriaFactory
  .createCriteria()
  .setWorkspace(RepositoryConstants.WEBSITE)
  .setBasePath("/Criteria/pets")
  .add(Restrictions.eq("@jcr:primaryType", ItemType.CONTENT.getSystemName()))
  .addOrder(Order.asc("@birthDate"));

AdvancedResult result = criteria.execute();
ResultIterator<AdvancedResultItem> iterator = result.getItems();
pageContext.setAttribute("iterator", iterator);
]]>
      </jsp:scriptlet>
      <p>Pets results:</p>
      <ul>
        <c:forEach var="item" items="${iterator}">
          <li>${item.handle} (${item.title})</li>
        </c:forEach>
      </ul>
    </body>
  </html>
</jsp:root>