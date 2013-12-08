<jsp:root version="2.1" xmlns:jsp="http://java.sun.com/JSP/Page" xmlns:c="http://java.sun.com/jsp/jstl/core"
  xmlns:fn="http://java.sun.com/jsp/jstl/functions" xmlns:tagcloud="http://net.sourceforge.openutils/mgnlTagCloud"
  xmlns:fmt="urn:jsptld:http://java.sun.com/jsp/jstl/fmt">
  <jsp:directive.attribute name="name" required="false" rtexprvalue="true" />
  <jsp:directive.attribute name="repository" required="false" rtexprvalue="true" />
  <jsp:directive.attribute name="path" required="false" rtexprvalue="true" />
  <jsp:directive.attribute name="property" required="false" rtexprvalue="true" />
  <jsp:directive.attribute name="count" required="false" rtexprvalue="true" type="java.lang.Integer" />
  <jsp:directive.attribute name="cache" required="false" rtexprvalue="true" type="java.lang.Boolean" />
  <jsp:directive.attribute name="orderby" required="false" rtexprvalue="true" />
  <jsp:directive.attribute name="id" required="false" rtexprvalue="true" />
  <jsp:directive.attribute name="cssclass" required="false" rtexprvalue="true" />
  <jsp:directive.attribute name="steps" required="false" rtexprvalue="true" type="java.lang.Integer" />
  <jsp:directive.attribute name="cssprefix" required="false" rtexprvalue="true" />
  <jsp:directive.attribute name="url" required="false" rtexprvalue="true" />
  <jsp:directive.attribute name="showFreq" required="false" rtexprvalue="true" type="java.lang.Boolean" />
  <!-- defaults -->
  <c:set var="cssclass" value="${empty cssclass ? 'tagcloud' : cssclass}" />
  <c:set var="repository" value="${empty repository ? 'website' : repository}" />
  <c:set var="path" value="${empty path ? '/' : path}" />
  <c:set var="property" value="${empty property ? 'tags' : property}" />
  <c:set var="count" value="${empty count ? 20 : count}" />
  <c:set var="steps" value="${empty steps ? 10 : steps}" />
  <c:set var="cssprefix" value="${empty cssprefix ? 'tagsize' : cssprefix}" />
  <c:set var="cache" value="${empty cache ? true : cache}" />
  <c:set var="showFreq" value="${empty showFreq ? false : showFreq}" />
  <!-- get tag cloud -->
  <c:choose>
    <c:when test="${not empty name}">
      <c:set var="tagCloud" value="${tagcloud:named(name)}" />
    </c:when>
    <c:when test="${not empty cache and not cache}">
      <c:set var="tagCloud" value="${tagcloud:notcached(repository, path, property, count)}" />
    </c:when>
    <c:otherwise>
      <c:set var="tagCloud" value="${tagcloud:cached(repository, path, property, count)}" />
    </c:otherwise>
  </c:choose>
  <c:if test="${not empty tagCloud}">
    <!-- order tag cloud -->
    <c:if test="${not empty orderby}">
      <c:set var="orderTokens" value="${fn:split(orderby, ' ')}" />
      <c:set var="ascending" value="${fn:length(orderTokens) eq 1 or orderTokens[1] eq 'asc'}" />
      <c:choose>
        <c:when test="${orderTokens[0] eq 'name'}">
          <c:set var="tagCloud" value="${tagcloud:sortbyname(tagCloud, ascending)}" />
        </c:when>
        <c:when test="${orderTokens[0] eq 'count'}">
          <c:set var="tagCloud" value="${tagcloud:sortbycount(tagCloud, ascending)}" />
        </c:when>
      </c:choose>
    </c:if>
    <!-- normalization factor -->
    <c:set var="props" value="${tagcloud:props(tagCloud)}" />
    <c:set var="norm" value="${(props.max - props.min) / steps}" />
    <c:set var="norm" value="${norm eq 0 ? 1 : norm}" /> 
    <![CDATA[
    <ul class="${cssclass}"
    ]]>
    <c:if test="${not empty id}"> id="${id}"</c:if>
    <![CDATA[
    >
    ]]>
    <c:forEach var="tag" items="${tagCloud}">
      <fmt:formatNumber value="${((tag.value - props.min) / norm) + 1}" pattern="#" var="roundedSize" />
      <li class="tag ${cssprefix}${roundedSize}">
        <c:if test="${not empty url}">
          <c:url var="encurl" value="${url}${tag.key}" />
        <![CDATA[<a href="${encurl}" title="
tag.value:${tag.value} 
props.min:${props.min} 
norm:${norm} 
rounded:${((tag.value - props.min) / norm) +1}">]]>
        </c:if>
        <jsp:text>${tag.key} </jsp:text>
        <c:if test="${showFreq}">(${tag.value})</c:if>
        <c:if test="${not empty url}">
        <![CDATA[</a>]]>
        </c:if>
      </li>
    </c:forEach>
    <![CDATA[
    </ul>
    ]]>
  </c:if>
</jsp:root>