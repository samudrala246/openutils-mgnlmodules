<jsp:root version="2.0" xmlns:jsp="http://java.sun.com/JSP/Page" xmlns:c="http://java.sun.com/jsp/jstl/core"
  xmlns:fmt="http://java.sun.com/jsp/jstl/fmt" xmlns:fn="http://java.sun.com/jsp/jstl/functions" xmlns:cms="http://magnolia-cms.com/taglib/templating-components/cms"
  xmlns:cmsu="cms-util-taglib" xmlns:cmsfn="http://magnolia-cms.com/taglib/templating-components/cmsfn" xmlns:media="http://net.sourceforge.openutils/mgnlMedia"
  xmlns:contextmenu="http://openutils/mgnlcontextmenu">
  <jsp:directive.page contentType="text/html; charset=UTF-8" session="false" />
  <section>
    <h3>Move fields (jsp)</h3>
    <p>Right click to move fields</p>
    <pre>
    <![CDATA[&lt;contextmenu:sortList  containerId="fieldList1" &gt; 
    &lt;contextmenu:sortItem&gt;
    &lt;div class="control-group"&gt;
    &lt;contextmenu:element name="field1" menu="formField"&gt;Field1:&lt;/contextmenu:element&gt;
    ..
    &lt;/div&gt;
    &lt;/contextmenu:sortItem&gt;
    &lt;contextmenu:sortItem&gt;
    &lt;div class="control-group"&gt;
    &lt;contextmenu:element name="field2" menu="formField"&gt;Field2:&lt;/contextmenu:element&gt;
    ..
    &lt;/div&gt;
    &lt;/contextmenu:sortItem&gt;
    &lt;/contextmenu:sortList&gt;
    ]]>
    </pre>
    <div class="testcontent">
      <div id="fieldList1">
        <contextmenu:sortList containerId="fieldList1">
          <contextmenu:sortItem>
            <div class="control-group">
              <label class="control-label">
                <contextmenu:element name="field1" menu="formField">Field1:</contextmenu:element>
              </label>
              <div class="controls">
                <input name="field1" id="field1" type="text" />
                <c:set var="help" value="${contextmenu:entryValue(content.JCRNode, 'field1.help')}" />
                <c:if test="${not empty help}">
                  <span class="help">
                    <![CDATA[${help}<span class="help-pointer">&nbsp;</span>]]>
                  </span>
                </c:if>
              </div>
            </div>
          </contextmenu:sortItem>
          <contextmenu:sortItem>
            <div class="control-group">
              <label class="control-label">
                <contextmenu:element name="field2" menu="formField">Field2:</contextmenu:element>
              </label>
              <div class="controls">
                <input name="field2" id="field2" type="text" />
                <c:set var="help" value="${contextmenu:entryValue(content.JCRNode, 'field2.help')}" />
                <c:if test="${not empty help}">
                  <span class="help">
                   <![CDATA[${help}<span class="help-pointer">&nbsp;</span>]]>
                  </span>
                </c:if>
              </div>
            </div>
          </contextmenu:sortItem>
        </contextmenu:sortList>
      </div>
    </div>
  </section>
</jsp:root>