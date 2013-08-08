<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/calendar_include.jsp"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">

<title>公章审批</title>
<script type="text/javascript">
	Ext.onReady(function() {
		if("${seal.status}".indexOf("已审批")>-1){
		var mytab = new Ext.TabPanel({
	        id: "tab",
	        renderTo: "divTab",
	        activeTab:0, //选中第一个 tab
	        autoScroll:true,
	        frame: true,
	        height: document.body.clientHeight-Ext.get('divTab').getTop(), 
	        defaults: {autoHeight: true,autoWidth:true},
	        items:[
	            {contentEl: "sealContent",title:"申请信息",id:"sealContentTab"},
		         {contentEl: "sealFile",title:"附件",id:"sealFileTab"} 
	           
	        ]
	    });
	
		 }
	if("${seal.status}".indexOf("已审批")>-1){
		
		//fileOpen("附件.doc","${seal.fileName}") ;
	}
		
		new Ext.Toolbar({
			renderTo : "divBtn",
			height : 30,
			defaults : {
				autoHeight : true,
				autoWidth : true
			},
			items : [{
				text : '返回',
				icon : '${pageContext.request.contextPath}/img/back.gif',
				handler : function() {
					window.history.back();
				}
			}, '->' ]
		});

	});
</script>

<style type="text/css">

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
<body >
<div id="divBtn"></div>
<form name="thisForm" method="post" action="" id="thisForm">
<div id=divTab>
<div id="sealContent" style="height: 90%;width:100%;position:relative; overflow:auto;">
	
	<input type="hidden" id="taskId" name="taskId" value="${taskId}">
	<input type="hidden" id="pdId" name="pdId" value="${pdId}">
	
	<span class="formTitle"><br> 公章申领信息</span>

<table border="0" style="line-height: 28px" class="data_tb"
	align="center">
	<tr>
		<td colspan="4" style="height: 15px;" class="data_tb_alignright">
		公章申领信息</td>
	</tr>
	<tr>
		<td align="right" width="20%" class="data_tb_alignright">申请人：</td>
		<td class="data_tb_content" align="left">
			 ${seal.userId} 
		</td>

		<td align="right" width="20%" class="data_tb_alignright">申请时间：</td>
		<td class="data_tb_content" align="left">
			${seal.applyDate} 
		</td>
	</tr>
	<tr>
		<td align="right" width="20%" class="data_tb_alignright">申请事项：</td>
		<td class="data_tb_content" align="left">
			 ${seal.matter} 
		</td>

		<td align="right" width="20%" class="data_tb_alignright">公章类型：</td>
		<td class="data_tb_content" align="left">
			 ${seal.ctype} 
		</td>
	</tr>
	<tr>
			<td align="right" width="20%" class="data_tb_alignright">申请份数：</td>
			<td class="data_tb_content" align="left">
				 ${seal.sealCount} 
			</td>
	
			<td align="right" width="20%" class="data_tb_alignright">使用部门：</td>
			<td class="data_tb_content" align="left">
				<input  type="text" name="applyDepartName" id="applyDepartName" class="required" onfocus="onPopDivClick(this);"
				onkeydown="onKeyDownEvent();"
				onkeyup="onKeyUpEvent();"
				onclick="onPopDivClick(this);" 
				autoid=4402
				value="${seal.applyDepartId}" />
			</td>
		</tr>
	<tr>
		<td align="right" width="20%" class="data_tb_alignright">申请事项：</td>
		<td class="data_tb_content" style="text-align: left ;" colspan="3">
			 ${seal.remark} 
		</td>
 
	</tr>
</table >
	<br><br>
	<c:forEach items="${nodeList}" var="node">
		<table border="0" cellSpacing="0" cellPadding="0" width="80%"
			align="center">
			<tr>
				<td width="100%" align="middle"><img
					src="${pageContext.request.contextPath}/images/downline.jpg"></td>
			</tr>
		</table>
		<table border="0" cellSpacing="1" cellPadding="2" width="80%"
			bgColor="#99BBE8" align="center" class="appTable">
			<tr bgColor="#DDE9F9">
				<td colSpan="2" style="height: 25px;"><b><span style="width: 30%;margin-left: 10px;">${node.nodeName}</span>
				<span style="width: 15%;">处理人：${node.dealUserId}</span> <span
					style="width: 25%;">处理时间：${node.dealTime}</span> </b></td>
			</tr>
			<c:forEach items="${node.formList}" var="form">
	
				<tr bgColor="#ffffff" style="height: 20px;">
					<td width="20%" align="right">${form.key}：</td>
					<td width="80%" style="padding-left: 5px;"><c:choose>
						<c:when test="${form.property != ''}">
							<a href=# onclick="fileOpen('${form.property}','${form.value}')">${form.value}
							</a>
						</c:when>
						<c:otherwise>
										${form.value}
									</c:otherwise>
					</c:choose></td>
				</tr>
			</c:forEach>
		</table>
	</c:forEach>
</div>
	
	 <div id="sealFile" class="x-hide-display" style="height:expression(document.body.clientHeight-62);width:100%">
			
			<iframe scrolling="auto" src="${pageContext.request.contextPath}/seal.do?method=filePage&file=${seal.attachname}&uuid=${seal.uuid}&opt=look" style="width: 100%;height: 100%"> </iframe>
			
	</div>
</div>
	<br>
	<br>
</form>


<script type="text/javascript">

	setObjDisabled("applyDepartName");
	
	function goAudit()
	{
			document.thisForm.action="${pageContext.request.contextPath}/seal.do?method=audit";
			document.thisForm.submit();
	} 
</script>

</body>
</html>
