<jsp:root version="2.0" xmlns:jsp="http://java.sun.com/JSP/Page" xmlns:cms="urn:jsptld:cms-taglib"
  xmlns:c="urn:jsptld:http://java.sun.com/jsp/jstl/core" xmlns:fmt="urn:jsptld:http://java.sun.com/jsp/jstl/fmt"
  xmlns:fn="urn:jsptld:http://java.sun.com/jsp/jstl/functions" xmlns:cmsu="urn:jsptld:cms-util-taglib"
  xmlns:cmsfn="http://www.magnolia.info/tlds/cmsfn-taglib.tld" xmlns:media="http://net.sourceforge.openutils/mgnlMedia"
  xmlns:mu="mgnlutils" xmlns:mcmenu="mgnlcontextmenu">
  <jsp:directive.page contentType="text/html; charset=UTF-8" session="false" />
  <cms:setNode var="node" />
  <pre>
    <![CDATA[&lt;mcmenu:element name="firstname" menu="samples-form-label"&gt;First Name:&lt;/mcmenu:element&gt;]]>
  </pre>
  <pre>
    <![CDATA[&lt;c:set var="hint" value="${'$'}{mcmenu:entryValue(node, 'firstname-hint')}" /&gt;]]>
  </pre>
  <br />
  <p style="font:normal 12px/15px Arial;">Tab or click through the fields to reveal the hints.</p>
  <dl>
    <!-- FIRST NAME -->
    <dt>
      <label for="firstname">
        <mcmenu:element name="firstname" menu="samples-form-label">First Name:</mcmenu:element>
      </label>
    </dt>
    <dd>
      <input name="firstname" id="firstname" type="text" />
      <!-- This is the name your mama called you when you were little. -->
      <c:set var="hint" value="${mcmenu:entryValue(node, 'firstname-hint')}" />
      <c:if test="${not empty hint}">
        <span class="hint">
          <![CDATA[${hint}<span class="hint-pointer">&nbsp;</span>]]>
        </span>
      </c:if>
    </dd>
    <!-- LAST NAME -->
    <dt>
      <label for="lastname">
        <mcmenu:element name="lastname" menu="samples-form-label">Last Name:</mcmenu:element>
      </label>
    </dt>
    <dd>
      <input name="lastname" id="lastname" type="text" />
      <!-- This is the name your sergeant called you when you went through bootcamp. -->
      <c:set var="hint" value="${mcmenu:entryValue(node, 'lastname-hint')}" />
      <c:if test="${not empty hint}">
        <span class="hint">
          <![CDATA[${hint}<span class="hint-pointer">&nbsp;</span>]]>
        </span>
      </c:if>
    </dd>
    <!-- EMAIL -->
    <dt>
      <label for="email">
        <mcmenu:element name="email" menu="samples-form-label">Email:</mcmenu:element>
      </label>
    </dt>
    <dd>
      <input name="email" id="email" type="text" />
      <!-- The thing with the @ symbol and the dot com at the end. -->
      <c:set var="hint" value="${mcmenu:entryValue(node, 'email-hint')}" />
      <c:if test="${not empty hint}">
        <span class="hint">
          <![CDATA[${hint}<span class="hint-pointer">&nbsp;</span>]]>
        </span>
      </c:if>
    </dd>
    <!-- BIRTH YEAR -->
    <dt>
      <label for="year">
        <mcmenu:element name="year" menu="samples-form-label">Birth Year:</mcmenu:element>
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
      <c:set var="hint" value="${mcmenu:entryValue(node, 'year-hint')}" />
      <c:if test="${not empty hint}">
        <span class="hint">
          <![CDATA[${hint}<span class="hint-pointer">&nbsp;</span>]]>
        </span>
      </c:if>
    </dd>
    <!-- USERNAME -->
    <dt>
      <label for="username">
        <mcmenu:element name="username" menu="samples-form-label">Username:</mcmenu:element>
      </label>
    </dt>
    <dd>
      <input name="username" id="username" type="text" />
      <!-- Between 4-12 characters. -->
      <c:set var="hint" value="${mcmenu:entryValue(node, 'username-hint')}" />
      <c:if test="${not empty hint}">
        <span class="hint">
          <![CDATA[${hint}<span class="hint-pointer">&nbsp;</span>]]>
        </span>
      </c:if>
    </dd>
    <!-- PASSWORD -->
    <dt>
      <label for="password">
        <mcmenu:element name="password" menu="samples-form-label">Password:</mcmenu:element>
      </label>
    </dt>
    <dd>
      <input name="password" id="password" type="password" />
      <!-- Between 5-13 characters, but not 7. Never 7. -->
      <c:set var="hint" value="${mcmenu:entryValue(node, 'password-hint')}" />
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
</jsp:root>