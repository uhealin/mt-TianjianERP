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
				window.history.back();
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
	padding-left:10px;
	WORD-WRAP: break-word
}

</style>
</head>
<body leftmargin="0" topmargin="0" >
<div id="divBtn" ></div>
	<div style="height:92%;overflow: auto;">
<form name="thisForm" method="post" action="" id="thisForm" > 
		<input type="hidden" id="unid" name="unid" value="${edit.unid}">
		<input type="hidden" id="taskId" name="taskId" value="${taskId}">
	<table  cellpadding="8" cellspacing="0" align="center" class="data_tb" >
	<tr>
	  <td class="data_tb_alignright" align="center" colspan="4">入职人员信息</td>
	</tr>
	<tr>
	  <td class="data_tb_alignright"  width="15%" align="right">应聘岗位：</td>
	  <td  class="data_tb_content" width="35%">${edit.jobname }</td>
	  <td class="data_tb_alignright"  width="15%" align="right">应聘部门：</td>
	  <td  class="data_tb_content" width="35%">
		 ${edit.departmentid} 
	  </td>
	</tr>
	<tr>
	  <td class="data_tb_alignright"  width="15%" align="right">证件类型：</td>
	  <td  class="data_tb_content" >
	  	 ${edit.paperstype }
	  </td>
	  <td class="data_tb_alignright"  width="15%" align="right">证件编号：</td>
	  <td  class="data_tb_content" > ${edit.papersnumber }</td>
	</tr>	
	<tr>
	  <td class="data_tb_alignright"  width="15%" align="right">姓名：</td>
	  <td  class="data_tb_content" > ${edit.name }</td>
	  <td class="data_tb_alignright"  width="15%" align="right">CPA编号：</td>
	  <td  class="data_tb_content"  > ${edit.cpano }</td>
	</tr>			
	<tr>
	  <td class="data_tb_alignright"  width="15%" align="right">性别：</td>
	  <td  class="data_tb_content" >
	  	 ${edit.sex}
	  </td>
	  <td class="data_tb_alignright"  width="15%" align="right">出生年月：</td>
	  <td  class="data_tb_content" width="35%"> ${edit.borndate }</td>	  
	</tr>		
	<tr>
	  <td class="data_tb_alignright"  width="15%" align="right">学历：</td>
	  <td  class="data_tb_content" width="35%"> ${edit.educational }</td>
	  <td class="data_tb_alignright"  width="15%" align="right">毕业院校及专业：</td>
	  <td  class="data_tb_content" width="35%">${edit.diploma }</td>
	</tr>	
	<tr>
	  <td class="data_tb_alignright"  width="15%" align="right">手机：</td>
	  <td  class="data_tb_content" width="35%"> ${edit.mobilephone }</td>  
	  <td class="data_tb_alignright"  width="15%" align="right">邮箱：</td>
	  <td  class="data_tb_content" width="35%"> ${edit.email }</td>
	</tr>	
	<tr> 
	  <td class="data_tb_alignright"  width="15%" align="right">工作简历：</td>
	  <td  class="data_tb_content" colspan="3">
	  	<pre>${edit.specialty}</pre></td>
	</tr>
	<tr> 
	  <td class="data_tb_alignright"  width="15%" align="right">附件：</td>
	  <td  class="data_tb_content" colspan="3">
	  	<script>
			attachInit('k_resume','${edit.attachid}',"showButton:false,remove:false");					
		</script>
	  </td>
	</tr>	
	</table>
	<br>
	<c:forEach items="${nodeList}" var="node">
		<table border="0" cellSpacing="0" cellPadding="0" width="70%"
			align="center">
			<tr>
				<td width="100%" align="middle"><img
					src="${pageContext.request.contextPath}/images/downline.jpg"></td>
			</tr>
		</table>
		<table border="0" cellSpacing="1" cellPadding="2" width="70%"
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
	<table border="0"  style="line-height: 28px"   class="data_tb" align="center">
		<c:if test="${curstate == '部门定职级和试用期限'}">	
		<tr >
			<td class="data_tb_alignright" align="right">试用职级：</td>
			<td class="data_tb_content" colspan="3" align="left">
				<input name="trialRank" type="text" id="trialRank" maxlength="10"  title="请输入，不得为空"
					onKeyDown="onKeyDownEvent();" onKeyUp="onKeyUpEvent();" class='required'
					noinput="true" onClick="onPopDivClick(this);" valuemustexist=true autoid=864 autoHeight=150
				>
			</td>
		</tr>
		<tr >
			<td class="data_tb_alignright" align="right">转正职级：</td>
			<td class="data_tb_content" colspan="3" align="left">
				<input name="fullRank" type="text" id="fullRank" maxlength="10" title="请输入，不得为空"
					onKeyDown="onKeyDownEvent();" onKeyUp="onKeyUpEvent();"  class='required'
					noinput="true" onClick="onPopDivClick(this);" valuemustexist=true autoid=864 autoHeight=150
				>
			</td>
		</tr>
		<tr>
			<td class="data_tb_alignright" align="right">操作权限<span class="mySpan">[*]</span>：</td>
			<td class="data_tb_content" colspan="3">
				<input name="rid" id="rid" type="text" size="25"  class="required"  multiselect="true"  title="请输入，不得为空" onKeyDown="onKeyDownEvent();" onKeyUp="onKeyUpEvent();"  onClick="onPopDivClick(this);" valuemustexist=true autoid=178>
			</td>
		</tr>
		<tr >
			<td class="data_tb_alignright" align="right">试用到期日：</td>
			<td class="data_tb_content" colspan="3" align="left">
				<input name="timeDate" type="text" id="timeDate" class='required' >
			</td>
		</tr>
		<script type="text/javascript">
			new Ext.form.DateField({			
				applyTo : 'timeDate',
				width: 133,
				format: 'Y-m-d'	
			});
		</script>
	</c:if>
	<c:if test="${curstate == '人事部审阅资料'}">	
		<tr  id="waresReturnTr">
			<td class="data_tb_alignright" align="right">审阅资料是否齐全：</td>
			<td class="data_tb_content" colspan="3">
				<select  name="dataComplete" id="dataComplete" style="width: 100">
					<option value="是">是</option>
					<option value="否">否</option>
				</select>
			</td>
		</tr>
		<tr  id="waresReturnTr">
			<td class="data_tb_alignright" align="right">劳动合同是否签订：</td>
			<td class="data_tb_content" colspan="3">
				<select name="signContract" id="signContract" style="width: 100">
					<option value="是">是</option>
					<option value="否">否</option>
				</select>
			</td>
		</tr>
			<tr  id="waresReturnTr">
			<td class="data_tb_alignright" align="right">合同到期日：</td>
			<td class="data_tb_content" colspan="3">
				<input type="text" name="contractExpire" id="contractExpire" class='required' >
			</td>
		</tr>
		<script type="text/javascript">
			new Ext.form.DateField({			
				applyTo : 'contractExpire',
				width: 133,
				format: 'Y-m-d'	
			});
		</script>
	</c:if>
	</table>
		<center><div id="sbtBtn" ></div></center>
		
 </form>
	</div>


<script type="text/javascript">
new Validation('thisForm');


function check(curstate){
	if(curstate !=""){
		var obj=document.all.tags("select"); 
		var len=obj.length; //获得页面所有INPUT对象的数量
		for (var i = 0;i<len; i++){
			if (obj[i].state==curstate){
				obj[i].disabled=false;
				//document.getElementById(obj[i].name+"Tr").style.display="block";
			}else{
				obj[i].disabled=true;
				//document.getElementById(obj[i].id+"Tr").style.display="none";
			}
		}
	}
}
 
function mySubmit() {
	
	if (!formSubmitCheck('thisForm')) return;
	
	var taskId = document.getElementById("taskId").value;
	if(taskId !=""){

		document.thisForm.action="${pageContext.request.contextPath}/job.do?method=auditAgree&taskId="+taskId;
		document.thisForm.submit();
	}
}

</script>

</body>
</html>
