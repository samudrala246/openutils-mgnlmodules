<jsp:root version="2.1" xmlns:jsp="http://java.sun.com/JSP/Page" xmlns:c="http://java.sun.com/jsp/jstl/core"
  xmlns:fmt="http://java.sun.com/jsp/jstl/fmt" xmlns:fn="http://java.sun.com/jsp/jstl/functions" xmlns:cms="http://magnolia-cms.com/taglib/templating-components/cms"
  xmlns:cmsfn="http://magnolia-cms.com/taglib/templating-components/cmsfn" xmlns:media="http://net.sourceforge.openutils/mgnlMedia"
  xmlns:mu="mgnlutils">
  <jsp:directive.page contentType="text/html; charset=UTF-8" session="false" />
  <table class="table table-bordered">
    <thead>
      <tr>
        <th>text</th>
        <th>link</th>
        <th>uuidLink</th>
        <th>fckedit</th>
        <th>file</th>
        <th>combo</th>
        <th>date</th>
        <th>color</th>
        <th>checkbox</th>
      </tr>
    </thead>
    <tbody>
      <c:forEach var="row" items="${mu:splitAndTokenize(cmsfn:decode(content).grid)}">
        <tr>

          <!-- text -->
          <td>${row[0]}</td>

          <!-- link -->
          <td>
            <c:if test="${!empty row[1]}">
              <a href="${mu:link(row[1])}">${row[1]}</a>
            </c:if>
          </td>

          <!-- uuidLink -->
          <td>
            <c:if test="${!empty row[2]}">
              <a href="${mu:link(row[2])}">${row[2]}</a>
            </c:if>
          </td>

          <!-- fckedit -->
          <td>${row[3]}</td>

          <!-- file -->
          <td>
            <c:if test="${!empty row[4]}">
              <c:set var="path" value="${fn:substringBefore(row[4], '/grid_files/')}/grid_files" />
              <c:set var="filesNode" value="${cmsfn:content(path, 'website')}" />
              <c:set var="property" value="${fn:substringBefore(fn:substringAfter(row[4], '/grid_files/'), '/')}" />
              <a href="${pageContext.request.contextPath}${row[4]}">
                <!--cms:out contentNode="${filesNode}" nodeDataName="${property}" fileProperty="name" /-->
                ${filesNode[property].fileName}
              </a>
            </c:if>
          </td>

          <!-- combo -->
          <td>${row[5]}</td>

          <!-- date -->
          <td>${row[6]}</td>

          <!-- color -->
          <td>${row[7]}</td>

          <!-- checkbox -->
          <td>${row[8]}</td>
        </tr>
      </c:forEach>
    </tbody>
  </table>
</jsp:root>