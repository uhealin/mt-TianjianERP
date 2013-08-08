<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"  errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>人员管理</title>
</head>

<script type="text/javascript">
var mytab;
function ext_init(){
	
	 mytab = new Ext.TabPanel({
        id: "tab",
        renderTo: "divTab",
        activeTab: 0, //选中第一个 tab
        autoScroll:true,
        frame: true,
        height: 
        	document.body.clientHeight-Ext.get('divTab').getTop(),
        defaults: {autoHeight: true,autoWidth:true},
        items:[
				   {contentEl: "tabUser1", title: "人员列表",id:"myUser1"} 
        ]
    });
    
    var isRefresh = false ;
    /*
    mytab.on("tabchange",function(tabpanel,tab) {
		if(!isRefresh) {
			goSearch_user2();
		}
		isRefresh = true ;    
    }) ;
    */
    
	var tbar_user = new Ext.Toolbar({
		renderTo: 'gridDiv_user',
		items:[
	       
        {
			text:'查询',
			id:'btn-query',
			cls:'x-btn-text-icon',
	   		icon:'${pageContext.request.contextPath}/img/query.gif',
			handler:QryEmWinFun //queryWinFun
		},
       {
			text:'查看详细信息',
			cls:'x-btn-text-icon',
			icon:'${pageContext.request.contextPath}/img/query.gif',
			handler:function () {
				if(document.getElementById("chooseValue_user").value==""){
					alert("请选择要查看的对象！");
				}else{
				    var url="${pageContext.request.contextPath}/oa/UserInformationFrame.jsp?myUserid="+document.getElementById("chooseValue_user").value;
					var n= mytab.add({
				    	id:document.getElementById("chooseValue_user").value,
				    	closable:true,
				    	title:"详细信息",
				        html:"<iframe style='height:expression(document.body.clientHeight-34);width:100%' src='"+url+"'></iframe>"
				    });
					mytab.setActiveTab(n);
				}
		}
		
       },{
			text:'刷新',
			cls:'x-btn-text-icon',
			icon:'${pageContext.request.contextPath}/img/refresh.gif',
			handler:function () {
				window.location.reload();
			}
			
		}]
	});
	
	
	
	//人员查询
	var queryWin = null;
	function queryWinFun(id){
	
		if(!queryWin) { 
			new BlockDiv().show();
			var searchDiv = document.getElementById("search") ;
		    queryWin = new Ext.Window({
				title: '人员查询',
		     	renderTo : searchWin,
		     	width: 600,
		     	height:195,
	        	closeAction:'hide',
	       	    listeners : {
		         	'hide':{
		         		fn: function () {
		         			new BlockDiv().hidden();
							queryWin.hide();
						}
					}
		        },
	        	layout:'fit',
		    	html:searchDiv.innerHTML,
		    	buttons:[{
	            	text:'确定',
	          		handler:goSearch1
	          		//handler:goView
	        	},{
	            	text:'取消',
	            	handler:function(){
		            	goClean_CH_user();
	               		queryWin.hide();
	            	}
	        	}]
		    });
	    }
	    queryWin.show();
	}

	//禁用人员查询
	var queryWin1 = null;
	function queryWinFun1(id){
	
		if(!queryWin1) {
			new BlockDiv().show();
			var searchDiv = document.getElementById("search1") ;
		    queryWin1 = new Ext.Window({
				title: '禁用人员查询',
		     	renderTo : searchWin,
		     	width: 300,
		     	height:195,
	        	closeAction:'hide',
	       	    listeners : {
		         	'hide':{
		         		fn: function () {
		         			new BlockDiv().hidden();
							queryWin1.hide();
						}
					}
		        },
	        	layout:'fit',
		    	html:searchDiv.innerHTML,
		    	buttons:[{
	            	text:'确定',
	          		handler:function(){
						goSearch2();
						//mytab.setActiveTab(mytab.getComponent('myUser2'));
					}
	        	},{
	            	text:'取消',
	            	handler:function(){
	               		queryWin1.hide();
	            	}
	        	}]
		    });
	    }
	    queryWin1.show();
	}
	
	
} 

