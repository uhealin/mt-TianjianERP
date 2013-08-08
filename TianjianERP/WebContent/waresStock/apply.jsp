<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/calendar_include.jsp"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">

<title>物品申领</title>
<script type="text/javascript">
	Ext.onReady(function() {
		new Ext.Toolbar({
			renderTo : "divBtn",
			height : 30,
			defaults : {
				autoHeight : true,
				autoWidth : true
			},
			items : [ {
				id : 'saveBtn',
				text : '保存',
				icon : '${pageContext.request.contextPath}/img/save.gif',
				handler : function() {
					mySubmit();
				}
			}, '-', {
				text : '返回',
				icon : '${pageContext.request.contextPath}/img/back.gif',
				handler : function() {
					window.history.back();
				}
			}, '->' ]
		});

		new ExtButtonPanel({
			desc : '',
			renderTo : 'sbtBtn',
			items : [ {
				text : '保存',
				id : 'appSubmit23',
				icon : '${pageContext.request.contextPath}/img/receive.png',
				scale : 'large',
				handler : function() {
					mySubmit();
				}
			}, {
				text : '返回',
				id : 'appSubmit25',
				icon : '${pageContext.request.contextPath}/img/back_32.png',
				scale : 'large',
				handler : function() {
					//closeTab(parent.tab);
					window.history.back();
				}
			} ]
		});

	});
</script>

<style type="text/css">
.before {
	border: 0px;
	background-color: #FFFFFF !important;
}

.data_tb {
	background-color: #ffffff;
	text-align: center;
	margin: 0 0px;
	width: 60%;
	border: #8db2e3 1px solid;
	BORDER-COLLAPSE: collapse;
	margin-top: 20px;
}

.data_tb_alignright {
	BACKGROUND: #e4f4fe;
	white-space: nowrap;
	padding: 5px;
	border-top: #8db2e3 1px solid;
	border-left: #8db2e3 1px solid;
	BORDER-RIGHT: #8db2e3 1px solid;
	BORDER-BOTTOM: #8db2e3 1px solid;
	height: 30px;
	background-color: #d3e1f1;
	font-size: 13px;
	font-family: "宋体";
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
<div id="divBtn"></div>
<div style="height: 90%;overflow: auto;">
<form name="thisForm" method="post" action="" id="thisForm">
	
	<input type="hidden" id="uuid" name="uuid" value="${waresStock.uuid}" /> 
	<span class="formTitle"><br> 物品申领信息</span>

<table border="0" style="line-height: 28px" class="data_tb"
	align="center">
	<tr>
		<td colspan="4" style="height: 15px;" class="data_tb_alignright">
		物品申领信息</td>
	</tr>
	<tr>
		<td align="right" width="20%" class="data_tb_alignright">申请人：</td>
		<td class="data_tb_content">
			<input type="text" id="name" name="name" style="border: 0" class="required" value="${userSession.userName}" readonly="readonly">
		</td>

		<td align="right" width="20%" class="data_tb_alignright">申请时间：</td>
		<td class="data_tb_content">
			<input type="text" id="applyDate" name="applyDate" style="border: 0" class="required" value="${applyDate}" readonly="readonly">
		</td>
	</tr>
</table>
<br>

<c:forEach items="${listWaresStock}" var="WaresStock" varStatus="status">
	<c:choose>
		<c:when test="${WaresStock.usableStock - WaresStock.coding <= 0 }">
		<table  align="center" style="background-color: #d3e1f1;border: #8db2e3 1px solid; BORDER-COLLAPSE: collapse;font-size: 14px !important;font-weight:normal; color:red;height: 30px;" width="60%">
				<tr><td >&nbsp;物品“ ${WaresStock.name} ”库存不足，无法进行申领。<script type="text/javascript"></script> </td></tr>
		</table>
		</c:when>
	<c:otherwise>
		<input  type="hidden" value="${status.index}" id="status${status.index}" name="status" >
		<table  align="center" style="border: #8db2e3 1px solid;BORDER-COLLAPSE: collapse;" width="60%">
			<tr>
				<td colspan="6" style="background-color:#d3e1f1; height: 40px;">
				<span style="font-size:20px !important ;text-align: center ;width: 100%">
					${WaresStock.name}
					<input type="hidden" value="${WaresStock.uuid}" name="waresStockId" id="waresStockId${status.index}" >
				</span>
				<span style="margin-left: 20px;">  类别： ${WaresStock.type } </span>
				<span style="margin-left: 20px;"> 
						当前库存：${WaresStock.usableStock }  
						<input type="hidden" name="usableStock" id="usableStock${status.index}" value="${WaresStock.usableStock }"> 
				</span> 
				</td>
			</tr>
			<tr>
				<td class="data_tb_alignright" align="right" width="20%">正在申请的数量：</td>
				<td class="data_tb_content">
					<input id="availableQuantity${status.index}" type="text" name="lowestStock" 
					value="${WaresStock.coding == null?'0':WaresStock.coding}" disabled="disabled" size="10" title="请输入，不能为空！" />
				</td>
				<td class="data_tb_alignright" align="right" width="20%">本次申请的数量：</td>
				<td class="data_tb_content" nowrap="nowrap">
					<input type="text" id="quantity${status.index}" size="10" class="required validate-digits"
						 name="quantity${status.index}" onpropertychange="checkQuantity('usableStock${status.index}','availableQuantity${status.index}','quantity${status.index}','msg${status.index}')"
						  title="请输入数量，并且不能大于${WaresStock.usableStock - WaresStock.coding}" />
					<span id="msg${status.index}" style="font-size: 11px !important ;color: red;font-weight:normal;"> </span>
				</td>
			</tr>
			<tr>
				<td class="data_tb_alignright" align="right"> 申请理由：</td>
				<td class="data_tb_content" colspan="3"> 
					<textarea name="remark" id="remark${status.index}" style="width:100%; height: 40px; overflow: visible;"></textarea>
				</td>
			</tr>
		</table>
	</c:otherwise>
	</c:choose>
</c:forEach>
<center>
<div id="sbtBtn"></div>
</center>
</form>
 </div>

<script type="text/javascript">
	new Validation('thisForm');

	//本次申请的数量
	function checkQuantity(sumObj,areObj,quantityObj,msgObj){
		var sumCount = sumObj;
		var areCount = areObj;
		var quantity = quantityObj;
	    sumCount = document.getElementById(sumCount).value;
	    areCount = document.getElementById(areCount).value;
	    quantity = document.getElementById(quantity).value;
		if(quantity > (sumCount*1)-(areCount*1)){
	     	//document.getElementById(msgObj).innerText="申请数量不能大于当前库存数-正在申请数";
	     	document.getElementById(quantityObj).focus();
     		document.getElementById(quantityObj).value=(sumCount*1)-(areCount*1);
	     	return ;
		}else{
			document.getElementById(msgObj).innerText="";
			//document.getElementById(quantityObj).value="";
		}
	}
	
	function mySubmit() {
		if (!formSubmitCheck('thisForm'))return;

		document.thisForm.action = "${pageContext.request.contextPath}/waresStockSy.do?method=applyWares";
		document.thisForm.submit();
		 
	}
</script>

</body>
</html>
