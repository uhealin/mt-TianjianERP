<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>险种类型管理</title>
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
	onclick="print_insurancetype();">

<form name="thisForm" method="post" action="">
<table width="100%" cellspacing="0" cellpadding="0">
	<tr>
		<td height="20" align=center><font size=3 color=red><b>险种类型管理</b></font></td>
	</tr>
</table>

<table width="30%" cellspacing="0" cellpadding="0">
	<tr>
		<td height="5"></td>
	</tr>
</table>
<mt:DataGridPrintByBean name="insurancetype" /></form>


<iframe src="/AuditSystem/insurancecircs.do?all=${all }"
	scrolling="auto" height="300" width="100%" id="personHistory"
	name="personHistory" frameborder="0"></iframe>

<Script>

function goAdd()
{
	window.location="/AuditSystem/oa/insurancetype/AddandEdit.jsp?all=${all }";
}
function goDelete()
{	
	if(document.getElementById("chooseValue_insurancetype").value=="")
	{
		alert("请选择要删除的险种类型！");
	}
	else
	{
		//alert(document.thisForm.chooseValue.value);
		if(confirm("确定删除此险种类型？")){
			var url = "insurancetype.do?method=checkExistInsurance";
			var request = "&autoid="+document.getElementById("chooseValue_insurancetype").value;
			var returnValue = ajaxLoadPageSynch(url,request);
			if(returnValue.indexOf("ok")>-1) {
				alert("存在该险种保险记录，不允许删除。");
				return;
			} else {
				window.location="insurancetype.do?method=del&&autoid="+document.getElementById("chooseValue_insurancetype").value+"&all=${all }";
				
			}
			
		}
	}
}
function goEdit()
{
	if(document.getElementById("chooseValue_insurancetype").value=="")
	{
		alert("请选择要修改的险种类型！");
	}
	else
	{
		//alert(document.thisForm.chooseValue.value);
		window.location="/AuditSystem/insurancetype.do?method=edit&autoid="+document.getElementById("chooseValue_insurancetype").value+"&all=${all }";
	}
}

function select_insurancetype(myThis) {
	setChooseValue_CH_insurancetype(myThis);
 	var autoid = myThis.autoid;
 	document.getElementById("personHistory").src = "/AuditSystem/insurancecircs.do?autoid1="+autoid+"&all=${all}";
}
</Script>