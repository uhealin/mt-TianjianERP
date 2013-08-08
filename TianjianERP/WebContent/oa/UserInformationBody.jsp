<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp"%>
<html>
<head>
<title></title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
</head>

<%
	String myUserid = (String)session.getAttribute("myUserid");
%>

<body>
<form id="thisForm"
	action="${pageContext.request.contextPath}/user.do?method=Edit&UserOpt=2&id=<%=myUserid%>&myControl=myControl"
	method="post"></form>
</body>

<script>
	try{
		document.getElementById("thisForm").submit();
	}catch(e){
		alert(e);
	}

</script>

</html>
