<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>人员角色关系管理</title>
<script Language=JavaScript>

	function ext_init(){
        
	    var tbar = new Ext.Toolbar({
	   		renderTo: "divBtn",
	   		defaults: {autoHeight: true,autoWidth:true},
            items:[{
	            text:'保存',
	            cls:'x-btn-text-icon',
	            icon:'${pageContext.request.contextPath}/img/save.gif',
	            handler:function(){
	            	goSave();
				}  
       		},'-',{
	            text:'返回',
	            cls:'x-btn-text-icon',
	            icon:'${pageContext.request.contextPath}/img/back.gif',
	            handler:function(){
					window.history.back();
				}
       		},'->'
			]
        });
        
    }
    
    window.attachEvent('onload',ext_init);
    
</script>



</head>
<body leftmargin="0" topmargin="0">

<div id="divBtn"></div>

<form name="thisForm" method="post" action="" class="autoHeightForm">


	
	<table width="100%" cellspacing="0" cellpadding="0"><tr><td height="20" align=center><font size=3 color=red><b>人员角色关系管理</b></font></td></tr></table>


	<table width="100%" cellpadding="5" border="1" bordercolor="#000000" style="border:1px outset #000000">
	<tr><td width="20%" valign="top">
	<span style="height:10 vertical-align:bottom">角色名称</span>&nbsp;&nbsp;
	<a href="#" onClick="look();">查看角色权限</a>
	<div style="width:100%;height:165;overflow: auto;">
	 
	${radio}
	
	</div>
	</td>
	<td align="center" width="100%">
	<table border="0" width="100%" height="100%">
		<tr height="10"><td>机构人员</td></tr>
		<tr height="160"><td align="left" valign="top" >
		<div style="width:100%;height:160;overflow: auto;" id="AUser">
	
		</div>
		</td></tr>

	</table>
	</td>
	</tr>
	</table>
	
	<table width="30%" cellspacing="0" cellpadding="0"><tr><td height="5"></td></tr></table>	
	<table width="100%" cellpadding="5" border="1" bordercolor="#000000" style="border:1px outset #000000">
		
		<tr height="15"><td valign="middle">已关联人员列表		
		</td></tr>
		<tr height="130"><td align="center" valign="top" >
		<div style="width:100%;height:130;overflow: auto;" id="BUser">

		</div>
		</td></tr>
	</table>
</form>
</body>
</html>
<Script type="text/javascript">


function goBack(){
	window.location="role.do";
}

var rid = "";
var rname = "";


function onRole(v){
	var oBao = new ActiveXObject("Microsoft.XMLHTTP");
	oBao.open("POST","role.do?method=BccUser&&random="+Math.random()+"&rid="+v.value,false);
	oBao.send();
	var strResult = unescape(oBao.responseText);
	var obj = document.getElementById("BUser");
	obj.innerHTML =  strResult;
	
	var selectBox = document.getElementsByTagName("INPUT");
	for(var i=0;i<selectBox.length;i++){
		if(selectBox[i].type=='checkbox' && selectBox[i].name=='Userid') {
			selectBox[i].checked = false;
		}
	}
	
	rid = v.value;
	rname = v.rname;
	isCheck();
}

getUser();
function getUser(){
//	alert(uid);
	var oBao = new ActiveXObject("Microsoft.XMLHTTP");
	oBao.open("POST","role.do?method=AccUser&&random="+Math.random(),false);
//	oBao.open("POST","AccUser.jsp?UserID="+uid+"&random="+Math.random(),false);
	oBao.send();
	var strResult = unescape(oBao.responseText);
	
	var obj = document.getElementById("AUser");

	obj.innerHTML =  strResult;
	
}

function isCheck(){
	var saves = "`";
	var selectBox = document.getElementsByTagName("INPUT");
	for(var i=0;i<selectBox.length;i++){
		if(selectBox[i].type=='checkbox' && selectBox[i].name=='Saveid') {
			saves += selectBox[i].value + "`";
		}
	}
	
	for(var i=0;i<selectBox.length;i++){
		if(selectBox[i].type=='checkbox' && selectBox[i].name=='Userid') {
			if(saves.indexOf("`"+selectBox[i].value+"`")>-1){
				selectBox[i].checked = true;
				continue;
			}
		}
	}
}


//getBUser();
var sr = "`";
var ttr = false;
function getBUser(){

	var selectBox = document.getElementsByTagName("INPUT");
	for(var i=0;i<selectBox.length;i++){

		if(selectBox[i].type=='checkbox' && selectBox[i].name=='Userid') {
			if(sr.indexOf("`"+selectBox[i].value+"`")==-1 && selectBox[i].checked == true){
				sr += selectBox[i].value + "`";
			}
			if(sr.indexOf("`"+selectBox[i].value+"`")>-1 && selectBox[i].checked == false){
				sr = sr.replace("`"+selectBox[i].value+"`","`");	
			}
		}
	}
	
	var oBao = new ActiveXObject("Microsoft.XMLHTTP");
	oBao.open("POST","role.do?method=BccUser&&random="+Math.random()+"&rid="+rid+"&usr="+sr,false);
	oBao.send();
	var strResult = unescape(oBao.responseText);
	
	var obj = document.getElementById("BUser");
	obj.innerHTML =  strResult;

}


function goSave(){
	var sUsr = "`";
	var selectBox = document.getElementsByTagName("INPUT");
	for(var i=0;i<selectBox.length;i++){		
		if(selectBox[i].type=='checkbox' && selectBox[i].name=='Userid' && selectBox[i].checked == true) {
			sUsr += selectBox[i].value + "`";
		}		
	}
	//alert(sUsr);
	if(rid=="1" && sUsr.indexOf('`19`')==-1){
		alert("［"+rname+"］角色不能删除［系统管理员］的关联！");
		return false;
	}
	/*
	if(sUsr=="`"){
		alert("请选择关联人员！");
		return false;
	}
	*/
	var oBao = new ActiveXObject("Microsoft.XMLHTTP");
	oBao.open("POST","role.do?method=SaveRole&&random="+Math.random()+"&rid="+rid+"&sUsr="+sUsr,false);
	oBao.send();
	alert("保存成功！");
	window.location.reload();
}

function look(){

	var selectBox = document.getElementsByTagName("INPUT");
	for(var i=0;i<selectBox.length;i++){
		if(selectBox[i].type=='radio' && selectBox[i].name=='role') {
			if(selectBox[i].checked){
				rid=selectBox[i].value
				break;
			}
		}
	}
	if( rid==""){
		alert("请选择查看的角色！");
		return false;
	}
	window.location="role.do?method=UpdatePopedom&&chooseValue="+rid;
}

</Script>

