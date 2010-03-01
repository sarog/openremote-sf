<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="../../common/taglibs.jsp" %>
<div id="breadcrumbs">
<c:set var="breadcrumb" value='${fn:split(path,"/")}'></c:set>
<a class="path_t" href="#"><span class="path_text">Beehvie</span></a>
<span class="path_t">/</span>
<c:forEach items='${breadcrumb}' var="name" varStatus="status">
   <a class="" href="#">${name}</a>
   <c:if test='${status.count lt fn:length(breadcrumb)}'>
      <span class="path_t">/</span>
   </c:if>
</c:forEach>

</div>