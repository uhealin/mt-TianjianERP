<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/calendar_include.jsp"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">

<title>员工保险情况管理</title>
</head>
<body leftmargin="0" topmargin="0">
<form name="thisForm" method="post"
	action="/AuditSystem/insurancecircs.do?method=add" id="thisForm">
<jodd:form bean="it" scope="request">
${menuLocation}

<fieldset style="width:100%"><legend>基本信息</legend>

	<table width="100%" height="50" border="0" cellpadding="0"
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
			<td width="13%">
			<div align="right"><font color="red" size=3>*</font>保险类型：</div>
			</td>
			<td width="87%"><input name="insurancetype" type="text"
				id="insurancetype" maxlength="20" class="required" title="请输入，不得为空"
				onkeydown="onKeyDownEvent();" onkeyup="onKeyUpEvent();"
				onclick="onPopDivClick(this);" valuemustexist="true" autoid="2008">
			<input name="autoid" type="hidden" id="autoid"></td>
		</tr>
		<tr>
			<td nowrap="nowrap">
			<div align="right"><font color="red" size=3>*</font>最后缴费金额：</div>
			</td>
			<td nowrap><input name="finallymoney" type="text"
				id="finallymoney" maxlength="20" class="validate-number"
				title="请输入有效数据"></td>
		</tr>
		<tr>
			<td width="13%">
			<div align="right"><font color="red" size=3>*</font>托管单位：</div>
			</td>
			<td width="87%"><input name="trusteeshipunit" type="text"
				id="trusteeshipunit" maxlength="50" class="required"
				title="请输入，不得为空"> <input name="autoid" type="hidden"
				id="autoid"></td>
		</tr>
		<tr>
			<td>
			<div align="right">入保时间：</div>
			</td>
			<td><input name="startdate" type="text" id="startdate"
				class="validate-date-cn" title="请输入日期！" showcalendar="true"></td>
		</tr>
		<tr>
			<td>
			<div align="right">到期时间：</div>
			</td>
			<td><input name="enddate" type="text" id="enddate"
				class="validate-date-cn" title="请输入日期！" showcalendar="true"></td>
		</tr>

		<tr>
			<td nowrap="nowrap">
			<div align="right">最后缴费时间：</div>
			</td>
			<td><input name="finallydate" type="text" id="finallydate"
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
			value="确  定" class="flyBT" onclick="mySubmit();";></td>
		<td width="8%">&nbsp;</td>
		<td width="55%"><input type="button" name="back" value="返  回"
			class="flyBT" onClick="window.history.back();"></td>
	</tr>
</table>

<input name="AS_dog" type="hidden" id="AS_dog" value=""></form>

<script type="text/javascript">
new Validation('thisForm');

function mySubmit() {
//	if(document.getElementById("autoid").value!="") {
//		thisForm.action="/AuditSystem/insurancecircs.do?method=update";
//	} else {
//		thisForm.action="/AuditSystem/insurancecircs.do?method=add";
//	}
	thisForm.action="/AuditSystem/insurancecircs.do?method=add&autoid1=${autoid1}&myedit=${myedit}&all=${all}";
}

</script>

</body>
</html>
