<%@page import="com.matech.framework.listener.UserSession"%>
<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/calendar_include.jsp"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<%
	UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
%>
<title>业务接洽备案</title>
<script type="text/javascript">


Ext.onReady(function (){
	new Ext.Toolbar({
			renderTo: "divBtn",
			height:30,
			defaults: {autoHeight: true,autoWidth:true},
	       items:[{ 
	           id:'saveBtn',
	           text:'保存',
	           icon:'${pageContext.request.contextPath}/img/save.gif' ,
	           handler:function(){
			   		mySubmit();
			   }
	     	 },'-',{ 
	        text:'返回',
	        icon:'${pageContext.request.contextPath}/img/back.gif', 
	        handler:function(){
				window.history.back();
			}
	  	},'->']
	});
	
	new ExtButtonPanel({
		desc:'',
		renderTo:'sbtBtn',
		items:[
		{
            text: '保存',
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
					window.history.back();
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
	WORD-WRAP: break-word
}

</style>
</head>
<body leftmargin="0" topmargin="0" >
<div id="divBtn" ></div>
	<div style="height:92%;overflow: auto;">
<form name="thisForm" method="post" action="" id="thisForm" > 
	<input type="hidden" id="autoId" name="autoId" value="${leaveType.autoId }">
	<span class="formTitle" ><br>请假类型设置</span>
	<table border="0"  style="line-height: 28px"   class="data_tb" align="center">
		<tr>
			<td colspan="4" style="height: 15px;" class="data_tb_alignright"> 
				请假类型设置 不限制请填“0”
			</td>
		</tr>
		<tr>
			<td class="data_tb_alignright" align="right" >名称：</td>
			<td class="data_tb_content"  nowrap="nowrap"><input type="text" id="name" class="required"
				maxlength="30"  name="name" value="${leaveType.name}" onchange="checkName()" />
				<div id="checkCustomerDiv"> </div>
				</td>
			<td class="data_tb_alignright" align="right">提前申请限制：</td>
			<td class="data_tb_content"><input type="text" id="applyLimit" class="required validate-digits"
				maxlength="50" name="applyLimit" value="${leaveType.applyLimit}"/></td>
		</tr>
		<tr>
			<td class="data_tb_alignright" align="right">每年天数上限：</td>
			<td class="data_tb_content"><input type="text" id="yearDayLimit" class="required validate-digits"
				maxlength="30"  name="yearDayLimit" value="${leaveType.yearDayLimit}" /></td>
			<td class="data_tb_alignright" align="right">每年累计上限(次数)：</td>
			<td class="data_tb_content"><input type="text" id="yearCountLimit" class="required validate-digits"
				maxlength="5" name="yearCountLimit" value="${leaveType.yearCountLimit}" />
			 </td>
		</tr>
		 
		<tr>
			<td class="data_tb_alignright" align="right">每月天数上限：</td>
			<td class="data_tb_content"><input type="text" id="monthDayLimit" class="required validate-digits"
				maxlength="40"  name="monthDayLimit" value="${leaveType.monthDayLimit}" /></td>
			<td class="data_tb_alignright" align="right">每月累计上限：</td>
			<td class="data_tb_content"><input type="text" id="monthCountLimit" class="required validate-digits"
				maxlength="3" size="20" name="monthCountLimit" value="${leaveType.monthCountLimit}" /></td>
		</tr>
		   
		<tr>
			<td class="data_tb_alignright" align="right">扣工资金额：</td>
			<td class="data_tb_content"><input type="text" id="deductMoney" name="deductMoney" class="validate-digits" value="${leaveType.deductMoney}" />按小时计算</td>
			<td class="data_tb_alignright" align="right">最小时间(起开始扣工资)：</td>
			<td class="data_tb_content"><input type="text" id="minTime" class="required validate-digits"
				maxlength="30"  name="minTime" value="${leaveType.minTime}"  /></td>
		</tr>
		<tr>
			<td class="data_tb_alignright" align="right">备注：</td>
			<td class="data_tb_content"  nowrap="nowrap"colspan="3">
			<textarea style="width: 100%;height:60;overflow:visible;" name="memo" id="memo">${leaveType.memo}</textarea></td>
		</tr>
	</table>
		<center><div id="sbtBtn" ></div></center>
		
 </form>
	</div>


<script type="text/javascript">
new Validation('thisForm');
 
function checkName(){
	 var name = document.getElementById("name").value;
	 if(name == ""){
		 return ;
	 }
	 if(name == "${leaveType.name}"){
		 return ;
	 }
	 var url="${pageContext.request.contextPath}/leaveType.do?method=getName";
	 var requestString = "&name="+name;
	 var request= ajaxLoadPageSynch(url,requestString);
	 if(request !=""){
		 document.getElementById("checkCustomerDiv").style.display="block";
		 document.getElementById("checkCustomerDiv").innerHTML = "<font color=red>请假类型名称“"+request+"”已存在!</font>";
	 }else{
		 document.getElementById("checkCustomerDiv").style.display="none";
		 return ;
	 }
}

function mySubmit() {
	
	if (!formSubmitCheck('thisForm')) return ;
	
	document.thisForm.action="${pageContext.request.contextPath}/leaveType.do?method=add";
	 
	document.thisForm.submit();
}

</script>

</body>
</html>
