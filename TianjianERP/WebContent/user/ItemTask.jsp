<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"  errorPage="/hasNoRight.jsp" %>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>个人项目底稿分工</title>
<script type="text/javascript">


Ext.onReady(function (){
	new Ext.Toolbar({
			renderTo: "divBtn",
			height:30,
			defaults: {autoHeight: true,autoWidth:true},
	       items:[{ 
	        text:'返回',
	        icon:'${pageContext.request.contextPath}/img/back.gif', 
	        handler:function(){
				window.history.back();
			}
	  	}]
	});
	 
});
</script>
</head>
<body leftmargin="0" topmargin="0">
<div id="divBtn" ></div>
<form name="thisForm" method="post" action="">
<span class="formTitle" >[${name}]的底稿分工<br/><br/> </span><br>
<div style="height:expression(document.body.clientHeight-68);" >	
	<mt:DataGridPrintByBean name="task"  message="请选择单位编号" />
</div>	
</form>
</body>
</html>