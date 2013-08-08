<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"  errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/calendar_include.jsp" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>事务所管理</title>
</head>

<body leftmargin="5" topmargin="0" style="margin: 0px;padding: 5px;">
<form name="thisForm" method="post" action="" id="thisForm" >
<font color="#FF0000" size="2" ><strong>您现在所在位置: </strong><font color="#0000CC">系统菜单-&gt;事务所管理-&gt;新增人员审批管理</font><br>
<table width="500"  border="0" cellpadding="0" cellspacing="0">
	<tr><td height="1" bgcolor="#0000FF"></td>
	</tr>
</table>
<br>


<div id="divBlock" style="position:absolute;width:100%;height:100%; top:expression(this.offsetParent.scrollTop); z-index:1; padding:10px; background:#ffffff;filter:alpha(opacity=50); text-align:center; display:none;"></div>
<div id="divDepartment" style="position:absolute;width:400px;height:200px; z-index:2;left:expression((document.body.clientWidth-400)/2);top:expression(this.offsetParent.scrollTop + 130); border:1px solid #6595d6; padding:10px; background:#ffffff;text-align:center; display:none ;">
	<fieldset>
		<legend><font size="2">还原人员</font></legend>
		<br>
		<table width="330" height="100%" border="0"  cellspacing="0" cellpadding="0">
		<tr>
			<td colspan="2">
				<div id="revertError" style="display:none">
				<table border=0 width="330" height="100%" cellpadding="0" cellspacing="0">
					<tr>
						<td colspan="2"><span id="tag"><font color="red">注意：您所还原的用户登录名重复，请重新输入！</font></span></td>
					</tr>
					<tr>
						<td width="80" align=right>新的登录名：</td>
						<td width="250" align=left><input name="loginid_two"
									maxlength="20"
									onBlur="goCheckUser();"
									type="text"
									class="required validate-alphanum"
									title="请输入，不得为空,且只能是数字和字母"><span id="divb"></span>
						</td>
					</tr>
				</table>
				</div>
			</td>
		</tr>
		<tr>
			<td width="80" align=right height="35">所属部门：</td>
			<td width="250" align=left>
				<input name="departmentid"
						type="text"
						id="departmentid"
						title="请输入，不得为空"
						onKeyDown="onKeyDownEvent();"
						onKeyUp="onKeyUpEvent();"
						noinput="true"
						onClick="onPopDivClick(this);"
						valuemustexist=true autoid=123>
			</td>
		</tr>
		<tr>
			<td align="right" height="35">操作权限：</td>
			<td align="left">
				<input name="roles"
						type="text"
						id="roles"
						class="required"
 						multiselect="true"
 						title="请输入，不得为空"
 						onKeyDown="onKeyDownEvent();"
 						onKeyUp="onKeyUpEvent();"
 						onClick="onPopDivClick(this);"
 						valuemustexist=true
 						autoid=178>
 			</td>
		</tr>
		<tr><td align=center colspan="2">
		<input type="button" class="flyBT" value="确定" onclick="revertUser();">&nbsp;&nbsp;
		<input type="button" class="flyBT" value="取消" onclick="hiddenDepartment();">
		</td></tr></table>

	</fieldset>
</div>

<div id="examineOrnot" >
<center>
	<font color="red" size="3" style="font-weight: bold;">待审批人员列表</font>
</center>  
<br/>
 
<input name="del" type="button" class="flyBT" value="审批通过" onclick="goRevert(1);">&nbsp;
<input name="del" type="button" class="flyBT" value="删  除" onclick="goDelete2(1);">&nbsp;
<input name="view" type="button" class="flyBT" value="打  印" onclick="print_user3();">

<br/>

