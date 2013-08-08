<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>工作流节点</title>
</head>

	<frameset cols="250,10,*" id="rightKingFrame" frameborder="0" border="0" framespacing="0" bordercolor="#FFFFFF">
	  <frame src="${pageContext.request.contextPath}/oa/task.do?method=tree" name="TaskLeftFrame" scrolling="auto" id="TaskLeftFrame" title="TaskLeftFrame" noresize="noresize"/>
	  <frame src="${pageContext.request.contextPath}/oa/task.do?method=bar" name="bar" id="bar" scrolling="no" noresize="noresize"/>
	  <frame src="${pageContext.request.contextPath}/oa/task.do?method=list&parentTaskId=${taskId}" name="TaskMainFrame" id="TaskMainFrame" title="TaskMainFrame" />
	</frameset>

<noframes>
	<body>
	很抱歉，阁下使用的浏览器不支援框架功能，请转用新的浏览器。
	</body>
</noframes>
</html>


