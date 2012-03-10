<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<%@ page contentType="text/html" pageEncoding="UTF-8" %> <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"
%> <%@ taglib prefix="cms" uri="cms-taglib" %> <%@ taglib prefix="cmsu" uri="cms-util-taglib" %> <%@ taglib prefix="mu"
uri="mgnlutils" %>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <title>
      <cms:ifNotEmpty nodeDataName="title">${content.title}</cms:ifNotEmpty>
      <cms:ifEmpty nodeDataName="title"><cms:out nodeDataName="name" /></cms:ifEmpty>
    </title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/docroot/samples/samples.css" />
    <cms:links />
    <style>
      .function {
      border: 1px solid #ccc;
      padding: 20px;
      }      
      .sampleresult {
        color: red;
      }
      * {
        font-family: tahoma, verdana, arial;
      }
      p{
        color: blue;
      }
    </style>
  </head>
  <body>

    <!-- Page Dialog  -->
    <cms:mainBar dialog="mainProperties" />
    <div id="header">
      <h1>Magnolia Utils Samples</h1>
    </div>

    <!-- Simple Navigation Tag  -->
    <div id="navigation">
      <cmsu:simpleNavigation />
    </div>
    <div id="main">
      <!-- Variable titleSize is set in the template definition, if empty sets it to 1 -->
      <cms:contentNodeIterator contentNodeCollectionName="newcontent">
        <cms:includeTemplate />
      </cms:contentNodeIterator>
      <cms:newBar contentNodeCollectionName="newcontent" paragraph="samplesHowToJSP" />
      <div class="function">
        <h2>firstPageWithCollection</h2>
        <p>Test if exists a parent page with content in the collectionName given as parameter, if exist set the parent
          page a sactpage</p>
        <div class="sampletext">firstPageWithCollection("newcontent")</div>
        <div class="sampleresult">${mu:firstPageWithCollection("newcontent")}}</div>
      </div>
      <div class="function">
        <h2>isPage</h2>
        <p>Evaluete if primary node type of the associated Node of an object is a mgnl:contentNode</p>
        <div class="sampletext">isPage(actpage)</div>
        <div class="sampleresult">${mu:isPage(actpage)}</div>
      </div>
       <div class="function">
        <h2>getPage</h2>
        <p>Return the object passed or the first parent page that has content type = mgnl:content</p>
        <div class="sampletext">getPage(actpage))</div>
        <div class="sampleresult">${mu:getPage(actpage)}</div>
      </div>
      <div class="function">
        <h2>requestAttributeMap</h2>
        <p>Return a map key=value of all magnolia request attributes</p>
        <div class="sampletext">requestAttributeMap()["javax.servlet.forward.request_uri"]</div>
        <div class="sampleresult">${mu:requestAttributeMap()["javax.servlet.forward.request_uri"]}</div>
      </div>
      <div class="function">
        <h2>countNodesInCollection</h2>
        <p>Count the nodes in a collection with a given content and the name of the collection</p>
        <div class="sampletext">countNodesInCollection(actpage,"newcontent")</div>
        <div class="sampleresult">${mu:countNodesInCollection(actpage,"newcontent")}</div>
      </div>
      <div class="function">
        <h2>countSubpages</h2>
        <p>Count subpages with a given content</p>
        <div class="sampletext">countSubpages(actpage)</div>
        <div class="sampleresult">${mu:countSubpages(actpage)}</div>
      </div>
      <div class="function">
        <h2>subpages</h2>
        <p>Return the collection of subpages of a given page</p>
        <div class="sampletext">subpages(actpage)</div>
        <div class="sampleresult">${mu:subpages(actpage)}</div>
      </div>
      <div class="function">
        <h2>user</h2>
        <p>Return the current user logged</p>
        <div class="sampletext">user()</div>
        <div class="sampleresult">${mu:user()}</div>
      </div>
      <div class="function">
        <h2>develop</h2>
        <p>Test the system property 'magnolia.develop'</p>
        <div class="sampletext">develop()</div>
        <div class="sampleresult">${mu:develop()}</div>
      </div>
      <div class="function">
        <h2>userInRole</h2>
        <p>Return true if the current user has a given role</p>
        <div class="sampletext">userInRole("superuser")</div>
        <div class="sampleresult">${mu:userInRole("superuser")}</div>
      </div>
      <div class="function">
        <h2>ctx</h2>
        <p>Get the current context of this thread.</p>
        <div class="sampletext">ctx()</div>
        <div class="sampleresult">${mu:ctx()}</div>
      </div>
      <div class="function">
        <h2>contentByPath</h2>
        <p>Return the content of a given path and repository</p>
        <div class="sampletext">contentByPath('Magnolia-Utils','website')</div>
        <div class="sampleresult">${mu:contentByPath('Magnolia-Utils','website')}</div>
      </div>
      <div class="function">
        <h2>contentByPath</h2>
        <p>Return the content of a given path and repository</p>
        <div class="sampletext">contentByPath('Magnolia-Utils','website')</div>
        <div class="sampleresult">${mu:contentByPath('Magnolia-Utils','website')}</div>
      </div>
      <div class="function">
        <h2>encodeISO9075</h2>
        <p>encode handle for a JackRabbit search</p>
        <div class="sampletext">encodeISO9075('Magnolia$Utils')</div>
        <div class="sampleresult">${mu:encodeISO9075('Magnolia$Utils')}</div>
      </div>
      <div class="function">
        <h2>isStringInSeparatedList</h2>
        <p> splits the first argument based on the third (regex) argument and checks if the second argument is present
          in the resulting array</p>
        <div class="sampletext">mu:isStringInSeparatedList('xAxBxC', 'B', '[A|B]x')</div>
        <div class="sampleresult">${mu:isStringInSeparatedList('xAxBxC', 'B', '[A|B]x')}</div>
      </div>
      <div class="function">
        <h2>isLine</h2>
        <p> check if the second argument string equals one line of the first argument string, same as isStringInSeparatedList
      with third argument "\n"</p>
        <div class="sampletext">mu:isLine('test', 'prova')</div>
        <div class="sampleresult">${mu:isLine('test', 'prova')}</div>
      </div>
       <div class="function">
        <h2>getValidatedLabel</h2>
        <p>Retrieve validate label for input string</p>
        <div class="sampletext">mu:getValidatedLabel('validlabel!test')</div>
        <div class="sampleresult">${mu:getValidatedLabel('validlabel!test')}</div>
      </div>
    </div>
    <div id="footer">
      <p>
        This page was last edited by
        <span class="author">
          <cms:out nodeDataName="mgnl:authorid" contentNodeName="MetaData" />
        </span>
        on
        <span class="modificationdate">
          <cms:out nodeDataName="mgnl:lastmodified" contentNodeName="MetaData" />
        </span>
      </p>

    </div>
  </body>
</html>