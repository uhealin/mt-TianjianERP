<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/calendar_include.jsp" %>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>商机录入</title>

<script type="text/javascript">  

function goEdit() {
	if(document.getElementById("chooseValue_businessAudit").value=="") {
		alert("请选择要修改的客户！");
	} else {
		window.location="customer.do?method=businessAuditAdd&autoid="+document.getElementById("chooseValue_businessAudit").value;
	}
}


function ext_init(){
	
	var tbar_levellist = new Ext.Toolbar({
		renderTo: 'gridDiv_businessAudit',
		items:[{
	            text:'审核',
	            cls:'x-btn-text-icon',
	            icon:'/AuditSystem/img/edit.gif',
	            handler:function(){
	            	goEdit();
				}
       		},'-',{
			text:'打印',
			cls:'x-btn-text-icon',
			icon:'${pageContext.request.contextPath}/img/print.gif',
			handler:function () {
				print_businessAudit();
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


<div style="height:expression(document.body.clientHeight-23);" >
<mt:DataGridPrintByBean name="businessAudit"  />
</div>


</body>
</html>
