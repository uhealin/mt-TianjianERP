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
	       items:[
	         <c:if test="${audit ==null}">  
	         <c:if test="${newCustomer.uuid ==null}">  
	        { 
	         
	           id:'saveBtn',
	           text:'保存并发起',
	           icon:'${pageContext.request.contextPath}/img/save.gif' ,
	           handler:function(){
			   		mySubmit(1);
			   }
	     	 },'-',
	     	 </c:if>
	     	 { 
		           text:'暂存',
		           icon:'${pageContext.request.contextPath}/img/save.gif' ,
		           handler:function(){
		        	   mySubmit();
				   }
	     	}
	        </c:if>
	        
	        <c:if test="${audit =='audit' }"> 
		        { 
			           text:'通过',
			           icon:'${pageContext.request.contextPath}/img/start.png' ,
			           handler:function(){
			        	   goPass(0);
					   }
		     	}
	        </c:if>
	         ,'-',{ 
	        text:'返回',
	        icon:'${pageContext.request.contextPath}/img/back.gif', 
	        handler:function(){
	        	 if ("${audit}" !="audit") {
		        	window.location = "${pageContext.request.contextPath}/newCustomer.do?method=list";
	        	}
	        	
	        	 if ("${audit}" =="audit") {
	        			window.location = "${pageContext.request.contextPath}/newCustomer.do?method=auditList";
	        	 }
				//window.history.back();
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
.data_tb_content1 {
	PADDING-LEFT: 2px; 
	BORDER-TOP: #8db2e3 1px solid; 
	BORDER-LEFT: #8db2e3 1px solid;
	BORDER-RIGHT: #8db2e3 1px solid;
	BORDER-BOTTOM: #8db2e3 1px solid;  
	TEXT-ALIGN: left
}
</style>
</head>
<body leftmargin="0" topmargin="0" >
<div id="divBtn" ></div>
	<div style="height:92%;overflow: auto;">
<form name="thisForm" method="post" action="" id="thisForm" > 
	<input type="hidden" id="uuid" name="uuid" value="${newCustomer.uuid}" />
	<span class="formTitle" ><br>业务接洽备案</span>
	<table border="0"  style="line-height: 28px"   class="data_tb" align="center">
		<tr>
			<td colspan="4" style="height: 15px;" class="data_tb_alignright"> 
				业务接洽备案
			</td>
		</tr>
		<tr>
			<td class="data_tb_alignright" align="right" >公司名称<font color="red" size=3>[*]</font>：</td>
			<td class="data_tb_content"  nowrap="nowrap"><input type="text" id="customerName" class="required"
				maxlength="30" size="40" name="customerName" value="${newCustomer.customerName}${map.customername}"
				onkeydown="onKeyDownEvent();"
			    onkeyup="onKeyUpEvent();"
			    onclick="onPopDivClick(this);"
			    autoid=601
				onpropertychange="checkCustomerName()" />
				<div id="checkCustomerDiv"> </div>
				</td>
			<td class="data_tb_alignright" align="right">所属行业<font color="red" size=3>[*]</font>：</td>
			<td class="data_tb_content"><input type="text" id="belongsIndustry" class="required"
				maxlength="50" size="35" name="belongsIndustry" value="${newCustomer.belongsIndustry}" 
				onkeydown="onKeyDownEvent();"
				onkeyup="onKeyUpEvent();"
				onclick="onPopDivClick(this);"
				multilevel=true
				norestorehint=true
				autoid=854 /></td>
		</tr>
		<tr>
			<td class="data_tb_alignright" align="right">委托方<font color="red" size=3>[*]</font>：</td>
			<td class="data_tb_content"><input type="text" id="client" class="required"
				onkeydown="onKeyDownEvent();"
			    onkeyup="onKeyUpEvent();"
			    onclick="onPopDivClick(this);"
			    autoid=601
				maxlength="30" size="40" name="client" value="${newCustomer.client}${map.customername}" /></td>
			<td class="data_tb_alignright" align="right">公司所在地<font color="red" size=3>[*]</font>：</td>
			<td class="data_tb_content"><input type="text" id="province" class="required"
				maxlength="5" size="10"  onfocus="onPopDivClick(this);"
				onkeydown="onKeyDownEvent();"
				onkeyup="onKeyUpEvent();"
				onclick="onPopDivClick(this);"
				multilevel=true
				norestorehint=true
				autoid=855
				refreshtarget="city" title="省份不能为空"
				hideresult=true name="province" value="${newCustomer.province}" />&nbsp;省&nbsp;&nbsp;
				<input type="text" id="city" class="required"
				maxlength="6" size="10" 
				onkeydown="onKeyDownEvent();"
				onkeyup="onKeyUpEvent();"
				onclick="onPopDivClick(this);"
				multilevel=true
				norestorehint=true
				autoid=856
				refer=province
				hideresult=true
				name="city" value="${newCustomer.city}" />&nbsp;市</td>
		</tr>
		<tr>
			<td class="data_tb_alignright" align="right">经营范围：</td>
			<td class="data_tb_content">
				<textarea id="runScope" name="runScope" style="width: 250;height:40;overflow:visible;">${newCustomer.runScope}</textarea>			
							</td>
			<td class="data_tb_alignright" align="right">接洽截止时间<font color="red" size=3>[*]</font>：</td>
			<td class="data_tb_content"><input type="text" id="deadlineDate" class="required"
				maxlength="3" size="30" name="deadlineDate" value="${newCustomer.deadlineDate}" /></td>
		</tr>
		<tr>
			<td class="data_tb_alignright" align="right">主要股东<font color="red" size=3>[*]</font>：</td>
			<td class="data_tb_content">
					<textarea style="width: 310;height:60;overflow:visible;"class="required" name="mainShareholder" id="mainShareholder" >${newCustomer.mainShareholder}</textarea></td>
			<td class="data_tb_alignright" align="right">主要高管人员：</td>
			<td class="data_tb_content">
				<textarea style="width: 310;height:60;overflow:visible;"name="mainExecutives" id="mainExecutives" >${newCustomer.mainExecutives }</textarea></td>
		</tr>
		<tr>
			<td class="data_tb_alignright" align="right">前任事务所：</td>
			<td class="data_tb_content"><input type="text" id="predecessorOffice"
				maxlength="40" size="40" name="predecessorOffice" value="${newCustomer.predecessorOffice}" /></td>
			<td class="data_tb_alignright" align="right">主要控股子公司家数<font color="red" size=3>[*]</font>：</td>
			<td class="data_tb_content"><input type="text" id="corporationCount" class="required validate-digits"
				maxlength="3" size="20" name="corporationCount" value="${newCustomer.corporationCount}" />家</td>
		</tr>
		<tr>
			<td class="data_tb_alignright" align="right">业务分类<font color="red" size=3>[*]</font>：</td>
			<td class="data_tb_content"  nowrap="nowrap"colspan="3"><input type="text" id="businessNature" class="required"
				maxlength="50" size="40" name="businessNature" 
				onkeydown="onKeyDownEvent();"
				onkeyup="onKeyUpEvent();"
				onclick="onPopDivClick(this);"
				noinput=true
				autoid=857
				value="${newCustomer.businessNature}" /></td>
	   </tr>
	   <tr>
			<td class="data_tb_alignright" align="right">客户来源<font color="red" size=3>[*]</font>：</td>
			<td class="data_tb_content"><input type="text" id="customerSource" class="required"
				onkeydown="onKeyDownEvent();"
				onkeyup="onKeyUpEvent();"
				onclick="onPopDivClick(this);"
				noinput=true
				autoid=860
				maxlength="30" size="40" name="customerSource" value="${newCustomer.customerSource}${map.source}" /></td>
		
				
			<td class="data_tb_alignright" align="right">业务承接部门：</td>
			<td class="data_tb_content">
				<c:if test="${newCustomer.optDepartment !=null}">
					<input type="text" id="optDepartment" readonly="readonly"
							maxlength="30" size="40" name="optDepartment" value="${newCustomer.optDepartment}" />
				</c:if>
				<c:if test="${newCustomer.optDepartment == null}">
					<input type="text" id="optDepartment" readonly="readonly"
							maxlength="30" size="40" name="optDepartment" value="<%=userSession.getUserAuditDepartmentName() %>" />
					
				</c:if>
		</td>
		</tr>
	   <tr>
			<td class="data_tb_alignright" align="right">服务内容<font color="red" size=3>[*]</font>：</td>
			<td class="data_tb_content"  nowrap="nowrap"colspan="3">
				<textarea name="content" id="content" style="width: 600;height:60;overflow:visible;"class="required">${newCustomer.content}</textarea></td>
		</tr>
		<tr >
			<td class="data_tb_alignright" align="right">第一承做人<font color="red" size=3>[*]</font>：</td>
			<td class="data_tb_content"><input type="text" id="oneBearUserId"  
			onfocus="onPopDivClick(this);"
			onkeydown="onKeyDownEvent();" onkeyup="onKeyUpEvent();"
			onclick="onPopDivClick(this);"  
			  autoid=867 multilevel=true valuemustexist=true
				maxlength="10" size="40" name="oneBearUserId" value="${newCustomer.oneBearUserId}" /></td>
			<td class="data_tb_alignright" align="right">第二承做人：</td>
			<td class="data_tb_content"><input type="text" id="twoBearUserId" 
				maxlength="10" size="40" name="twoBearUserId"
				onfocus="onPopDivClick(this);"
				onkeydown="onKeyDownEvent();" onkeyup="onKeyUpEvent();"
				onclick="onPopDivClick(this);"   
				 autoid=867 multilevel=true valuemustexist=true
			 value="${newCustomer.twoBearUserId}" /></td>
		</tr>
		<tr >
			<td class="data_tb_alignright" align="right">是否签约<font color="red" size=3>[*]</font>：</td>
			<td class="data_tb_content"> <select style="width: 60px;" name="signBook" id="signBook"><option value="否">否</option><option value="是">是</option></select> </td>
			<td class="data_tb_alignright" align="right">业务承接性质<font color="red" size=3>[*]</font>：</td>
			<td class="data_tb_content"><input type="text" id="optQuality"  
				maxlength="30" size="40" name="optQuality" value="${newCustomer.optQuality}"
				onkeydown="onKeyDownEvent();"
				onkeyup="onKeyUpEvent();"
				onclick="onPopDivClick(this);"
				noinput=true
				autoid=861
				 /></td>
		</tr>
		<tr >
			<td class="data_tb_alignright" align="right">承做人办公电话：</td>
			<td class="data_tb_content"><input type="text" id="mobilePhone"
				maxlength="13" size="40" name="mobilePhone" value="${newCustomer.mobilePhone}" /></td>
			<td class="data_tb_alignright" align="right">承做人移动电话：</td>
			<td class="data_tb_content"><input type="text" id="phone"
				maxlength="11" size="40" name="phone" value="${newCustomer.phone}" /></td>
		</tr>
		<tr >
			<td class="data_tb_alignright" align="right">项目编号：<font color="red" size=3>[*]</font>：</td>
			<td class="data_tb_content1" colspan="3"><input type="text" id="projestId" class="required" title="请输入"
				maxlength="20" size="40" name="projestId" value="${newCustomer.projestId}" /></td>
		</tr>
		<tr>
			<td class="data_tb_alignright" align="right">备注：</td>
			<td class="data_tb_content"  nowrap="nowrap"colspan="3">
			<textarea style="width: 100%;height:60;overflow:visible;" name="remark" id="remark">${newCustomer.remark}</textarea></td>
		</tr>
	</table >
		<br>
		<center><div id="sbtBtn" ></div></center>
		<input type="hidden" id="state" name="state" value="${newCustomer.state}">
		<input type="hidden" id="obautoId" name="obautoId" value=${map.obautoId }>
		<br><br><br>
 </form>
	</div>


<script type="text/javascript">

new Validation('thisForm');
	if("${audit}" !="audit"){

		new Ext.form.DateField({			
			applyTo : 'deadlineDate',
			width: 150,
			minValue:new Date(),
			format: 'Y-m-d'	
		});
	}
var uuid = "${newCustomer.uuid}";
if(uuid=="" ){
	document.getElementById("mainExecutives").innerText="董事长：\n总经理：\n财务总监：";
} 

if("${audit}" =="audit"){
	var dmAll = document.all;
	
	
	for(var i = 0;i<dmAll.length;i++){

		
		if(dmAll[i].type =="text"){
			
			if(dmAll[i].id !="projestPartner" && dmAll[i].id !="projestManager" &&  dmAll[i].id !="projestId"){

				setObjDisabled(dmAll[i].id);
				dmAll[i].style.border="none";
				dmAll[i].style.backgroundColor ="transparent";
			}
		}
		if(dmAll[i].type =="textarea"){
			setObjDisabled( dmAll[i].id);
			dmAll[i].style.border="none";
			dmAll[i].style.backgroundColor ="transparent";
		}
			
	}
	
}
	//审核通过
	function goPass(){
/*		 var projestPartner = document.getElementById("projestPartner").value; //合伙人
		 var projestManager = document.getElementById("projestManager").value;  //高级经理
		
		 if(projestPartner =="" || projestManager==""){
			 alert("必须要选择我所的合伙人及高级经理才可以通过!");
		 	return ;
		 }*/
		 if (!formSubmitCheck('thisForm')) return ;
		 document.thisForm.action="${pageContext.request.contextPath}/newCustomer.do?method=auditPass";
		 document.thisForm.submit();
	}
	
function checkCustomerName(){
	 var customerName = document.getElementById("customerName").value;
	 if(customerName == ""){
		 return ;
	 }
	 if("${audit}" !="audit"){
		 
		 var url="${pageContext.request.contextPath}/newCustomer.do?method=getIfCustomerName";
		 var requestString = "&customerName="+customerName;
		 var request= ajaxLoadPageSynch(url,requestString);
		 
		 if(request !=""){
			 var result = request.split("@`@");
			 document.getElementById("checkCustomerDiv").style.display="block";
			 document.getElementById("checkCustomerDiv").innerHTML = "<font color=red>"+result[0]+"</font>";
		 }else{
			 document.getElementById("checkCustomerDiv").style.display="none";
			 return ;
		 }
	 }
}

function mySubmit(obj) {
	
	if(obj =="1"){
		if(confirm("您确定要发起吗?","yes")){
			document.getElementById("state").value="已发起";
			if (!formSubmitCheck('thisForm')) return ;
			if(document.getElementById("uuid").value!="") {
			
				document.thisForm.action="${pageContext.request.contextPath}/newCustomer.do?method=update";
			} else {
			
				document.thisForm.action="${pageContext.request.contextPath}/newCustomer.do?method=add";
			}
			document.thisForm.submit();
		}
	}else{
		document.getElementById("state").value="未发起";
		if (!formSubmitCheck('thisForm')) return ;
	if(document.getElementById("uuid").value!="") {
	
		document.thisForm.action="${pageContext.request.contextPath}/newCustomer.do?method=update";
	} else {
	
		document.thisForm.action="${pageContext.request.contextPath}/newCustomer.do?method=add";
	}
	document.thisForm.submit();
	}
	
	
}

</script>

</body>
</html>
