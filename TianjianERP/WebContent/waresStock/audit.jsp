<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/calendar_include.jsp"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">

<title>物品申领审批</title>
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
	width: 80%;
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
	TEXT-ALIGN: center;
	WORD-WRAP: break-word
}
</style>
</head>
<body leftmargin="0" topmargin="0">
<div id="divBtn"></div>
<form name="thisForm" method="post" action="" id="thisForm">
	
	<input type="hidden" id="ctype" name="ctype" value="${ctype}">
	<input type="hidden" id="taskIds" name="taskIds" value="${taskIds}">
	<input type="hidden" id="pdIds" name="pdIds" value="${pdIds}">
	<input type="hidden" id="uuids" name="uuids" value="${uuids}">
	
	<span class="formTitle"><br> 物品申领信息</span>

<table border="0" style="line-height: 28px" class="data_tb"
	align="center">
	<tr>
		<td colspan="4" style="height: 15px;" class="data_tb_alignright">
		物品申领信息</td>
	</tr>
	<tr>
		<td align="right" width="20%" class="data_tb_alignright">审批人：</td>
		<td align="left">
			<input type="text" id="name" name="name" style="border:0" class="required" value="${userSession.userName}" readonly="readonly">
		</td>

		<td align="right" width="20%" class="data_tb_alignright">审批时间：</td>
		<td align="left">
			<input type="text" id="approveDate" style="border: 0" name="approveDate" class="required" value="${applyDate}" readonly="readonly">
		</td>
	</tr>
</table>
<br>

		<table  align="center" style="border: #8db2e3 1px solid;BORDER-COLLAPSE: collapse;" width="80%">
			<tr>
				<td align="center" width="80" class="data_tb_alignright">
					申请人
				</td>
				 
		
				<td align="center" width="130"  class="data_tb_alignright">	
					申请时间
				</td>
				 
				<td  align="center" width="140" class="data_tb_alignright">
					物品名称 
				</td>
				<td align="center" width="80" class="data_tb_alignright">
					  类别   
				 </td>
				 <td align="center" width="300" class="data_tb_alignright">
						备注  
				 
				</td>
				 <td align="center" width="70" class="data_tb_alignright">
						当前库存 
						<input type="hidden" name="usableStock" id="usableStock${status.index}" value="${WaresStream.status }"> 
				 
				</td>
				<td align="center" width="100" class="data_tb_alignright">
						本次申请的数量  
				 
				</td>
				<td align="center" width="100" class="data_tb_alignright">
						实际发放的数量
				 
				</td>
			</tr>
			<c:forEach items="${listWaresStream}" var="WaresStream" varStatus="status">
			<c:if test="${WaresStream.waresStockId !=null }">
			<input  type="hidden" value="${status.index}" id="status${status.index}" name="status" >
					<tr>
						<td   class="data_tb_content" align="right" >
							${WaresStream.userId}&nbsp;
						</td>
						<td  class="data_tb_content">
							 ${WaresStream.applyDate}&nbsp;
						</td>
						<td  class="data_tb_content" align="right"  >
							${WaresStream.approveIdea}&nbsp;
							<input type="hidden" value="${WaresStream.uuid}" name="uuid" id="uuid${status.index}" >
						</td>
						<td  class="data_tb_content" nowrap="nowrap">
							${WaresStream.approveQuantity}&nbsp;
						</td>
						<td class="data_tb_content">
							${WaresStream.applyReason}&nbsp;
						</td>
						<td  class="data_tb_content" nowrap="nowrap">
							${WaresStream.status}&nbsp;
							<input type="hidden" id="sumCount${status.index}" name="sumCount" value="${WaresStream.status}"> 
						</td>
						<td  class="data_tb_content" nowrap="nowrap">
							${WaresStream.quantity}&nbsp;
							<input type="hidden" id="userCount${status.index}" name="userCount" value="${WaresStream.quantity}">
						</td>
						<td  class="data_tb_content" nowrap="nowrap">
							<input type="text" size="10" id="approveQuantity${status.index}"
								 name="approveQuantity${status.index}" 
								class="required validate-digits"
								title="请输入数量" onpropertychange="checkQuantity('sumCount${status.index}','userCount${status.index}','approveQuantity${status.index}')" >
						</td>
					</tr>
				</c:if>
			</c:forEach>
		</table>

<center>
<div id="sbtBtn"></div>
</center>
</form>


<script type="text/javascript">
	new Validation('thisForm');

	//暂时没有
	function checkQuantity(sumObj,areObj,quantityObj){
		var sumCount = sumObj;
		var areCount = areObj;
		var quantity = quantityObj;
	    sumCount = document.getElementById(sumCount).value;
	    areCount = document.getElementById(areCount).value;
	    quantity = document.getElementById(quantity).value;
		if(quantity > (sumCount*1)){
	     	//document.getElementById(msgObj).innerText="申请数量不能大于当前库存数-正在申请数";
	     	if(quantity>areCount){
	     		document.getElementById(quantityObj).value=areCount;
	     	}else{
	     		document.getElementById(quantityObj).focus();
		     	document.getElementById(quantityObj).value=sumCount;
	     	}
		} 
	}
	
	function mySubmit() {
		if (!formSubmitCheck('thisForm'))return;

		document.thisForm.action = "${pageContext.request.contextPath}/waresStock.do?method=auditSave";
		
		document.thisForm.submit();
		 
	}
</script>

</body>
</html>