<fieldset>
    <legend>用户查找</legend>
	<table border="0" cellpadding="0" cellspacing="0" width="100%" bgcolor="">
		<tr>
			<td width=50>登录名：</td>
			<td align=left><input type="text" name="loginid2" id="loginid2"></td>
			<td width=150></td>
			<td width=50>姓名：</td>
			<td align=left><input type="text" name="name2" id="name2" ></td>
		</tr>
		<tr>
		 <td height="5" colspan=10></td>
		</tr>
		<tr>
		 <td height="1" bgcolor="" colspan=10></td>
		</tr>
		<tr>
		 <td height="5" colspan=10></td>
		</tr>
		<tr>
			<td height=10 colspan = 10 align=center>
			<input type="button" name="srh" value="搜 索" onclick="goSearch_user2();">&nbsp;&nbsp;&nbsp;&nbsp;
			<input type="button" name="csrh" value="清 除" onclick="goClean_CH_user();">
			</td>
		</tr>
	</table>
</fieldset> 
<br> 
<mt:DataGridPrintByBean name="user3"  message="请选择单位编号" />
</div>

</form>
</body>
</html>

<script language="javascript" >
	var AuditReport =  new ActiveXObject("AuditReportPoject.AuditReport");
</script>

<script>
var UsrName = "";
function goSetUsrID()
{
	var obj = getTR();
	thisForm.UsrID.value = obj.cells[3].innerText;
	UsrName = obj.cells[2].innerText;
}

/*检验用户登陆名唯一*/
function goCheckUser() {
	if(thisForm.loginid_two.value != '') {
		var oBao = new ActiveXObject("Microsoft.XMLHTTP");
		oBao.open("POST","user.do?method=CheckUser&loginid=" + thisForm.loginid_two.value,false);
		oBao.send();
		if(oBao.responseText != 'yes') {
			document.getElementById("divb").innerHTML = "用户名已经存在";
			thisForm.loginid.value = '';
			document.all.loginid.focus();
			return false;
		} else {
			document.getElementById("divb").innerHTML = "";
		}
	}else {
		document.getElementById("divb").innerHTML = "";
	}

}

/*显示修改登陆名*/
function updateLoginid(){
	var divObj = document.getElementById("divProduce");
	var blockObj =  document.getElementById("divBlock");
	var divDepartment = document.getElementById("divDepartment");

	divObj.style.display = "";
	blockObj.style.display = "";
}
/*隐藏修改登陆名*/
function hiddenProDiv(){
	var divObj = document.getElementById("divProduce");
	var blockObj =  document.getElementById("divBlock");
	divObj.style.display = "none";
	blockObj.style.display = "none";
	thisForm.loginid_two.value = "";
	document.getElementById("divb").innerHTML = "";
}

/*返回*/
function subClearSearch(){
	//window.location="user.do?method=List";
	window.close();
}
/*新增*/
function goAdd()
{
	window.location="user.do?method=Edit&UserOpt=1";
}
/*修改*/
function goEdit()
{
	if(document.thisForm.chooseValue_user.value=="")
	{
		alert("请选择要修改的非禁用人员！");
	}
	else
	{
		var aJax = new ActiveXObject("Microsoft.XMLHTTP");
		var url="user.do?method=CheckName&id="+thisForm.chooseValue_user.value;

	    aJax.open("POST", url, false);
	    aJax.send();

	    if(aJax.responseText == 'yes'){
	    	alert("系统管理员不能被修改！");
	    }else{
			window.location="user.do?method=Edit&UserOpt=2&id="+thisForm.chooseValue_user.value;
	    }

	}
}
/*还原*/
function goRevert(v){
	if(v==0){
		window.open("user.do?method=List&revert=1");
	}else{
		if(document.thisForm.chooseValue_user3.value=="")
		{
			alert("请选择要审批的待审批人员！");
		}else{
			if(confirm("您是否还原该记录吗？","提示")){
				var oBao = new ActiveXObject("Microsoft.XMLHTTP");
				var url = "user.do?method=CheckUser&goBack=true&id=" + thisForm.chooseValue_user3.value;
				oBao.open("POST", url, false);
				oBao.send();
				var result = oBao.responseText;		
				if(result.indexOf("yes")>-1) {
				  
					document.getElementById("divDepartment").style.display = "";
					
					var allresult = new Array();
					allresult = result.split("*");
					if(allresult.length>2){
						document.getElementById("departmentid").value = allresult[1];
						document.getElementById("roles").value = allresult[2];					
					}
					
				//	document.getElementById("revertError").style.display = "";
				}
			}
		}
	}
}

