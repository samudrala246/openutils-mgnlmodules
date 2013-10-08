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
    <div class="testcontent" id="fieldsetlist">
      <contextmenu:sortList containerId="fieldsetlist">
        <contextmenu:sortItem>
          <h5 class="control-group">
            <contextmenu:element name="list1" menu="move">List 1</contextmenu:element>
          </h5>
          <div id="fieldList1">
            <contextmenu:sortList containerId="fieldList1">
              <contextmenu:sortItem>
                <div class="control-group">
                  <label class="control-label">
                    <contextmenu:element name="field1" menu="formField">Field1:</contextmenu:element>
                  </label>
                  <div class="controls">
                    <input name="field1" id="field1" type="text" />
                    <c:set var="help" value="${contextmenu:entryValue(cmsfn:asJCRNode(content), 'field1.help')}" />
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
                    <c:set var="help" value="${contextmenu:entryValue(cmsfn:asJCRNode(content), 'field2.help')}" />
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
                    <contextmenu:element name="field3" menu="formField">Field3:</contextmenu:element>
                  </label>
                  <div class="controls">
                    <input name="field3" id="field3" type="text" />
                    <c:set var="help" value="${contextmenu:entryValue(cmsfn:asJCRNode(content), 'field3.help')}" />
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
        </contextmenu:sortItem>
        <contextmenu:sortItem>
          <h5 class="control-group">
            <contextmenu:element name="list2" menu="move">List 2</contextmenu:element>
          </h5>
          <div id="fieldList2">
            <contextmenu:sortList containerId="fieldList2">
              <contextmenu:sortItem>
                <div class="control-group">
                  <label class="control-label">
                    <contextmenu:element name="field1b" menu="formField">Field1 #2:</contextmenu:element>
                  </label>
                  <div class="controls">
                    <input name="field1" id="field1b" type="text" />
                    <c:set var="help" value="${contextmenu:entryValue(cmsfn:asJCRNode(content), 'field1b.help')}" />
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
                    <contextmenu:element name="field2b" menu="formField">Field2 #2:</contextmenu:element>
                  </label>
                  <div class="controls">
                    <input name="field2" id="field2b" type="text" />
                    <c:set var="help" value="${contextmenu:entryValue(cmsfn:asJCRNode(content), 'field2b.help')}" />
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
                    <contextmenu:element name="field4b" menu="formField">Field3 #2:</contextmenu:element>
                  </label>
                  <div class="controls">
                    <input name="field3b" id="field3b" type="text" />
                    <c:set var="help" value="${contextmenu:entryValue(cmsfn:asJCRNode(content), 'field3b.help')}" />
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
        </contextmenu:sortItem>
      </contextmenu:sortList>
    </div>
  </section>
</jsp:root>