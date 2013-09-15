<jsp:root version="2.0" xmlns:jsp="http://java.sun.com/JSP/Page" xmlns:c="http://java.sun.com/jsp/jstl/core"
  xmlns:fmt="http://java.sun.com/jsp/jstl/fmt" xmlns:fn="http://java.sun.com/jsp/jstl/functions" xmlns:cms="http://magnolia-cms.com/taglib/templating-components/cms"
  xmlns:cmsu="cms-util-taglib" xmlns:cmsfn="http://magnolia-cms.com/taglib/templating-components/cmsfn" xmlns:media="http://net.sourceforge.openutils/mgnlMedia"
  xmlns:contextmenu="http://openutils/mgnlcontextmenu">
  <jsp:directive.page contentType="text/html; charset=UTF-8" session="false" />
  <section>
    <h3>Form sample (jsp)</h3>
    <p>Right click any form element to edit</p>
    <pre>
      <![CDATA[
&lt;contextmenu:element name="firstname" menu="samples-form-label"&gt;First Name:&lt;/contextmenu:element&gt;
&lt;c:set var="hint" value="${'$'}{contextmenu:entryValue(content.JCRNode, 'firstname-hint')}" /&gt;]]>
    </pre>
    <br />
    <div class="testcontent clearfix">
      <p>Tab or click through the fields to reveal the hints.</p>
      <form class="form form-horizontal" action="">
        <div class="form-group">
          <label class="col-lg-2 control-label" for="firstname">
            <contextmenu:element name="firstname" menu="samples-form-label">First Name:</contextmenu:element>
          </label>
          <div class="col-lg-10">
            <input name="firstname" id="firstname" type="text" />
            <c:set var="hint" value="${contextmenu:entryValue(content.JCRNode, 'firstname-hint')}" />
            <c:if test="${not empty hint}">
              <span class="hint">
            <![CDATA[${hint}<span class="hint-pointer">&nbsp;</span>]]>
              </span>
            </c:if>
          </div>
        </div>
        <div class="form-group">
          <label class="col-lg-2 control-label" for="lastname">
            <contextmenu:element name="lastname" menu="samples-form-label">Last Name:</contextmenu:element>
          </label>
          <div class="col-lg-10">
            <input name="lastname" id="lastname" type="text" />
            <c:set var="hint" value="${contextmenu:entryValue(content.JCRNode, 'lastname-hint')}" />
            <c:if test="${not empty hint}">
              <span class="hint">
            <![CDATA[${hint}<span class="hint-pointer">&nbsp;</span>]]>
              </span>
            </c:if>
          </div>
        </div>
        <div class="form-group">
          <label class="col-lg-2 control-label" for="email">
            <contextmenu:element name="email" menu="samples-form-label">Email:</contextmenu:element>
          </label>
          <div class="col-lg-10">
            <input name="email" id="email" type="text" />
            <c:set var="hint" value="${contextmenu:entryValue(content.JCRNode, 'email-hint')}" />
            <c:if test="${not empty hint}">
              <span class="hint">
            <![CDATA[${hint}<span class="hint-pointer">&nbsp;</span>]]>
              </span>
            </c:if>
          </div>
        </div>
        <div class="form-group">
          <label class="col-lg-2 control-label" for="year">
            <contextmenu:element name="year" menu="samples-form-label">Birth Year:</contextmenu:element>
          </label>
          <div class="col-lg-10">
            <select id="year" name="year">
              <option value="">YYYY</option>
              <option value="1066">1066</option>
              <option value="1492">1492</option>
              <option value="1776">1776</option>
              <option value="1812">1812</option>
              <option value="1917">1917</option>
              <option value="1942">1942</option>
              <option value="1999">1999</option>
            </select>
            <c:set var="hint" value="${contextmenu:entryValue(content.JCRNode, 'year-hint')}" />
            <c:if test="${not empty hint}">
              <span class="hint">
            <![CDATA[${hint}<span class="hint-pointer">&nbsp;</span>]]>
              </span>
            </c:if>
          </div>
        </div>
        <div class="form-group">
          <label class="col-lg-2 control-label" for="username">
            <contextmenu:element name="username" menu="samples-form-label">Username:</contextmenu:element>
          </label>
          <div class="col-lg-10">
            <input name="username" id="username" type="text" />
            <c:set var="hint" value="${contextmenu:entryValue(content.JCRNode, 'username-hint')}" />
            <c:if test="${not empty hint}">
              <span class="hint">
            <![CDATA[${hint}<span class="hint-pointer">&nbsp;</span>]]>
              </span>
            </c:if>
          </div>
        </div>
        <div class="form-group">
          <label class="col-lg-2 control-label" for="password">
            <contextmenu:element name="password" menu="samples-form-label">Password:</contextmenu:element>
          </label>
          <div class="col-lg-10">
            <input name="password" id="password" type="password" />
            <c:set var="hint" value="${contextmenu:entryValue(content.JCRNode, 'password-hint')}" />
            <c:if test="${not empty hint}">
              <span class="hint">
            <![CDATA[${hint}<span class="hint-pointer">&nbsp;</span>]]>
              </span>
            </c:if>
          </div>
        </div>
        <div class="form-group">
          <div class="col-lg-offset-2 col-lg-10">
            <button type="submit" class="btn btn-default">Submit</button>
          </div>
        </div>
      </form>
    </div>
  </section>
</jsp:root>