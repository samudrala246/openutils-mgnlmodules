<jsp:root version="2.1" xmlns:jsp="http://java.sun.com/JSP/Page" xmlns:c="http://java.sun.com/jsp/jstl/core"
  xmlns:fmt="http://java.sun.com/jsp/jstl/fmt" xmlns:fn="http://java.sun.com/jsp/jstl/functions" xmlns:cms="http://magnolia-cms.com/taglib/templating-components/cms"
  xmlns:cmsfn="http://magnolia-cms.com/taglib/templating-components/cmsfn" xmlns:mu="mgnlutils"
  xmlns:tags="urn:jsptagdir:/WEB-INF/tags/openutils">
  <jsp:directive.page contentType="text/html; charset=UTF-8" session="false" />
<![CDATA[
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
]]>
  <html>
    <head>
      <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
      <title> ${content.title}
      </title>
      <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/.resources/bootstrap/3.0/css/bootstrap.min.css" />
      <meta name="viewport" content="width=device-width, initial-scale=1.0" />
      <style>
    <![CDATA[
      code { display: block}
      ]]>
      </style>
      <cms:init />
    </head>
    <body>
      <div id="main" class="container">
        <div id="header">
          <h1>Magnolia Utils Samples</h1>
        </div>
        <c:set var="oldcontent" value="${state.currentContent}" />
        <c:set var="contentMap" value="${content}" />
        <c:set var="node" value="${oldcontent.JCRNode}" />
        <table class="table table-striped table-bordered table-hover">
          <thead>
            <tr>
              <th>Property</th>
              <th>Node</th>
              <th>ContentMap</th>
              <th>Content</th>
            </tr>
          </thead>
          <tbody>
            <tr>
              <th><!--  -->
              </th>
              <td>${node}
              </td>
              <td>${contentMap}
              </td>
              <td>${oldcontent}
              </td>
            </tr>
            <tr>
              <th>class
              </th>
              <td>${node['class'].name}
              </td>
              <td>${contentMap['class'].name }
              </td>
              <td>${oldcontent['class'].name}
              </td>
            </tr>
            <tr>
              <th>UUID
              </th>
              <td>${node.UUID}
              </td>
              <td>${contentMap.UUID}
              </td>
              <td>${oldcontent.UUID}
              </td>
            </tr>
            <tr>
              <th>@uuid
              </th>
              <td>${node['@uuid']}
              </td>
              <td>${contentMap['@uuid']}
              </td>
              <td>${oldcontent['@uuid']}
              </td>
            </tr>
            <tr>
              <th>@UUID
              </th>
              <td>${node['@UUID']}
              </td>
              <td>${contentMap['@UUID']}
              </td>
              <td>${oldcontent['@UUID']}
              </td>
            </tr>
            <tr>
              <th>@id
              </th>
              <td>${node['@id']}
              </td>
              <td>${contentMap['@id']}
              </td>
              <td>${oldcontent['@id']}
              </td>
            </tr>
            <tr>
              <th>handle
              </th>
              <td>${node.handle}
              </td>
              <td>${contentMap.handle}
              </td>
              <td>${oldcontent.handle}
              </td>
            </tr>
            <tr>
              <th>path
              </th>
              <td>${node.path}
              </td>
              <td>${contentMap.path}
              </td>
              <td>${oldcontent.path}
              </td>
            </tr>
            <tr>
              <th>@handle
              </th>
              <td>${node['@handle']}
              </td>
              <td>${contentMap['@handle']}
              </td>
              <td>${oldcontent['@handle']}
              </td>
            </tr>
            <tr>
              <th>@path
              </th>
              <td>${node['@path']}
              </td>
              <td>${contentMap['@path']}
              </td>
              <td>${oldcontent['@path']}
              </td>
            </tr>
            <tr>
              <th>@depth
              </th>
              <td>${node['@depth']}
              </td>
              <td>${contentMap['@depth']}
              </td>
              <td>${oldcontent['@depth']}
              </td>
            </tr>
            <tr>
              <th>@level
              </th>
              <td>${node['@level']}
              </td>
              <td>${contentMap['@level']}
              </td>
              <td>${oldcontent['@level']}
              </td>
            </tr>
            <tr>
              <th>level
              </th>
              <td>${node.level}
              </td>
              <td>${contentMap.level}
              </td>
              <td>${oldcontent.level}
              </td>
            </tr>
            <tr>
              <th>title
              </th>
              <td>${node.title}
              </td>
              <td>${contentMap.title}
              </td>
              <td>${oldcontent.title}
              </td>
            </tr>
            <tr>
              <th>@name
              </th>
              <td>${node['@name']}
              </td>
              <td>${contentMap['@name']}
              </td>
              <td>${oldcontent['@name']}
              </td>
            </tr>
            <tr>
              <th>name
              </th>
              <td>${node.name}
              </td>
              <td>${contentMap.name}
              </td>
              <td>${oldcontent.name}
              </td>
            </tr>
          </tbody>
        </table>
      
      <!-- Variable titleSize is set in the template definition, if empty sets it to 1 -->
       <!-- 
        <cms:contentNodeIterator contentNodeCollectionName="newcontent">
          <cms:includeTemplate />
        </cms:contentNodeIterator>
        <cms:newBar contentNodeCollectionName="newcontent" paragraph="samplesHowToJSP" />
         -->
        <div class="well">
          <h2>firstPageWithCollection</h2>
          <p>Test if exists a parent page with content in the collectionName given as parameter, if exist set the parent
            page as actpage
          </p>
          <pre class="prettyprint">
            <code>firstPageWithCollection("newcontent")</code>
            <code class="text-success">${mu:firstPageWithCollection("newcontent")}</code>
          </pre>
        </div>
        <div class="well">
          <h2>isPage</h2>
          <p>Evaluete if primary node type of the associated Node of an object is a mgnl:contentNode</p>
          <pre class="prettyprint">
            <code>isPage(actpage)</code>
            <code class="text-success">${mu:isPage(actpage)}</code>
          </pre>
        </div>
        <div class="well">
          <h2>getPage</h2>
          <p>Return the object passed or the first parent page that has content type = mgnl:content</p>
          <pre class="prettyprint">
            <code>getPage(actpage))</code>
            <code class="text-success">${mu:getPage(actpage)}</code>
          </pre>
        </div>
        <div class="well">
          <h2>requestAttributeMap</h2>
          <p>Return a map key=value of all magnolia request attributes</p>
          <pre class="prettyprint">
            <code>requestAttributeMap()["javax.servlet.forward.request_uri"]</code>
            <code class="text-success">${mu:requestAttributeMap()["javax.servlet.forward.request_uri"]}</code>
          </pre>
        </div>
        <div class="well">
          <h2>countNodesInCollection</h2>
          <p>Count the nodes in a collection with a given content and the name of the collection</p>
          <pre class="prettyprint">
            <code>countNodesInCollection(actpage,"newcontent")</code>
            <code class="text-success">${mu:countNodesInCollection(actpage,"newcontent")}</code>
          </pre>
        </div>
        <div class="well">
          <h2>countSubpages</h2>
          <p>Count subpages with a given content</p>
          <pre class="prettyprint">
            <code>countSubpages(actpage)</code>
            <code class="text-success">${mu:countSubpages(actpage)}</code>
          </pre>
        </div>
        <div class="well">
          <h2>subpages</h2>
          <p>Return the collection of subpages of a given page</p>
          <pre class="prettyprint">
            <code>subpages(actpage)</code>
            <code class="text-success">${mu:subpages(actpage)}</code>
          </pre>
        </div>
        <div class="well">
          <h2>user</h2>
          <p>Return the current user logged</p>
          <pre class="prettyprint">
            <code>user()</code>
            <code class="text-success">${mu:user()}</code>
          </pre>
        </div>
        <div class="well">
          <h2>develop</h2>
          <p>Test the system property 'magnolia.develop'</p>
          <pre class="prettyprint">
            <code>develop()</code>
            <code class="text-success">${mu:develop()}</code>
          </pre>
        </div>
        <div class="well">
          <h2>userInRole</h2>
          <p>Return true if the current user has a given role</p>
          <pre class="prettyprint">
            <code>userInRole("superuser")</code>
            <code class="text-success">${mu:userInRole("superuser")}</code>
          </pre>
        </div>
        <div class="well">
          <h2>ctx</h2>
          <p>Get the current context of this thread.</p>
          <pre class="prettyprint">
            <code>ctx()</code>
            <code class="text-success">${mu:ctx()}</code>
          </pre>
        </div>
        <div class="well">
          <h2>contentByPath</h2>
          <p>Return the content of a given path and repository</p>
          <pre class="prettyprint">
            <code>contentByPath('Magnolia-Utils','website')</code>
            <code class="text-success">${mu:contentByPath('Magnolia-Utils','website')}</code>
          </pre>
        </div>
        <div class="well">
          <h2>contentByPath</h2>
          <p>Return the content of a given path and repository</p>
          <pre class="prettyprint">
            <code>contentByPath('Magnolia-Utils','website')</code>
            <code class="text-success">${mu:contentByPath('Magnolia-Utils','website')}</code>
          </pre>
        </div>
        <div class="well">
          <h2>encodeISO9075</h2>
          <p>encode handle for a JackRabbit search</p>
          <pre class="prettyprint">
            <code>encodeISO9075('Magnolia$Utils')</code>
            <code class="text-success">${mu:encodeISO9075('Magnolia$Utils')}</code>
          </pre>
        </div>
        <div class="well">
          <h2>isStringInSeparatedList</h2>
          <p> splits the first argument based on the third (regex) argument and checks if the second argument is present
            in the resulting array
          </p>
          <pre class="prettyprint">
            <code>mu:isStringInSeparatedList('xAxBxC', 'B', '[A|B]x')</code>
            <code class="text-success">${mu:isStringInSeparatedList('xAxBxC', 'B', '[A|B]x')}</code>
          </pre>
        </div>
        <div class="well">
          <h2>isLine</h2>
          <p> check if the second argument string equals one line of the first argument string, same as
            isStringInSeparatedList with third argument "\n"
          </p>
          <pre class="prettyprint">
            <code>mu:isLine('test', 'prova')</code>
            <code class="text-success">${mu:isLine('test', 'prova')}</code>
          </pre>
        </div>
        <div class="well">
          <h2>getValidatedLabel</h2>
          <p>Retrieve validate label for input string</p>
          <pre class="prettyprint">
            <code>mu:getValidatedLabel('validlabel!test')</code>
            <code class="text-success">${mu:getValidatedLabel('validlabel!test')}</code>
          </pre>
        </div>
      </div>
      <tags:debug />
    </body>
  </html>
</jsp:root>