<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>人员基本资料管理</title>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp"%>
</head>

<body leftmargin="0" topmargin="0">

${menuLocation}

<table width="30%" cellspacing="0" cellpadding="0">
	<tr>
		<td height="5"></td>
	</tr>
</table>
<input name="add" type="button" class="flyBT" value=" 增 加 "
	onclick="goAdd();">
<input name="mod" type="button" class="flyBT" value=" 修 改 "
	onclick="goEdit();">
<input name="del" type="button" class="flyBT" value=" 删 除 "
	onclick="goDelete();">
<input name="view" type="button" class="flyBT" value=" 打 印 "
	onclick="print_staff();">

<form name="thisForm" method="post" action="">
<table width="100%" cellspacing="0" cellpadding="0">
	<tr>
		<td height="20" align=center><font size=3 color=red><b>人员基本资料管理</b></font></td>
	</tr>
</table>

<table width="30%" cellspacing="0" cellpadding="0">
	<tr>
		<td height="5"></td>
	</tr>
</table>
<mt:DataGridPrintByBean name="staff" /></form>

<Script>

function goAdd()
{
	window.location="/AuditSystem/oa/staff/AddandEdit.jsp";
}
function goDelete()
{	
	if(document.getElementById("chooseValue_staff").value=="")
	{
		alert("请选择要删除的人员基本资料记录！");
	}
	else
	{
		//alert(document.thisForm.chooseValue.value);
		if(confirm("确定删除此人员基本资料记录？")){
			window.location="staff.do?method=del&&autoid="+document.getElementById("chooseValue_staff").value;
		}
	}
}
function goEdit()
{
	if(document.getElementById("chooseValue_staff").value=="")
	{
		alert("请选择要修改的人员基本资料记录！");
	}
	else
	{
		//alert(document.thisForm.chooseValue.value);
		window.location="/AuditSystem/staff.do?method=edit&autoid="+document.getElementById("chooseValue_staff").value+"";
	}
}
</Script>