<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>动态显示</title>

<script type="text/javascript">
var uuid="${param.uuid}";
${extjsSupportFunction}
Ext.onReady(function(){
	var tbar_customer = new Ext.Toolbar({
		renderTo:'divBtn',
           items:[${extjs}]
        });  
	
});

</script>
</head>

<body style="margin: 0px;padding: 0px;">
	<div id="divBtn"></div>
	
<div style="height:expression(document.body.clientHeight-23);" >
	<mt:DataGridPrintByBean name="${tableid}"/>
</div>
<input type="hidden" name="formId" id="formId" value="${formId}">
<input type="hidden" name="tableId" id="tableId" value="${tableid}">

${extHtml}
</body>
</html>