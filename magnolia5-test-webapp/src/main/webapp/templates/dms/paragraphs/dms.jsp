<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="org.apache.commons.lang.StringUtils" %>
<%@ page import="info.magnolia.cms.core.Content" %>
<%@ page import="info.magnolia.context.MgnlContext" %>
<%@ page import="info.magnolia.cms.core.ItemType" %>
<%@ page import="info.magnolia.module.dms.beans.Document" %>
<%@ page import="info.magnolia.cms.core.search.Query" %>
<%@ page import="info.magnolia.cms.core.search.QueryResult" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="info.magnolia.cms.i18n.I18nContentSupportFactory" %>
<%
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm" );
    List foundDocuments = new ArrayList();

    // TODO - move this to a model class !
    // get the values from the current paragraph
    Content content = (Content) pageContext.getAttribute("content", PageContext.REQUEST_SCOPE);
    String link = I18nContentSupportFactory.getI18nSupport().getNodeData(content, "link").getString();
    String extension = I18nContentSupportFactory.getI18nSupport().getNodeData(content, "extension").getString();
    String query = I18nContentSupportFactory.getI18nSupport().getNodeData(content, "query").getString();

    List expressions = new ArrayList();

    // if a link is defined (folder or document)
    if(StringUtils.isNotEmpty(link)){
        Content node = MgnlContext.getHierarchyManager("dms").getContent(link);
        // if this is a folder search all containing documents
        if(node.getItemType().equals(ItemType.CONTENT)){
            expressions.add("jcr:path like '" + link + "/%'");
        }
        // a single file
        else if(Document.isDocument(node)){
            expressions.add("jcr:path like '" + link + "'");
        }
    }

    // search on file type
    if(StringUtils.isNotEmpty(extension)){
        expressions.add("extension = '" + extension + "'");
    }

    // additional query
    if(StringUtils.isNotEmpty(query)){
        expressions.add(query);
    }

    // join the expressions
    String where = StringUtils.join(expressions.iterator(), " and ");

    String queryStr= "SELECT * FROM nt:base WHERE " + where;

    Query q = MgnlContext.getQueryManager("dms").createQuery(queryStr, "sql");

    QueryResult result = q.execute();
    for(Iterator iter = result.getContent("mgnl:contentNode").iterator(); iter.hasNext();){
        Content node = (Content) iter.next();
        if(Document.isDocument(node)){
            Document doc = new Document(node);
            foundDocuments.add(
                new String[]{
                    "<img style=\"border: none;\"" +
                            " src=\"" + request.getContextPath() + doc.getMimeTypeIcon() + "\"" +
                            " alt=\"Document mime-type: "+ doc.getMimeType() + "\"" +
                            " />",
                    doc.getTitle() + "." + doc.getFileExtension(),
                    doc.getNodeData("subject").getString(),
                    dateFormat.format(doc.getModificationDate().getTime()),
                    doc.getLink()
                }
            );
        }
    }
    pageContext.setAttribute("foundDocuments", foundDocuments);
%>
<table>
    <c:forEach items="${foundDocuments}" var="doc">
        <tr>
            <td><a href="${pageContext.request.contextPath}/dms${doc[4]}">${doc[0]}</a></td>
            <td><a href="${pageContext.request.contextPath}/dms${doc[4]}">${doc[1]}</a></td>
            <td>${doc[2]}</td>
            <td>${doc[3]}</td>
        </tr>
    </c:forEach>
</table>