//还原人员
var queryWin2 = null;
function queryWinFun2(id){

	if(!queryWin2) {
		new BlockDiv().show();
		var searchDiv = document.getElementById("search2") ;
	    queryWin2 = new Ext.Window({
			title: '还原人员',
	     	renderTo : searchWin,
	     	width: 400,
	     	height:250,
        	closeAction:'hide',
       	    listeners : {
	         	'hide':{
	         		fn: function () {
	         			new BlockDiv().hidden();
						queryWin2.hide();
					}
				}
	        },
        	layout:'fit',
	    	html:searchDiv.innerHTML,
	    	buttons:[{
            	text:'确定',
          		handler:function(){
					revertUser();
				}
        	},{
            	text:'取消',
            	handler:function(){
	            	goClean_CH_user();
               		queryWin2.hide();
            	}
        	}]
	    });
    }
    queryWin2.show();
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
}
</style>

<body>

<form name="thisForm" method="post" action="" id="thisForm" >
<input type="hidden" id="departmentid" name="departmentid" value="${departmentid}" />
<input type="hidden" id="emtype" name="emtype" value="${emtype}" /> 
<input type="hidden" id="qryWhere_em" name="qryWhere_em" value="" /> 
<input type="hidden" id="qryJoin_em" name="qryJoin_em" value="" /> 

<div id="searchWin" style="position:absolute;left:expression((document.body.clientWidth-250)/2);top:expression(this.offsetParent.scrollTop +200); z-index: 2"></div>

<div id="panel"></div>
<div id="divTab" style="overflow:auto">

<div id="tabUser1"
style="height:expression(document.body.clientHeight-62);width:100%">
<mt:DataGridPrintByBean name="user"  message="请选择单位编号" outputData="false" outputType="invokeSearch"  />
</div>

</form>


<div id="customQry_em" style="display: none;">
 <br/>
 <table class="qryTb" align="center"> 
 	<tr>
 		<th width="5%"> 
 			<a href="javascript:;" onclick="addEmQuery('em');"> 
 			<img src="/AuditSystem/img/add.gif" />
 			</a> 
 		</th> 
 		<th width=20%>表名</th>
 		<th width=20%>列名称</th> 
 		<th width=8%>条件</th>
 		<th width=30%>值</th>
 		<th width=5%>删除</th>
	</tr>
	<tbody id="queryTBody_em">
	</tbody> 
 </table>
 <input type="hidden" id="qryWhere_em" name="qryWhere_em">
</div>



</body>
</html>

<script language="javascript" >
	var mtoffice =  new ActiveXObject("MTOffice.WebOffice");
</script>

<script>
var UsrName = "";
function goSetUsrID()
{
	var obj = getTR();
	document.getElementById("UsrID").value = obj.cells[3].innerText;
	UsrName = obj.cells[2].innerText;
}

