<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/calendar_include.jsp"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">

<title>人事档案缴费管理</title>
</head>
<body leftmargin="0" topmargin="0">
<form name="thisForm" method="post"
	action="/AuditSystem/personCapture.do?method=add" id="thisForm">
<jodd:form bean="pct" scope="request">
${menuLocation}

<fieldset style="width:100%"><legend>人事档案缴费管理</legend>

	<table width="100%" height="46" border="0" cellpadding="0"
		cellspacing="0">

		<c:if test="${all=='all' || param.all=='all' }">
			<tr>
				<td width="120" align="right"><font color="red" size=3>*</font>缴费人:</td>
				<td><input name="userid" type="text" class='required'
					id="userid" maxlength="40" onkeydown="onKeyDownEvent();"
					onkeyup="onKeyUpEvent();" onclick="onPopDivClick(this);"
					valuemustexist="true" autoid="7"></td>
			</tr>
		</c:if>

		<tr>
			<td>
			<div align="right"><font color="red" size=3>*</font>托管单位：</div>
			</td>
			<td nowrap><input name="units" type="text" id="units"
				class="required" size="30"> <input name="autoid"
				type="hidden" id="autoid"></td>
		</tr>
		<tr>
			<td>
			<div align="right">入托时间：</div>
			</td>
			<td><input name="starttime" type="text" id="starttime"
				class="validate-date-cn" title="请输入日期！" showcalendar="true"></td>
		</tr>
		<tr>
			<td>
			<div align="right">到期时间：</div>
			</td>
			<td><input name="endtime" type="text" id="endtime"
				class="validate-date-cn" title="请输入日期！" showcalendar="true"></td>
		</tr>
		<tr>
			<td>
			<div align="right">最后缴费金额：</div>
			</td>
			<td><input name="capturemoney" maxlength="50" type="text"
				id="capturemoney" class="validate-number" size="30" title="请输入有效的金额"></td>
		</tr>
		<tr>
			<td>
			<div align="right">最后缴费时间：</div>
			</td>
			<td><input name="endcapturetime" type="text" id="endcapturetime"
				class="validate-date-cn" title="请输入日期！" showcalendar="true"></td>
		</tr>
	</table>
	</fieldset>
</jodd:form>


<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td height="22" colspan="3">&nbsp;</td>
	</tr>
	<tr>
		<td width="37%" align="right"><input type="submit" name="next"
			value="确  定" class="flyBT" onclick="return mySubmit();";></td>
		<td width="8%">&nbsp;</td>
		<td width="55%"><input type="button" name="back" value="返  回"
			class="flyBT" onClick="window.history.back();"></td>
	</tr>
</table>

<input name="AS_dog" type="hidden" id="AS_dog" value=""></form>
<c:if test="${myEdit!='edit' }">
	<hr>

	<table width="30%" cellspacing="0" cellpadding="0">
		<tr>
			<td height="5"></td>
		</tr>
	</table>
	<input name="add" type="hidden" class="flyBT" value=" 增 加 "
		onclick="goAdd();">
	<input name="mod" type="button" class="flyBT" value=" 修 改 "
		onclick="goEdit();">
	<input name="del" type="button" class="flyBT" value=" 删 除 "
		onclick="goDelete();">
	<input name="view" type="button" class="flyBT" value=" 打 印 "
		onclick="print_personcapture();">

	<form name="thisForm1" id="thisForm1" method="post" action="/AuditSystem/personCapture.do?all=${all }">
	
	<c:if test="${all=='all' }">
<fieldset style="width:100%">
    <legend>查询条件</legend>
	<table width="100%" height="46" border="0" cellpadding="0" cellspacing="0" bgcolor="">
	<tr>
		<td>
		缴费人：&nbsp;&nbsp;<input type="text" name="capturePerson" id="capturePerson" value="" onkeydown="onKeyDownEvent();" onkeyup="onKeyUpEvent();" onclick="onPopDivClick(this);" autoid=7>
		&nbsp;&nbsp;
		托管单位：&nbsp;&nbsp;<input type="text" name="captureDepart" id="captureDepart" value="" onkeydown="onKeyDownEvent();" onkeyup="onKeyUpEvent();" onclick="onPopDivClick(this);" autoid=2007>
		<input type="submit" name="srh" class="flyBT" value="搜 索"  />
             &nbsp;
             <input type="reset" name="csrh" value="显示全部" class="flyBT" onclick="thisForm1.submit();" />
             </td>
	  </tr>
  </table>
</fieldset>
</c:if>
	
	
	<table width="100%" cellspacing="0" cellpadding="0">
		<tr>
			<td height="20" align=center><font size=3 color=red><b>人事档案缴费记录</b></font></td>
		</tr>
	</table>

	<table width="30%" cellspacing="0" cellpadding="0">
		<tr>
			<td height="5"></td>
		</tr>
	</table>
	<mt:DataGridPrintByBean name="personcapture" /></form>
</c:if>

<script type="text/javascript">
new Validation('thisForm');

if(${myEdit=='edit'}) {
	document.getElementById("next").value="保  存";
} else {
	document.getElementById("next").value="续  费";
}

function mySubmit() {
	if(document.getElementById("starttime").value>document.getElementById("endtime").value) {
		alert("入托时间不能大于到期时间");
		return false;
	}
	if(${myEdit=='edit'}) {
		thisForm.action="/AuditSystem/personCapture.do?method=update&all=${all}"
	} else {
		thisForm.action="/AuditSystem/personCapture.do?method=add&all=${param.all}"
	}
}

function goAdd()
{
	window.location="/AuditSystem/oa/personCapture/AddandEdit.jsp?all=${all}";
}
function goDelete()
{	
	if(document.getElementById("chooseValue_personcapture").value=="")
	{
		alert("请选择要删除的人事档案缴费记录！");
	}
	else
	{
		//alert(document.thisForm.chooseValue.value);
		if(confirm("确定删除此人事档案缴费记录？")){
			window.location="personCapture.do?method=del&&autoid="+document.getElementById("chooseValue_personcapture").value+"&all=${all}";
		}
	}
}
function goEdit()
{
	if(document.getElementById("chooseValue_personcapture").value=="")
	{
		alert("请选择要修改的人事档案缴费记录！");
	}
	else
	{
		//alert(document.thisForm.chooseValue.value);
		window.location="/AuditSystem/personCapture.do?method=edit&autoid="+document.getElementById("chooseValue_personcapture").value+"&myEdit=edit&all=${all}";
	}
}
</script>

</body>
</html>
