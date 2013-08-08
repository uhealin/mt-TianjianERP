<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>员工保险情况管理</title>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp"%>
</head>

<body style="padding: 0px;margin: 0px;">

${menuLocation}

<table width="30%" cellspacing="10" cellpadding="0">
	<tr>
		<td height="5"></td>
	</tr>
</table>
<input name="add" type="button" class="flyBT" value=" 续 费 "
	onclick="goAdd();">
<input name="mod" type="button" class="flyBT" value=" 修 改 "
	onclick="goEdit();">
<input name="del" type="button" class="flyBT" value=" 删 除 "
	onclick="goDelete();">
<input name="view" type="button" class="flyBT" value=" 打 印 "
	onclick="print_insurancecircs();">

<form name="thisForm" method="post" action="/AuditSystem/insurancecircs.do?all=${all }">

<c:if test="${all=='all' }">
<fieldset style="width:100%">
    <legend>查询条件</legend>
	<table width="100%" height="46" border="0" cellpadding="0" cellspacing="0" bgcolor="">
	<tr>
		<td>
		缴费人：&nbsp;&nbsp;<input type="text" name="insurancePerson" id="insurancePerson" value="" onkeydown="onKeyDownEvent();" onkeyup="onKeyUpEvent();" onclick="onPopDivClick(this);" autoid=7>
		&nbsp;&nbsp;
		保险类型：&nbsp;&nbsp;<input type="text" name="insuranceType" id="insuranceType" value="" onkeydown="onKeyDownEvent();" onkeyup="onKeyUpEvent();" onclick="onPopDivClick(this);" autoid=2008>
		&nbsp;&nbsp;
		托管单位：&nbsp;&nbsp;<input type="text" name="insuranceDepart" id="insuranceDepart" value="" onkeydown="onKeyDownEvent();" onkeyup="onKeyUpEvent();" onclick="onPopDivClick(this);" autoid=2009>
		<br/>
		<input type="submit" name="srh" class="flyBT" value="搜 索" />
             &nbsp;
             <input type="reset" name="csrh" value="显示全部" class="flyBT" onclick="thisForm.submit();" />
             </td>
	  </tr>
  </table>
</fieldset>
</c:if>

<table width="100%" cellspacing="0" cellpadding="0">
	<tr>
		<td height="20" align=center><font size=3 color=red><b>员工保险情况管理</b></font></td>
	</tr>
</table>
<input type="hidden" id="autoid1" name="autoid1" value="${autoid1 }">
<table width="30%" cellspacing="0" cellpadding="0">
	<tr>
		<td height="5"></td>
	</tr>
</table>
<mt:DataGridPrintByBean name="insurancecircs" /></form>

<Script>

function goAdd()
{
	window.location="/AuditSystem/insurancecircs.do?method=laterOn&autoid1="+document.getElementById("autoid1").value+"&all=${all}";
}
function goDelete()
{	
	if(document.getElementById("chooseValue_insurancecircs").value=="")
	{
		alert("请选择要删除的员工保险情况记录！");
	}
	else
	{
		//alert(document.thisForm.chooseValue.value);
		if(confirm("确定删除此员工保险情况记录？")){
			window.location="insurancecircs.do?method=del&&autoid="+document.getElementById("chooseValue_insurancecircs").value+"&autoid1="+document.getElementById("autoid1").value+"&all=${all}";
		}
	}
}
function goEdit()
{
	if(document.getElementById("chooseValue_insurancecircs").value=="")
	{
		alert("请选择要修改的员工保险情况记录！");
	}
	else
	{
		//alert(document.thisForm.chooseValue.value);
		window.location="/AuditSystem/insurancecircs.do?method=edit&autoid="+document.getElementById("chooseValue_insurancecircs").value+"&autoid1="+document.getElementById("autoid1").value+"&myedit=edit&all=${all}";
	}
}
</Script>