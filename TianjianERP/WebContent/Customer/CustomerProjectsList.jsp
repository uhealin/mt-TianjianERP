<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>客户相关项目</title>

<script type="text/javascript">  

function ext_init(){
	
	var tbar_customerProjectsList = new Ext.Toolbar({
		renderTo: 'gridDiv_customerProjectsList',
		items:[{
			text:'打印',
			cls:'x-btn-text-icon',
			icon:'${pageContext.request.contextPath}/img/print.gif',
			handler:function () {
				print_customerProjectsList();
			}
		}]
	});
	
} 


window.attachEvent('onload',ext_init);




</script>

</head>

<body >
<div style="height:expression(document.body.clientHeight-23);" >
<mt:DataGridPrintByBean name="customerProjectsList" outputData="true" outputType="invokeSearch" />
</div>
</body>
</html>