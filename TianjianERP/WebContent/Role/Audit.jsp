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

<input type="button"  value="通过申请" class="flyBT" onclick="submits()">	
<input name="close" type="button" class="flyBT" value=" 返 回 " onclick="history.back(-1)">
<input name="view" type="button" class="flyBT" value=" 打 印 " onclick="print_rightaudit();">



<form name="thisForm" method="post" action="">
<table width="100%" cellspacing="0" cellpadding="0"><tr><td height="20" align=center><font size=3 color=red><b>权限申请</b></font></td></tr></table>
<input type="hidden" name="chooseValues" id="chooseValues">
<input type="hidden" name="noChooseValues" id="noChooseValues">
<input type="hidden" name="applyId" id="applyId" value=${id }>
<input type="hidden" name="userId" id="userId" value=${userId }>
<textarea rows="5" cols="40" name="auditmemo" id="auditmemo" title="备注"></textarea> 
<table width="30%" cellspacing="0" cellpadding="0"><tr><td height="5"></td></tr></table>


<mt:DataGridPrintByBean name="rightaudit"  />	
<table width="30%" cellspacing="0" cellpadding="0"><tr><td height="10"></td></tr></table>
<input type="button"  value="通过申请" class="flyBT" onclick="submits()">	
</form>

<Script>

var o= document.getElementById("rightaudit");  
  if (o){
 	 setObjEnabled("view");
  	
  }else{
  	
  	setObjDisabled("view");
  }

function setChooseValue_CH_rightaudit(chooseValue)
{
   var i=0;
   var str="";
   var noStr="";
   var oChooses=document.getElementsByName("choose_rightaudit");
   var j=oChooses.length;
   for(i=0;i<j;i++)
   {
      if(oChooses[i].checked==true)
      {
    
         str=str+oChooses[i].value+',';
      }else{
    
      	 noStr=noStr+oChooses[i].value+',';
      }
   }
    str = str.substring(0,(str.length-1));
	document.getElementById("chooseValue_rightaudit").value=str;
	noStr = noStr.substring(0,(noStr.length-1));
	document.getElementById("noChooseValues").value=noStr;
}


function submits(){
		if(confirm("确定通过这些权限？")){
		
		
			document.getElementById("chooseValues").value=document.getElementById("chooseValue_rightaudit").value
		
			thisForm.action="${pageContext.request.contextPath}/role.do?method=auditSave";
			thisForm.submit();
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
