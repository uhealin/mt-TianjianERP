<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/calendar_include.jsp"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">

<title>员工奖惩记录管理</title>
</head>
<body leftmargin="0" topmargin="0">
<input type="hidden" id="autoid" name="autoid" value="${autoid }"/>
<form name="thisForm" method="post"
	action="/AuditSystem/examinelibrary.do?method=add" id="thisForm">
<jodd:form bean="elt" scope="request">
${menuLocation}

<fieldset  style="width:100%" >
<legend>指标基础信息设置</legend>
<table border="0" width="100%" id="table1">
	<tr >
		<td width="70" align="right"><font color="red">*</font>指标类型：</td>
		<td width="" align="left" ><input name="ctype" id="ctype" type="text"  onKeyDown="onKeyDownEvent();" onKeyUp="onKeyUpEvent();" onClick="onPopDivClick(this);" valuemustexist=true autoid=2012 noinput=true size="46" <c:if test="${autoid!=null}">readonly="readonly" style="background-color: #eeeeee;"</c:if> class='required' title="请选择指标类型" >
		</td>
	</tr>
	<tr >
		<td  width="70" align="right"><font color="red">*</font>指标名称：</td>
		<td width="85%" height="29" align="left"><input type="text"  class='required'  id="cname" name="cname" size="46" refer="ctype" <c:if test="${autoid!=null}">readonly="readonly" style="background-color: #eeeeee;"</c:if> >
		&nbsp;&nbsp;&nbsp;&nbsp;
	</tr>
	
	
	 
	
	
	<tr >
		<td  width="70" align="right"><font color="red">*</font>指标规则：</td>
		<td width="85%" height="29" align="left">
		<textarea rows="5" cols="40" class='required' <c:if test="${autoid!=null && myType=='0' }">readonly="readonly" style="background-color: #eeeeee;"</c:if>  id="ccal" name="ccal"></textarea>
		<font color="red">以"~"为分隔符：如：优~良~中~差</font>
		</td>
	</tr>
	
	<tr >
		<td  width="70" align="right"><font color="red">*</font>指标值：</td>
		<td width="85%" height="29" align="left">
		<textarea rows="5" cols="40" class='required' <c:if test="${autoid!=null && myType=='0' }">readonly="readonly" style="background-color: #eeeeee;"</c:if>  id="cformula" name="cformula"></textarea>
		<font color="red">以"~"为分隔符：如：5~4~3~1</font>
		&nbsp;&nbsp;&nbsp;&nbsp;
		</td>
	</tr>
	
	<tr >
		<td  width="70" align="right"><font color="red">*</font>是否有效：</td>
		<td width="85%" height="29" align="left">
		<select id="isenable" name="isenable">
		<option selected="selected">有效</option>
		<option>无效</option>
		</select>
		&nbsp;&nbsp;&nbsp;&nbsp;
		</td>
	</tr>

	<tr >
		<td  width="70" align="right" valign="top">指标说明：</td>
		<td width="85%" height="29" align="left"><textarea rows="5" cols="40" id="memo" name="memo"></textarea></td>
	</tr>
	
	 <tr>
 	 <td  width="70" align="right" >
 	
 	 排序位置：</td>
  	<div id="position1" align="right">
    <td width="85%"><input type="text" maxLength="12" size="12" name="orderid" refer="ctype"
				id="orderid" class='checkexist-wheninputed'
				onkeydown="onKeyDownEvent();" onkeyup="onKeyUpEvent();"
				onclick="onPopDivClick(this);" noinput="true"   valuemustexist="true" 　
				autoid=2013  title="指标位置"    multilevel=true/>
    		<input name="tip" type="radio" id="tip" checked="checked" value="before" />在此之前
    		<input type="radio" value="after" name="tip" id="tip"/>在此之后<font color="red" >&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;不选位置则排在最后</font></td>
	
	</div>
 
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
		thisForm.action="/AuditSystem/examinelibrary.do?method=add&autoid=${autoid}";
	} else {
		thisForm.action="/AuditSystem/examinelibrary.do?method=add";
	}
}

function checkCname() {
	var url = "/AuditSystem/examinelibrary.do";
	var query_String = "method=checkCname&ctype="+document.getElementById("ctype").value+"'&cname="+document.getElementById("cname").value+"&autoid="+document.getElementById("cname").value;
	alert(query_String);
	return;
	var myreturnValue = ajaxLoadPageSynch(url,query_String);
	if(myreturnValue.indexOf("error")==-1) {
		alert("更新成功");
	} else {
		
	}
}

</script>

</body>
</html>
