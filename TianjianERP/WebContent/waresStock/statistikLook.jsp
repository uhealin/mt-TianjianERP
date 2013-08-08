<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8" errorPage="/hasNocenter.jsp"%>
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
	width:80%;
	border:#8db2e3 1px solid; 
	BORDER-COLLAPSE: collapse; 
	margin-top: 20px;
}
.data_tb_aligncenter {	
	BACKGROUND: #e4f4fe; 
	white-space:nowrap;
	padding:5px;
	border-top: #8db2e3 1px solid;
	border-left: #8db2e3 1px solid;
	BORDER-center: #8db2e3 1px solid;
	BORDER-BOTTOM: #8db2e3 1px solid; 
	height:30px;
	background-color: #d3e1f1;
 	TEXT-ALIGN: center; 
	font-size: 13px;
	font-family:"宋体";
}
.data_tb_content {
	PADDING-LEFT: 2px; 
	BORDER-TOP: #8db2e3 1px solid; 
	BORDER-LEFT: #8db2e3 1px solid;
	BORDER-center: #8db2e3 1px solid;
	BORDER-BOTTOM: #8db2e3 1px solid;  
	WORD-BREAK: break-all; 
	TEXT-ALIGN: center; 
	WORD-WRAP: break-word
}

</style>
</head>
<body leftmargin="0" topmargin="0">
<div id="divBtn" ></div>
<form name="thisForm" method="post" action="" id="thisForm" > 

<input type="hidden" id="uuid" name="uuid" value="${waresStock.uuid}" />

	<span class="formTitle" ><br>物品入库信息</span>
	<table border="0"  style="line-height: 28px"   class="data_tb" align="center" >
		 
		 <tr>
			<td class="data_tb_aligncenter"  width="30%">用品名称</td>
			<td class="data_tb_aligncenter" >用品类别</td>
			<td class="data_tb_aligncenter"  >计量单位</td>
			<td class="data_tb_aligncenter" >当前库存</td>
			<td class="data_tb_aligncenter"  >采购数</td>
			<td class="data_tb_aligncenter"  >领用数</td>
			<td class="data_tb_aligncenter"  >正在领用数</td>
			<td class="data_tb_aligncenter"  >报废数</td>
			<td class="data_tb_aligncenter"  >归还数</td>
		 </tr>
		  
		 <tr>
			<td class="data_tb_content"   width="30%">用品名称</td>
			<td class="data_tb_content" >用品类别</td>
			<td class="data_tb_content"  >计量单位</td>
			<td class="data_tb_content" >当前库存</td>
			<td class="data_tb_content"  >采购数</td>
			<td class="data_tb_content"  >领用数</td>
			<td class="data_tb_content"  >正在领用数</td>
			<td class="data_tb_content"  >报废数</td>
			<td class="data_tb_content"  >归还数</td>
		 </tr>
	</table>
 </form>


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
