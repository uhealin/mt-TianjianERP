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
				text : '通过',
				icon : '${pageContext.request.contextPath}/img/save.gif',
				handler : function() {
					goApply();
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
				text : '通过',
				id : 'appSubmit23',
				icon : '${pageContext.request.contextPath}/img/receive.png',
				scale : 'large',
				handler : function() {
					goApply();
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
<form name="thisForm" method="post" action="waresStock.do" id="thisForm">
	
	<input type="hidden" id="uuid" name="uuid" value="${waresStockDetails.uuid}" /> 
	<input type="hidden" id="pdId" name="pdId" value="${pdId}" /> 
	<input type="hidden" id="taskId" name="taskId" value="${taskId}" /> 
	<input type="hidden" id="waresStockId" name="waresStockId" value="${waresStock.uuid}" /> 
	<span class="formTitle"><br> 物品报废信息</span>

<table border="0" style="line-height: 28px" class="data_tb"
	align="center">
	<tr>
		<td colspan="4" style="height: 15px;" class="data_tb_alignright">
		物品报废信息</td>
	</tr>
	<tr>
		<td align="right" width="20%" class="data_tb_alignright">申请人：</td>
		<td class="data_tb_content">
			 ${waresStockDetails.userId} 
		</td>

		<td align="right" width="20%" class="data_tb_alignright">申请时间：</td>
		<td class="data_tb_content">
			 ${waresStockDetails.date} 
		</td>
	</tr>
		<tr>
		<td align="right" width="20%" class="data_tb_alignright">报废物品名称：</td>
		<td class="data_tb_content">
			 ${waresStock.name} 
		</td>

		<td align="right" width="20%" class="data_tb_alignright">物品类型：</td>
		<td class="data_tb_content">
			 ${waresStock.type} 
		</td>
	</tr>
		<tr>
		<td align="right" width="20%" class="data_tb_alignright">报废数量：</td>
		<td class="data_tb_content" colspan="3">
			 ${waresStockDetails.quantity}
		</td>
</table>
<br>
 
<center>
<div id="sbtBtn"></div>
</center>
</form>
 </div>

<script type="text/javascript">
	new Validation('thisForm');

	function goApply(){
		document.thisForm.action = "${pageContext.request.contextPath}/waresStock.do?method=stocScrapkAgree";
		document.thisForm.submit();
	}
	
 
</script>

</body>
</html>
