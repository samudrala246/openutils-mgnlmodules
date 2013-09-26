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
&lt;contextmenu:element name="firstname" menu="simpleForm"&gt;First Name:&lt;/contextmenu:element&gt;
&lt;c:set var="help" value="${'$'}{contextmenu:entryValue(cmsfn:asJCRNode(content), 'firstname.help')}" /&gt;]]>
    </pre>
    <br />
    <div class="testcontent clearfix">
      <p>Tab or click through the fields to reveal the helps.</p>
      <form class="form form-horizontal" action="">
        <div class="form-group">
          <label class="col-lg-2 control-label" for="firstname">
            <contextmenu:element name="firstname" menu="simpleForm">First Name:</contextmenu:element>
          </label>
          <div class="col-lg-4">
            <input name="firstname" id="firstname" type="text" />
            <c:set var="desc" value="${contextmenu:entryValue(cmsfn:asJCRNode(content), 'firstname.description')}" />
            ${desc}
            <c:set var="help" value="${contextmenu:entryValue(cmsfn:asJCRNode(content), 'firstname.help')}" />
            <c:if test="${not empty help}">
              <span class="help">
            <![CDATA[${help}<span class="help-pointer">&nbsp;</span>]]>
              </span>
            </c:if>
          </div>
        </div>
        <div class="form-group">
          <label class="col-lg-2 control-label" for="lastname">
            <contextmenu:element name="lastname" menu="simpleForm">Last Name:</contextmenu:element>
          </label>
          <div class="col-lg-4">
            <input name="lastname" id="lastname" type="text" />
            <c:set var="desc" value="${contextmenu:entryValue(cmsfn:asJCRNode(content), 'lastname.description')}" />
            ${desc}
            <c:set var="help" value="${contextmenu:entryValue(cmsfn:asJCRNode(content), 'lastname.help')}" />
            <c:if test="${not empty help}">
              <span class="help">
            <![CDATA[${help}<span class="help-pointer">&nbsp;</span>]]>
              </span>
            </c:if>
          </div>
        </div>
        <div class="form-group">
          <label class="col-lg-2 control-label" for="email">
            <contextmenu:element name="email" menu="simpleForm">Email:</contextmenu:element>
          </label>
          <div class="col-lg-4">
            <input name="email" id="email" type="text" />
            <c:set var="desc" value="${contextmenu:entryValue(cmsfn:asJCRNode(content), 'email.description')}" />
            ${desc}
            <c:set var="help" value="${contextmenu:entryValue(cmsfn:asJCRNode(content), 'email.help')}" />
            <c:if test="${not empty help}">
              <span class="help">
            <![CDATA[${help}<span class="help-pointer">&nbsp;</span>]]>
              </span>
            </c:if>
          </div>
        </div>
        <div class="form-group">
          <label class="col-lg-2 control-label" for="year">
            <contextmenu:element name="year" menu="simpleForm">Birth Year:</contextmenu:element>
          </label>
          <div class="col-lg-4">
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
            <c:set var="desc" value="${contextmenu:entryValue(cmsfn:asJCRNode(content), 'year.description')}" />
            ${desc}
            <c:set var="help" value="${contextmenu:entryValue(cmsfn:asJCRNode(content), 'year.help')}" />
            <c:if test="${not empty help}">
              <span class="help">
            <![CDATA[${help}<span class="help-pointer">&nbsp;</span>]]>
              </span>
            </c:if>
          </div>
        </div>
        <div class="form-group">
          <label class="col-lg-2 control-label" for="username">
            <contextmenu:element name="username" menu="simpleForm">Username:</contextmenu:element>
          </label>
          <div class="col-lg-4">
            <input name="username" id="username" type="text" />
            <c:set var="desc" value="${contextmenu:entryValue(cmsfn:asJCRNode(content), 'username.description')}" />
            ${desc}
            <c:set var="help" value="${contextmenu:entryValue(cmsfn:asJCRNode(content), 'username.help')}" />
            <c:if test="${not empty help}">
              <span class="help">
            <![CDATA[${help}<span class="help-pointer">&nbsp;</span>]]>
              </span>
            </c:if>
          </div>
        </div>
        <div class="form-group">
          <label class="col-lg-2 control-label" for="password">
            <contextmenu:element name="password" menu="simpleForm">Password:</contextmenu:element>
          </label>
          <div class="col-lg-4">
            <input name="password" id="password" type="password" />
            <c:set var="desc" value="${contextmenu:entryValue(cmsfn:asJCRNode(content), 'password.description')}" />
            ${desc}
            <c:set var="help" value="${contextmenu:entryValue(cmsfn:asJCRNode(content), 'password.help')}" />
            <c:if test="${not empty help}">
              <span class="help">
            <![CDATA[${help}<span class="help-pointer">&nbsp;</span>]]>
              </span>
            </c:if>
          </div>
        </div>
        <div class="form-group">
          <div class="col-lg-offset-2 col-lg-4">
            <button type="submit" class="btn btn-default">Submit</button>
          </div>
        </div>
      </form>
    </div>
  </section>
</jsp:root>