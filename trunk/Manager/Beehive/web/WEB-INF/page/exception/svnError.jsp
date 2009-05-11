<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="../../../common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page import="java.util.Enumeration,java.util.Iterator"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>SVNException</title>
</head>
<body>
	<c:set value="${exception}" var="ee" />
	<jsp:useBean id="ee" type="org.openremote.beehive.exception.SVNException" />
	SVNException:
	<%=ee.getMessage()%>
</body>
</html>