/*检验用户登陆名唯一*/
function goCheckUser() {
	if(thisForm.loginid_two.value != '') {
		var oBao = new ActiveXObject("Microsoft.XMLHTTP");
		oBao.open("POST","${pageContext.request.contextPath}/user.do?method=CheckUser&loginid=" + document.getElementById("loginid_two").value,false);
		oBao.send();
		if(oBao.responseText != 'yes') {
			document.getElementById("divb").innerHTML = "用户名已经存在";
			document.getElementById("loginid").value = '';
			document.getElementById("loginid").focus();
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
	window.location="${pageContext.request.contextPath}/user.do?method=Edit&UserOpt=1";
}
/*修改*/
function goEdit()
{
	if(document.getElementById("chooseValue_user").value=="")
	{
		alert("请选择要修改的非禁用人员！");
	}
	else
	{
		var aJax = new ActiveXObject("Microsoft.XMLHTTP");
		var url="${pageContext.request.contextPath}/user.do?method=CheckName&id="+document.getElementById("chooseValue_user").value;

	    aJax.open("POST", url, false);
	    aJax.send();

	    if(aJax.responseText == 'yes'){
	    	alert("系统管理员不能被修改！");
	    }else{
			window.location="${pageContext.request.contextPath}/user.do?method=Edit&UserOpt=2&id="+document.getElementById("chooseValue_user").value;
	    }

	}
}
/*还原*/
function goRevert(v){
	if(v==0){
		window.open("${pageContext.request.contextPath}/user.do?method=List&revert=1");
	}else{
		if(document.getElementById("chooseValue_user2").value=="")
		{
			alert("请选择要还原的禁用人员！");
		}else{
			if(confirm("您是否还原该记录吗？","提示")){
			
				//document.getElementById("divBlock").style.display = "";
				//document.getElementById("divDepartment").style.display = "";
				queryWinFun2();
				
				var oBao = new ActiveXObject("Microsoft.XMLHTTP");
				var url = "${pageContext.request.contextPath}/user.do?method=CheckUser&id=" + document.getElementById("chooseValue_user2").value;
				oBao.open("POST", url, false);
				oBao.send();

				if(oBao.responseText != 'yes') {
					document.getElementById("revertError").style.display = "";
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

	var lid = document.getElementById("loginid_two").value;
	var id = document.getElementById("chooseValue_user2").value;

	var oBao = new ActiveXObject("Microsoft.XMLHTTP");
	var url="${pageContext.request.contextPath}/user.do?method=Revert&id="+id+"&loginid="+lid+"&random="+Math.random();
	oBao.open("POST", url, false);
	oBao.setRequestHeader("Content-Type","application/x-www-form-urlencoded");
	oBao.send("departmentid="+departmentid+"&roles="+role);
	var strResult = unescape(oBao.responseText);
//	opener.location = opener.location;
//	goRefresh_CH_user();
	alert(strResult);

	goClean_CH_user();
//	window.location = "${pageContext.request.contextPath}/user.do?method=List&revert=1";
}

//还原用户1
//已不用
function Revert(){
	var oBao = new ActiveXObject("Microsoft.XMLHTTP");
	var lid=document.getElementById("loginid_two").value;
	var id = document.getElementById("chooseValue_user").value;
	var departmentid = document.getElementById("departmentid").value;
	var role = document.getElementById("roles").value;

	var url="${pageContext.request.contextPath}/user.do?method=Revert&id="+id+"&loginid="+lid+"&random="+Math.random();
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
	if(document.getElementById("divb").innerHTML=="" && document.getElementById("loginid_two").value !=""){
		Revert();
		hiddenProDiv();
	}
}
/*删除*/
function goDelete(v) {
	if(document.getElementById("chooseValue_user").value==""){
		alert("请选择要禁用的非禁用人员！");
		return;
	}else{
		var aJax = new ActiveXObject("Microsoft.XMLHTTP");
		var url="${pageContext.request.contextPath}/user.do?method=CheckName&id="+document.getElementById("chooseValue_user").value;

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
				var url="${pageContext.request.contextPath}/user.do?method=Remove&id="+document.getElementById("chooseValue_user").value+"&opt="+v+"&random="+Math.random();
				oBao.open("POST", url, false);
				oBao.send();
				var strResult = unescape(oBao.responseText);
				//goRefresh_CH_user();
				goSearch_user();
				goSearch_user2();
				alert(strResult);
			}
	  	}
	}

	goClean_CH_user();
}

/*删除*/
function goDelete2(v) {
	if(document.getElementById("chooseValue_user2").value==""){
		alert("请选择要永远删除的禁用人员！");
		return;
	}else{
		var aJax = new ActiveXObject("Microsoft.XMLHTTP");
		var url="${pageContext.request.contextPath}/user.do?method=CheckName&id="+document.getElementById("chooseValue_user2").value;

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
				var url="${pageContext.request.contextPath}/user.do?method=Remove&id="+document.getElementById("chooseValue_user2").value+"&opt="+v+"&random="+Math.random();

				oBao.open("POST", url, false);
				oBao.send();
				var strResult = unescape(oBao.responseText);
				goSearch_user();
				goSearch_user2();
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
	var url="${pageContext.request.contextPath}/user.do?method=ReadDog&random="+Math.random();

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
			document.getElementById("AS_dog").value = mtoffice.funReadDog();
		}catch(e){

			mtoffice = null;
			alert("验证控件安装失败或者没安装,请安装");
			//出错了，说明控件安装不成功，导航到专门的安装界面
	        window.location="/AuditSystem/AS_SYSTEM/ocxsetup.htm";
		}

		var AS_dog = document.getElementById("AS_dog").value;

		if(AS_dog.indexOf("没有查找") < 0){

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
	var url="${pageContext.request.contextPath}/user.do?method=ReadDog&random="+Math.random()+"&opt=2&usr="+usr+"&dog="+dog;
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



/*项目简历*/
function goResume(){
	if(document.getElementById("chooseValue_user").value=="")
	{
		alert("请选择要用户！");
	}
	else
	{
//		alert(UsrName);
		window.location="${pageContext.request.contextPath}/user.do?method=Item&id="+document.getElementById("chooseValue_user").value;
	}
}

/*菜单权限*/
function goEditPopedom(){
	if(document.getElementById("chooseValue_user").value=="")
	{
		alert("请选择要修改权限的操作员！");
		return false;
	}
	window.location="${pageContext.request.contextPath}/user.do?method=UserPopedom&id="+document.getElementById("chooseValue_user").value;
}

/*批量导入*/
function goJoin(){
	window.location="${pageContext.request.contextPath}/user.do?method=Upload";
}

/*批量打印*/
function goPrintln(){
	window.location="${pageContext.request.contextPath}/user.do?method=xlsPrint";

}
function goClean_CH_user() {
	window.location="${pageContext.request.contextPath}/user.do?method=List";
}

function goSearch1() {
	var flag = false;
	var strW = "";
	if(document.getElementById("loginid").value != "") {

		flag = true;
	}
	if(document.getElementById("name").value != "") {

		flag = true;
	}
	if(document.getElementById("department").value != "") {

		flag = true;
	}
	if(flag) {
	//	goSearch_user();
		document.thisForm.action="${pageContext.request.contextPath}/user.do?method=List"
		document.thisForm.submit();
	} else {
		alert("至少选一项查询条件！");
	}
}

function goSearch2() {
	var flag = false;
	var strW = "";
	if(document.getElementById("loginid2").value != "") {

		flag = true;
	}
	if(document.getElementById("name2").value != "") {

		flag = true;
	}
	if(document.getElementById("department2").value != "") {

		flag = true;
	}
	if(flag) {
//		goSearch_user2();
		document.thisForm.action="${pageContext.request.contextPath}/user.do?method=List"
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
	//window.open("${pageContext.request.contextPath}/Voucher/View.jsp?chooseValue="+obj.voucherid+"&VoucherID="+obj.voucherid+"&AccPackageID="+obj.accpackageid,"_blank","height=480,width=840,resizable=yes, toolbar=no,menubar=no,titlebar=no,scrollbars=yes");
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

/*
try{
	

	if(examineOrnot=="是"){
		document.getElementById("examineOrnot").style.display = "none";
	}else{
		document.getElementById("examineOrnot").style.display = "";
	}
}catch(e){

}
*/
function goMutiDelete() {

	window.location="${pageContext.request.contextPath}/user.do?method=mutiDeleteList";
}
function goMutiDelete1(v) {
	
	window.location="${pageContext.request.contextPath}/user.do?method=mutiDeleteList1&flag="+v;
}

var setOrderByWin = null;
function setOrderBy() {

	var url="${pageContext.request.contextPath}/user.do?method=setOrderBy&flag=window&rand=" + Math.random();
		
	if(!setOrderByWin) { 
		setOrderByWin = new Ext.Window({
	     	renderTo : Ext.getBody(),
	     	width: 500,
	     	id:'setOrderByWin',
	     	height:350,
	     	title:'设置用户排序',
	     	closable:'flase',
        	closeAction:'hide',
       	    listeners : {
	         	'hide':{
	         		fn: function () {
	         			new BlockDiv().hidden();
						setOrderByWin.hide();
						
					}
				}
	        },
	       	html:'<iframe name="orderByFrame" id="orderByFrame" scrolling="no" frameborder="0" width="100%" height="100%" src="' + url + '"></iframe>',
        	layout:'fit'
	    });
    } else {
    	document.getElementById("orderByFrame").src = url;
    }
	
	new BlockDiv().show();
	setOrderByWin.show(); 
}

var jarrEmTable=[];
var customQryWin;
//�Զ����ѯ
function QryEmWinFun() {
	var tableId="em";
	//var customQryWin = this["customQryWin"] ;
	document.getElementById("customQry_"+tableId).style.display = "";
	
	if(customQryWin == null) { 
		customQryWin = new Ext.Window({
			title: '人员信息高级查询',
			width: 800,
			height:300,
			contentEl:'customQry_'+tableId, 
	        closeAction:'hide',
	        autoScroll:true,
	        modal:true,
	        listeners:{
				'hide':{fn: function () {
					 document.getElementById("customQry_"+tableId).style.display = "none";
				}}
			},
	        layout:'fit',
		    buttons:[{
	            text:'查询',
	          	handler:function() {
	          		var qryWhere = createEmQryWhere(tableId);
	          		var qryJoin = createEmQryJoin(tableId);
	          		if(qryWhere == false) return ;
	          		document.getElementById("qryWhere_"+tableId).value = qryWhere ;
	          		document.getElementById("qryJoin_"+tableId).value = qryJoin ;
	          		//eval("goSearch_"+tableId+"();");
	          		goSearch_user();
	          		customQryWin.hide();
	          	}
	        },{
	            text:'取消',
	            handler:function(){
	            	customQryWin.hide();
	            }
	        }]
	    }); 
		this["customQryWin_"+tableId] = customQryWin ;
		addEmQuery(tableId,true);
	}
	customQryWin.show();
}

function addEmQuery(tableId,first) {
	var trObj ;
	var tdObj ;
	var r=parseInt(Math.random()*10000+1); 　
	//var grid = Ext.getCmp("gridId_"+tableId);
	//var columns = grid.getColumnModel().columns;
	var columns = [{id:'111',header:"333",freequery:"4545"}];
	var tbody = document.getElementById("queryTBody_"+tableId);
	trObj = tbody.insertRow();
	trObj.id = "queryTr_" + tableId;
	
	//����
	tdObj = trObj.insertCell();
	tdObj.align = "center";
	
	var display = "" ;
	if(first) {
		display = "display:none;" ;
	}
	tdObj.innerHTML = "<div class=selectDiv style=\"width:80px;"+display+"\">"
					+ "<select class=mySelect style=\"width:80px;\" name='query_logic_"+tableId+"' id='query_logic_" + tableId + "'>"
					+ "		<option value='and'>与(and)</option>"
					+ "		<option value='or'>或(or)</option>"
					+ "</select>"
					+ "</div>" ;
	
	//����
	tdObj = trObj.insertCell();
	tdObj.align = "center";
	
	var tableHtml = "" //"<div class=selectDiv style=\"width:200px;\">"
		//+ "	<select class=mySelect style=\"width:200px;\" name='query_table_" + tableId + "' id='query_table_" + tableId + "'>" ;
     +"<input autoid='5011' name='query_table_" + tableId + "' noinput='true' id='query_table_" + tableId +r+ "'>"
		//for(var i=0;i<jarrEmTable.length;i++){
    //	tableHtml+="<option value='" + jarrEmTable[i]["table_name"] + "'> " +jarrEmTable[i]["table_name_cn"]  + " </option>" ;
    	
    //}

	//tableHtml += " </select>";
   //tableHtml += " </div>";
    tdObj.innerHTML = tableHtml ;

//�����
tdObj = trObj.insertCell();
tdObj.align = "center";
	
	var columnHtml = "" //"<div class=selectDiv style=\"width:120px;\">"
					//+ "	<select class=mySelect style=\"width:120px;\" name='query_column_" + tableId + "' id='query_column_" + tableId + "'>" ;
	    +"<input autoid='5012|query_table_" + tableId +r+ "' noinput='true'  refer='query_table_" + tableId +r+ "' name='query_column_" + tableId + "' id='query_column_" + tableId +r+ "'>"
	/*				
	for(var i=0;i<columns.length;i++) {
		var id = columns[i].freequery || columns[i].id ;
		var header = columns[i].header ;
		var hidden = columns[i].hidden ;
		 
		if(header != "ѡ" && id != "numberer" && header != "trValue" && id != "chooseValue") {
			columnHtml += "<option value='" + id + "'> " + header + " </option>" ;
		}  
	}
    */
	//columnHtml += " </select>";
	//columnHtml += " </div>";
	tdObj.innerHTML = columnHtml ;
	
	//�����
	tdObj = trObj.insertCell();
	tdObj.align = "center";
	
	tdObj.innerHTML = "<div class=selectDiv style=\"width:80px;\">"
	+ "	<select class=mySelect style=\"width:80px;\" name='query_operator_" + tableId + "' id='query_operator_" + tableId + "'>"
	+ "		<option value='='> 等于(=) </option> "
	+ "		<option value='!='> 不等于(!=) </option> "
	+ "		<option value='>'> 大于(&gt;) </option> "
	+ "		<option value='<'> 小于(&lt;) </option> "
	+ "		<option value='>='> 大于等于(&gt;=) </option> "
	+ "		<option value='<='> 小于等于(&lt;=) </option> "
	+ "		<option value='like'> 包含</option> "
	+ "		<option value='not like'> 不包含 </option> "
	+ " </select>";
	+ " </div>";
	
	//����
	tdObj = trObj.insertCell();
	tdObj.align = "center";

	tdObj.innerHTML = "<input type=text id='query_condition_" + tableId + "' name='query_condition_" + tableId + "'  size='30'>";
	
	
	//����
	tdObj = trObj.insertCell();
	tdObj.align = "center";
	if(!first) {
		tdObj.innerHTML = "<a href='javascript:;' onclick='removeEmQuery(this);' ><img src=" + MATECH_SYSTEM_WEB_ROOT + "img/delete.gif></a>" ;
	}
	initCombox("query_table_" + tableId +r);
	initCombox("query_column_" + tableId +r);

}


function removeEmQuery(obj) {
	var tbody = obj.parentElement.parentElement.parentElement ;
	var trObj = obj.parentElement.parentElement ;
	if(trObj) {
		tbody.removeChild(trObj);
	}
}


function createEmQryJoin(tableId) {
	
	var query_table = document.getElementsByName("query_table_"+tableId) ;
	
	
	var qryJoin = "" ;
	var mapTable={};
	for(var i=0;i<query_table.length;i++) {
		var table = query_table[i].value ;
		if(!mapTable[table]){
			qryJoin+=" left join "+table+" st_"+table+" on st_"+table+".userid =a.id ";
			mapTable[table]=table;
		}
	}
	
	return qryJoin ; 
}


function createEmQryWhere(tableId) {
	var query_table = document.getElementsByName("query_table_"+tableId) ;
	var query_logic = document.getElementsByName("query_logic_"+tableId) ;
	var query_column = document.getElementsByName("query_column_"+tableId) ;
	var query_operator = document.getElementsByName("query_operator_"+tableId) ;
	var query_condition = document.getElementsByName("query_condition_"+tableId) ;
	
	var qryWhere = "" ;
	for(var i=0;i<query_logic.length;i++) {
		var logic = query_logic[i].value ;
		var column = query_column[i].value ;
		var operator = query_operator[i].value ;
		var condition = query_condition[i].value ;
		var table = query_table[i].value ;
		if(column == "") {
			alert("所有条件不能为空!") ;
			return false ;
		}
		
		if(operator.indexOf("like") > -1) {
			if(condition != "") {
				condition = "'%" + condition + "%'" ;
			}
		}else{ //if(isNaN(condition) || condition == "") {
			condition = "'" + condition + "'" ;
		}
		
		qryWhere += " " + logic + " st_"+table+"." + column + " " + operator + " " + condition ;
	}
	
	return qryWhere ; 
}

</script>

<script Language=JavaScript>
ext_init();
goSearch_user();
</script>
