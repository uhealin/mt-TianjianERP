<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/Validate_include.jsp" %>
<%@ page import="java.util.*"%>


<%
ArrayList alDep=(ArrayList)request.getAttribute("alDep");

%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">

<title>客户权限</title>	
<script src="${pageContext.request.contextPath}/AS_INCLUDE/calendar.js" type="text/javascript" charset="GBK"></script>
<script src="${pageContext.request.contextPath}/AS_INCLUDE/calendar-zh.js" type="text/javascript" charset="GBK"></script>
<script src="${pageContext.request.contextPath}/AS_INCLUDE/calendar-setup.js" type="text/javascript" charset="GBK"></script>

<script type="text/javascript">

var ext_init = function () {
	 var tbar_customer = new Ext.Toolbar({
		   		renderTo: "divBtn",
		   		height:30,
		   		defaults: {autoHeight: true,autoWidth:true},
	            items:[
/*	            
	            {
		            text:'全选',
		            cls:'x-btn-text-icon',
		            icon:'${pageContext.request.contextPath}/img/selectall.gif',
		            handler:function () {
		            	
		            }
	       		},'-',{ 
		            text:'清空',
		            cls:'x-btn-text-icon',
		            icon:'${pageContext.request.contextPath}/img/clear.gif',
		            handler:function(){
						history.back();
					}
	       		},'-',{
		            text:'保存授权',
		            cls:'x-btn-text-icon',
		            icon:'${pageContext.request.contextPath}/img/save.gif',
		            handler:function () {
		            	
		            }
	       		},'-',{ 
		            text:'删除授权',
		            cls:'x-btn-text-icon',
		            icon:'${pageContext.request.contextPath}/img/delete.gif',
		            handler:function(){
						history.back();
					}
	       		},'-',
*/	       		
	       		{ 
		            text:'返回',
		            cls:'x-btn-text-icon',
		            icon:'${pageContext.request.contextPath}/img/back.gif',
		            handler:function(){
						//history.back();
						window.location = "${pageContext.request.contextPath}/customer.do";
					}
	       		},'-',new Ext.Toolbar.Fill()]
	 });
	 
	}
	
	window.attachEvent("onload",ext_init) ;
</script>

</head>
<body leftmargin="0" topmargin="0">
<div id="divBtn" ></div>
<div id="waiting"></div>
<div style="height:expression(document.body.clientHeight-27);overflow:auto;">
<form name="thisForm" method="post" action="" class="autoHeightForm">
	<table width="100%" border="0"  style="border:1px outset #000000">
	<tr><td valign="middle">当前操作员：${UserID}</td></tr>
	</table>
	
	<table width="30%" cellspacing="0" cellpadding="0"><tr><td height="5"></td></tr></table>
	<table width="100%" cellpadding="5" border="1" bordercolor="#000000" style="border:1px outset #000000">
	<tr><td width="20%" valign="top">
	<span style="height:10 vertical-align:bottom">按部门授权</span>
	<div style="width:100%;height:165;overflow: auto;">
	${sTable}
	</div>
	</td>
	<td align="center" width="100%">
	<table border="0" width="100%" height="100%">
		<tr height="10"><td>按人员授权</td></tr>
		<tr height="160"><td align="left" valign="top" >
		<div style="width:100%;height:160;overflow: auto;" id="AUser">
	
		</div>
		</td></tr>

	</table>
	</td>
	</tr>
	</table>
	
	<table width="30%" cellspacing="0" cellpadding="0"><tr><td height="5"></td></tr></table>
	
	<table width="100%" border="0"  style="border:1px outset #000000">
	<tr><td valign="middle">当前客户信息：${al} </td></tr>
	</table>
	<table width="30%" cellspacing="0" cellpadding="0"><tr><td height="3"></td></tr></table>
	<table width="100%" cellpadding="5" border="1" bordercolor="#000000" style="border:1px outset #000000">
		
		<tr height="15"><td valign="middle">已授权人员或部门列表
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		<input name="search" type="button" class="flyBT" value="保存授权"onclick="goSave();">&nbsp;
		<input name="Delret" type="button" class="flyBT" value="删除授权"onclick="goClear();" disabled>&nbsp;
		</td></tr>
		<tr height="215"><td align="center" valign="top" >
		<div style="width:100%;height:215;overflow: auto;" id="BUser">

		</div>
		</td></tr>
	</table>
</form>
</div>
</body>
</html>
<Script type="text/javascript">