//还原用户
function revertUser(){
	var departmentid = document.getElementById("departmentid").value;
	var role = document.getElementById("roles").value;

	if(document.getElementById("revertError").style.display == ""){
		if(thisForm.loginid_two.value == ""){
			alert("请先填写登录名！");
			return false;
		}
	}

	if(departmentid == ""){
		alert("请先选择：所在部门！");
		return false;
	}
	if(role == ""){
		alert("请先选择：操作权限！");
		return false;
	}

	if(document.getElementById("divb").innerHTML != ""){
		alert("用户名已经存在");
		return false;
	}

	var lid=thisForm.loginid_two.value;
	var id=thisForm.chooseValue_user3.value;

	var oBao = new ActiveXObject("Microsoft.XMLHTTP");
	var url="user.do?method=Revert&id="+id+"&loginid="+lid+"&random="+Math.random();
	oBao.open("POST", url, false);
	oBao.setRequestHeader("Content-Type","application/x-www-form-urlencoded");
	oBao.send("departmentid="+departmentid+"&roles="+role);
	var strResult = unescape(oBao.responseText);
//	opener.location = opener.location;
	goRefresh_CH_user();
	alert(strResult);
	window.location = "user.do?method=List2";
}

//还原用户1
//已不用
function Revert(){
	var oBao = new ActiveXObject("Microsoft.XMLHTTP");
	var lid=thisForm.loginid_two.value;
	var id = thisForm.chooseValue_user.value;
	var departmentid = document.getElementById("departmentid").value;
	var role = document.getElementById("roles").value;

	var url="user.do?method=Revert&id="+id+"&loginid="+lid+"&random="+Math.random();
	oBao.open("POST", url, false);
	oBao.setRequestHeader("Content-Type","application/x-www-form-urlencoded");
	oBao.send("departmentid="+departmentid+"&roles="+role);
	var strResult = unescape(oBao.responseText);
	goRefresh_CH_user();
	alert(strResult);
}

//还原用户2
//已不用
function updateRevert(){
	if(document.getElementById("divb").innerHTML=="" && thisForm.loginid_two.value !=""){
		Revert();
		hiddenProDiv();
	}
}
/*删除*/
function goDelete(v) {
	if(document.thisForm.chooseValue_user.value==""){
		alert("请选择要禁用的非禁用人员！");
		return;
	}else{
		var aJax = new ActiveXObject("Microsoft.XMLHTTP");
		var url="user.do?method=CheckName&id="+thisForm.chooseValue_user.value;

	    aJax.open("POST", url, false);
	    aJax.send();

	    if(aJax.responseText == 'yes'){
	    	alert("系统管理员不能被禁用！");
	    	return;
	    }else{
    		var str = "禁用后该人员将无法登录系统，取消禁用可以在还原功能中进行，确定要禁用吗？";
			if(v==1) str="您的操作会永远删除该记录，您将无法还原该记录，是否继续？";
			if(confirm(str,"提示")){
				var oBao = new ActiveXObject("Microsoft.XMLHTTP");
				var url="user.do?method=Remove&id="+thisForm.chooseValue_user.value+"&opt="+v+"&random="+Math.random();
				oBao.open("POST", url, false);
				oBao.send();
				var strResult = unescape(oBao.responseText);
				goRefresh_CH_user();
				alert(strResult);
			}
	  	}
	}
	
	goClean_CH_user();
}

