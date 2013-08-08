<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>高管简历</title>

<script type="text/javascript">  

function ext_init(){
	
	var tbar_managerlist = new Ext.Toolbar({
		renderTo: 'gridDiv_managerlist',
		items:[{
			text:'新增', 
			cls:'x-btn-text-icon',
	   		icon:'${pageContext.request.contextPath}/img/add.gif',
			handler:function () {
				goAdd();
			}
		},'-',{
            text:'修改',
            cls:'x-btn-text-icon',
            icon:'/AuditSystem/img/edit.gif',
           	handler:function(){
				goUpdate();
			}
        },'-',{
            text:'删除',
            cls:'x-btn-text-icon',
            icon:'${pageContext.request.contextPath}/img/delete.gif',
            handler:function () {
            	goDelete();
            }
        },'-',{
			text:'打印',
			cls:'x-btn-text-icon',
			icon:'${pageContext.request.contextPath}/img/print.gif',
			handler:function () {
				print_managerlist();
			}
		},'-',{
			text:'关闭',
			cls:'x-btn-text-icon',
            icon:'${pageContext.request.contextPath}/img/close.gif',
			handler:function () {
				closeTab(parent.tab);
				//parent.tab.remove(parent.tab.getActiveTab()); 
			}
		}]
	});
	
} 



window.attachEvent('onload',ext_init);




</script>


</head>
<body>

<div style="height:expression(document.body.clientHeight-23);" >
<mt:DataGridPrintByBean name="managerlist" outputData="true" outputType="invokeSearch" />
</div>
</body>
<script>
function goAdd(){

    window.location="${pageContext.request.contextPath}/Customer/manageradd.jsp?departid=" + <%=request.getParameter("departid") %>;
	       
} 
function goView(){
	
	var choose_manager = document.getElementById("chooseValue_managerlist").value;

	
	if(choose_manager == ""){
		alert("请选择要查看的记录！");
	} else {		
		window.location="${pageContext.request.contextPath}/manager1.do?method=exitManager&autoid=" + choose_manager+"&action=look";		
	}       
}   
function goUpdate(){
	var choose_manager = document.getElementById("chooseValue_managerlist").value;

	
	if(choose_manager == ""){
		alert("请选择要修改记录！");
	} else {		
		window.location="${pageContext.request.contextPath}/manager1.do?method=exitManager&autoid=" + choose_manager+"&action=update&departid=" + <%=request.getParameter("departid") %>;		
	}
}

function goDelete(){
	var choose_manager = document.getElementById("chooseValue_managerlist").value;

	if(choose_manager == ""){
		alert("请选择要删除的记录！");
	} else {
		if(confirm("您的操作可能会造成数据丢失，您确定要删除该记录吗？","提示")){
			window.location="${pageContext.request.contextPath}/manager1.do?method=removeManager&autoid=" + choose_manager+"&customerid=" + <%=request.getParameter("departid") %>;
		}
	}
} 




</script>



</html>