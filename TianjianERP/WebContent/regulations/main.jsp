<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>regulations  list method    main.jsp</title>
</head>
<body>
<h1>规章制度中心</h1>
<table>
		<tr>
		    <th>标题</th>
		     <th>内容</th>
		  </tr>
	<c:forEach items="${regulationsList}" var="regulations">
		  <tr>
		    <th>${regulations.title}</th>
		     <th>${regulations.contents}</th>
		  </tr>
	</c:forEach>
</table>


</body>
</html>