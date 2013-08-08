<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" errorPage="../hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>
<html>
<head>
<title>查看流程图</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">

</head>
<body >
	<c:choose>
		<c:when test="${id!=null && id != ''}">
			<img src="${pageContext.request.contextPath}/process.do?method=viewImageByInstanceId&id=${id}" style="position:absolute;left:0px;top:0px;">
			<div style="position:absolute;border:1px solid red;left:${ac.x}px;top:${ac.y}px;width:${ac.width}px;height:${ac.height}px;"></div>
		</c:when>
		<c:otherwise>
			<img src="${pageContext.request.contextPath}/process.do?method=viewImageByDefinitionId&pdId=${pdId}" style="position:absolute;left:0px;top:0px;">
		</c:otherwise>
	</c:choose>
</body>
</html>
