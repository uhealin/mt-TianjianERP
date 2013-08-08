<%@page import="java.util.UUID"%>
<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/calendar_include.jsp"%>
<html>
<head>
<title>公告</title>

<style type="text/css">
.title{
	border-width: 1px;
	border-style: solid;
	border-color: #ccdeea;
	width: 100%; 
	height:25px;
	text-align: center;
	background-color: #f2f7f9;
	line-height: 20px;
	font-size: 20px !important;
	
}
.publish{
	border-width: 1px;
	border-style: solid;
	border-color: #ccdeea;
	width: 100%; 
	height:23px;
	background-color: #f0f0f0;
}
.content{
	border-width: 1px;
	border-style: solid;
	border-color: #ccdeea;
	width: 100%; 
	height:40%;
}
</style>
</head>
<body>
	<center>
		<div align="right" class="title" ><b>${proclamation.title }</b></div>
	</center>
		<div align="right" class="publish" >
			<label style="margin-right: 20px;vertical-align: middle;line-height: 20px;">
					发布部门：${proclamation.departmentId } &nbsp;
					发布人：${proclamation.userId }&nbsp; 
					发布于：${proclamation.publishDate }</label>
		</div>
	<div class="content">
		<!-- <pre>		</pre>先不使用(保证格式) -->
			${proclamation.content} 
	</div>
	<div style="margin-left: 50px;margin-top:10px; margin-bottom:45px;">
	<table>
		<tr>
			<td style="vertical-align: top;">附件：</td>
			<td>
				<script type="text/javascript">
						attachInit('proclamation','${proclamation.fileName}',"showButton:false,remove:false");
				</script>
			</td>
		</tr>
	</table>
		</div>
	<c:forEach items="${nodeList}" var="node">
		<table border="0" cellSpacing="0" cellPadding="0" width="98%"
			align="center">
			<tr>
				<td width="100%" align="middle"><img
					src="${pageContext.request.contextPath}/images/downline.jpg"></td>
			</tr>
		</table>
		<table border="0" cellSpacing="1" cellPadding="2" width="95%"
			bgColor="#99BBE8" align="center" class="appTable">
			<tr bgColor="#DDE9F9">
				<td colSpan="2" style="height: 25px;"><b><span style="width: 30%;margin-left: 10px;">${node.nodeName}</span>
				<span style="width: 15%;">处理人：${node.dealUserId}</span> <span
					style="width: 25%;">处理时间：${node.dealTime}</span> </b></td>
			</tr>
			<c:forEach items="${node.formList}" var="form">
	
				<tr bgColor="#ffffff" style="height: 20px;">
					<td width="20%" align="right">${form.key}：</td>
					<td width="80%" style="padding-left: 5px;"><c:choose>
						<c:when test="${form.property != ''}">
							<a href=# onclick="fileOpen('${form.property}','${form.value}')">${form.value}
							</a>
						</c:when>
						<c:otherwise>
										${form.value}
									</c:otherwise>
					</c:choose></td>
				</tr>
			</c:forEach>
		</table>
	</c:forEach>
	<br>
</body>
</html>