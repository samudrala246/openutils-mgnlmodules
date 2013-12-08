<jsp:root version="2.1" xmlns:jsp="http://java.sun.com/JSP/Page" xmlns:c="http://java.sun.com/jsp/jstl/core"
  xmlns:fmt="http://java.sun.com/jsp/jstl/fmt" xmlns:fn="http://java.sun.com/jsp/jstl/functions" xmlns:cms="http://magnolia-cms.com/taglib/templating-components/cms"
  xmlns:cmsfn="http://magnolia-cms.com/taglib/templating-components/cmsfn" xmlns:media="http://net.sourceforge.openutils/mgnlMedia"
  xmlns:mu="mgnlutils" xmlns:rules="mgnlrules">
  <jsp:directive.page contentType="text/html; charset=UTF-8" session="false" />
  <section>
    <h3>Grid sample (jsp)</h3>
    <div>
      <table class="table table-bordered">
        <thead>
          <tr>
            <th>expression</th>
            <th>result</th>
          </tr>
        </thead>
        <tbody>
          <c:forEach var="row" items="${mu:splitAndTokenize(cmsfn:decode(content).grid)}">
            <tr>
          <!-- expression -->
              <td>${row[0]}</td>
              <td>${rules:evaluate(row[0], pageContext)}</td>
            </tr>
          </c:forEach>
        </tbody>
      </table>
    </div>
  </section>
</jsp:root>