function goClear(){

	if(confirm("您的操作会让此客户的客户权限为[公开]，是否继续？","提示")){
		var oBao = new ActiveXObject("Microsoft.XMLHTTP");
		oBao.open("POST","customer.do?method=BccUser&&opt=2&DepartID=${DepartID}&random="+Math.random(),false);
		oBao.send();
		var strResult = unescape(oBao.responseText);
		if(strResult!=null){
			alert("清除成功");
		}
		window.location.reload();
	}
	
}



//getBUser();
var sr = "`";
var ttr = false;
function getBUser(){

	var ss = "";
	var selectBox = document.getElementsByTagName("INPUT");
	
	for(var i=0;i<selectBox.length;i++){
		if(selectBox[i].type=='checkbox' && selectBox[i].name=='departname' && selectBox[i].checked == true) {
			ss += "" + selectBox[i].value + "`";
			
		}
	
		if(selectBox[i].type=='checkbox' && selectBox[i].name=='Userid') {
		
			if(sr.indexOf("`"+selectBox[i].value+"`")==-1 && selectBox[i].checked == true){
				sr += selectBox[i].value + "`";
			}
			if(sr.indexOf("`"+selectBox[i].value+"`")>-1 && selectBox[i].checked == false){
				sr = sr.replace("`"+selectBox[i].value+"`","`");
			//	alert(sr);
			}
		}
	}
	
	var oBao = new ActiveXObject("Microsoft.XMLHTTP");
	oBao.open("POST","customer.do?method=BccUser&&opt=1&DepartID=${DepartID}&optSs="+ss+"&optSr="+sr+"&random="+Math.random(),false);
	oBao.send();
	var strResult = unescape(oBao.responseText);
	var obj = document.getElementById("BUser");
	obj.innerHTML = strResult;

	if(ttr){
		doLoadCalendar();
	}
}



getUser();
function getUser(){
//	alert(uid);
	var oBao = new ActiveXObject("Microsoft.XMLHTTP");
	oBao.open("POST","customer.do?method=AccUser&&random="+Math.random(),false);
//	oBao.open("POST","AccUser.jsp?UserID="+uid+"&random="+Math.random(),false);
	oBao.send();
	var strResult = unescape(oBao.responseText);
	
	var obj = document.getElementById("AUser");
	obj.innerHTML =  strResult;
	
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
function goRet(){
	window.location="customer.do";
}
function selectAll(){
	var selectBox = document.getElementsByTagName("INPUT");
	for(var i=0;i<selectBox.length;i++){
		if(selectBox[i].type=='checkbox') {
			selectBox[i].checked = true;
		}
	}
	getBUser();
}
function selectNone(){
	var selectBox = document.getElementsByTagName("INPUT");
	for(var i=0;i<selectBox.length;i++){
		if(selectBox[i].type=='checkbox') {
			selectBox[i].checked = false;
		}
	}
	getBUser();
}
function goSave(){
	var selectBox = document.getElementsByTagName("INPUT");
	var result = "";
	for(var i=0;i<selectBox.length;i++){
		if(selectBox[i].type=='checkbox' && selectBox[i].checked ==true  && selectBox[i].name=='Saveid') {
			var vv = document.getElementById("bdate"+selectBox[i].tvalue).value;
			if(vv=="")vv=" ";
			result += selectBox[i].value + "`" + vv + "|";
		}
	}
//	if(result!=""){
		window.location="customer.do?method=SaveRight&&DepartID=${DepartID}&result="+result+"&random="+Math.random();
//	}
//	alert(result);
}

getDList();
function getDList(){


<%
if(alDep!=null){
	for(int i=0;i<alDep.size();i++){
		ArrayList all = (ArrayList)alDep.get(i);
		String str = (String)all.get(0);
		String ss = (String)all.get(1);
%>
	var selectBox = document.getElementsByTagName("INPUT");
	for(var i=0;i<selectBox.length;i++){
	if(selectBox[i].type=='checkbox') {
		if(selectBox[i].value=="<%=str%>"){
			selectBox[i].checked = true;
		}
	}
	}
<%		
	}

	if(alDep.size()>0){
%>
		document.thisForm.Delret.disabled = false;
<%}
}
%>		
getBUser();
ttr=true;
<%
if(alDep!=null){
for(int i=0;i<alDep.size();i++){
	ArrayList all = (ArrayList)alDep.get(i);
	String str = (String)all.get(0);
	String ss = (String)all.get(1);
%>
	var selectBox = document.getElementsByTagName("INPUT");
	
	//if(selectBox[i].type=='checkbox' && selectBox[i].name=='Saveid'){
	//	var aaa="bdate<%=str%>";
	//	document.getElementById(aaa).value = "<%=ss%>";
	//}
<%	
}
}
%>

}

</Script>