/*删除*/
function goDelete2(v) {
	if(document.thisForm.chooseValue_user3.value==""){
		alert("请选择要删除的待审批人员！");
		return;
	}else{
		var aJax = new ActiveXObject("Microsoft.XMLHTTP");
		var url="user.do?method=CheckName&id="+thisForm.chooseValue_user3.value;

	    aJax.open("POST", url, false);
	    aJax.send();

	    if(aJax.responseText == 'yes'){
	    	alert("系统管理员不能被禁用！");
	    	return;
	    }else{
    		var str = "禁用后该人员将无法登录系统，取消禁用可以在还原功能中进行，确定要禁用吗？";
			if(v==1) str="您的操作会永远删除该记录，您将无法还原该记录，是否继续？";
			if(confirm(str,"提示")){
				var oBao = new ActiveXObject("Microsoft.XMLHTTP");
				var url="user.do?method=Remove2&id="+thisForm.chooseValue_user3.value+"&opt="+v+"&random="+Math.random();
						
				oBao.open("POST", url, false);
				oBao.send();
				var strResult = unescape(oBao.responseText);
				goRefresh_CH_user();
				alert(strResult);
			}
	  	}
	}
	
	goClean_CH_user();
}
/*读狗*/
function bindDog(v){
	var obj = getTR();
	var usr =obj.cells[3].innerText;
	//解除狗绑定
	var oBao = new ActiveXObject("Microsoft.XMLHTTP");
	var url="user.do?method=ReadDog&random="+Math.random();

	if(v.value == "解除狗绑定"){
		if(confirm("您的操作会解除此用户或狗的绑定，是否继续？","提示")){
			url += "&opt=1&usr="+usr;
			oBao.open("POST", url, false);
			oBao.send();
			obj.cells[9].innerText = "";
			v.value = "绑定狗";
			v.title = "绑定狗信息";

		}
	}else{ //绑定狗

		try{
			document.getElementById("AS_dog").value=AuditReport.funReadDog();
		}catch(e){
			alert("验证控件安装失败或者没安装,请安装");
			//出错了，说明控件安装不成功，导航到专门的安装界面
	        window.location="/AuditSystem/AS_SYSTEM/ocxsetup.htm";
		}
		var AS_dog = document.getElementById("AS_dog").value;
		if(AS_dog != "查找狗失败"){
			url += "&AS_dog="+AS_dog;

			oBao.open("POST", url, false);
			oBao.send();
			var strResult = unescape(oBao.responseText);
			if(strResult==0){
				alert("该加密狗已绑定，请先解除绑定或用另一加密狗作绑定！");
				return false;
			}
			if(strResult==1){
				alert("服务器没有插加密狗，请先插入加密狗！");
				return false;
			}
			if(strResult==2){
				alert("客户端没有插加密狗，请先插入加密狗！");
				return false;
			}
			if(strResult==3){
				alert("服务器与客户端的加密狗信息不同，请重新插入加密狗！");
				return false;
			}

			//obj.cells[8].innerHTML = "<input  type=\"text\" name=\"dog\" id=\"dog\" size=\"15\"  value=\""+strResult+"\"  readOnly  />";
			obj.cells[9].innerHTML = "<input  type=\"text\" name=\"dog\" id=\"dog\" size=\"15\"  value=\""+strResult+"\"  readOnly  />"+
						"<input  type=\"button\" class=\"flyBT\"  name=\"save\" id=\"save\" size=\"15\" value=\"保存\" onclick = \"saveDog('"+usr+"','"+strResult.Trim()+"');\"  />&nbsp;<input  type=\"button\" class=\"flyBT\" name=\"cancel\" value=\"取消\" id=\"cancel\" size=\"15\"  onclick = \"cancelDog()\"  />";
			//document.getElementById("clientDogSysUi").value = strResult;

		}else{
			alert(AS_dog);
			//document.getElementById("clientDogSysUi").value = "";
		}

	}

}
/*保存狗*/
function saveDog(usr,dog){
	var obj = getTR();
	var oBao = new ActiveXObject("Microsoft.XMLHTTP");
	var url="user.do?method=ReadDog&random="+Math.random()+"&opt=2&usr="+usr+"&dog="+dog;
	oBao.open("POST", url, false);
	oBao.send();
	var strResult = unescape(oBao.responseText);
	if(strResult == 5){
		alert("该加密狗已绑定，请先解除绑定或用另一加密狗作绑定！");
		//obj.cells[8].innerText = "";
		obj.cells[10].innerHTML = "<input type=\"button\"  value=\"绑定狗\" style=\"width: 100px;\" class=\"flyBT\"  onclick=\"bindDog(this);\"  title=\"绑定狗信息\">";

	}else{
		obj.cells[9].innerText = dog;
		obj.cells[10].innerHTML = "<input type=\"button\"  value=\"解除狗绑定\" style=\"width: 100px;\" class=\"flyBT\"  onclick=\"bindDog(this);\"  title=\"解除狗绑定\">";
	}
}
/*取消狗*/
function cancelDog(){
	var obj = getTR();
	//obj.cells[8].innerText = "";
	obj.cells[9].innerText = "";
	obj.cells[10].innerHTML = "<input type=\"button\"  value=\"绑定狗\" style=\"width: 100px;\" class=\"flyBT\"  onclick=\"bindDog(this);\"  title=\"绑定狗信息\">";
}

