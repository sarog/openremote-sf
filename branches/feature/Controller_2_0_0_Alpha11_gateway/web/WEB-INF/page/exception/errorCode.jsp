
<%@ page language="java" contentType="text/html; charset=UTF8" pageEncoding="UTF8" 
    import="org.openremote.controller.exception.ButtonCommandException" %>
<%
	ButtonCommandException e = (ButtonCommandException)request.getAttribute("exception");
	response.setStatus(e.getErrorCode());
%>
Exception(<%=e.getErrorCode()%>): <%=e.getMessage()%>