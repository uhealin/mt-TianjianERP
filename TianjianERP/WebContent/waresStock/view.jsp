<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/calendar_include.jsp"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">

<title>物品入库登记</title>
<script type="text/javascript">


Ext.onReady(function (){
	new Ext.Toolbar({
			renderTo: "divBtn",
			height:30,
			defaults: {autoHeight: true,autoWidth:true},
	       items:[ { 
	        text:'返回',
	        icon:'${pageContext.request.contextPath}/img/back.gif', 
	        handler:function(){
				window.history.back();
			}
	  	},'->']
	});
	 
});
</script>

<style type="text/css">

.before{
	border: 0px;
	background-color: #FFFFFF !important;
}

.data_tb {
	background-color: #ffffff;
	text-align:center;
	margin:0 0px;
	width:60%;
	border:#8db2e3 1px solid; 
	BORDER-COLLAPSE: collapse; 
	margin-top: 20px;
}
.data_tb_alignright {	
	BACKGROUND: #e4f4fe; 
	white-space:nowrap;
	padding:5px;
	border-top: #8db2e3 1px solid;
	border-left: #8db2e3 1px solid;
	BORDER-RIGHT: #8db2e3 1px solid;
	BORDER-BOTTOM: #8db2e3 1px solid; 
	height:30px;
	background-color: #d3e1f1;
	font-size: 13px;
	font-family:"宋体";
}
.data_tb_content {
	PADDING-LEFT: 2px; 
	BORDER-TOP: #8db2e3 1px solid; 
	BORDER-LEFT: #8db2e3 1px solid;
	BORDER-RIGHT: #8db2e3 1px solid;
	BORDER-BOTTOM: #8db2e3 1px solid;  
	WORD-BREAK: break-all; 
	TEXT-ALIGN: left; 
	WORD-WRAP: break-word
}

</style>
</head>
<body leftmargin="0" topmargin="0">
<div id="divBtn" ></div>
<div style="overflow: scroll;height: 90%;overflow-x:hidden; ">
<form name="thisForm" method="post" action="" id="thisForm" > 

<input type="hidden" id="uuid" name="uuid" value="${waresStock.uuid}" />

	<span class="formTitle" ><br>物品入库信息</span>
	<table border="0"  style="line-height: 28px"   class="data_tb" align="center">
		<tr>
			<td colspan="2" style="height: 15px;" class="data_tb_alignright"> 
				物品入库信息
			</td>
		</tr>
		<tr >
			<td align="right"  class="data_tb_alignright">名称：</td>
			<td class="data_tb_content" width="80%">
				 ${waresStock.name}</td>
		</tr>
		
		<tr> 
			<td class="data_tb_alignright" align="right">描述：</td>
			<td class="data_tb_content">
				 ${waresStock.remark } </td>
		</tr>
		
		<tr>
			<td class="data_tb_alignright" align="right">用品类别：</td>
			<td class="data_tb_content"> ${waresStock.type} </td>
		</tr>
		
		<tr>  
			<td class="data_tb_alignright" align="right" >编码：</td>
			<td class="data_tb_content"> ${waresStock.coding} </td>
		</tr>
		<tr>
			<td class="data_tb_alignright" align="right" >计量单位：</td>
			<td class="data_tb_content"> ${waresStock.unitUnit}</td>
		</tr>
		<tr>
			<td class="data_tb_alignright" align="right" >最低库存：</td>
			<td class="data_tb_content"> ${waresStock.lowestStock} </td>
		</tr>
		
		<tr> 	 
			<td class="data_tb_alignright" align="right" >最低警戒库存：</td>
			<td class="data_tb_content"> ${waresStock.lowestWarnStock} </td>
		</tr>
		<tr>
			<td class="data_tb_alignright" align="right" >最高警戒库存：</td>
			<td class="data_tb_content"> ${waresStock.highestWarnStock}</td>
		</tr>
		
		<tr>  
			<td class="data_tb_alignright" align="right" >所属部门：</td>
			<td class="data_tb_content"> ${waresStock.departmentId}</td>
		</tr>
	</table>
	
		<center><div id="sbtBtn" ></div></center>
		
 </form>

</div>
<script type="text/javascript">
new Validation('thisForm');

function mySubmit() {
	
	if (!formSubmitCheck('thisForm')) return ;

	if(document.getElementById("uuid").value!="") {
	
		document.thisForm.action="${pageContext.request.contextPath}/waresStock.do?method=update";
	} else {
	
		document.thisForm.action="${pageContext.request.contextPath}/waresStock.do?method=add";
	}
	document.thisForm.submit();
}

</script>

</body>
</html>
