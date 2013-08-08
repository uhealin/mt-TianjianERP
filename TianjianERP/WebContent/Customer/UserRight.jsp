<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"  errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>客户权限</title>
<script Language=JavaScript>
function ext_init(){

	var tbar_customer = new Ext.Toolbar({
		renderTo: "divBtn",
		defaults: {autoHeight: true,autoWidth:true},
 		items:[{
			text:'保存客户权限',
			id:'btn-query',
			cls:'x-btn-text-icon',
	   		icon:'${pageContext.request.contextPath}/img/save.gif',
			handler:function () {
				var customers = getChooseValue("${tmpName}");
				document.getElementById("customers").value = customers;
				thisForm.action = "${pageContext.request.contextPath}/customer.do?method=saveUserRight";
				thisForm.submit();				
			}
		},'-',{
			text:'打印',
			cls:'x-btn-text-icon',
			icon:'${pageContext.request.contextPath}/img/print.gif',
			handler:function () {
				//getPrintln();
				print_${tmpName }();
			}
		},'-',{
			text:'返回',
			cls:'x-btn-text-icon',
            icon:'${pageContext.request.contextPath}/img/back.gif',
			handler:function () {
				window.history.back();
				//closeTab(parent.tab);
			}
		},'-',new Ext.Toolbar.Fill()]
	});
        
}

window.attachEvent('onload',ext_init);
</script>
</head>
<body>
<div id="divBtn"></div>
<form name="thisForm" method="post" action="" id="thisForm" >
<div style="height: expression(  document.body.clientHeight - 27 );overflow: auto" >
	<mt:DataGridPrintByBean name="${tmpName }"  message="请选择账套编号"  />
</div>
<input type="hidden" id = "customers" name = "customers" value="" >
<input type="hidden" id = "userid" name = "userid" value="${userid }" >
</form>
</body>
</html>