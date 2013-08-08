<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>权限申请列表</title>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/Validate_include.jsp" %>
<script src="<%=request.getContextPath()%>/AS_INCLUDE/searchSession.js charset=gbk"></script>
<script type="text/javascript" language="javascript" src="<%=request.getContextPath()%>/AS_INCLUDE/main.js"
	charset="GBK"></script>
<DIV id="TipLayer"
	style="visibility:hidden;position:absolute;z-index:1000;top:-100;"></DIV>
<script type="text/javascript" language="javascript" src="<%=request.getContextPath()%>/AS_INCLUDE/style.js"
	charset="GBK"></script>
</head>

<body leftmargin="0" topmargin="0">

${menuLocation}

<table width="30%" cellspacing="0" cellpadding="0"><tr><td height="5"></td></tr></table>


<input name="view" type="button" class="flyBT" value=" 增 加 " onclick="goAdd();">

<!--  
<input name="view" type="button" class="flyBT" value=" 追 回  " onclick="getBack();">
<input name="view" type="button" class="flyBT" value=" 查看追回申请  " onclick="getBack();">
-->

<input name="view" type="button" class="flyBT" value=" 打 印 " onclick="print_rightapplyList();">




<form name="thisForm" method="post" action="">
<table width="100%" cellspacing="0" cellpadding="0"><tr><td height="20" align=center><font size=3 color=red><b>权限申请列表</b></font></td></tr></table>
<input type="hidden" name="PopedomPowerId" id="PopedomPowerId">
<fieldset id="serachFrame">
    <legend>权限申请查找</legend>
	<table border="0" cellpadding="0" cellspacing="0" width="100%" bgcolor="">
		<tr>
			<td width=80>申请人：</td>
			<td align=left>
			
			<input type="text" name="applyuserid" id="applyuserid" id="auditPopedomok " onkeydown="onKeyDownEvent();" onkeyup="onKeyUpEvent();" onclick="onPopDivClick(this);"   hideresult=true autoid=362  ></td>
			
			<td width=80>申请权限：</td>
			<td align=left>
			
			<input type="text" name="applyPopedom" id="applyPopedom"  onkeydown="onKeyDownEvent();" onkeyup="onKeyUpEvent();" onclick="onPopDivClick(this);"   autoid=363 ></td>
			
												
            <td width=80>申请日期：</td>
            <td align=left>
                <input type="text" name="applydate" id="applydate" class="validate-date-cn" readonly="readonly"  showcalendar=true  >
            </td>
         </tr>
         <tr>
			<td width=80>申请备注：</td>
			<td align=left><input type="text" name="applymemo" id="applymemo" 
							 ></td>
			<td width=80>审批人：</td>
			<td align=left><input type="text" name="audituserid" id="audituserid" autoid=362  hideresult=true onkeydown="onKeyDownEvent();" onkeyup="onKeyUpEvent();" onclick="onPopDivClick(this);"
							 ></td>
			<td width=80>通过权限：</td>
			<td align=left>
			
			<input type="text" name="auditPopedomok " id="auditPopedomok " onkeydown="onKeyDownEvent();" onkeyup="onKeyUpEvent();" onclick="onPopDivClick(this);"   autoid=363 ></td>
			
		</tr>
		<tr>
			<td width=80>未通过权限：</td>
			<td align=left><input type="text" name="auditPopedomng" id="auditPopedomng" onkeydown="onKeyDownEvent();" onkeyup="onKeyUpEvent();" onclick="onPopDivClick(this);"   autoid=363 
							 ></td>
			<td width=80>审批日期：</td>
			<td align=left><input type="text" name="auditdate" id="auditdate" class="validate-date-cn"   showcalendar=true readonly="readonly"
							 ></td>
			<td width=80>审批备注：</td>
			<td align=left>
			
			<input type="text" name="auditmemo" id="auditmemo"   ></td>
		</tr>
		
		<tr>
		 <td height="5" colspan=10></td>
		</tr>
		<tr>
		 <td height="1" bgcolor="" colspan=10></td>
		</tr>
		<tr>
		 <td height="5" colspan=10 on></td>
		</tr>
		<tr>
			<td height=10 colspan = 10 align=center>
			<input type="button" name="srh" value="搜 索" onclick="goSearch_rightapplyList();">&nbsp;&nbsp;&nbsp;&nbsp;
			<input type="button" name="csrh" value="清 除" onclick="clearF();goSearch_rightapplyList();">
			</td>
		</tr>
	</table>
</fieldset>

<table width="30%" cellspacing="0" cellpadding="0"><tr><td height="5"></td></tr></table>
<mt:DataGridPrintByBean name="rightapplyList"  outputData="true" outputType="invokeSearch"/>	
<table width="30%" cellspacing="0" cellpadding="0"><tr><td height="10"></td></tr></table>

</form>

<Script>


function cyl(){
	

	document.getElementById("PopedomPowerId").value = window.event.srcElement.innerHTML 



	var query_String = formToRequestString(document.thisForm);

	var url = "${pageContext.request.contextPath}/role.do?method=getNameByPopedomPowerId&random="+Math.random();

	result = ajaxLoadPageSynch(url,query_String);


  var displayText=result
  _showhint=1;
  stm(['菜单权限',displayText],Style[10]);

}


//提示控件
function tis(){
  _showhint=0;
    htm();
}


function clearF(){
	thisForm.reset();
}


function getBack(){
	
	
}

function goAdd()
{
	window.location="${pageContext.request.contextPath}/role.do?method=applyAdd";
}


function submitAndsendMessage(){

	if(document.getElementById("chooseValue_rightapply").value=="")
	{
		alert("请选择要申请的菜单项！");
	}
	else
	{
		if(confirm("确定删除此角色？")){
			window.location="role.do?method=del&&act=del&&chooseValue="+document.getElementById("chooseValue_rightapply").value;
		}
	}

}

function goUserPpm(){
	window.location="role.do?method=UserRole";
}

function goPpm(){
	window.location="${pageContext.request.contextPath}/role.do?method=RolePopedom";
}

function goUpdatePpm(){
	if(document.getElementById("chooseValue_role").value=="")
	{
		alert("请选择要更新权限的角色！");
	}else{
	window.location="role.do?method=UpdatePopedom&&chooseValue="+document.getElementById("chooseValue_role").value;
	}	
}


function goDelete()
{	
	if(document.getElementById("chooseValue_role").value=="")
	{
		alert("请选择要删除的菜单项！");
	}
	else
	{
		//alert(document.thisForm.chooseValue.value);
		if(confirm("确定删除此角色？")){
			window.location="role.do?method=del&&act=del&&chooseValue="+document.getElementById("chooseValue_role").value;
		}
	}
}
function goEdit()
{
	if(document.getElementById("chooseValue_role").value=="")
	{
		alert("请选择要修改的角色！");
	}
	else
	{
		//alert(document.thisForm.chooseValue.value);
		window.location="role.do?method=del&&chooseValue="+document.getElementById("chooseValue_role").value+"";
	}
}
</Script>
</body>
</html>
