<%@page import="com.matech.framework.listener.UserSession"%>
<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/calendar_include.jsp"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<script src="${pageContext.request.contextPath}/AS_INCLUDE/ext_time/ext-lang-zh_CN.js" type="text/javascript"></script>
<script src="${pageContext.request.contextPath}/AS_INCLUDE/ext_time/SpinnerField.js" type="text/javascript"></script>
<script src="${pageContext.request.contextPath}/AS_INCLUDE/ext_time/Spinner.js" type="text/javascript"></script>
<script src="${pageContext.request.contextPath}/AS_INCLUDE/ext_time/timeFiled.js" charset="GBK" type="text/javascript"></script>

<link href="${pageContext.request.contextPath}/AS_INCLUDE/ext_time/resources/css/ext-all.css" rel="stylesheet" type="text/css" />
<%
	UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
%>
<title>请假审批</title>
<script type="text/javascript">


Ext.onReady(function (){
	new Ext.Toolbar({
			renderTo: "divBtn",
			height:30,
			defaults: {autoHeight: true,autoWidth:true},
	       items:[{ 
	           id:'saveBtn',
	           text:'审批通过',
	           icon:'${pageContext.request.contextPath}/img/start.png' ,
	           handler:function(){
			   		mySubmit();
			   }
	     	 },'-',{ 
	        text:'返回',
	        icon:'${pageContext.request.contextPath}/img/back.gif', 
	        handler:function(){
	        	<c:if test="${ctype =='销假审批'}">
   			  		  window.location.href="${pageContext.request.contextPath}/leave.do?method=destroyAuditList";
				</c:if>
				<c:if test="${ctype !='销假审批'}">
				   	 window.location.href="${pageContext.request.contextPath}/leave.do?method=leaveAuditList";
				</c:if>
				//window.history.back();
			}
	  	},'->']
	});
	
	new ExtButtonPanel({
		desc:'',
		renderTo:'sbtBtn',
		items:[
		{
            text: '审批通过',
            id:'appSubmit23', 
            icon:'${pageContext.request.contextPath}/img/receive.png' ,
            scale: 'large',
            handler:function(){
            	mySubmit();
   			}
           },{
            text: '返回',
            id:'appSubmit25', 
            icon:'${pageContext.request.contextPath}/img/back_32.png' ,
            scale: 'large',
               handler:function(){
            	  //closeTab(parent.tab);
				//	window.history.back();
            	  <c:if test="${ctype =='销假审批'}">
			  		  window.location.href="${pageContext.request.contextPath}/leave.do?method=destroyAuditList";
				</c:if>
				<c:if test="${ctype !='销假审批'}">
				   	 window.location.href="${pageContext.request.contextPath}/leave.do?method=leaveAuditList";
				</c:if>
   			   }
           }
        ]  
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
	width:70%;
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
	width:15%;
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
	padding-left:10px;
	WORD-WRAP: break-word
}

</style>
</head>
<body leftmargin="0" topmargin="0" >
<div id="divBtn" ></div>
	<div style="height:92%;overflow: auto;">
<form name="thisForm" method="post" action="" id="thisForm" > 
	<input type="hidden" id="uuid" name="uuid" value="${leave.uuid}">
	<input type="hidden" id="taskId" name="taskId" value="${taskId}">
	<span class="formTitle" ><br>${ctype}</span>
	<table border="0"  style="line-height: 28px"   class="data_tb" align="center">
		<tr>
			<td colspan="4" style="height: 15px;" class="data_tb_alignright"> 
				${ctype}
			</td>
		</tr>
		<tr>
			<td class="data_tb_alignright" align="right" >请假人：</td>
			<td class="data_tb_content"  nowrap="nowrap" width="35%">${leave.userId}
				<div id="checkCustomerDiv"> </div>
				</td>
			<td class="data_tb_alignright" align="right">申请时间：</td>
			<td class="data_tb_content"> ${leave.applyDate}</td>
		</tr>
		<tr>
			<td class="data_tb_alignright" align="right">请假类型：</td>
			<td class="data_tb_content">
					<input type="text" id="leaveTypeId"
					maxlength="30"  name="leaveTypeId" value="${leave.leaveTypeId}" 
					onkeydown="onKeyDownEvent();"
					onkeyup="onKeyUpEvent();"
					onclick="onPopDivClick(this);"
					noinput=true
					autoid=865 style="border: none;" />
			<span id="checkUseTypeDiv"></span>	
			</td>
			<td class="data_tb_alignright" align="right">请假时数：</td>
			<td class="data_tb_content"><span style="color:blue">${leave.leaveHourCount }</span>个小时</td>
		</tr>
		<tr>
			<td class="data_tb_alignright" align="right">请假开始时间：</td>
			<td class="data_tb_content">
				 ${leave.leaveStartTime} 
				</td>
			<td class="data_tb_alignright" align="right">请假结束时间：</td>
			<td class="data_tb_content">
				 ${leave.leaveEndTime}   
			</td>
		</tr>
		<tr >
			<td class="data_tb_alignright" align="right">请假事由：</td>
			<c:choose>
			  <c:when test="${ctype=='请假审批'}">
			    <td class="data_tb_content"  nowrap="nowrap"colspan="3"> 
					<pre>${leave.memo}</pre>
			   </td>
			  </c:when>
			  <c:otherwise>
			  <td class="data_tb_content"  nowrap="nowrap"> 
					<pre>${leave.memo}</pre>
			   </td>
			   <td class="data_tb_alignright" align="right">实际时数：</td> 
			   <td class="data_tb_content"><span style="color:blue">${leave.destroyHourCount }</span>个小时</td>
			  </c:otherwise>
			</c:choose>
			
		</tr>
		<tr style="display: none;" id="destroyDate">
			<td class="data_tb_alignright" align="right">实际请假开始时间：</td>
			<td class="data_tb_content">
				 ${leave.destroyStartTime}
				</td>
			<td class="data_tb_alignright" align="right">实际请假结束时间：</td>
			<td class="data_tb_content">
				 ${leave.destroyEndTime}  
			</td>
		</tr>
	</table>
		<center><div id="sbtBtn" ></div></center>
 </form>
	</div>


<script type="text/javascript">
new Validation('thisForm');

	setObjDisabled("leaveTypeId");
if("${ctype}".indexOf("销假")>-1){
	document.getElementById("destroyDate").style.display="block";
	
}
document.getElementById("leaveTypeId").style.backgroundColor ="transparent";

function mySubmit() {
	
	if("${ctype}".indexOf("请假")>-1){ //请假审批
		document.thisForm.action="${pageContext.request.contextPath}/leave.do?method=auditAgree";
	}else{ //销假
		document.thisForm.action="${pageContext.request.contextPath}/leave.do?method=destroy";
	}
	document.thisForm.submit();
}

</script>

</body>
</html>
