<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"  errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>人员管理</title>
</head>

<script type="text/javascript">

function ext_init(){
	
	var mytab = new Ext.TabPanel({
        id: "tab",
        renderTo: "divTab",
        activeTab: ${tabOpt}, //选中第一个 tab
        autoScroll:true,
        frame: true,
        height: document.body.clientHeight-Ext.get('divTab').getTop(),
        defaults: {autoHeight: true,autoWidth:true},
        items:[
				<c:choose>
				<c:when test="${judge == ''}">
				     {contentEl: "tabUser1", title: "人员列表",id:"myUser1"},
		             {contentEl: "tabUser2", title: "禁用人员列表",id:"myUser2"}
				</c:when>
				<c:otherwise>
				   {contentEl: "tabUser1", title: "人员列表",id:"myUser1"} 
				</c:otherwise>
				</c:choose> 
        ]
    });
    
    var isRefresh = false ;
    mytab.on("tabchange",function(tabpanel,tab) {
		if(!isRefresh) {
			goSearch_user2();
		}
		isRefresh = true ;    
    }) ;
    
	var tbar_user = new Ext.Toolbar({
		renderTo: 'gridDiv_user',
		items:[
<c:if test="${judge == ''}">		       

		{
			text:'新增',
			cls:'x-btn-text-icon',
			icon:'${pageContext.request.contextPath}/img/add.gif',
			handler:function(){
				goAdd();
			}
		},				
		{
			text:'修改',
			cls:'x-btn-text-icon',
			icon:'${pageContext.request.contextPath}/img/edit.gif',
			handler:function(){
				goEdit();
			}
		},'-',{
			text:'禁用',
			cls:'x-btn-text-icon',
			hidden:true,
			icon:'${pageContext.request.contextPath}/img/delete.gif',
			handler:function () {
				goDelete(0);
			}
		},{
			text:'批量删除',
			cls:'x-btn-text-icon',
			icon:'${pageContext.request.contextPath}/img/delete.gif',
			handler:function () {
				goMutiDelete1(1);
			}
		},'-',{
			text:'菜单权限',
			cls:'x-btn-text-icon',
			icon:'${pageContext.request.contextPath}/img/department.gif',
			handler:function () {
				goEditPopedom();
			}
		},
		</c:if>
	/*	
		{
			text:'批量禁用',
			cls:'x-btn-text-icon',
			icon:'${pageContext.request.contextPath}/img/delete.gif',
			handler:function () {
				goMutiDelete();
			}
		},
	*/	
		'-',{
			text:'查询',
			id:'btn-query',
			cls:'x-btn-text-icon',
	   		icon:'${pageContext.request.contextPath}/img/query.gif',
			handler:queryWinFun
		},
<c:if test="${userSession.userLoginId == 'admin'}">
		<c:if test="${judge == ''}">
		'-',{
			text:'批量导入',
			cls:'x-btn-text-icon',
			icon:'${pageContext.request.contextPath}/img/import.gif',
			handler:function () {
				goJoin();
			}
		},
		</c:if>
		'-',{
			text:'打印',
			cls:'x-btn-text-icon',
			icon:'${pageContext.request.contextPath}/img/print.gif',
			handler:function () {
				print_user();
				//myOpenUrlByWindowOpen('user.do?method=Print&tableid=user','','');
			}
		},
	/*	
		{
			text:'批量打印',
			cls:'x-btn-text-icon',
			icon:'${pageContext.request.contextPath}/img/print.gif',
			handler:function () {
				goPrintln();
			}
		},
	*/	<c:if test="${judge == ''}">
		'-',{
			text:'菜单权限',
			cls:'x-btn-text-icon',
			icon:'${pageContext.request.contextPath}/img/department.gif',
			handler:function () {
				goEditPopedom();
			}
		},{
			text:'客户/项目权限',
			cls:'x-btn-text-icon',
			icon:'${pageContext.request.contextPath}/img/department.gif',
			handler:function () {
				goEPopedom();
			}
		},'-',{
            text:'设置用户排序',
            cls:'x-btn-text-icon',
            icon:'${pageContext.request.contextPath}/img/flow.gif',
            handler:function () {
            	setOrderBy();
            }
        },
        </c:if>
        </c:if>	
        <c:if test="${judge != ''}">
        '-',{
			text:'查看详细信息',
			cls:'x-btn-text-icon',
			icon:'${pageContext.request.contextPath}/img/query.gif',
			handler:function () {
				if(document.getElementById("chooseValue_user").value==""){
					alert("请选择要查看的对象！");
				}else{
					window.location = "${pageContext.request.contextPath}/oa/UserInformationFrame.jsp?myUserid="+document.getElementById("chooseValue_user").value+"&judge=${judge}";
				}
			}
		},
		</c:if>
	/*	
		{
			text:'项目简历',
			cls:'x-btn-text-icon',
			icon:'${pageContext.request.contextPath}/img/import.gif',
			handler:function () {
				goResume();
			}
		},
	*/	

		'->',{
			text:'刷新',
			cls:'x-btn-text-icon',
			icon:'${pageContext.request.contextPath}/img/refresh.gif',
			handler:function () {
				window.location.reload();
			}
			
		}]
	});
	
	var tbar_user2 = new Ext.Toolbar({
		renderTo: 'gridDiv_user2',
		items:[{
			text:'还原',
			cls:'x-btn-text-icon',
			icon:'${pageContext.request.contextPath}/img/add.gif',
			handler:function(){
				goRevert(1);
			}
		},'-',{
			text:'永远删除',
			cls:'x-btn-text-icon',
			icon:'${pageContext.request.contextPath}/img/delete.gif',
			handler:function () {
				goDelete2(1);
			}
		},'-',{
			text:'批量删除',
			cls:'x-btn-text-icon',
			icon:'${pageContext.request.contextPath}/img/delete.gif',
			handler:function () {
				goMutiDelete1(0);
			}
		},
<c:if test="${userSession.userLoginId == 'admin'}">		
		'-',{
			text:'打印',
			cls:'x-btn-text-icon',
			icon:'${pageContext.request.contextPath}/img/print.gif',
			handler:function () {
				print_user2();
			}
		},
</c:if>		
		'-',{
			text:'查询',
			id:'btn-query',
			cls:'x-btn-text-icon',
	   		icon:'${pageContext.request.contextPath}/img/query.gif',
			handler:queryWinFun1
		},'->',{
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
		     	width: 300,
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

<div id="searchWin" style="position:absolute;left:expression((document.body.clientWidth-250)/2);top:expression(this.offsetParent.scrollTop +200); z-index: 2"></div>

<div id="panel"></div>
<div id="divTab" style="overflow:auto">

<div id="tabUser1" <c:if test='${tabOpt==1}'>class="x-hide-display"</c:if>  style="height:expression(document.body.clientHeight-62);width:100%">
<mt:DataGridPrintByBean name="user"  message="请选择单位编号" />
</div>

<div id="tabUser2" <c:if test='${tabOpt==0}'>class="x-hide-display"</c:if>  style="height:expression(document.body.clientHeight-62);width:100%">
<div id="examineOrnot" style="height: 100%;width: 100%">
<input type="hidden" name="UsrID" value="">
<input  name="AS_dog" type="hidden" id="AS_dog" value="">
<mt:DataGridPrintByBean name="user2" message="请选择单位编号"/>
</div>
</div>

</form>

<div id="search" style="display:none">
<br/>
<div style="margin:0 20 0 20">请在下面输入查询条件：</div>
<div style="border-bottom:1px solid #AAAAAA; height: 1px;margin:0 20 4 20" ></div>
	<table border="0" cellpadding="0" cellspacing="0" width="100%" bgcolor="">
		<tr align="center">
			
			<td align="right">登录名：</td>
			<td align=left>
				<input type="text" name="loginid" id="loginid"> 
			</td>
		</tr>
		<tr align="center">
			
			<td align="right">姓名：</td>
			<td align=left>
				<input type="text" name="name" id="name" >
			</td>
		</tr>
		<tr align="center">
			
			<td align="right">所属部门：</td>
			<td align=left>
				<input name="department" id="department" type="text" onKeyDown="onKeyDownEvent();" onKeyUp="onKeyUpEvent();"  onClick="onPopDivClick(this);" valuemustexist=true autoid=4402>
			</td>
		</tr>
	</table>
</div>

<div id="search1" style="display:none">
<br/>
<div style="margin:0 20 0 20">请在下面输入查询条件：</div>
<div style="border-bottom:1px solid #AAAAAA; height: 1px;margin:0 20 4 20" ></div>
	<table border="0" cellpadding="0" cellspacing="0" width="100%" bgcolor="">
		<tr align="center">
			
			<td align="right">登录名：</td>
			<td align=left>
				<input type="text" name="loginid2" id="loginid2">
			</td>
		</tr>
		<tr align="center">
			
			<td align="right">姓名：</td>
			<td align=left>
				<input type="text" name="name2" id="name2" >
			</td>
		</tr>
		
		<tr align="center">
			
			<td align="right">所属部门：</td>
			<td align=left>
				<input name="department2" id="department2" type="text" onKeyDown="onKeyDownEvent();" onKeyUp="onKeyUpEvent();"  onClick="onPopDivClick(this);" valuemustexist=true autoid=4402>
			</td>
		</tr>
	</table>
</div>


<div id="search2" style="display:none">
<br/>
<div style="margin:0 20 0 20">人员还原</div>
<div style="border-bottom:1px solid #AAAAAA; height: 1px;margin:0 20 4 20" ></div>
	<table border="0" cellpadding="0" cellspacing="0" width="100%" bgcolor="">
	<tr align="center"><td>
		
		
	<table width="330" height="100%" border="0"  cellspacing="0" cellpadding="0">
		<tr>
			<td colspan="2">
				<div id="revertError" style="display: none">
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
						 autoid=123 />
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
	</table>

	</td></tr></table>
	
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

/*项目权限*/
function goEPopedom(){
	if(document.getElementById("chooseValue_user").value=="")
	{
		alert("请选择要修改权限的操作员！");
		return false;
	}
	window.location="${pageContext.request.contextPath}/user.do?method=ProjectPopedom&menuid=${param.menuid}&id="+document.getElementById("chooseValue_user").value;
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


try{
	var examineOrnot = "${svalue}";

	if(examineOrnot=="是"){
		document.getElementById("examineOrnot").style.display = "none";
	}else{
		document.getElementById("examineOrnot").style.display = "";
	}
}catch(e){

}

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
</script>

<script Language=JavaScript>ext_init();</script>
