<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/calendar_include.jsp"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">

<title>人员基本资料管理</title>
</head>
<body leftmargin="0" topmargin="0">
<input type="hidden" id="autoid" name="autoid" value="${autoid }" />
<form name="thisForm" method="post"
	action="/AuditSystem/staff.do?method=add" id="thisForm"><jodd:form
	bean="StaffTable" scope="request">
${menuLocation}
<fieldset style="width:100%"><legend>基本信息（必填）</legend>
	<table width="100%" height="46" border="0" cellpadding="0"
		cellspacing="0">
		<tr>
			<td align="right" width="120">真实姓名：</td>
			<td><input id="cname" type="text" class="required"
				maxlength="18" name="cname" title="请输入，不能为空！" /></td>
			<td align="right" width="80">性别：</td>
			<td><input type="text" id="sex" class="required" name="sex"
				title="请输入，不能为空！" /></td>
		</tr>
		<tr>
			<td align="right" width="120">部门名称：</td>
			<td><input type="text" id="department" class="required"
				maxlength="20" name="department" title="请输入，不能为空！" /></td>
			<td align="right" width="80">岗位：</td>
			<td><input type="text" id="post" class="required" maxlength="20"
				name="post" title="请输入，不能为空！" /></td>
		</tr>
		<tr>
			<td align="right" width="120">联系电话：</td>
			<td><input type="text" id="mobilephone" class="required"
				maxlength="20" name="mobilephone" title="请输入，不能为空！" /></td>
			<td align="right" width="80">Email：</td>
			<td><input type="text" id="email" class="required"
				maxlength="20" name="email" title="请输入，不能为空！" /></td>
		</tr>
	</table>
	</fieldset>
	<fieldset style="width:100%"><legend>高级设置（可选）</legend>

	<table width="100%" height="346" border="0" cellpadding="0"
		cellspacing="0">
		<tr>
			<td align="right" width="120">身份证号：</td>
			<td><input type="text" id="identitycard" maxlength="18"
				name="identitycard" /></td>
			<td align="right" width="80">民族：</td>
			<td><input type="text" id="nation" maxlength="20" name="nation" />
			</td>
		</tr>
		<tr>
			<td align="right" width="120">家庭电话：</td>
			<td><input type="text" id="familytelephone" maxlength="20"
				name="familytelephone" /></td>
			<td align="right" width="80">办公电话：</td>
			<td><input type="text" id="officetelephone" maxlength="20"
				name="officetelephone" /></td>
		</tr>
		<tr>
			<td align="right" width="120">出生日期：</td>
			<td><input type="text" id="birthday" title="请选择日期"
				maxlength="10" name="birthday" showcalendar="true" /></td>
			<td align="right" width="80">婚姻状况：</td>
			<td><input type="text" id="marriage" maxlength="50"
				name="marriage" /></td>
		</tr>
		<tr>
			<td align="right" width="120">入职时间：</td>
			<td><input type="text" id="workstarttime" title="请选择日期"
				maxlength="20" name="workstarttime" showcalendar="true" /></td>
			<td align="right" width="80">工作状态：</td>
			<td><input type="text" id="workstate" maxlength="20"
				name="workstate" /></td>
		</tr>
		<tr>
			<td align="right" width="120">毕业院校：</td>
			<td><input type="text" id="graduateschool" maxlength="30"
				name="graduateschool" /></td>
			<td align="right" width="80">专业：</td>
			<td><input type="text" id="speciality" maxlength="30"
				name="speciality" /></td>
		</tr>
		<tr>
			<td align="right" width="120">学历：</td>
			<td><input type="text" id="schoolage" maxlength="20"
				name="schoolage" /></td>
			<td align="right" width="80">职称：</td>
			<td><input type="text" id="duty" maxlength="20" name="duty" />
			</td>
		</tr>
		<tr>
			<td align="right" width="120">政治面貌：</td>
			<td><input type="text" id="government" maxlength="50"
				name="government" /></td>
			<td align="right" width="80">身体状况：</td>
			<td><input type="text" id="health" maxlength="50" name="health" />
			</td>
		</tr>
		<tr>
			<td align="right" width="120">配偶姓名：</td>
			<td><input type="text" id="consortname" maxlength="20"
				name="consortname" /></td>
			<td align="right" width="80">孩子姓名：</td>
			<td><input type="text" id="childname" maxlength="120"
				name="childname" /></td>
		</tr>
		<tr>
			<td align="right" width="120">开户银行：</td>
			<td><input type="text" id="bank" maxlength="50" name="bank" />
			</td>
			<td align="right" width="80">银行帐号：</td>
			<td><input type="text" id="bankaccounts" maxlength="120"
				name="bankaccounts" /></td>
		</tr>
		<tr>
			<td align="right" width="120">邮政编码：</td>
			<td><input type="text" id="dakcoding" maxlength="6"
				name="dakcoding" /></td>
			<td align="right" width="80">通讯地址：</td>
			<td><input type="text" id="address" maxlength="120" size="42"
				name="address" /></td>
		</tr>
		<tr>
			<td align="right" width="120">离职时间：</td>
			<td><input type="text" id="dimissiontime" title="请选择日期"
				maxlength="20" name="dimissiontime" showcalendar="true" /></td>
			<td align="right" width="80">户口所在地：</td>
			<td><input type="text" id="hukou" maxlength="120" size="42"
				name="hukou" /></td>
		</tr>
		<tr>
			<td align="right" width="120">QQ：</td>
			<td><input type="text" id="qq" maxlength="20" name="qq" /></td>
			<td align="right" width="80">籍贯：</td>
			<td><input type="text" id="nativeplace" maxlength="120"
				size="42" name="nativeplace" /></td>
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
	if(document.getElementById("autoid").value!="") {
	
		thisForm.action="/AuditSystem/staff.do?method=update&autoid="+document.getElementById("autoid").value;
	} else {
	
		thisForm.action="/AuditSystem/staff.do?method=add";
	}
}

</script>

</body>
</html>