/*项目权限*/
function goEPopedom(){
	if(document.thisForm.chooseValue_user.value=="")
	{
		alert("请选择要修改权限的操作员！");
		return false;
	}
	window.location="user.do?method=ProjectPopedom&id="+thisForm.chooseValue_user.value;
}

/*项目简历*/
function goResume(){
	if(document.thisForm.chooseValue_user.value=="")
	{
		alert("请选择要用户！");
	}
	else
	{
//		alert(UsrName);
		window.location="user.do?method=Item&id="+thisForm.chooseValue_user.value;
	}
}

/*菜单权限*/
function goEditPopedom(){
	if(document.thisForm.chooseValue_user.value=="")
	{
		alert("请选择要修改权限的操作员！");
		return false;
	}
	window.location="user.do?method=UserPopedom&id="+thisForm.chooseValue_user.value;
}

/*批量导入*/
function goJoin(){
	window.location="user.do?method=Upload";
}

/*批量打印*/
function goPrintln(){
	window.location="user.do?method=xlsPrint";

}
function goClean_CH_user() {
	window.location="user.do?method=List2&examineOrnot=true";
}

function goRefresh_CH_user(){
	window.location="user.do?method=List2&examineOrnot=true";
}

function goSearch_user() {
	var flag = false;
	var strW = "";
	if(document.thisForm.loginid.value != "") {

		flag = true;
	}
	if(document.thisForm.name.value != "") {

		flag = true;
	}
	if(flag) {
		document.thisForm.action="user.do?method=List"
		document.thisForm.submit();
	} else {
		alert("至少选一项查询条件！");
	}
}

function goSearch_user2() {
	var flag = false;
	var strW = "";
	if(document.thisForm.loginid2.value != "") {

		flag = true;
	}
	if(document.thisForm.name2.value != "") {

		flag = true;
	}
	if(flag) {
		document.thisForm.action="user.do?method=List2"
		document.thisForm.submit();
	} else {
		alert("至少选一项查询条件！");
	}
}

//隐藏选择部门内容
function hiddenDepartment(){
	document.getElementById("divBlock").style.display = "none";
	document.getElementById("divDepartment").style.display = "none";
}

function goSort() {
    var obj=getTR();
	//window.open("<%= request.getContextPath() %>/Voucher/View.jsp?chooseValue="+obj.voucherid+"&VoucherID="+obj.voucherid+"&AccPackageID="+obj.accpackageid,"_blank","height=480,width=840,resizable=yes, toolbar=no,menubar=no,titlebar=no,scrollbars=yes");
	var myRight = '${userSession.userPopedom }';
	if(myRight.indexOf("10000014")>-1) {
		window.location = "${pageContext.request.contextPath}/oa/UserInformationFrame.jsp?myUserid="+obj.myUserid;
	} else {
		window.location = "${pageContext.request.contextPath}/user.do?method=Edit&UserOpt=2&id="+obj.myUserid;
	}
}

String.prototype.Trim=function(){return this.replace(/(^\s*)|(\s*$)/g,"");}
String.prototype.Ltrim = function(){return this.replace(/(^\s*)/g, "");}
String.prototype.Rtrim = function(){return this.replace(/(\s*$)/g, "");}


//try{
//	var examineOrnot = "${svalue}";
//	
//	if(examineOrnot=="是"){
//		document.getElementById("examineOrnot").style.display = "";
//	}else{
//		document.getElementById("examineOrnot").style.display = "none";
//	}
//}catch(e){
//
//}

</script>
