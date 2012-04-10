<jsp:root version="2.0" xmlns:jsp="http://java.sun.com/JSP/Page" xmlns:c="http://java.sun.com/jsp/jstl/core"
  xmlns:fmt="http://java.sun.com/jsp/jstl/fmt" xmlns:fn="http://java.sun.com/jsp/jstl/functions" xmlns:cms="http://magnolia-cms.com/taglib/templating-components/cms"
  xmlns:cmsfn="http://magnolia-cms.com/taglib/templating-components/cmsfn" xmlns:media="http://net.sourceforge.openutils/mgnlMedia">
  <jsp:directive.page contentType="text/html; charset=UTF-8" session="false" />
  <cms:setNode var="node" />
  <table cellspacing="0" border="1">
    <thead>
      <tr>
        <th>media</th>
      </tr>
    </thead>
    <tbody>
      <c:forEach var="row" items="${mu:splitAndTokenize(node.grid)}">
        <tr>
          <td>
            <media:media item="${row[0]}" />
          </td>
        </tr>
      </c:forEach>
    </tbody>
  </table>
</jsp:root>