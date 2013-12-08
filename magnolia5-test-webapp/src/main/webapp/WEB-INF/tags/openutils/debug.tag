<jsp:root version="2.1" xmlns:jsp="http://java.sun.com/JSP/Page" xmlns:c="http://java.sun.com/jsp/jstl/core"
  xmlns:fmt="http://java.sun.com/jsp/jstl/fmt" xmlns:fn="http://java.sun.com/jsp/jstl/functions" xmlns:mgnl="http://magnolia-cms.com/taglib/templating-components/cms"
  xmlns:cmsfn="http://magnolia-cms.com/taglib/templating-components/cmsfn" xmlns:su="http://openutils.sf.net/openutils-stringutils"
  xmlns:mu="mgnlutils" xmlns:du="dateutils" xmlns:el="project">
  <c:if test="${mu:systemProperty('magnolia.develop') or param.debug eq '42'}">
    <div id="debugpanel">
      <table class="table table-striped table-bordered table-hover table-condensed">
        <tr>
          <td>request URI</td>
          <td>${pageContext.request.requestURI}</td>
        </tr>
        <tr>
          <td>state</td>
          <td>${state}</td>
        </tr>
        <tr>
          <td>state.handle</td>
          <td>${state.handle}</td>
        </tr>
        <tr>
          <td>state.repository</td>
          <td>${state.repository}</td>
        </tr>
        <tr>
          <td>state.templateName</td>
          <td>${state.templateName}</td>
        </tr>
        <tr>
          <td>state.locale</td>
          <td>${state.locale}</td>
        </tr>
        <tr>
          <td>state.channel.name</td>
          <td>${state.channel.name}</td>
        </tr>
        <tr>
          <td>state.currentURI</td>
          <td>${state.currentURI}</td>
        </tr>
        <tr>
          <td>state.originalURL</td>
          <td>${state.originalURL}</td>
        </tr>
        <tr>
          <td>state.originalURI</td>
          <td>${state.originalURI}</td>
        </tr>
<!--        <tr>
          <td>state.mainContent</td>
          <td>${state.mainContent} ${state.mainContent.class}</td>
        </tr>-->
<!--        <tr>
          <td>state.mainContent.JCRNode</td>
          <td>${state.mainContent.JCRNode} ${state.mainContent.JCRNode.class}</td>
        </tr>-->
        <tr>
          <td>state.mainContent.JCRNode.identifier</td>
          <td>${state.mainContent.JCRNode.identifier}</td>
        </tr>
        <tr>
          <td>state.currentContent</td>
          <td>${state.currentContent}</td>
        </tr>
        <tr>
          <td>content</td>
          <td>${content} content.title: ${content.title} id: ${content['@id']} name: ${content['@name']} depth:
            ${content['@depth']} path: ${content['@path']} nodeType: ${content['@nodeType'].name}
          </td>
        </tr>
        <tr>
          <td>def</td>
          <td>${def}</td>
        </tr>
        <tr>
          <td>model</td>
          <td>${model}</td>
        </tr>
<!--        <tr>
          <td>model.node</td>
          <td>${model.node} ${model.node.class} ${model.node.name} ${model.node}</td>
        </tr>-->
        <tr>
          <td>model.content</td>
          <td>${model.content}</td>
        </tr>
        <tr>
          <td>model.def</td>
          <td>${model.def}</td>
        </tr>
        <tr>
          <td>cmsfn</td>
          <td>${cmsfn}</td>
        </tr>
        <tr>
          <td>request attr</td>
          <td>
            <table>
              <c:forEach items="${pageContext.request.attributeNames}" var="attr">
                <c:if test="${attr ne 'org.springframework.core.convert.ConversionService'}">
                  <tr>
                    <td>${attr}</td>
                    <td>${requestScope[attr]}</td>
                  </tr>
                </c:if>
              </c:forEach>
            </table>
          </td>
        </tr>
      </table>
    </div>
  </c:if>
</jsp:root>