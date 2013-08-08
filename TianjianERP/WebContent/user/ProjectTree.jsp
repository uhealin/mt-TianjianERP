<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"  errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>项目权限设置</title>


<script Language=JavaScript>

function ext_init(){

	var tbar_customer = new Ext.Toolbar({
		renderTo: "divBtn",
		defaults: {autoHeight: true,autoWidth:true},
 		items:[{
			text:'保存',
            cls:'x-btn-text-icon',
            icon:'${pageContext.request.contextPath}/img/save.gif',
            handler:function () {
				goSave();
			}
      	},'-',{
            text:'全选',
            cls:'x-btn-text-icon',
            icon:'${pageContext.request.contextPath}/img/import.gif',
            handler:function(){
				selectAll();
			}
      	},'-',{
            text:'清空',
            cls:'x-btn-text-icon',
            icon:'${pageContext.request.contextPath}/img/import.gif',
            handler:function(){
				selectNone();
			}
      	},'-',{
            text:'返回',
            cls:'x-btn-text-icon',
            icon:'${pageContext.request.contextPath}/img/back.gif',
            handler:function(){
				window.history.back();
			}
      	},'-',new Ext.Toolbar.Fill()]
	});
        
}

</script>
<style>
.mySpan {
	color:#FF6600; 
	font-family:"宋体";
	font: normal;
	font-size: 9pt;
	padding: 0px;
	margin: 0px;
}

input {
	border: 1px solid #AEC9D3;
}

legend {
	color: #006699;
}

body {
	background-image:url("${pageContext.request.contextPath}/images/new_bg.gif");
	overflow:hidden ;
	
}
</style>

</head>
<body leftmargin="10" topmargin="5">

<div id="divBtn"></div>

<form name="thisForm" method="post" action="" class="autoHeightForm">
<span class="formTitle" >
[${name}]&nbsp;&nbsp;的&nbsp;&nbsp;项&nbsp;&nbsp;目&nbsp;&nbsp;权&nbsp;&nbsp;限
</span>


<table width="98%" border="0" cellpadding="0" cellspacing="0">
<tr>
	<td height="30" width="160" valign="top" align="right">&nbsp;</td>
	<td>
	<div id="divTree"  style="overflow:auto;height: 450" >
	${sTable }
	</div>
	</td>
</tr>
</table>	

<input type="hidden" name="stAll" value="">
<input type="hidden" name="loginid" value="${loginid }">
</form>
</body>
</html>
<script>
getTree();
function getTree(){ 
	var ppm = "${ppm }";
	var pp = ppm.split(".");
//	alert(pp)
	var selectBox = document.getElementsByTagName("INPUT");
	for(var j=0;j<pp.length;j++){
		for(var i=0;i<selectBox.length;i++){
			if(selectBox[i].type=='checkbox' && selectBox[i].value==pp[j]) {
				selectBox[i].checked = true;
			}
		}
	}
}

var stAll = ".";
function getSubTree(id)
{
	var objId = "subTree"+id;
	var objImg = "ActImg"+id;
	var objBank = "subImg"+id;
	var objM=document.getElementById(objImg);
	var objB=document.getElementById(objBank);
	var obj=document.getElementById(objId);
	if(obj.style.display=="")
	{
		obj.style.display = "none";
		objB.style.display = "none";
		objM.src="${pageContext.request.contextPath}/images/plus.jpg";
	}
	else
	{
		obj.style.display = "";
		objB.style.display = "";
		objM.src="${pageContext.request.contextPath}/images/nofollow.jpg";
	}
}
function selectAll(){
	var selectBox = document.getElementsByTagName("INPUT");
	for(var i=0;i<selectBox.length;i++){
		if(selectBox[i].type=='checkbox') {
			selectBox[i].checked = true;
		}
	}

}
function selectNone(){
	var selectBox = document.getElementsByTagName("INPUT");
	for(var i=0;i<selectBox.length;i++){
		if(selectBox[i].type=='checkbox') {
			selectBox[i].checked = false;
		}
	}

}
function getBUser(){

}
function goSave()
{
	stAll = ".";
	var selectBox = document.getElementsByTagName("INPUT");
	var result = "";
	for(var i=0;i<selectBox.length;i++){
		if(selectBox[i].type=='checkbox' && selectBox[i].checked ==true  && selectBox[i].name=='departname') {			
			stAll += selectBox[i].value + ".";
		}
	}
	

	document.thisForm.stAll.value = stAll;
		var departid=<%=request.getParameter("departid")%>;
	var vflag="<%=request.getParameter("myflag")%>";
	var menuid="<%=request.getParameter("menuid")%>";
	thisForm.action="user.do?method=ProjectPopedom&opt=save&flag="+vflag+"&menuid="+menuid+"&departid="+departid+"";
	thisForm.submit();
} 

</script>
<script Language=JavaScript>ext_init();</script>