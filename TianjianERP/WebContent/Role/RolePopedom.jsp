<%@page import="org.util.Debug"%>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
 <%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<link href="<%=request.getContextPath()%>/AS_CSS/css_main.css" rel="stylesheet" type="text/css">
<link rel="stylesheet" type="text/css" href="../AS_CSS/calendar.css">

<script type="text/javascript" charset="GBK"  src="../AS_INCLUDE/cal.js"></script>
<script type="text/javascript" charset="GBK"  src="../AS_INCLUDE/calendar.js"></script>
<script type="text/javascript" charset="GBK" src="../AS_INCLUDE/calendar-zh.js"></script>
<script type="text/javascript"  charset="GBK" src="../AS_INCLUDE/calendar-setup.js"></script>

</head>
<body leftmargin="0" topmargin="0">
<div id="waiting"></div>
<form name="thisForm" method="post" action="">
<font color="#FF0000" size="2" ><strong>您现在所在位置: </strong><font color="#0000CC">系统管理-&gt;角色管理-&gt;角色授权</font></font><br>
	<table width="500"  border="0" cellpadding="0" cellspacing="0">
	  <tr>
	      <td height="1" bgcolor="#0000FF"></td>
	  </tr>		
	</table>
	<table width="30%" cellspacing="0" cellpadding="0"><tr><td height="5"></td></tr></table>
	<table width="30%" cellspacing="0" cellpadding="0"><tr><td height="5"></td></tr></table>	
	<table width="100%" cellspacing="0" cellpadding="0"><tr><td height="20" align=center><font size=3 color=red><b>角色授权</b></font></td></tr></table>
	<table width="30%" cellspacing="0" cellpadding="0"><tr><td height="5"></td></tr></table>		
	<table width="100%" cellpadding="5" border="1" bordercolor="#000000" style="border:1px outset #000000">
	<tr>
	<td width="20%" valign="top"><span style="height:10 vertical-align:bottom">角色名称</span>
	<div style="width:100%;height:350;overflow: auto;"> ${string}</div></td>
	<td width="80%" valign="top"><span style="height:10 vertical-align:bottom">系统菜单&nbsp;&nbsp;&nbsp;&nbsp;<a href="#" onClick="selectAll();">全选</a>&nbsp;&nbsp;&nbsp;&nbsp;<a href="#" onClick="selectNone();">清空</a></span>
	<div style="width:100%;height:350;overflow: auto;">
	${string2}
	</div></td>
	</tr>	
	</table>
	<table width="30%" cellspacing="0" cellpadding="0"><tr><td height="5"></td></tr></table>
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr><td align="center">
	<input type="button" name="next" value="授  权" class="flyBT" onclick="goSave();">&nbsp;&nbsp;
	<input type="button" name="back" value="返  回" class="flyBT"  onClick="goBack();">
	</td>
	</tr>
	</table>
	<input type="hidden" name="stAll" value="">
	<input type="hidden" name="stRole" value="">
</form>
</body>
</html>
<Script type="text/javascript">
function onRole(v){

}

function selectAll()
{
	var len = document.thisForm.MenuID.length;
	var i = 0;
	for(i=0;i<len;i++)
	{
		if(document.thisForm.MenuID[i].disabled==false){
		document.thisForm.MenuID[i].checked = true;
		}
	}
}
function selectNone()
{
	var len = document.thisForm.MenuID.length;
	var i = 0;
	for(i=0;i<len;i++)
	{
		if(document.thisForm.MenuID[i].disabled==false){
		document.thisForm.MenuID[i].checked = false;
		}
	}
}
function getSubTree(id)
{
	var objId = "subTree"+id;
	var objImg = "ActImg"+id;
	var objBank = "subImg"+id;
	var objM=document.getElementById(objImg);
	var objB=document.getElementById(objBank);
	var obj=document.getElementById(objId);
	if(obj.style.display=="block")
	{
		obj.style.display = "none";
		objB.style.display = "none";
		objM.src="${pageContext.request.contextPath}/images/plus.jpg";
	}
	else
	{
		obj.style.display = "block";
		objB.style.display = "block";
		objM.src="${pageContext.request.contextPath}/images/nofollow.jpg";
	}
}
function setEnableTree(temp) {
	
	if(temp.checked == true) {
		var len = document.thisForm.MenuID.length;
		var i = 0;
		var str = "";
		for(i=0;i<len;i++) {
			str = document.thisForm.MenuID[i].id;	
			if(str.indexOf(temp.value)== 0) {
				document.thisForm.MenuID[i].checked = true;
			}
	
		}
	}else {
		var len = document.thisForm.MenuID.length;
		var i = 0;
		var str = "";
		for(i=0;i<len;i++) {
			str = document.thisForm.MenuID[i].id;	
			if(str.indexOf(temp.value)== 0) {
				document.thisForm.MenuID[i].checked = false;
			}
		}
	}
}
function setCountEnable()
{
	stAll = ".";
}

function goSave(){
	
	var len = document.thisForm.role.length;
	stRole = ".";
	var i = 0;
	for(i=0;i<len;i++){
		if(document.thisForm.role[i].checked == true)
		{
			stRole = stRole + document.thisForm.role[i].value + ".";
		}
	}
	
//	alert(stRole);
	if(stRole == "."){
		alert("角色名称不能为空！");
		return false;
	}
	document.thisForm.stRole.value = stRole;
	
	i = 0;
	stAll = ".";
	len = document.thisForm.MenuID.length;
	
	for(i=0;i<len;i++)
	{
		if(document.thisForm.MenuID[i].checked == true)
		{
			stAll = stAll + document.thisForm.MenuID[i].value + ".";
		}
	}
	if(stAll == "."){
		stAll = "";
	}
	document.thisForm.stAll.value = stAll;
//	alert(stAll);
	
	thisForm.action="role.do?method=RolePopedom";
	thisForm.submit();
}

function goBack(){
	window.location="role.do";
}


</Script>

