<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>客户潜在项目跟踪</title>

<script type="text/javascript">  

function ext_init(){
	
	var tbar_latencyCustomerList = new Ext.Toolbar({
		renderTo: 'gridDiv_latencyCustomerList',
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
				print_latencyCustomerList();
			}
		}]
	});
	
} 



window.attachEvent('onload',ext_init);




</script>








</head>



<body >

<div style="height:expression(document.body.clientHeight-23);" >
<mt:DataGridPrintByBean name="latencyCustomerList" outputData="true" outputType="invokeSearch" />
</div>
</body>

<script>
//添加客户潜在项目
function goAdd(){
	
	if("${customerid}"==""){	
		window.location="${pageContext.request.contextPath}/Customer/LatencyCustomerAdd.jsp?isAll=all" ;
	}else{
		window.location="${pageContext.request.contextPath}/Customer/LatencyCustomerAdd.jsp?customerid="+"${customerid}" ;
	}	       
}                                 

//修改客户潜在项目
function goUpdate(){
	var choose_latencyCustomerList = document.getElementById("chooseValue_latencyCustomerList").value;

	if(choose_latencyCustomerList == ""){
		alert("请选择要修改的客户潜在项目！");
	} else {		
		window.location="${pageContext.request.contextPath}/latencyCustomer.do?method=exitLatencyCustomer&autoid=" + choose_latencyCustomerList+"&customerid="+"${customerid}" ;		
	}
}

//删除客户潜在项目
function goDelete(){
	var choose_latencyCustomerList = document.getElementById("chooseValue_latencyCustomerList").value;

	if(choose_latencyCustomerList == ""){
		alert("请选择要删除的客户潜在项目！");
	} else {
		if(confirm("您的操作可能会造成数据丢失，您确定要删除该记录吗？","提示")){
			window.location="${pageContext.request.contextPath}/latencyCustomer.do?method=removeLatencyCustomer&autoid=" + choose_latencyCustomerList+"&customerid="+"${customerid}" ;
		}
	}
}

</script>
</html>
