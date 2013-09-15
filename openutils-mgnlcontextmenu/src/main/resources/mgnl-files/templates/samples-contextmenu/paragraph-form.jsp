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
      <dl>
      <!-- FIRST NAME -->
        <dt>
          <label for="firstname">
            <contextmenu:element name="firstname" menu="samples-form-label">First Name:</contextmenu:element>
          </label>
        </dt>
        <dd>
          <input name="firstname" id="firstname" type="text" />
        <!-- This is the name your mama called you when you were little. -->
          <c:set var="hint" value="${contextmenu:entryValue(content.JCRNode, 'firstname-hint')}" />
          <c:if test="${not empty hint}">
            <span class="hint">
            <![CDATA[${hint}<span class="hint-pointer">&nbsp;</span>]]>
            </span>
          </c:if>
        </dd>
      <!-- LAST NAME -->
        <dt>
          <label for="lastname">
            <contextmenu:element name="lastname" menu="samples-form-label">Last Name:</contextmenu:element>
          </label>
        </dt>
        <dd>
          <input name="lastname" id="lastname" type="text" />
        <!-- This is the name your sergeant called you when you went through bootcamp. -->
          <c:set var="hint" value="${contextmenu:entryValue(content.JCRNode, 'lastname-hint')}" />
          <c:if test="${not empty hint}">
            <span class="hint">
            <![CDATA[${hint}<span class="hint-pointer">&nbsp;</span>]]>
            </span>
          </c:if>
        </dd>
      <!-- EMAIL -->
        <dt>
          <label for="email">
            <contextmenu:element name="email" menu="samples-form-label">Email:</contextmenu:element>
          </label>
        </dt>
        <dd>
          <input name="email" id="email" type="text" />
        <!-- The thing with the @ symbol and the dot com at the end. -->
          <c:set var="hint" value="${contextmenu:entryValue(content.JCRNode, 'email-hint')}" />
          <c:if test="${not empty hint}">
            <span class="hint">
            <![CDATA[${hint}<span class="hint-pointer">&nbsp;</span>]]>
            </span>
          </c:if>
        </dd>
      <!-- BIRTH YEAR -->
        <dt>
          <label for="year">
            <contextmenu:element name="year" menu="samples-form-label">Birth Year:</contextmenu:element>
          </label>
        </dt>
        <dd>
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
        <!-- Pick a famous year to be born in. -->
          <c:set var="hint" value="${contextmenu:entryValue(content.JCRNode, 'year-hint')}" />
          <c:if test="${not empty hint}">
            <span class="hint">
            <![CDATA[${hint}<span class="hint-pointer">&nbsp;</span>]]>
            </span>
          </c:if>
        </dd>
      <!-- USERNAME -->
        <dt>
          <label for="username">
            <contextmenu:element name="username" menu="samples-form-label">Username:</contextmenu:element>
          </label>
        </dt>
        <dd>
          <input name="username" id="username" type="text" />
        <!-- Between 4-12 characters. -->
          <c:set var="hint" value="${contextmenu:entryValue(content.JCRNode, 'username-hint')}" />
          <c:if test="${not empty hint}">
            <span class="hint">
            <![CDATA[${hint}<span class="hint-pointer">&nbsp;</span>]]>
            </span>
          </c:if>
        </dd>
      <!-- PASSWORD -->
        <dt>
          <label for="password">
            <contextmenu:element name="password" menu="samples-form-label">Password:</contextmenu:element>
          </label>
        </dt>
        <dd>
          <input name="password" id="password" type="password" />
        <!-- Between 5-13 characters, but not 7. Never 7. -->
          <c:set var="hint" value="${contextmenu:entryValue(content.JCRNode, 'password-hint')}" />
          <c:if test="${not empty hint}">
            <span class="hint">
            <![CDATA[${hint}<span class="hint-pointer">&nbsp;</span>]]>
            </span>
          </c:if>
        </dd>
        <dt class="button">&amp;nbsp;</dt>
        <dd class="button">
          <input type="submit" class="button" value="Submit" />
        </dd>
      </dl>
    </div>
  </section>
</jsp:root>