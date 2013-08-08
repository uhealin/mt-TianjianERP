<%@page import="java.util.UUID"%>
<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/calendar_include.jsp"%>
<html>
<head>
<title>公告</title>
<script type="text/javascript">
	Ext.onReady(function() {
		
		new Ext.Toolbar({
			renderTo : "divBtn",
			height : 30,
			defaults : {
				autoHeight : true,
				autoWidth : true
			},
			items : [ {
				id : 'saveBtn',
				text : '返回',
				icon : '${pageContext.request.contextPath}/img/back.gif',
				handler : function() {
					window.history.back();
				}
			}, '-', {
				text : '关闭',
				icon : '${pageContext.request.contextPath}/img/close.gif',
				handler : function() {
					closeTab(parent.tab);
				}
			}, '->' ]
		});

	});
</script>

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
	font-size: 50px;
	
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
	width: 99%; 
	height:75%;
	word-wrap:break-word; 
	margin-left:10px;
	overflow:hidden; 
}
</style>
</head>
<body>
	<div id="divBtn"> </div>
	<center>
		<div align="right" class="title" ><b>${proclamation.title }</b></div>
	</center>
		<div align="right" class="publish" >
			<label style="margin-right: 20px;vertical-align: middle;line-height: 20px;">
					发布部门：${proclamation.departmentId } &nbsp;
					发布人：${proclamation.userId }&nbsp; 
					发布于：${proclamation.publishDate }</label>
		</div>
	<div class="content" >
		<pre >
			${proclamation.content} 
		</pre>
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
	
</body>
</html>