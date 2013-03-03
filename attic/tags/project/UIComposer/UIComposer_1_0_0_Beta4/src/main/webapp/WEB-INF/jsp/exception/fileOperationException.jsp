<%response.setStatus(500); %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=UTF8"
    pageEncoding="UTF8"%>
<c:set value="${exception}" var="ee" />
<jsp:useBean id="ee" type="org.openremote.irbuilder.exception.FileOperationException" />
   FileOperationException:
   <%=ee.getMessage()%>
