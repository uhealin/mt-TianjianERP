<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/calendar_include.jsp" %>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>客户评级历史记录</title>

<script type="text/javascript">  

function ext_init(){
	
	var tbar_levellist = new Ext.Toolbar({
		renderTo: 'gridDiv_clevellist',
		items:[{
			text:'打印',
			cls:'x-btn-text-icon',
			icon:'${pageContext.request.contextPath}/img/print.gif',
			handler:function () {
				print_clevellist();
			}
		},'-',{
			text:'关闭',
			icon:'${pageContext.request.contextPath}/img/close.gif',
           	handler:function(){
           		closeTab(parent.tab);
			}
		}]
	});
	
} 


function grid_dblclick(trObj,tableId) {

}
window.attachEvent('onload',ext_init);




</script>

</head>
<body >

<input type="hidden" name="customerid" id="customerid" value="${customerid}">

<div style="height:expression(document.body.clientHeight-23);" >
<mt:DataGridPrintByBean name="clevellist"  />
</div>


</body>
</html>
