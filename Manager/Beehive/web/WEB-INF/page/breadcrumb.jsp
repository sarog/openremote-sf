<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="../../common/taglibs.jsp" %>
<div id="breadcrumbs">
<c:set var="breadcrumb" value='${fn:split(path,"/")}'></c:set>

<a class="path_t" href="history.htm"><span class="path_text">Beehvie</span></a>
<span class="path_t">/</span>
<c:set var="getModelsUrl" value='history.htm?method=getModels&path='></c:set>
<c:set var="getContentUrl" value='history.htm?method=getContent&path='></c:set>
<c:forEach items='${breadcrumb}' var="name" varStatus="status">
   <c:if test="${status.count eq 1}">
      <a class="" href="${getModelsUrl}${name}">${name}</a>
   </c:if>
   <c:if test="${status.count eq 2}">
	   <a class="" href="${getContentUrl}${name}">${name}</a>
   </c:if>
   <c:if test='${status.count lt fn:length(breadcrumb)}'>
      <span class="path_t">/</span>
   </c:if>
</c:forEach>

</div>