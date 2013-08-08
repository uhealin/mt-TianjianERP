<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>权限申请</title>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>
<script src="<%=request.getContextPath()%>/AS_INCLUDE/searchSession.js charset=gbk"></script>
</head>

<body leftmargin="0" topmargin="0">

${menuLocation}

<table width="30%" cellspacing="0" cellpadding="0"><tr><td height="5"></td></tr></table>

<input type="button"  value="提交申请" class="flyBT" onclick="submits()">	
<input name="close" type="button" class="flyBT" value=" 返 回 " onclick="history.back(-1)">
<input name="view" type="button" class="flyBT" value=" 打 印 " onclick="print_rightapply();">



<form name="thisForm" method="post" action="">
<table width="100%" cellspacing="0" cellpadding="0"><tr><td height="20" align=center><font size=3 color=red><b>权限申请</b></font></td></tr></table>
<input type="hidden" name="chooseValues" id="chooseValues">
<textarea rows="5" cols="40" name="applymemo" id="applymemo"  title="备注"></textarea> 
<table width="30%" cellspacing="0" cellpadding="0"><tr><td height="5"></td></tr></table>
<mt:DataGridPrintByBean name="rightapply"  />	
<table width="30%" cellspacing="0" cellpadding="0"><tr><td height="10"></td></tr></table>
<input type="button"  value="提交申请" class="flyBT" onclick="submits()">	
</form>

<Script>

var o= document.getElementById("rightapply");  
  if (o){
 	 setObjEnabled("view");
  	
  }else{
  	
  	setObjDisabled("view");
  }


function submits(){

	if(document.getElementById("chooseValue_rightapply").value=="")
	{
		alert("请选择要申请的菜单项！");
	}
	else
	{
		if(confirm("确定申请这些权限？")){
			document.getElementById("chooseValues").value=document.getElementById("chooseValue_rightapply").value
			thisForm.action="${pageContext.request.contextPath}/role.do?method=applySave";
			thisForm.submit();
		}
	}

}

function goUserPpm(){
	window.location="role.do?method=UserRole";
}

function goPpm(){
	window.location="role.do?method=RolePopedom";
}

function goUpdatePpm(){
	if(document.getElementById("chooseValue_role").value=="")
	{
		alert("请选择要更新权限的角色！");
	}else{
	window.location="role.do?method=UpdatePopedom&&chooseValue="+document.getElementById("chooseValue_role").value;
	}	
}

function goAdd()
{
	window.location="role.do?method=del";
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
